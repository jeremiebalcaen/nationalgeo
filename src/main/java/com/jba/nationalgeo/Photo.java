package com.jba.nationalgeo;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Photo {

	private static final String PHOTO_PROGRAM_LOCATION = "C://Program Files//PhotoOfTheDay";

	private static final String PHOTO_FILE_NAME = "photooftheday.jpg";

	private static String photoProgramLocation;
	
	private static String proxyHost;
	
	private static String proxyPort;

	public static void main(String[] args) throws Exception {
		
		boolean getArchivedPhoto = false;
		if(args.length != 0) {
			String programLocation = args[0];
			setPhotoProgramLocation(programLocation);
			String getPhotoFromArchive = args[1];
			getArchivedPhoto = Boolean.valueOf(getPhotoFromArchive);
			if(args.length == 4) {
				proxyHost = args[2];
				proxyPort = args[3];
			}
		}
		if(getArchivedPhoto) {
			getRandomPhoto();
		}
		else {
			BufferedWriter bw = null;
			try {
				File fout = new File("C://projects//nationalgeo//download//listcontent");
				FileOutputStream fos = new FileOutputStream(fout, true);	 
				bw = new BufferedWriter(new OutputStreamWriter(fos));
				WallPaper wp = JSonReader.getUrl(proxyHost, proxyPort);
				
				downloadPhoto(bw, wp.getUrl(), wp.getImageName());
				copyPhotoOfTheDay(wp.getImageName());
				
				bw.write(wp.getUrl() + "\n" + wp.getTitle() + "\n" + wp.getImageName() + "\n\n");
				bw.flush();
			}
			finally {
	    		bw.close();
	    	}
			
		}
	}

	private static void getRandomPhoto() throws IOException {
		String archiveLocation = getPhotoArchiveLocation();

		File dir = new File(archiveLocation);
		File[] files = dir.listFiles();
		int size = files.length;

		int idz = (int)(Math.random() * size);
		File chosenFile = files[idz];

		File fileDest = new File(getPhotoDownloadLocation());
		System.out.println(String.format("Copying file [%1$s] to destination [%2$s]", chosenFile.toPath(), fileDest.toPath()));
		Files.copy(chosenFile.toPath(), fileDest.toPath(), StandardCopyOption.REPLACE_EXISTING);
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

	private static void copyPhotoOfTheDay(String imageName) throws IOException {
		File fileDest = new File(getPhotoDownloadLocation());
		File fileSource = new File(getPhotoArchiveLocation() + imageName);
		Files.copy(fileSource.toPath(), fileDest.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

	public static void downloadPhoto(BufferedWriter bw, String urlString, String name) throws Exception {
		InputStream in = URLUtils.getContent(urlString, proxyHost, proxyPort);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int n = 0;
		while (-1 != (n = in.read(buf))) {
			out.write(buf, 0, n);
		}
		out.close();
		in.close();
		byte[] bresponse = out.toByteArray();

		FileOutputStream fos = null;
		fos = new FileOutputStream("C://projects//nationalgeo//download//archive//" + name);
		fos.write(bresponse);
		fos.close();	
	}

}
