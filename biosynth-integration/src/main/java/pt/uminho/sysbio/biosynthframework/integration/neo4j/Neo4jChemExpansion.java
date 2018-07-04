package pt.uminho.sysbio.biosynthframework.integration.neo4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbPropertyNode;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.cheminformatics.CheminformaticsModule;
import pt.uminho.sysbio.biosynthframework.cheminformatics.DefaultCheminformaticsModule;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;
import pt.uminho.sysbio.biosynthframework.util.KeggUtils;

public class Neo4jChemExpansion {

  private static final Logger logger = LoggerFactory.getLogger(Neo4jChemExpansion.class);

  private final BiodbGraphDatabaseService graphDatabaseService;
  private final CheminformaticsModule cm;


  public Map<Long, String> existingFormulas = new HashMap<> ();
  public Map<Long, String> newFormulas = new HashMap<> ();
  public Map<Long, String> existingKeys = new HashMap<> ();
  public Map<Long, String> newKeys = new HashMap<> ();
  public Map<Long, String> existingInchis = new HashMap<> ();
  public Map<Long, String> newInchis = new HashMap<> ();
  public Set<Long> a = new HashSet<>();
  public Set<Long> e = new HashSet<>();
  
  public Neo4jChemExpansion(CheminformaticsModule cm, BiodbGraphDatabaseService service) {
    this.graphDatabaseService = service;
    this.cm = cm;
  }

  private BiodbPropertyNode connectProperty(BiodbPropertyNode sourceProperty, String property, 
      Set<BiodbMetaboliteNode> cpdNodes, 
      MetabolitePropertyLabel propertyType, 
      MetaboliteRelationshipType relationshipType, 
      MetabolitePropertyLabel...extraLabels) {
    
    
    
    if (property != null && !property.trim().isEmpty()) {
      property = property.trim();
      BiodbPropertyNode propertyNode = graphDatabaseService.getMetaboliteProperty(property, propertyType);
      if (propertyNode == null) {
        propertyNode = graphDatabaseService.getOrCreateMetaboliteProperty(property, propertyType);
        for (MetabolitePropertyLabel l : extraLabels) {
          propertyNode.addLabel(l);
        }
        a.add(propertyNode.getId());
      } else {
        e.add(propertyNode.getId());
      }

      if (sourceProperty != null) {
        logger.debug("{} -[{}]-> {}", sourceProperty, relationshipType, propertyNode);
        Relationship r = sourceProperty.connectToProperty(propertyNode, relationshipType);
        r.setProperty("source", "inferred");
      }

      if (cpdNodes != null) {
        for (BiodbMetaboliteNode cpdNode : cpdNodes) {
          logger.debug("{} -[{}]-> {}", cpdNode, relationshipType, propertyNode);
          Relationship r = cpdNode.addMetaboliteProperty(propertyNode, relationshipType);
          r.setProperty("source", "inferred");
          Neo4jUtils.setUpdatedTimestamp(r);
        }
      }

      return propertyNode;
    }

    return null;
  }

  protected BiodbPropertyNode connectFormula(BiodbPropertyNode sourceProperty, String formula, Set<BiodbMetaboliteNode> cpdNodes) {
    return connectProperty(sourceProperty, formula, cpdNodes, 
        MetabolitePropertyLabel.MolecularFormula, MetaboliteRelationshipType.has_molecular_formula);
  }

  protected BiodbPropertyNode connectInchi(BiodbPropertyNode sourceProperty, String inchi, Set<BiodbMetaboliteNode> cpdNodes) {
    return connectProperty(sourceProperty, inchi, cpdNodes, 
        MetabolitePropertyLabel.InChI, MetaboliteRelationshipType.has_inchi);
  }

  protected BiodbPropertyNode connectInchikey(BiodbPropertyNode sourceProperty, String inchikey, Set<BiodbMetaboliteNode> cpdNodes) {
    return connectProperty(sourceProperty, inchikey, cpdNodes, 
        MetabolitePropertyLabel.InChIKey, MetaboliteRelationshipType.has_inchikey);
  }

  protected BiodbPropertyNode connectSmiles(BiodbPropertyNode sourceProperty, String smiles, Set<BiodbMetaboliteNode> cpdNodes) {
    return connectProperty(sourceProperty, smiles, cpdNodes, 
        MetabolitePropertyLabel.SMILES, MetaboliteRelationshipType.has_smiles);
  }

  protected BiodbPropertyNode connectUniversalSmiles(BiodbPropertyNode sourceProperty, String universalSmiles, Set<BiodbMetaboliteNode> cpdNodes) {
    return connectProperty(sourceProperty, universalSmiles, cpdNodes, 
        MetabolitePropertyLabel.SMILES, MetaboliteRelationshipType.has_smiles, MetabolitePropertyLabel.UniversalSMILES);
  }

  public void expandKeggGlycanComposition() {
    for (BiodbMetaboliteNode cpdNode : graphDatabaseService.listMetabolites(MetaboliteMajorLabel.LigandGlycan)) {
      this.expandKeggGlycanComposition(cpdNode);
    }
  }
  
  public void expandKeggGlycanComposition(BiodbMetaboliteNode glNode) {
    if (glNode.hasProperty("composition")) {
      Map<String, Integer> comp = KeggUtils.parseComposition(glNode.getProperty("composition").toString());
      //      System.out.println(glNode.getProperty("entry") + " -> " + comp);

      if (comp != null) {
        Map<String, Integer> sum = new HashMap<> ();
        for (String cpdEntry : comp.keySet()) {
          Integer multiplier = comp.get(cpdEntry);
          Node cpdNode = graphDatabaseService.getMetabolite(
              cpdEntry, MetaboliteMajorLabel.LigandCompound);
          if (cpdNode == null) {
            logger.warn("{} not found", cpdEntry);
            return;
          }
          Set<String> formulas = new HashSet<> ();
          for (Relationship r : cpdNode.getRelationships(MetaboliteRelationshipType.has_molecular_formula)) {
            Node formulaNode = r.getOtherNode(cpdNode);
            String source = (String) r.getProperty("source", "resource");
            if ("resource".equals(source)) {
              formulas.add((String) formulaNode.getProperty(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT));
            }
          }
          Map<String, Integer> atomCount = cm.resolveFormula(formulas);
          if (atomCount == null) {
            logger.warn("{} unable to get atom count", cpdEntry);
            return;
          }

          for (String atom : atomCount.keySet()) {
            CollectionUtils.increaseCount(sum, atom, atomCount.get(atom) * multiplier);
          }
        }

        String formula = DefaultCheminformaticsModule.getFormulaFromAtomMap(sum);
        Set<BiodbMetaboliteNode> cpdNodes = new HashSet<> ();
        cpdNodes.add(glNode);
        if (formula != null) {
          connectFormula(null, formula, cpdNodes);
        }
      }

    }
  }

  public void expandInchi() {
    for (BiodbPropertyNode propNode : graphDatabaseService.listMetaboliteProperties(MetabolitePropertyLabel.InChI)) {
      this.expandInchi(propNode);
    }
  }
  
  public Set<BiodbPropertyNode> expandInchi(BiodbPropertyNode inchiNode) {
    Set<BiodbPropertyNode> added = new HashSet<> ();
    String k = inchiNode.getValue();

    Set<BiodbMetaboliteNode> cpdNodes = inchiNode.getMetabolites(MetaboliteRelationshipType.has_inchi);

    String inchiKey = cm.inchiToInchiKey(k);
    //      String can = null;;
    String smi = cm.inchiToSmiles(k);
    Map<String, Integer> atomCount = cm.inchiToAtomMap(k);
    String formula= cm.atomMapToFormula(atomCount);

    //      if (can != null) {
    //        BiodbPropertyNode node = connectUniversalSmiles(inchiNode, can, cpdNodes);
    //        if (node != null) {
    //          added.add(node);
    //        }
    //      }

    if (smi != null) {
      BiodbPropertyNode node = connectSmiles(inchiNode, smi, cpdNodes);
      if (node != null) {
        added.add(node);
      }
    }

    if (formula != null) {
      BiodbPropertyNode node = connectFormula(inchiNode, formula, cpdNodes);
      if (node != null) {
        added.add(node);
      }
    }

    if (inchiKey != null) {
      BiodbPropertyNode node = connectInchikey(inchiNode, inchiKey, cpdNodes);
      if (node != null) {
        added.add(node);
      }
    }


    //      CdkWrapper.c
    //      System.out.println(k + "\t" + inchiKey + "\t" + formula + "\t"+ "ok!");

    return added;
  }

  public void expandSmiles() {
    for (BiodbPropertyNode propNode : graphDatabaseService.listMetaboliteProperties(MetabolitePropertyLabel.SMILES)) {
      this.expandSmiles(propNode);
    }
  }
  
  public Set<BiodbPropertyNode> expandSmiles(BiodbPropertyNode smiNode) {
    if (!smiNode.hasLabel(MetabolitePropertyLabel.SMILES)) {
      logger.warn("expected SMILES node, found: {}", Neo4jUtils.getLabelsAsString(smiNode));
      return null;
    }
    Set<BiodbPropertyNode> added = new HashSet<> ();
    String k = smiNode.getValue();
    Set<BiodbMetaboliteNode> cpdNodes = smiNode.getMetabolites(MetaboliteRelationshipType.has_smiles);

    try {
      String can = cm.smilesToCannonical(k);
      Map<String, Integer> atomCount = cm.smilesToAtomMap(k);
      String formula= cm.atomMapToFormula(atomCount);
      String inchi = cm.smilesToInchi(k);
      //      String inchiKey = ig.getInchiKey();

      if (k.equals(can) && !smiNode.hasLabel(MetabolitePropertyLabel.UniversalSMILES)) {
        smiNode.addLabel(MetabolitePropertyLabel.UniversalSMILES);
      } else {
        BiodbPropertyNode node = connectUniversalSmiles(smiNode, can, cpdNodes);
        if (node != null) {
          added.add(node);
        }
      }



      if (formula != null) {
        BiodbPropertyNode node = connectFormula(smiNode, formula, cpdNodes);
        if (node != null) {
          added.add(node);
        }
      }

      //      if (inchiKey != null) {
      //        Node node = connectInchikey(smiNode, inchiKey, cpdNodes);
      //        if (node != null) {
      //          added.add(node);
      //        }
      //      }

      if (inchi != null) {
        BiodbPropertyNode node = connectInchi(smiNode, inchi, cpdNodes);
        if (node != null) {
          added.add(node);
        }
      }  

    } catch ( IllegalArgumentException e) {
      smiNode.setProperty("cdk-error", e.getMessage());
      e.printStackTrace();
    }

    return added;
  }

  public void expandMol() {
    for (BiodbPropertyNode propNode : graphDatabaseService.listMetaboliteProperties(MetabolitePropertyLabel.MDLMolFile)) {
      this.expandMol(propNode);
    }
  }
  
  public Set<BiodbPropertyNode> expandMol(BiodbPropertyNode molNode) {
    if (!molNode.hasLabel(MetabolitePropertyLabel.MDLMolFile)) {
      logger.warn("expected MDLMolFile node, found: {}", Neo4jUtils.getLabelsAsString(molNode));
      return null;
    }
    Set<BiodbPropertyNode> added = new HashSet<> ();
    String k = molNode.getValue();
    if (k.startsWith("ERROR_")) {
      logger.warn("unable to read property value: {}", molNode);
      return added;
    }
    Set<BiodbMetaboliteNode> cpdNodes = molNode.getMetabolites(MetaboliteRelationshipType.has_mdl_mol_file);

    String formula = null;
    String can = cm.molToSmiles(k);
    String inchi = cm.molToInchi(k);

    if (!k.contains(" R ")) {
      Map<String, Integer> atomCount = cm.molToAtomMap(k);
      formula = cm.atomMapToFormula(atomCount);
    }

    if (formula != null) {
      BiodbPropertyNode node = connectFormula(molNode, formula, cpdNodes);
      if (node != null) {
        added.add(node);
      }
    }
    if (can != null) {
      BiodbPropertyNode node = connectUniversalSmiles(molNode, can, cpdNodes);
      if (node != null) {
        added.add(node);
      }
    }
    if (inchi != null) {
      BiodbPropertyNode node = connectInchi(molNode, inchi, cpdNodes);
      if (node != null) {
        added.add(node);
      }
    }
    //      if (inchiKey != null) {
    //        Node node = connectInchikey(molNode, inchiKey, cpdNodes);
    //        if (node != null) {
    //          added.add(node);
    //        }
    //      }

    return added;
  }

  public void expandName() {
    for (BiodbPropertyNode propNode : graphDatabaseService.listMetaboliteProperties(MetabolitePropertyLabel.Name)) {
      this.expandName(propNode);
    }
  }
  
  public Set<BiodbPropertyNode> expandName(BiodbPropertyNode nameNode) {
    Set<BiodbPropertyNode> added = new HashSet<> ();
    String k = nameNode.getValue();
    Set<String> values = new HashSet<> ();
    for (Relationship r : nameNode.getRelationships(MetaboliteRelationshipType.has_name)) {
      Node cpdNode = r.getOtherNode(nameNode);
      if (cpdNode.hasLabel(GlobalLabel.Metabolite)) {
        if (r.hasProperty("DCS-original")) {
          values.add(r.getProperty("DCS-original").toString());
        } else {
          values.add(k);
        }
      }
    }

    for (String name : values) {
      String inchi = cm.nameToInchi(name);

      Set<BiodbMetaboliteNode> cpdNodes = nameNode.getMetabolites(MetaboliteRelationshipType.has_name);
      //      NameToInchi n = new NameToInchi();
      //      n.parseToStdInchi(name)

      if (inchi != null) {
        if (!nameNode.hasLabel(MetabolitePropertyLabel.OpsinName)) {
          nameNode.addLabel(MetabolitePropertyLabel.OpsinName);
          a.add(nameNode.getId());
        }

        BiodbPropertyNode inchiNode = connectInchi(nameNode, inchi, cpdNodes);
        if (inchiNode != null) {
          added.add(inchiNode);
        }
      }
    }

    return added;
  }

  public void expandInchikey() {
    for (BiodbPropertyNode propNode : graphDatabaseService.listMetaboliteProperties(MetabolitePropertyLabel.InChIKey)) {
      this.expandInchikey(propNode);
    }
  }
  
  public Set<BiodbPropertyNode> expandInchikey(BiodbPropertyNode inchikeyNode) {
    if (!inchikeyNode.hasLabel(MetabolitePropertyLabel.InChIKey)) {
      logger.warn("expected InChIKey node, found: {}", Neo4jUtils.getLabelsAsString(inchikeyNode));
      return null;
    }
    Set<BiodbPropertyNode> added = new HashSet<> ();

    String k = (String) inchikeyNode.getProperty("key");
    if (k != null && k.length() == 27 && 
        StringUtils.countMatches(k, '-') == 2) {
      String[] p = k.split("-");
      String fihb = p[0];
      String sihb = p[1];
      String prot = p[2];
      {
        BiodbPropertyNode node = connectProperty(inchikeyNode, 
            fihb, 
            null, //compounds to connect
            MetabolitePropertyLabel.FIKHB, 
            MetaboliteRelationshipType.has_inchikey_fikhb);
        if (node != null) {
          added.add(node);
        }
      }
      {
        BiodbPropertyNode node = connectProperty(inchikeyNode, 
            sihb, 
            null, //compounds to connect
            MetabolitePropertyLabel.SIKHB, 
            MetaboliteRelationshipType.has_inchikey_sikhb);
        if (node != null) {
          added.add(node);
        }
      }
      inchikeyNode.setProperty("protonation", prot);
      Neo4jUtils.setUpdatedTimestamp(inchikeyNode);
    }

    return added;
  }

  public Set<BiodbPropertyNode> expandProperty(BiodbPropertyNode propNode) {
    Set<BiodbPropertyNode> expanded = new HashSet<> ();
    if (propNode.hasLabel(GlobalLabel.MetaboliteProperty)) {
      String ml = (String) propNode.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY);
      MetabolitePropertyLabel property = MetabolitePropertyLabel.valueOf(ml);
      switch (property) {
        case Name:
          {
            Set<BiodbPropertyNode> exp = expandName(propNode);
            expanded.addAll(exp);
          }
          break;
        case InChI:
          {
            Set<BiodbPropertyNode> exp = expandInchi(propNode);
            expanded.addAll(exp);
          }
          break;
        case SMILES:
          {
            Set<BiodbPropertyNode> exp = expandSmiles(propNode);
            expanded.addAll(exp);
          }
          break;
        case MDLMolFile:
          {
            Set<BiodbPropertyNode> exp = expandMol(propNode);
            expanded.addAll(exp);
          }
          break;
        case MolecularFormula:
          break;
        case InChIKey:
          {
            Set<BiodbPropertyNode> exp = expandInchikey(propNode);
            expanded.addAll(exp);
          }
          break;
      default:
        logger.warn("[SKIP] {}", property);
        break;
      }
    }

    return expanded;
  }

  int maxIt = 10;
  public Set<Node> visitedCache = new HashSet<> ();

  public void expandProperties(Set<BiodbPropertyNode> nodes, Set<Node> visited, int iteration) {
    logger.debug("[ITERATION:{}] exp: {}, visit: {}", iteration, nodes.size(), visited.size());

    if (nodes == null || nodes.isEmpty() || iteration >= maxIt) {
      return;
    }

    Set<BiodbPropertyNode> next = new HashSet<> ();
    for (BiodbPropertyNode node : nodes) {
      if (!visited.contains(node)) {
        next.addAll(expandProperty(node));
        visited.add(node);
      }
    }

    expandProperties(next, visited, ++iteration);
  }
}
