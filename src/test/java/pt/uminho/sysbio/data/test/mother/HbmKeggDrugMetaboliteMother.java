package pt.uminho.sysbio.data.test.mother;

import org.hibernate.Session;

import edu.uminho.biosynth.core.components.biodb.kegg.KeggDrugMetaboliteEntity;

public class HbmKeggDrugMetaboliteMother extends AbstractHbmObjectMother<KeggDrugMetaboliteEntity> {

	private String entry = "D99999";
	private String name = "A KeGg Dr Metabolite";
//	private Integer charge = 0;
//	private List<String> compartments = new ArrayList<> ();
	private String formula = "A2B1C3";
	private String description = "abcdef 123456 !<>%&/\\() .;,-_";
	private String metaboliteClass = "Foo";
	private String source = "MotherPattern";
	private String component;
	private String activity;
	private String comment;
	
	public HbmKeggDrugMetaboliteMother(Session session) {
		super(session);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected KeggDrugMetaboliteEntity loadInstance(Session session) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected KeggDrugMetaboliteEntity createInstance() {
		KeggDrugMetaboliteEntity drugMetaboliteEntity =
				new KeggDrugMetaboliteEntity();
		drugMetaboliteEntity.setEntry(entry);
		return drugMetaboliteEntity;
	}

	@Override
	protected void configure(KeggDrugMetaboliteEntity cpd) {
		cpd.setName(name);
		cpd.setActivity(activity);
		cpd.setComment(comment);
		cpd.setComponent(component);
		cpd.setFormula(formula);
		cpd.setSource(source);
		cpd.setDescription(description);
		cpd.setMetaboliteClass(metaboliteClass);
	}

	public String getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}

	
}
