package dev.aries.iijra.security;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

import com.nimbusds.jose.jwk.KeyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeyUtil {

	private static final String DIRECTORY_PATH = "jwt-token-keys";

	@Value("${jwt.token.access.private}")
	private String tokenPrivateKeyPath;
	@Value("${jwt.token.access.public}")
	private String tokenPublicKeyPath;

	private KeyPair tokenKeyPair;

	public RSAPublicKey getTokenPublicKey() {
		return (RSAPublicKey) getTokenKeyPair().getPublic();
	}

	public RSAPrivateKey getTokenPrivateKey() {
		return (RSAPrivateKey) getTokenKeyPair().getPrivate();
	}

	private KeyPair getTokenKeyPair() {
		if (Objects.isNull(tokenKeyPair)) {
			tokenKeyPair = loadOrCreateKeyPair(
					tokenPublicKeyPath, tokenPrivateKeyPath
			);
		}
		return tokenKeyPair;
	}

	private KeyPair loadOrCreateKeyPair(String publicKeyPath, String privateKeyPath) {
		if (keyFilesExist(publicKeyPath, privateKeyPath)) {
			return loadKeyPair(publicKeyPath, privateKeyPath);
		} else {
			ensureDirectoryExists(DIRECTORY_PATH);
			return generateKeyPair(publicKeyPath, privateKeyPath);
		}
	}

	private boolean keyFilesExist(String publicKeyPath, String privateKeyPath) {
		return new File(publicKeyPath).exists() && new File(privateKeyPath).exists();
	}

	private KeyPair loadKeyPair(String publicKeyPath, String privateKeyPath) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(KeyType.RSA.getValue());
			byte[] publicKeyBytes = Files.readAllBytes(new File(publicKeyPath).toPath());
			PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

			byte[] privateKeyBytes = Files.readAllBytes(new File(privateKeyPath).toPath());
			PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

			return new KeyPair(publicKey, privateKey);
		} catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException ex) {
			throw new IllegalStateException("Key pair not found");
		}
	}

	private void ensureDirectoryExists(String directoryPath) {
		File directory = new File(directoryPath);
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	private KeyPair generateKeyPair(String publicKeyPath, String privateKeyPath) {
		try {
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KeyType.RSA.getValue());
			keyPairGen.initialize(2048);
			KeyPair keyPair = keyPairGen.generateKeyPair();

			saveKeyToFile(publicKeyPath, keyPair.getPublic().getEncoded());
			saveKeyToFile(privateKeyPath, keyPair.getPrivate().getEncoded());

			return keyPair;
		} catch (NoSuchAlgorithmException | IOException ex) {
			throw new IllegalStateException("Failed to generate key pair");
		}
	}

	private void saveKeyToFile(String path, byte[] key) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(path)) {
			fos.write(key);
		}
	}
}
