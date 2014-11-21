package pt.uminho.sysbio.biosynthframework.biodb.factory;

import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycReactionLeftEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycReactionRightEntity;

public class BiocycReactionStoichiometryFactory {

	private String coefficient = "1.0";
	private Double stoichiometry = 1.0d;
	private final String entry;
//	private Long cpdId;
	private Long id;
	
	public BiocycReactionStoichiometryFactory(String entry) {
		this.entry = entry;
	}
	
	public BiocycReactionStoichiometryFactory withCoefficient(String coefficient) {
		this.coefficient = coefficient;
		return this;
	}
	
	public BioCycReactionLeftEntity buildLeft() {
		BioCycReactionLeftEntity biocycReactionLeftEntity = new BioCycReactionLeftEntity();
		biocycReactionLeftEntity.setCoefficient(coefficient);
		biocycReactionLeftEntity.setCpdEntry(entry);
//		biocycReactionLeftEntity.setCpdKey(cpdId);
		biocycReactionLeftEntity.setId(id);
		biocycReactionLeftEntity.setStoichiometry(stoichiometry);
		return biocycReactionLeftEntity;
	}
	
	public BioCycReactionRightEntity buildRight() {
		BioCycReactionRightEntity biocycReactionRightEntity = new BioCycReactionRightEntity();
		biocycReactionRightEntity.setCoefficient(coefficient);
		biocycReactionRightEntity.setCpdEntry(entry);
//		biocycReactionRightEntity.setCpdKey(cpdId);
		biocycReactionRightEntity.setId(id);
		biocycReactionRightEntity.setStoichiometry(stoichiometry);
		
		return biocycReactionRightEntity;
	}
}
