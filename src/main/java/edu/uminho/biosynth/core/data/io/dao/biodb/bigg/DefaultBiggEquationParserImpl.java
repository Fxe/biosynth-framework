package edu.uminho.biosynth.core.data.io.dao.biodb.bigg;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uminho.biosynth.core.components.Orientation;
import edu.uminho.biosynth.core.components.biodb.bigg.components.BiggReactionLeftEntity;
import edu.uminho.biosynth.core.components.biodb.bigg.components.BiggReactionRightEntity;

public class DefaultBiggEquationParserImpl implements BiggEquationParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultBiggEquationParserImpl.class);
	
	private String equation;
	private boolean translocation = false;
	private Orientation orientation;
	private List<BiggReactionLeftEntity> left = new ArrayList<> ();
	private List<BiggReactionRightEntity> right = new ArrayList<> ();
	
	public DefaultBiggEquationParserImpl() {}
	
	public DefaultBiggEquationParserImpl(String equation) {
		this.equation = equation;
	}
	
	public String getEquation() { return equation;}
	public void setEquation(String equation) { this.equation = equation;}
	
	public boolean isTranslocation() { return translocation;}
	
	public Orientation getOrientation() { return orientation;}
	
	@Override
	public List<BiggReactionLeftEntity> getLeft() { return this.left;}

	@Override
	public List<BiggReactionRightEntity> getRight() { return this.right;}

	@Override
	public void parse() {
		this.left = new ArrayList<> ();
		this.right = new ArrayList<> ();
		
		if (this.equation == null || this.equation.trim().isEmpty()) {
			LOGGER.warn("Empty equation");
			return;
		}
		
		
		String[] split = equation.split(" : ");
		String eq = split.length > 1 ? split[1] : split[0];
		String compartment = split.length > 1 ? split[0].trim() : null;
		split = eq.split(">");
		String left = split[0];
		String right = split.length > 1 ? split[1].trim() : "";
		String operator = left.substring(left.length() - 3).trim().concat(">");
		left = left.substring(0, left.length() - 3).trim();
		
		LOGGER.trace(String.format("Operator [%s]", operator));
		
		switch (operator) {
			case "-->": 
				this.orientation = Orientation.LeftToRight;
				break;
			case "<==>":
				this.orientation = Orientation.Reversible;
				break;
			default:
				this.orientation = Orientation.Unknown;
				break;
		}
		
		List<String[]> leftTriples = this.getElements(left.split("\\+"));
		List<String[]> rigthTriples = this.getElements(right.split("\\+"));
		for (String[] triple : leftTriples) {
			BiggReactionLeftEntity l = new BiggReactionLeftEntity();
			l.setValue(Double.parseDouble(triple[0]));
			l.setCpdEntry(triple[1]);
			l.setCompartment(compartment == null ? triple[2] : compartment);
			this.left.add(l);
		}
		for (String[] triple : rigthTriples) {
			BiggReactionRightEntity r = new BiggReactionRightEntity();
			r.setValue(Double.parseDouble(triple[0]));
			r.setCpdEntry(triple[1]);
			r.setCompartment(compartment == null ? triple[2] : compartment);
			this.right.add(r);
		}
		
//		System.out.println(this.left);
//		System.out.println(this.right);
	}
	
	private List<String[]> getElements(String[] elements) {
		List<String[]> stoichiometricTriples = new ArrayList<> ();
		
		for (String element : elements) {
			if (!(element == null || element.trim().isEmpty())) {
				LOGGER.trace(String.format("Building Triple [%s]", element));
				String[] triple = new String[3];
				element = element.trim();
				
				String[] pair;
				//Only search for stoichiometry value if element starts with '('xxxx) ABC
				if (element.startsWith("(")) {
					pair = element.split("\\)");
					triple[0] = pair.length > 1 ? pair[0].replaceAll("[\\(\\)]", "") : "1";
				} else {
					pair = new String[1];
					pair[0] = element;
					triple[0] = "1";
				}
				
//				System.out.println(pair[pair.length - 1]);
				
				pair = pair[pair.length - 1].split("\\[");
				
				triple[1] = pair[0].trim();
				if (pair.length > 1) triple[2] = "[".concat(pair[1].trim());
				
				LOGGER.trace(String.format("Triple: [%s, %s, %s]", triple[0], triple[1], triple[2]));
				stoichiometricTriples.add(triple);
			}
		}
		
		return stoichiometricTriples;
	}


	
}
