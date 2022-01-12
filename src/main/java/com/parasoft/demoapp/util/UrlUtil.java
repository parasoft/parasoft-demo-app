package com.parasoft.demoapp.util;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtil {

    public static final String URI_WITH_RESTFUL_API_VERSION_REGEX = "^/(?i)v[0-9]+(.[0-9]+(.[0-9])?)?/+.*";

    public static final String WELL_FORMED_URL_REGEX = "^(https?)://([a-zA-Z0-9-_]+.?)*[a-zA-Z0-9-_]+(((/[\\S]+))?/?)$";

	/**
	 * Validate specific URL.
	 *
	 * @param urlStr
	 * @return http response code
	 * @throws IOException
	 */
	public static int validateUrl(String urlStr) throws IOException {

        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setUseCaches(false);
        con.setConnectTimeout(1000);
        con.connect();
        return con.getResponseCode();
    }
    
    public static boolean isGoodHttpForm(String urlStr) {
    	if(urlStr == null) {
    		return false;
    	}
    	
    	Pattern pattern = Pattern.compile(WELL_FORMED_URL_REGEX);
    	Matcher matcher = pattern.matcher(urlStr);
    	
    	return matcher.matches();
    }

    /**
     * Decide the uri is restful or not.</br>
     * It's restful api if the uri starts with /v1 /v1.0 /v1.0.0, otherwise it is not.
     * @param req HttpServletRequest
     * @return true or false
     */
    public static boolean isRestfulApiRequest(HttpServletRequest req) {
        String uri = req.getRequestURI();
        return Pattern.matches(URI_WITH_RESTFUL_API_VERSION_REGEX, uri);
    }
}
