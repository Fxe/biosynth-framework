package pt.uminho.sysbio.biosynthframework.core.data;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggKOsDaoImpl;

public class GeneralDriver {
  public static void main(String[] args) {
    RestKeggKOsDaoImpl k = new RestKeggKOsDaoImpl();
    k.setLocalStorage("/var/biodb/kegg2");
    k.setSaveLocalStorage(true);
    k.setUseLocalStorage(true);
    //      System.out.println(k.getAllKOEntries());
    System.out.println(k.getKOByEntry("K03472"));
  }
}
