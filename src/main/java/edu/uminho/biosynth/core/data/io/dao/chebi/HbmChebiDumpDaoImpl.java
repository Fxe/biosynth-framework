package edu.uminho.biosynth.core.data.io.dao.chebi;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import edu.uminho.biosynth.core.components.biodb.chebi.ChebiDumpMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.chebi.ChebiMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.chebi.components.ChebiDumpMetaboliteChemicalDataEntity;
import edu.uminho.biosynth.core.components.biodb.chebi.components.ChebiDumpMetaboliteNameEntity;
import edu.uminho.biosynth.core.components.biodb.chebi.components.ChebiDumpMetaboliteStructuresEntity;
import edu.uminho.biosynth.core.components.biodb.chebi.components.ChebiMetaboliteNameEntity;
import edu.uminho.biosynth.core.data.io.dao.IMetaboliteDao;

public class HbmChebiDumpDaoImpl implements IMetaboliteDao<ChebiMetaboliteEntity> {

	private SessionFactory sessionFactory;
	
	
	
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
		ChebiMetaboliteEntity res = new ChebiMetaboliteEntity();
		res.setId(cpd.getId());
		res.setName(cpd.getName());
		res.setEntry(cpd.getChebiAccession());
		res.setCreatedBy(cpd.getCreatedBy());
		res.setStars(cpd.getStar());
		res.setSource(cpd.getSource());
		
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
		return res;
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
		// TODO Auto-generated method stub
		return null;
	}
}
