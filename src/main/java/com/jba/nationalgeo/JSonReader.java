package com.jba.nationalgeo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSonReader {

    private static final String PHOTO_OF_THE_DAY_URL = "https://www.nationalgeographic.com/photography/photo-of-the-day/_jcr_content/.gallery.json";

    private static final String ROOT_PATH = "https://www.nationalgeographic.com";

    public static void main(String[] args) throws Exception {
    	BufferedWriter bw = null;
    	try {
	    	File fout = new File("C://projects//nationalgeo//download//archive//listcontent");
			FileOutputStream fos = new FileOutputStream(fout);	 
			bw = new BufferedWriter(new OutputStreamWriter(fos));
			
	        String previousItem = PHOTO_OF_THE_DAY_URL;
	        while(true) {
	            System.out.println("\nGoing to download " + previousItem + "\n");
	            bw.write("****************************************");
	            bw.write("\nGoing to download " + previousItem + "\n");
	            bw.newLine();
	            previousItem = JSonReader.downloadFile(bw, previousItem, null, null);	            
                bw.newLine();
                bw.flush();
	            previousItem = ROOT_PATH + previousItem;
	        }
    	}
    	finally {
    		bw.close();
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

    public static WallPaper getUrl(String proxyHost, String proxyPort) throws Exception {
    	WallPaper wp = new WallPaper();
        JSONObject json = readJsonFromUrl(PHOTO_OF_THE_DAY_URL, proxyHost, proxyPort);
        System.out.println(json.toString());
        JSONArray items = json.getJSONArray("items");
        JSONObject item = items.getJSONObject(0);
        JSONObject image = item.getJSONObject("image");
        String url = image.getString("uri");
        String title = image.getString("caption");
        String[] urlSplit = url.split("/");
        int size = urlSplit.length;
        String imageName = String.join("_", urlSplit[size-3], urlSplit[size-2], urlSplit[size - 1]);
        wp.setImageName(imageName);
        wp.setTitle(title);
        wp.setUrl(url);
        System.out.println("Downloading image: " + wp.getUrl() + "-" + wp.getTitle() + ":" + wp.getImageName() + "\n");
        return wp;
    }

   

    public static String downloadFile(BufferedWriter bw, String jsonUrl, String proxyHost, String proxyPort) throws Exception{
    	WallPaper wp = new WallPaper();
    	
    	if(jsonUrl == null) {
    		jsonUrl = PHOTO_OF_THE_DAY_URL;
    	}
        JSONObject json = readJsonFromUrl(jsonUrl, null, null);
        JSONArray items = json.getJSONArray("items");
        for(Object o: items){
            if ( o instanceof JSONObject ) {
                JSONObject item = ((JSONObject)o);
                JSONObject image = item.getJSONObject("image");
                String title = image.getString("caption");
                String url = image.getString("uri");
                String[] urlSplit = url.split("/");
                int size = urlSplit.length;
                String imageName = String.join("_", urlSplit[size-3], urlSplit[size-2], urlSplit[size - 1]);
                
                wp.setImageName(imageName);
                wp.setTitle(title);
                wp.setUrl(url);
                
                System.out.println("Downloading image: " + wp.getUrl() + "-" + wp.getTitle() + ":" + wp.getImageName() + "\n");
            	bw.write(wp.getUrl() + "\n" + wp.getTitle() + "\n" + wp.getImageName());
                bw.newLine();
                bw.newLine();
                bw.flush();
                
            	Photo.downloadPhoto(bw, wp.getUrl(), wp.getImageName());

            }
        }
        String previousItem = json.getString("previousEndpoint");
        return previousItem;
    }



}
