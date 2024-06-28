package able.cmm.crypto.web;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import able.com.util.crypto.AES256CryptoService;
import able.com.util.crypto.ARIACryptoService;
import able.com.util.crypto.SHA256PasswordEncoder;
import able.com.util.crypto.TripleDESCryptoService;
import able.com.vo.HMap;
import able.com.web.HController;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : CryptoController.java
 * @Description : 암호화 컨트롤러
 * @author "ADM Technology Team"
 * @since 2016. 7. 1
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2016. 7. 1.     "ADM Technology Team"     	최초 생성
 * </pre>
 */
@Controller
public class CryptoController extends HController {
	
	@Resource(name="ariaCryptoService")
	ARIACryptoService ariaCryptoService;
	
	@Resource(name="aes256CryptoService")
	AES256CryptoService aes256CryptoService;
	
	@Resource(name="tripleDESCryptoService")
	TripleDESCryptoService tripleDESCryptoService;
	
	
	@RequestMapping(path="/cmm/crypto/sampleSHA256.do", method = RequestMethod.GET)
	public String sampleSHA256(HMap hmap, ModelMap model) throws Exception {
		//기본 값 설정
		return "able/crypto/cryptoSampleSHA256";
	}
	
	@RequestMapping(path="/cmm/crypto/encryptSHA256.do", method = RequestMethod.POST)
	public String encryptSHA256(HMap hmap, ModelMap model) throws Exception {
		int iterations = Integer.parseInt(hmap.getString("iterations"));
		String msg = hmap.getString("msg");
		String salt = hmap.getString("salt");

		// 해쉬
		String encrypted= SHA256PasswordEncoder.encryptPassword(msg, salt, iterations);
		
		model.put("iterations",iterations);
		model.put("msg",msg);
		model.put("salt",salt);
		model.put("encrypted",encrypted);
		return "able/crypto/cryptoSampleSHA256";
	}
	
	@RequestMapping(path="/cmm/crypto/sampleARIA.do", method = RequestMethod.GET)
	public String sampleARIA(HMap hmap, ModelMap model) throws Exception {
		//기본 값 설정
		return "able/crypto/cryptoSampleARIA";
	}

	
	@RequestMapping(path="/cmm/crypto/encryptARIA.do", method = RequestMethod.POST)
	public String encryptARIA(HMap hmap, ModelMap model) throws Exception {
		String msg = hmap.getString("msg");
		String key = hmap.getString("key");
		
		// String -> byte[] 변환
		byte[] msgB = msg.getBytes();
		// 암호화
		byte[] encryptedB = ariaCryptoService.encrypt(msgB, key); 
		// byte[] -> Base64 String 변환
		String encrypted = Base64.encodeBase64String(encryptedB);
		
		model.put("keyARIA",key);
		model.put("msg",msg);
		model.put("encrypted",encrypted);
		return "able/crypto/cryptoSampleARIA";
	}
	
	
	@RequestMapping(path="/cmm/crypto/decryptARIA.do", method = RequestMethod.POST)
	public String decryptARIA(HMap hmap, ModelMap model) throws Exception {
		String encMsg = hmap.getString("encMsg");
		String key = hmap.getString("key");
		
		// Base64 String -> byte[] 변환
		byte[] encyptedB = Base64.decodeBase64(encMsg);
		// 복호화
		byte[] decryptedB = ariaCryptoService.decrypt(encyptedB, key); 
		// byte[] -> String 변환
		String decrypted = new String(decryptedB);

		model.put("keyARIA",key);
		model.put("encMsg",encMsg);
		model.put("decrypted",decrypted);
		return "able/crypto/cryptoSampleARIA";
	}
	
	@RequestMapping(path="/cmm/crypto/sampleAES256.do", method = RequestMethod.GET)
	public String sampleAES256(HMap hmap, ModelMap model) throws Exception {
		//기본 값 설정
		return "able/crypto/cryptoSampleAES256";
	}

	
	@RequestMapping(path="/cmm/crypto/encryptAES256.do", method = RequestMethod.POST)
	public String encryptAES256(HMap hmap, ModelMap model) throws Exception {
		String msg = hmap.getString("msg");
		String key = hmap.getString("key");
		String iv = hmap.getString("iv");
		
		// Hex String -> byte[] 변환
		byte[] keyB = Hex.decodeHex(key.toCharArray());
		byte[] ivB = Hex.decodeHex(iv.toCharArray());
		
		// 암호화 - Base64 String 결과 반환
		String encrypted = aes256CryptoService.encrypt(keyB, ivB, msg);
		
		model.put("keyAES",key);
		model.put("iv",iv);
		model.put("msg",msg);
		model.put("encrypted",encrypted);
		return "able/crypto/cryptoSampleAES256";
	}
	
	
	@RequestMapping(path="/cmm/crypto/decryptAES256.do", method = RequestMethod.POST)
	public String decryptAES256(HMap hmap, ModelMap model) throws Exception {
		String encMsg = hmap.getString("encMsg");
		String key = hmap.getString("key");
		String iv = hmap.getString("iv");
		
		// Base64 String -> byte[] 변환
		byte[] keyB = Hex.decodeHex(key.toCharArray());
		byte[] ivB = Hex.decodeHex(iv.toCharArray());
		
		// 복호화
		String decrypted = aes256CryptoService.decrypt(keyB, ivB, encMsg); 

		model.put("keyAES",key);
		model.put("iv",iv);
		model.put("encMsg",encMsg);
		model.put("decrypted",decrypted);
		return "able/crypto/cryptoSampleAES256";
	}
	
	@RequestMapping(path="/cmm/crypto/sampleTripleDES.do", method = RequestMethod.GET)
	public String sampleTripleDES(HMap hmap, ModelMap model) throws Exception {
		//기본 값 설정
		return "able/crypto/cryptoSampleTripleDES";
	}

	
	@RequestMapping(path="/cmm/crypto/encryptTripleDES.do", method = RequestMethod.POST)
	public String encryptTripleDES(HMap hmap, ModelMap model) throws Exception {
		String msg = hmap.getString("msg");
		String key = hmap.getString("key");
		String iv = hmap.getString("iv");
		
		// Hex String -> byte[] 변환
		byte[] keyB = Hex.decodeHex(key.toCharArray());
		byte[] ivB = Hex.decodeHex(iv.toCharArray());
		
		// 암호화 - Base64 String 결과 반환
		String encrypted = tripleDESCryptoService.encrypt(keyB, ivB, msg);
		
		model.put("keyTDES",key);
		model.put("iv",iv);
		model.put("msg",msg);
		model.put("encrypted",encrypted);
		return "able/crypto/cryptoSampleTripleDES";
	}
	
	
	@RequestMapping(path="/cmm/crypto/decryptTripleDES.do", method = RequestMethod.POST)
	public String decryptTripleDES(HMap hmap, ModelMap model) throws Exception {
		String encMsg = hmap.getString("encMsg");
		String key = hmap.getString("key");
		String iv = hmap.getString("iv");
		
		// Base64 String -> byte[] 변환
		byte[] keyB = Hex.decodeHex(key.toCharArray());
		byte[] ivB = Hex.decodeHex(iv.toCharArray());
		
		// 복호화
		String decrypted = tripleDESCryptoService.decrypt(keyB, ivB, encMsg); 

		model.put("keyTDES",key);
		model.put("iv",iv);
		model.put("encMsg",encMsg);
		model.put("decrypted",decrypted);
		return "able/crypto/cryptoSampleTripleDES";
	}
	

}
