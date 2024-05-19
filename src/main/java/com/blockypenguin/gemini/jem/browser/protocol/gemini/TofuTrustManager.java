package com.blockypenguin.gemini.jem.browser.protocol.gemini;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.X509TrustManager;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

// SSL Certificates are not something I know a great deal about
// This is a mess
// I hope it works
public class TofuTrustManager implements X509TrustManager {
    private final Map<String, CertificateInfo> certificateInfoMap = new HashMap<>();
    private final Logger LOGGER = LogManager.getLogger("TOFU Trust Manager");

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
        // Client certificate verification not needed for client-side TrustManager
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if(chain == null || chain.length == 0)
            throw new IllegalArgumentException("Certificate chain is null or empty");

        for(int i = 0; i < chain.length; i++) {
            X509Certificate cert = chain[i];

            // Check certificate validity period
            cert.checkValidity();

            if(i > 0) { // Skip the root CA certificate
                X509Certificate issuerCert = chain[i - 1];
                if(!cert.getIssuerX500Principal().equals(issuerCert.getSubjectX500Principal()))
                    throw new CertificateException("Certificate chain is not valid: issuer does not match");
            }

            byte[] fingerprint;

            try {
                fingerprint = calculateFingerprint(cert);
            }catch(NoSuchAlgorithmException e) {
                throw new CertificateException("Could not create SHA-256 Message Digest!", e);
            }

            long expiryDate = cert.getNotAfter().getTime();

            CertificateInfo storedCertificateInfo = certificateInfoMap.get(cert.getSubjectX500Principal().getName());

            LOGGER.info(cert.getSubjectX500Principal().getName());

            if(storedCertificateInfo == null) {
                certificateInfoMap.put(
                    cert.getSubjectX500Principal().getName(),
                    new CertificateInfo(fingerprint, expiryDate)
                );
            }else {
                if(!MessageDigest.isEqual(storedCertificateInfo.fingerprint(), fingerprint))
                    throw new CertificateException("Certificate fingerprint does not match stored value");

                if(expiryDate > storedCertificateInfo.expiryDate())
                    throw new CertificateException("Certificate has expired");
            }
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    private byte[] calculateFingerprint(X509Certificate cert) throws CertificateEncodingException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(cert.getEncoded());
    }

    private static record CertificateInfo(byte[] fingerprint, long expiryDate) {}
}