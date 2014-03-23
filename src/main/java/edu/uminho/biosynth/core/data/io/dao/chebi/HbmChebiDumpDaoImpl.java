package edu.uminho.biosynth.core.data.io.dao.chebi;

import java.io.Serializable;
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

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.biodb.chebi.ChebiDumpMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.chebi.ChebiMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.chebi.components.ChebiDumpMetaboliteChemicalDataEntity;
import edu.uminho.biosynth.core.components.biodb.chebi.components.ChebiDumpMetaboliteDatabaseAccession;
import edu.uminho.biosynth.core.components.biodb.chebi.components.ChebiDumpMetaboliteNameEntity;
import edu.uminho.biosynth.core.components.biodb.chebi.components.ChebiDumpMetaboliteReferenceEntity;
import edu.uminho.biosynth.core.components.biodb.chebi.components.ChebiDumpMetaboliteStructuresEntity;
import edu.uminho.biosynth.core.components.biodb.chebi.components.ChebiMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.components.biodb.chebi.components.ChebiMetaboliteNameEntity;
import edu.uminho.biosynth.core.data.io.dao.IMetaboliteDao;

@Repository
public class HbmChebiDumpDaoImpl implements IMetaboliteDao<ChebiMetaboliteEntity> {

	private static Logger LOGGER = Logger.getLogger(HbmChebiDumpDaoImpl.class);
	
	private SessionFactory sessionFactory;
	
	private static final Set<String> validDbEntries = new HashSet<> (Arrays.asList(new String[]{
			"kegg compound accession", "cas registry number", "lipid maps instance accession", 
			"kegg drug accession", "kegg glycan accession", "metacyc accession", "hmdb accession",
			"pubchem accession", "chemspider accession", "drugbank accession"}));
	
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}


	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}


	@Override
	public ChebiMetaboliteEntity find(Serializable id) {
		ChebiDumpMetaboliteEntity cpd = 
				ChebiDumpMetaboliteEntity.class.cast(this.getSession().get(ChebiDumpMetaboliteEntity.class, id));
		return this.dumpToChebi(cpd);
	}

	
	@Override
	public List<ChebiMetaboliteEntity> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable save(ChebiMetaboliteEntity cpd) {
		this.getSession().save(chebiToDump(cpd));
		return null;
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
			ChebiMetaboliteCrossReferenceEntity xref = new ChebiMetaboliteCrossReferenceEntity();
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
			ChebiMetaboliteCrossReferenceEntity xref = new ChebiMetaboliteCrossReferenceEntity();
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
	public ChebiMetaboliteEntity getMetaboliteInformation(Serializable id) {
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
	public ChebiMetaboliteEntity saveMetaboliteInformation(
			ChebiMetaboliteEntity metabolite) {
		throw new RuntimeException("Unsupported Operation");
	}


	@Override
	public Serializable save(Object entity) {
		throw new RuntimeException("Unsupported Operation");
	}
}
