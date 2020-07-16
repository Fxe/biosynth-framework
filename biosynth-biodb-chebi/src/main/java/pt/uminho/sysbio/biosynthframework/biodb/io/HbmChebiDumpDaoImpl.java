package pt.uminho.sysbio.biosynthframework.biodb.io;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.biodb.ChebiDumpMetaboliteChemicalDataEntity;
import pt.uminho.sysbio.biosynthframework.biodb.ChebiDumpMetaboliteCommentEntity;
import pt.uminho.sysbio.biosynthframework.biodb.ChebiDumpMetaboliteDatabaseAccession;
import pt.uminho.sysbio.biosynthframework.biodb.ChebiDumpMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.ChebiDumpMetaboliteNameEntity;
import pt.uminho.sysbio.biosynthframework.biodb.ChebiDumpMetaboliteReferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.ChebiDumpMetaboliteStructuresEntity;
import pt.uminho.sysbio.biosynthframework.biodb.chebi.ChebiMetaboliteComment;
import pt.uminho.sysbio.biosynthframework.biodb.chebi.ChebiMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.chebi.ChebiMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.chebi.ChebiMetaboliteNameEntity;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

@Repository
public class HbmChebiDumpDaoImpl implements MetaboliteDao<ChebiMetaboliteEntity> {

	private static Logger LOGGER = LoggerFactory.getLogger(HbmChebiDumpDaoImpl.class);
	
	private boolean setParentAsCrossreference = false;
	
	private SessionFactory sessionFactory;
	
	private static final Set<String> validDbEntries = new HashSet<> (Arrays.asList(new String[]{
			"kegg compound accession", "cas registry number", "lipid maps instance accession", 
			"kegg drug accession", "kegg glycan accession", "metacyc accession", "hmdb accession",
			"pubchem accession", "chemspider accession", "drugbank accession", "ymdb accession", "wikipedia accession",
			"knapsack accession"}));
	
	public SessionFactory getSessionFactory() { return sessionFactory; }
	public void setSessionFactory(SessionFactory sessionFactory) { this.sessionFactory = sessionFactory;}

	
	
	public boolean isSetParentAsCrossreference() {
		return setParentAsCrossreference;
	}
	public void setSetParentAsCrossreference(boolean setParentAsCrossreference) {
		this.setParentAsCrossreference = setParentAsCrossreference;
	}
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
			
			LOGGER.debug("Added ChebiMetaboliteNameEntity: " + resName);
		}
		
		for (ChebiDumpMetaboliteCommentEntity comment : cpd.getComments()) {
			ChebiMetaboliteComment comment_ = new ChebiMetaboliteComment();
			comment_.setComment(comment.getText());
			comment_.setDataType(comment.getDataType());
			comment_.setChebiMetaboliteEntity(res);
			res.getComments().add(comment_);
			LOGGER.debug("Added ChebiMetaboliteComment: " + comment_);
		}
		
		for (ChebiDumpMetaboliteChemicalDataEntity chemData: cpd.getChemicalData()) {
			switch (chemData.getType().toLowerCase()) {
				case "charge":
					LOGGER.debug("Charge: " + chemData.getChemicalData());
					res.setCharge(Integer.parseInt(chemData.getChemicalData()));
					break;
				case "formula":
					LOGGER.debug("Formula: " + chemData.getChemicalData());
					res.setFormula(chemData.getChemicalData());
					break;
				case "mass":
					LOGGER.debug("Mass: " + chemData.getChemicalData());
					res.setMass(Double.parseDouble(chemData.getChemicalData()));
					break;
				default:
					throw new RuntimeException("Unsupported Type: " + chemData);
			}
		}
		
		for (ChebiDumpMetaboliteStructuresEntity structure: cpd.getStructures()) {
			switch (structure.getType().toLowerCase()) {
				case "inchi":
					LOGGER.debug("InChI: " + structure.getStructure());
					res.setInchi(structure.getStructure());
					break;
				case "inchikey":
					LOGGER.debug("InChI Key: " + structure.getStructure());
					res.setInchiKey(structure.getStructure());
					break;
				case "smiles":
					LOGGER.debug("SMILES: " + structure.getStructure());
					res.setSmiles(structure.getStructure());
					break;
				case "mol":
					LOGGER.debug("Found Mol: " + structure.getDimension());
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
			ReferenceType type = referenceDbToType(reference.getReferenceDbName().toLowerCase());
			if ( !type.equals(ReferenceType.UNKNOWN)) {
				xref.setType(type);
				xref.setRef(reference.getReferenceDbName());
				xref.setValue(reference.getReferenceId());
				if (reference.getLocationInRef() != null) xref.setLocationInReference(reference.getLocationInRef().trim().isEmpty()?null:reference.getLocationInRef());
				if (reference.getReferenceName() != null) xref.setReferenceName(reference.getReferenceName().trim().isEmpty()?null:reference.getReferenceName());
				
				xref.setChebiMetaboliteEntity(res);
				
				res.getCrossreferences().add(xref);
			}

		}
		
		for (ChebiDumpMetaboliteDatabaseAccession reference: cpd.getAccessions()) {
			ChebiMetaboliteCrossreferenceEntity xref = new ChebiMetaboliteCrossreferenceEntity();
			ReferenceType type = referenceDbToType(reference.getType().toLowerCase());
			if ( !type.equals(ReferenceType.UNKNOWN)) {
				xref.setType(type);
				xref.setRef(reference.getType());
				xref.setValue(reference.getAccessionNumber());
				xref.setReferenceName(reference.getSource());
				
				xref.setChebiMetaboliteEntity(res);
				
				res.getCrossreferences().add(xref);
			}
		}
		
		
		if (isSetParentAsCrossreference()) {
			//this should be optional !
			// Generate the single internal cross reference to the parent
			if (cpd.getParentId() != null) {
				ChebiMetaboliteCrossreferenceEntity parentXref = new ChebiMetaboliteCrossreferenceEntity();
				parentXref.setType(ReferenceType.DATABASE);
				parentXref.setRef("chebi");
				parentXref.setValue(cpd.getParentId().toString());
				parentXref.setChebiMetaboliteEntity(res);
				res.getCrossreferences().add(parentXref);
			}
		}
		
		return res;
	}
	
	private ReferenceType referenceDbToType(String db) {		
		if (validDbEntries.contains(db)) return ReferenceType.DATABASE;
		
		ReferenceType type = null;
		switch (db) {
			case "pubmed citation":
				type = ReferenceType.CITATION;
				break;			
			case "pubMed central citation":
				type = ReferenceType.CITATION;
				break;
			case "citexplore citation":
				type = ReferenceType.CITATION;
				break;
			case "patent":
				type = ReferenceType.PATENT;
				break;
			case "patent accession":
				type = ReferenceType.PATENT;
				break;
			case "pubchem":
				type = ReferenceType.DATABASE;
				break;
			case "uniprot":
				type = ReferenceType.GENE;
				break;
			case "reactome":
				type = ReferenceType.REACTION;
			 	break;
			case "sabio-rk":
				type = ReferenceType.REACTION;
			 	break;
			case "rhea":
				type = ReferenceType.REACTION;
				break;
			case "brenda":
				type = ReferenceType.ECNUMBER;
				break;
			case "enzymeportal":
				type = ReferenceType.PROTEIN;
				break;
			default:
				LOGGER.warn("Unknown type: " + db);
				type = ReferenceType.UNKNOWN;
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
			CriteriaQuery<ChebiDumpMetaboliteEntity> query = 
			    this.getSession().getCriteriaBuilder().createQuery(ChebiDumpMetaboliteEntity.class);
//			query.from(ChebiDumpMetaboliteEntity.class).
//			query.getGroupRestriction(Restrictions.eq("chebiAccession", chebiAccession));
//			CriteriaBuilderImpl a = new CriteriaBuilderImpl(this.getSession());
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
		if (!entry.startsWith("CHEBI:")) {
			entry = "CHEBI:".concat(entry);
		}
		Criteria criteria = this.getSession().createCriteria(ChebiDumpMetaboliteEntity.class);
		ChebiDumpMetaboliteEntity dumpMetaboliteEntity = ChebiDumpMetaboliteEntity.class.cast(
				criteria.add(Restrictions.eq("chebiAccession", entry)).uniqueResult());
		
		ChebiMetaboliteEntity entity = this.dumpToChebi(dumpMetaboliteEntity);
		
		return entity;
	}


	@Override
	public List<String> getAllMetaboliteEntries() {
		Query query = this.getSession().createQuery("SELECT cpd.chebiAccession FROM ChebiDumpMetaboliteEntity cpd");
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
