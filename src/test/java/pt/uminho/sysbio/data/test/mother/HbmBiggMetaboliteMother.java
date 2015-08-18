package pt.uminho.sysbio.data.test.mother;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteEntity;

public class HbmBiggMetaboliteMother extends AbstractHbmObjectMother<BiggMetaboliteEntity> {

	private Long id = 123456L;
	private String entry = "a2b";
	private String name = "A BiGG Metabolite";
	private Integer charge = 0;
	private List<String> compartments = new ArrayList<> ();
	private String formula = "A2B1C3";
	private String description = "abcdef 123456 !<>%&/\\() .;,-_";
	private String metaboliteClass = "Foo";
	private String source = "MotherPattern";
	private List<BiggMetaboliteCrossreferenceEntity> crossReferenceEntities = new ArrayList<> ();
	
	public HbmBiggMetaboliteMother(Session session) {
		super(session);
		
		//Generate a crossreference instance
		this.crossReferenceEntities.add(
				new HbmBiggMetaboliteCrossReferenceMother(session)
				.instance());
	}

	@Override
	protected BiggMetaboliteEntity loadInstance(Session session) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected BiggMetaboliteEntity createInstance() {
		BiggMetaboliteEntity biggMetaboliteEntity = 
				new BiggMetaboliteEntity();
		biggMetaboliteEntity.setId(this.id);
		biggMetaboliteEntity.setEntry(this.entry);
		return biggMetaboliteEntity;
	}

	@Override
	protected void configure(BiggMetaboliteEntity cpd) {
		cpd.setCharge(this.charge);
		cpd.setCompartments(this.compartments);
		cpd.setCrossReferences(this.crossReferenceEntities);
		cpd.setFormula(this.formula);
//		biggMetaboliteEntity.setEnzymeIdSet(null);
		cpd.setDescription(this.description);
		cpd.setMetaboliteClass(this.metaboliteClass);
		cpd.setSource(this.source);
		cpd.setName(this.name);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}

	public Integer getCharge() {
		return charge;
	}

	public void setCharge(Integer charge) {
		this.charge = charge;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	
}
