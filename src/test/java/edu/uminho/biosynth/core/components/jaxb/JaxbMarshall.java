package edu.uminho.biosynth.core.components.jaxb;

import static org.junit.Assert.*;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.components.biodb.kegg.KeggCompoundMetaboliteEntity;

public class JaxbMarshall {

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
		try {
			JAXBContext jc = JAXBContext.newInstance(KeggCompoundMetaboliteEntity.class);
			KeggCompoundMetaboliteEntity cpd = new KeggCompoundMetaboliteEntity();
			cpd.setId(23);
			cpd.setEntry("CPD00002");
			
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
			marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
			StringWriter stringWriter = new StringWriter();
			marshaller.marshal(cpd, stringWriter);
			String jsonStr = stringWriter.toString();
			JSONObject json = new JSONObject(jsonStr);
			System.out.println(json);
		} catch (JAXBException e) {
			System.out.println(e);
		} catch (JSONException e) {
			System.out.println(e);
		}
		fail("Not yet implemented");
	}

}
