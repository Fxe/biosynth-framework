package pt.uminho.sysbio.biosynthframework.optimization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.core.components.optimization.Solution;
import pt.uminho.sysbio.biosynthframework.core.components.optimization.SolutionSet;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.pgraph.OperatingUnit;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.pgraph.ProcessGraph;

public class RadiusSearch {

  public static final Logger logger = LoggerFactory.getLogger(RadiusSearch.class);

  public static String cleanWASD(String lol)  {
    if ( lol.charAt(lol.length() - 1) == 'R') {
      return lol.substring(0, lol.length() - 1);
    }
    return lol;
  }

  public boolean mapMetaboliteToReaction(String cpdId, String rxnId, Map<String, Set<String>> solutionMap) {
    if ( solutionMap.containsKey(cpdId)) {
      return solutionMap.get(cpdId).add(rxnId);
    } else {
      Set<String> rxnIdSet = new HashSet<String> ();
      rxnIdSet.add(rxnId);
      solutionMap.put(cpdId, rxnIdSet);
    }
    return true;
  }

  protected Set<String> S;
  protected Set<String> T;
  protected Set<OperatingUnit<String>> O;

  public Set<String> forbiddenCpds = new HashSet<>();
  
  protected Set<String> M;
  protected Set<String> B;

  private Set<OperatingUnit<String>> I;
  private int level;

  private int radius;
  private Solution solution;

  public RadiusSearch(ProcessGraph<String> graph, Set<String> sources, Set<String> targets, int radius) {
    this.S = sources;
    this.T = targets;
    this.O = graph.getO();

    this.I = new HashSet<OperatingUnit<String>> ();
    this.M = new HashSet<String> ();
    this.B = new HashSet<String> (T);
    this.radius = radius;
  }

  public static Set<String> expand(ProcessGraph<String> graph, Set<String> reations, Set<String> sources, Set<String> targets) {


    return null;
  }

  public RadiusSearch(ProcessGraph<String> graph, String[] sources,  String[] targets, int radius) {
    this.S = new HashSet<> (java.util.Arrays.asList(sources));
    this.T = new HashSet<> (java.util.Arrays.asList(targets));
    this.O = graph.getO();

    this.I = new HashSet<OperatingUnit<String>> ();
    this.M = new HashSet<String> ();
    this.B = new HashSet<String> (T);
    this.radius = radius;
  }

  //    @Override
  public void solve() {
    this.level = 0;
    
    while ( level < radius) {
      logger.debug("Level {}, B:{}", level, B.size());
      this.move();
    }
    //      this.getSolution();
  }

  public Solution getSolution() {
    this.solution.setProperty("head", S);
    this.solution.setProperty("tail", T);
    this.solution.setProperty("id", 0);
    Map<String, Set<String>> solutionMap = new HashMap<> ();
    for (OperatingUnit<String> o : I) {
      mapMetaboliteToReaction("OUTPUT", cleanWASD(o.getID()), solutionMap);
    }

    this.solution.setProperty("solution", solutionMap);

    return this.solution;
  }

  public int getLevel() {
    return this.level;
  }

  public Set<String> getReactions() {
    Set<String> rxnIdSet = new HashSet<String> ();
    for (OperatingUnit<String> o : I) {
      rxnIdSet.add(o.getEntry());
    }
    return rxnIdSet;
  }
  
  public int overExpansionSize = 50;

  public void move() {
    level++;
    forbiddenCpds.add("C00126");
    forbiddenCpds.add("C00138"); //Reduced ferredoxin;
    forbiddenCpds.add("META:Quinones");
    forbiddenCpds.add("META:Quinones");
    forbiddenCpds.add("META:ETR-Quinones");
    forbiddenCpds.add("META:ETR-Quinols");
    forbiddenCpds.add("META:Oxidized-ferredoxins");
    forbiddenCpds.add("META:NADH-P-OR-NOP");
    forbiddenCpds.add("META:NAD-P-OR-NOP");
    forbiddenCpds.add("nh4");
    forbiddenCpds.add("co");
    forbiddenCpds.add("C00028"); //Acceptor
    forbiddenCpds.add("C00030"); //Red
    forbiddenCpds.add("META:Acceptor"); //C00028 Acceptor
    forbiddenCpds.add("META:Donor-H2"); //C00030 Red
    forbiddenCpds.add("C00017"); //Protein
    forbiddenCpds.add("G10619"); //UDP
    forbiddenCpds.add("C03024"); //Reduced flavoprotein
    forbiddenCpds.add("fdxrd"); //Reduced ferredoxin
    forbiddenCpds.add("fdxox"); //Oxidized ferredoxin
    forbiddenCpds.add("META:Ox-NADPH-Hemoprotein-Reductases");
    forbiddenCpds.add("META:Red-NADPH-Hemoprotein-Reductases");
    forbiddenCpds.add("META:Oxidized-Flavoproteins");
    forbiddenCpds.add("META:FERRICYTOCHROME-B5");
    forbiddenCpds.add("META:FERROCYTOCHROME-B5");
    forbiddenCpds.add("e<SUP>-</SUP>"); //Red
    
    B.removeAll(forbiddenCpds);
    
    logger.debug("Reactions: {} - M: {}", O.size(), B);
    Set< OperatingUnit<String>> reactions = SetMap.phi_minus(B, O);
//    for (String b : B) {
//      Set<String> BB = new HashSet<>();
//      BB.add(b);
//      System.out.println(b + "\t" + SetMap.phi_minus(BB, O).size());
//    }
    logger.debug("phi_minus B, O: {}, {} = {}", B.size(), O.size(), reactions.size());
    if (reactions.size() > overExpansionSize) {
      logger.warn("Over expansion {} > {} {}", reactions.size(), overExpansionSize, B);
      for (String b : B) {
        Set<String> B_ = new HashSet<> ();
        B_.add(b);
        Set< OperatingUnit<String>> exp = SetMap.phi_minus(B_, O);
//        if ()
        if (exp.size() > overExpansionSize) {
          logger.warn("{} -> {}", b, exp.size());
        } else {
          logger.debug("{} -> {}", b, exp.size());
        }
      }
    }
    reactions.removeAll(I);
    //      System.out.println(reactions.size());
    logger.debug("Included {} reactions", reactions.size());
    Set<String> cpdnew = SetMap.psi_minus(reactions);

    logger.debug("Included {} compounds", cpdnew.size());

    M.addAll(B);
    B.clear();
    B.addAll(cpdnew);
    B.removeAll(S);
    B.removeAll(M);

    //      System.out.println("B:" + B);

    I.addAll(reactions);
  }

  //    @Override
  public SolutionSet<Solution> getSolutionSet(SolutionSet<Solution> solutionSet) {
    //      SolutionSet<Solution> solutionSet = 
    //              new SolutionSetNew(BioSynthUtils.getUUID(), "Neighbour Search --radius " + this.radius, 1);
    //      solutionSet.add(solution);
    //      solutionSet.addSolution(solution.getId(), solution);
    solutionSet.add(solution);
    return solutionSet;
  }

  //    @Override
  public Map<Integer, Solution> getSolutionMap() {
    Map<Integer, Solution> singleTonMap = new HashMap<Integer, Solution> ();
    singleTonMap.put(0, solution);
    return singleTonMap;
  }

  //    @Override
  public void setSolutionMap(Map<Integer, Solution> map) {
    System.err.println("NO EFFECT!");
  }
}

