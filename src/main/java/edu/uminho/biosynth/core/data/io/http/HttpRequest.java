package edu.uminho.biosynth.core.data.io.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class HttpRequest {
	
	private final static Logger LOGGER = Logger.getLogger(HttpRequest.class.getName());
	
	public static String get(String url) {
		LOGGER.log(Level.INFO, "HttpRequest - " + url);
		
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet(url);
		StringBuilder ret = new StringBuilder();
		
		try {
			BufferedReader buffer = new BufferedReader( new InputStreamReader( client.execute(httpGet).getEntity().getContent()));
			String line;
			while ( (line = buffer.readLine()) != null) {
				ret.append(line).append('\n');
			}
		} catch (IOException ioEx) {
			LOGGER.log(Level.SEVERE, "HttpRequest::get - " + ioEx.getMessage() + " - " + url);
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
				LOGGER.log(Level.INFO, "Retry attempt " + n + " - " + url);
			}
			
		}
		
		return ret;
	}
}
