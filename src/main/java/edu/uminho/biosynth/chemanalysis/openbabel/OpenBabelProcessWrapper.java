package edu.uminho.biosynth.chemanalysis.openbabel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class OpenBabelProcessWrapper {
	
	public static String JVM_PATH = "C:/Program Files (x86)/Java/jre8/bin/java.exe";
	public static String SMILES_TO_CAN = "D:/SmilesToCan.jar";
	public static String INCHI_TO_CAN = "D:/InchiToCan.jar";

	public static String convertSmilesToCannonicalSmiles(String smiles) throws IOException {
		StringBuilder sb = new StringBuilder();
		ProcessBuilder pb = new ProcessBuilder(JVM_PATH,
				"-jar", SMILES_TO_CAN, smiles);
		Process process = pb.start();
		InputStream stdout = process.getInputStream ();  
		
		BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
		String line;
        while ((line = reader.readLine()) != null) {
        	sb.append(line);
        }
        
        if (sb.length() < 1) return null;
        return sb.toString();
	}
	
	public static String convertInchiToCannonicalSmiles(String inchi) throws IOException {
		StringBuilder sb = new StringBuilder();
		ProcessBuilder pb = new ProcessBuilder(JVM_PATH,
				"-jar", INCHI_TO_CAN, inchi);
		Process process = pb.start();
		InputStream stdout = process.getInputStream ();  
		
		BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
		String line;
        while ((line = reader.readLine()) != null) {
        	sb.append(line);
        }
        
        return sb.toString();
	}
}
