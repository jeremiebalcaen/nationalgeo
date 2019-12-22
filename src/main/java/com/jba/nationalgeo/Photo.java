package com.jba.nationalgeo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
		Photo http = new Photo();
		if(getArchivedPhoto) {
			getRandomPhoto();
		}
		else {
			String url = JSonReader.getUrl(proxyHost, proxyPort);
			http.downloadPhoto(url);
			http.copyPhotoOfTheDay();
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

	private void copyPhotoOfTheDay() throws IOException {
		File fileSource = new File(getPhotoDownloadLocation());
		Path fileSourcePath = fileSource.toPath();
		File fileArchiveDest = new File(getPhotoArchiveLocation() + getArchiveDateFileName() + "_" + PHOTO_FILE_NAME);
		Path fileArchiveDestPath = fileArchiveDest.toPath();
		Files.createDirectories(fileArchiveDestPath.getParent());
		Files.copy(fileSourcePath, fileArchiveDestPath);
	}

	private void downloadPhoto(String urlString) throws Exception {
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
