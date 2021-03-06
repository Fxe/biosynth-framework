package pt.uminho.sysbio.biosynthframework.biodb.kegg;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.util.BioSynthUtils;

public class KeggEquationParser {

  private static final Logger logger = LoggerFactory.getLogger(KeggEquationParser.class);
  
  public static final boolean VERBOSE = false;

  public static final int LEFT_TO_RIGHT = 1;
  public static final int RIGHT_TO_LEFT = -1;
  public static final int BIDIRECTIONAL = 0;

  public static final String LEFT = "LEFT";
  public static final String RIGHT = "RIGHT";

  private boolean variable = false;
  private Map<String, Double> leftBasic = new HashMap<> ();
  private Map<String, String> leftVariable = new HashMap<> ();
  private Map<String, Double> rightBasic = new HashMap<> ();
  private Map<String, String> rightVariable = new HashMap<> ();
  public Map<String, String> leftModifier = new HashMap<> ();
  public Map<String, String> rightModifier = new HashMap<> ();
  
  private String equation;

  public KeggEquationParser(String eq) {
    this.equation = eq.replaceAll("\\s+", " ");
    this.parseEquation(equation);
    variable = !(leftVariable.isEmpty() && rightVariable.isEmpty());
  }



  public String getEquation() { return equation;}

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

  private void parseExpression(String expression, 
                               Map<String, Double> basic, 
                               Map<String, String> variable, 
                               Map<String, String> modifier) throws IllegalArgumentException {
    String[] split = expression.split(" \\+ ");
    
    for (int i = 0; i < split.length; i++) {
      String specie;
      String value;
      String block = split[i];
      
      logger.debug("block: [{}]", block);
      
      block = block.replaceAll("\\)|\\(", " ").trim();;
      if (VERBOSE) System.out.println("i: #" + block + "#");
      block = block.replaceAll("\\s+", " ").trim();
      String blockParts[] = block.split(" ");
      
      logger.debug("blockParts: [{}]", blockParts.length);
      
      String cpdModifier = null;
      if (blockParts.length > 2) {
        cpdModifier = blockParts[2];
        if (cpdModifier.equals("in") || cpdModifier.equals("out")) {
          blockParts = new String[] {blockParts[0], blockParts[1]};
        }
      }
      
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
      
      
      logger.debug("block: [{}] compound[{}] value[{}] modifier[{}]", block, specie, value, cpdModifier);
      
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
      modifier.put(specie, cpdModifier);
    }
  }

  private void parseEquation(String eq) {
    logger.debug("equation: [{}]", eq);
    String[] eqLeftRightSplit = eq.split("=");
    String eqLeft = eqLeftRightSplit[0];

    eqLeft = eqLeft.replaceAll("<", "").trim();
    logger.debug("LHS: [{}]", eqLeft);
    parseExpression(eqLeft, leftBasic, leftVariable, leftModifier);

    String eqRight = eqLeftRightSplit[1];
    eqRight = eqRight.replaceAll(">", "").trim();
    logger.debug("RHS: [{}]", eqRight);
    parseExpression(eqRight, rightBasic, rightVariable, rightModifier);
  }

  public String[][] getLeftTriplet() {
    //TODO: parse generic values
    int size = leftBasic.size() + leftVariable.size();
    String[][] ret = new String[size][4];
    int ptr = 0;
    for ( String cpdId : leftBasic.keySet()) {
      ret[ptr][0] = cpdId;
      ret[ptr][1] = leftBasic.get(cpdId).toString();
      ret[ptr][2] = ret[ptr][1];
      ret[ptr][3] = leftModifier.get(cpdId);
      ptr++;
    }
    for ( String cpdId : leftVariable.keySet()) {
      ret[ptr][0] = cpdId;
      ret[ptr][1] = "1";
      ret[ptr][2] = leftVariable.get(cpdId).toString();
      ret[ptr][3] = leftModifier.get(cpdId);
      ptr++;
    }
    return ret;
  }

  public String[][] getRightTriplet() {
    //TODO: parse generic values
    int size = rightBasic.size() + rightVariable.size();
    String[][] ret = new String[size][4];
    int ptr = 0;
    for ( String cpdId : rightBasic.keySet()) {
      ret[ptr][0] = cpdId;
      ret[ptr][1] = rightBasic.get(cpdId).toString();
      ret[ptr][2] = ret[ptr][1];
      ret[ptr][3] = rightModifier.get(cpdId);
      ptr++;
    }
    for ( String cpdId : rightVariable.keySet()) {
      ret[ptr][0] = cpdId;
      ret[ptr][1] = "1";
      ret[ptr][2] = rightVariable.get(cpdId).toString();
      ret[ptr][3] = rightModifier.get(cpdId);
      ptr++;
    }
    //System.out.println(rightModifier);
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

    //    int ptr;
    //    
    //    ptr = 0;
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
