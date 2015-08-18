package pt.uminho.sysbio.biosynthframework.sbml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlSbmlSpecie extends XmlObject {
	private Map<String, List<XmlObject>> listOfAnnotations = new HashMap<> ();
	
	public Map<String, List<XmlObject>> getListOfAnnotations() { return listOfAnnotations;}
	public void setListOfAnnotations(Map<String, List<XmlObject>> listOfAnnotations) { this.listOfAnnotations = listOfAnnotations;}

}
