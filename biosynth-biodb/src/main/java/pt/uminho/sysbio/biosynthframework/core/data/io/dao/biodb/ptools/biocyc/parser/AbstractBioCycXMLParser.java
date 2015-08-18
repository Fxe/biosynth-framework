package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.ptools.biocyc.parser;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.ptools.PathwayToolsParser;

public class AbstractBioCycXMLParser implements PathwayToolsParser {

	protected final String xmlDocument;
	protected JSONObject content;
	
	public AbstractBioCycXMLParser(String xmlDocument) {
		this.xmlDocument = xmlDocument;
	}
	
	public void parseContent() throws JSONException {
		content = XML.toJSONObject(this.xmlDocument);
	}

}
