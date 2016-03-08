package pt.uminho.sysbio.biosynth.integration.model;

import java.io.IOException;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;


import org.hibernate.SessionFactory;
import org.json.JSONObject;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.xml.sax.SAXException;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelLabel;

public class GPRToNeo4j {
	
	static SessionFactory sessionFactory;
	static GraphDatabaseService graphDatabaseService;
	
	public static void main(String[] args){
//		graphDatabaseService = GPRHelperNeo4jConfigInitializer
//				.initializeNeo4jDatabaseConstraints("/home/chris/neo4j-community-2.1.6/data/graph.db");
//		
//		Container container;
//		try {
//			container = new Container(new JSBMLReader(
//					"/home/chris/Dropbox/iMM904_flux.xml", "", false));
		
//			String model = "iMM904";
//			
//			Transaction tx = graphDatabaseService.beginTx();
//			for (String reaction : container.getReactions().keySet()){
//				if (!(container.getReaction(reaction).getGeneRule().getRootNode() == null)){
//					GraphGPREntity gprEntity = new GraphGPREntity();
//					gprEntity.setEntry(reaction + "@" + model);
//					gprEntity.addProperty("rule", container.getReaction(reaction).getGeneRuleString());
//					gprEntity.setReaction(reaction);
//					
//					AbstractSyntaxTree<DataTypeEnum, IValue> gpr = container.getReaction(reaction).getGeneRule();
//					GPRNormalization norm_gpr = new GPRNormalization();
//					Set<Set<String>> normalized_gpr = norm_gpr.getVariablesToSenceNode(gpr.getRootNode(), true);
//					NormalizedGPRTree normTree = new NormalizedGPRTree(normalized_gpr);
//					gprEntity.setNormalizedTree(normTree);
//					gprEntity.setMajorLabel(GPRMajorLabels.Root.toString());
//					gprEntity.addLabel(MetabolicModelLabel.ModelGPR.toString());
//					Neo4jGraphNormalizedGPR neo4jDao = new Neo4jGraphNormalizedGPR(graphDatabaseService, model);
//					neo4jDao.saveGPR("", gprEntity);
//				}
//			}
//			tx.success();
//			tx.close();
//			graphDatabaseService.shutdown();
//		} catch (IOException | XMLStreamException | ErrorsException
//				| ParserConfigurationException | SAXException
//				| JSBMLValidationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	
	
}
