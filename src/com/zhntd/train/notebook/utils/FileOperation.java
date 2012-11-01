package com.zhntd.train.notebook.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileOperation {
	private File folder;
	private File file;
	private BufferedWriter writer;
	private BufferedReader reader;

	private boolean createFolder() {
		folder = new File(Utils.PATH_FOLDER);
		if (!folder.exists()) {
			return folder.mkdirs();
		}
		return true;
	}

	private boolean createFile() {
		file = new File(Utils.PATH);
		if (!isFileExisted()) {
			try {
				return file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public boolean doBackupReady() {
		if (createFolder()) {
			if (createFile()) {
				try {
					writer = new BufferedWriter(new FileWriter(file));
					return true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	public void backupLine(StringBuffer strBuffer) {
		try {
			writer.write(strBuffer.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void doWriterClose() {
		try {
			if (writer != null) {
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public boolean isFileExisted() {
		file = new File(Utils.PATH);
		return file.exists();
	}

	public void doRestoreReady () {
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public String readLine() {
		String record = null;
		try {
			record = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return record;
	}

	public void doReaderClose() {
		try {
			if (reader != null) {
				reader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
