package edu.uminho.biosynth.core.data.integration.neo4j;

public class CentralDataReactionStoichiometryProperty extends GenericCentralEntityProperty{

	private double value;
	private String coefficient;
	
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public String getCoefficient() {
		return coefficient;
	}
	public void setCoefficient(String coefficient) {
		this.coefficient = coefficient;
	}
	
	
}
