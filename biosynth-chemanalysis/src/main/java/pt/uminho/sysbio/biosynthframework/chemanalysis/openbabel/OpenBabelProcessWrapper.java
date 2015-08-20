package pt.uminho.sysbio.biosynthframework.chemanalysis.openbabel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Very dumb move use a process instead a java thingy ...
 * @author Filipe
 *
 */
@Deprecated
public class OpenBabelProcessWrapper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenBabelProcessWrapper.class);
	
	public static String JVM_PATH = "C:/Program Files (x86)/Java/jre8/bin/java.exe";
	public static String OBABEL_PATH = "D:/home/fliu/workspace/java/biosynth-chemanalysis/lib/obabel.exe";
	public static String SMILES_TO_CAN = "D:/SmilesToCan.jar";
	public static String INCHI_TO_CAN = "D:/InchiToCan.jar";
	
	public static String convert(String inputType, String outputType, String data) {
		try {
			String[] out = execute(data, OBABEL_PATH, "-i", inputType, "-o", outputType);
			return out[0].isEmpty() ? null : out[0];
		} catch (IOException io) {
			return null;
		}
	}
	
	public static String[] execute(String stdin, String...args) throws IOException {
		LOGGER.debug("Process: " + StringUtils.join(args, ' '));
		LOGGER.trace(stdin);
		
		StringBuilder stdout = new StringBuilder();
		StringBuilder stderr = new StringBuilder();
		
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		final Process process = processBuilder.start();
		
		IOUtils.write(stdin, process.getOutputStream());
		process.getOutputStream().close();

		stdout.append(IOUtils.toString(process.getInputStream()));
		stderr.append(IOUtils.toString(process.getErrorStream()));
		
		String[] output = new String[2];
		output[0] = stdout.toString().trim();
		output[1] = stderr.toString().trim();
		
		LOGGER.trace("stdout: " + output[0]);
		LOGGER.trace("stderr: " + output[1]);
		return output;
	}

	@Deprecated
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
	
	@Deprecated
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
