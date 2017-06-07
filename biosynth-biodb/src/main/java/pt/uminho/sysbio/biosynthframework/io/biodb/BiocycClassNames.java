package pt.uminho.sysbio.biosynthframework.io.biodb;

public enum BiocycClassNames {
  Active_Peptides(""),
  All_Genes("All-Genes"),
  Amino_Acids(""),
  CCO(""),
  Chemicals(""),
  Chromosomes(""),
  Composite_Reactions(""),
  Compound_Mediated_Translation_Regulation(""),
  Compound_Mixtures(""),
  Compounds(""),
  Contigs(""),
  Cryptic_Genes(""),
  DNA_Binding_Sites(""),
  DNA_Segments(""),
  Elements(""),
  Enzymatic_Reactions(""),
  FRAMES(""),
  Gene_Ontology_Terms(""),
  Generalized_Reactions(""),
  Genes(""),
  Genetic_Elements(""),
  Growth_Media(""),
  Growth_Observations(""),
  Interrupted_Genes(""),
  Macromolecules(""),
  Modified_Proteins(""),
  Organisms(""),
  Organizations(""),
  Pathways(""),
  People(""),
  Phantom_Genes(""),
  Plasmids(""),
  Polymer_Segments(""),
  Polypeptides(""),
  Promoter_Boxes(""),
  Promoters(""),
  Protein_Binding_Features(""),
  Protein_Complexes(""),
  Protein_Features(""),
  Protein_Mediated_Attenuation(""),
  Protein_Mediated_Translation_Regulation(""),
  Protein_RNA_Complexes(""),
  Protein_Small_Molecule_Complexes(""),
  Proteins("Proteins"),
  Pseudo_Genes(""),
  Publications(""),
  RNA_Mediated_Translation_Regulation(""),
  RNAs(""),
  Reactions(""),
  Regulation(""),
  Regulation_of_Enzyme_Activity(""),
  Regulation_of_Transcription_Initiation(""),
  Regulation_of_Translation(""),
  Replicon_Buckets(""),
  Sigma_Factors(""),
  Signaling_Pathways(""),
  Small_Molecule_Mediated_Attenuation(""),
  Small_Molecule_Reactions(""),
  Super_Pathways(""),
  THINGS(""),
  Transcription_Units(""),
  Transcriptional_Attenuation(""),
  Unclassified_Genes(""),
  mRNA_Binding_Sites(""),
  mRNA_Segments("");
  
  private String string;

  // constructor to set the string
  BiocycClassNames(String name){string = name;}
  
  @Override
  public String toString() {
    return string;
  }
}
