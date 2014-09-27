package edu.uminho.biosynth.core.components.biodb.factory;

import java.util.ArrayList;
import java.util.List;

import edu.uminho.biosynth.core.components.Orientation;
import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycReactionEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycReactionCrossReferenceEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycReactionLeftEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycReactionRightEntity;

public class BiocycReactionFactory {

	private final String frameId;
	private final String entry;
	private final String pgdb;
	private Long id = null;
	private String name = null;
	private String description = null;
	private Double gibbs = null;
	private Orientation orientation = null;
	private List<BioCycReactionLeftEntity> left = new ArrayList<> ();
	private List<BioCycReactionRightEntity> right = new ArrayList<> ();
	private List<BioCycReactionCrossReferenceEntity> crossreferences = new ArrayList<> ();
	
	public BiocycReactionFactory(String pgdb, String frameId) {
		this.frameId = frameId;
		this.pgdb = pgdb;
		this.entry = String.format("%s:%s", pgdb, frameId);
	}
	
	public BiocycReactionFactory withId(Long id) {
		this.id = id;
		return this;
	}
	
	public BiocycReactionFactory withName(String name) {
		this.name = name;
		return this;
	}
	
	public BiocycReactionFactory withDescription(String description) {
		this.description = description;
		return this;
	}
	
	public BiocycReactionFactory withGibbs(Double gibbs) {
		this.gibbs = gibbs;
		return this;
	}
	
	public BiocycReactionFactory withOrientation(Orientation orientation) {
		this.orientation = orientation;
		return this;
	}
	
	public BiocycReactionFactory withLeftEntity(BioCycReactionLeftEntity biocycReactionLeftEntity) {
		this.left.add(biocycReactionLeftEntity);
		return this;
	}
	
	public BiocycReactionFactory withRightEntity(BioCycReactionRightEntity biocycReactionRightEntity) {
		this.right.add(biocycReactionRightEntity);
		return this;
	}
	
	public BiocycReactionFactory withCrossreference(BioCycReactionCrossReferenceEntity crossreference) {
		this.crossreferences.add(crossreference);
		return this;
	}
	
	public BioCycReactionEntity build() {
		BioCycReactionEntity entity = new BioCycReactionEntity();
		
		entity.setId(this.id);
		entity.setEntry(this.entry);
		entity.setSource(this.pgdb);
		entity.setFrameId(this.frameId);
		entity.setName(this.name);
		entity.setDescription(this.description);
		entity.setOrientation(this.orientation);
		entity.setGibbs(this.gibbs);
		entity.setLeft(this.left);
		entity.setRight(this.right);
		entity.setCrossreferences(crossreferences);
		for (BioCycReactionLeftEntity l : this.left) l.setBioCycReactionEntity(entity);
		for (BioCycReactionRightEntity r : this.right) r.setBioCycReactionEntity(entity);
		for (BioCycReactionCrossReferenceEntity x : this.crossreferences) x.setBioCycReactionEntity(entity);
		
		
		return entity;
	}
}
