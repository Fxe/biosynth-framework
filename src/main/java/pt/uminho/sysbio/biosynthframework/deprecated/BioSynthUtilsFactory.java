package pt.uminho.sysbio.biosynthframework.deprecated;


@Deprecated
public class BioSynthUtilsFactory {
	
//	public static KeggCompoundMetaboliteEntity buildKegg(
//			String entry, String name, String description,
//			String formula,
//			String comment, String remark,
//			double mass, double molWeight,
//			String[] xrefs) {
//		
//		KeggCompoundMetaboliteEntity cpdKegg = new KeggCompoundMetaboliteEntity();
//		cpdKegg.setEntry(entry);
//		cpdKegg.setName(name);
//		cpdKegg.setFormula(formula);
//		cpdKegg.setComment(comment);
//		cpdKegg.setRemark(remark);
//		cpdKegg.setDescription(description);
//		cpdKegg.setMass(mass);
//		cpdKegg.setMolWeight(molWeight);
//		
//		
//		
//		for (int i = 0; i < xrefs.length; i+=2) {
//			if (i + 1 < xrefs.length) {
//				KeggCompoundMetaboliteCrossreferenceEntity xref = new KeggCompoundMetaboliteCrossreferenceEntity();
//				xref.setType(GenericCrossReference.Type.DATABASE);
//				xref.setRef(xrefs[i]);
//				xref.setValue(xrefs[i+1]);
//				cpdKegg.addCrossReference(xref);
//			}
//		}
//		return cpdKegg;
//	}
	
//	public static BiggMetaboliteEntity buildBigg(
//			String entry, String name, String description,
//			String formula,
//
//			String[] xrefs) {
//		
//		BiggMetaboliteEntity cpd = new BiggMetaboliteEntity();
//		cpd.setEntry(entry);
//		cpd.setName(name);
//		cpd.setFormula(formula);
//		cpd.setCharge(0);
//		cpd.setDescription(description);
//		
//		
//		for (int i = 0; i < xrefs.length; i+=2) {
//			if (i + 1 < xrefs.length) {
//				BiggMetaboliteCrossreferenceEntity xref = new BiggMetaboliteCrossreferenceEntity();
//				xref.setType(GenericCrossReference.Type.DATABASE);
//				xref.setRef(xrefs[i]);
//				xref.setValue(xrefs[i+1]);
//				cpd.addCrossReference(xref);
//			}
//		}
//		
//		return cpd;
//	}
//	
//	public static BioCycMetaboliteEntity buildBiocyc(
//			String entry, String name, String description,
//			String formula,
//			String comment,
//			int charge, double cmlMolWeight, double molWeight, double gibbs,
//			String[] xrefs) {
//		
//		BioCycMetaboliteEntity cpdBiocyc = new BioCycMetaboliteEntity();
//		cpdBiocyc.setEntry(entry);
//		cpdBiocyc.setName(name);
//		cpdBiocyc.setFormula(formula);
//		cpdBiocyc.setComment(comment);
//		cpdBiocyc.setCharge(0);
//		cpdBiocyc.setGibbs(gibbs);
//		cpdBiocyc.setDescription(description);
//		cpdBiocyc.setCmlMolWeight(cmlMolWeight);
//		cpdBiocyc.setMolWeight(molWeight);
//		
//		
//		
//		for (int i = 0; i < xrefs.length; i+=4) {
//			if (i + 3 < xrefs.length) {
//				BioCycMetaboliteCrossreferenceEntity xref = new BioCycMetaboliteCrossreferenceEntity();
//				xref.setType(GenericCrossReference.Type.DATABASE);
//				xref.setRef(xrefs[i]);
//				xref.setValue(xrefs[i+1]);
//				xref.setRelationship(xrefs[i+2]);
//				xref.setUrl(xrefs[i+3]);
//				cpdBiocyc.addCrossReference(xref);
//			}
//		}
//		return cpdBiocyc;
//	}
}
