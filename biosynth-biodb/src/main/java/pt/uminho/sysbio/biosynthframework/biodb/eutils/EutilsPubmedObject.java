package pt.uminho.sysbio.biosynthframework.biodb.eutils;

import java.util.Map;

public class EutilsPubmedObject {
  public static class Article {
    public String PubModel;
    public String ArticleTitle;
    public Map<String, Object> Journal; //{ISSN={IssnType=Electronic, =1752-0509},
//    public Map<String, Object> JournalIssue; //{CitedMedium=Internet, Volume=5, PubDate={Year=2011, Month=Aug, Day=16}},
    public Map<String, Object> Pagination;
    public Map<String, Object> Abstract;
    public Map<String, Object> AuthorList;
    public Map<String, Object> ELocationID;
  }
  
  public static class MedlineCitation {
    //MeshHeadingList={MeshHeading={DescriptorName={UI=D049490, MajorTopicYN=N, =Systems Biology}, QualifierName={UI=Q000379, MajorTopicYN=Y, =methods}}}
    public EutilsDate DateCompleted;
    public EutilsDate DateRevised;
    public String Owner;
    public String Status;
    public String CitationSubset;
    public Article Article;
    public Map<String, Object> PMID;
    public Map<String, Object> MedlineJournalInfo;
  }
  
  public static class PubmedArticle {
    public MedlineCitation MedlineCitation;
    public Object PubmedData;
  }
  
  //Article={PubModel=Print-Electronic, Journal={ISSN={IssnType=Electronic, =1742-2051}, 
  //JournalIssue={CitedMedium=Internet, Volume=6, Issue=2, PubDate={Year=2010, Month=Feb}}, Title=Molecular bioSystems, ISOAbbreviation=Mol Biosyst}, 
  //ArticleTitle=Genome-scale metabolic network analysis and drug targeting of multi-drug resistant pathogen Acinetobacter baumannii AYE., Pagination={MedlinePgn=339-48}, ELocationID={EIdType=doi, ValidYN=Y, =10.1039/b916446d}, Abstract={AbstractText=Acinetobacter baumannii has emerged as a new clinical threat to human health, particularly to ill patients in the hospital environment. Current lack of effective clinical solutions to treat this pathogen urges us to carry out systems-level studies that could contribute to the development of an effective therapy. Here we report the development of a strategy for identifying drug targets by combined genome-scale metabolic network and essentiality analyses. First, a genome-scale metabolic network of A. baumannii AYE, a drug-resistant strain, was reconstructed based on its genome annotation data, and biochemical knowledge from literatures and databases. In order to evaluate the performance of the in silico model, constraints-based flux analysis was carried out with appropriate constraints. Simulations were performed from both reaction (gene)- and metabolite-centric perspectives, each of which identifies essential genes/reactions and metabolites critical to the cell growth. The gene/reaction essentiality enables validation of the model and its comparative study with other known organisms' models. The metabolite essentiality approach was undertaken to predict essential metabolites that are critical to the cell growth. The EMFilter, a framework that filters initially predicted essential metabolites to find the most effective ones as drug targets, was also developed. EMFilter considers metabolite types, number of total and consuming reaction linkage with essential metabolites, and presence of essential metabolites and their relevant enzymes in human metabolism. Final drug target candidates obtained by this system framework are presented along with implications of this approach.}, AuthorList={CompleteYN=Y, Author={ValidYN=Y, LastName=Lee, ForeName=Sang Yup, Initials=SY}}, Language=eng, PublicationTypeList={PublicationType={UI=D013485, =Research Support, Non-U.S. Gov't}}, ArticleDate={DateType=Electronic, Year=2009, Month=12, Day=08}}, MedlineJournalInfo={Country=England, MedlineTA=Mol Biosyst, NlmUniqueID=101251620, ISSNLinking=1742-2051}, CitationSubset=IM, MeshHeadingList={MeshHeading={DescriptorName={UI=D049490, MajorTopicYN=N, =Systems Biology}, QualifierName={UI=Q000379, MajorTopicYN=Y, =methods}}}}, PubmedData={History={PubMedPubDate={PubStatus=medline, Year=2010, Month=4, Day=30, Hour=6, Minute=0}}, PublicationStatus=ppublish, ArticleIdList={ArticleId={IdType=doi, =10.1039/b916446d}}}
  public PubmedArticle PubmedArticle;
}
