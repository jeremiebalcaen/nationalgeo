package com.jba.nationalgeo;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSonReader {

    private static final String PHOTO_OF_THE_DAY_URL = "https://www.nationalgeographic.com/photography/photo-of-the-day/_jcr_content/.gallery.json";

    private static final String ROOT_PATH = "https://www.nationalgeographic.com";

    public static void main(String[] args) throws Exception {
        String previousItem = PHOTO_OF_THE_DAY_URL;
        while(true) {
            System.out.println("\nGoing to download " + previousItem + "\n");
            previousItem = JSonReader.downloadFiles(previousItem);
            previousItem = ROOT_PATH + previousItem;
        }
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
        JSONObject image = item.getJSONObject("image");
        String url = image.getString("uri");
        System.out.print("URL to download: " + url);
        return url;
    }

    public static String downloadFiles(String jsonUrl) throws Exception {
        List<String> uris = new ArrayList<>();
        JSONObject json = readJsonFromUrl(jsonUrl, null, null);
        JSONArray items = json.getJSONArray("items");
        for(Object o: items){
            if ( o instanceof JSONObject ) {
                JSONObject item = ((JSONObject)o);
                JSONObject image = item.getJSONObject("image");
                String url = image.getString("uri");
                String[] urlSplit = url.split("/");
                int size = urlSplit.length;
                String imageName = String.join("_", urlSplit[size-3], urlSplit[size-2], urlSplit[size - 1]);

                System.out.print("Downloading image: " + url + "\n");
                Photo photo = new Photo();
                photo.downloadPhoto(url, imageName);
            }
        }
        String previousItem = json.getString("previousEndpoint");
        return previousItem;


    }



}
