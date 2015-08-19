package pt.uminho.sysbio.biosynthframework.biodb.kegg;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.KeggTokens;

public class KeggECNumberEntity extends KeggEntity{
	
	@Override
	public void addProperty(String key, String value) {
		if(key.equals(KeggTokens.ENTRY))
		{
			Pattern p = Pattern.compile(KeggTokens.ECNUMBER_REGEXP);
			Matcher m = p.matcher(value);
			m.find();
			entry = m.group();
		}
		else
			super.addProperty(key, value);
	}

}
