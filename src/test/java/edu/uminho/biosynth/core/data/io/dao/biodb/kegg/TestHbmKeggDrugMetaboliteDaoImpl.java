package edu.uminho.biosynth.core.data.io.dao.biodb.kegg;

import org.junit.Before;

import pt.uminho.sysbio.data.test.mother.HbmKeggDrugMetaboliteMother;
import pt.uminho.sysbio.data.test.rule.SessionFactoryRule;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggDrugMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.components.KeegDrugMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.data.io.dao.biodb.AbstractTestHbmMetaboliteDaoTemplate;
import edu.uminho.biosynth.core.data.io.factory.KeggDaoFactory;

public class TestHbmKeggDrugMetaboliteDaoImpl
extends AbstractTestHbmMetaboliteDaoTemplate<KeggDrugMetaboliteEntity>{

	private HbmKeggDrugMetaboliteMother hbmKeggDrugMetaboliteMother;
	
	@Before
	public void setUp() throws Exception {
		this.metaboliteDao = new KeggDaoFactory()
			.withSessionFactory(this.getSessionFactory())
			.buildHbmKeggDrugMetaboliteDao();
		
		hbmKeggDrugMetaboliteMother = new HbmKeggDrugMetaboliteMother(
				this.getSessionFactory().getCurrentSession());
		this.hbmObjectMother = hbmKeggDrugMetaboliteMother;
		
		this.entry = this.hbmKeggDrugMetaboliteMother.getEntry();
	}
	
	public TestHbmKeggDrugMetaboliteDaoImpl() {
		super(new SessionFactoryRule(
				KeggDrugMetaboliteEntity.class,
				KeegDrugMetaboliteCrossreferenceEntity.class));
	}
}
