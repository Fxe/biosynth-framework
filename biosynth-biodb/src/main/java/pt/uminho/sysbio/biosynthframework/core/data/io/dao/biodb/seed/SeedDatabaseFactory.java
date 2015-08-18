package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.seed;


//import org.apache.commons.io.IOUtils;
//import org.codehaus.jackson.JsonNode;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.codehaus.jackson.node.ObjectNode;
//import org.neo4j.helpers.collection.IteratorUtil;

public class SeedDatabaseFactory {
	public void newEmbeddedDatabase(String path) {
//		try {
//			String bigJson = IOUtils.toString(new FileInputStream(jsonFile));
//			System.out.println(bigJson.substring(0, 100));
//			ObjectMapper m = new ObjectMapper();
//			JsonNode rootNode = m.readTree(new FileInputStream(jsonFile));
//			System.out.println("Array ? " + rootNode.isArray());
//			ObjectNode metadata = m.createObjectNode();
//			for (String fieldName : IteratorUtil.asCollection(rootNode.getFieldNames())) {
//				JsonNode node = rootNode.get(fieldName);
//				if (node.isArray()) {
//					IOUtils.write(node.toString().getBytes(), new FileOutputStream(new File("D:/home/data/seed/" + fieldName + ".json")));
//				} else {
//					metadata.put(fieldName, node);
//				}
//				System.out.println(fieldName + " is array ? " + node.isArray());
//			}
//			for (String fieldNames : IteratorUtil.asCollection(rootNode.fieldNames())) {
//				System.out.println(fieldNames);
//			}
//			System.out.println(rootNode.size());
//			IOUtils.write(metadata.toString().getBytes(), new FileOutputStream(new File("D:/home/data/seed/metadata.json")));
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
	}
}
