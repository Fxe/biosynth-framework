package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser;

import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggEntity;

public class KeggGenericEntityFlatFileParser {
	
	static public <T extends KeggEntity> T parse(Class<T> entityClass, String rawText) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		Pattern p = Pattern.compile("\\n");
		Matcher m = p.matcher(rawText);

		T entity = entityClass.getDeclaredConstructor().newInstance();
		
		int i=0;
		String currProp=null;
		String line;
//		m.find();
//		String line = rawText.substring(i, m.start()).trim();
//		parseFirstRow(line, entity);
//		i = m.end();
		while(m.find())
		{	
			line = rawText.substring(i, m.start()).trim();
			if(!line.equals("") && !line.equals(KeggTokens.END_OF_FILE_REGEXP))
			{
			  
				Pattern p2 = Pattern.compile(KeggTokens.KEY_VALUES_REGEXP);
				Matcher m2 = p2.matcher(line);
				if(m2.find())
				{
					currProp = m2.group(1);
					entity.addProperty(currProp, m2.group(3));
				}
				else
					entity.addProperty(currProp, line);
				i=m.end();
			}
			
		}
		return entity;
	}
	
	static protected <T extends KeggEntity> void parseFirstRow(String line, T entity){
		String[] ts = line.split(KeggTokens.PROP_KEY_VALUE_SEPARATOR_REGEXP);
		
		if(ts.length%2==0)
			for(int i=0; i<ts.length; i+=2)
				entity.addProperty(ts[i], ts[i+1]);
		else
			entity.addProperty(ts[0], ts[1]);
	}
	

}