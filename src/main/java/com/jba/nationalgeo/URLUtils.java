package com.jba.nationalgeo;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;

import java.io.InputStream;

public class URLUtils
{

    public static InputStream getContent(String url, String proxyHost, String proxyPort) throws Exception {
        CloseableHttpClient httpclient;
        InputStream in = null;
        if(proxyHost != null) {
            HttpHost proxy = new HttpHost(proxyHost, Integer.parseInt(proxyPort), "http");
            httpclient = HttpClients.custom()
                    .setRedirectStrategy(new LaxRedirectStrategy())
                    .setProxy(proxy)
                    .build();
        }
        else {

            httpclient = HttpClients.custom()
                    .setRedirectStrategy(new LaxRedirectStrategy())
                    .build();
        }


        HttpClientContext context = HttpClientContext.create();
        HttpGet httpGet = new HttpGet(url);
        httpclient.execute(httpGet, context);
        HttpResponse response = httpclient.execute(httpGet);

        return response.getEntity().getContent();

    }
}
