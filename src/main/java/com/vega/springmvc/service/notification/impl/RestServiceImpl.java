package com.vega.springmvc.service.notification.impl;

import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.vega.springmvc.model.LoanDocument;
import com.vega.springmvc.service.notification.RestService;
import com.vega.springmvc.service.transform.CoreResponse;

/**
 * @author khoenv
 *
 */
@Service("restService")
@Transactional
public class RestServiceImpl implements RestService {
	Logger logger = Logger.getLogger(RestServiceImpl.class);
	private String host;
	private String context_path;
	private String urlAckResponse;
	private HttpHeaders headers;
	private RestTemplate restTemplate;
	private HttpClient httpClient;
	private ParameterizedTypeReference<CoreResponse> parameterizedTypeReference = new ParameterizedTypeReference<CoreResponse>() {
	};

	public RestServiceImpl(Environment env) {
		host = env.getProperty("host");
		context_path = env.getProperty("context_path");
	}

	public CoreResponse sendGet(String ackUrl, Object... object) {
		urlAckResponse = host + context_path + ackUrl;
		headers = new HttpHeaders();
		logger.info("\n[GET] call url: " + urlAckResponse);

		// set up
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

		HttpClientBuilder clientBuilder = HttpClients.custom().setConnectionManager(connectionManager)
				.setRetryHandler(new DefaultHttpRequestRetryHandler(5, true));

		httpClient = clientBuilder.build();
		restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
		// get
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlAckResponse);
		for (int i = 0; i < object.length; i += 2) {
			builder.queryParam(object[i].toString(), object[i + 1]);
		}

		logger.info("Builder.toUriString: " + builder.toUriString());
		ResponseEntity<CoreResponse> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
				new HttpEntity<Object>(headers), parameterizedTypeReference);

		return response.getBody();
	}

	public CoreResponse sendPost(String ackUrl, List<LoanDocument> loanDocuments) {
		urlAckResponse = host + context_path + ackUrl;
		headers = new HttpHeaders();

		// set up
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

		HttpClientBuilder clientBuilder = HttpClients.custom().setConnectionManager(connectionManager)
				.setRetryHandler(new DefaultHttpRequestRetryHandler(5, true));

		httpClient = clientBuilder.build();// Return a new httpClient
		restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
		// post
		ResponseEntity<CoreResponse> response = restTemplate.exchange(urlAckResponse, HttpMethod.POST,
				new HttpEntity<List<LoanDocument>>(loanDocuments, headers), parameterizedTypeReference);
		logger.info("Response body: " + response.getBody().getMessageCode());
		return response.getBody();
	}
}
