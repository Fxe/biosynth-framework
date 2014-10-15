package pt.uminho.sysbio.biosynth.chemanalysis.cdk;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;

import pt.uminho.sysbio.biosynth.chemanalysis.cdk.CdkWrapper;

public class TestCdkWrapper {

	private static final String MOLFILE1 = " \n \n \n  3  2  0  0  0  0  0  0  0  0999 V2000\n   22.1250  -16.2017    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n   23.6000  -15.2112    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n   20.7129  -15.2859    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n  1  2  1  0     0  0\n  1  3  1  0     0  0\nM  END";
	private static final String MOLFILE2 = " \n \n \n 21 21  0  0  0  0  0  0  0  0999 V2000\n   24.7324  -18.0951    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   23.5001  -18.7842    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   24.7383  -16.6876    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   25.9414  -18.7959    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   22.3029  -18.0774    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   23.5235  -15.9751    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   27.1503  -18.1067    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n   25.9997  -20.2033    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n   22.3088  -16.6817    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   21.0823  -18.7666    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n   28.3651  -18.8076    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   21.0999  -15.9692    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n   29.5798  -18.1125    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   19.8852  -16.6643    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   30.7945  -18.8133    0.0000 N   0  0  3  0  0  0  0  0  0  0  0  0\n   18.6763  -15.9692    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   32.0034  -18.1184    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   30.7888  -20.2150    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   17.4731  -16.6643    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   33.2124  -18.8193    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   31.9918  -20.9275    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n  1  2  1  0     0  0\n  1  3  2  0     0  0\n  1  4  1  0     0  0\n  2  5  2  0     0  0\n  3  6  1  0     0  0\n  4  7  1  0     0  0\n  4  8  2  0     0  0\n  5  9  1  0     0  0\n  5 10  1  0     0  0\n  7 11  1  0     0  0\n  9 12  1  0     0  0\n 11 13  1  0     0  0\n 12 14  1  0     0  0\n 13 15  1  0     0  0\n 14 16  1  0     0  0\n 15 17  1  0     0  0\n 15 18  1  0     0  0\n 16 19  1  0     0  0\n 17 20  1  0     0  0\n 18 21  1  0     0  0\n  6  9  2  0     0  0\nM  END\n\n> <ENTRY>\ncpd:C07383\n\n$$$$";
	
	@Test
	public void testRemoveOnes() {
		String ret = CdkWrapper.toIsotopeMolecularFormula("C1H1O1", false);
		
		assertEquals("CHO", ret);
	}
	
	@Test
	public void testRemoveOnes_BiGG_Invalid_Example() {
		String ret = CdkWrapper.toIsotopeMolecularFormula("C20H29OFULLR2CO", false);

		assertEquals(null, ret);
	}
	
	@Test
	public void testAddOnes() {
		String ret = CdkWrapper.toIsotopeMolecularFormula("CHO", true);
		
		assertEquals("C1H1O1", ret);
	}
	
	@Test
	public void testMol2dToInchi() throws Exception {
		Pair<String, String> inchi = CdkWrapper.convertMol2dToInChI(MOLFILE1);
		assertEquals("InChI=1S/H2O/h1H2", inchi.getLeft());
	}
	
	@Test
	public void testLOL() {
		try {
			System.out.println(CdkWrapper.generateSvg(CdkWrapper.readMol2d(MOLFILE2)));
		
		} catch (CDKException | IOException e) {
			
		}
	}

}
