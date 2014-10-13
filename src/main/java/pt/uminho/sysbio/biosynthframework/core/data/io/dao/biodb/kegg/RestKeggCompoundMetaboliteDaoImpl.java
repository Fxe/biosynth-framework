package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.KeggCompoundFlatFileParser;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

@Repository
public class RestKeggCompoundMetaboliteDaoImpl 
extends AbstractRestfulKeggDao implements MetaboliteDao<KeggCompoundMetaboliteEntity> {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestKeggCompoundMetaboliteDaoImpl.class);
	
	private static final String restCpdQuery = "http://rest.kegg.jp/get/cpd:%s";
	private static final String restCpdMolQuery = "http://rest.kegg.jp/get/cpd:%s/mol";
	
	@Override
	public KeggCompoundMetaboliteEntity getMetaboliteById(Serializable id) {
		String restCpdQuery = String.format(RestKeggCompoundMetaboliteDaoImpl.restCpdQuery, id);
		String restCpdMolQuery = String.format(RestKeggCompoundMetaboliteDaoImpl.restCpdMolQuery, id);
		
		String localPath = this.getLocalStorage() + "cpd" + "/" + id;
		
		String cpdFlatFile = null;
		String cpdMolFile = null;
		try {
			cpdFlatFile = getLocalOrWeb(restCpdQuery, localPath + ".txt");
			if (cpdFlatFile == null) return null;
			
			cpdMolFile = getLocalOrWeb(restCpdMolQuery, localPath + ".mol");
			
//			System.out.println(drFlatFile);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
		KeggCompoundFlatFileParser parser = new KeggCompoundFlatFileParser(cpdFlatFile);
//		System.out.println(parser.getTabs());
		KeggCompoundMetaboliteEntity cpd = new KeggCompoundMetaboliteEntity();
		
		cpd.setEntry(parser.getEntry());
		cpd.setName(parser.getName());
		cpd.setFormula(parser.getFormula());
		cpd.setMass(parser.getMass());
		cpd.setRemark(parser.getRemark());
		cpd.setComment(parser.getComment());
		if (cpdMolFile != null && !cpdMolFile.isEmpty()) {
			cpd.setMol2d(cpdMolFile);
		}
		cpd.setCrossReferences(parser.getCrossReferences());
		cpd.setReactions(parser.getReactions());
		cpd.setEnzymes(parser.getEnzymes());
		cpd.setPathways(parser.getPathways());
		return cpd;
	}
	
	public String getMetaboliteFlatFile(Serializable id) {
		String restCpdQuery = String.format(RestKeggCompoundMetaboliteDaoImpl.restCpdQuery, id);
		String localPath = this.getLocalStorage()  + "cpd" + "/" + id;
		
		String drFlatFile = null;
		try {
			drFlatFile = getLocalOrWeb(restCpdQuery, localPath + ".txt");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
		return drFlatFile;
	}

	@Override
	public KeggCompoundMetaboliteEntity saveMetabolite(
			KeggCompoundMetaboliteEntity metabolite) {
		throw new RuntimeException("Unsupported Operation");
	}

	@Override
	public List<Serializable> getAllMetaboliteIds() {
		List<Serializable> cpdIds = new ArrayList<>();
		String restListDrQuery = String.format("http://rest.kegg.jp/%s/%s", "list", "cpd");
		String localPath = this.getLocalStorage() + "query" + "/compound.txt";
		try {
			String httpResponseString = getLocalOrWeb(restListDrQuery, localPath);
			String[] httpResponseLine = httpResponseString.split("\n");
			for ( int i = 0; i < httpResponseLine.length; i++) {
				String[] values = httpResponseLine[i].split("\\t");
				cpdIds.add(values[0].substring(4));
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return cpdIds;
	}

	@Override
	public Serializable save(KeggCompoundMetaboliteEntity entity) {
		throw new RuntimeException("Unsupported Operation");
	}
//}
//	
//	private static final String restDrQuery = "http://rest.kegg.jp/get/dr:%s";
//	private static final String restDrMolQuery = "http://rest.kegg.jp/get/dr:%s/mol";

	@Override
	public Serializable saveMetabolite(Object entity) {
		throw new RuntimeException("Unsupported Operation");
	}

	@Override
	public KeggCompoundMetaboliteEntity getMetaboliteByEntry(String entry) {
		return this.getMetaboliteById(entry);
	}

	@Override
	public List<String> getAllMetaboliteEntries() {
		List<String> cpdIds = new ArrayList<>();
		String restListDrQuery = String.format("http://rest.kegg.jp/%s/%s", "list", "cpd");
		String localPath = this.getLocalStorage() + "query" + "/compound.txt";
		LOGGER.debug("LocalPath: " + localPath);
		try {
			String httpResponseString = getLocalOrWeb(restListDrQuery, localPath);
			String[] httpResponseLine = httpResponseString.split("\n");
			for ( int i = 0; i < httpResponseLine.length; i++) {
				String[] values = httpResponseLine[i].split("\\t");
				cpdIds.add(values[0].substring(4));
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return cpdIds;
	}


}
