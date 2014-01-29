package edu.uminho.biosynth.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

public class BioSynthUtilsIO {
	
	public static void printDir(String dir) {
		File f = new File(dir); // current directory

	    File[] files = f.listFiles();
	    for (File file : files) {
	        if (file.isDirectory()) {
	            System.out.print("directory:");
	        } else {
	            System.out.print("     file:");
	        }
	        
	        try {
	        	System.out.println(file.getCanonicalPath());
	        } catch (IOException ioEx) {
	        	System.out.println(ioEx);
	        }
	    }
	}
	
	public static void printDir() {
		printDir(".");
	}
	
	public static String readFromFile(String file) throws FileNotFoundException, IOException {
		StringBuilder sb = new StringBuilder();

		File file_ptr = new File(file);
		FileReader reader = new FileReader(file_ptr);
		BufferedReader br = new BufferedReader(reader);
		String line;
		while ( (line = br.readLine()) != null ) {
			sb.append(line).append('\n');
		}
		
		br.close();
		reader.close();

		return sb.toString();
	}
	
	public static void writeToFile(String data, String file) throws IOException {
		StringReader stringReader = new StringReader(data);
		BufferedReader bufferedReader = new BufferedReader(stringReader);
		FileWriter fileWriter = new FileWriter(file);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        for(String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
        }
        bufferedReader.close();
        bufferedWriter.close();
        fileWriter.close();
        bufferedReader.close();
        stringReader.close();
	}
}
