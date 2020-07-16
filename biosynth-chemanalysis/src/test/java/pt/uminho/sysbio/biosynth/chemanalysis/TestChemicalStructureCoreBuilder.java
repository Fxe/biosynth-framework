package pt.uminho.sysbio.biosynth.chemanalysis;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestChemicalStructureCoreBuilder {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
//    OpenBabelWrapper.initializeLibrary();
  }
//
//	@Test
//	public void testBuildFromCML() {
//		ChemicalStructureCore example = ChemicalStructureCoreBuilder.build("<cml>"
//				+ "<molecule id='CPD-12377' title='hydroxyl+radical' formalCharge='0'>"
//                + "<atomArray>"
//          + "<atom id='CPD-12377-atom1' elementType='O' x2='-999.0' y2='-407.0'/>"
//         + "</atomArray>"
//        + "<bondArray/>"
//         + "<formula concise='H 1 O 1'/>"
//       + "<float title='molecularWeight' units='g/mol'>18.015</float>"
//       + "<string title='smiles'>O</string>"
//      + "</molecule>"
//    + "</cml>", "cml");
//		System.out.println(example);
//		assertEquals("O", example.getFormula());
//		assertEquals("[O]	hydroxyl+radical", example.getCan());
//		assertEquals("QVGXLLKOCUKJST-UHFFFAOYSA-N", example.getInchiKey());
//	}
//
  @Test
  public void testBuildFromInchi() {
//    ChemicalStructureCore example = ChemicalStructureCoreBuilder.build("InChI=1S/5C7H12N2O4.3Al.4H2O/c5*1-4(10)9-5(7(12)13)2-3-6(8)11;;;;;;;/h5*5H,2-3H2,1H3,(H2,8,11)(H,9,10)(H,12,13);;;;4*1H2/q;;;;;3*+3;;;;/p-9/t5*5-;;;;;;;/m00000......./s1", "inchi");
//    System.out.println(example);
//    assertEquals("C35H59Al3N10O24", example.getFormula());
//    assertEquals("[O-]C(=N)CC[C@@H](C(=O)[O-])N=C(C)[O-].[O-]C(=N)CC[C@@H](C(=O)[O-])N=C(C)[O-].[O-]C(=N)CC[C@@H](C(=O)[O-])N=C(C)[O-].OC(=N)CC[C@@H](C(=O)O)N=C(O)C.OC(=N)CC[C@@H](C(=O)O)N=C(O)C.O.O.O.O.[Al+3].[Al+3].[Al+3]", example.getCan());
//    assertEquals("IVVHAAOJLULJLK-YDXSIYMFSA-E", example.getInchiKey());
  }
}
