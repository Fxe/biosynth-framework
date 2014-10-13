package pt.uminho.sysbio.biosynthframework.core.data.io.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

public class HttpRequest {
	
	private final static Logger LOGGER = Logger.getLogger(HttpRequest.class);
	
	public static String get(String url) {
		StringBuilder ret = new StringBuilder();
		
		LOGGER.info(String.format("HttpRequest - %s", url));
		
		HttpClient client = HttpClientBuilder.create().build();
		try {
			HttpGet httpGet = new HttpGet(url);

			BufferedReader buffer = new BufferedReader( new InputStreamReader( client.execute(httpGet).getEntity().getContent()));
			String line;
			while ( (line = buffer.readLine()) != null) {
				ret.append(line).append('\n');
			}
		} catch (IOException ioEx) {
			LOGGER.error(String.format("IO ERROR - %s", ioEx.getMessage()));
			return null;
		}

		return ret.toString();
	}
	
	public static String get(String url, int retryAtempts) {
		int n = 0;
		boolean success = false;
		String ret = null;
		while (!success && n <= retryAtempts) {
			success = true;
			ret = get(url);
			
			n++;
			if ( n <= retryAtempts && ret == null) {
				success = false;
				LOGGER.warn(String.format("Retry attempt %d - %s", n, url));
			}
			
		}
		
		return ret;
	}
}
