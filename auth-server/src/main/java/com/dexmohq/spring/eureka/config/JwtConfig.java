package com.dexmohq.spring.eureka.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

@Configuration
public class JwtConfig {

    @Value("${jwt.signing.rsa.private-key}")
    private String privateRsaKey;

    @Bean
    public KeyPair jwtKeyPair() throws NoSuchAlgorithmException, InvalidKeySpecException {

        final KeyFactory kf = KeyFactory.getInstance("RSA");
        final PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateRsaKey));
        final RSAPrivateCrtKey privateKey = (RSAPrivateCrtKey) kf.generatePrivate(privateKeySpec);

        final RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(privateKey.getModulus(), privateKey.getPublicExponent());
        final PublicKey publicKey = kf.generatePublic(publicKeySpec);
        return new KeyPair(publicKey, privateKey);
    }

}
