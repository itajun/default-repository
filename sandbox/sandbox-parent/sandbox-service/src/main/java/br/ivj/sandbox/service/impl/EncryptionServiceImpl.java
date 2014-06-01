package br.ivj.sandbox.service.impl;

import org.jasypt.digest.StandardStringDigester;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.stereotype.Service;

import br.ivj.sandbox.service.EncryptionService;

@Service("encryptionService")
public class EncryptionServiceImpl implements EncryptionService {
	private static final int DIGEST_INTERATIONS = 5;
	private static final String DEFAULT_PASSWORD = "mYDeFaUlTpAsS";

	@Override
	public String generatePasswordHash(String password) {
		BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
		return passwordEncryptor.encryptPassword(password);
	}

	@Override
	public String encryptMessage(String message) {
		return encryptMessage(message, DEFAULT_PASSWORD);
	}

	@Override
	public String decryptMessage(String message) {
		return decryptMessage(message, DEFAULT_PASSWORD);
	}

	@Override
	public String digestMessage(String message) {
		StandardStringDigester digester = new StandardStringDigester();
		digester.setIterations(DIGEST_INTERATIONS);
		return digester.digest(message);
	}

	@Override
	public boolean verifyDigest(String message, String digest) {
		StandardStringDigester digester = new StandardStringDigester();
		digester.setIterations(5);
		return digester.matches(message, digest);
	}

	@Override
	public String encryptMessage(String message, String password) {
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword(password);
		return textEncryptor.encrypt(message);
	}

	@Override
	public String decryptMessage(String message, String password) {
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword(password);
		return textEncryptor.decrypt(message);
	}

	@Override
	public boolean verifyPassword(String password, String hash) {
		BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
		return passwordEncryptor.checkPassword(password, hash);
	}
}
