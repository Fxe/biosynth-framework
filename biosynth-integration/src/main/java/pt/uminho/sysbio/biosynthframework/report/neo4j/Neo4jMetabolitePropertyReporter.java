package pt.uminho.sysbio.biosynthframework.report.neo4j;

import java.util.HashMap;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynthframework.Dataset;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

public class Neo4jMetabolitePropertyReporter extends AbstractNeo4jReporter {

  public Neo4jMetabolitePropertyReporter(GraphDatabaseService service) {
    super(service);
  }
  
  public static enum P{
    INCHI_SOURCE, INCHI_COMP,
    IUPAC_NAME_SOURCE, IUPAC_NAME_COMP,
    TRAD_NAME_SOURCE, TRAD_NAME_COMP,
    NAME_SOURCE, NAME_COMP,
    UNIV_SMILES_SOURCE, UNIV_SMILES_COMP,
    SMILES_SOURCE, SMILES_COMP,
    FORMULA_SOURCE, FORMULA_COMP,
  }
  
  public static<T, K> void count(T db, K p, Dataset<T, K, Integer> a) {
    if (!a.dataset.containsKey(db)) {
      a.dataset.put(db, new HashMap<K, Integer>());
    }
    CollectionUtils.increaseCount(a.dataset.get(db), p, 1);
  }

  public void report() {
    Dataset<MetaboliteMajorLabel, P, Integer> a = new Dataset<>();
    
    Dataset<MetaboliteMajorLabel, String, Integer> b = new Dataset<>();
    
    for (Node cpdNode : service.getAllNodes()) {
      Set<String> labels = Neo4jUtils.getLabelsAsString(cpdNode);
      if (cpdNode.hasLabel(GlobalLabel.Metabolite) && cpdNode.getProperty(Neo4jDefinitions.PROXY_PROPERTY).equals(false)) {
        String ml = (String) cpdNode.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY);
        MetaboliteMajorLabel db = MetaboliteMajorLabel.valueOf(ml);
        boolean hasoInchi= false;
        boolean hascInchi= false;
        boolean hasNameIupac = false;
        boolean hasName = false;
        boolean hascSmiles = false;
        boolean hasoSmiles = false;
        boolean hasoFormula = false;
        boolean hascFormula = false;

        for (Relationship r : cpdNode.getRelationships(MetaboliteRelationshipType.has_inchi)) {
          String source = (String) r.getProperty("source", "original");
          if (source.equals("original")) {
            hasoInchi = true;
            count(db, P.INCHI_SOURCE, a);
          } else if (source.equals("inferred")) {
            hascInchi = true;
            count(db, P.INCHI_COMP, a);
          }
        }

        for (Relationship r : cpdNode.getRelationships(MetaboliteRelationshipType.has_name)) {
          Node nameNode = r.getOtherNode(cpdNode);
          if (nameNode.hasLabel(MetabolitePropertyLabel.IUPACName)) {
            hasNameIupac = true;
            String source = (String) r.getProperty("source", "original");
            if (source.equals("original")) {
              count(db, P.IUPAC_NAME_SOURCE, a);
            } else if (source.equals("inferred")) {
              count(db, P.IUPAC_NAME_COMP, a);
            }
          } else {
            hasName = true;
            String source = (String) r.getProperty("source", "original");
            if (source.equals("original")) {
              count(db, P.NAME_SOURCE, a);
            } else if (source.equals("inferred")) {
              count(db, P.NAME_COMP, a);
            }
          }
        }

        for (Relationship r : cpdNode.getRelationships(MetaboliteRelationshipType.has_smiles)) {
          Node nameNode = r.getOtherNode(cpdNode);
          if (nameNode.hasLabel(MetabolitePropertyLabel.UniversalSMILES)) {
            String source = (String) r.getProperty("source", "original");
            if (source.equals("original")) {
              count(db, P.UNIV_SMILES_SOURCE, a);
              hasoSmiles = true;
            } else if (source.equals("inferred")) {
              count(db, P.UNIV_SMILES_COMP, a);
              hascSmiles = true;
            }
          } else {
            String source = (String) r.getProperty("source", "original");
            if (source.equals("original")) {
              count(db, P.SMILES_SOURCE, a);
              hasoSmiles = true;
            } else if (source.equals("inferred")) {
              count(db, P.SMILES_COMP, a);
              hascSmiles = true;
            }
          }
        }

        for (Relationship r : cpdNode.getRelationships(MetaboliteRelationshipType.has_molecular_formula)) {
          String source = (String) r.getProperty("source", "original");
          if (source.equals("original")) {
            count(db, P.FORMULA_SOURCE, a);
            hasoFormula = true;
          } else if (source.equals("inferred")) {
            count(db, P.FORMULA_COMP, a);
            hascFormula = true;
          }
        }

        if (hasoFormula && hascFormula) {
          count(db, "FORMULA_BOTH", b);
        } else if (hascFormula) {
          count(db, "FORMULA_COMP", b);
        } else if (hasoFormula) {
          count(db, "FORMULA_ORIG", b);
        } else {
          count(db, "FORMULA_NONE", b);
        }

        if (hascInchi && hasoInchi) {
          count(db, "INCHI_BOTH", b);
        } else if (hascInchi) {
          count(db, "INCHI_COMP", b);
        } else if (hasoInchi) {
          count(db, "INCHI_ORIG", b);
        } else {
          count(db, "INCHI_NONE", b);
        }

        if (hasName && hasNameIupac) {
          count(db, "NAME_BOTH", b);
        } else if (hasName) {
          count(db, "NAME_NORM", b);
        } else if (hasNameIupac) {
          count(db, "NAME_IUPC", b);
        } else {
          count(db, "NAME_NONE", b);
        }

        if (hascSmiles && hasoSmiles) {
          count(db, "SMILES_BOTH", b);
        } else if (hascSmiles) {
          count(db, "SMILES_COMP", b);
        } else if (hasoSmiles) {
          count(db, "SMILES_ORIG", b);
        } else {
          count(db, "SMILES_NONE", b);
        }
      }
    }
  }
}
