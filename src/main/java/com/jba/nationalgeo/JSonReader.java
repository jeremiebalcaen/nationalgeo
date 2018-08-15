package com.jba.nationalgeo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSonReader {

    private static final String PHOTO_OF_THE_DAY_URL = "https://www.nationalgeographic.com/photography/photo-of-the-day/_jcr_content/.gallery.json";

    public static void main(String[] args) throws Exception {
        String proxyHost = "10.7.80.40";
        String proxyPort = "8080";
        JSonReader.getUrl(proxyHost, proxyPort);
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static JSONObject readJsonFromUrl(String url, String proxyHost, String proxyPort) throws Exception {
        InputStream is = URLUtils.getContent(url, proxyHost, proxyPort);
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }



    public static String getUrl(String proxyHost, String proxyPort) throws Exception {
        JSONObject json = readJsonFromUrl(PHOTO_OF_THE_DAY_URL, proxyHost, proxyPort);
        System.out.println(json.toString());
        JSONArray items = json.getJSONArray("items");
        JSONObject item = items.getJSONObject(0);
        JSONObject sizes = item.getJSONObject("sizes");

        String url = null;

        if(sizes.has("2048")) {
            url = sizes.getString("2048");
        }

        System.out.print("URL to download: " + url);
        return url;
    }

}
