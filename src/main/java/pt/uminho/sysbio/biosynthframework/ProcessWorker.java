package pt.uminho.sysbio.biosynthframework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessWorker implements Runnable {
	
	private static Logger LOGGER = LoggerFactory.getLogger(ProcessWorker.class);
	
	private final Process process;
	private Integer exit;
	
	public ProcessWorker(Process process) {
		this.process = process;
	}

	@Override
	public void run() {
		try {
			exit = process.waitFor();
			LOGGER.trace("exit status for {}: {}", process, exit);
		} catch (InterruptedException e) {
			return;
		}
		
	}
}
