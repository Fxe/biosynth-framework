package pt.uminho.sysbio.biosynthframework.chemanalysis;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import pt.uminho.sysbio.biosynthframework.util.DigestUtils;

public class SignatureUtils {
	
	public static Map<Signature, Double> intersection(Map<Signature, Double> a, Map<Signature, Double> b) {
//		Map<Signature, Double> result = new HashMap<> ();
		
		for (Signature s : a.keySet()) {
			if (b.containsKey(s)) {
				//what to do with negatives ?
			}
		}
		
		throw new RuntimeException("not implemented");
	}
	
	/**
	 * a - b
	 * @param a
	 * @param b
	 * @return
	 */
	public static Map<Signature, Double> diff(Map<Signature, Double> a, Map<Signature, Double> b) {
		//too lazy to do this ... just sub and remove negatives
		//(zeros already gone from sub)
		Map<Signature, Double> c_ = sub(a, b);
		Map<Signature, Double> c = new HashMap<Signature, Double> ();
		for (Signature s : c_.keySet()) {
			double v = c_.get(s);
			if (v > 0) {
				c.put(s, v);
			}
		}
		return c;
	}
	
	public static MolecularSignature scaleSignature(MolecularSignature signatureSet, double alpha) {
		if (signatureSet == null) return null;
		Map<Signature, Double> signature = signatureSet.getSignatureMap();
		Map<Signature, Double> r = new HashMap<> ();
		for (Signature s : signature.keySet()) {
			r.put(s, signature.get(s) * alpha);
		}
		MolecularSignature signatureSet_ = new MolecularSignature();
		signatureSet_.setId(signatureSet.getId());
		signatureSet_.setSignatureMap(r);
		signatureSet_.setH(signatureSet.getH());
		
		return signatureSet_;
	}
	
	public static MolecularSignature subtract(MolecularSignature a, MolecularSignature b) {
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
		MolecularSignature c = new MolecularSignature();
		c.setSignatureMap(r);
		
		return c;
	}
	
	public static Map<Signature, Double> sub(Map<Signature, Double> a, Map<Signature, Double> b) {
		Map<Signature, Double> c = new HashMap<> ();
		Set<Signature> common = new HashSet<> (a.keySet());
		common.retainAll(b.keySet());
		for (Signature s : a.keySet()) {
			if (common.contains(s)) {
				double v = a.get(s) - b.get(s);
				if (v != 0.0) c.put(s, v);
			} else {
				c.put(s, a.get(s));
			}
		}
		
		for (Signature s : b.keySet()) {
			if (!common.contains(s)) {
				c.put(s, -1 * b.get(s));
			}
		}
		return c;
	}
	
	public static MolecularSignature sumSignatures(List<MolecularSignature> signatureList) {
		Map<Signature, Double> result = new HashMap<> ();
		for (MolecularSignature signatureSet : signatureList) {
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
		
		MolecularSignature signatureSet = new MolecularSignature();
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
	
	public static String toString(MolecularSignature sgs) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("id:%d, h:%d, stereo:%s\n", sgs.getId(), sgs.getH(), sgs.isStereo()));
		for (Signature sig : sgs.getSignatureMap().keySet()) {
			double value = sgs.getSignatureMap().get(sig);
			sb.append(value).append("\t").append(sig).append('\n');
		}
		
		return sb.toString();
	}
	
	public static String toString(ReactionSignature sgs) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("id:%d, h:%d, stereo:%s\n", sgs.getId(), sgs.getH(), sgs.isStereo()));
		for (Signature sig : sgs.getLeftSignatureMap().keySet()) {
			double value = sgs.getLeftSignatureMap().get(sig);
			sb.append(value).append("\t").append(sig).append('\n');
		}
		sb.append("=================\n");
		for (Signature sig : sgs.getRightSignatureMap().keySet()) {
			double value = sgs.getRightSignatureMap().get(sig);
			sb.append(value).append("\t").append(sig).append('\n');
		}
		
		return sb.toString();
	}

	public static Map<Signature, Double> absSignatureMap(Map<Signature, Double> map) {
		Map<Signature, Double> r = new HashMap<> ();
		for (Signature s : map.keySet()) {
			double v = Math.abs(map.get(s));
			r.put(s, v);
		}
		return r;
	}

	//should return multipliers
	public static List<Signature> intersect(
			Map<Signature, Double> sig1,
			Map<Signature, Double> sig2) {
		List<Signature> signatureList = new ArrayList<> ();
		
		Set<Signature> sigs = new HashSet<> (sig1.keySet());
		sigs.retainAll(sig2.keySet());
		signatureList.addAll(sigs);
		return signatureList;
	}

	public static Map<Signature, Double> sum(
			List<Map<Signature, Double>> lMsigs) {
		Map<Signature, Double> result = new HashMap<> ();
		for (Map<Signature, Double> signatures : lMsigs) {
			for (Signature s : signatures.keySet()) {
				if (!result.containsKey(s)) {
					result.put(s, 0.0);
				}
				
				double v = result.get(s);
				v += signatures.get(s);
				result.put(s, v);
			}
		}

		return result;
	}
	
	public static Map<Signature, Double> sum(
			Map<Signature, Double> a, Map<Signature, Double> b) {
		List<Map<Signature, Double>> sum = new ArrayList<> ();
		sum.add(a);
		sum.add(b);
		return sum(sum);
	}

	public static ReactionSignature buildReactionSignature(int h, boolean stereo, MolecularSignature[] la, MolecularSignature[] ra) {
		List<MolecularSignature> l = Arrays.asList(la);
		List<MolecularSignature> r = Arrays.asList(ra);
		
		MolecularSignature l_sum = SignatureUtils.sumSignatures(l);
		MolecularSignature r_sum = SignatureUtils.sumSignatures(r);
		MolecularSignature r_sub = SignatureUtils.subtract(r_sum, l_sum);
		
		ReactionSignature rsig = new ReactionSignature(); //.subtract(r_sum, l_sum);
		rsig.setH(h);
		rsig.setStereo(stereo);
		
		for (Signature s : r_sub.getSignatureMap().keySet()) {
			double v = r_sub.getSignatureMap().get(s);
			if (v < 0.0) rsig.getLeftSignatureMap().put(s, v);
		}
		
		for (Signature s : r_sub.getSignatureMap().keySet()) {
			double v = r_sub.getSignatureMap().get(s);
			if (v > 0.0) rsig.getRightSignatureMap().put(s, v);
		}
		
		rsig.setLeftSignatureMap(SignatureUtils.absSignatureMap(rsig.getLeftSignatureMap()));
		rsig.setRightSignatureMap(SignatureUtils.absSignatureMap(rsig.getRightSignatureMap()));
		
		return rsig;
	}

	public static double numberOfSignatures(
			Map<Signature, Double> sigs) {
		double s = 0.0d;
		for (Double v : sigs.values()) {
			s += v;
		}
		
		return s;
	}

	public static long hash(Map<Signature, Double> signatureMap) {
		final long prime = 37;
		long h = 0;
		Iterator<Entry<Signature, Double>> i = signatureMap.entrySet().iterator();
		while (i.hasNext()) {
			Entry<Signature, Double> entry = i.next();
			long hash = entry.getKey().hash() * prime + DigestUtils.hash(Double.toString(entry.getValue()));
			h += hash;
		}
		return h;
	}
}
