package com.vega.springmvc.controller;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vega.springmvc.service.transform.CoreResponse;

public class PartnerService<I> {
	protected RestTemplate restTemplate;
	protected ObjectMapper mapper;
	protected HttpClient httpClient;
	protected HttpHeaders headers;

	protected String url;
	protected String host = "http://localhost:8080";
	protected String contextPath = "LendingApi";

	private ParameterizedTypeReference<CoreResponse> parameterizedTypeReference;

	public PartnerService(String context, ParameterizedTypeReference<CoreResponse> parameterizedTypeReference) {
		this.url = host + contextPath + context;
		this.parameterizedTypeReference = parameterizedTypeReference;

		headers = new HttpHeaders();
	}

	public void setUp() {
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

		HttpClientBuilder clientBuilder = HttpClients.custom().setConnectionManager(connectionManager)
				.setRetryHandler(new DefaultHttpRequestRetryHandler(5, true));

		httpClient = clientBuilder.build();
		restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
	}

	protected CoreResponse post(I input) {
		ResponseEntity<CoreResponse> response = restTemplate.exchange(url, HttpMethod.POST,
				new HttpEntity<I>(input, headers), parameterizedTypeReference);
		return response.getBody();
	}
}
