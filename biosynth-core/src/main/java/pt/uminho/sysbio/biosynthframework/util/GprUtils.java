package pt.uminho.sysbio.biosynthframework.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bpodgursky.jbool_expressions.And;
import com.bpodgursky.jbool_expressions.ExprUtil;
import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Or;
import com.bpodgursky.jbool_expressions.Variable;
import com.bpodgursky.jbool_expressions.parsers.ExprParser;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.google.common.collect.Sets;

public class GprUtils {
  
  private static final Logger logger = LoggerFactory.getLogger(GprUtils.class);
  
  public static<T> Set<T> getProtein(Variable<T> variable) {
    Set<T> protein = new HashSet<>();
    protein.add(variable.getValue());
    return protein;
  }
  
  public static<T> Set<T> getProtein(And<T> and) {
    Set<T> protein = new HashSet<>();
    List<Expression<T>> variables = and.getChildren();
    for (Expression<T> variable : variables) {
      if (variable.getExprType().equals("variable")) {
        protein.addAll(getProtein((Variable<T>)variable));
      } else {
        throw new IllegalArgumentException("expected and of variables");
      }
    }
    return protein;
  }
  
  public static<T> Set<Set<T>> getProtein(Or<T> or) {
    Set<Set<T>> protein = new HashSet<>();
    List<Expression<T>> expressions = or.getChildren();
    for (Expression<T> expression : expressions) {
      switch (expression.getExprType()) {
      case "variable":
        protein.add(new HashSet<>(getProtein((Variable<T>)expression)));
        break;
      case "and":
        protein.add(new HashSet<>(getProtein((And<T>)expression)));
        break;
      default:
        throw new IllegalArgumentException("expected expression of and or variable: " + or.toString());
      }
    }

    return protein;
  }
  
  public static String toLexicographicString(String gprExpression) {
    String expression = gprExpression.replaceAll(" and ", "&");
    expression = expression.replaceAll(" AND ", "&");
    expression = expression.replaceAll(" or ", "|");
    expression = expression.replaceAll(" OR ", "|");
    expression = expression.replaceAll("-", "_");
    Expression<String> nonStandard = ExprParser.parse(expression);
    Expression<String> dnf = RuleSet.toDNF(nonStandard);
    
    return dnf.toLexicographicString();
  }
  
  public static<T> Set<Set<T>> translate(Set<Set<T>> proteins, Map<T, T> t) {
    Set<Set<T>> result = new HashSet<>();
    for (Set<T> p : proteins) {
      Set<T> protein = new HashSet<>();
      for (T g : p) {
        if (t.containsKey(g)) {
          protein.add(t.get(g));
        } else {
          protein.add(g);
        }
      }
      result.add(protein);
    }
    
    return result;
  }
  
  public static Set<Set<String>> getProteins(String gprExpression) {
//    MathUtils mathUtils = new MathUtils();
    Set<String> variables = getVariables(gprExpression);
    
    logger.debug("{} Variables. {}", variables.size(), variables);
    
    Map<String, Long> geneToIndex = new HashMap<>();
    Set<Long> taken = new HashSet<>();
    Map<String, String> indexToGene = new HashMap<>();
    
    for (String g : variables) {
      if (StringUtils.isNumeric(g)) {
        taken.add(Long.parseLong(g));
      }
    }
    
    logger.debug("{} Numeric Variables. {}", taken.size(), taken);
    
    
    Long i = 0L;
    for (String g : variables) {
      if (!StringUtils.isNumeric(g)) {
        while (taken.contains(i)) {
          logger.debug("Skip {}", i);
          i++;
        }
        taken.add(i);
        logger.debug("Assign {} -> {}", i, g);
        indexToGene.put(i.toString(), g);
        geneToIndex.put(g, i++);
      } else {
        taken.add(Long.parseLong(g));
      }
    }
    
    String gprTranslated = gprExpression;
    for (String gene : geneToIndex.keySet()) {
      while (gprTranslated.contains(gene)) {
        gprTranslated = gprTranslated.replace(gene, geneToIndex.get(gene).toString());
      }
    }
    
    logger.debug("Translated GPR: {}", gprTranslated);
    
    Set<Set<String>> proteins = getProteinsSimple(gprTranslated);
    
    return translate(proteins, indexToGene);
  }
  
  public static boolean isCNF(Expression<String> expr) {
    String str = expr.toString();
    
    //dumb method
    if (str.startsWith("((") && str.endsWith("))")) {
      String b[] = str.split(" & ");
      for (String block : b) {
        if (block.contains("&")) {
          return false;
        }
      }
    }
    
    return true;
  }
  
  public static Expression<String> fromCNFtoDNF(Expression<String> expr) {
    String str = expr.toString();
    List<Set<String>> andOfOrs = new ArrayList<> ();
    if (isCNF(expr)) {
      if (str.startsWith("((") && str.endsWith("))")) {
        str = str.substring(1, str.length() - 1);
        String b[] = str.split(" & ");
        for (String block : b) {
          Set<String> var = getVariables(block);
          andOfOrs.add(var);
        }
      }
    }
    
    Set<List<String>> dnf = Sets.cartesianProduct(andOfOrs);
    
    if (dnf.size() > 100) {
      throw new IllegalArgumentException("Too many combinations: " + dnf.size());
    }
    Set<String> ors = new HashSet<>();
    for (List<String> ands : dnf) {
      ors.add("(" + StringUtils.join(ands, " & ") + ")");
    }
    
    String gpr = StringUtils.join(ors, " | ");

    return ExprParser.parse(gpr);
  }
  
  public static Expression<String> getExpression(String str) {
    return ExprParser.parse(str);
  }
  
  public static String toSymbols(String gprExpression) {
    String expression = gprExpression.replaceAll(" and ", "&");
    expression = expression.replaceAll(" AND ", "&");
    expression = expression.replaceAll(" or ", "|");
    expression = expression.replaceAll(" OR ", "|");
    return expression;
  }
  
  public static Set<Set<String>> getProteinsSimple(String gprExpression) {
    Set<Set<String>> proteins = new HashSet<>();
    String expression = gprExpression.replaceAll(" and ", "&");
    expression = expression.replaceAll(" AND ", "&");
    expression = expression.replaceAll(" or ", "|");
    expression = expression.replaceAll(" OR ", "|");
//    expression = expression.replaceAll("-", "_");
    
    logger.trace("Parse: {}", expression);
    
    Expression<String> nonStandard = ExprParser.parse(expression);
    if (isCNF(nonStandard)) {
      
    }
    logger.trace("To DNF ... {}", nonStandard);
    
    Expression<String> dnf = RuleSet.toDNF(nonStandard);
    
    logger.trace("DNF: {}", dnf);
    
    Set<String> protein = null;
    switch (dnf.getExprType()) {
      case "variable":
        protein = getProtein((Variable<String>) dnf);
        proteins.add(new HashSet<>(protein));
        break;
      case "or":
        Set<Set<String>> prts = getProtein((Or<String>) dnf);
        proteins.addAll(new HashSet<>(prts));
        break;
      case "and":
        protein = getProtein((And<String>) dnf);
        proteins.add(new HashSet<>(protein));
        break;
      default:
        logger.warn("{}", dnf.getExprType());
        break;
    }
    
    return proteins;
  }
  
  public static Set<String> getVariables(String gprExpression) {
    Expression<String> expr = ExprParser.parse(toSymbols(gprExpression));
    Set<String> variables = ExprUtil.getVariables(expr);
    return variables;
  }
  
  public static Set<String> getGenes(String gprExpression) {
    return getVariables(gprExpression);
  }
}