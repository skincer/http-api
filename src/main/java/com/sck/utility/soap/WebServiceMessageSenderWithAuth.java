package com.sck.utility.soap;

import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by TEKKINCERS on 3/4/2016.
 */
public class WebServiceMessageSenderWithAuth extends HttpUrlConnectionMessageSender {

    // Basic Base64Encoded(Username:Password)
    private String basicAuthString;

    public WebServiceMessageSenderWithAuth(String basicAuthString) {
        this.basicAuthString = basicAuthString;
    }

    @Override
    protected void prepareConnection(HttpURLConnection connection) throws IOException {

        connection.setRequestProperty("Authorization", basicAuthString);

        super.prepareConnection(connection);
    }
}
