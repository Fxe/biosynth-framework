package pt.uminho.sysbio.data.test.mother;

import org.hibernate.Session;

import pt.uminho.sysbio.biosynthframework.GenericCrossReference;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteCrossreferenceEntity;

public class HbmBiggMetaboliteCrossReferenceMother extends AbstractHbmObjectMother<BiggMetaboliteCrossreferenceEntity> {

	private GenericCrossReference.Type type = GenericCrossReference.Type.DATABASE;
	private String referenceDatabase 	= "BarBase";
	private String referenceValue 		= "BAZ0001";
	
	public HbmBiggMetaboliteCrossReferenceMother(Session session) {
		super(session);
	}

	@Override
	protected BiggMetaboliteCrossreferenceEntity loadInstance(Session session) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected BiggMetaboliteCrossreferenceEntity createInstance() {
		BiggMetaboliteCrossreferenceEntity crossReference = 
				new BiggMetaboliteCrossreferenceEntity();
		return crossReference;
	}

	@Override
	protected void configure(BiggMetaboliteCrossreferenceEntity crossReference) {
		crossReference.setType(this.type);
		crossReference.setRef(this.referenceDatabase);
		crossReference.setValue(this.referenceValue);
	}

}
