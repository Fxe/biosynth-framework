package pt.uminho.sysbio.biosynthframework;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnzymeCommissionNumber {

	public static EnzymeCommissionNumber parseEnzymeCommissionNumber(String s) throws IllegalArgumentException {
		EnzymeCommissionNumber ecn = new EnzymeCommissionNumber();
		
		try {
		
		Pattern pattern = Pattern.compile("([0-9]+)");
		Matcher matcher = pattern.matcher(s);
		Integer[] ecnLevels = new Integer[4];
		int lIndex = 0;
		while (matcher.find()) {
			ecnLevels[lIndex++] = Integer.parseInt(matcher.group(0));
		}
		ecn.setLevel1(ecnLevels[0]);
		ecn.setLevel2(ecnLevels[1]);
		ecn.setLevel3(ecnLevels[2]);
		ecn.setLevel4(ecnLevels[3]);
		
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException(s);
		}
		
		return ecn;
	}
	
	private Integer level1;
	private Integer level2;
	private Integer level3;
	private Integer level4;
	
	public Integer getLevel1() { return level1;}
	public void setLevel1(Integer level1) { this.level1 = level1;}
	
	public Integer getLevel2() { return level2;}
	public void setLevel2(Integer level2) { this.level2 = level2;}
	
	public Integer getLevel3() { return level3;}
	public void setLevel3(Integer level3) { this.level3 = level3;}
	
	public Integer getLevel4() { return level4;}
	public void setLevel4(Integer level4) { this.level4 = level4;}
	
	public String getEnzymeCommissionNumber() {
		return String.format("%s.%s.%s.%s", 
				level1==null? "-" : level1,
				level2==null? "-" : level2,
				level3==null? "-" : level3,
				level4==null? "-" : level4);
	}
	
	@Override
	public String toString() {
		return getEnzymeCommissionNumber();
	}
}
