package Booster;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.Properties;

public class UrlConnection {

	public static InputStream setConnection(String stringUrl, Properties vinliProperties) {
		URL url = null;
        HttpURLConnection uc = null;
		try {
			url = new URL(stringUrl);
			uc = (HttpURLConnection)url.openConnection();
			String appid = vinliProperties.getProperty("appId");
			String appsecret = vinliProperties.getProperty("appSecret");
			String userpass = appid + ":" + appsecret;   
			String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
			uc.setRequestProperty("Authorization ", basicAuth);
		    try {
		    	return uc.getInputStream();
			} catch(Exception e) {
				System.out.printf("HTTP Error Code:%d \n", uc.getResponseCode());
				e.printStackTrace();
			}
		} catch (MalformedURLException m) {
			System.out.println(m.getMessage());
			m.printStackTrace();	
		} catch (IOException io) {
			System.out.println(io.getMessage());
			io.printStackTrace();
		}
		return null;
	}
}
