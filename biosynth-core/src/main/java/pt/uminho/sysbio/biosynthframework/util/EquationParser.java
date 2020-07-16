package pt.uminho.sysbio.biosynthframework.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.Tuple2;

public class EquationParser {
  
  private static final Logger logger = LoggerFactory.getLogger(EquationParser.class);
  
  public static boolean VERBOSE = false;

  public static final int LEFT_TO_RIGHT = 1;
  public static final int RIGHT_TO_LEFT = -1;
  public static final int BIDIRECTIONAL = 0;

  public static final String LEFT = "LEFT";
  public static final String RIGHT = "RIGHT";

  private boolean variable = false;
  public Map<String, Double> leftBasic;
  private Map<String, String> leftVariable;
  public Map<String, Double> rightBasic;
  private Map<String, String> rightVariable;

  private String equation;
  
  public Map<String, Integer> splitTokens = new HashMap<>();
  
  public String detectSplitToken(String eq) {
    String result = null;
    String str = " " + eq + " ";
    for (String symbol : splitTokens.keySet()) {
      if (str.indexOf(String.format(" %s ", symbol)) >= 0) {
        result = symbol;
        break;
      }
    }
    return result;
  }
  
  public static Tuple2<String> getTuple(String str) {
    
    String[] pair = str.trim().split("\\s+");
    String entry = "?";
    String value = "1";
    
    if (pair.length == 1) {
      entry = pair[0].trim();
    } else {
      value = pair[0].trim();
      entry = pair[1].trim();
    }
    
    return new Tuple2<String>(entry, value);
  }
  
  public static List<Tuple2<String>> getBlocks(String str) {
    
    List<Tuple2<String>> result = new ArrayList<>();
    String[] parts = str.split("\\+");
    for (String p : parts) {
      if (!DataUtils.empty(p)) {
        Tuple2<String> tuple = getTuple(p);
        result.add(tuple);
      }
    }
    
    return result;
  }
  
  public static Map<String, Double> getStoichiometry(List<Tuple2<String>> tuples) {
    Map<String, Double> result = new HashMap<>();
    
    for (Tuple2<String> t : tuples) {
      Double value = result.get(t.e1);
      if (value == null) {
        value = 0.0;
      }
      value += Double.parseDouble(t.e2);
      result.put(t.e1, value);
    }
    
    return result;
  }
  
  public void parse(String eq) {
    logger.debug("Equation: {}", eq);
    
    String eqSplit = detectSplitToken(eq);
    if (eqSplit == null) {
      throw new IllegalArgumentException("split token not found. expected: " + splitTokens.keySet());
    }
    
    logger.debug("Split   : {}", eqSplit);
    
    String[] blocks = (" " + eq + " ").split(eqSplit);
    
    logger.debug("Block 1 : {}", blocks[0]);
    logger.debug("Block 2 : {}", blocks[1]);
    
    List<Tuple2<String>> l = getBlocks(blocks[0]);
    List<Tuple2<String>> r = getBlocks(blocks[1]);
    
    logger.debug("Tuple 1 : {}", l);
    logger.debug("Tuple 2 : {}", r);
    
    this.leftBasic  = getStoichiometry(l);
    this.rightBasic = getStoichiometry(r);
    
//    System.out.println(eqSplit + " " + l + " " + r);
  }
  
  public EquationParser() {
    
  }
  
  public EquationParser(String eq) {
    this.equation = eq.replaceAll("\\s+", " ");
    this.leftVariable = new HashMap<String, String> ();
    this.rightVariable = new HashMap<String, String> ();
    this.leftBasic = new HashMap<String, Double> ();
    this.rightBasic = new HashMap<String, Double> ();

    this.parseEquation(equation);
    variable = !(leftVariable.isEmpty() && rightVariable.isEmpty());
  }



  public String getEquation() {
    return equation;
  }

  public Set<String> getLeft() {
    Set<String> ret = new HashSet<String> ();
    ret.addAll( leftBasic.keySet());
    ret.addAll( leftVariable.keySet());
    return ret;
  }

  public Set<String> getRight() {
    Set<String> ret = new HashSet<String> ();
    ret.addAll( rightBasic.keySet());
    ret.addAll( rightVariable.keySet());
    return ret;
  }

  private boolean isSingleton(String[] eqPart) {
    return (eqPart.length == 1);
  }

  private void parseExpression(String expression, Map<String, Double> basic, Map<String, String> variable) throws IllegalArgumentException {
    String[] split = expression.split(" \\+ ");
    if (VERBOSE) System.out.println( Arrays.toString(split));
    for (int i = 0; i < split.length; i++) {
      String specie;
      String value;
      String block = split[i];
      block = block.replaceAll("\\)|\\(", " ").trim();;
      if (VERBOSE) System.out.println("i: #" + block + "#");
      block = block.replaceAll("\\s+", " ").trim();
      String blockParts[] = block.split(" ");
      if (blockParts.length > 2) {
        System.err.println( "ERROR LENGTH " + Arrays.toString(blockParts) + " " + this.equation);
        throw new IllegalArgumentException("ERROR LENGTH " + Arrays.toString(blockParts) + " " + this.equation);
      }
      if ( isSingleton(blockParts)) {
        value = "1";
        specie = blockParts[0];
      } else if ( Character.isUpperCase( blockParts[0].charAt(0))) {
        specie = blockParts[0];
        value = blockParts[1];
      } else {
        specie = blockParts[1];
        value = blockParts[0];
      }

      if (VERBOSE) System.out.println( "SPECIE: " + specie + " VAL: " + value);
      if ( BioSynthUtils.isNumeric(value)) {
        basic.put(specie, Double.parseDouble(value));
      } else {
        if ( variable.containsKey(specie)) {
          variable.put(specie, variable.get(specie) + "+" + value);
        } else {
          variable.put(specie, value);
        }
      }
    }
  }

  private void parseEquation(String eq) {

    String[] eqLeftRightSplit = eq.split("=");
    String eqLeft = eqLeftRightSplit[0];

    eqLeft = eqLeft.replaceAll("<", "").trim();
    parseExpression(eqLeft, leftBasic, leftVariable);

    String eqRight = eqLeftRightSplit[1];
    eqRight = eqRight.replaceAll(">", "").trim();
    parseExpression(eqRight, rightBasic, rightVariable);

    /*
		split = eqLeft.split(" \\+ ");
		for (String s : split) {
			String trimS = s.trim();
			String[] eqSplit = trimS.split(" ");
			if ( eqSplit.length > 2) System.err.println("BAD SPLIT LENGTH > 2 " + Arrays.toString(eqSplit));
			if ( isSingleton(eqSplit)) {
				leftBasic.put(trimS, 1d);
				System.out.println("[" + 1.0d + "]" + " " + trimS);
			} else if ( BioSynthUtilsIO.isNumeric(eqSplit[0])) {
				leftBasic.put(eqSplit[1], Double.parseDouble(eqSplit[0]));
				System.out.println("[" + Double.parseDouble(eqSplit[0]) + "]" + " " + eqSplit[1]);
			} else {
				eqSplit[0] = eqSplit[0].replaceAll("\\)|\\(", "");
				leftGeneric.put(eqSplit[1], eqSplit[0]);
				System.out.println("#" + eqSplit[0] + "#");
			}

		}*/


    /*
		System.out.println("RIGHT:");
		split = eqRight.split(" \\+ ");

		for (String s : split) {
			String trimS = s.trim();
			String[] eqSplit = trimS.split(" ");
			if ( eqSplit.length > 2) System.err.println("BAD SPLIT LENGTH > 2 " + Arrays.toString(eqSplit));
			if ( isSingleton(eqSplit)) {
				rightBasic.put(trimS, 1d);
				System.out.println("[" + 1.0d + "]" + " " + trimS);
			} else if ( BioSynthUtilsIO.isNumeric(eqSplit[0])) {
				rightBasic.put(eqSplit[1], Double.parseDouble(eqSplit[0]));
				System.out.println("[" + Double.parseDouble(eqSplit[0]) + "]" + " " + eqSplit[1]);
			} else {
				eqSplit[0] = eqSplit[0].replaceAll("\\)|\\(", "");
				rightGeneric.put(eqSplit[1], eqSplit[0]);
				System.out.println("#" + eqSplit[0] + "#");
			}
		}
     */
  }

  public String[][] getLeftTriplet() {
    //TODO: parse generic values
    int size = leftBasic.size() + leftVariable.size();
    String[][] ret = new String[size][3];
    int ptr = 0;
    for ( String cpdId : leftBasic.keySet()) {
      ret[ptr][0] = cpdId;
      ret[ptr][1] = leftBasic.get(cpdId).toString();
      ret[ptr][2] = ret[ptr][1];
      ptr++;
    }
    for ( String cpdId : leftVariable.keySet()) {
      ret[ptr][0] = cpdId;
      ret[ptr][1] = "1";
      ret[ptr][2] = leftVariable.get(cpdId).toString();
      ptr++;
    }
    return ret;
  }

  public String[][] getRightTriplet() {
    //TODO: parse generic values
    int size = rightBasic.size() + rightVariable.size();
    String[][] ret = new String[size][3];
    int ptr = 0;
    for ( String cpdId : rightBasic.keySet()) {
      ret[ptr][0] = cpdId;
      ret[ptr][1] = rightBasic.get(cpdId).toString();
      ret[ptr][2] = ret[ptr][1];
      ptr++;
    }
    for ( String cpdId : rightVariable.keySet()) {
      ret[ptr][0] = cpdId;
      ret[ptr][1] = "1";
      ret[ptr][2] = rightVariable.get(cpdId).toString();
      ptr++;
    }
    return ret;
  }

  public boolean isVariable() {
    return this.variable;
  }

  public boolean isEquivalent(String exp1, String exp2) {
    return exp1.equals(exp2);
  }

  public boolean isFeasible() {
    Map<String, String> left = new HashMap<String, String> ();
    Map<String, String> right = new HashMap<String, String> ();

    for ( String k : leftBasic.keySet()) {
      left.put(k, leftBasic.get(k).toString());
    }
    for ( String k : leftVariable.keySet()) {
      if ( left.containsKey(k)) {
        left.put(k, left.get(k) + "+" + leftVariable.get(k).toString());
      } else {
        left.put(k, leftVariable.get(k).toString());
      }
    }

    for ( String k : rightBasic.keySet()) {
      right.put(k, rightBasic.get(k).toString());
    }
    for ( String k : rightVariable.keySet()) {
      if ( right.containsKey(k)) {
        right.put(k, right.get(k) + "+" + rightVariable.get(k).toString());
      } else {
        right.put(k, rightVariable.get(k).toString());
      }
    }

    //System.out.println( left);
    //System.out.println( right);

    for (String entry : left.keySet()) {
      if ( right.containsKey(entry)) {
        if ( isEquivalent(left.get(entry), right.get(entry))) {
          return false;
        }
      }
    }
    return true;
  }

  public void reduceEquationStoich() {
    //TODO: !!!!!!!
    double[][] stoich = new double[2][];
    int leftSize = this.leftBasic.size() + this.leftVariable.size();
    int rightSize = this.rightBasic.size() + this.rightVariable.size();
    stoich[0] = new double[ leftSize];
    stoich[1] = new double[ rightSize];

    //		int ptr;
    //		
    //		ptr = 0;
    for ( int i = 0; i < stoich[0].length; i++) {

    }
    //n A <=> n-1 A + C
    //n Q + m K <=> n+m K
    Map<String, String> variables = new HashMap<String, String> ();
    for ( String leftVar : this.leftVariable.keySet()) {
      System.out.println( leftVar);
      String a = leftVariable.get(leftVar);
      String[] vars = a.split("[^a-z]");
      String[] values = a.split("[a-z]");
      System.out.println( Arrays.toString(vars));
      System.out.println( Arrays.toString(values));
      for ( int i = 0; i < vars.length; i++) {
        variables.put( vars[i], null);
      }
    }

    for ( String rightVar : this.rightVariable.keySet()) {
      System.out.println( rightVar);
      String a = rightVariable.get(rightVar);
      System.out.println( Arrays.toString(a.split("[a-z]")));
      System.out.println( Arrays.toString(a.split("[^a-z]")));
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append( variable ? "Variable Equation" : "Basic Equation");
    sb.append(leftBasic).append(leftVariable);
    sb.append(rightBasic).append(rightVariable);
    return sb.toString();
  }
}
