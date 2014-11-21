package pt.uminho.sysbio.biosynthframework.annotations;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.AbstractBiosynthEntity;
import pt.uminho.sysbio.biosynthframework.GenericReaction;

public class TestAnnotationPropertyContainerBuilder {

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
	public void test() {

		AbstractBiosynthEntity entity = new AbstractBiosynthEntity() {

			private static final long serialVersionUID = 1L;
			
			@MetaProperty
			private String data = "data";
			
			@MetaProperty
			private String foo = "foo";
			
			@InChIProperty
			private String inchi = "inchi";
		};
		
		AnnotationPropertyContainerBuilder annotationPropertyContainerBuilder =
				new AnnotationPropertyContainerBuilder();
		
		try {
			System.out.println(annotationPropertyContainerBuilder.omg(entity));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Test
	public void test_generic_reaction() {

		GenericReaction entity = new GenericReaction() {

			private static final long serialVersionUID = 1L;
			
			@MetaProperty
			private String data = "data";
			
			@MetaProperty
			private String foo = "foo";
			
			@InChIProperty
			private String inchi = "inchi";
		};
		
		AnnotationPropertyContainerBuilder annotationPropertyContainerBuilder =
				new AnnotationPropertyContainerBuilder();
		
		try {
			System.out.println(annotationPropertyContainerBuilder.omg(entity));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
