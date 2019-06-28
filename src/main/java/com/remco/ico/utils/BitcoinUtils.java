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
import com.remco.ico.model.BitcoinInfo;
import com.remco.ico.model.TokenRateInfo;
import com.remco.ico.repository.BitcoinInfoRepository;
import com.remco.ico.repository.TokenRateInfoRepository;

@Service
public class BitcoinUtils {

	static final Logger LOG = LoggerFactory.getLogger(BitcoinUtils.class);

	public static final int DEFAULT_SCALE = Coin.SMALLEST_UNIT_EXPONENT;

	public static final MathContext DEFAULT_CONTEXT = new MathContext(0, RoundingMode.UNNECESSARY);

	public static final BigDecimal satoshipercoinDecimal = new BigDecimal(Coin.COIN.value, DEFAULT_CONTEXT);
	
	@Autowired
	private Environment env;
	@Autowired
	private BitcoinInfoRepository bitcoinInfoRepository;
	@Autowired
	private BitcoinCashUtils bitcoinCashUtils;
	@Autowired
	private CurrencyRateUtils currencyRateUtils;
	@Autowired
	private TokenRateInfoRepository tokenRateInfoRopository;

	public boolean createBitcoinWallet(UserRegisterDTO userRegisterDTO) throws Exception {
		
		BitcoinInfo bitcoinInfo = bitcoinInfoRepository.findByEmailId(userRegisterDTO.getEmailId());
		
		String tokenType = EncryptDecrypt.decrypt(bitcoinInfo.getTokenType());
		String accessToken = bitcoinInfo.getAccessTokens();
		
		String url = env.getProperty("bitcoin.wallet.generate.url");
		URL object = new URL(url);

		HttpURLConnection con = (HttpURLConnection) object.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Authorization", tokenType + " " + accessToken);
		con.setRequestMethod("POST");

		JSONObject cred = new JSONObject();
//		JSONObject auth = new JSONObject();
//		JSONObject parent = new JSONObject();
		
		
		
		System.out.println( "Label" + userRegisterDTO.getEmailId());
		
		cred.put("label", userRegisterDTO.getEmailId());
		cred.put("passphrase", userRegisterDTO.getWalletPassword());
		bitcoinInfo.setLabel(userRegisterDTO.getEmailId());
		
		System.out.println(accessToken);
		System.out.println(tokenType);
		
//		auth.put("Authorization", bitcoinCashInfo.getTokenType() + " " + bitcoinCashInfo.getAccessTokens());
//		auth.put("body", cred.toString());
//		parent.put("auth", auth.toString());

		OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
		wr.write(cred.toString());
		wr.flush();
		
//		System.out.println(parent.toString());
		
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

//			BitcoinCashInfo bitcoinCashInfo = new BitcoinCashInfo();	
			
			JSONObject json1 = new JSONObject(json.get("receiveAddress").toString());
			System.out.println("myResponse" + json1.get("address"));
			System.out.println("myResponse" + json1.get("wallet"));
			
			String walletId = EncryptDecrypt.encrypt(json1.get("wallet").toString());
			String btcAddress = EncryptDecrypt.encrypt(json1.get("address").toString());
			String btcPassword = EncryptDecrypt.encrypt(userRegisterDTO.getWalletPassword());
			
			bitcoinInfo.setWalletId(walletId);
			bitcoinInfo.setBtcWalletAddress(btcAddress);
			bitcoinInfo.setBtcWalletPassword(btcPassword);
			bitcoinInfoRepository.save(bitcoinInfo);

		} else {
			System.out.println(con.getResponseMessage());
			System.out.println("Getting failure response in create btc wallet");
			return false;
		}
		return true;
	}
	
	public String getWalletAddress(UserRegisterDTO userRegisterDTO) throws Exception {

		System.out.println("Email Id ::::::::::::::::" + userRegisterDTO.getEmailId());
		
		BitcoinInfo bitcoinInfo = bitcoinInfoRepository.findByEmailId(userRegisterDTO.getEmailId());
		String tokenType = EncryptDecrypt.decrypt(bitcoinInfo.getTokenType());
		String accessToken = bitcoinInfo.getAccessTokens();
		String walletId = EncryptDecrypt.decrypt(bitcoinInfo.getWalletId());
		
		String url = env.getProperty("bitcoin.get.wallet.address") + walletId;
		URL object = new URL(url);

		HttpURLConnection con = (HttpURLConnection) object.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
//		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Authorization", tokenType + " " + accessToken);
		con.setRequestMethod("GET");

//		JSONObject cred = new JSONObject();
//		JSONObject auth = new JSONObject();
//		JSONObject parent = new JSONObject();
		
		
		
//		System.out.println( "Label" + userRegisterDTO.getEmailId());
//		
//		cred.put("label", userRegisterDTO.getEmailId());
//		cred.put("passphrase", userRegisterDTO.getWalletPassword());
//		bitcoinCashInfo.setLabel(userRegisterDTO.getEmailId());
		
		System.out.println(accessToken);
		System.out.println(tokenType);
		
//		auth.put("Authorization", bitcoinCashInfo.getTokenType() + " " + bitcoinCashInfo.getAccessTokens());
//		auth.put("body", cred.toString());
//		parent.put("auth", auth.toString());

//		OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
//		wr.write(cred.toString());
//		wr.flush();
		
//		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//		String inputLine;
		
//		System.out.println(parent.toString());
		
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
			System.out.println("::" + sb.toString());
			JSONObject json = new JSONObject(sb.toString());
			System.out.println("myResponse" + json.get("id"));

//			BitcoinCashInfo bitcoinCashInfo = new BitcoinCashInfo();	
			
			JSONObject json1 = new JSONObject(json.get("receiveAddress").toString());
			System.out.println("myResponse" + json1.get("address"));
			System.out.println("myResponse" + json1.get("wallet"));
			
			String walletIds = EncryptDecrypt.encrypt(json1.get("wallet").toString());
			String btcAddress = EncryptDecrypt.encrypt(json1.get("address").toString());
			
			bitcoinInfo.setWalletId(walletIds);
			bitcoinInfo.setBtcWalletAddress(btcAddress);
//			bitcoinCashInfo.setBchWalletPassword(userRegisterDTO.getWalletPassword());
			bitcoinInfoRepository.save(bitcoinInfo);
			userRegisterDTO.setBitcoinWalletAddress(json1.get("address").toString());
			return json1.get("address").toString();
		} else 
			System.out.println(con.getResponseMessage());
			System.out.println("Not Getting response in get bitcoin address");
			return null;
	}
	
	public Double getBitcoinBalance(UserRegisterDTO userRegisterDTO) throws Exception {
		
		System.out.println("Email Id : " + userRegisterDTO.getEmailId());
		
		BitcoinInfo bitcoinInfo = bitcoinInfoRepository.findByEmailId(userRegisterDTO.getEmailId());
		
		String tokenType = EncryptDecrypt.decrypt(bitcoinInfo.getTokenType());
		String accessToken = bitcoinInfo.getAccessTokens();
		String walletId = EncryptDecrypt.decrypt(bitcoinInfo.getWalletId());
		
		String url = env.getProperty("bitcoin.get.wallet.address") + walletId;
		URL object = new URL(url);

		HttpURLConnection con = (HttpURLConnection) object.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
//		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Authorization", tokenType + " " + accessToken);
		con.setRequestMethod("GET");

//		JSONObject cred = new JSONObject();
//		JSONObject auth = new JSONObject();
//		JSONObject parent = new JSONObject();
	
//		System.out.println( "Label" + userRegisterDTO.getEmailId());
//		
//		cred.put("label", userRegisterDTO.getEmailId());
//		cred.put("passphrase", userRegisterDTO.getWalletPassword());
//		bitcoinCashInfo.setLabel(userRegisterDTO.getEmailId());
		
		System.out.println(accessToken);
		System.out.println(tokenType);
		
//		auth.put("Authorization", bitcoinCashInfo.getTokenType() + " " + bitcoinCashInfo.getAccessTokens());
//		auth.put("body", cred.toString());
//		parent.put("auth", auth.toString());

//		OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
//		wr.write(cred.toString());
//		wr.flush();
		
//		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//		String inputLine;
		
//		System.out.println(parent.toString());
		
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
			System.out.println("myResponse" + json.get("balance"));
			
			Integer balance = (Integer)json.get("balance");
			
			Double btcBalance = balance.doubleValue() / 100000000;
			userRegisterDTO.setBitcoinWalletBalance(btcBalance);
//			BitcoinCashInfo bitcoinCashInfo = new BitcoinCashInfo();	
			
//			JSONObject json1 = new JSONObject(json.get("receiveAddress").toString());
//			System.out.println("myResponse" + json1.get("address"));
//			System.out.println("myResponse" + json1.get("wallet"));
//			
//			bitcoinCashInfo.setWalletId(json1.get("wallet").toString());
//			bitcoinCashInfo.setBchWalletAddress(json1.get("address").toString());
//			bitcoinCashInfo.setBchWalletPassword(userRegisterDTO.getWalletPassword());
//			bitcoinCashInfoRepository.save(bitcoinCashInfo);
//			userRegisterDTO.setBitcoinCashWalletAddress(json1.get("address").toString());
			return btcBalance;
		} else 
			System.out.println(con.getResponseMessage());
			System.out.println("Not getting response in getting btc balance");
			return null;
	}
	
	@SuppressWarnings("unused")
	public String btcSendCoins(TokenDTO tokenDTO) throws Exception {

		System.out.println("Email Id : " + tokenDTO.getEmailId());

		boolean unlock = bitcoinCashUtils.unlock(tokenDTO);
		
		BitcoinInfo bitcoinInfo = bitcoinInfoRepository.findByEmailId(tokenDTO.getEmailId());
		
		String tokenType = EncryptDecrypt.decrypt(bitcoinInfo.getTokenType());
		String accessToken = bitcoinInfo.getAccessTokens();
		String walletId = EncryptDecrypt.decrypt(bitcoinInfo.getWalletId());
		String walletPassphrase = EncryptDecrypt.decrypt(bitcoinInfo.getBtcWalletPassword());
		
		String url = env.getProperty("bitcoin.wallet.sendcoins.url") + walletId + env.getProperty("sendcoins.url");
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
//		Double ethValue = tokenDTO.getTokenAmount() * tokenRateInfo.getUsdValue().doubleValue();
		
		Double btcValue = currencyRateUtils.getCryptoRateFromDollar(tokenDTO);
		
		Double btcValueForOneToken = btcValue * tokenRateInfo.getTokenValue().doubleValue();
		System.out.println("current Bch Value for 1 usd : " + btcValueForOneToken);

		Double btcAmount = tokenDTO.getTokenAmount() * btcValueForOneToken;
		
		System.out.println("currentBchValue : " + btcAmount);
		
		Double btc = btcAmount * 100000000;
		
		Integer bitcoin = btc.intValue();
		System.out.println("Btc Value : " + bitcoin);
		cred.put("address", env.getProperty("bitcoin.admin.wallet.address"));
		cred.put("amount", bitcoin);
		cred.put("walletPassphrase", walletPassphrase);
//		bitcoinCashInfo.setLabel(userRegisterDTO.getEmailId());

		System.out.println(bitcoinInfo.getAccessTokens());
		System.out.println(bitcoinInfo.getTokenType());

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

			// BitcoinCashInfo bitcoinCashInfo = new BitcoinCashInfo();

//			JSONObject json1 = new JSONObject(json.get("receiveAddress").toString());
			System.out.println("myResponse" + json.get("tx"));
			System.out.println("myResponse" + json.get("status"));
			
			tokenDTO.setBtcAmount(btcAmount);
			
			String status = (String) json.get("status");
			return status;
		} else {
			System.out.println(con.getResponseMessage());
			System.out.println("::::::::::::");
			return null;
		}
	}
	
}
