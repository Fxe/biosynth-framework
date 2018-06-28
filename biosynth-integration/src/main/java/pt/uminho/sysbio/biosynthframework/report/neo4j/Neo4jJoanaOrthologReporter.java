package pt.uminho.sysbio.biosynthframework.report.neo4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.ExternalReference;

public class Neo4jJoanaOrthologReporter {

  public static class OrthologData {
    public String entry;
    public String database;
    public String status;
    public Map<String, Object> data = new HashMap<> ();
    public String match;
    public boolean missingData;

    public boolean isMissingData() {
      return status == null || match == null;
    }

    @Override
    public String toString() {
      return String.format("[%s@%s:%s]", entry, database, match);
    }
  }

  public static class ReactionData {
    public String entry;
    public String database;
    public Set<Set<String>> orthologs = new HashSet<> ();
  }

  public static class OrthologReport {
    public String modelEntry;
    public String taxaEntry;
    public String taxonomy;
    public Map<String, ReactionData> reactionMapping = new HashMap<>();
    public Map<String, OrthologData> orthologsMapping = new HashMap<>();

    public void add(String modelReaction, ExternalReference ref) {
      this.add(modelReaction, ref, null);
    }

    public void add(String modelReaction, ExternalReference ref, Set<Set<String>> ors) {
      if (!reactionMapping.containsKey(modelReaction)) {
        ReactionData rdata = new ReactionData();
        rdata.entry = ref.entry;
        rdata.database = ref.source;
        if (ors != null) {
          rdata.orthologs.addAll(ors);
          for (Set<String> ands : ors) {
            for (String o : ands) {
              if (!orthologsMapping.containsKey(o)) {
                orthologsMapping.put(o, null);
              }
            }
          }
        }
        reactionMapping.put(modelReaction, rdata);
      }
    }
  }
}
