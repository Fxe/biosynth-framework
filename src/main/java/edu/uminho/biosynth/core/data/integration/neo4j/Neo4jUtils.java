package edu.uminho.biosynth.core.data.integration.neo4j;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Label;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;

/**
 * Utilities used to perform several Neo4j operations.
 * 
 * @author Filipe
 * 
 * @see pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils
 */
@Deprecated
public class Neo4jUtils {
	
	private static final Set<String> majorCompoundLabels = new HashSet<> ();
	
	static {
		for (MetaboliteMajorLabel label : MetaboliteMajorLabel.values()) {
			majorCompoundLabels.add(label.toString());
		}
	}
	
	public static String getMetaboliteMajorLabel(Iterable<Label> labels) {
		Set<String> labelSet = new HashSet<> ();
		for (Label l : labels) labelSet.add(l.toString());
//		System.out.println(majorCompoundLabels);
//		System.out.println(labelSet);
		labelSet.retainAll(majorCompoundLabels);
//		System.out.println(labelSet);
		if (labelSet.isEmpty() || labelSet.size() > 1) System.err.println("ERROR FOR " + labels);
		
		if (labelSet.isEmpty()) return "NOTFOUND";
		return labelSet.iterator().next();
	}
}
