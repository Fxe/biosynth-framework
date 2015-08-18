package pt.uminho.sysbio.biosynth.core.data.io.dao.biodb.bigg;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg.DefaultBiggEquationParserImpl;

public class TestDefaultBiggEquationParserImpl {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test_GLYtm() {
		String equation = "gly[c] <==> gly[m]";
		DefaultBiggEquationParserImpl equationParserImpl = new DefaultBiggEquationParserImpl(equation);
		equationParserImpl.parse();
		
		assertEquals(1, equationParserImpl.getLeft().size());
		assertEquals(1, equationParserImpl.getRight().size());
//		assertThat(equationParserImpl.getLeft(), hasItems());
	}
	
	@Test
	public void test_FTHFLi() {
		String equation = "[c] : atp + for + thf --> 10fthf + adp + pi";
		DefaultBiggEquationParserImpl equationParserImpl = new DefaultBiggEquationParserImpl(equation);
		equationParserImpl.parse();
		
		assertEquals(3, equationParserImpl.getLeft().size());
		assertEquals(3, equationParserImpl.getRight().size());
	}
	
	@Test
	public void test_H2SO() {
		String equation = "[c] : h2s + (2) o2 --> (2) h + so4";
		DefaultBiggEquationParserImpl equationParserImpl = new DefaultBiggEquationParserImpl(equation);
		equationParserImpl.parse();
		
		assertEquals(2, equationParserImpl.getLeft().size());
		assertEquals(2, equationParserImpl.getRight().size());
	}
	
	@Test
	public void test_ENTCS() {
		String equation = "[c] : (3) 23dhba [deleted 07/14/2004  09:51:42 AM] + (3) seramp --> (6) amp + enter + (6) h";
		DefaultBiggEquationParserImpl equationParserImpl = new DefaultBiggEquationParserImpl(equation);
		equationParserImpl.parse();
		
		assertEquals(2, equationParserImpl.getLeft().size());
		assertEquals(3, equationParserImpl.getRight().size());
	}
	
	@Test
	public void test_DM_dgtp_n() {
		String equation = "[n] : dgtp -->";
		DefaultBiggEquationParserImpl equationParserImpl = new DefaultBiggEquationParserImpl(equation);
		equationParserImpl.parse();
		
		assertEquals(1, equationParserImpl.getLeft().size());
		assertEquals(0, equationParserImpl.getRight().size());
	}
	
	@Test
	public void test_M4ATAer() {
		String equation = "[r] : m(em)3gacpail_hs + pre_prot --> gpi_sig + m(em)3gacpail_prot_hs";
		DefaultBiggEquationParserImpl equationParserImpl = new DefaultBiggEquationParserImpl(equation);
		equationParserImpl.parse();
		
		assertEquals(2, equationParserImpl.getLeft().size());
		assertEquals(2, equationParserImpl.getRight().size());
	}
}
