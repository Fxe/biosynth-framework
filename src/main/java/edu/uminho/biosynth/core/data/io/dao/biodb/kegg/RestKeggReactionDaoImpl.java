package edu.uminho.biosynth.core.data.io.dao.biodb.kegg;

import java.io.IOException;
import java.io.Serializable;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.uminho.biosynth.core.components.biodb.kegg.KeggReactionEntity;
import edu.uminho.biosynth.core.data.io.dao.ReactionDao;
import edu.uminho.biosynth.core.data.io.dao.biodb.kegg.parser.KeggReactionFlatFileParser;

public class RestKeggReactionDaoImpl
extends AbstractRestfulKeggDao implements ReactionDao<KeggReactionEntity> {

	private static final Logger LOGGER = Logger.getLogger(RestKeggReactionDaoImpl.class);
	private static final String restRxnQuery = "http://rest.kegg.jp/get/rn:%s";
	
	@Override
	public KeggReactionEntity getReactionById(Serializable id) {
		throw new RuntimeException("Unsupported Operation.");
	}

	@Override
	public KeggReactionEntity getReactionByEntry(String entry) {
		String restRxnQuery = String.format(RestKeggReactionDaoImpl.restRxnQuery, entry);
		
		String localPath = this.getLocalStorage()  + "rn" + "/" + entry;
		KeggReactionEntity rxn = new KeggReactionEntity();
		
		String rnFlatFile = null;
		try {
			LOGGER.info(restRxnQuery);
			LOGGER.info(localPath);
			rnFlatFile = this.getLocalOrWeb(restRxnQuery, localPath);
			
			KeggReactionFlatFileParser parser = new KeggReactionFlatFileParser(rnFlatFile);
			rxn.setEntry(parser.getEntry());
			rxn.setName(parser.getName());
			rxn.setComment(parser.getComment());
			rxn.setRemark(parser.getRemark());
			rxn.setDefinition(parser.getDefinition());
			rxn.setEquation(parser.getEquation());
			rxn.setEnzymes(parser.getEnzymes());
			rxn.setPathways(parser.getPathways());
			rxn.setRpairs(parser.getRPairs());
			rxn.setOrthologies(parser.getOrthologies());
			rxn.setLeft(parser.getLeft());
			rxn.setRight(parser.getRight());
			
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			LOGGER.debug(e.getStackTrace());
			return null;
		}
		return rxn;
	}

	@Override
	public KeggReactionEntity saveReaction(KeggReactionEntity reaction) {
		throw new RuntimeException("Unsupported Operation.");
	}

	@Override
	public Set<Serializable> getAllReactionIds() {
		throw new RuntimeException("Unsupported Operation.");
	}

	@Override
	public Set<String> getAllReactionEntries() {
		// TODO Auto-generated method stub
		return null;
	}

}
