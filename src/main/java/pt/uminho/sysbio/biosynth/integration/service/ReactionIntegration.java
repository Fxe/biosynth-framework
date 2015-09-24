package pt.uminho.sysbio.biosynth.integration.service;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.io.dao.IntegrationMetadataDao;
import pt.uminho.sysbio.biosynth.integration.io.dao.ReactionHeterogeneousDao;
import edu.uminho.biosynth.core.data.integration.chimera.strategy.ClusteringStrategy;

/**
 * Some reaction integration ?service?
 * or component or wtv
 * @author Filipe
 *
 */
public class ReactionIntegration {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReactionIntegration.class);
	
	private IntegrationMetadataDao metadataDao;
	private ReactionHeterogeneousDao<?> heterogeneousDao;
	
	public IntegratedCluster generateCluster(Long eid, Set<Long> domain, ClusteringStrategy strategy) {
		LOGGER.debug(String.format("Generating cluster for %d", eid));
		
//		Object rxn = heterogeneousDao.getReactionById("", eid);
		
		//select element id
		strategy.setInitialNode(eid);
		
		//apply strategy to element id
		Set<Long> reids = strategy.execute();
		
		//select only domain elements
		reids.retainAll(domain);
		
		//build cluster
		IntegratedCluster integratedCluster = null;
		
		return integratedCluster;
	}
}
