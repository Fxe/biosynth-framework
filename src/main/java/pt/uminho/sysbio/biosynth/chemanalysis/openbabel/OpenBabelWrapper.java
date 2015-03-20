package pt.uminho.sysbio.biosynth.chemanalysis.openbabel;

import org.openbabel.OBConversion;
import org.openbabel.OBMol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenBabelWrapper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenBabelWrapper.class);

//	public static boolean INITIALIZED = false;
	
	public static void initializeLibrary() {
		LOGGER.info("Loading library");
//		System.setProperty("java.library.path", System.getProperty("java.library.path").concat(";D:\\home\\workspace\\java\\biosynth-chemanalysis\\lib"));
//		System.out.println(System.getProperty("java.library.path"));
		System.loadLibrary("openbabel_java");
//		System.load("D:\\home\\workspace\\java\\biosynth-chemanalysis\\lib\\openbabel_java.dll");
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
