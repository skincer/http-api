package com.sck.utility.consumer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * Created by TEKKINCERS on 11/11/2016.
 */
@Component
public class CustomRestClient {

    private CloseableHttpClient client;

    public CustomRestClient() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setConnectionManagerShared(true);
        client = builder.build();
    }

    public CustomRestClientResponse getRequest(String url, HttpHeaders httpHeaders) throws IOException {
        HttpGet request = new HttpGet(url);

        for(Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
            request.addHeader(entry.getKey(), entry.getValue().get(0));
        }

        return buildResponse(client.execute(request));
    }

    public CustomRestClientResponse getRequest(String url) throws IOException {
        HttpGet request = new HttpGet(url);

        request.setHeader("Content-Type", "application/json");

        return buildResponse(client.execute(request));
    }

    public CustomRestClientResponse putRequest(String url, String body, HttpHeaders httpHeaders) throws IOException {
        HttpPut request = new HttpPut(url);

        for(Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
            request.addHeader(entry.getKey(), entry.getValue().get(0));
        }

        HttpEntity httpEntity = new StringEntity(body);
        request.setEntity(httpEntity);

        return buildResponse(client.execute(request));
    }

    public CustomRestClientResponse putRequest(String url, String body) throws IOException {
        HttpPut request = new HttpPut(url);

        request.setHeader("Content-Type", "application/json");
        HttpEntity httpEntity = new StringEntity(body);
        request.setEntity(httpEntity);

        return buildResponse(client.execute(request));
    }

    public CustomRestClientResponse postRequest(String url, String body, HttpHeaders httpHeaders) throws IOException {
        HttpPost request = new HttpPost(url);

        for(Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
            request.addHeader(entry.getKey(), entry.getValue().get(0));
        }

        HttpEntity httpEntity = new StringEntity(body);
        request.setEntity(httpEntity);

        return buildResponse(client.execute(request));
    }


    public CustomRestClientResponse postRequest(String url, String body) throws IOException {
        HttpPost request = new HttpPost(url);

        request.setHeader("Content-Type", "application/json");
        HttpEntity httpEntity = new StringEntity(body);
        request.setEntity(httpEntity);

        return buildResponse(client.execute(request));
    }


    private CustomRestClientResponse buildResponse(HttpResponse httpResponse) throws IOException {
        CustomRestClientResponse response = new CustomRestClientResponse();
        response.setHeaders(httpResponse.getAllHeaders());
        response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        StringBuilder content = new StringBuilder();

        if(httpResponse.getEntity() != null) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();
        }
        client.close();
        response.setResponse(content.toString());

        return response;
    }

}
