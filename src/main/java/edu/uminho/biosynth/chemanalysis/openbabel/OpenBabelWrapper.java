package edu.uminho.biosynth.chemanalysis.openbabel;

import org.openbabel.OBConversion;
import org.openbabel.OBMol;

public class OpenBabelWrapper {
	
//	public static boolean INITIALIZED = false;
	
	public static void initializeLibrary() {
//		System.out.println(System.getProperty("java.library.path"));
		System.loadLibrary("openbabel_java");
//		INITIALIZED = true;
	}
	
	public static String convert(String data, String in, String out) {
		OBConversion conv = new OBConversion();
		OBMol mol = new OBMol();
		conv.SetInFormat(in);
		conv.ReadString(mol, data);
		conv.SetOutFormat(out);
		return conv.WriteString(mol).trim();
	}

	public static String convertSmilesToCannonicalSmiles(String smiles) {
		return convert(smiles, "smi", "can");
	}
	
	public static String convertSmilesToInchi(String smiles) {
		return convert(smiles, "can", "inchi");
	}
	
	public static String convertMol2dToSmiles(String molFile) {
		return convert(molFile, "mol", "can");
	}
	
	public static String getFormula(String data, String format) {
		OBConversion conv = new OBConversion();
		OBMol mol = new OBMol();
		conv.SetInFormat(format);
		conv.ReadString(mol, data);
		
		return mol.GetFormula();
	}
}
