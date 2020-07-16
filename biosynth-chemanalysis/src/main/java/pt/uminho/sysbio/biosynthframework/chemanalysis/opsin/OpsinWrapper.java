package pt.uminho.sysbio.biosynthframework.chemanalysis.opsin;

import uk.ac.cam.ch.wwmm.opsin.NameToInchi;
import uk.ac.cam.ch.wwmm.opsin.NameToStructure;
import uk.ac.cam.ch.wwmm.opsin.NameToStructureException;

public class OpsinWrapper {
	
	public static String iupacToSmiles(String name) throws NameToStructureException {
		
		NameToStructure nameToStructure = NameToStructure.getInstance();
		return nameToStructure.parseToSmiles(name);
	}
	
	public static String iupacToCml(String name) throws NameToStructureException {
		NameToStructure nameToStructure = NameToStructure.getInstance();
		String xml = nameToStructure.parseToCML(name);
//		Element element = nameToStructure.parseToCML(name);
		return xml;
//		return ;nameToStructure.parseToCML(name)
	}
	
//	public static String asf(String inchi) {
//		Inchi
//	}
	
	public static String iupacToInchi(String name) throws NameToStructureException {
		NameToInchi nameToInchi = new NameToInchi();
		return nameToInchi.parseToStdInchi(name);
	}
}
