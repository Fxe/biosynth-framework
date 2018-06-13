package pt.uminho.sysbio.biosynthframework.io.biodb;

import java.io.InputStream;
import java.util.Set;

import org.springframework.core.io.InputStreamResource;

import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggReactionEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg.CsvBiggReactionDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg.DefaultBiggEquationParserImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg.DefaultBiggReactionParserImpl;
import pt.uminho.sysbio.biosynthframework.io.BiosDao;

public class InternalBigg1ReactionDaoImpl extends CsvBiggReactionDaoImpl
implements BiosDao<BiggReactionEntity>{
  
  public InternalBigg1ReactionDaoImpl() {
    InputStream is = getClass().getClassLoader().getResourceAsStream("bigg1_reactions.tsv");
    this.setCsvFile(new InputStreamResource(is));
    this.setBiggEquationParser(new DefaultBiggEquationParserImpl());
    this.setBiggReactionParser(new DefaultBiggReactionParserImpl());
    this.initialize();
  }

  @Override
  public BiggReactionEntity getByEntry(String entry) {
    return this.getReactionByEntry(entry);
  }

  @Override
  public BiggReactionEntity getById(long id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Long save(BiggReactionEntity o) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean delete(BiggReactionEntity o) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Set<Long> getAllIds() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<String> getAllEntries() {
    return this.getAllReactionEntries();
  }
}
