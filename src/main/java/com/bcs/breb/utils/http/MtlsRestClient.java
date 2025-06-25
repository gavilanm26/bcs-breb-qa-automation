// src/main/java/com/bcs/breb/utils/http/MtlsRestClient.java
package com.bcs.breb.utils.http;

import com.bcs.breb.utils.security.SslConfigurator;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;

import javax.net.ssl.SSLContext;

public class MtlsRestClient {

    /**
     * Construye un HttpClient (DefaultHttpClient) configurado con nuestro SSLContext de MTLS.
     */
    public static HttpClient build() {
        try {
            SSLContext sslContext = SslConfigurator.createSSLContext();

            SSLSocketFactory socketFactory =
                    new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("https", 443, socketFactory));
            schemeRegistry.register(new Scheme("http",  80, PlainSocketFactory.getSocketFactory()));

            BasicHttpParams params = new BasicHttpParams();
            ClientConnectionManager cm = new SingleClientConnManager(params, schemeRegistry);

            return new DefaultHttpClient(cm, params);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Error creando cliente MTLS", e);
        }
    }
}
