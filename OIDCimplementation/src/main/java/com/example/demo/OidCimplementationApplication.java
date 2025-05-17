
package com.example.demo;

import java.security.cert.X509Certificate;
import javax.net.ssl.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OidCimplementationApplication {

    public static void main(String[] args) throws Exception {
      
        disableCertificateValidation();

        
        System.setProperty("com.sun.jndi.ldap.object.disableEndpointIdentification", "true");

        SpringApplication.run(OidCimplementationApplication.class, args);
    }

    
    public static void disableCertificateValidation() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() { return null; }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {}
            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
        }};

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
    }
}


