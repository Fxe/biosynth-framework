package pt.uminho.sysbio.biosynthframework.integration.assembler;

import java.io.File;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.integration.assembler.database.BiggMetaboliteAssemblePlugin;
import pt.uminho.sysbio.biosynthframework.integration.assembler.database.BiggReactionAssemblePlugin;
import pt.uminho.sysbio.biosynthframework.integration.assembler.database.BiocycMetaboliteAssemblePlugin;
import pt.uminho.sysbio.biosynthframework.integration.assembler.database.BiocycReactionAssemblePlugin;
import pt.uminho.sysbio.biosynthframework.integration.assembler.database.HmdbAssemblePlugin;
import pt.uminho.sysbio.biosynthframework.integration.assembler.database.KeggReactionAssemblePlugin;
import pt.uminho.sysbio.biosynthframework.integration.assembler.database.LipidmapsAssemblePlugin;
import pt.uminho.sysbio.biosynthframework.integration.assembler.database.ModelseedMetaboliteAssemblePlugin;
import pt.uminho.sysbio.biosynthframework.integration.model.ConnectedComponents;
import pt.uminho.sysbio.biosynthframework.util.ConnectedComponentsUtils;

public class DatabaseAssembler {

  public static class Builder {

    private ConnectedComponents<String> cpdSets = null;
    private ConnectedComponents<String> rxnSets = null;
    private ConnectedComponents<String> cpdCurationSets = null;
    private ConnectedComponents<String> rxnCurationSets = null;
    private BiodbGraphDatabaseService service;

    public Builder withNeo4jGraphDatabaseService(BiodbGraphDatabaseService service) {
      this.service = service;
      return this;
    }

    public Builder withMetaboliteCurationSets(String path) {
      this.cpdCurationSets = ConnectedComponentsUtils.loadConnectedComponents(path);
      return this;
    }

    public Builder withReactionCurationSets(String path) {
      this.rxnCurationSets =  ConnectedComponentsUtils.loadConnectedComponents(path);
      return this;
    }


    public Builder withMetaboliteSets(String path) {
      this.cpdSets = ConnectedComponentsUtils.loadConnectedComponents(path);
      return this;
    }

    public Builder withReactionSets(String path) {
      this.rxnSets =  ConnectedComponentsUtils.loadConnectedComponents(path);
      return this;
    }

    public DatabaseAssembler build() {
      DatabaseAssembler assembler = new DatabaseAssembler();
      assembler.cpdSets = cpdSets;
      assembler.rxnSets = rxnSets;
      assembler.cpdCurationSets = cpdCurationSets;
      assembler.rxnCurationSets = rxnCurationSets;
      assembler.service = service;
      return assembler;
    }
  }

  public ConnectedComponents<String> cpdSets = null;
  public ConnectedComponents<String> rxnSets = null;
  public ConnectedComponents<String> cpdCurationSets = null;
  public ConnectedComponents<String> rxnCurationSets = null;
  public BiodbGraphDatabaseService service;
  public DefaultAssembler assembler = null;
  
  public void mkdirs(String path) {
    File cpds = new File(path + "/data/cpd");
    File rxns = new File(path + "/data/rxn");
    File structures = new File(path + "/data/structure");
    if (!structures.exists()) {
      structures.mkdirs();
    }
    if (!cpds.exists()) {
      cpds.mkdirs();
    }
    if (!rxns.exists()) {
      rxns.mkdirs();
    }
  }
  
  public void assemble(String path) {
    mkdirs(path);
    
    ConnectedComponents<Long> ccCpdIds = Neo4jUtils.toCpdIds(cpdSets, null, service);
//    ConnectedComponents<Long> cuCpdIds = Helper.toCpdIds(cpdCurationSets, service);
    ConnectedComponents<Long> ccRxnIds = Neo4jUtils.toRxnIds(rxnSets, null, service);
    
    assembler = new DefaultAssembler(service, ccCpdIds, ccRxnIds);
    assembler.cpdAliasGenerator = new AliasGenerator(service, MetaboliteMajorLabel.BiGGMetabolite);
    assembler.rxnAliasGenerator = new AliasGenerator(service, ReactionMajorLabel.BiGGReaction);
    assembler.cpdAssemblePlugins.put("hmdb", new HmdbAssemblePlugin(service));
    assembler.cpdAssemblePlugins.put("lm", new LipidmapsAssemblePlugin(service));
    assembler.cpdAssemblePlugins.put("bigg", new BiggMetaboliteAssemblePlugin(service));
    assembler.cpdAssemblePlugins.put("ms", new ModelseedMetaboliteAssemblePlugin(service));
    assembler.cpdAssemblePlugins.put("metacyc", new BiocycMetaboliteAssemblePlugin(service, MetaboliteMajorLabel.MetaCyc.toString()));
    assembler.rxnAssemblePlugins.put("metacyc", new BiocycReactionAssemblePlugin(service, ReactionMajorLabel.MetaCyc.toString()));
    assembler.rxnAssemblePlugins.put("bigg", new BiggReactionAssemblePlugin(service));
    assembler.rxnAssemblePlugins.put("kegg", new KeggReactionAssemblePlugin(service));
    assembler.outputPath = path;
    
    assembler.generateIntegratedIds();
    assembler.assembleCpd(ccCpdIds);
    assembler.assembleRxn(ccRxnIds, ccCpdIds);
  }
}
