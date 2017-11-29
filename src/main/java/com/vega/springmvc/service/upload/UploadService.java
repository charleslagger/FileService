package com.vega.springmvc.service.upload;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.vega.springmvc.config.FileStatus;
import com.vega.springmvc.config.PartnerStatus;
import com.vega.springmvc.model.FileBucket;
import com.vega.springmvc.model.FintechFile;
import com.vega.springmvc.model.LoanDocument;
import com.vega.springmvc.model.MultiFileBucket;
import com.vega.springmvc.model.Partner;
import com.vega.springmvc.model.UrlParam;
import com.vega.springmvc.service.infor.FintechFileService;
import com.vega.springmvc.service.infor.PartnerService;
import com.vega.springmvc.service.notification.RestService;
import com.vega.springmvc.util.JsonUtil;

@Service
public class UploadService {

	private Logger logger = Logger.getLogger(UploadService.class);
	private Base64 base64 = new Base64();
	private final String HOST;
	private String dirFile;

	@Autowired
	private PartnerService partnerService;

	@Autowired
	private FintechFileService fintechFileService;

	@Autowired
	private RestService restService;

	@Autowired
	private JsonUtil jsonUtil;

	public UploadService(Environment env) {
		HOST = env.getProperty("host");
	}

	public void getInfo(UrlParam urlParam, String urlReceive) {
		urlParam.setfName(getField(urlReceive, 6));
		urlParam.setPartnerId(getField(urlReceive, 1));
		urlParam.setOrderId(Long.parseLong(getField(urlReceive, 3)));
		urlParam.setmKey(getField(urlReceive, 4));
		urlParam.setAckUrl(getField(urlReceive, 5));
		urlParam.setOpt(Long.parseLong(getField(urlReceive, 2)));
		urlParam.setAmountFile(0);
	}

	public String getField(String urlReceive, int index) {
		String[] docTypes = urlReceive.split("=");
		docTypes = docTypes[docTypes.length - index].split("&");

		return docTypes[0];
	}

	// return attribute fname
	public String[] getFileName(UrlParam urlParam, String fileName) {

		String[] splitName = fileName.split(";");
		urlParam.setAmountFile(splitName.length);
		for (int i = 0; i < splitName.length; i++) {
			splitName[i] = splitName[i].substring(0, splitName[i].length() - 2);
		}

		return splitName;
	}

	// return attribute to determine field is mandatory or optional
	public List<String> getTailsList(UrlParam urlParam, String fileName) {

		String[] splitName = fileName.split(";");
		List<String> tails = new ArrayList<String>();
		// new String[splitName.length];

		urlParam.setAmountFile(splitName.length);
		for (int i = 0; i < splitName.length; i++) {
			tails.add(splitName[i].substring(splitName[i].lastIndexOf("_") + 1));
		}
		
		return tails;
	}

	public ModelAndView uploadAllFile(MultiFileBucket multiFileBucket, ModelAndView modelSuccess,
			ModelAndView modelErrors, UrlParam urlParam) throws IOException {
		ArrayList<String> filesUploadName = filesUploadName(multiFileBucket, urlParam);
		if (filesUploadName.size() != urlParam.getAmountFile()) {
			getInvalidView(urlParam, modelErrors);
			return modelErrors;
		} else
			modelSuccess.addObject("fileNames", filesUploadName);
		
		return modelSuccess;
	}

	public ModelAndView uploadMadatoryFile(MultiFileBucket multiFileBucket, ModelAndView modelSuccess,
			ModelAndView modelErrors, UrlParam urlParam) throws IOException {
		List<String> doctype = getTailsList(urlParam, urlParam.getfName());
		/*
		 * i: index, count: count the optional file name equals 0
		 */
		int i = 0, countOptionalFile = 0;
		for (FileBucket file : multiFileBucket.getFiles()) {
			if (file.getFile() != null) {
				if (file.getFile().getSize() == 0) {
					switch (doctype.get(i)) {
					case FileStatus.OPTIONAL:
						countOptionalFile++;
						break;
					case FileStatus.MANDATORY:
						getInvalidView(urlParam, modelErrors);
						return modelErrors;
					default:
						break;
					}
				}
			}

			if (isValid(file)) {
				logger.info("Prevent files.");
				getInvalidView(urlParam, modelErrors);
				return modelErrors;
			}
			i++;
		}
		if (countOptionalFile == urlParam.getAmountFile()) {
			getInvalidView(urlParam, modelErrors);
			return modelErrors;
		} else {
			modelSuccess.addObject("fileNames", filesUploadName(multiFileBucket, urlParam));
			
			return modelSuccess;
		}
	}

	public ArrayList<String> filesUploadName(MultiFileBucket multiFileBucket, UrlParam urlParam) throws IOException {

		ArrayList<String> filesUploadName = new ArrayList<String>(); // List ten file duoc upload len
		ArrayList<String> docTypeName = new ArrayList<String>(); // List ten cac doctype duoc upload len
		fetchingFiles(multiFileBucket, filesUploadName, docTypeName, urlParam);
		storeFileToDBAndSendInforToPartner(multiFileBucket, filesUploadName, docTypeName, urlParam);

		return filesUploadName;
	}

	public boolean isValid(FileBucket file) {

		String fileFormat[] = file.getFile().getOriginalFilename().split("\\.");
		// Prevent file invalid
		return fileFormat[fileFormat.length - 1].equals("exe") || fileFormat[fileFormat.length - 1].equals("php")
				|| fileFormat[fileFormat.length - 1].equals("sh") || fileFormat[fileFormat.length - 1].equals("com");
	}

	public void getInvalidView(UrlParam urlParam, ModelAndView modelErrors) {

		modelErrors.addObject("doctypeField", getFileName(urlParam, urlParam.getfName()));
		modelErrors.addObject("requiredField", getTailsList(urlParam, urlParam.getfName()));
	}

	public void fetchingFiles(MultiFileBucket multiFileBucket, ArrayList<String> filesUploadName,
			ArrayList<String> docTypeName, UrlParam urlParam) throws IOException {
		int index = 0;
		for (FileBucket file : multiFileBucket.getFiles()) {
			if (file.getFile() != null) {
				if (file.getFile().getSize() == 0) {
					index++;
					continue;
				}
			}
			// Split file name and format of filename
			String fileFormat[] = file.getFile().getOriginalFilename().split("\\.");

			if (fileFormat[fileFormat.length - 1].equals("exe") || fileFormat[fileFormat.length - 1].equals("php")
					|| fileFormat[fileFormat.length - 1].equals("sh")
					|| fileFormat[fileFormat.length - 1].equals("com")) {
				logger.info("Prevent Files.");
				break;
			}

			byte[] bytes = file.getFile().getBytes();
			// Creating the directory to store file
			String rootPath = System.getProperty("catalina.home");
			File dir = new File(rootPath + File.separator + "file-store");
			if (!dir.exists()) {
				dir.mkdirs();
			}

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
			filesUploadName.add("" + "file_" + currentTimeUpload + "." + fileFormat[fileFormat.length - 1]);
			docTypeName.add(getFileName(urlParam, urlParam.getfName())[index]);
			index++;
		}
	}

	public void storeFileToDBAndSendInforToPartner(MultiFileBucket multiFileBucket, ArrayList<String> filesUploadName,
			ArrayList<String> docTypeName, UrlParam urlParam) throws UnknownHostException {

		List<LoanDocument> loanDocumentRequests = new LinkedList<LoanDocument>();

		String contractDocumentId = new String();
		String disbursementDocumentId = new String();

		int index = 0;
		for (FileBucket file : multiFileBucket.getFiles()) {
			if (index == filesUploadName.size()) {
				break;
			}

			storeFile(file, filesUploadName, docTypeName, index, urlParam);

			contractDocumentId = filesUploadName.get(index);
			disbursementDocumentId = filesUploadName.get(index);
			logger.info("--->ContractId and DisbursementId: " + filesUploadName.get(index));

			if (urlParam.getOpt() == null || urlParam.getOpt() == 0) {
				addLoanDocuments(filesUploadName, docTypeName, loanDocumentRequests, index, urlParam);
			}
			index++;
		}

		storePartner(urlParam);

		sendNotiToCore(loanDocumentRequests, contractDocumentId, disbursementDocumentId, urlParam);
	}

	private void addLoanDocuments(ArrayList<String> filesUploadName, ArrayList<String> docTypeName,
			List<LoanDocument> loanDocumentRequests, int index, UrlParam urlParam) {
		LoanDocument loanDocument = new LoanDocument();
		loanDocument.setOrderId(urlParam.getOrderId());
		loanDocument.setDocumentTypeName(docTypeName.get(index));
		loanDocument.setOriginalFileId(filesUploadName.get(index));

		loanDocumentRequests.add(loanDocument);
	}

	private void storeFile(FileBucket file, ArrayList<String> filesUploadName, ArrayList<String> docTypeName, int index,
			UrlParam urlParam) {
		// Split file name and format of filename
		String fileFormat[] = file.getFile().getOriginalFilename().split("\\.");

		FintechFile fintechFile = new FintechFile();
		String[] dateCreate = filesUploadName.get(index).split("[_.]");
		fintechFile
				.setPath(dirFile + File.separator + "file_" + dateCreate[1] + "." + fileFormat[fileFormat.length - 1]);

		fintechFile.setFileName(filesUploadName.get(index));

		fintechFile.setDateCreated(dateCreate[1]);
		// TODO: Content field in Mongo
		fintechFile.setContent(docTypeName.get(index));
		fintechFile.setOrderId(urlParam.getOrderId());
		fintechFile.setPartnerId(urlParam.getPartnerId());

		fintechFileService.addFile(fintechFile);
	}

	private void sendNotiToCore(List<LoanDocument> loanDocumentRequests, String contractDocumentId,
			String disbursementDocumentId, UrlParam urlParam) {
		if (urlParam.getOpt() == 0 || urlParam.getOpt() == null) {
			logger.info("Amount doctype uploaded: " + loanDocumentRequests.size());
			restService.sendPost(urlParam.getAckUrl(), loanDocumentRequests);
		} else if (urlParam.getOpt() == 1) {
			restService.sendGet(urlParam.getAckUrl(), "orderId", Long.valueOf(urlParam.getOrderId()),
					"contractDocumentId", contractDocumentId);
			logger.info("Contract uploaded");
		} else if (urlParam.getOrderId() == 2) {
			restService.sendGet(urlParam.getAckUrl(), "orderId", urlParam.getOrderId(), "disbursementDocumentId",
					disbursementDocumentId);
			logger.info("Disbursement uploaded");
		}
	}

	private void storePartner(UrlParam urlParam) {

		Partner partner = new Partner();

		String[] partnerIP = HOST.split(":");
		String partnerName = new String(base64.encode(urlParam.getPartnerId().getBytes()));

		partner.setName(partnerName);
		partner.setIp(partnerIP[1].substring(2));
		partner.setAuthenKey(urlParam.getmKey());
		partner.setStatus(PartnerStatus.ACTIVE);

		partnerService.addPartner(partner);
		logger.info("Partner save to DB: " + jsonUtil.objectToJSon(partner));
	}

}
