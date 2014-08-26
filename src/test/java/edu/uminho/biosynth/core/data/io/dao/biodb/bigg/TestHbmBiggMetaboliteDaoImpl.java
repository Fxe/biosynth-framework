package edu.uminho.biosynth.core.data.io.dao.biodb.bigg;

import org.junit.Before;
import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.bigg.components.BiggMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.data.io.dao.biodb.AbstractTestHbmMetaboliteDaoTemplate;
import edu.uminho.biosynth.core.data.io.factory.BiggDaoFactory;
import pt.uminho.sysbio.data.test.mother.HbmBiggMetaboliteMother;
import pt.uminho.sysbio.data.test.rule.SessionFactoryRule;

public class TestHbmBiggMetaboliteDaoImpl
extends AbstractTestHbmMetaboliteDaoTemplate<BiggMetaboliteEntity> {

	private HbmBiggMetaboliteMother mother;
	
	@Before
	public void setUp() {
		this.metaboliteDao = new BiggDaoFactory()
			.withSessionFactory(this.getSessionFactory())
			.buildHbmBiggMetaboliteDao();
		
		this.mother = new HbmBiggMetaboliteMother(this.getSessionFactory().getCurrentSession());
		this.hbmObjectMother = mother;
		
		this.entry = mother.getEntry();
		this.id = mother.getId();
	}
	
	public TestHbmBiggMetaboliteDaoImpl() {
		super(new SessionFactoryRule(
				BiggMetaboliteEntity.class, 
				BiggMetaboliteCrossReferenceEntity.class));
	}
}
