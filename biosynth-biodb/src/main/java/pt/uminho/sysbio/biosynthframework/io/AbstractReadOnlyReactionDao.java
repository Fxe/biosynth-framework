package pt.uminho.sysbio.biosynthframework.io;

import java.util.Set;

import pt.uminho.sysbio.biosynthframework.Reaction;

public abstract class AbstractReadOnlyReactionDao<R extends Reaction> 
extends AbstractReactionDao<R> {

  public AbstractReadOnlyReactionDao(String version) {
    super(version);
  }

  @Override
  public R saveReaction(R reaction) {
    throw new UnsupportedOperationException();
  }

  @Override
  public abstract R getReactionById(Long id);

  @Override
  public abstract R getReactionByEntry(String entry);

  @Override
  public abstract Set<Long> getAllReactionIds();

  @Override
  public abstract Set<String> getAllReactionEntries();
  
  
}
