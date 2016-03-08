package pt.uminho.sysbio.biosynth.integration.model;


import java.io.Serializable;
import java.util.List;
import java.util.Set;


import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.AbstractNeo4jDao;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtilsIntegration;


public class Neo4jGraphNormalizedGPR extends AbstractNeo4jDao implements GPRDao<GraphGPREntity>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jGraphNormalizedGPR.class);
	protected static final Label GENE_LABEL = GlobalLabel.Gene;
	private String model;
	
	public Neo4jGraphNormalizedGPR(GraphDatabaseService graphDatabaseService, String model) {
		super(graphDatabaseService);
		this.model = model;
		// TODO Auto-generated constructor stub
	}

	@Override
	public GraphGPREntity getGPRById(String tag, Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphGPREntity getGPRByEntry(String tag, String entry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphGPREntity saveGPR(String tag, GraphGPREntity gpr) {
//		GPRNormalization gprNormalization = new GPRNormalization();
		Node node = graphDatabaseService.createNode();
		LOGGER.debug("Create " + node);
		node.addLabel(DynamicLabel.label(gpr.getMajorLabel()));
		for (String label : gpr.getLabels()){
			node.addLabel(DynamicLabel.label(label));
		}
		Neo4jUtils.applyProperties(node,  gpr.getProperties());
//		AbstractSyntaxTree<DataTypeEnum, IValue> gr = gpr.getGeneRule();
//		Set<Set<String>> ngprs;
		try {
//			ngprs = gprNormalization.getVariablesToSenceNode(gr.getRootNode(), true);
			NormalizedGPRTree normalizedTree = gpr.getNormalizedTree();
			createNormalizedGPR(node, normalizedTree.getTreeNode());
			node.setProperty("normalized_rule", normalizedTree.toString());
			node.setProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY.toString(), gpr.getMajorLabel());
			node.setProperty(Neo4jDefinitions.PROXY_PROPERTY.toString(), false);
			gpr.setId(node.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return gpr;
	}
	
	public void createNormalizedGPR(Node parent, NormalizedGPRTreeNode gpr){
		if (gpr instanceof GPRLeaf){
			createNodeLeaf(parent, ((GPRLeaf) gpr).getLeaf());
		} else if (gpr instanceof GPRAnd){
			Node node_and = createNodeAnd(parent);
			for (String leaf : ((GPRAnd) gpr).getElements()){
				createNodeLeaf(node_and, leaf);
			}
		} else if (gpr instanceof GPROr){
			
			Node node_or = createNodeOr(parent);
			
			for (Set<String> orElements : ((GPROr) gpr).getElements()){
				if (orElements.size() == 1){
					createNodeLeaf(node_or, orElements.iterator().next());
				} else {
					Node node_and = createNodeAnd(node_or);
					for (String andElement : orElements){
						createNodeLeaf(node_and, andElement);
					}
				}
			}
		}
	}
	
	public Node createNodeAnd(Node parent){
		DynamicRelationshipType relationshipType = DynamicRelationshipType.withName(GPRRelationshipType.has_logical_operator.toString());
		Node node_and = graphDatabaseService.createNode();
		LOGGER.debug("Create " + node_and);
		node_and.addLabel(DynamicLabel.label(GPRMajorLabels.AND.toString()));
		node_and.addLabel(DynamicLabel.label(GlobalLabel.LogicalOperator.toString()));
		node_and.setProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY.toString(), GPRMajorLabels.AND.toString());
		node_and.setProperty(Neo4jDefinitions.PROXY_PROPERTY.toString(), false);
		Relationship relationship = parent.createRelationshipTo(node_and, relationshipType);
		return node_and;
	}
	
	public Node createNodeOr(Node parent){
		DynamicRelationshipType relationshipType = DynamicRelationshipType.withName(GPRRelationshipType.has_logical_operator.toString());
		Node node_or = graphDatabaseService.createNode();
		LOGGER.debug("Create " + node_or);
		node_or.addLabel(DynamicLabel.label(GPRMajorLabels.OR.toString()));
		node_or.addLabel(DynamicLabel.label(GlobalLabel.LogicalOperator.toString()));
		node_or.setProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY.toString(), GPRMajorLabels.OR.toString());
		node_or.setProperty(Neo4jDefinitions.PROXY_PROPERTY.toString(), false);
		Relationship relationship = parent.createRelationshipTo(node_or, relationshipType);
		return node_or;
	}
	
	public void createNodeLeaf(Node parent, String gene){
		GraphGPRGeneEntity gprGene = new GraphGPRGeneEntity();
		gprGene.setEntry(gene + "@" + this.model);
		gprGene.addLabel(MetabolicModelLabel.ModelGene.toString());
		gprGene.setMajorLabel(GPRMajorLabels.Leaf.toString());
		gprGene.setName(gene);
		gprGene.setModel(this.model);
		createGeneGPR(parent, gprGene);
	}
	
	public GraphGPRGeneEntity createGeneGPR(Node parent, GraphGPRGeneEntity gene){
		DynamicRelationshipType relationshipType = DynamicRelationshipType.withName(GPRRelationshipType.has_leaf.toString());
		boolean create = true;
		for (Node node : graphDatabaseService.findNodesByLabelAndProperty(
				DynamicLabel.label(gene.getMajorLabel()),
				"entry",
				gene.getEntry())) {
			create = false;
			LOGGER.debug(String.format("Found Previous node with entry:%s", gene.getEntry()));
			LOGGER.debug(String.format("MODE:UPDATE %s", node));
			Neo4jUtils.applyProperties(node, gene.getProperties());
			
			node.setProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY.toString(), gene.getMajorLabel());
			node.setProperty(Neo4jDefinitions.PROXY_PROPERTY.toString(), false);
			Relationship relationship = parent.createRelationshipTo(node, relationshipType);
			gene.setId(node.getId());
		}
		
		if (create){
			Node node = graphDatabaseService.createNode();
			LOGGER.debug("Create " + node);
			node.addLabel(DynamicLabel.label(gene.getMajorLabel()));
			for (String label : gene.getLabels()){
				node.addLabel(DynamicLabel.label(label));
			}
			Neo4jUtils.applyProperties(node,  gene.getProperties());
			
			node.setProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY.toString(), gene.getMajorLabel());
			node.setProperty(Neo4jDefinitions.PROXY_PROPERTY.toString(), false);
			Relationship relationship = parent.createRelationshipTo(node, relationshipType);
			gene.setId(node.getId());

		}
		
		return gene;
	}

	@Override
	public List<Long> getGlobalAllGPRIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Long> getAllGPRIds(String tag) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAllGPREntries(String tag) {
		// TODO Auto-generated method stub
		return null;
	}


	
}
