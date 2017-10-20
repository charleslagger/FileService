package com.vega.springmvc.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.vega.springmvc.model.FileBucket;
import com.vega.springmvc.model.LoanDocument;
import com.vega.springmvc.model.MultiFileBucket;
import com.vega.springmvc.service.transform.CoreResponse;
import com.vega.springmvc.util.MultiFileValidator;

@Controller
public class MainController {
	protected RestTemplate restTemplate;
	protected ObjectMapper mapper;
	protected HttpClient httpClient;
	protected HttpHeaders headers;

	/*
	 * Make response loan document infor to partner
	 */
	// protected String host = "http://localhost:8080";
	protected String host = "http://192.168.43.206:8080";
	protected String contextPath = "";
	protected String urlResponse;
	private ParameterizedTypeReference<CoreResponse> parameterizedTypeReference = new ParameterizedTypeReference<CoreResponse>() {
	};

	// private String urlReceived;
	// count amount file must upload
	private int amountFile = 0;
	// use this variables to sync fname in method GET to method POST
	private String mFileName;
	private String mPartnerId;
	private Long mOrderId;
	private String mAckUrl;
	private String mKey;

	// dir store file
	private String dirFile;

	@Autowired
	MultiFileValidator multiFileValidator;

	@InitBinder("multiFileBucket")
	protected void initBinderMultiFileBucket(WebDataBinder data) {
		data.setValidator(multiFileValidator);
	}

	@GetMapping(value = "/{urlReceive}")
	public ModelAndView getData(@PathVariable(value = "urlReceive") String urlReceive) {
		// reference to file uploadFile.jsp
		Base64 base64 = new Base64();
		urlReceive = new String(base64.decode(urlReceive.getBytes()));
		System.out.println("Url received is: " + urlReceive);
		//TODO: 
		String fNameField = getDocTypeName(urlReceive);
		String partnerId = getPartnerId(urlReceive);
		Long orderId = getOrderId(urlReceive);
		String key = getKey(urlReceive);
		String ackUrl = getAckUrl(urlReceive);

		ModelAndView model = new ModelAndView("uploadFile");
		model.addObject("titleFile", getFileName(fNameField));
		model.addObject("manFile", getDocTypeList(fNameField));
		mFileName = "" + fNameField;
		mPartnerId = "" + partnerId;
		mOrderId = orderId;
		mAckUrl = ackUrl;
		System.out.println("----> Ack Url: " + mAckUrl);
		mKey = key;
		System.out.println("----> mKey: " + mKey);

		MultiFileBucket filesModel = new MultiFileBucket(amountFile);
		model.addObject("multiFileBucket", filesModel);
		return model;
	}

	private String getAckUrl(String urlReceive) {
		String[] docTypes = urlReceive.split("=");
		docTypes = docTypes[docTypes.length - 4].split("&");
		return docTypes[0];
	}

	private String getKey(String urlReceive) {
		String[] docTypes = urlReceive.split("=");
		docTypes = docTypes[docTypes.length - 3].split("&");
		return docTypes[0];
	}

	private Long getOrderId(String urlReceive) {
		String[] docTypes = urlReceive.split("=");
		docTypes = docTypes[docTypes.length - 2].split("&");
		return Long.parseLong(docTypes[0]);
	}

	private String getPartnerId(String urlReceive) {
		String[] docTypes = urlReceive.split("=");
		return docTypes[docTypes.length - 1];
	}

	private String getDocTypeName(String urlReceive) {
		String[] docTypes = urlReceive.split("=");
		docTypes = docTypes[1].split("&");
		return docTypes[0];
	}

	// Todo: Post infor to database
	@PostMapping(value = "/upload")
	public ModelAndView multiFileUpload(@Valid MultiFileBucket multiFileBucket, BindingResult result)
			throws IOException {
		ModelAndView modelErrors = new ModelAndView("uploadFile");
		ModelAndView modelSuccess = new ModelAndView("uploadSuccess");

		if (result.hasErrors()) {
			System.out.println("validation errors in multi upload");
			String doctype[] = getDocTypeList(mFileName);

			/*
			 * i: index, count: count the mandatory file name equals 0
			 */
			int i = 0, countVar = 0;
			for (FileBucket file : multiFileBucket.getFiles()) {
				if (file.getFile() != null) {
					if (file.getFile().getSize() == 0) {
						if (doctype[i].equals("0")) {
							countVar++;
						} else if (doctype[i].equals("1")) {
							getInvalidView(modelErrors);
							return modelErrors;
						}
					}
				}

				String fileFormat[] = file.getFile().getOriginalFilename().split("\\.");

				boolean checkValidFile = fileFormat[fileFormat.length - 1].equals("exe")
						|| fileFormat[fileFormat.length - 1].equals("php")
						|| fileFormat[fileFormat.length - 1].equals("sh")
						|| fileFormat[fileFormat.length - 1].equals("com");

				if (checkValidFile) {
					System.out.println("Prevent some files mandatory.");
					getInvalidView(modelErrors);
					return modelErrors;
				}
				i++;
			}
			if (countVar == amountFile) {
				getInvalidView(modelErrors);
				return modelErrors;
			} else {
				System.out.println("Fetching some files");

				ArrayList<String> fileNames = new ArrayList<String>();
				fetchingFiles(multiFileBucket, fileNames);
				storeFileToDBAndSendInforToPartner(multiFileBucket, fileNames);
				modelSuccess.addObject("fileNames", fileNames);
				return modelSuccess;
			}
		}
		System.out.println("Fetching all files");
		ArrayList<String> fileNames = new ArrayList<String>();
		fetchingFiles(multiFileBucket, fileNames);
		storeFileToDBAndSendInforToPartner(multiFileBucket, fileNames);
		if (fileNames.size() != amountFile) {
			getInvalidView(modelErrors);
			return modelErrors;
		} else
			modelSuccess.addObject("fileNames", fileNames);
		return modelSuccess;
	}

	// return attribute fname
	public String[] getFileName(String fileName) {
		String[] splitName = fileName.split(";");
		amountFile = splitName.length;
		for (int i = 0; i < splitName.length; i++) {
			splitName[i] = splitName[i].substring(0, splitName[i].length() - 2);
		}
		return splitName;
	}

	// return atribute mandatory
	public String[] getDocTypeList(String fileName) {
		String[] splitName = fileName.split(";");
		String[] tails = new String[splitName.length];

		amountFile = splitName.length;
		for (int i = 0; i < splitName.length; i++) {
			tails[i] = splitName[i].substring(splitName[i].indexOf("_") + 1);
		}
		return tails;
	}

	public void getInvalidView(ModelAndView modelErrors) {
		modelErrors.addObject("titleFile", getFileName(mFileName));
		modelErrors.addObject("manFile", getDocTypeList(mFileName));
	}

	/*
	 * Fetching file valid and save to server
	 */
	public void fetchingFiles(MultiFileBucket multiFileBucket, ArrayList<String> fileNames) throws IOException {
		for (FileBucket file : multiFileBucket.getFiles()) {
			if (file.getFile() != null) {
				if (file.getFile().getSize() == 0) {
					continue;
				}
			}
			// Split file name and format of filename
			String fileFormat[] = file.getFile().getOriginalFilename().split("\\.");

			if (fileFormat[fileFormat.length - 1].equals("exe") || fileFormat[fileFormat.length - 1].equals("php")
					|| fileFormat[fileFormat.length - 1].equals("sh")
					|| fileFormat[fileFormat.length - 1].equals("com")) {
				System.out.println("Prevent files");
				break;
			}

			byte[] bytes = file.getFile().getBytes();
			// Creating the directory to store file
			String rootPath = System.getProperty("catalina.home");
			File dir = new File(rootPath + File.separator + "file-store");
			if (!dir.exists()) {
				dir.mkdirs();
			}

			/*
			 * Current time upload To know time GMT you can follow link:
			 * http://www.fileformat.info/tip/java/date2millis.htm
			 */
			long currentTimeUpload = System.currentTimeMillis();
			dirFile = dir.getAbsolutePath();

			// Create the file on server
			File serverFile = new File(dir.getAbsolutePath() + File.separator + "file_" + currentTimeUpload + "."
					+ fileFormat[fileFormat.length - 1]);
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
			stream.write(bytes);
			stream.close();

			/*
			 * Encrypt file name with format: name + current time +format file
			 */
			fileNames.add("" + "file_" + currentTimeUpload + "." + fileFormat[fileFormat.length - 1]);
		}
	}

	public void storeFileToDBAndSendInforToPartner(MultiFileBucket multiFileBucket, ArrayList<String> fileNames)
			throws UnknownHostException {
		MongoClient mongo = new MongoClient("localhost", 27017);
		DB db = mongo.getDB("File");
		DBCollection collectionFiles = db.getCollection("FintechFiles");

		List<LoanDocument> loanDocumentRequests = new LinkedList<LoanDocument>();

		int index = 0;
		for (FileBucket file : multiFileBucket.getFiles()) {
			LoanDocument loanDocumentRequest = new LoanDocument();
			if (index == fileNames.size()) {
				break;
			}
			// Split file name and format of filename
			String fileFormat[] = file.getFile().getOriginalFilename().split("\\.");

			BasicDBObject documentFiles = new BasicDBObject();
			documentFiles.put("FileName", fileNames.get(index));
			String[] dateCreate = fileNames.get(index).split("[_.]");
			documentFiles.put("Path",
					dirFile + File.separator + "file_" + dateCreate[1] + "." + fileFormat[fileFormat.length - 1]);
			documentFiles.put("DateCreated", "" + dateCreate[1]);
			//TODO:
			documentFiles.put("Content", getFileName(mFileName)[index]);
			documentFiles.put("OrderId", mOrderId);
			documentFiles.put("PartnerId", mPartnerId);
			collectionFiles.insert(documentFiles);

			loanDocumentRequest.setOrderId(mOrderId);
			// System.out.println("mOrderId: " + mOrderId);
			// TODO: error mFileName index
			loanDocumentRequest.setDocumentTypeName(getFileName(mFileName)[index]);
			System.out.println("getDocTypeName: " + getFileName(mFileName)[index]);
			loanDocumentRequest.setOriginalFileId(fileNames.get(index));
			// System.out.println("getOriginaFileId: " + fileNames.get(index));
			loanDocumentRequests.add(loanDocumentRequest);

			index++;
		}

		// TODO: encode mPartnerId same ObjectID
		DBCollection collectionPartners = db.getCollection("Partners");

		BasicDBObject query = new BasicDBObject();
		query.put("Name", mPartnerId);
		DBCursor cursor = collectionPartners.find(query);

		// Kiem tra neu partnerId da co trong DB thi ko lam gi, neu co thi tao moi
		if (cursor.hasNext() == false) {
			query.put("Name", mPartnerId);
			String[] partnerIP = host.split(":");
			System.out.println("-------->>>>>PartnerIP: " + partnerIP[1].substring(2));
			query.put("IP", partnerIP[1].substring(2));
			query.put("AuthKey", mKey);
			query.put("Status", mPartnerId);
			collectionPartners.insert(query);
		}
		System.out.println(loanDocumentRequests.size());
		post(loanDocumentRequests);
	}

	public CoreResponse post(List<LoanDocument> loanDocumentRequests) {
		urlResponse = host + contextPath + mAckUrl;
		headers = new HttpHeaders();

		// set up
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

		HttpClientBuilder clientBuilder = HttpClients.custom().setConnectionManager(connectionManager)
				.setRetryHandler(new DefaultHttpRequestRetryHandler(5, true));

		httpClient = clientBuilder.build();
		restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
		// post
		ResponseEntity<CoreResponse> response = restTemplate.exchange(urlResponse, HttpMethod.POST,
				new HttpEntity<List<LoanDocument>>(loanDocumentRequests, headers), parameterizedTypeReference);
		System.out.println("--->>> " + response.getBody().getMessageCode());
		return response.getBody();
	}
}
