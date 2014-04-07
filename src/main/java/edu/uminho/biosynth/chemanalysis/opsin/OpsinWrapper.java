package edu.uminho.biosynth.chemanalysis.opsin;

import nu.xom.Element;
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
		Element element = nameToStructure.parseToCML(name);
		return element.toXML();
//		return ;nameToStructure.parseToCML(name)
	}
	
	public static String iupacToInchi(String name) throws NameToStructureException {
		NameToInchi nameToInchi = new NameToInchi();
		return nameToInchi.parseToStdInchi(name);
	}
}
