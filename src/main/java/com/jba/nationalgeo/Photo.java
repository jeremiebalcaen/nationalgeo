package com.jba.nationalgeo;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;

public class Photo {

	private static final String PHOTO_OF_THE_DAY_URL = "http://photography.nationalgeographic.com/photography/photo-of-the-day";

	private static final String PHOTO_PROGRAM_LOCATION = "C://Program Files//PhotoOfTheDay";

	private static final String PHOTO_FILE_NAME = "photooftheday.jpg";

	private static final String KEYWORD_TO_SEARCH_FOR = "aemLeadImage";

	private static final String HTTP = "http";
	
	private static String photoProgramLocation;
	
	private static String proxyHost;
	
	private static String proxyPort;
	
	public static void main(String[] args) throws Exception {
		if(args.length != 0) {
			String programLocation = args[0];
			setPhotoProgramLocation(programLocation);
			if(args.length == 3) {
				proxyHost = args[1];
				proxyPort = args[2];
			}
		}
		Photo http = new Photo();
		String url = http.getUrlOfPhoto(PHOTO_OF_THE_DAY_URL);
		http.downloadPhoto(url);
		http.copyPhotoOfTheDay();
	}
	
	private static String getPhotoProgramLocation() {
		if(photoProgramLocation == null) {
			return PHOTO_PROGRAM_LOCATION;
		}
		else {
			return photoProgramLocation;
		}
	}
	
	private static String getPhotoDownloadLocation() {
		return getPhotoProgramLocation() + "//" + PHOTO_FILE_NAME;
	}
	
	private static String getPhotoArchiveLocation() {
		return getPhotoProgramLocation() + "//archive//";
	}
	
	private static void setPhotoProgramLocation(String url) {
		System.out.println("Set Program location: " + url);
		photoProgramLocation = url;
	}

	// HTTP GET request
	private String getUrlOfPhoto(String url) throws Exception {
		InputStream in = getContent(url);
		BufferedReader rd = new BufferedReader
        	    (new InputStreamReader(in));
        
        return searchPhoto(rd);
	}

	private void copyPhotoOfTheDay() throws IOException {
		File fileSource = new File(getPhotoDownloadLocation());
		File fileArchiveDest = new File(getPhotoArchiveLocation() + getArchiveDateFileName() + "_" + PHOTO_FILE_NAME);
		Files.copy(fileSource.toPath(), fileArchiveDest.toPath());
	}

	private String searchPhoto(BufferedReader in) throws Exception {
		String inputLine;
		String targetLine = null;
		String result = null;
		System.out.println("Print Downloaded Page");
		System.out.println("**********************************************");
		while ((inputLine = in.readLine()) != null) {
			System.out.println(inputLine);
			if (inputLine.contains(KEYWORD_TO_SEARCH_FOR)) {
				targetLine = inputLine;
				break;
			}
		}
		in.close();
		System.out.println("**********************************************");
		String[] data = targetLine.split("'");
		for (String value : data) {
			if (value.contains(HTTP)) {
				result = value;
				break;
			}
		}

		// print result
		System.out.println("Result [" + result + "]");
		return result;
	}
	
	private InputStream getContent(String url) throws Exception {
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

	private void downloadPhoto(String urlString) throws Exception {
		InputStream in = getContent(urlString);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int n = 0;
		while (-1 != (n = in.read(buf))) {
			out.write(buf, 0, n);
		}
		out.close();
		in.close();
		byte[] bresponse = out.toByteArray();
		FileOutputStream fos = new FileOutputStream(getPhotoDownloadLocation());
		fos.write(bresponse);
		fos.close();	
	}

	private String getArchiveDateFileName() {
		Date date = Calendar.getInstance().getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(date);
	}

}
