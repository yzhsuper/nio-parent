package com.tutorial.nio.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class CopyFile {

	public static void fileCopy(String in, String out) throws IOException {
		FileChannel inChannel = null;
		FileChannel outChannel = null;
		FileInputStream inputFile = null;
		FileOutputStream outFile = null;
		try {
			inputFile = new FileInputStream(in);
			inChannel = inputFile.getChannel();
			outFile = new FileOutputStream(out);
			outChannel = outFile.getChannel();
			// inChannel.transferTo(0, inChannel.size(), outChannel);
			// original -- apparently has trouble copying large files on Windows

			// magic number for Windows, 64Mb - 32Kb)
			int maxCount = (64 * 1024 * 1024) - (32 * 1024);
			long size = inChannel.size();
			long position = 0;
			while (position < size) {
				position += inChannel.transferTo(position, maxCount, outChannel);
			}
		} finally {
			if (inputFile != null) {
				inputFile.close();
			}
			if (outFile != null) {
				outFile.close();
			}
			if (inChannel != null) {
				inChannel.close();
			}
			if (outChannel != null) {
				outChannel.close();
			}
		}
	}

	public static void main(String[] args) {
		try {
			CopyFile.fileCopy("/home/yangzhuo/untitled.js", "/home/yangzhuo/untitled.copy2.js");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
