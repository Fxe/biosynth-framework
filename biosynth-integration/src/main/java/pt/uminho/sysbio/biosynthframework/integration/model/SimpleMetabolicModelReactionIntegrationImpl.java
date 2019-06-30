package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.Set;
import java.util.function.Function

import pt.uminho.sysbio.biosynthframework.AbstractBiosynthEntity;
import pt.uminho.sysbio.biosynthframework.CompartmentalizedStoichiometry;
import pt.uminho.sysbio.biosynthframework.ModelAdapter;
import pt.uminho.sysbio.biosynthframework.integration.ReactionTMatcher;

public class SimpleMetabolicModelReactionIntegrationImpl<RXN extends AbstractBiosynthEntity> extends AbstractMetabolicModelReactionIntegration<String, RXN> {

  private final ModelAdapter model;
  
  public SimpleMetabolicModelReactionIntegrationImpl(ModelAdapter model,
                                                     Function<String, String> spiToCpdTranslateFunction,
                                                     Function<Set<String>, String> selectFilter) {
    super(spiToCpdTranslateFunction, selectFilter);
    this.model = model;
  }

  @Override
  public void integrateTranslocation() {
    ReactionTMatcher<Integer, String> matcher = new ReactionTMatcher<>();
    matcher.allowSingle = false;
    matcher.testReverse = true;
    
    for (String rxnEntry : dao.getAllEntries()) {
      RXN rxn = dao.getByEntry(rxnEntry);
      if (!isBasic.apply(rxn)) {
        CompartmentalizedStoichiometry<String, Integer> cstoich = convertToStoich.apply(rxn);
        matcher.addReaction(cstoich, rxnEntry);
      }
    }
    
    for (String mrxnId : model.getReactionIds()) {
      if (model.isTranslocation(mrxnId)) {
        CompartmentalizedStoichiometry<String, String> cstoich = model.getCompartmentalizedStoichiometry(mrxnId);
        this.aaa(mrxnId, cstoich, matcher, null, treport);
      }
    }
  }
  
  @Override
  public void integrateBasic(Set<String> excludeIds) {
    ReactionTMatcher<Integer, String> matcher = new ReactionTMatcher<>();
    matcher.allowSingle = true;
    matcher.testReverse = true;
    
    for (String rxnEntry : dao.getAllEntries()) {
      RXN rxn = dao.getByEntry(rxnEntry);
      if (isBasic.apply(rxn)) {
        CompartmentalizedStoichiometry<String, Integer> cstoich = convertToStoich.apply(rxn);
        for (String id : excludeIds) {
          cstoich.remove(id);
        }
        matcher.addReaction(cstoich, rxnEntry);
      }
    }
    
    for (String mrxnId : model.getReactionIds()) {
      if (!model.isTranslocation(mrxnId)) {
        CompartmentalizedStoichiometry<String, String> cstoich = model.getCompartmentalizedStoichiometry(mrxnId);
        this.aaa(mrxnId, cstoich, matcher, excludeIds, mreport);
      }
    }
  }
}
