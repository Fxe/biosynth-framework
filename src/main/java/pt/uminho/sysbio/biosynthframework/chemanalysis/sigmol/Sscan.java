package pt.uminho.sysbio.biosynthframework.chemanalysis.sigmol;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.chemanalysis.Signature;

public class Sscan {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(Sscan.class);
	
	private boolean removeHydrogen = true;
	
	public static Map<Signature, Double> getSignatureSetFromMol(String mol, SigMolMode mode, int h) throws IOException {
//		StringBuilder stdout = new StringBuilder();
//		StringBuilder stderr = new StringBuilder();
//		
//		String modeParam = String.format("%s%d", mode.toString().toLowerCase(), h*2);
//		ProcessBuilder pb = new ProcessBuilder(SSCAN_PATH, "-o", "D:/tmp/gg.sig", "C00877.mol", modeParam);
//		final Process process = pb.start();
//		
//		String mol_ = obb.execute(mol, "-imol", "-omol", "-d")[0];
//		IOUtils.write(mol_, process.getOutputStream());
//		process.getOutputStream().close();
//		
//		stdout.append(IOUtils.toString(process.getInputStream()));
//		stderr.append(IOUtils.toString(process.getErrorStream()));
//		
//		
//		
//		String[] output = new String[2];
//		output[0] = stdout.toString();
//		output[1] = stderr.toString();
//		
//		LOGGER.debug("sscan stderr: " + output[1]);
//		return readSignature(new ByteArrayInputStream(output[0].getBytes()));
//		
		return null;
	}
}
