package edu.uminho.biosynth.example;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.components.kegg.KeggMetaboliteEntity;
import edu.uminho.biosynth.core.components.kegg.KeggReactionEntity;
import edu.uminho.biosynth.core.components.mnx.MnxMetaboliteEntity;
import edu.uminho.biosynth.core.components.mnx.components.MnxMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.components.mnx.components.MnxReactionCrossReferenceEntity;
import edu.uminho.biosynth.core.data.io.dao.GenericEntityDAO;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;
import edu.uminho.biosynth.optflux.ContainerLoader;
import edu.uminho.biosynth.optflux.parser.DefaultSbmlTransformerImpl;
import edu.uminho.biosynth.util.BioSynthUtilsIO;

public class TestSBMLMapping {

	private static String[] cpdMap = { "h2o", "C00001",
		"atp", "C00002",
		"nad", "C00003",
		"nadh", "C00004",
		"nadph", "C00005",
		"nadp", "C00006",
		"o2", "C00007",
		"adp", "C00008",
		"pi", "C00009",
		"coa", "C00010",
		"co2", "C00011",
		"ppi", "C00013",
		"nh4", "C00014",
		"udp", "C00015",
		"fad", "C00016",
		"pydx5p", "C00018",
		"amet", "C00019",
		"amp", "C00020",
		"ahcys", "C00021",
		"pyr", "C00022",
		"accoa", "C00024",
		"glu-L", "C00025",
		"akg", "C00026",
		"h2o2", "C00027",
		"udpg", "C00029",
		"glc-D", "C00031",
		"ac", "C00033",
		"gdp", "C00035",
		"oaa", "C00036",
		"gly", "C00037",
		"ala-L", "C00041",
		"succ", "C00042",
		"uacgam", "C00043",
		"gtp", "C00044",
		"lys-L", "C00047",
		"glx", "C00048",
		"asp-L", "C00049",
		"gthrd", "C00051",
		"udpgal", "C00052",
		"paps", "C00053",
		"pap", "C00054",
		"cmp", "C00055",
		"for", "C00058",
		"so4", "C00059",
		"fmn", "C00061",
		"arg-L", "C00062",
		"ctp", "C00063",
		"gln-L", "C00064",
		"ser-L", "C00065",
		"thmpp", "C00068",
		"met-L", "C00073",
		"pep", "C00074",
		"utp", "C00075",
		"trp-L", "C00078",
		"phe-L", "C00079",
		"h", "C00080",
		"itp", "C00081",
		"tyr-L", "C00082",
		"malcoa", "C00083",
		"acald", "C00084",
		"f6p", "C00085",
		"urea", "C00086",
		"no2", "C00088",
		"sucr", "C00089",
		"succoa", "C00091",
		"g6p", "C00092",
		"glyc3p", "C00093",
		"so3", "C00094",
		"fru", "C00095",
		"gdpmann", "C00096",
		"cys-L", "C00097",
		"ala-B", "C00099",
		"ppcoa", "C00100",
		"thf", "C00101",
		"g1p", "C00103",
		"idp", "C00104",
		"ump", "C00105",
		"ura", "C00106",
		"anth", "C00108",
		"2obut", "C00109",
		"dhap", "C00111",
		"cdp", "C00112",
		"chol", "C00114",
		"glyc", "C00116",
		"prpp", "C00119",
		"btn", "C00120",
		"rib-D", "C00121",
		"fum", "C00122",
		"leu-L", "C00123",
		"gal", "C00124",
		"gthox", "C00127",
		"ipdp", "C00129",
		"imp", "C00130",
		"datp", "C00131",
		"ala-D", "C00133",
		"ptrc", "C00134",
		"his-L", "C00135",
		"btcoa", "C00136",
		"inost", "C00137",
		"acgam", "C00140",
		"3mob", "C00141",
		"mlthf", "C00143",
		"gmp", "C00144",
		"ade", "C00147",
		"pro-L", "C00148",
		"mal-L", "C00149",
		"asn-L", "C00152",
		"ncam", "C00153",
		"hcys-L", "C00155",
		"4hbz", "C00156",
		"cit", "C00158",
		"man", "C00159",
		"glyclt", "C00160",
		"ppa", "C00163",
		"acac", "C00164",
		"phpyr", "C00166",
		"udpglcur", "C00167",
		"hpyr", "C00168",
		"cbp", "C00169",
		"5mta", "C00170",
		"thym", "C00178",
		"agm", "C00179",
		"xyl-D", "C00181",
		"val-L", "C00183",
		"dha", "C00184",
		"lac-L", "C00186",
		"thr-L", "C00188",
		"etha", "C00189",
		"glcur", "C00191",
		"adocbl", "C00194",
		"23dhb", "C00196",
		"3pg", "C00197",
		"ru5p-D", "C00199",
		"2ddglcn", "C00204",
		"dadp", "C00206",
		"malt", "C00208",
		"adn", "C00212",
		"thymd", "C00214",
		"glu-D", "C00217",
		"aps", "C00224",
		"actp", "C00227",
		"ACP", "C00229",
		"xu5p-D", "C00231",
		"sucsal", "C00232",
		"4mop", "C00233",
		"10fthf", "C00234",
		"dmpp", "C00235",
		"13dpg", "C00236",
		"dcmp", "C00239",
		"gua", "C00242",
		"lcts", "C00243",
		"no3", "C00244",
		"taur", "C00245",
		"but", "C00246",
		"hdca", "C00249",
		"pydx", "C00250",
		"chor", "C00251",
		"nac", "C00253",
		"pphn", "C00254",
		"ribflv", "C00255",
		"lac-D", "C00256",
		"glcn", "C00257",
		"glyc-R", "C00258",
		"arab-L", "C00259",
		"hxan", "C00262",
		"hom-L", "C00263",
		"gcald", "C00266",
		"acnam", "C00270",
		"man6p", "C00275",
		"e4p", "C00279",
		"h2", "C00282",
		"h2s", "C00283",
		"dgtp", "C00286",
		"hco3", "C00288",
		"ins", "C00294",
		"orot", "C00295",
		"uri", "C00299",
		"xylu-D", "C00310",
		"icit", "C00311",
		"pydxn", "C00314",
		"spmd", "C00315",
		"crn", "C00318",
		"tsul", "C00320",
		"gdpfuc", "C00325",
		"citr-L", "C00327",
		"gam", "C00329",
		"dgsn", "C00330",
		"aacoa", "C00332",
		"galur", "C00333",
		"4abut", "C00334",
		"dhor-S", "C00337",
		"grdp", "C00341",
		"trdrd", "C00342",
		"trdox", "C00343",
		"6pgc", "C00345",
		"gam6p", "C00352",
		"fdp", "C00354",
		"acgam6p", "C00357",
		"damp", "C00360",
		"dgdp", "C00361",
		"dgmp", "C00362",
		"dtdp", "C00363",
		"dtmp", "C00364",
		"dump", "C00365",
		"thm", "C00378",
		"csn", "C00380",
		"xan", "C00385",
		"gsn", "C00387",
		"mnl", "C00392",
		"ile-L", "C00407",
		"dhf", "C00415",
		"cinnm", "C00423",
		"lald-L", "C00424",
		"5aop", "C00430",
		"acorn", "C00437",
		"cbasp", "C00438",
		"5mthf", "C00440",
		"aspsa", "C00441",
		"methf", "C00445",
		"gal1p", "C00446",
		"frdp", "C00448",
		"nmn", "C00455",
		"dctp", "C00458",
		"dttp", "C00459",
		"dutp", "C00460",
		"indole", "C00463",
		"etoh", "C00469",
		"cytd", "C00475",
		"skm", "C00493",
		"adpglc", "C00498",
		"alltt", "C00499",
		"rmn", "C00507",
		"rbl-L", "C00508",
		"mana", "C00514",
		"pant-R", "C00522",
		"duri", "C00526",
		"hqn", "C00530",
		"pydam", "C00534",
		"pppi", "C00536",
		"mthgxl", "C00546",
		"4abutn", "C00555",
		"tagur", "C00558",
		"dad-2", "C00559",
		"tma", "C00565",
		"4abz", "C00568",
		"cdpea", "C00570",
		"camp", "C00575",
		"betald", "C00576",
		"glyald", "C00577",
		"dms", "C00580",
		"phaccoa", "C00582",
		"op4en", "C00596",
		"pacald", "C00601",
		"urdglyc", "C00603",
		"N1aspmd", "C00612",
		"3dhguln", "C00618",
		"r1p", "C00620",
		"acglu", "C00624",
		"pdx5p", "C00627",
		"2pg", "C00631",
		"man1p", "C00636",
		"12dgr_EC", "C00641",
		"mnl1p", "C00644",
		"acmana", "C00645",
		"pyam5p", "C00647",
		"xmp", "C00655",
		"26dap-LL", "C00666",
		"glucys", "C00669",
		"g3pc", "C00670",
		"3mop", "C00671",
		"2dr1p", "C00672",
		"2dr5p", "C00673",
		"5dh4dglc", "C00679",
		"26dap-M", "C00680",
		"mmcoa-S", "C00683",
		"dtdp4d6dm", "C00688",
		"tre6p", "C00689",
		"uamag", "C00692",
		"o2s", "C00704",
		"dcdp", "C00705",
		"glyb", "C00719",
		"ser-D", "C00740",
		"sheme", "C00748",
		"idon-L", "C00770",
		"sbt-D", "C00794",
		"altrn", "C00817",
		"glcr", "C00818",
		"dtdpglu", "C00842",
		"cbl1", "C00853",
		"met-D", "C00855",
		"dnad", "C00857",
		"histd", "C00860",
		"rml", "C00861",
		"pnto-R", "C00864",
		"galct-D", "C00879",
		"galctn-D", "C00880",
		"dcyt", "C00881",
		"dpcoa", "C00882",
		"ichor", "C00885",
		"tartr-L", "C00898",
		"fruur", "C00905",
		"dhpt", "C00921",
		"ppbng", "C00931",
		"3dhq", "C00944",
		"2dhp", "C00966",
		"acser", "C00979",
		"2pglyc", "C00988",
		"alaala", "C00993",
		"pser-L", "C01005",
		"fuc-L", "C01019",
		"hmbil", "C01024",
		"n8aspmd", "C01029",
		"dann", "C01037",
		"uamr", "C01050",
		"uppg3", "C01051",
		"5dglcn", "C01062",
		"pmcoa", "C01063",
		"pppg9", "C01079",
		"thmmp", "C01081",
		"tre", "C01083",
		"8aonn", "C01092",
		"f1p", "C01094",
		"sbt6p", "C01096",
		"tag6p-D", "C01097",
		"fc1p", "C01099",
		"hisp", "C01100",
		"ru5p-L", "C01101",
		"phom", "C01102",
		"orot5p", "C01103",
		"tmao", "C01104",
		"ara5p", "C01112",
		"suchms", "C01118",
		"rml1p", "C01131",
		"pan4p", "C01134",
		"ametam", "C01137",
		"2h3oppan", "C01146",
		"glu5sa", "C01165",
		"uacmam", "C01170",
		"mi1p-D", "C01177",
		"34hpp", "C01179",
		"2kmb", "C01180",
		"nicrnt", "C01185",
		"kdo", "C01187",
		"octeACP", "C01203",
		"malACP", "C01209",
		"uama", "C01212",
		"mmcoa-R", "C01213",
		"2dh3dgal", "C01216",
		"gdpddman", "C01222",
		"g3pi", "C01225",
		"g3pe", "C01233",
		"6pgl", "C01236",
		"acg5sa", "C01250",
		"ap4a", "C01260",
		"gp4g", "C01261",
		"imacp", "C01267",
		"5apru", "C01268",
		"3psme", "C01269",
		"4ahmmp", "C01279",
		"2dh3dgal6p", "C01286",
		"unaga", "C01289",
		"2cpr5p", "C01302",
		"25drapp", "C01304",
		"cyan", "C01326",
		"dudp", "C01346",
		"fadh2", "C01352",
		"cynt", "C01417",
		"seln", "C01528",
		"ocdca", "C01530",
		"alltn", "C01551",
		"orn", "C01602",
		"trnaglu", "C01641",
		"15dap", "C01672",
		"galt", "C01697",
		"fcl-L", "C01721",
		"tcynt", "C01755",
		"xtsn", "C01762",
		"dtbt", "C01909",
		"ppp9", "C02191",
		"2mcit", "C02225",
		"cyst-L", "C02291",
		"acon-T", "C02341",
		"dscl", "C02463",
		"3c3hmp", "C02504",
		"suc6p", "C02591",
		"2ippm", "C02631",
		"3dhsk", "C02637",
		"sucbz", "C02730",
		"prbatp", "C02739",
		"prbamp", "C02741",
		"25dkglcn", "C02780",
		"ppap", "C02876",
		"12ppd-S", "C02917",
		"fuc1p-L", "C02985",
		"glutrna", "C02987",
		"malt6p", "C02995",
		"4pasp", "C03082",
		"5mtr", "C03089",
		"pram", "C03090",
		"dmbzid", "C03114",
		"skm5p", "C03175",
		"3php", "C03232",
		"cpppg3", "C03263",
		"glu5p", "C03287",
		"xu5p-L", "C03291",
		"sucarg", "C03296",
		"dtdprmn", "C03319",
		"air", "C03373",
		"4per", "C03393",
		"argsuc", "C03406",
		"sucorn", "C03415",
		"lgt-S", "C03451",
		"4ppan", "C03492",
		"3ig3p", "C03506",
		"2aobut", "C03508",
		"rhcys", "C03539",
		"4h2opntn", "C03589",
		"dhna", "C03657",
		"apoACP", "C03688",
		"quln", "C03722",
		"udpgalfur", "C03733",
		"glu1sa", "C03741",
		"dcamp", "C03794",
		"gar", "C03838",
		"pgp_EC", "C03892",
		"acACP", "C03939",
		"thdp", "C03972",
		"23dhba", "C04030",
		"dhpppn", "C04044",
		"ap5a", "C04058",
		"ctbt", "C04114",
		"ckdo", "C04121",
		"acg5p", "C04133",
		"octdp", "C04146",
		"5mdr1p", "C04188",
		"2mcacn", "C04225",
		"3c4mop", "C04236",
		"acmanap", "C04257",
		"23dhmb", "C04272",
		"4mhetz", "C04294",
		"pran", "C04302",
		"1pyr5c", "C04322",
		"4mpetz", "C04327",
		"dmlz", "C04332",
		"dtdp4addg", "C04346",
		"4ppcys", "C04352",
		"fgam", "C04376",
		"3c2hmp", "C04411",
		"sl26da", "C04421",
		"amob", "C04425",
		"2ddg6p", "C04442",
		"5aprbu", "C04454",
		"sl2a6o", "C04462",
		"kdo8p", "C04478",
		"hkndd", "C04479",
		"acgam1p", "C04501",
		"4ampm", "C04556",
		"23doguln", "C04575",
		"5mdru1p", "C04582",
		"micit", "C04593",
		"uaccg", "C04631",
		"fpram", "C04640",
		"u23ga", "C04652",
		"eig3p", "C04666",
		"aicar", "C04677",
		"3hmrsACP", "C04688",
		"2dda7p", "C04691",
		"4r5au", "C04732",
		"fprica", "C04734",
		"u3aga", "C04738",
		"5aizc", "C04751",
		"2mahmp", "C04752",
		"5prdmbz", "C04778",
		"25aics", "C04823",
		"lipidX", "C04824",
		"dhnpt", "C04874",
		"ugmd", "C04877",
		"ugmda", "C04882",
		"ahdt", "C04895",
		"prfp", "C04896",
		"prlp", "C04916",
		"lipidA", "C04919",
		"lipidAds", "C04932",
		"selnp", "C05172",
		"ddcaACP", "C05223",
		"peamn", "C05332",
		"s7p", "C05382",
		"melib", "C05402",
		"din", "C05512",
		"pppn", "C05629",
		"gtspmd", "C05730",
		"actACP", "C05744",
		"myrsACP", "C05761",
		"cbi", "C05774",
		"rdmbzi", "C05775",
		"scl", "C05778",
		"3ophb", "C05809",
		"2oph", "C05810",
		"2ohph", "C05811",
		"2omph", "C05812",
		"2shchc", "C05817",
		"enter", "C05821",
		"iasp", "C05840",
		"uagmda", "C05897",
		"uaagmda", "C05898",
		"dhpmp", "C05925",
		"sucglu", "C05931",
		"sucgsa", "C05932",
		"2ahbut", "C06006",
		"23dhmp", "C06007",
		"alac-S", "C06010",
		"u3hga", "C06022",
		"kdolipid4", "C06024",
		"kdo2lipid4", "C06025",
		"lipa", "C06026",
		"ohpb", "C06054",
		"phthr", "C06055",
		"4hthr", "C06056",
		"gam1p", "C06156",
		"arbt6p", "C06187",
		"uacmamu", "C06240",
		"kdo2lipid4L", "C06251",
		"dxyl", "C06257",
		"galt1p", "C06311",
		"ttdca", "C06424",
		"2dhglcn", "C06473",
		"adocbi", "C06508",
		"adocbip", "C06509",
		"agdpcbi", "C06510",
		"aacald", "C06735",
		"pac", "C07086",
		"gmhep1p", "C07838",
		"hdcea", "C08362",
		"dmso", "C11143",
		"4adcho", "C11355",
		"2me4p", "C11434",
		"4c2me", "C11435",
		"2p4c2me", "C11436",
		"dxyl5p", "C11437",
		"2mecdp", "C11453",
		"3hpppn", "C11457",
		"gmhep17bp", "C11472",
		"aconm", "C11514",
		"cechddd", "C11588",
		"h2mb4p", "C11811",
		"dhptd", "C11838",
		"dtdp4d6dg", "C11926",
		"3hcinnm", "C12621",
		"cenchddd", "C12622",
		"dhcinnm", "C12623",
		"hkntd", "C12624",
		"fe2", "C14818",
		"3dhgulnp", "C14899",
		"db4p", "C15556",
		"dkmpp", "C15650",
		"5caiz", "C15667",
		"hemeO", "C15672",
		"udcpp", "C17556",
		"q8", "C17569",
		"gmhep7p", "C19882",
		"btnso", "C20386",
		"4hba", "CXXXXX",
		"gbbtn", "CXXXXX",
		"2dhguln", "CXXXXX",
		"6hmhpt", "CXXXXX",
		"eca_EC", "CXXXXX",
		"tdeACP", "CXXXXX",
		"r5p", "CXXXXX",
		"q8h2", "CXXXXX",
		"23ddhb", "CXXXXX",
		"palmACP", "CXXXXX",
		"2dmmq8", "CXXXXX",
		"2omhmbl", "CXXXXX",
		"mql8", "CXXXXX",
		"ctbtcoa", "CXXXXX",
		"g3p", "CXXXXX",
		"unagamu", "CXXXXX",
		"seramp", "CXXXXX",
		"na1", "CXXXXX",
		"dtdp4aaddg", "CXXXXX",
		"crncoa", "CXXXXX",
		"2ommbl", "CXXXXX",
		"ps_EC", "CXXXXX",
		"agpe_EC", "CXXXXX",
		"agpg_EC", "CXXXXX",
		"cdpdag_EC", "CXXXXX",
		"peptido_EC", "CXXXXX",
		"udcpdp", "CXXXXX",
		"mqn8", "CXXXXX",
		"gdpofuc", "CXXXXX",
		"2dmmql8", "CXXXXX",
		"apg_EC", "CXXXXX",
		"bbtcoa", "CXXXXX",
		"kdo2lipid4p", "CXXXXX",
		"g3pg", "CXXXXX",
		"lipa_cold", "CXXXXX",
		"malthp", "CXXXXX",
		"malthx", "CXXXXX",
		"maltpt", "CXXXXX",
		"maltttr", "CXXXXX",
		"malttr", "CXXXXX",
		"unagamuf", "CXXXXX",
		"g3ps", "CXXXXX",
		"hmfurn", "CXXXXX",
		"ocdcea", "CXXXXX",
		"adphep-DD", "CXXXXX",
		"adphep-LD", "CXXXXX",
		"23dhdp", "CXXXXX",
		"ttdcea", "CXXXXX",
		"sbzcoa", "CXXXXX",
		"hdeACP", "CXXXXX",
		"2ombzl", "CXXXXX",
		"pg_EC", "CXXXXX",
		"tagdp-D", "CXXXXX",
		"pa_EC", "CXXXXX",
		"clpn_EC", "CXXXXX",
		"pe_EC", "CXXXXX",
		"6hmhptpp", "CXXXXX",
		"agpc_EC", "CXXXXX",
		"lps_EC", "CXXXXX",
		"ssaltpp", "CXXXXX",
		"pc_EC", "CXXXXX",
		"k", "CXXXXX",
		"glycogen", "CXXXXX",
		"pheme", "CXXXXX", };
	
	
	
	private static SessionFactory sessionFactory;
	private static Map<String, MnxMetaboliteEntity> biggToMnxMap;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Configuration config = new Configuration();
		config.configure();
		ServiceRegistry servReg = 
				new ServiceRegistryBuilder().applySettings(config.getProperties()).buildServiceRegistry();
		sessionFactory = config.buildSessionFactory(servReg);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		sessionFactory.close();
	}

	@Before
	public void setUp() throws Exception {
		sessionFactory.openSession();
	}

	@After
	public void tearDown() throws Exception {
		sessionFactory.getCurrentSession().close();
	}
	
	private void map(String file, GenericEntityDAO dao) throws Exception {
		File sbml = new File(file);
		DefaultSbmlTransformerImpl transformer = new DefaultSbmlTransformerImpl();
//		System.out.println(transformer.normalizeMetaboliteId("M_lald_L_c"));
		
		ContainerLoader loader = new ContainerLoader(sbml, transformer);
		

		for (String cpdId : loader.getMetaboliteIdSet()) {
			StringBuilder sb = new StringBuilder();
			sb.append(cpdId).append("\t");
			if (biggToMnxMap.containsKey(cpdId)) {
				sb.append(biggToMnxMap.get(cpdId).getEntry()).append("\t");
				for (MnxMetaboliteCrossReferenceEntity xref : biggToMnxMap.get(cpdId).getCrossReferences()) {
					if (xref.getRef().toLowerCase().equals("metacyc") /*&& xref.getValue().toLowerCase().charAt(0) == 'c' */) {
						sb.append(xref).append("\t");
//						List<KeggMetaboliteEntity> entry = dao.criteria(KeggMetaboliteEntity.class, Restrictions.eq("entry", xref.getValue()));
//						if (entry.size() == 1) {
//							if (entry.get(0).getReactions().size() > 0) sb.append(xref).append("\t");
//						} else {
//							sb.append("##############OMG###############");
//						}
					}
				}
			} else {
				sb.append("NOTFOUND");
			}
			System.out.println(sb.toString());
		}
		
//		for (String cpdId : loader.getMetaboliteIdSet()) {
//			for (String cpdSpecieId : loader.getMetaboliteSpecieMap().get(cpdId)) {
////				System.out.println(cpdSpecieId + "\t" + );
//			}
//		}
	}
	
	@Test
	public void test2() throws Exception {
		Map<String, String> toKeggMap = new HashMap<> ();
		for (int i = 0; i < cpdMap.length; i+=2) {
			toKeggMap.put(cpdMap[i], cpdMap[i + 1]);
		}
		
		File sbml = new File("./src/main/resources/sbml/iJR904.xml");
		DefaultSbmlTransformerImpl transformer = new DefaultSbmlTransformerImpl();
		ContainerLoader loader = new ContainerLoader(sbml, transformer);
		
		for (String rxnId : loader.getReactionSpecies()) {
			System.out.println(rxnId + "\t" + "RXXXXX" + "\tKEGG_REACTION");
		}
		
//		for (String cpdSiD : loader.getSpecies()) {
//			System.out.println(cpdSiD + "\t" + toKeggMap.get(transformer.normalizeMetaboliteId(cpdSiD)) + "\tKEGG_CPD");
//		}
	}
	
	
	@Test
	public void keggToBigg() {
		GenericEntityDAO dao = new GenericEntityDaoImpl(sessionFactory);
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		String file = BioSynthUtilsIO.readFromFile("E:/kegg_rxn_id.txt");
		String[] rxn = file.split("\n");
		for (String rxnId : rxn) {
			Criterion criterion1 = Restrictions.and(
					Restrictions.eq("ref", "kegg"),
					Restrictions.eq("value", rxnId));
			List<MnxReactionCrossReferenceEntity> res = dao.criteria(MnxReactionCrossReferenceEntity.class, criterion1);
			for (MnxReactionCrossReferenceEntity xref : res) {
				StringBuilder sb = new StringBuilder();
				sb.append(xref.getValue()).append("\t");
				for (MnxReactionCrossReferenceEntity rxnXref : xref.getMnxReactionEntity().getCrossReferences()) {
					if (rxnXref.getRef().equals("bigg")) {
						sb.append(rxnXref.getValue()).append("\t");
					}
				}
				System.out.println(sb.toString());
//				System.out.println();
//				xref.getMnxReactionEntity().getId();
//				Criterion criterion2 = Restrictions.and(
//						Restrictions.eq("id_reaction", "kegg"),
//						Restrictions.eq("ref", "bigg"));
//				List<MnxReactionCrossReferenceEntity> bigg = dao.criteria(MnxReactionCrossReferenceEntity.class, criterion2);
				
			}
//			dao.getReference(MnxReactionCrossReferenceEntity.class, id)
//			System.out.println(rxnId);
		}
		System.out.println(rxn.length);
		tx.commit();
	}
	
	@Test
	public void testMapSbmlMetabolites() throws Exception {
		biggToMnxMap = new HashMap<> ();
		
		GenericEntityDAO dao = new GenericEntityDaoImpl(sessionFactory);
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		List<MnxMetaboliteEntity> metabolites = dao.findAll(MnxMetaboliteEntity.class);
		
		for (MnxMetaboliteEntity mnxCpd : metabolites) {
			for (MnxMetaboliteCrossReferenceEntity xref : mnxCpd.getCrossReferences()) {
				if (xref.getRef().toLowerCase().equals("bigg")) {
					if (biggToMnxMap.put(xref.getValue(), mnxCpd) != null) {
						System.out.println("COLLISION !!! " + mnxCpd);
					}
				}
			}
			
//			System.out.println(mnxCpd.getEntry());
		}
		

		map("./src/main/resources/sbml/iJR904.xml", dao);
		
		tx.commit();
//		System.out.println("####################################################################");
//		System.out.println("####################################################################");
//		System.out.println("####################################################################");
//		map("./src/main/resources/sbml/recon1.xml");
//		System.out.println("####################################################################");
//		System.out.println("####################################################################");
//		System.out.println("####################################################################");
//		map("./src/main/resources/sbml/iSB619.xml");
		
//		ContainerLoader loader = new ContainerLoader();
		
		
		assertEquals(true, true);
	}

}
