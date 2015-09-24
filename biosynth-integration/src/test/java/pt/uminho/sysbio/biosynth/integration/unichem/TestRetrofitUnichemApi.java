package pt.uminho.sysbio.biosynth.integration.unichem;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;

public class TestRetrofitUnichemApi {

	private static RestAdapter restAdapter;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		restAdapter = new RestAdapter.Builder()
									 .setEndpoint("https://www.ebi.ac.uk/unichem/rest/")
									 .setLogLevel(LogLevel.NONE)
									 .build();
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
		UnichemApi unichemApi = restAdapter.create(UnichemApi.class);
		System.out.println(unichemApi.getSourceIds());
	}

}
