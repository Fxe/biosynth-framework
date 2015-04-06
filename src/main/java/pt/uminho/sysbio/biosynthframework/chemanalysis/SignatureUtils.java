package pt.uminho.sysbio.biosynthframework.chemanalysis;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;

public class SignatureUtils {
	
	public static SignatureSet scaleSignature(SignatureSet signatureSet, double alpha) {
		if (signatureSet == null) return null;
		Map<Signature, Double> signature = signatureSet.getSignatureMap();
		Map<Signature, Double> r = new HashMap<> ();
		for (Signature s : signature.keySet()) {
			r.put(s, signature.get(s) * alpha);
		}
		SignatureSet signatureSet_ = new SignatureSet();
		signatureSet_.setId(signatureSet.getId());
		signatureSet_.setSignatureMap(r);
		signatureSet_.setH(signatureSet.getH());
		
		return signatureSet_;
	}
	
	public static SignatureSet subtract(SignatureSet a, SignatureSet b) {
		Map<Signature, Double> r = new HashMap<> ();
		Set<Signature> common = new HashSet<> (a.getSignatureMap().keySet());
		common.retainAll(b.getSignatureMap().keySet());
		for (Signature s : a.getSignatureMap().keySet()) {
			if (common.contains(s)) {
				double v = a.getSignatureMap().get(s) - b.getSignatureMap().get(s);
				if (v != 0.0) r.put(s, v);
			} else {
				r.put(s, a.getSignatureMap().get(s));
			}
		}
		
		for (Signature s : b.getSignatureMap().keySet()) {
			if (!common.contains(s)) {
				r.put(s, -1 * b.getSignatureMap().get(s));
			}
		}
		SignatureSet c = new SignatureSet();
		c.setSignatureMap(r);
		
		return c;
	}
	
	public static SignatureSet sumSignatures(List<SignatureSet> signatureList) {
		Map<Signature, Double> result = new HashMap<> ();
		for (SignatureSet signatureSet : signatureList) {
			Map<Signature, Double> signatures = signatureSet.getSignatureMap();
			for (Signature s : signatures.keySet()) {
				if (!result.containsKey(s)) {
					result.put(s, 0.0);
				}
				
				double v = result.get(s);
				v += signatures.get(s);
				result.put(s, v);
			}
		}
		
		SignatureSet signatureSet = new SignatureSet();
		signatureSet.setSignatureMap(result);

		return signatureSet;
	}
	
	public static Map<Signature, Double> readSignature(InputStream is) throws IOException {
		Map<Signature, Double> signatures = new HashMap<> ();
		List<String> lines = IOUtils.readLines(is);
		for (String line : lines) {
			String[] col = line.split(" ");
			double v = Double.parseDouble(col[0]);
			String signature = null;
			if (col.length > 1) {
				signature = col[1].trim();
			}
			if (signature != null) {
				signatures.put(new Signature(signature), v);
			}
		}
		
		return signatures;
	}
	
	public static String toString(SignatureSet sgs) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("id:%d, h:%d, stereo:%s\n", sgs.getId(), sgs.getH(), sgs.isStereo()));
		for (Signature sig : sgs.getSignatureMap().keySet()) {
			double value = sgs.getSignatureMap().get(sig);
			sb.append(value).append("\t").append(sig).append('\n');
		}
		
		return sb.toString();
	}
}
