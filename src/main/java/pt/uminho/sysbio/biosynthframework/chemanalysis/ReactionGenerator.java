package pt.uminho.sysbio.biosynthframework.chemanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.io.MolecularSignatureDao;
import pt.uminho.sysbio.biosynthframework.util.DigestUtils;

import com.google.common.collect.Sets;

public class ReactionGenerator {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(ReactionGenerator.class);
  
  private MolecularSignatureDao signatureDao;
  
  public ReactionGenerator(MolecularSignatureDao signatureDao) {
    this.signatureDao = signatureDao;
  }
  
  public List<MolecularSignature> toMolecularSignature(List<String> hashList) {
    List<MolecularSignature> ms = new ArrayList<> ();
    for (String hash : hashList) {
      MolecularSignature m = signatureDao.getMolecularSignatureByHash(hash);
      ms.add(m);
    }
    return ms;
  }
  
  public Set<List<String>> fixedRegionTemplateMatch(Map<Signature, Double> template, MolecularSignature[] ms) {
//    System.out.println(template);
    List<Set<String>> leftCandidates = new ArrayList<> ();
    for (int i = 0; i < ms.length; i++) {
      LOGGER.info("Find substrate {}", i);
      MolecularSignature subsig = new MolecularSignature();
      Map<Signature, Double> diff = new HashMap<> (template);
      for (int j = 0; j < ms.length; j++) {
        if (j != i) diff = SignatureUtils.diff(diff, ms[j].getSignatureMap());
      }
      subsig.setSignatureMap(diff);
      Set<String> candidates = new HashSet<> ();
      for (long msigId : findExactFit2(subsig.getSignatureMap())) {
        String hash = signatureDao.getMolecularSignatureHashById(msigId);
        candidates.add(hash);
      }
      leftCandidates.add(candidates);
      LOGGER.info("Found {} candidates for substrate {}", candidates.size(), i);
    }
    Set<List<String>> result = Sets.cartesianProduct(leftCandidates);
    return result;
  }
  
  public static boolean validateTemplate(Map<Signature, Double> template, List<MolecularSignature> ms) {
    MolecularSignature sum = SignatureUtils.sumSignatures(ms);
    for (Signature s : template.keySet()) {
      double v = template.get(s);
      Double v_ = sum.getSignatureMap().get(s);
      if (v_ == null) {
        LOGGER.info("Signature {} not found", s);
        return false;
      }
      if (v > v_) {
        LOGGER.info("Signature {} invalid ammount. expected >= {}, atual {}", s, v, v_);
        return false;
      }
    }
    return true;
  }
  
  public void scaffoldReactions2(ReactionSignature rsig, MolecularSignature[] hint) {
    Set<List<String>> substrates = fixedRegionTemplateMatch(rsig.getLeftSignatureMap(), hint);
    for (List<String> r : substrates) {
      List<MolecularSignature> ms = toMolecularSignature(r);
      boolean valid = validateTemplate(rsig.getLeftSignatureMap(), ms);
      if (valid) {
        Map<Signature, Double> products = apply(ms, rsig);
        omg.clear();
        List<String> m = new ArrayList<> ();
        zzzz(products, m);
        LOGGER.info("Solution: {}", omg);
      } else {
        System.out.println("invalid !");
      }
    }
  }
  
  public List<Pair<Map<String, Double>, Map<String, Double>>> scaffoldReactions3(ReactionSignature rsig,
      MolecularSignature[] molecularSignatures1,
      MolecularSignature[] molecularSignatures2) {
    List<Pair<Map<String, Double>, Map<String, Double>>> result = new ArrayList<> ();
    
    Set<List<String>> l = fixedRegionTemplateMatch(rsig.getLeftSignatureMap() , molecularSignatures1);
    Set<List<String>> r = fixedRegionTemplateMatch(rsig.getRightSignatureMap(), molecularSignatures2);
//    Map<String, MolecularSignature> a = new HashMap<> ();
    Map<String, Set<List<String>>> leftHashToSubstrates = new HashMap<> ();
    for (List<String> m : l) {
      List<MolecularSignature> ms = toMolecularSignature(m);
      boolean valid = validateTemplate(rsig.getLeftSignatureMap(), ms);
      if (valid) {
        Map<Signature, Double> products = apply(ms, rsig);
        MolecularSignature rightSignatureAssert = new MolecularSignature();
        rightSignatureAssert.setSignatureMap(products);
        String hash = DigestUtils.hex(rightSignatureAssert.hash());
//        a.put(hash, rightSignatureAssert);
        if (!leftHashToSubstrates.containsKey(hash)) {
          leftHashToSubstrates.put(hash, new HashSet<List<String>> ());
        }
        
        leftHashToSubstrates.get(hash).add(m);
      }
    }
    for (List<String> m : r) {
      List<MolecularSignature> ms = toMolecularSignature(m);
      boolean valid = validateTemplate(rsig.getRightSignatureMap(), ms);
      if (valid) {
        MolecularSignature rmsig = SignatureUtils.sumSignatures(ms);
        String hash = DigestUtils.hex(rmsig.hash());
        Set<List<String>> match = leftHashToSubstrates.get(hash);
        if (match != null) {
          Map<String, Double> products = toMap(m);
          for (List<String> substrateList : match) {
            Map<String, Double> substrates = toMap(substrateList);
            Pair<Map<String, Double>, Map<String, Double>> reaction = 
                new ImmutablePair<>(substrates, products);
            result.add(reaction);
          }
          
//          System.out.println(match);
//          System.out.println(m);
//          System.out.println("------------------------");
        }
//        Map<Signature, Double> products = apply(ms, rsig);
      }
    }
    
    return result;
  }
  
  public static Map<String, Double> toMap(List<String> l) {
    Map<String, Double> map = new HashMap<> ();
    
    for (String e : l) {
      Double v = map.get(e);
      map.put(e, v == null ? 1.0 : v + 1);
    }
    
    return map;
  }
  
  public Set<List<String>> omg = new HashSet<> ();
  public void zzzz(Map<Signature, Double> signatures, List<String> match) {
    for (Double v : signatures.values()) { if (v < 0.0) return; }
    if (signatures.isEmpty()) { omg.add(match); return;}
    
    LOGGER.debug("Lookup match for {} signatures", signatures.size());
    
    Set<Long> a = signatureDao.findMolecularSignatureContainedIn(signatures);
//    System.out.println("->" + a);
    for (String msigHash : toMolecularSignatureHash(a)) {
//      MolecularSignature msig = signatureDao.getMolecularSignatureById(sigSetId);
//      Set<String> cpdMatch = new HashSet<> ();
//      for (long msigId : signatureDao.getMsigIdByHash(msigHash)) {
//        String cpdEntry = signatureDao.getEntryById(msigId);
//        cpdMatch.add(cpdEntry);
//      }
      
//      for (long cpdId : M_SIGNATURE_DAO.getMoleculeReferencesBySignatureSetId(sigSetId)) {
//      for (String cpdEntry : cpdMatch) {
//        GenericMetabolite cpd = ApiUtils.toMetabolite(databaseService.getDatabaseMetaboliteEntity("MetaCyc", "META:".concat(cpdEntry)));
//        LOGGER.info("{} - > {}", msigHash, cpd.getEntry());
//      }
//        System.out.println(sigSetId + " -> " + cpd.getEntry());
        MolecularSignature msig = signatureDao.getMolecularSignatureByHash(msigHash);
//        MolecularSignature msig = M_SIGNATURE_DAO.getMolecularSignature(cpdId, H, STEREO);
//        if (cpd.getSource().equals("MetaCyc")) {
        LOGGER.debug("Found match {}", msigHash);
//        }
        Map<Signature, Double> remaining = SignatureUtils.sub(signatures, msig.getSignatureMap());
//        System.out.println(remaining.values());
        List<String> match_ = new ArrayList<> (match);
        match_.add(msigHash);
        zzzz(remaining, match_);
//      }
    }
  }
  
  public Set<Long> findExactFit2(Map<Signature, Double> sigs) {
    List<Set<Long>> intersection = new ArrayList<> ();
    for (Signature s : sigs.keySet()) {
      double v1 = sigs.get(s);
      //get signature sets that contain this signature node
      Set<Long> msigIdSet = signatureDao.listMolecularSignatureIdBySignature(s);
      LOGGER.trace("{} molecular signatures contains {}", msigIdSet.size(), s);
      
      Set<Long> msigIdSet_ = new HashSet<> ();
      for (long msigId : msigIdSet) {
        MolecularSignature msig = signatureDao.getMolecularSignatureById(msigId);
        double v2 = msig.getSignatureMap().get(s);
        if (v2 >= v1) msigIdSet_.add(msigId);
      }
      LOGGER.trace("{} molecular signatures contains {} with atleast value >= {}", msigIdSet_.size(), s, v1);
      intersection.add(msigIdSet_);
    }
    
    Set<Long> result = intersection.get(0);
    for (int i = 1; i < intersection.size(); i++) {
      result.retainAll(intersection.get(i));
    }
    
    return result;
  }
  
  public Set<String> toMolecularSignatureHash(Set<Long> msigIdSet) {
    Set<String> msigHashSet = new HashSet<> ();
    
    for (long msigId : msigIdSet) {
      msigHashSet.add(signatureDao.getMolecularSignatureHashById(msigId));
    }
    
    return msigHashSet;
  }
  
  public static Map<Signature, Double> apply(List<MolecularSignature> msigs, ReactionSignature rsig) {
    List<Map<Signature, Double>> sigMaps = new ArrayList<> ();
    for (MolecularSignature msig : msigs) sigMaps.add(msig.getSignatureMap()); 
    Map<Signature, Double> sigSum = SignatureUtils.sum(sigMaps);
    Map<Signature, Double> sigMinusLeft = SignatureUtils.sub(sigSum,     rsig.getLeftSignatureMap());
    Map<Signature, Double> sigPlusRight = SignatureUtils.sum(sigMinusLeft,  rsig.getRightSignatureMap());
    return sigPlusRight;
  }
}
