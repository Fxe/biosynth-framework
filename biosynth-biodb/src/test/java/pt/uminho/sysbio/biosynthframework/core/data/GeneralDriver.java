package pt.uminho.sysbio.biosynthframework.core.data;

import org.apache.commons.lang3.tuple.Pair;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggGenomeEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggKOEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggModuleEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggGeneDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggGenesDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggGenomeDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggKOsDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggModuleDaoImpl;

public class GeneralDriver {
  public static void main(String[] args) {
    String dbPath = "/var/biodb/kegg2";
    
    RestKeggModuleDaoImpl moduleDao = new RestKeggModuleDaoImpl();
    moduleDao.setLocalStorage(dbPath);
    moduleDao.setSaveLocalStorage(true);
    moduleDao.setUseLocalStorage(true);
    
    RestKeggKOsDaoImpl koDao = new RestKeggKOsDaoImpl();
    koDao.setLocalStorage(dbPath);
    koDao.setSaveLocalStorage(true);
    koDao.setUseLocalStorage(true);
    
    RestKeggGenesDaoImpl geneDao = new RestKeggGenesDaoImpl();
    geneDao.setLocalStorage(dbPath);
    geneDao.setSaveLocalStorage(true);
    geneDao.setUseLocalStorage(true);
    geneDao.replace = true;
    
    RestKeggGenomeDaoImpl genomeDao = new RestKeggGenomeDaoImpl();
    genomeDao.setLocalStorage(dbPath);
    genomeDao.setSaveLocalStorage(true);
    genomeDao.setUseLocalStorage(true);
    
    KeggModuleEntity module = moduleDao.getModuleByEntry("M00124");
    for (String koEntry : module.getOrthologs()) {
      KeggKOEntity ko = koDao.getKOByEntry(koEntry);
      for (Pair<String, String> gpair : ko.g) {
        System.out.println(gpair);
        try {
          KeggGenomeEntity g = genomeDao.getGenomeByEntry(gpair.getLeft());

          System.out.println(g.getTaxonomy());
          
          geneDao.getGeneByEntry(String.format("%s:%s", gpair.getKey(), gpair.getRight()));
          
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    
    
    //      System.out.println(k.getAllKOEntries());
    
  }
}
