package com.faculty.pcd.homework1.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomFileReader {

	private String receivedFile;
	private File file;
	
	private int fileDataLength = 0;
	private String fileName = "";
	private String fileExtension = "";
	
	private byte[] fileData;

	public CustomFileReader(String filename) {
		this.receivedFile = filename;
	}

	public void readFile() throws IOException {
		file = new File(receivedFile);
		fileData = new byte[(int) file.length()];
		BufferedInputStream br = new BufferedInputStream(new FileInputStream(file));

		int bytesRead = 0;
		int b;
		while ((b = br.read()) != -1) {
			fileData[bytesRead++] = (byte) b;
		}

		br.close();
		fileDataLength = fileData.length;
		fileName = file.getName();
		
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
		    fileExtension = fileName.substring(i+1);
		}
	}

	public List<Byte[]> splitFileInBytesMessages() throws IOException {
		List<Byte[]> messagesInBytes = new ArrayList<Byte[]>();

		Byte[] message = null;
		if (fileData.length < 65535) {
			message = new Byte[fileData.length];
		} else {
			message = new Byte[65535];
		}
		for (int i = 0; i < fileData.length; i++) {
			if (i % 65535 == 0) {
				if (i != 0 && i < fileData.length - 1) {
					messagesInBytes.add(message);
				}
				if( (fileData.length - (i + 1)) <  65535 ) {
					message = new Byte[fileData.length - i + 1 ];
				}else {
					message = new Byte[65535];
				}		
				message[i % 65535] = fileData[i];
				if (i == fileData.length - 1) {
					messagesInBytes.add(message);
				}
			} else {
				message[i % 65535] = fileData[i];
				if (i == fileData.length - 1) {
					messagesInBytes.add(message);
				}
			}
		}

		return messagesInBytes;
	}
	
	public int getFileDataLength() {
		return fileDataLength;
	}

	public String getFileName() {
		return fileName;
	}

	public String getFileExtension() {
		return fileExtension;
	}
}
