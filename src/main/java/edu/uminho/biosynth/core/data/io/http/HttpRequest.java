package edu.uminho.biosynth.core.data.io.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

public class HttpRequest {
	
	private final static Logger LOGGER = Logger.getLogger(HttpRequest.class.getName());
	
	public static String get(String url) {
		LOGGER.log(Level.INFO, "HttpRequest - " + url);
		
		
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(url);
		StringBuilder ret = new StringBuilder();
		try {
			client.executeMethod(method);
			BufferedReader buffer = new BufferedReader( new InputStreamReader( method.getResponseBodyAsStream()));
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
