package br.ivj.sandbox.test.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.ivj.sandbox.service.EncryptionService;

/**
 * Simple test of jasypt features. Download the package from <a
 * href="http://www.jasypt.org/cli.html">here</a> and run:<br>
 * <code>encrypt input="This is a test message" password=myPass</code><br>
 * <code>decrypt input="ax+oN4oxB/pvP7vmHVtcDiH9CjFAFKuoeN9GsqN89xM=" password=myPass</code>
 * <br>
 * You can change the algorithm, but basically you just need to encrypt it, copy
 * and paste in the properties file, configure the Encryptor bean and voilà.<br>
 * Here we also perform tests with the Encryptor bean for messages that are not
 * stored in the properties file.
 * 
 * @author Itamar
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/app-context-service-test.xml",
		"/app-context-persistence-test.xml" })
@ActiveProfiles(profiles = { "development" })
public class CryptographyTests {
	@Autowired
	@Qualifier("encryptionService")
	private EncryptionService encryptionService;
	
	private static final String MY_TEST_PASS = "MY_TEST_PASS";
	private static final String MY_TEST_MESSAGE = "Test message";

	@Autowired
	@Qualifier("myEncriptedMessage")
	private String myEncriptedMessage;

	@Autowired
	@Qualifier("myPlainMessage")
	private String myPlainMessage;

	@Test
	public void correctEncryptedContextProperties() {
		assertEquals("This is a [plain] test message", myPlainMessage);
		assertEquals("This is a test message", myEncriptedMessage);
	}

	@Test
	public void checkPassword() {
		String passwordHash1 = encryptionService.generatePasswordHash(MY_TEST_PASS);
		String passwordHash2 = encryptionService.generatePasswordHash(MY_TEST_PASS);
		assertTrue(encryptionService.verifyPassword(MY_TEST_PASS, passwordHash1));
		assertTrue(encryptionService.verifyPassword(MY_TEST_PASS, passwordHash2));
		// They can actually be equal, but it would be so rare that I decided to leave it here
		assertNotEquals(passwordHash1, passwordHash2);
		assertFalse(encryptionService.verifyPassword(MY_TEST_PASS + "X", passwordHash2));
	}

	@Test
	public void checkMessageEncryption() {
		String defaultPassEncMessage = encryptionService.encryptMessage(MY_TEST_MESSAGE);
		String customPassEncMessage = encryptionService.encryptMessage(MY_TEST_MESSAGE, MY_TEST_PASS);
		String defaultPassDecMessage = encryptionService.decryptMessage(defaultPassEncMessage);
		String customPassDecMessage = encryptionService.decryptMessage(customPassEncMessage, MY_TEST_PASS);
		assertEquals(defaultPassDecMessage, customPassDecMessage);
		assertEquals(defaultPassDecMessage, MY_TEST_MESSAGE);
		assertNotEquals(defaultPassEncMessage, customPassEncMessage);
		assertNotEquals(defaultPassEncMessage, MY_TEST_MESSAGE);
	}
	
	@Test
	public void checkMessageDigest() {
		String digest1 = encryptionService.digestMessage(MY_TEST_MESSAGE);
		assertTrue(encryptionService.verifyDigest(MY_TEST_MESSAGE, digest1));
	}
	
}
