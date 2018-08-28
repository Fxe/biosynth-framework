package pt.uminho.sysbio.biosynthframework.io.biodb.modelseed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.biodb.modelseed.ModelSeedReaction;
import pt.uminho.sysbio.biosynthframework.biodb.modelseed.ModelSeedReactionCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.modelseed.ModelSeedReactionEntity;
import pt.uminho.sysbio.biosynthframework.biodb.modelseed.ModelSeedReactionReagentEntity;
import pt.uminho.sysbio.biosynthframework.io.ReactionDao;

public class JsonModelSeedReactionDaoImpl implements ReactionDao<ModelSeedReactionEntity>{

  private static final Logger logger = LoggerFactory.getLogger(JsonModelSeedReactionDaoImpl.class);
  
  public Map<String, ModelSeedReaction> data = new HashMap<> ();
  
  public Function<String, ModelSeedReactionReagentEntity> stoichParser = 
      new Function<String, ModelSeedReactionReagentEntity>() {

    @Override
    public ModelSeedReactionReagentEntity apply(String t) {
      ModelSeedReactionReagentEntity reagent = 
          new ModelSeedReactionReagentEntity();
      String[] data = t.split(":");
      //STOICH:CPDXXXXX:CMP:??:NAME
      reagent.setStoichiometry(Math.abs(Double.parseDouble(data[0])));
      reagent.setCoefficient(Double.parseDouble(data[0]));
      reagent.setCpdEntry(data[1]);
      reagent.setCompartment(Integer.parseInt(data[2]));
      if (reagent.getCoefficient() == 0.0) {
        logger.warn("coefficient value {}", reagent.getStoichiometry());
      }
      return reagent;
    }
  };
  
  public Function<ModelSeedReaction, ModelSeedReactionEntity> function = 
      new Function<ModelSeedReaction, ModelSeedReactionEntity>() {

        @Override
        public ModelSeedReactionEntity apply(ModelSeedReaction t) {
          ModelSeedReactionEntity rxn = new ModelSeedReactionEntity();
          rxn.setEntry(t.id);
          rxn.setName(t.name);
          
          rxn.setAbbreviation(t.abbreviation);
          rxn.setCode(t.code);
          rxn.setDefinition(t.definition);
          rxn.setDirection(t.direction);
          rxn.setReversibility(t.reversibility);
          rxn.setEquation(t.equation);
          
          rxn.setDeltag(t.deltag);
          rxn.setDeltagerr(t.deltagerr);
          rxn.setStatus(t.status);
          rxn.setObsolete(JsonModelSeedMetaboliteDaoImpl.integerToBoolean(t.is_obsolete));
          
          rxn.setNames(t.names);
          for (String ecStr : t.ec_numbers) {
            for (String a : ecStr.split(",")) {
              for (String b : a.split("\\s+")) {
                if (b != null && !b.trim().isEmpty()) {
                  rxn.getEc().add(b);
                }
              }
            }
          }
          
          List<ModelSeedReactionReagentEntity> reagents = new ArrayList<> ();
          for (String s : t.stoichiometry) {
            ModelSeedReactionReagentEntity reagent = stoichParser.apply(s);
            reagents.add(reagent);
          }
          rxn.setReagents(reagents);
          
          List<ModelSeedReactionCrossreferenceEntity> refs = new ArrayList<> ();
          for (String bigg : t.bigg_aliases) {
            refs.add(new ModelSeedReactionCrossreferenceEntity(
                ReferenceType.DATABASE, "BiGG", bigg));
          }
          for (String kegg : t.kegg_aliases) {
            refs.add(new ModelSeedReactionCrossreferenceEntity(
                ReferenceType.DATABASE, "LigandReaction", kegg));
          }
          for (String metacyc : t.metacyc_aliases) {
            refs.add(new ModelSeedReactionCrossreferenceEntity(
                ReferenceType.DATABASE, "MetaCyc", "META:" + metacyc));
          }
          rxn.setCrossreferences(refs);
          
          for (String role : t.roles) {
            String id = role.split(";")[0];
            rxn.getRoles().add(id);
          }
          for (String subsystem : t.subsystems) {
            String id = subsystem.split(";")[0];
            rxn.getSubsystems().add(id);
          }
          
          /*
           * skipped templates, linked_reactions?
           * pathways: aracyc, plantcyc, metacyc, kegg, ecocyc, hop
           * complexes
           */
          
          return rxn;
        }
  };
  
  public JsonModelSeedReactionDaoImpl(Resource reactionsJson) {
    ObjectMapper m = new ObjectMapper();
    m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    CollectionType ctype = m.getTypeFactory()
                            .constructCollectionType(List.class, 
                                                     ModelSeedReaction.class);
    
    try {
      List<ModelSeedReaction> reactions = m.readValue(reactionsJson.getInputStream(), ctype);
      for (ModelSeedReaction rxn : reactions) {
        if (rxn != null && rxn.id != null && !rxn.id.trim().isEmpty()) {
          if (data.put(rxn.id, rxn) != null) {
            logger.warn("duplicate ID - {}", rxn.id);
          }
        } else {
          logger.warn("invalid record - {}", rxn);
        }
      }
    } catch (IOException e) {
      logger.error("IO Error: {}", e.getMessage());
    }
  }
  
  @Override
  public ModelSeedReactionEntity getReactionById(Long id) {
    throw new RuntimeException("Unsupported operation");
  }

  @Override
  public ModelSeedReactionEntity getReactionByEntry(String entry) {
    return function.apply(data.get(entry));
  }

  @Override
  public ModelSeedReactionEntity saveReaction(ModelSeedReactionEntity reaction) {
    throw new RuntimeException("Unsupported operation");
  }

  @Override
  public Set<Long> getAllReactionIds() {
    throw new RuntimeException("Unsupported operation");
  }

  @Override
  public Set<String> getAllReactionEntries() {
    return new HashSet<> (data.keySet());
  }

}
