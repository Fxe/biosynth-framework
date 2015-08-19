package pt.uminho.sysbio.biosynthframework.biodb.kegg;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.KeggTokens;

public class KeggGeneEntity extends KeggEntity{

	protected String nucleotidesSeq;
	protected String aminoacidsSeq;
	protected Set<String> ecNumbers;
	
	
	public void addEcNumber(String ec){
		if(ecNumbers==null)
			ecNumbers = new HashSet<>();
		ecNumbers.add(ec);
	}
	
	@Override
	public void addProperty(String key, String value) {
		if(key.equals(KeggTokens.DEFINITION))
		{
			Pattern p = Pattern.compile("\\(EC:([^\\)]+)\\)");
			Matcher m = p.matcher(value);
			if(m.find())
			{
				String ecs = m.group();
				p = Pattern.compile(KeggTokens.ECNUMBER_REGEXP);
				m = p.matcher(ecs);
				while(m.find())
					addEcNumber(m.group());
			}
		}
		else if(key.equals(KeggTokens.AASEQ))
		{
			if(!value.matches("[0-9]+")) // ignoring first row with the symbols counting
			{
				if(aminoacidsSeq==null)
					aminoacidsSeq = value;
				else
					aminoacidsSeq += value;
			}
		}
		else if(key.equals(KeggTokens.NTSEQ))
		{
			if(!value.matches("[0-9]+")) // ignoring first row with the symbols counting
			{
				if(nucleotidesSeq==null)
					nucleotidesSeq = value;
				else
					nucleotidesSeq += value;
			}
		}
		else
			super.addProperty(key, value);
	}
	
	@Override
	public void addProperty(String key, List<String> values) {
		
		if(key.equals(KeggTokens.AASEQ))
		{
			aminoacidsSeq = values.get(1); // ignoring first row with the symbols counting
			for(int i=2; i<values.size(); i++)
				aminoacidsSeq += values.get(i);
		}
		else if(key.equals(KeggTokens.NTSEQ))
		{
			nucleotidesSeq = values.get(1); // ignoring first row with the symbols counting
			for(int i=2; i<values.size(); i++)
				nucleotidesSeq += values.get(i);
		}
		else
			super.addProperty(key, values);
	}
	

	public String getNucleotidesSeq() {
		return nucleotidesSeq;
	}
	public void setNucleotidesSeq(String nucleotidesSeq) {
		this.nucleotidesSeq = nucleotidesSeq;
	}
	public String getAminoacidsSeq() {
		return aminoacidsSeq;
	}
	public void setAminoacidsSeq(String aminoacidsSeq) {
		this.aminoacidsSeq = aminoacidsSeq;
	}
	public Set<String> getEcNumbers() {
		return ecNumbers;
	}
	public void setEcNumbers(Set<String> ecNumbers) {
		this.ecNumbers = ecNumbers;
	}
	
}
