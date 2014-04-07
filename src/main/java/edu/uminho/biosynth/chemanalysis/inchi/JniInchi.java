package edu.uminho.biosynth.chemanalysis.inchi;

import org.apache.log4j.Logger;

import net.sf.jniinchi.INCHI_KEY;
import net.sf.jniinchi.JniInchiException;
import net.sf.jniinchi.JniInchiOutputKey;
import net.sf.jniinchi.JniInchiWrapper;

public class JniInchi {
	
	private static final Logger LOGGER = Logger.getLogger(JniInchi.class);
	
	public static INCHI_KEY LAST_CALL_STATUS = null;
	
	public static String getInchiKeyFromInchi(String inchi) {
		JniInchiOutputKey out;
		String key = null;
		try {
			out = JniInchiWrapper.getInchiKey(inchi);
			LAST_CALL_STATUS = out.getReturnStatus();
			key = out.getKey();
		} catch (JniInchiException e) {
			LOGGER.error("JniInchiException " + e.getMessage());
		}
		
		return key;
	}
}
