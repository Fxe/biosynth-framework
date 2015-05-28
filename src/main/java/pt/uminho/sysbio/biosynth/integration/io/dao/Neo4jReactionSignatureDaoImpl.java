package pt.uminho.sysbio.biosynth.integration.io.dao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.tooling.GlobalGraphOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.IntegrationNodeLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynthframework.chemanalysis.ReactionSignature;
import pt.uminho.sysbio.biosynthframework.chemanalysis.Signature;
import pt.uminho.sysbio.biosynthframework.io.ReactionSignatureDao;

public class Neo4jReactionSignatureDaoImpl extends AbstractNeo4jDao implements ReactionSignatureDao {

	private final static Logger LOGGER = LoggerFactory.getLogger(Neo4jReactionSignatureDaoImpl.class);
	
	public Neo4jReactionSignatureDaoImpl(GraphDatabaseService graphDatabaseService) {
		super(graphDatabaseService);
	}
	
	@Override
	public ReactionSignature getReactionSignatureById(long rsigId) {
		Node sigSetNode = graphDatabaseService.getNodeById(rsigId);
		
		int h_ = (int) sigSetNode.getProperty("h");
		boolean stereo_ = (boolean) sigSetNode.getProperty("stereo");
		
		ReactionSignature signatureSet = new ReactionSignature();
		signatureSet.setId(sigSetNode.getId());
		signatureSet.setH(h_);
		signatureSet.setStereo(stereo_);
		
		{
			Map<Signature, Double> sigMap = new HashMap<> ();
			for (Relationship sigRel : sigSetNode.getRelationships(
					Neo4jSignatureRelationship.left_signature)) {
				Node sigNode = sigRel.getOtherNode(sigSetNode);
				
				Signature sig = new Signature(
						(String) sigNode.getProperty(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT));
				double value = (double) sigRel.getProperty("count");
				
				sigMap.put(sig, value);
			}
			signatureSet.setLeftSignatureMap(sigMap);
		}
		
		{
			Map<Signature, Double> sigMap = new HashMap<> ();
			for (Relationship sigRel : sigSetNode.getRelationships(
					Neo4jSignatureRelationship.right_signature)) {
				Node sigNode = sigRel.getOtherNode(sigSetNode);
				
				Signature sig = new Signature(
						(String) sigNode.getProperty(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT));
				double value = (double) sigRel.getProperty("count");
				
				sigMap.put(sig, value);
			}
			signatureSet.setRightSignatureMap(sigMap);
		}
		
		return signatureSet;
	}
	
	@Override
	public ReactionSignature getReactionSignature(long rxnId, int h,
			boolean stereo) {
		Node rxnNode = Neo4jUtils.getUniqueResult(
				graphDatabaseService.findNodesByLabelAndProperty(
						IntegrationNodeLabel.IntegratedMember, 
						Neo4jDefinitions.MEMBER_REFERENCE, 
						rxnId));
		if (rxnNode == null) return null;
		
		for (Relationship relationship : rxnNode.getRelationships(Neo4jSignatureRelationship.has_signature_set)) {
			Node sigSetNode = relationship.getOtherNode(rxnNode);
			int h_ = (int) sigSetNode.getProperty("h");
			boolean stereo_ = (boolean) sigSetNode.getProperty("stereo");
			if (h_ == h && stereo_ == stereo) {
				LOGGER.debug("Found signature set for {} with h:{} and stereo:{}", rxnNode, h, stereo);
				ReactionSignature signatureSet = new ReactionSignature();
				signatureSet.setId(sigSetNode.getId());
				signatureSet.setH(h_);
				signatureSet.setStereo(stereo_);
				
				{
					Map<Signature, Double> sigMap = new HashMap<> ();
					for (Relationship sigRel : sigSetNode.getRelationships(
							Neo4jSignatureRelationship.left_signature)) {
						Node sigNode = sigRel.getOtherNode(sigSetNode);
						
						Signature sig = new Signature(
								(String) sigNode.getProperty(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT));
						double value = (double) sigRel.getProperty("count");
						
						sigMap.put(sig, value);
					}
					signatureSet.setLeftSignatureMap(sigMap);
				}
				
				{
					Map<Signature, Double> sigMap = new HashMap<> ();
					for (Relationship sigRel : sigSetNode.getRelationships(
							Neo4jSignatureRelationship.right_signature)) {
						Node sigNode = sigRel.getOtherNode(sigSetNode);
						
						Signature sig = new Signature(
								(String) sigNode.getProperty(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT));
						double value = (double) sigRel.getProperty("count");
						
						sigMap.put(sig, value);
					}
					signatureSet.setRightSignatureMap(sigMap);
				}
				
				return signatureSet;
			}
		}
		return null;
	}

	@Override
	public boolean deleteReactionSignature(long rxnId, int h, boolean stereo) {
		Node rxnNode = Neo4jUtils.getUniqueResult(
				graphDatabaseService.findNodesByLabelAndProperty(
						IntegrationNodeLabel.IntegratedMember, 
						Neo4jDefinitions.MEMBER_REFERENCE, 
						rxnId));
		if (rxnNode == null) return false;
		
		for (Relationship relationship : rxnNode.getRelationships(Neo4jSignatureRelationship.has_signature_set)) {
			Node sigSetNode = relationship.getOtherNode(rxnNode);
			int h_ = (int) sigSetNode.getProperty("h");
			boolean stereo_ = (boolean) sigSetNode.getProperty("stereo");
			if (h_ == h && stereo_ == stereo) {
				LOGGER.debug("Delete signature set for {} with h:{} and stereo:{}", rxnNode, h, stereo);
				Map<Long, Node> testForOrphan = new HashMap<> ();
				for (Relationship r : sigSetNode.getRelationships()) {
					Node orphan = r.getOtherNode(sigSetNode);
					testForOrphan.put(orphan.getId(), orphan);
					r.delete();
					LOGGER.debug("Deleted releationship {}", r.getId());
				}
				
				for (Node n : testForOrphan.values()) {
					if (n.getDegree() == 0) {
						LOGGER.debug("Deleted node {}", n.getId());
						n.delete();
					}
				}
				
				LOGGER.debug("Deleted node {}", sigSetNode.getId());
				sigSetNode.delete();
//				ReactionSignature signatureSet = new ReactionSignature();
//				signatureSet.setId(sigSetNode.getId());
//				signatureSet.setH(h_);
//				signatureSet.setStereo(stereo_);
//				
//				{
//					Map<Signature, Double> sigMap = new HashMap<> ();
//					for (Relationship sigRel : sigSetNode.getRelationships(
//							Neo4jSignatureRelationship.left_signature)) {
//						Node sigNode = sigRel.getOtherNode(sigSetNode);
//						
//						Signature sig = new Signature(
//								(String) sigNode.getProperty(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT));
//						double value = (double) sigRel.getProperty("count");
//						
//						sigMap.put(sig, value);
//					}
//					signatureSet.setLeftSignatureMap(sigMap);
//				}
//				
//				{
//					Map<Signature, Double> sigMap = new HashMap<> ();
//					for (Relationship sigRel : sigSetNode.getRelationships(
//							Neo4jSignatureRelationship.right_signature)) {
//						Node sigNode = sigRel.getOtherNode(sigSetNode);
//						
//						Signature sig = new Signature(
//								(String) sigNode.getProperty(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT));
//						double value = (double) sigRel.getProperty("count");
//						
//						sigMap.put(sig, value);
//					}
//					signatureSet.setRightSignatureMap(sigMap);
//				}
				
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void saveReactionSignature(long rxnId,
			ReactionSignature signatureSet) {
		if (signatureSet.getH() < 1) throw new IllegalArgumentException("h must be greater or equal than 1");
		Node rxnNode = generateMemberNode(rxnId, IntegrationNodeLabel.ReactionMember);

		//check if rxn -> sigs exists
		for (Relationship relationship : rxnNode.getRelationships(Neo4jSignatureRelationship.has_signature_set)) {
			Node sigSetNode = relationship.getOtherNode(rxnNode);
			int h = (int) sigSetNode.getProperty("h");
			boolean stereo = (boolean) sigSetNode.getProperty("stereo");
			if (signatureSet.getH() == h && signatureSet.isStereo() == stereo) {
				LOGGER.debug("Found signature set for {} with h:{} and stereo:{}", rxnId, h, stereo);
				//maybe in future update !
				return;
			}
		}
		
		
		Node sigSetNode = graphDatabaseService.createNode();	
		sigSetNode.addLabel(Neo4jSignatureLabel.ReactionSignature);
		sigSetNode.setProperty("h", signatureSet.getH());
		sigSetNode.setProperty("stereo", signatureSet.isStereo());
		
		LOGGER.debug("created new signature set id:{}", sigSetNode.getId());
		rxnNode.createRelationshipTo(sigSetNode, Neo4jSignatureRelationship.has_signature_set);

		for (Signature signature : signatureSet.getLeftSignatureMap().keySet()) {
			Node sigNode = Neo4jUtils.mergeUniqueNode(
					Neo4jSignatureLabel.Signature, 
					Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT, 
					signature.getSignature(), executionEngine);
			Relationship relationship = sigSetNode.createRelationshipTo(
					sigNode, Neo4jSignatureRelationship.left_signature);
			
			relationship.setProperty("count", signatureSet.getLeftSignatureMap().get(signature));
		}
		
		for (Signature signature : signatureSet.getRightSignatureMap().keySet()) {
			Node sigNode = Neo4jUtils.mergeUniqueNode(
					Neo4jSignatureLabel.Signature, 
					Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT, 
					signature.getSignature(), executionEngine);
			Relationship relationship = sigSetNode.createRelationshipTo(
					sigNode, Neo4jSignatureRelationship.right_signature);
			
			relationship.setProperty("count", signatureSet.getRightSignatureMap().get(signature));
		}
	}
	
	 public long saveReactionSignature(ReactionSignature signatureSet) {
	    if (signatureSet.getH() < 1) {
	      throw new IllegalArgumentException("h must be greater or equal than 1");
	    }
	    
	    Node rsigNode = graphDatabaseService.createNode();  
	    rsigNode.addLabel(Neo4jSignatureLabel.ReactionSignature);
	    rsigNode.setProperty("h", signatureSet.getH());
	    rsigNode.setProperty("stereo", signatureSet.isStereo());

	    for (Signature signature : signatureSet.getLeftSignatureMap().keySet()) {
	      Node sigNode = Neo4jUtils.mergeUniqueNode(
	          Neo4jSignatureLabel.Signature, 
	          Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT, 
	          signature.getSignature(), executionEngine);
	      Relationship relationship = rsigNode.createRelationshipTo(
	          sigNode, Neo4jSignatureRelationship.left_signature);
	      
	      relationship.setProperty("count", signatureSet.getLeftSignatureMap().get(signature));
	    }
	    
	    for (Signature signature : signatureSet.getRightSignatureMap().keySet()) {
	      Node sigNode = Neo4jUtils.mergeUniqueNode(
	          Neo4jSignatureLabel.Signature, 
	          Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT, 
	          signature.getSignature(), executionEngine);
	      Relationship relationship = rsigNode.createRelationshipTo(
	          sigNode, Neo4jSignatureRelationship.right_signature);
	      
	      relationship.setProperty("count", signatureSet.getRightSignatureMap().get(signature));
	    }
	    
	    return rsigNode.getId();
	  }

	public Node generateMemberNode(long referenceId, IntegrationNodeLabel label) {
		Node node = Neo4jUtils.getUniqueResult(graphDatabaseService
				.findNodesByLabelAndProperty(
						IntegrationNodeLabel.IntegratedMember, 
						Neo4jDefinitions.MEMBER_REFERENCE, referenceId));
		
		if (node == null) {
			node = graphDatabaseService.createNode();
			node.addLabel(IntegrationNodeLabel.IntegratedMember);
			node.addLabel(label);
			node.setProperty(Neo4jDefinitions.MEMBER_REFERENCE, referenceId);
		}
		
		return node;
	}

	@Override
	public Set<Long> listAllReactionSignatureId(int h, boolean stereo) {
		Set<Long> ids = new HashSet<> ();
		
		for (Node node : GlobalGraphOperations
				.at(graphDatabaseService)
				.getAllNodesWithLabel(Neo4jSignatureLabel.ReactionSignature)) {
			int h_ = (int) node.getProperty("h");
			boolean stereo_ = (boolean) node.getProperty("stereo");
			if (h_ == h && stereo_ == stereo) {
				ids.add(node.getId());
			}
		}
		
		return ids;
	}
}
