package pt.uminho.sysbio.biosynthframework.genome;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.alignment.Alignments.PairwiseSequenceAlignerType;
import org.biojava.nbio.alignment.template.PairwiseSequenceAligner;
import org.biojava.nbio.alignment.SimpleGapPenalty;
import org.biojava.nbio.core.alignment.matrices.SimpleSubstitutionMatrix;
import org.biojava.nbio.core.alignment.template.SequencePair;
import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.AccessionID;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.RNASequence;
import org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.io.DNASequenceCreator;
import org.biojava.nbio.core.sequence.io.FastaReader;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;
import org.biojava.nbio.core.sequence.io.GenbankReader;
import org.biojava.nbio.core.sequence.io.GenericFastaHeaderParser;
import org.biojava.nbio.core.sequence.io.GenericGenbankHeaderParser;
import org.biojava.nbio.core.sequence.template.CompoundSet;
import org.biojava.nbio.core.util.ConcurrencyTools;

public class Ncbi {
  
  public static void biojava() {
    try {
      DNASequence seq = new DNASequence("GTAC");
    } catch (CompoundNotFoundException e) {
      e.printStackTrace();
    }
    
    try {
      RNASequence rna = new RNASequence("AUGC");
    } catch (CompoundNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    try {
      ProteinSequence prot = new ProteinSequence("AW");
    } catch (CompoundNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    
    
    
    InputStream is = null;

    try {
      is = new FileInputStream("/var/genome/iMF721_1.txt");
      FastaReader<DNASequence, NucleotideCompound> fastaReader = 
          new FastaReader<>(is, 
                            new GenericFastaHeaderParser<DNASequence, NucleotideCompound>(), 
                            new DNASequenceCreator(
                                AmbiguityDNACompoundSet.getDNACompoundSet()));
      
      Map<String, DNASequence> seqs = null;
      while ((seqs = fastaReader.process(5)) != null) {
        for (String k : seqs.keySet()) {
          System.out.println(k);
        }
      };
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(is);
    }
    
//    try {
//      is = new FileInputStream("/var/genome/GCF_000005845.2_ASM584v2_genomic.gbff");
//      GenbankReader<DNASequence, NucleotideCompound> genbankReader = 
//          new GenbankReader<>(is, 
//                              new GenericGenbankHeaderParser<>(), 
//                              new DNASequenceCreator(
//                                  AmbiguityDNACompoundSet.getDNACompoundSet()));
//      
//      Map<String, DNASequence> seqs = null;
//      while ((seqs = genbankReader.process()) != null) {
//        for (String k : seqs.keySet()) {
//          System.out.println(k);
//          DNASequence seq = seqs.get(k);
//          System.out.println(seq.getDescription());
//          System.out.println(seq.getAccession());
//          System.out.println(seq.getAccession());
//        }
//      };
//    } catch (IOException | CompoundNotFoundException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    } finally {
//      IOUtils.closeQuietly(is);
//    }
  }
  
  public static void alignSomething() {
    ClustalOmega omega = new ClustalOmega("D:/opt/clustal-omega/1.2.2/");
    try {
      String out = omega.malign(">a\nAKM\n>b\nAM");
      System.out.println(out);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static ProteinSequence getQ21691() {
    ProteinSequence sequence;
    try {
      sequence = new ProteinSequence("MDLLDKVMGEMGSKPGSTAKKPATSASSTPRTNVWGTAKKPSSQQQPPKPLFTTPGSQQGSLGGRIPKREHTDRTGPDPKRKPLGGLSVPDSFNNFGTFRVQMNAWNLDISKMDERISRIMFRATLVHTDGRRFELSLGVSAFSGDVNRQQRRQAQCLLFRAWFKRNPELFKGMTDPAIAAYDAAETIYVGCSFFDVELTEHVCHLTEADFSPQEWKIVSLISRRSGSTFEIRIKTNPPIYTRGPNALTLENRSELTRIIEAITDQCLHNEKFLLYSSGTFPTKGGDIASPDEVTLIKSGFVKTTKIVDRDGVPDAIMTVDTTKSPFYKDTSLLKFFTAKMDQLTNSGGGPRGHNGGRERRDGGGNSRKYDDRRSPRDGEIDYDERTVSHYQRQFQDERISDGMLNTLKQSLKGLDCQPIHLKDSKANRSIMIDEIHTGTADSVTFEQKLPDGEMKLTSITEYYLQRYNYRLKFPHLPLVTSKRAKCYDFYPMELMSILPGQRIKQSHMTVDIQSYMTGKMSSLPDQHIKQSKLVLTEYLKLGDQPANRQMDAFRVSLKSIQPIVTNAHWLSPPDMKFANNQLYSLNPTRGVRFQTNGKFVMPARVKSVTIINYDKEFNRNVDMFAEGLAKHCSEQGMKFDSRPNSWKKVNLGSSDRRGTKVEIEEAIRNGVTIVFGIIAEKRPDMHDILKYFEEKLGQQTIQISSETADKFMRDHGGKQTIDNVIRKLNPKCGGTNFLIDVPESVGHRVVCNNSAEMRAKLYAKTQFIGFEMSHTGARTRFDIQKVMFDGDPTVVGVAYSLKHSAQLGGFSYFQESRLHKLTNLQEKMQICLNAYEQSSSYLPETVVVYRVGSGEGDYPQIVNEVNEMKLAARKKKHGYNPKFLVICTQRNSHIRVFPEHINERGKSMEQNVKSGTCVDVPGASHGYEEFILCCQTPLIGTVKPTKYTIIVNDCRWSKNEIMNVTYHLAFAHQVSYAPPAIPNVSYAAQNLAKRGHNNYKTHTKLVDMNDYSYRIKEKHEEIISSEEVDDILMRDFIETVSNDLNAMTINGRNFWA");
      sequence.setAccession(new AccessionID("Q21691"));
      return sequence;
    } catch (CompoundNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public static ProteinSequence getO48771() {
    ProteinSequence sequence;
    try {
      sequence = new ProteinSequence("METSSSLPLSPISIEPEQPSHRDYDITTRRGVGTTGNPIELCTNHFNVSVRQPDVVFYQYTVSITTENGDAVDGTGISRKLMDQLFKTYSSDLDGKRLAYDGEKTLYTVGPLPQNEFDFLVIVEGSFSKRDCGVSDGGSSSGTCKRSKRSFLPRSYKVQIHYAAEIPLKTVLGTQRGAYTPDKSAQDALRVLDIVLRQQAAERGCLLVRQAFFHSDGHPMKVGGGVIGIRGLHSSFRPTHGGLSLNIDVSTTMILEPGPVIEFLKANQSVETPRQIDWIKAAKMLKHMRVKATHRNMEFKIIGLSSKPCNQQLFSMKIKDGEREVPIREITVYDYFKQTYTEPISSAYFPCLDVGKPDRPNYLPLEFCNLVSLQRYTKPLSGRQRVLLVESSRQKPLERIKTLNDAMHTYCYDKDPFLAGCGISIEKEMTQVEGRVLKPPMLKFGKNEDFQPCNGRWNFNNKMLLEPRAIKSWAIVNFSFPCDSSHISRELISCGMRKGIEIDRPFALVEEDPQYKKAGPVERVEKMIATMKLKFPDPPHFILCILPERKTSDIYGPWKKICLTEEGIHTQCICPIKISDQYLTNVLLKINSKLGGINSLLGIEYSYNIPLINKIPTLILGMDVSHGPPGRADVPSVAAVVGSKCWPLISRYRAAVRTQSPRLEMIDSLFQPIENTEKGDNGIMNELFVEFYRTSRARKPKQIIIFRDGVSESQFEQVLKIEVDQIIKAYQRLGESDVPKFTVIVAQKNHHTKLFQAKGPENVPAGTVVDTKIVHPTNYDFYMCAHAGKIGTSRPAHYHVLLDEIGFSPDDLQNLIHSLSYVNQRSTTATSIVAPVRYAHLAAAQVAQFTKFEGISEDGKVPELPRLHENVEGNMFFC");
      sequence.setAccession(new AccessionID("O48771"));
      return sequence;
    } catch (CompoundNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public static void main(String[] args) {
    String[] ids = new String[] {"Q21691", "O48771"};
    List<ProteinSequence> lst = new ArrayList<ProteinSequence>();
    lst.add(getQ21691());
    lst.add(getO48771());
//    for (String uniProtId : ids) {
//      try {
//        URL uniprotFasta = new URL(String.format("http://www.uniprot.org/uniprot/%s.fasta", uniProtId));
//        InputStream is = uniprotFasta.openStream();
//        ProteinSequence seq = FastaReaderHelper.readFastaProteinSequence(is).get(uniProtId);
//        System.out.printf("id : %s %s%n%s%n", uniProtId, seq, seq.getOriginalHeader());
//        lst.add(seq);
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//    }
    AlignTool tool = new AlignTool();
    System.out.println(lst);
    List<SequencePair<ProteinSequence, AminoAcidCompound>> alig = tool.galign(lst);
    for (SequencePair<ProteinSequence, AminoAcidCompound> pair : alig) {
//      SimpleSubstitutionMatrix.getBlosum62().getValue(from, to)
//      pair.get
      System.out.printf("%n%s vs %s%n%s", pair.getQuery().getAccession(), pair.getTarget().getAccession(), pair);
      System.out.println(pair.getNumIdenticals() + " " + pair.getNumSimilars() + " " + pair.getLength());
    }
    
    alig = tool.lalign(lst);
    for (SequencePair<ProteinSequence, AminoAcidCompound> pair : alig) {
//      SimpleSubstitutionMatrix.getBlosum62().getValue(from, to)
//      pair.get
      System.out.printf("%n%s vs %s%n%s", pair.getQuery().getAccession(), pair.getTarget().getAccession(), pair);
      System.out.println(pair.getNumIdenticals() + " " + pair.getNumSimilars() + " " + pair.getLength());
    }
    
    PairwiseSequenceAligner<ProteinSequence, AminoAcidCompound> a = tool.lalign(lst.get(0), lst.get(1));
    SequencePair<ProteinSequence, AminoAcidCompound> pair = a.getPair();
    System.out.println(a.getSimilarity() + " " + pair.getLength() + " " + a.getDistance() + " " + pair.getNumIdenticals());
    
  }
}
