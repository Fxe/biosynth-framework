package edu.uminho.biosynth.core.data.integration.chimera.strategy;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.strategy.AbstractNeo4jClusteringStrategy;
import edu.uminho.biosynth.core.data.integration.neo4j.CompoundNodeLabel;

public class BiggCompoundMatcherStrategy extends AbstractNeo4jClusteringStrategy {

//	private String getFormula(Node node) {
//		Set<Node> formulaNodes = new HashSet<> ();
//		for (Relationship relationship : node.getRelationships(CompoundRelationshipType.HasFormula)) {
//			formulaNodes.add(relationship.getOtherNode(node));
//		}
//		
//		if (formulaNodes.size() > 1) System.out.println("More than one formula");
//		
//		for (Node formulaNode : formulaNodes) {
//			if (formulaNode.hasLabel(CompoundPropertyLabel.Formula)) {
//				String formula = (String)formulaNode.getProperty("formula");
//				if (!formula.trim().isEmpty()) return formula.trim();
//			}
//		}
//		
//		return null;
//	}
	
	public BiggCompoundMatcherStrategy() {
		this.initialNodeLabel = MetaboliteMajorLabel.BiGG;
	}
	
	private String getIsotopeFormula(Node node) {
		Set<Node> formulaNodes = new HashSet<> ();
		for (Relationship relationship : node.getRelationships(MetaboliteRelationshipType.HasMolecularFormula)) {
			formulaNodes.add(relationship.getOtherNode(node));
		}
		
		if (formulaNodes.size() > 1) System.out.println("More than one formula");
		
		for (Node formulaNode : formulaNodes) {
			if (formulaNode.hasLabel(MetabolitePropertyLabel.MolecularFormula)) {
				for (Relationship relationship : formulaNode
						.getRelationships(MetaboliteRelationshipType.Isomorphic)) {
					Node isotopeFormulaNode = relationship.getOtherNode(formulaNode);
					String isotopeFormula = (String) isotopeFormulaNode.getProperty("formula");
					if (!isotopeFormula.trim().isEmpty()) return isotopeFormula.trim();
				}
			}
		}
		return null;
	}
	
	private Integer getCharge(Node node) {
		Set<Node> chargeNodes = new HashSet<> ();
		for (Relationship relationship : node.getRelationships(MetaboliteRelationshipType.HasCharge)) {
			chargeNodes.add(relationship.getOtherNode(node));
		}
		
		if (chargeNodes.size() > 1) System.out.println("More than one charge");
		
		for (Node chargeNode : chargeNodes) {
			if (chargeNode.hasLabel(MetabolitePropertyLabel.Charge)) {
				Integer charge = (Integer)chargeNode.getProperty("charge");
				if (charge != null) return charge;
			}
		}
		
		return null;
	}
	
	private boolean isMetaboliteNode(Node node) {
		if (node.hasLabel(CompoundNodeLabel.Compound) 
				&& node.hasProperty("proxy")
				&& !(boolean)node.getProperty("proxy")) {

			return true;
		} else {
			//whine if necessary
		}
		return false;
	}
	
	private boolean isMatch(String isotopeFormula, Integer charge, Node node) {
		Integer targetCharge = this.getCharge(node);
		String targetIsotopeFormula = this.getIsotopeFormula(node);
//		System.out.println(node.getLabels());
		if (charge.equals(targetCharge) && isotopeFormula.equals(targetIsotopeFormula)) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public Set<Long> execute() {
		Set<Long> result = new HashSet<> ();
		String myIsotopeFormula = this.getIsotopeFormula(initialNode);
//		String myFormula = this.getFormula(initialNode);
		Integer myCharge = this.getCharge(initialNode);
//		System.out.println(myIsotopeFormula);
//		System.out.println(myCharge);
		Set<Long> visited = new HashSet<> ();
		visited.add(this.initialNode.getId());
		
		for (Relationship relationship : this.initialNode
				.getRelationships(MetaboliteRelationshipType.HasCrossreferenceTo)) {
			Node xref = relationship.getOtherNode(initialNode);
			//Accept Xref if formula does not have isotope version
			visited.add(xref.getId());
			if (myIsotopeFormula == null) {
				if (isMetaboliteNode(xref)) result.add(xref.getId());
			} else {
				if (isMetaboliteNode(xref) && isMatch(myIsotopeFormula, myCharge, xref)) {
					result.add(xref.getId());
				} else {
					for (Relationship xrefRelationship : xref
							.getRelationships(MetaboliteRelationshipType.HasCrossreferenceTo)) {
						Node xref_ = xrefRelationship.getOtherNode(xref);
						if (!visited.contains(xref_.getId())) {
							if (isMatch(myIsotopeFormula, myCharge, xref_)) {
								result.add(xref_.getId());
							}
						}
					}
				}
			}
		}
		//Goto the xref:
		
		//Check if xref is my 'match'
		
		//Travel thourgh xref inchi and find other protonation states of the inchi for the correct match
		
		//accept all owners of the protonation state of the inchi
		result.add(this.initialNode.getId());
		return result;
	}

}
