package edu.uminho.biosynth.core.components;

import java.io.Serializable;

public class GenericOrganism extends AbstractGenericEntity implements Serializable {

	private static final long serialVersionUID = 45454568017L;
	
	private int numberProteinsGenes;
	private int numberRnaGenes;
	
	/*
	 * More attributes could be added
	 * Organism Entity not used by any source
	 */
	
	public GenericOrganism(String id) {
		super(id);
		this.id = 0;
		this.name = "unnamed";
		this.numberProteinsGenes = 0;
		this.numberRnaGenes = 0;
	}
	
	public GenericOrganism(String id, int key) {
		super(id);
		this.id = key;
		this.name = "unnamed";
		this.numberProteinsGenes = 0;
		this.numberRnaGenes = 0;
	}

	public int getNumberProteinsGenes() {
		return numberProteinsGenes;
	}
	public void setNumberProteinsGenes(int numberProteinsGenes) {
		this.numberProteinsGenes = numberProteinsGenes;
	}

	public int getNumberRnaGenes() {
		return numberRnaGenes;
	}
	public void setNumberRnaGenes(int numberRnaGenes) {
		this.numberRnaGenes = numberRnaGenes;
	}
}
