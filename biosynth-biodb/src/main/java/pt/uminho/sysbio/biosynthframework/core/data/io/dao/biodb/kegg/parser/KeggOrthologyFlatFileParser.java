package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;


public class KeggOrthologyFlatFileParser extends AbstractKeggFlatFileParser {

  public KeggOrthologyFlatFileParser(String flatfile) {
    super(flatfile);
    this.parseContent();
  }

  public Set<Pair<String, String>> getGenes() {
    
    int tabIndex = this.getTabIndex("GENES");
    String content = this.tabContent_.get(tabIndex);
    
    Set<Pair<String, String>> genes = new HashSet<> ();
    
    for (String geneString : content.split("\n")) {
      //ORG: GENE_ID(name) name is optional
      String[] split = geneString.trim().split(":");
      if (split.length > 1) {
        String org = split[0].trim().toLowerCase();
        String geneId = split[1].trim();
        if (geneId.indexOf('(') >= 0) {
          geneId = geneId.substring(0, geneId.indexOf('('));
        }
        genes.add(new ImmutablePair<>(org, geneId));
      }
      
    }
    
    return genes;
  }
}
