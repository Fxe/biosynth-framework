package pt.uminho.sysbio.biosynthframework.chemanalysis.openbabel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.ProcessWorker;

public class OpenBabelProcess {
	private final static Logger LOGGER = LoggerFactory.getLogger(OpenBabelProcess.class);
	
	public static String OBABEL_PATH = "obabel.exe";
	
	public String[] execute(String stdin) throws IOException {		
		return execute(stdin, new HashMap<String, String> ());
	}
	
	public String[] execute(String stdin, String ...params) throws IOException {
		Map<String, String> args = new HashMap<> ();
		for (String p : params) {
			args.put(p, null);
		}
		
		return execute(stdin, args);
	}
	
	public String[] execute(String stdin, Map<String, String> args) throws IOException {
		List<String> argL = new ArrayList<> ();
		
		argL.add(OBABEL_PATH);
		
		for (String k : args.keySet()) {
			String v = args.get(k);
			argL.add(k);
			if (v != null) argL.add(v);
		}
		
		ProcessBuilder pb = new ProcessBuilder(argL.toArray(new String[0]));
		LOGGER.debug("{}", StringUtils.join(pb.command(), ' '));
		final Process process = pb.start();
		IOUtils.write(stdin, process.getOutputStream());
		
		process.getOutputStream().close();
		
		ProcessWorker worker = new ProcessWorker(process);
		Thread thread = new Thread(worker);
		thread.start();
		try {
			thread.join(2 *  1000);
		} catch (InterruptedException ex) {
			thread.interrupt();
			ex.printStackTrace();
		} finally {
			process.destroy();
		}
		
		StringBuilder stdout = new StringBuilder();
		StringBuilder stderr = new StringBuilder();
		stdout.append(IOUtils.toString(process.getInputStream()));
		stderr.append(IOUtils.toString(process.getErrorStream()));
		
		String[] output = new String[2];
		output[0] = stdout.toString();
		output[1] = stderr.toString();
		
		return output;
	}
}
