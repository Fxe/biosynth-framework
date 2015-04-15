package pt.uminho.sysbio.biosynth.integration.io.dao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.IntegrationNodeLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynthframework.chemanalysis.Signature;
import pt.uminho.sysbio.biosynthframework.chemanalysis.MolecularSignature;
import pt.uminho.sysbio.biosynthframework.io.MolecularSignatureDao;

public class Neo4jMolecularSignatureDaoImpl extends AbstractNeo4jDao implements MolecularSignatureDao {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(Neo4jMolecularSignatureDaoImpl.class); 
	
	public Neo4jMolecularSignatureDaoImpl(GraphDatabaseService graphDatabaseService) {
		super(graphDatabaseService);
	}
	
	@Override
	public MolecularSignature getMolecularSignatureById(long msigId) {
		Node sigSetNode = graphDatabaseService.getNodeById(msigId);
		int h_ = (int) sigSetNode.getProperty("h");
		boolean stereo_ = (boolean) sigSetNode.getProperty("stereo");
		
		MolecularSignature signatureSet = new MolecularSignature();
		signatureSet.setId(sigSetNode.getId());
		signatureSet.setH(h_);
		signatureSet.setStereo(stereo_);
		
		Map<Signature, Double> sigMap = new HashMap<> ();
		
		for (Relationship sigRel : sigSetNode.getRelationships(
				Neo4jSignatureRelationship.has_signature)) {
			Node sigNode = sigRel.getOtherNode(sigSetNode);
			
			Signature sig = new Signature(
					(String) sigNode.getProperty(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT));
			double value = (double) sigRel.getProperty("count");
			
			sigMap.put(sig, value);
		}
		
		signatureSet.setSignatureMap(sigMap);
		return signatureSet;
	}
	
	public MolecularSignature getMolecularSignature(long cpdId, int h, boolean stereo) {
		Node cpdNode = Neo4jUtils.getUniqueResult(
				graphDatabaseService.findNodesByLabelAndProperty(
						IntegrationNodeLabel.IntegratedMember, 
						Neo4jDefinitions.MEMBER_REFERENCE, 
						cpdId));
		
		if (cpdNode == null) return null;
		
		for (Relationship relationship : cpdNode.getRelationships(Neo4jSignatureRelationship.has_signature_set)) {
			Node sigSetNode = relationship.getOtherNode(cpdNode);
			int h_ = (int) sigSetNode.getProperty("h");
			boolean stereo_ = (boolean) sigSetNode.getProperty("stereo");
			if (h_ == h && stereo_ == stereo) {
				LOGGER.debug("Found signature set for {} with h:{} and stereo:{}", cpdId, h, stereo);
				MolecularSignature signatureSet = new MolecularSignature();
				signatureSet.setId(sigSetNode.getId());
				signatureSet.setH(h_);
				signatureSet.setStereo(stereo_);
				
				Map<Signature, Double> sigMap = new HashMap<> ();
				
				for (Relationship sigRel : sigSetNode.getRelationships(
						Neo4jSignatureRelationship.has_signature)) {
					Node sigNode = sigRel.getOtherNode(sigSetNode);
					
					Signature sig = new Signature(
							(String) sigNode.getProperty(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT));
					double value = (double) sigRel.getProperty("count");
					
					sigMap.put(sig, value);
				}
				
				signatureSet.setSignatureMap(sigMap);
				return signatureSet;
			}
		}
		
		return null;
	}

	public void deleteMolecularSignature(long cpdId, int h, boolean stereo) {
		// TODO Auto-generated method stub
		
	}

	public void saveMolecularSignature(long cpdId,
			MolecularSignature signatureSet) {
		if (signatureSet.getH() < 1) throw new IllegalArgumentException("h must be greater or equal than 1");
		Node cpdNode = generateMemberNode(cpdId, IntegrationNodeLabel.MetaboliteMember);
		//check if cpd -> sigs exists
		for (Relationship relationship : cpdNode.getRelationships(Neo4jSignatureRelationship.has_signature_set)) {
			Node sigSetNode = relationship.getOtherNode(cpdNode);
			int h = (int) sigSetNode.getProperty("h");
			boolean stereo = (boolean) sigSetNode.getProperty("stereo");
			if (signatureSet.getH() == h && signatureSet.isStereo() == stereo) {
				LOGGER.debug("Found signature set for {} with h:{} and stereo:{}", cpdId, h, stereo);
				//maybe in future update !
				return;
			}
		}
		
		
		Node sigSetNode = graphDatabaseService.createNode();
		sigSetNode.addLabel(Neo4jSignatureLabel.MolecularSignature);
		sigSetNode.setProperty("h", signatureSet.getH());
		sigSetNode.setProperty("stereo", signatureSet.isStereo());
		
		LOGGER.debug("created new signature set id:{}", sigSetNode.getId());
		cpdNode.createRelationshipTo(sigSetNode, Neo4jSignatureRelationship.has_signature_set);

		for (Signature signature : signatureSet.getSignatureMap().keySet()) {
			Node sigNode = Neo4jUtils.mergeUniqueNode(
					Neo4jSignatureLabel.Signature, 
					Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT, 
					signature.getSignature(), executionEngine);
			Relationship relationship = sigSetNode.createRelationshipTo(
					sigNode, Neo4jSignatureRelationship.has_signature);
			
			relationship.setProperty("count", signatureSet.getSignatureMap().get(signature));
		}
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
	public Set<Long> getMoleculeReferencesBySignatureSet(
			MolecularSignature signatureSet) {
		if (signatureSet.getId() == null) return null;
		return getMoleculeReferencesBySignatureSetId(signatureSet.getId());
	}

	@Override
	public Set<Long> getMoleculeReferencesBySignatureSetId(long signatureSetId) {
		Set<Long> result = new HashSet<> ();
		Node sigSetNode = graphDatabaseService.getNodeById(signatureSetId);
		for (Node node : Neo4jUtils.collectNodeRelationshipNodes(sigSetNode, Neo4jSignatureRelationship.has_signature_set)) {
			result.add((long) node.getProperty(Neo4jDefinitions.MEMBER_REFERENCE));
		}
		return result;
	}


}
