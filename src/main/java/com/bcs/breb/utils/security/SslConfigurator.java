// src/main/java/com/bcs/breb/utils/security/SslConfigurator.java
package com.bcs.breb.utils.security;

import com.bcs.breb.utils.config.ConfigReader;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import javax.net.ssl.*;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Collections;

public class SslConfigurator {

    /**
     * Crea un SSLContext que:
     *  - use KeyManagers con tu certificado de cliente
     *  - use un TrustManager que confíe en **todos** los certificados de servidor
     */
    public static SSLContext createSSLContext() {
        try {
            Security.addProvider(new BouncyCastleProvider());

            // 1) Leer Base64 desde .properties
            String certB64 = ConfigReader.get("ssl.client.cert.base64");
            String keyB64  = ConfigReader.get("ssl.client.key.base64");
            String caB64   = ConfigReader.get("ssl.client.ca.base64");

            // 2) Parsear certificado y clave
            X509Certificate clientCert = parseCertificate(new String(Base64.getDecoder().decode(certB64)));
            PrivateKey privateKey      = parsePrivateKey(new String(Base64.getDecoder().decode(keyB64)));

            // 3) Construir KeyStore con tu certificado de cliente
            char[] pwd = "".toCharArray();
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(null, pwd);
            keyStore.setKeyEntry("client", privateKey, pwd, new java.security.cert.Certificate[]{clientCert});

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keyStore, pwd);
            KeyManager[] keyManagers = kmf.getKeyManagers();

            // 4) Crear TrustManager “trust-all” para no validar servidor
            TrustManager[] trustAll = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                }
            };

            // 5) Inicializar SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, trustAll, null);

            return sslContext;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Error configurando MTLS desde base64", e);
        }
    }

    private static X509Certificate parseCertificate(String pem) throws Exception {
        String normalized = pem
                .replaceAll("-----BEGIN CERTIFICATE-----", "")
                .replaceAll("-----END CERTIFICATE-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(normalized);
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(decoded));
    }

    private static PrivateKey parsePrivateKey(String pem) throws Exception {
        try (PEMParser pemParser = new PEMParser(new StringReader(pem))) {
            Object object = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
            if (object instanceof PEMKeyPair) {
                return converter.getKeyPair((PEMKeyPair) object).getPrivate();
            } else if (object instanceof PrivateKeyInfo) {
                return converter.getPrivateKey((PrivateKeyInfo) object);
            } else {
                throw new IllegalArgumentException("Formato de clave privada no reconocido");
            }
        }
    }
}
