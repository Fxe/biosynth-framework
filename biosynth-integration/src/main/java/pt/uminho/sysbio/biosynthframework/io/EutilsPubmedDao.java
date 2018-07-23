package pt.uminho.sysbio.biosynthframework.io;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import pt.uminho.sysbio.biosynthframework.biodb.eutils.EntrezTaxonomyConverter;
import pt.uminho.sysbio.biosynthframework.biodb.eutils.EutilsPubmedObject;
import pt.uminho.sysbio.biosynthframework.biodb.eutils.EutilsService;
import pt.uminho.sysbio.biosynthframework.biodb.eutils.PubmedEntity;
import pt.uminho.sysbio.biosynthframework.io.biodb.Bigg2ApiService;
import retrofit2.Retrofit;

public class EutilsPubmedDao implements BiosDao<PubmedEntity> {
  public static final String defaultEndpoint = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils";

  private final EutilsService service;

  public EutilsPubmedDao() {

    long connectionTimeout = 60;
    long readTimeout = 60;
    final OkHttpClient okHttpClient = new OkHttpClient();
    Retrofit retrofit = new Retrofit.Builder().client(okHttpClient)
                                              .baseUrl(defaultEndpoint)
                                              .addConverterFactory(new EntrezTaxonomyConverter(null))
                                              .build();
    service = retrofit.create(EutilsService.class);
//    final OkHttpClient okHttpClient = new OkHttpClient();
////    okHttpClient.setReadTimeout(readTimeout, TimeUnit.SECONDS);
////    okHttpClient.setConnectTimeout(connectionTimeout, TimeUnit.SECONDS);
//    RestAdapter restAdapter = new RestAdapter.Builder()
//        .setConverter(new EntrezTaxonomyConverter())
//        .setLogLevel(LogLevel.NONE)
//        .setClient(new OkClient(okHttpClient))
//        .setEndpoint(defaultEndpoint)
//        .build();
//    service = restAdapter.create(EutilsService.class);

  }

  public static PubmedEntity convert(String pmid, EutilsPubmedObject o) {
    String title = o.PubmedArticle.MedlineCitation.Article.ArticleTitle;
    String doi = null;
    if (o.PubmedArticle.MedlineCitation.Article.ELocationID != null && 
        o.PubmedArticle.MedlineCitation.Article.ELocationID.get("EIdType").equals("doi")) {
      doi = (String) o.PubmedArticle.MedlineCitation.Article.ELocationID.get("");
    } else {
      doi = null;
    }
    String journal = (String) o.PubmedArticle.MedlineCitation.Article.Journal.get("Title");
    String journalAbbr = (String) o.PubmedArticle.MedlineCitation.Article.Journal.get("ISOAbbreviation");

    PubmedEntity entity = new PubmedEntity();
    entity.setEntry(pmid);
    entity.setJournal(journal);
    entity.setJournalAbbreviation(journalAbbr);
    entity.setTitle(title);
    entity.setSource("eutils");
    entity.setDoi(doi);
    return entity;
  }


  public PubmedEntity getByEntry(String entry) {
    EutilsPubmedObject o = service.efetchPubmed(entry, "xml");
    return convert(entry, o);
  }

  @Override
  public PubmedEntity getById(long id) {
    throw new RuntimeException("not supported");
  }

  @Override
  public Long save(PubmedEntity o) {
    throw new RuntimeException("not supported");
  }

  @Override
  public Set<Long> getAllIds() {
    throw new RuntimeException("not supported");
  }

  @Override
  public Set<String> getAllEntries() {
    throw new RuntimeException("not supported");
  }

  @Override
  public boolean delete(PubmedEntity o) {
    throw new RuntimeException("not supported");
  }
}
