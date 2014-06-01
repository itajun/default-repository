package br.ivj.sandbox.service;

public interface EncryptionService {
	public String generatePasswordHash(String password);
	
	public boolean verifyPassword(String password, String hash);

	public String encryptMessage(String message);

	public String decryptMessage(String message);

	public String encryptMessage(String message, String password);

	public String decryptMessage(String message, String password);

	public String digestMessage(String message);

	public boolean verifyDigest(String message, String digest);
}
