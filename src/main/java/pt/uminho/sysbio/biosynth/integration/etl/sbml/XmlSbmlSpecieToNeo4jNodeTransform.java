package pt.uminho.sysbio.biosynth.integration.etl.sbml;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.DefaultNeo4jEntityNode;
import pt.uminho.sysbio.biosynth.integration.etl.EtlTransform;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelLabel;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlSpecie;

public class XmlSbmlSpecieToNeo4jNodeTransform implements EtlTransform<XmlSbmlSpecie, DefaultNeo4jEntityNode> {

	private final static Logger LOGGER = LoggerFactory.getLogger(XmlSbmlSpecieToNeo4jNodeTransform.class);
	
	
	enum DataType {
		STRING, INTEGER, LONG, FLOAT, DOUBLE, BOOLEAN
	}
	Map<String, String> translateMap = new HashMap<> ();
	Map<String, DataType> map2 = new HashMap<> ();
	
	public XmlSbmlSpecieToNeo4jNodeTransform() {
		translateMap.put("id", "entry");
		map2.put("constant", DataType.BOOLEAN);
		map2.put("boundaryCondition", DataType.BOOLEAN);
		map2.put("hasOnlySubstanceUnits", DataType.BOOLEAN);
		map2.put("initialConcentration", DataType.DOUBLE);
		map2.put("id", DataType.STRING);
		map2.put("name", DataType.STRING);
		map2.put("metaid", DataType.STRING);
		map2.put("sboTerm", DataType.STRING);
		map2.put("compartment", DataType.STRING);
	}

	@Override
	public DefaultNeo4jEntityNode etlTransform(XmlSbmlSpecie spi) {
		DefaultNeo4jEntityNode node = new DefaultNeo4jEntityNode(MetabolicModelLabel.MetaboliteSpecie, false);
		setupProperties(node, spi);
//		node.set
		return null;
	}
	
	private void setupProperties(DefaultNeo4jEntityNode node, XmlSbmlSpecie spi) {
		for (String property : spi.getAttributes().keySet()) {
			String key = property;
			if (translateMap.containsKey(property)) {
				key = translateMap.get(property);
			}
			DataType dataType = DataType.STRING;
			if (map2.containsKey(property)) {
				dataType = map2.get(property);
			} else {
				LOGGER.warn("Unknown data type for {} assumming {}", property, dataType);
			}
			String value_ = spi.getAttributes().get(property);
			Object value = null;
			switch (dataType) {
				case BOOLEAN: value = Boolean.parseBoolean(value_); break;
				case INTEGER: value = Integer.parseInt(value_); break;
				case FLOAT: value = Float.parseFloat(value_); break;
				case DOUBLE: value = Double.parseDouble(value_); break;
				case STRING: value = value_; break;
				default:
					value = value_;
					LOGGER.warn("{}", dataType);
					break;
			}
			LOGGER.trace("Property {} {} -> {} {}", property, key, value, dataType);
			node.getProperties().put(key, value);
		}
	}

}
