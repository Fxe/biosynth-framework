package pt.uminho.sysbio.biosynthframework.integration.etl.biodb;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pt.uminho.sysbio.biosynth.integration.etl.DefaultMetaboliteEtlExtract;
import pt.uminho.sysbio.biosynthframework.Dataset;
import pt.uminho.sysbio.biosynthframework.Metabolite;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycReactionEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycReactionLeftEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycReactionRightEntity;
import pt.uminho.sysbio.biosynthframework.io.FileDatasetDao;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;
import pt.uminho.sysbio.biosynthframework.io.ReactionDao;

public class BiocycEtlExtract extends DefaultMetaboliteEtlExtract<BioCycMetaboliteEntity> {
  
  public static String VIRTUAL = "Virtual";
  
  private final MetaboliteDao<BioCycMetaboliteEntity> cpdDao;
  private final ReactionDao<BioCycReactionEntity> rxnDao;
  private final File indexFile;
  
  public BiocycEtlExtract(MetaboliteDao<BioCycMetaboliteEntity> cpdDao, 
                          ReactionDao<BioCycReactionEntity> rxnDao, File indexFile) {
    super(cpdDao);
    this.cpdDao = cpdDao;
    this.rxnDao = rxnDao;
    this.indexFile = indexFile;
  }
  
  public List<Serializable> getIdentifierList() {
    List<Serializable> result = new ArrayList<>();
    FileDatasetDao dao = new FileDatasetDao(indexFile);
    Dataset<String, String, Object> index = dao.getDataset();
    
    for (String cpdEntry : cpdDao.getAllMetaboliteEntries()) {
      if (!index.dataset.containsKey("META:" + cpdEntry)) {
        Metabolite cpd = cpdDao.getMetaboliteByEntry(cpdEntry);
        if (cpd != null) {
          index.add(cpd.getEntry(), "type", "Compound");
        }
      }
    }
    for (String rxnEntry : rxnDao.getAllReactionEntries()) {
      try {
        BioCycReactionEntity rxn = rxnDao.getReactionByEntry(rxnEntry);
        for (BioCycReactionLeftEntity l : rxn.getLeft()) {
          if (!index.dataset.containsKey(l.getCpdEntry())) {
            Metabolite cpd = cpdDao.getMetaboliteByEntry(l.getCpdEntry());
            if (cpd != null) {
              index.add(cpd.getEntry(), "type", "Compound");
            } else {
              index.add(l.getCpdEntry(), "type", VIRTUAL);
            }
          }
        }
        for (BioCycReactionRightEntity r : rxn.getRight()) {
          if (!index.dataset.containsKey(r.getCpdEntry())) {
            Metabolite cpd = cpdDao.getMetaboliteByEntry(r.getCpdEntry());
            if (cpd != null) {
              index.add(cpd.getEntry(), "type", "Compound");
            } else {
              index.add(r.getCpdEntry(), "type", VIRTUAL);
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    dao.save();
    
    for (String k : index.dataset.keySet()) {
      if (!index.dataset.get(k).get("type").equals(VIRTUAL)) {
        result.add(k);
      }
    }
    
    return result;
  }
  
  @Override
  public List<Serializable> getAllKeys() {
    return this.getIdentifierList();
  }
}
