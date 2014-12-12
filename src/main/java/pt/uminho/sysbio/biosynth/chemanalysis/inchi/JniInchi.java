package pt.uminho.sysbio.biosynth.chemanalysis.inchi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jniinchi.INCHI_KEY;
import net.sf.jniinchi.JniInchiException;
import net.sf.jniinchi.JniInchiOutputKey;
import net.sf.jniinchi.JniInchiWrapper;

public class JniInchi {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JniInchi.class);
	
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
