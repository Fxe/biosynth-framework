package pt.uminho.sysbio.biosynth.integration.etl.sbml;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.etl.EtlTransform;
import pt.uminho.sysbio.biosynth.integration.io.dao.AbstractNeo4jDao;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynthframework.sbml.XMLSbmlMetabolicModel;

public class XmlSbmlMetaboliteToNeo4jNodeTransform extends AbstractNeo4jDao implements EtlTransform<XMLSbmlMetabolicModel, Node>{

	
	public XmlSbmlMetaboliteToNeo4jNodeTransform(GraphDatabaseService graphDatabaseService) {
		super(graphDatabaseService);
	}
	
	@Override
	public Node etlTransform(XMLSbmlMetabolicModel mmd) {
		if (!validate(mmd)) return null;
		
		Node mmdNode = Neo4jUtils.mergeUniqueNode(GlobalLabel.MetabolicModel, "entry", mmd.getAttributes().get("entry"), this.executionEngine);
		
		return null;
	}
	
	public boolean validate(XMLSbmlMetabolicModel mmd) {
		if (mmd.getAttributes().get("entry") == null) {
			return false;
		}
		return true;
	}

  @Override
  public Node apply(XMLSbmlMetabolicModel t) {
    return etlTransform(t);
  }

}
