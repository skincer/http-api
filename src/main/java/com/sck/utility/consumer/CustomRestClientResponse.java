package com.sck.utility.consumer;


import org.apache.http.Header;

import java.util.Arrays;

/**
 * Created by TEKKINCERS on 11/11/2016.
 */
public class CustomRestClientResponse {

    private Header[] headers;
    private int statusCode;
    private String response;

    public CustomRestClientResponse() {
    }

    public CustomRestClientResponse(int statusCode, String response) {
        this.statusCode = statusCode;
        this.response = response;
    }

    public CustomRestClientResponse(Header[] headers, int statusCode, String response) {
        this.headers = headers;
        this.statusCode = statusCode;
        this.response = response;
    }

    public Header[] getHeaders() {
        return headers;
    }

    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "RestClientResponse{" +
                "headers=" + Arrays.toString(headers) +
                ", statusCode=" + statusCode +
                ", response='" + response + '\'' +
                '}';
    }
}
