package pt.uminho.sysbio.biosynthframework.util.math;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.parsers.ExprParser;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.fathzer.soft.javaluator.AbstractEvaluator;
import com.fathzer.soft.javaluator.BracketPair;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Parameters;

public class MathUtils {
  public String toDnf(String expression) {
    Expression<String> expr = ExprParser.parse(expression);
    Expression<String> dnfExpr = RuleSet.toDNF(expr);
    return dnfExpr.toLexicographicString();
  }
  
  public static class Eval extends AbstractEvaluator<String> {

    public Set<String> variables = new HashSet<>();
    
    protected Eval(Parameters arg0) {
      super(arg0);
    }
    
    @Override
    protected String evaluate(Operator operator, Iterator<String> operands, Object evaluationContext) {
      return operands.next();
    }

    @Override
    protected String toValue(String arg0, Object arg1) {
//      System.out.println(arg0 + " " + arg1);
      variables.add(arg0);
      return arg0;
    }
  }
  
  public Set<String> getVariables(String expression) {
    Operator and1 = new Operator("and", 2, Operator.Associativity.LEFT, 2);
    Operator and2 = new Operator("&", 2, Operator.Associativity.LEFT, 2);
    Operator or1 = new Operator("or", 2, Operator.Associativity.LEFT, 1);
    Operator or2 = new Operator("|", 2, Operator.Associativity.LEFT, 2);
    
    Parameters parameters = new Parameters();
    parameters.add(or1);
    parameters.add(or2);
    parameters.add(and1);
    parameters.add(and2);
    parameters.addExpressionBracket(BracketPair.PARENTHESES);
    Eval e = new Eval(parameters);
    e.evaluate(expression);
    
    return new HashSet<>(e.variables);
  }
}
