package com.remco.ico.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bitcoinj.core.Coin;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.remco.ico.DTO.TokenDTO;
import com.remco.ico.DTO.UserRegisterDTO;
import com.remco.ico.model.BitcoinCashInfo;
import com.remco.ico.model.BitcoinInfo;
import com.remco.ico.model.TokenRateInfo;
import com.remco.ico.repository.BitcoinCashInfoRepository;
import com.remco.ico.repository.BitcoinInfoRepository;
import com.remco.ico.repository.TokenRateInfoRepository;

@Service
public class BitcoinCashUtils {

	static final Logger LOG = LoggerFactory.getLogger(BitcoinCashUtils.class);

	public static final int DEFAULT_SCALE = Coin.SMALLEST_UNIT_EXPONENT;

	public static final MathContext DEFAULT_CONTEXT = new MathContext(0, RoundingMode.UNNECESSARY);

	public static final BigDecimal satoshipercoinDecimal = new BigDecimal(Coin.COIN.value, DEFAULT_CONTEXT);

	@Autowired
	private Environment env;
	@Autowired
	private BitcoinCashInfoRepository bitcoinCashInfoRepository;
	@Autowired
	private BitcoinInfoRepository bitcoinInfoRepository;
	@Autowired
	private TokenRateInfoRepository tokenRateInfoRopository;
	@Autowired
	private CurrencyRateUtils currencyRateUtils;

	public boolean loginCrypto(UserRegisterDTO userRegisterDTO) throws Exception {

		String url = env.getProperty("bitgo.login.url");
		URL object = new URL(url);

		HttpURLConnection con = (HttpURLConnection) object.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestMethod("POST");

		JSONObject cred = new JSONObject();
		// JSONObject auth = new JSONObject();
		// JSONObject parent = new JSONObject();

		cred.put("email", env.getProperty("bitgo.login.email"));
		cred.put("password", env.getProperty("bitgo.login.password"));
		cred.put("otp", env.getProperty("bitgo.login.otp"));

		// auth.put("tenantName", "adm");
		// auth.put("passwordCredentials", cred.toString());

		// parent.put("auth", auth.toString());

		OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
		wr.write(cred.toString());
		wr.flush();

		// display what returns the POST request

		StringBuilder sb = new StringBuilder();
		int HttpResult = con.getResponseCode();
		
		System.out.println("HTTP URL connectresponse----->"+HttpResult);

		if (HttpResult == HttpURLConnection.HTTP_OK) {
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();
			System.out.println("::::::::::::" + sb.toString());
			JSONObject json = new JSONObject(sb.toString());
			System.out.println("myResponse" + json.get("access_token"));

			String accessToken = json.get("access_token").toString();
			//***************Encrypting the Token Type******************
			String tokenType = EncryptDecrypt.encrypt(json.get("token_type").toString());  
			
			BitcoinCashInfo bitcoinCashInfo1 = bitcoinCashInfoRepository.findByEmailId(userRegisterDTO.getEmailId());
			if (bitcoinCashInfo1 != null) {
				bitcoinCashInfo1.setAccessTokens(accessToken);
				bitcoinCashInfo1.setTokenType(tokenType);
				bitcoinCashInfoRepository.save(bitcoinCashInfo1);

			} else {
				BitcoinCashInfo bitcoinCashInfo = new BitcoinCashInfo();
				bitcoinCashInfo.setEmailId(userRegisterDTO.getEmailId());
				bitcoinCashInfo.setAccessTokens(accessToken);
				bitcoinCashInfo.setTokenType(tokenType);
				bitcoinCashInfoRepository.save(bitcoinCashInfo);
			}

			BitcoinInfo bitcoinInfo1 = bitcoinInfoRepository.findByEmailId(userRegisterDTO.getEmailId());
			if (bitcoinInfo1 != null) {
				bitcoinInfo1.setAccessTokens(accessToken);
				bitcoinInfo1.setTokenType(tokenType);
				bitcoinInfoRepository.save(bitcoinInfo1);
			} else {
				BitcoinInfo bitcoinInfo = new BitcoinInfo();
				bitcoinInfo.setEmailId(userRegisterDTO.getEmailId());
				bitcoinInfo.setAccessTokens(accessToken);
				bitcoinInfo.setTokenType(tokenType);
				bitcoinInfoRepository.save(bitcoinInfo);
			}
		} else {
			System.out.println(con.getResponseMessage());
			return false;
		}
		return true;

	}

	public boolean unlock(TokenDTO tokenDTO) throws Exception {

		BitcoinCashInfo bitcoinCashInfo = bitcoinCashInfoRepository.findByEmailId(tokenDTO.getEmailId());
		
		String tokenType = EncryptDecrypt.decrypt(bitcoinCashInfo.getTokenType());
		String accessToken = bitcoinCashInfo.getAccessTokens();
		
		String url = env.getProperty("bitgo.unlock.url");
		URL object = new URL(url);

		HttpURLConnection con = (HttpURLConnection) object.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Authorization",
				tokenType + " " + accessToken);
		con.setRequestMethod("POST");

		JSONObject cred = new JSONObject();
		// JSONObject auth = new JSONObject();
		// JSONObject parent = new JSONObject();

		// cred.put("email", env.getProperty("bitgo.login.email"));
		// cred.put("password", env.getProperty("bitgo.login.password"));
		cred.put("otp", env.getProperty("bitgo.login.otp"));

		// auth.put("tenantName", "adm");
		// auth.put("passwordCredentials", cred.toString());

		// parent.put("auth", auth.toString());

		OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
		wr.write(cred.toString());
		wr.flush();

		// display what returns the POST request

		StringBuilder sb = new StringBuilder();
		int HttpResult = con.getResponseCode();

		if (HttpResult == HttpURLConnection.HTTP_OK) {
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();
			System.out.println("::::::::::::" + sb.toString());
			JSONObject json = new JSONObject(sb.toString());
			System.out.println("myResponse" + json.get("session"));

			// BitcoinCashInfo bitcoinCashInfo1 =
			// bitcoinCashInfoRepository.findByEmailId(userRegisterDTO.getEmailId());
			// if(bitcoinCashInfo1 != null) {
			// bitcoinCashInfo1.setAccessTokens(json.get("access_token").toString());
			// bitcoinCashInfo1.setTokenType(json.get("token_type").toString());
			// bitcoinCashInfoRepository.save(bitcoinCashInfo1);
			//
			// } else {
			// BitcoinCashInfo bitcoinCashInfo = new BitcoinCashInfo();
			// bitcoinCashInfo.setEmailId(userRegisterDTO.getEmailId());
			// bitcoinCashInfo.setAccessTokens(json.get("access_token").toString());
			// bitcoinCashInfo.setTokenType(json.get("token_type").toString());
			// bitcoinCashInfoRepository.save(bitcoinCashInfo);
			// }
			//
			// BitcoinInfo bitcoinInfo1 =
			// bitcoinInfoRepository.findByEmailId(userRegisterDTO.getEmailId());
			// if(bitcoinInfo1 != null) {
			// bitcoinInfo1.setAccessTokens(json.get("access_token").toString());
			// bitcoinInfo1.setTokenType(json.get("token_type").toString());
			// bitcoinInfoRepository.save(bitcoinInfo1);
			// } else {
			// BitcoinInfo bitcoinInfo = new BitcoinInfo();
			// bitcoinInfo.setEmailId(userRegisterDTO.getEmailId());
			// bitcoinInfo.setAccessTokens(json.get("access_token").toString());
			// bitcoinInfo.setTokenType(json.get("token_type").toString());
			// bitcoinInfoRepository.save(bitcoinInfo);
			// }
		} else {
			System.out.println(con.getResponseMessage());
			return false;
		}
		return true;

	}

	public boolean generateBitcoinCashWallet(UserRegisterDTO userRegisterDTO) throws Exception {

		System.out.println("Email Id : " + userRegisterDTO.getEmailId());

		BitcoinCashInfo bitcoinCashInfo = bitcoinCashInfoRepository.findByEmailId(userRegisterDTO.getEmailId());
		
		String tokenType = EncryptDecrypt.decrypt(bitcoinCashInfo.getTokenType());
		String accessToken = bitcoinCashInfo.getAccessTokens();
		
		String url = env.getProperty("bitcoincash.wallet.generate.url");
		URL object = new URL(url);

		HttpURLConnection con = (HttpURLConnection) object.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Authorization", tokenType + " " + accessToken);
		con.setRequestMethod("POST");

		JSONObject cred = new JSONObject();
		// JSONObject auth = new JSONObject();
		// JSONObject parent = new JSONObject();

		System.out.println("Label" + userRegisterDTO.getEmailId());

		cred.put("label", userRegisterDTO.getEmailId());
		cred.put("passphrase", userRegisterDTO.getWalletPassword());
		bitcoinCashInfo.setLabel(userRegisterDTO.getEmailId());

		System.out.println(tokenType);
		System.out.println(accessToken);

		// auth.put("Authorization", bitcoinCashInfo.getTokenType() + " " +
		// bitcoinCashInfo.getAccessTokens());
		// auth.put("body", cred.toString());
		// parent.put("auth", auth.toString());

		OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
		wr.write(cred.toString());
		wr.flush();

		// System.out.println(parent.toString());

		// display what returns the POST request

		StringBuilder sb = new StringBuilder();
		int HttpResult = con.getResponseCode();

		if (HttpResult == HttpURLConnection.HTTP_OK) {
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();
			System.out.println("::::::::::" + sb.toString());
			JSONObject json = new JSONObject(sb.toString());
			System.out.println("myResponse" + json.get("id"));

			// BitcoinCashInfo bitcoinCashInfo = new BitcoinCashInfo();

			JSONObject json1 = new JSONObject(json.get("receiveAddress").toString());
			System.out.println("myResponse" + json1.get("address"));
			System.out.println("myResponse" + json1.get("wallet"));
			
			//Ecrypting the Wallet Details : 
			
			String walletId = EncryptDecrypt.encrypt(json1.get("wallet").toString());
			String bchAddress = EncryptDecrypt.encrypt(json1.get("address").toString());
			String bchAddressPassword = EncryptDecrypt.encrypt(userRegisterDTO.getWalletPassword());
			
			bitcoinCashInfo.setWalletId(walletId);
			bitcoinCashInfo.setBchWalletAddress(bchAddress);
			bitcoinCashInfo.setBchWalletPassword(bchAddressPassword);
			bitcoinCashInfoRepository.save(bitcoinCashInfo);

		} else {
			System.out.println(con.getResponseMessage());
			System.out.println("Not Getting Response");
			return false;
		}
		return true;

	}

	public String getWalletAddress(UserRegisterDTO userRegisterDTO) throws Exception {

		System.out.println("Email Id ::::::::::::::::" + userRegisterDTO.getEmailId());

		BitcoinCashInfo bitcoinCashInfo = bitcoinCashInfoRepository.findByEmailId(userRegisterDTO.getEmailId());
		String tokenType = EncryptDecrypt.decrypt(bitcoinCashInfo.getTokenType());
		String accessToken = bitcoinCashInfo.getAccessTokens();
		String walletId = EncryptDecrypt.decrypt(bitcoinCashInfo.getWalletId());
		
		String url = env.getProperty("bitcoincash.wallet.get.wallet.address") + walletId;
		URL object = new URL(url);

		HttpURLConnection con = (HttpURLConnection) object.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		// con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Authorization",
				tokenType + " " + accessToken);
		con.setRequestMethod("GET");

		// JSONObject cred = new JSONObject();
		// JSONObject auth = new JSONObject();
		// JSONObject parent = new JSONObject();

		// System.out.println( "Label" + userRegisterDTO.getEmailId());
		//
		// cred.put("label", userRegisterDTO.getEmailId());
		// cred.put("passphrase", userRegisterDTO.getWalletPassword());
		// bitcoinCashInfo.setLabel(userRegisterDTO.getEmailId());

		System.out.println(bitcoinCashInfo.getAccessTokens());
		System.out.println(bitcoinCashInfo.getTokenType());

		// auth.put("Authorization", bitcoinCashInfo.getTokenType() + " " +
		// bitcoinCashInfo.getAccessTokens());
		// auth.put("body", cred.toString());
		// parent.put("auth", auth.toString());

		// OutputStreamWriter wr = new
		// OutputStreamWriter(con.getOutputStream());
		// wr.write(cred.toString());
		// wr.flush();

		// BufferedReader in = new BufferedReader(new
		// InputStreamReader(con.getInputStream()));
		// String inputLine;

		// System.out.println(parent.toString());

		// display what returns the POST request

		StringBuilder sb = new StringBuilder();
		int HttpResult = con.getResponseCode();

		if (HttpResult == HttpURLConnection.HTTP_OK) {
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();
			System.out.println(":::::::::::::::::::::::::::::::" + sb.toString());
			JSONObject json = new JSONObject(sb.toString());
			System.out.println("myResponse" + json.get("id"));

			// BitcoinCashInfo bitcoinCashInfo = new BitcoinCashInfo();

			JSONObject json1 = new JSONObject(json.get("receiveAddress").toString());
			System.out.println("myResponse" + json1.get("address"));
			System.out.println("myResponse" + json1.get("wallet"));

			String walletIds = EncryptDecrypt.encrypt(json1.get("wallet").toString());
			String walletAddress = EncryptDecrypt.encrypt(json1.get("address").toString());
			
			bitcoinCashInfo.setWalletId(walletIds);
			bitcoinCashInfo.setBchWalletAddress(walletAddress);
			// bitcoinCashInfo.setBchWalletPassword(userRegisterDTO.getWalletPassword());
			bitcoinCashInfoRepository.save(bitcoinCashInfo);
			userRegisterDTO.setBitcoinCashWalletAddress(json1.get("address").toString());
			return json1.get("address").toString();
		} else
			System.out.println(con.getResponseMessage());
		System.out.println("Not Getting Response in getting wallet Address Method");
		return null;
	}

	public Double getBitcoinCashBalance(UserRegisterDTO userRegisterDTO) throws Exception {

		System.out.println("Email Id ::::::::::::::::" + userRegisterDTO.getEmailId());

		BitcoinCashInfo bitcoinCashInfo = bitcoinCashInfoRepository.findByEmailId(userRegisterDTO.getEmailId());
		
		String tokenType = EncryptDecrypt.decrypt(bitcoinCashInfo.getTokenType());
		String accessToken = bitcoinCashInfo.getAccessTokens();
		String walletId = EncryptDecrypt.decrypt(bitcoinCashInfo.getWalletId());
		
		// UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
		String url = env.getProperty("bitcoincash.wallet.get.wallet.address") + walletId;
		URL object = new URL(url);

		HttpURLConnection con = (HttpURLConnection) object.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		// con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Authorization",
				tokenType + " " + accessToken);
		con.setRequestMethod("GET");

		// JSONObject cred = new JSONObject();
		// JSONObject auth = new JSONObject();
		// JSONObject parent = new JSONObject();

		// System.out.println( "Label" + userRegisterDTO.getEmailId());
		//
		// cred.put("label", userRegisterDTO.getEmailId());
		// cred.put("passphrase", userRegisterDTO.getWalletPassword());
		// bitcoinCashInfo.setLabel(userRegisterDTO.getEmailId());

		System.out.println(bitcoinCashInfo.getAccessTokens());
		System.out.println(bitcoinCashInfo.getTokenType());

		// auth.put("Authorization", bitcoinCashInfo.getTokenType() + " " +
		// bitcoinCashInfo.getAccessTokens());
		// auth.put("body", cred.toString());
		// parent.put("auth", auth.toString());

		// OutputStreamWriter wr = new
		// OutputStreamWriter(con.getOutputStream());
		// wr.write(cred.toString());
		// wr.flush();

		// BufferedReader in = new BufferedReader(new
		// InputStreamReader(con.getInputStream()));
		// String inputLine;

		// System.out.println(parent.toString());

		// display what returns the POST request

		StringBuilder sb = new StringBuilder();
		int HttpResult = con.getResponseCode();

		if (HttpResult == HttpURLConnection.HTTP_OK) {
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();
			System.out.println(":::::::" + sb.toString());
			JSONObject json = new JSONObject(sb.toString());
			System.out.println("myResponse" + json.get("balance"));

			Integer balance = (Integer) json.get("balance");

			Double bchBalance = balance.doubleValue() / 100000000;
			userRegisterDTO.setBitcoinCashWalletBalance(bchBalance);
			// BitcoinCashInfo bitcoinCashInfo = new BitcoinCashInfo();

			// JSONObject json1 = new
			// JSONObject(json.get("receiveAddress").toString());
			// System.out.println("myResponse" + json1.get("address"));
			// System.out.println("myResponse" + json1.get("wallet"));
			//
			// bitcoinCashInfo.setWalletId(json1.get("wallet").toString());
			// bitcoinCashInfo.setBchWalletAddress(json1.get("address").toString());
			// bitcoinCashInfo.setBchWalletPassword(userRegisterDTO.getWalletPassword());
			// bitcoinCashInfoRepository.save(bitcoinCashInfo);
			// userRegisterDTO.setBitcoinCashWalletAddress(json1.get("address").toString());
			return bchBalance;
		} else
			System.out.println(con.getResponseMessage());
		System.out.println(":::::::::::::");
		return null;
	}

	@SuppressWarnings("unused")
	public String bchSendCoins(TokenDTO tokenDTO) throws Exception {

		System.out.println("Email Id : " + tokenDTO.getEmailId());

		boolean unlock = unlock(tokenDTO);

		BitcoinCashInfo bitcoinCashInfo = bitcoinCashInfoRepository.findByEmailId(tokenDTO.getEmailId());

		String walletId = EncryptDecrypt.decrypt(bitcoinCashInfo.getWalletId());
		String tokenType = EncryptDecrypt.decrypt(bitcoinCashInfo.getTokenType());
		String accessToken = bitcoinCashInfo.getAccessTokens();
		String walletPassphrase = EncryptDecrypt.decrypt(bitcoinCashInfo.getBchWalletPassword());
		
		String url = env.getProperty("bitcoincash.wallet.sendcoins.url") + walletId + env.getProperty("sendcoins.url");
		URL object = new URL(url);

		HttpURLConnection con = (HttpURLConnection) object.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Authorization",
				tokenType + " " + accessToken);
		con.setRequestMethod("POST");

		JSONObject cred = new JSONObject();
		// JSONObject auth = new JSONObject();
		// JSONObject parent = new JSONObject();

		
		TokenRateInfo tokenRateInfo = tokenRateInfoRopository.findBySaletype(tokenDTO.getSaleType());

		Double bchValue = currencyRateUtils.getCryptoRateFromDollar(tokenDTO);

		Double bchValueForOneToken = bchValue * tokenRateInfo.getTokenValue().doubleValue();
		System.out.println("current Bch Value for 1 usd : " + bchValueForOneToken);
			
		Double bchAmount = tokenDTO.getTokenAmount() * bchValueForOneToken;

		System.out.println("currentBchValue : " + bchAmount);

		Double bch = bchAmount * 100000000;

		Integer bitcoinCash = bch.intValue();

		System.out.println("Bch Value : " + bitcoinCash);
		cred.put("address", env.getProperty("bitcoincash.admin.wallet.address"));
		cred.put("amount", bitcoinCash);
		cred.put("walletPassphrase", walletPassphrase);
		// bitcoinCashInfo.setLabel(userRegisterDTO.getEmailId());

		System.out.println(bitcoinCashInfo.getAccessTokens());
		System.out.println(bitcoinCashInfo.getTokenType());

		// auth.put("Authorization", bitcoinCashInfo.getTokenType() + " " +
		// bitcoinCashInfo.getAccessTokens());
		// auth.put("body", cred.toString());
		// parent.put("auth", auth.toString());

		OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
		wr.write(cred.toString());
		wr.flush();

		// display what returns the POST request

		StringBuilder sb = new StringBuilder();
		int HttpResult = con.getResponseCode();

		if (HttpResult == HttpURLConnection.HTTP_OK) {
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();
			System.out.println("::::::::::" + sb.toString());
			JSONObject json = new JSONObject(sb.toString());
			System.out.println("myResponse" + json.get("txid"));

			// JSONObject json1 = new
			// JSONObject(json.get("receiveAddress").toString());
			System.out.println("myResponse" + json.get("tx"));
			System.out.println("myResponse" + json.get("status"));

			tokenDTO.setBchAmount(bchAmount);

			String status = (String) json.get("status");
			return status;
		} else {
			System.out.println(con.getResponseMessage());
			System.out.println("::::::::::::");
			return null;
		}
	}

}
