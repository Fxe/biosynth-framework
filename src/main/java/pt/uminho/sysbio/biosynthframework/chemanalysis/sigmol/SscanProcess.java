package pt.uminho.sysbio.biosynthframework.chemanalysis.sigmol;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SscanProcess {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(SscanProcess.class);
	
	public static String SSCAN_PATH = "sscan.exe";
	
	public String[] execute(String mol, SigMolMode mode, int h) throws IOException  {
		String modeParam = String.format("%s%d", mode.toString().toLowerCase(), h*2);
		ProcessBuilder pb = new ProcessBuilder(SSCAN_PATH, "-o", "D:/tmp/gg.sig", "C00877.mol", modeParam);
		
		LOGGER.debug("{}", StringUtils.join(pb.command(), ' '));
		
		final Process process = pb.start();
		
		IOUtils.write(mol, process.getOutputStream());
		process.getOutputStream().close();
		
		String[] output = new String[2];
		output[0] = IOUtils.toString(process.getInputStream());
		output[1] = IOUtils.toString(process.getErrorStream());
		
		return output;
	}
}
