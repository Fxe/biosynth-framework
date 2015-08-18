package pt.uminho.sysbio.biosynthframework.biodb.factory;

import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycReactionCrossReferenceEntity;

public class BiocycReactionCrossreferenceFactory {

	private final String tag;
	private final String entry;
	private Long id;
	private ReferenceType type = ReferenceType.DATABASE;
	private String url;
	private String relationship;
	
	public BiocycReactionCrossreferenceFactory(String tag, String entry) {
		this.tag = tag;
		this.entry = entry;
	}
	
	public BiocycReactionCrossreferenceFactory withId(Long id) {
		this.id = id;
		return this;
	}
	
	public BiocycReactionCrossreferenceFactory withUrl(String url) {
		this.url = url;
		return this;
	}
	
	public BiocycReactionCrossreferenceFactory withRelationship(String relationship) {
		this.relationship = relationship;
		return this;
	}
	
	public BioCycReactionCrossReferenceEntity build() {
		BioCycReactionCrossReferenceEntity entity = new BioCycReactionCrossReferenceEntity();
		entity.setId(id);
		entity.setRef(tag);
		entity.setRelationship(relationship);
		entity.setType(type);
		entity.setUrl(url);
		entity.setValue(entry);
		
		return entity;
	}
}
