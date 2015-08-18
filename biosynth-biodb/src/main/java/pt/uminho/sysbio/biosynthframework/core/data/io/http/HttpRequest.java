package pt.uminho.sysbio.biosynthframework.core.data.io.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(HttpRequest.class);
	
//	public boolean RETRY = false;
//	public long RETRY_DELAY_MILLI = 300000;
	
	public static String get(String urlStr) throws IOException {
		StringBuilder ret = new StringBuilder();
		
		LOGGER.info(String.format("HttpRequest - %s", urlStr));
		
		
		URL url = new URL(urlStr);
		InputStream is = url.openStream();
		String response = IOUtils.toString(is);
		is.close();
//		HttpClient client = HttpClientBuilder.create().build();
////		try {
//			HttpGet httpGet = new HttpGet(url);
//
//			BufferedReader buffer = new BufferedReader( new InputStreamReader( client.execute(httpGet).getEntity().getContent()));
//			String line;
//			while ( (line = buffer.readLine()) != null) {
//				ret.append(line).append('\n');
//			}
//			buffer.close();
//			httpGet.
//		} catch (IOException ioEx) {
//			LOGGER.error(String.format("IO ERROR - %s", ioEx.getMessage()));
//			return null;
//		}
			
		if (ret != null) {
			LOGGER.info("Read {} bytes", response.getBytes().length);
		}

		return response;
	}
	
//	public static String get(String url, int retryAtempts) IOException {
//		int n = 0;
//		boolean success = false;
//		String ret = null;
//		while (!success && n <= retryAtempts) {
//			success = true;
//			ret = get(url);
//			
//			n++;
//			if ( n <= retryAtempts && ret == null) {
//				success = false;
//				LOGGER.warn(String.format("Retry attempt %d - %s", n, url));
//			}
//			
//		}
//		
//		return ret;
//	}
}
