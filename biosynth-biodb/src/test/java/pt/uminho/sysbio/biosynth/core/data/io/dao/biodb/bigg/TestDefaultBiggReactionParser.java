package pt.uminho.sysbio.biosynth.core.data.io.dao.biodb.bigg;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.Orientation;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggReactionEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg.BiggEquationParser;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg.DefaultBiggEquationParserImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg.DefaultBiggReactionParserImpl;

public class TestDefaultBiggReactionParser {

	private static DefaultBiggReactionParserImpl biggReactionParser;
	private static BiggEquationParser equationParser;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		equationParser = new DefaultBiggEquationParserImpl();
		biggReactionParser = new DefaultBiggReactionParserImpl();
		biggReactionParser.setEquationParser(equationParser);
	}
	
	@Test
	public void test_MTCMMT() {
		String reactionRecord = "MTCMMT\tMethylthiol: coenzyme M methyltransferase\t\t[c] : com + dms --> ch4s + mcom\tCytosol\t\tIrreversible\tN\t1800860\t9\t";
		BiggReactionEntity rxn = biggReactionParser.parseReaction(reactionRecord);
		
		assertEquals("MTCMMT", rxn.getEntry());
		assertEquals("Methylthiol: coenzyme M methyltransferase", rxn.getName());
		assertEquals(true, rxn.getSynonyms().isEmpty());
		assertEquals("[c] : com + dms --> ch4s + mcom", rxn.getEquation());
		assertThat(rxn.getCompartments(), hasItems("Cytosol"));
		assertEquals(Orientation.LeftToRight, rxn.getOrientation());
		assertEquals(false, rxn.isTranslocation());
		assertEquals(1800860, (long)rxn.getInternalId());
	}
	
	@Test
	public void test_FAH1() {
		String reactionRecord = "FAH1\tFatty acid omega-hydroxylase\tCytochrome P450\t[c] : ddca + h + nadph + o2 --> h2o + nadp + whddca\tCytosol\t\tIrreversible\tN\t2304732\t2\t";
		BiggReactionEntity rxn = biggReactionParser.parseReaction(reactionRecord);
		
		assertEquals("FAH1", rxn.getEntry());
		assertEquals("Fatty acid omega-hydroxylase", rxn.getName());
		assertThat(rxn.getSynonyms(), hasItems("Cytochrome P450"));
		assertEquals("[c] : ddca + h + nadph + o2 --> h2o + nadp + whddca", rxn.getEquation());
		assertThat(rxn.getCompartments(), hasItems("Cytosol"));
		assertEquals(Orientation.LeftToRight, rxn.getOrientation());
		assertEquals(false, rxn.isTranslocation());
		assertEquals(2304732, (long)rxn.getInternalId());
	}

}
