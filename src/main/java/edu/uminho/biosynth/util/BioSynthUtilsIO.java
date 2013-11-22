package edu.uminho.biosynth.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class BioSynthUtilsIO {
	
	public static String readFromFile(String file) {
		StringBuilder sb = new StringBuilder();
		
		try {
			File file_ptr = new File(file);
			FileReader reader = new FileReader(file_ptr);
			BufferedReader br = new BufferedReader(reader);
			String line;
			while ( (line = br.readLine()) != null ) {
				sb.append(line).append('\n');
			}
			
			br.close();
			reader.close();
		} catch (IOException fnfEx) {
			System.err.println("FILE NOT FOUND - " + file);
		}

		return sb.toString();
	}
}
