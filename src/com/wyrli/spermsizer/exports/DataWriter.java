package com.wyrli.spermsizer.exports;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DataWriter {
	private BufferedWriter writer;

	public DataWriter(String folder, String fileName) {
		FileWriter fw;
		try {
			fw = new FileWriter(new File(folder + "/" + fileName));
			writer = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(String s) {
		try {
			writer.write(s + System.lineSeparator());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}