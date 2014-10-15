package pt.uminho.sysbio.biosynthframework.biodb.io;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import pt.uminho.sysbio.biosynthframework.GenericCrossReference;
import pt.uminho.sysbio.biosynthframework.biodb.ChebiDumpMetaboliteChemicalDataEntity;
import pt.uminho.sysbio.biosynthframework.biodb.ChebiDumpMetaboliteDatabaseAccession;
import pt.uminho.sysbio.biosynthframework.biodb.ChebiDumpMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.ChebiDumpMetaboliteNameEntity;
import pt.uminho.sysbio.biosynthframework.biodb.ChebiDumpMetaboliteReferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.ChebiDumpMetaboliteStructuresEntity;
import pt.uminho.sysbio.biosynthframework.biodb.chebi.ChebiMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.chebi.ChebiMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.chebi.ChebiMetaboliteNameEntity;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

@Repository
public class HbmChebiDumpDaoImpl implements MetaboliteDao<ChebiMetaboliteEntity> {

	private static Logger LOGGER = Logger.getLogger(HbmChebiDumpDaoImpl.class);
	
	private SessionFactory sessionFactory;
	
	private static final Set<String> validDbEntries = new HashSet<> (Arrays.asList(new String[]{
			"kegg compound accession", "cas registry number", "lipid maps instance accession", 
			"kegg drug accession", "kegg glycan accession", "metacyc accession", "hmdb accession",
			"pubchem accession", "chemspider accession", "drugbank accession"}));
	
	public SessionFactory getSessionFactory() { return sessionFactory; }
	public void setSessionFactory(SessionFactory sessionFactory) { this.sessionFactory = sessionFactory;}

	@Override
	public Serializable save(ChebiMetaboliteEntity cpd) {
//		this.getSession().save(chebiToDump(cpd));
//		return null;
		throw new RuntimeException("Unsupported Operation");
	}
	
	private ChebiMetaboliteEntity dumpToChebi(ChebiDumpMetaboliteEntity cpd) {
		if (cpd == null) return null;
		
		ChebiMetaboliteEntity res = new ChebiMetaboliteEntity();
		res.setId(cpd.getId());
		res.setName(cpd.getName());
		res.setEntry(cpd.getChebiAccession().replace("CHEBI:", ""));
		res.setCreatedBy(cpd.getCreatedBy());
		res.setStars(cpd.getStar());
		res.setSource(cpd.getSource());
		res.setParentId(cpd.getParentId());
		
		for (ChebiDumpMetaboliteNameEntity name : cpd.getNames()) {
			ChebiMetaboliteNameEntity resName = new ChebiMetaboliteNameEntity();
			resName.setLanguage(name.getLanguage());
			resName.setName(name.getName());
			resName.setSource(name.getSource());
			resName.setType(name.getType());
			resName.setAdapted(name.getAdapted());
			resName.setChebiMetaboliteEntity(res);
			res.getNames().add(resName);
		}
		
		for (ChebiDumpMetaboliteChemicalDataEntity chemData: cpd.getChemicalData()) {
			switch (chemData.getType().toLowerCase()) {
				case "charge":
					res.setCharge(Integer.parseInt(chemData.getChemicalData()));
					break;
				case "formula":
					res.setFormula(chemData.getChemicalData());
					break;
				case "mass":
					res.setMass(Double.parseDouble(chemData.getChemicalData()));
					break;
				default:
					throw new RuntimeException("Unsupported Type: " + chemData);
			}
		}
		
		for (ChebiDumpMetaboliteStructuresEntity structure: cpd.getStructures()) {
			switch (structure.getType().toLowerCase()) {
				case "inchi":
					res.setInchi(structure.getStructure());
					break;
				case "inchikey":
					res.setInchiKey(structure.getStructure());
					break;
				case "smiles":
					res.setSmiles(structure.getStructure());
					break;
				case "mol":
					if (structure.getDimension().equals("3D")) {
						res.setMol3d(structure.getStructure());
					} else if (structure.getDimension().equals("2D")) {
						res.setMol2d(structure.getStructure());
					} else {
						throw new RuntimeException("Unsupported Type: " + structure);
					}
					break;
				default:
					throw new RuntimeException("Unsupported Type: " + structure);
			}
		}
		
		for (ChebiDumpMetaboliteReferenceEntity reference: cpd.getReferences()) {
//			System.out.println(reference);
			ChebiMetaboliteCrossreferenceEntity xref = new ChebiMetaboliteCrossreferenceEntity();
			GenericCrossReference.Type type = referenceDbToType(reference.getReferenceDbName().toLowerCase());
			if ( !type.equals(GenericCrossReference.Type.UNKNOWN)) {
				xref.setType(type);
				xref.setRef(reference.getReferenceDbName());
				xref.setValue(reference.getReferenceId());
				xref.setLocationInReference(reference.getLocationInRef().trim().isEmpty()?null:reference.getLocationInRef());
				xref.setReferenceName(reference.getReferenceName().trim().isEmpty()?null:reference.getReferenceName());
				
				xref.setChebiMetaboliteEntity(res);
				
				res.getCrossreferences().add(xref);
			}

		}
		
		for (ChebiDumpMetaboliteDatabaseAccession reference: cpd.getAccessions()) {
			ChebiMetaboliteCrossreferenceEntity xref = new ChebiMetaboliteCrossreferenceEntity();
			GenericCrossReference.Type type = referenceDbToType(reference.getType().toLowerCase());
			if ( !type.equals(GenericCrossReference.Type.UNKNOWN)) {
				xref.setType(type);
				xref.setRef(reference.getType());
				xref.setValue(reference.getAccessionNumber());
				xref.setReferenceName(reference.getSource());
				
				xref.setChebiMetaboliteEntity(res);
				
				res.getCrossreferences().add(xref);
			}
		}
		
		
		// Generate the single internal cross reference to the parent
		if (cpd.getParentId() != null) {
			ChebiMetaboliteCrossreferenceEntity parentXref = new ChebiMetaboliteCrossreferenceEntity();
			parentXref.setType(GenericCrossReference.Type.DATABASE);
			parentXref.setRef("chebi");
			parentXref.setValue(cpd.getParentId().toString());
			parentXref.setChebiMetaboliteEntity(res);
			res.getCrossreferences().add(parentXref);
		}
		
		return res;
	}
	
	private GenericCrossReference.Type referenceDbToType(String db) {		
		if (validDbEntries.contains(db)) return GenericCrossReference.Type.DATABASE;
		
		GenericCrossReference.Type type = null;
		switch (db) {
			case "pubmed citation":
				type = GenericCrossReference.Type.CITATION;
				break;			
			case "pubMed central citation":
				type = GenericCrossReference.Type.CITATION;
				break;
			case "citexplore citation":
				type = GenericCrossReference.Type.CITATION;
				break;
//			case "patent":
//				type = GenericCrossReference.Type.PATENT;
//				break;
			case "pubchem":
				type = GenericCrossReference.Type.DATABASE;
				break;
			case "uniprot":
				type = GenericCrossReference.Type.GENE;
				break;
			case "rhea":
				type = GenericCrossReference.Type.REACTION;
				break;
			case "brenda":
				type = GenericCrossReference.Type.ECNUMBER;
				break;
			default:
				type = GenericCrossReference.Type.UNKNOWN;
				break;
		}
		
		return type;
	}
	
	@SuppressWarnings("unused")
	private ChebiDumpMetaboliteEntity chebiToDump(ChebiMetaboliteEntity cpd) {
//		ChebiDumpMetaboliteEntity res = new ChebiDumpMetaboliteEntity();
		throw new RuntimeException("Unsupported Operation");
	}
	
	private Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}

	@Override
	public List<Serializable> getAllMetaboliteIds() {
		Query query = this.getSession().createQuery("SELECT cpd.id FROM ChebiDumpMetaboliteEntity cpd");
		@SuppressWarnings("unchecked")
		List<Serializable> res = query.list();
		return res;
	}


	@Override
	public ChebiMetaboliteEntity getMetaboliteById(Serializable id) {
		ChebiDumpMetaboliteEntity cpd = null;
		if (id instanceof String) {
			String chebiAccession = ((String)id).startsWith("CHEBI:")?(String)id:"CHEBI:".concat((String)id);
			Criteria criteria = this.getSession().createCriteria(ChebiDumpMetaboliteEntity.class);
			criteria.add(Restrictions.eq("chebiAccession", chebiAccession));
			List<?> res = criteria.list();
			for (Object o: res) {
				if (cpd != null) {
					LOGGER.warn(String.format("Multiple compounds found for %s", id));
				}
				cpd = (ChebiDumpMetaboliteEntity) o;
			}
		} else {
			cpd = ChebiDumpMetaboliteEntity.class.cast(this.getSession().get(ChebiDumpMetaboliteEntity.class, id));
		}
		
		return this.dumpToChebi(cpd);
	}


	@Override
	public ChebiMetaboliteEntity saveMetabolite(
			ChebiMetaboliteEntity metabolite) {
		throw new RuntimeException("Unsupported Operation");
	}


	@Override
	public Serializable saveMetabolite(Object entity) {
		throw new RuntimeException("Unsupported Operation");
	}


	@Override
	public ChebiMetaboliteEntity getMetaboliteByEntry(String entry) {
		throw new RuntimeException("Not implememted yet");
	}


	@Override
	public List<String> getAllMetaboliteEntries() {
		Query query = this.getSession().createQuery("SELECT cpd.accessionNumber FROM ChebiDumpMetaboliteEntity cpd");
		@SuppressWarnings("unchecked")
		List<String> accessions = query.list();
		Set<String> entries = new HashSet<> ();
		for (String accession:accessions) {
			if (!entries.add(accession.replace("CHEBI:", ""))) {
				LOGGER.warn(String.format("Duplicated Accession [%s] Found in the ChEBI dump library", accession));
			}
		}
		return new ArrayList<String> (entries);
	}
}
