package pt.uminho.sysbio.data.test.mother;

import org.hibernate.Session;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.biodb.bigg.components.BiggMetaboliteCrossReferenceEntity;

public class HbmBiggMetaboliteCrossReferenceMother extends AbstractHbmObjectMother<BiggMetaboliteCrossReferenceEntity> {

	private GenericCrossReference.Type type = GenericCrossReference.Type.DATABASE;
	private String referenceDatabase 	= "BarBase";
	private String referenceValue 		= "BAZ0001";
	
	public HbmBiggMetaboliteCrossReferenceMother(Session session) {
		super(session);
	}

	@Override
	protected BiggMetaboliteCrossReferenceEntity loadInstance(Session session) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected BiggMetaboliteCrossReferenceEntity createInstance() {
		BiggMetaboliteCrossReferenceEntity crossReference = 
				new BiggMetaboliteCrossReferenceEntity();
		return crossReference;
	}

	@Override
	protected void configure(BiggMetaboliteCrossReferenceEntity crossReference) {
		crossReference.setType(this.type);
		crossReference.setRef(this.referenceDatabase);
		crossReference.setValue(this.referenceValue);
	}

}
