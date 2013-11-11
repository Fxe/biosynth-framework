package edu.uminho.biosynth.core.data.io.parser.swissprot;

import java.util.HashMap;

public class AbstractSwissProtFlatFileParser {
	protected final String flatfile_;
	protected HashMap<String, String> tabContent_ = null;
	//private final String TAB_REGEX = "\n(\\w+)\\s+";
	private final String END_OF_FILE_DELIMITER = "//";
	
	public AbstractSwissProtFlatFileParser(String flatfile) {
		this.flatfile_ = flatfile;
	}
	
	protected void parseContent() {
		if ( flatfile_ == null) return;
		this.tabContent_ = new HashMap<String, String> ();
		
		String[] lines = flatfile_.split("\\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.equals(END_OF_FILE_DELIMITER)) break;
			//System.out.println(line);
			String linecode = line.substring(0, 2);
			String body = line.substring(5, line.length());
			//System.out.println("#" + linecode + "#");
			//System.out.println("#" + body + "#");
			if ( !this.tabContent_.containsKey(linecode)) this.tabContent_.put(linecode, "");
			String data = this.tabContent_.get(linecode);
			data = data.concat(body);
			//System.out.println(data);
			this.tabContent_.put(linecode, data);
		}
		
		//System.out.println(this.tabContent_);
	}
}
