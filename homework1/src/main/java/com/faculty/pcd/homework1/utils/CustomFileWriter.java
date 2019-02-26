package com.faculty.pcd.homework1.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.faculty.pcd.homework1.globals.Constants;

public class CustomFileWriter {
	private File file;
	
	private String fileName;
	private String fileExtension;

	public CustomFileWriter(String fileName, String fileExtension) {
		this.fileName = fileName;
		this.fileExtension = fileExtension;
	}

	public int writeFile(byte[] dataToWrite) throws IOException {
//		(( !fileExtension.equals("")) ? (fileName +  "." + fileExtension) : fileName)
		file = new File(Constants.PATH_TO_SAVE_RESULTED_FILE, fileName);
		file.createNewFile(); // if file already exists will do nothing 
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        int bytesWritten = 0;
        
        for(int i = 0; i < dataToWrite.length; i++) {
            bos.write(dataToWrite[i]);
            bytesWritten++;
        }
        bos.close();
        
        return bytesWritten;
	}
}
