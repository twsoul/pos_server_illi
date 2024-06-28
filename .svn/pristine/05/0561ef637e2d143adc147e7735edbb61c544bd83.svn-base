package crypto;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;

import able.com.util.crypto.AES256CryptoService;
import able.com.util.crypto.ARIACryptoService;
import able.com.util.crypto.GeneralCryptoService;
import able.com.util.crypto.SHA256PasswordEncoder;
import able.com.util.crypto.TripleDESCryptoService;

/**
 * 암호화 테스트
 * @author hglee
 * @see able.com.util.crypto
 *
 */
public class CryptoTest {

	@Ignore
	@Test
	public void testSHA256PasswordEncoder() throws Exception {
		// ableframe (SHA-256) : RlJC5jTeln5sthKgUYNWxzQpF67U8oODeO/vb3q8fuU=
		String plainPassword = "ableframe";
		
		// SHA-256 (base64)
		String base64String = "RlJC5jTeln5sthKgUYNWxzQpF67U8oODeO/vb3q8fuU=";
		
		
		assertEquals(base64String, SHA256PasswordEncoder.encryptPassword(plainPassword));
		assertTrue(SHA256PasswordEncoder.checkPassword(plainPassword, base64String));
		

		// salt and iteration test
		String salt = "ThisIsSalt";
		
		// salt
		String encWithSalt = "SG1U7VvssZ9fpkLdHszEYDZrHSpbs7MohBsLMD99FXU=";
		// salt + iterations(2)
		String encWithSaltIt2 = "f2LXnxt+bZMSGMnxgLmekkMWV8T8yLbPcIlWfZaJrlU=";
		// salt + iterations(3)
		String encWithSaltIt3 = "DnAK7wkkAZ488io7waKH1WbGyY9fmsn0ybD3evRWnRY=";
		// salt + iterations(4)
		String encWithSaltIt4 = "LGrsX1Z6klaEfxtaK1d+j4TaYdim28makXDFKOlPqPo=";
		// salt + iterations(5)
		String encWithSaltIt5 = "CVIdgcHk4XOvwahEWLi7DySYfBimRWAOnHLqfopViX0=";
		
		assertEquals(encWithSalt, SHA256PasswordEncoder.encryptPassword(plainPassword, salt));
		assertEquals(encWithSalt, SHA256PasswordEncoder.encryptPassword(plainPassword, salt.getBytes()));
		assertEquals(encWithSaltIt2, SHA256PasswordEncoder.encryptPassword(plainPassword, salt, 2));
		assertEquals(encWithSaltIt2, SHA256PasswordEncoder.encryptPassword(plainPassword, salt.getBytes(), 2));
		assertEquals(encWithSaltIt3, SHA256PasswordEncoder.encryptPassword(plainPassword, salt, 3));
		assertEquals(encWithSaltIt3, SHA256PasswordEncoder.encryptPassword(plainPassword, salt.getBytes(), 3));
		assertEquals(encWithSaltIt4, SHA256PasswordEncoder.encryptPassword(plainPassword, salt, 4));
		assertEquals(encWithSaltIt4, SHA256PasswordEncoder.encryptPassword(plainPassword, salt.getBytes(), 4));
		assertEquals(encWithSaltIt5, SHA256PasswordEncoder.encryptPassword(plainPassword, salt, 5));
		assertEquals(encWithSaltIt5, SHA256PasswordEncoder.encryptPassword(plainPassword, salt.getBytes(), 5));
		
		assertTrue(SHA256PasswordEncoder.checkPassword(plainPassword, encWithSalt, salt));
		assertTrue(SHA256PasswordEncoder.checkPassword(plainPassword, encWithSalt, salt.getBytes()));
		assertTrue(SHA256PasswordEncoder.checkPassword(plainPassword, encWithSaltIt2, salt, 2));
		assertTrue(SHA256PasswordEncoder.checkPassword(plainPassword, encWithSaltIt2, salt.getBytes(), 2));
		assertTrue(SHA256PasswordEncoder.checkPassword(plainPassword, encWithSaltIt3, salt, 3));
		assertTrue(SHA256PasswordEncoder.checkPassword(plainPassword, encWithSaltIt3, salt.getBytes(), 3));
		assertTrue(SHA256PasswordEncoder.checkPassword(plainPassword, encWithSaltIt4, salt, 4));
		assertTrue(SHA256PasswordEncoder.checkPassword(plainPassword, encWithSaltIt4, salt.getBytes(), 4));
		assertTrue(SHA256PasswordEncoder.checkPassword(plainPassword, encWithSaltIt5, salt, 5));
		assertTrue(SHA256PasswordEncoder.checkPassword(plainPassword, encWithSaltIt5, salt.getBytes(), 5));
	}
	
	@Ignore
	@Test
	public void testAES256() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, Exception {
		
		String key = "603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4"; //hex - 256bits (32bytes)
		String iv = "000102030405060708090A0B0C0D0E0F"; //Hex - 128bits (16bytes)
		
		String msg = "ableframe";
		
		String encryptedBase64 = "nvtv1CtLNp51m5t1IWQdDA==";
		String encryptedHex = "9efb6fd42b4b369e759b9b7521641d0c";
		
		byte[] keyB = Hex.decodeHex(key.toCharArray());
		byte[] ivB = Hex.decodeHex(iv.toCharArray());
		byte[] msgB = msg.getBytes();
		
		AES256CryptoService aes256srv = new AES256CryptoService();
		
		
		//암호화 검증
		//msg byte[] -> encrypted byte[] -> base64
		assertEquals(encryptedBase64, Base64.encodeBase64String(aes256srv.encrypt(keyB, ivB, msgB)));
		//msg byte[] -> encrypted byte[] -> Hex
		assertEquals(encryptedHex, Hex.encodeHexString(aes256srv.encrypt(keyB, ivB, msgB))) ;
		//msg -> encrypted base64
		assertEquals(encryptedBase64, aes256srv.encrypt(keyB, ivB, msg));
		//msg -> encrypted Hex
		assertEquals(encryptedHex, aes256srv.encryptToHexString(keyB, ivB, msg));


		//복호화 검증
		//encrypted byte[] -> decrypted byte[] 
		assertArrayEquals(msgB, aes256srv.decrypt(keyB, ivB, Base64.decodeBase64(encryptedBase64)));
		//encrypted byte[] -> decrypted byte[]
		assertArrayEquals(msgB, aes256srv.decrypt(keyB, ivB, Hex.decodeHex(encryptedHex.toCharArray())));
		//encrypted base64 -> decrypted msg
		assertEquals(msg, aes256srv.decrypt(keyB, ivB, encryptedBase64));
		//encrypted Hex -> decrypted msg
		assertEquals(msg, aes256srv.decryptHexString(keyB, ivB, encryptedHex));
		
		//랜덤 키, 랜덤 IV 생성
		byte[] randomKeyB = aes256srv.generateRandomKey();
		byte[] randomIvB = aes256srv.generateRandomIV();
		
		assertEquals(32, randomKeyB.length);
		assertEquals(16, randomIvB.length);
	}
	
	@Ignore
	@Test
	public void testTripleDES() throws Exception {
		
		String key = "6e7ae0e33251ec91a7fb62c4b97c1a646d70ab016ba846b6"; //Hex - 192bits (24bytes) 7+1,7+1,7+1
		
		String iv = "0001020304050607"; //Hex - 64bits (8bytes)
		
		String msg = "ableframe";
		
		String encryptedBase64 = "Cp6qGvtTTbnt3Bc07bAptg==";
		String encryptedHex = "0a9eaa1afb534db9eddc1734edb029b6";
		
		byte[] keyB = Hex.decodeHex(key.toCharArray());
		byte[] ivB = Hex.decodeHex(iv.toCharArray());
		byte[] msgB = msg.getBytes();
		
		TripleDESCryptoService tdesSrv = new TripleDESCryptoService();
		
		//암호화 검증
		//msg byte[] -> encrypted byte[] -> base64
		assertEquals(encryptedBase64, Base64.encodeBase64String(tdesSrv.encrypt(keyB, ivB, msgB)));
		//msg byte[] -> encrypted byte[] -> Hex
		assertEquals(encryptedHex, Hex.encodeHexString(tdesSrv.encrypt(keyB, ivB, msgB))) ;
		//msg -> encrypted base64
		assertEquals(encryptedBase64, tdesSrv.encrypt(keyB, ivB, msg));
		//msg -> encrypted Hex
		assertEquals(encryptedHex, tdesSrv.encryptToHexString(keyB, ivB, msg));


		//복호화 검증
		//encrypted byte[] -> decrypted byte[] 
		assertArrayEquals(msgB, tdesSrv.decrypt(keyB, ivB, Base64.decodeBase64(encryptedBase64)));
		//encrypted byte[] -> decrypted byte[]
		assertArrayEquals(msgB, tdesSrv.decrypt(keyB, ivB, Hex.decodeHex(encryptedHex.toCharArray())));
		//encrypted base64 -> decrypted msg
		assertEquals(msg, tdesSrv.decrypt(keyB, ivB, encryptedBase64));
		//encrypted Hex -> decrypted msg
		assertEquals(msg, tdesSrv.decryptHexString(keyB, ivB, encryptedHex));
		
		//랜덤 키 생성
		//랜덤 키, 랜덤 IV 생성
		byte[] randomKeyB = tdesSrv.generateRandomKey();
		byte[] randomIvB = tdesSrv.generateRandomIV();
		
		assertEquals(24, randomKeyB.length);
		assertEquals(8, randomIvB.length);
	}
	
	@Ignore
	@Test
	public void testARIAEngine() {
		//RFC5794 참조 검증용 데이터
		//128bit key
//		   - Key       : 000102030405060708090a0b0c0d0e0f
//		   - Plaintext : 00112233445566778899aabbccddeeff
//		   - Ciphertext: d718fbd6ab644c739da95f3be6451778
		
		/*String key = "000102030405060708090a0b0c0d0e0f";
		   String msg = "00112233445566778899aabbccddeeff";
		   String encrypted = "d718fbd6ab644c739da95f3be6451778"; 
		
		   ARIAEngine instance = new ARIAEngine(128);
		   
		   byte[] keyB = Hex.decodeHex(key.toCharArray());
		   byte[] msgB = Hex.decodeHex(msg.toCharArray());
		   
		   byte[] data = instance.encrypt(msgB, keyB);
		   
		   String dataHex = Hex.encodeHexString(data);
		   System.out.println(dataHex);
		   assertEquals(encrypted, dataHex);*/
		
		//192bit key
//		   Key       : 000102030405060708090a0b0c0d0e0f1011121314151617
//		   Plaintext : 00112233445566778899aabbccddeeff
//		   Ciphertext: 26449c1805dbe7aa25a468ce263a9e79
		
		/*String key = "000102030405060708090a0b0c0d0e0f1011121314151617";
		   String msg = "00112233445566778899aabbccddeeff";
		   String encrypted = "26449c1805dbe7aa25a468ce263a9e79"; 
		
		   ARIAEngine instance = new ARIAEngine(192);
		   
		   byte[] keyB = Hex.decodeHex(key.toCharArray());
		   byte[] msgB = Hex.decodeHex(msg.toCharArray());
		   
		   byte[] data = instance.encrypt(msgB, keyB);
		   
		   String dataHex = Hex.encodeHexString(data);
		   System.out.println(dataHex);
		   assertEquals(encrypted, dataHex);*/
		
		//256bit key
//		   Key       : 000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f
//		   Plaintext : 00112233445566778899aabbccddeeff
//		   Ciphertext: f92bd7c79fb72e2f2b8f80c1972d24fc
		
		/*String key = "000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f";
		   String msg = "00112233445566778899aabbccddeeff";
		   String encrypted = "f92bd7c79fb72e2f2b8f80c1972d24fc"; 
		
		   ARIAEngine instance = new ARIAEngine(256);
		   
		   byte[] keyB = Hex.decodeHex(key.toCharArray());
		   byte[] msgB = Hex.decodeHex(msg.toCharArray());
		   
		   byte[] data = instance.encrypt(msgB, keyB);
		   
		   String dataHex = Hex.encodeHexString(data);
		   System.out.println(dataHex);
		   assertEquals(encrypted, dataHex);*/
	}
	
	@Ignore
	@Test
	public void testARIA() throws Exception {
		String key = "KeySizeIs256bitsKeySizeIs256bits"; // 256bits (32bytes)
		String msg = "ableframe";
		
		byte[] msgB = msg.getBytes();
		ARIACryptoService ariaCryptoService = new ARIACryptoService();
		byte[] encrypted = ariaCryptoService.encrypt(msgB, key);
		byte[] decrypted = ariaCryptoService.decrypt(encrypted, key);
		System.out.println(new String(decrypted));
		
	}
	
	@Ignore
	@Test
	public void testGeneral() throws Exception {
		String key = "KeySizeIs256bitsKeySizeIs256bits"; // 256bits (32bytes)
		String msg = "ableframe";
		
		byte[] msgB = msg.getBytes();
		GeneralCryptoService generalCryptoService = new GeneralCryptoService();
		byte[] encrypted = generalCryptoService.encrypt(msgB, key);
		System.out.println(Base64.encodeBase64String(encrypted));
		byte[] decrypted = generalCryptoService.decrypt(encrypted, key);
		System.out.println(new String(decrypted));
		
	}
	
	@Ignore
	@Test
	public void testConversion() throws Exception {
		String msg = "ableframe";
		
		//String -> byte[] 변환
		byte[] msgB = msg.getBytes();
		//byte[] -> String(Base64)
		String base64 = Base64.encodeBase64String(msgB);
		//byte[] -> String(hex)
		String hex = Hex.encodeHexString(msgB);
		//String(Base64) -> byte[]
		byte[] decodeBase64 = Base64.decodeBase64(base64);
		//String(hex) -> byte[]
		byte[] decodeHex = Hex.decodeHex(hex.toCharArray());
		
		System.out.println(msg);
		System.out.println(msgB);
		System.out.println(base64);
		System.out.println(hex);
		System.out.println(decodeBase64);
		System.out.println(decodeHex);
		
		System.out.println(new String(msgB));
		
	}
	
	@Ignore
	@Test
	public void testAES256File() throws Exception {
		String key = "603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4"; //hex - 256bits (32bytes)
		String iv = "000102030405060708090A0B0C0D0E0F"; //Hex - 128bits (16bytes)
		
		byte[] keyB = Hex.decodeHex(key.toCharArray());
		byte[] ivB = Hex.decodeHex(iv.toCharArray());
		
		String plainPath = "plaintext";
		String encPath = "encrypt.aes256";
		String decPath = "decrypt.aes256";
		
		
		File plainFile = new File(plainPath);
		File encFile = new File(encPath);
		File decFile = new File(decPath);
		
		
		//arguments file test
		FileUtils.write(plainFile, "ableframe");
		
		AES256CryptoService aes256CryptoService = new AES256CryptoService();
		aes256CryptoService.encrypt(keyB, ivB, plainFile, encFile);
		aes256CryptoService.decrypt(keyB, ivB, encFile, decFile);

		
		assertTrue(plainFile.exists());
		assertTrue(encFile.exists());
		assertTrue(decFile.exists());
		
		assertTrue(FileUtils.contentEquals(plainFile, decFile));
		assertTrue(FileUtils.readFileToString(decFile).equals("ableframe"));
		
		assertTrue(plainFile.delete());
		assertTrue(encFile.delete());
		assertTrue(decFile.delete());
		
		//arguments path test
		FileUtils.write(plainFile, "ableframe2");
		
		aes256CryptoService.encrypt(keyB, ivB, plainPath, encPath);
		aes256CryptoService.decrypt(keyB, ivB, encPath, decPath);
		
		
		assertTrue(plainFile.exists());
		assertTrue(encFile.exists());
		assertTrue(decFile.exists());
		
		assertTrue(FileUtils.contentEquals(plainFile, decFile));
		assertTrue(FileUtils.readFileToString(decFile).equals("ableframe2"));
		
		assertTrue(plainFile.delete());
		assertTrue(encFile.delete());
		assertTrue(decFile.delete());
	}
	
	@Ignore
	@Test
	public void testTripleDESFile() throws Exception {
		String key = "6e7ae0e33251ec91a7fb62c4b97c1a646d70ab016ba846b6"; //Hex - 192bits (24bytes) 7+1,7+1,7+1
		
		String iv = "0001020304050607"; //Hex - 64bits (8bytes)
		
		byte[] keyB = Hex.decodeHex(key.toCharArray());
		byte[] ivB = Hex.decodeHex(iv.toCharArray());
		
		String plainPath = "plaintext";
		String encPath = "encrypt.tdes";
		String decPath = "decrypt.tdes";
		
		
		File plainFile = new File(plainPath);
		File encFile = new File(encPath);
		File decFile = new File(decPath);
		
		
		//arguments file test
		FileUtils.write(plainFile, "ableframe3");
		
		TripleDESCryptoService tripleDESCryptoService = new TripleDESCryptoService();
		tripleDESCryptoService.encrypt(keyB, ivB, plainFile, encFile);
		tripleDESCryptoService.decrypt(keyB, ivB, encFile, decFile);

		
		assertTrue(plainFile.exists());
		assertTrue(encFile.exists());
		assertTrue(decFile.exists());
		
		assertTrue(FileUtils.contentEquals(plainFile, decFile));
		assertTrue(FileUtils.readFileToString(decFile).equals("ableframe3"));
		
		assertTrue(plainFile.delete());
		assertTrue(encFile.delete());
		assertTrue(decFile.delete());
		
		//arguments path test
		FileUtils.write(plainFile, "ableframe4");
		
		tripleDESCryptoService.encrypt(keyB, ivB, plainPath, encPath);
		tripleDESCryptoService.decrypt(keyB, ivB, encPath, decPath);
		
		
		assertTrue(plainFile.exists());
		assertTrue(encFile.exists());
		assertTrue(decFile.exists());
		
		assertTrue(FileUtils.contentEquals(plainFile, decFile));
		assertTrue(FileUtils.readFileToString(decFile).equals("ableframe4"));
		
		assertTrue(plainFile.delete());
		assertTrue(encFile.delete());
		assertTrue(decFile.delete());
	}
}
