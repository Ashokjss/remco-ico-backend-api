package com.remco.ico.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.remco.ico.DTO.TokenDTO;
import com.remco.ico.DTO.UserRegisterDTO;
import com.remco.ico.model.GasFeeInfo;
import com.remco.ico.model.TokenDiscountInfo;
import com.remco.ico.model.TokenRateInfo;
import com.remco.ico.repository.GasFeeInfoRepository;
import com.remco.ico.repository.TokenDiscountInfoRepository;
import com.remco.ico.repository.TokenRateInfoRepository;

@Service
public class CurrencyRateUtils {

	private static final Logger LOG = LoggerFactory.getLogger(CurrencyRateUtils.class);

	@Autowired
	private Environment env;
	@Autowired
	private TokenRateInfoRepository tokenRateInfoRopository;
	@Autowired
	private TokenDiscountInfoRepository tokenDiscountInfoRepository;
	@Autowired
	private GasFeeInfoRepository gasFeeInfoRepository;

	private static BigDecimal truncateDecimal(double x,int numberofDecimals)
	{
	    if ( x > 0) {
	        return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_FLOOR);
	    } else {
	        return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_CEILING);
	    }
	}
	
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	public Double getCryptoRateFromDollar(TokenDTO tokenDTO) throws JSONException, IOException {

		// URL Converts the 1 USD values to ETH, BCH, BTC

		Double value = 0.0;
		if (tokenDTO.getTypeOfPurchase().equalsIgnoreCase(env.getProperty("eth.payment"))) {

			JSONObject json = readJsonFromUrl(env.getProperty("crypto.compare.url.eth"));
			LOG.info("Inside ETH : " + json.get("data"));
			JSONObject data = new JSONObject(json.get("data").toString());
			JSONObject quotes = new JSONObject(data.get("quotes").toString());

			JSONObject USD = new JSONObject(quotes.get("USD").toString());
			JSONObject ETH = new JSONObject(quotes.get("ETH").toString());
			LOG.info("Price from BTC to USD and ETH : " + ETH.get("price") + "::::::::::::" + USD.get("price"));

			Double eth = (Double) ETH.get("price");
			Double usd = (Double) USD.get("price");
			Double oneUSDValueToEther = eth / usd;

			value = oneUSDValueToEther;
		} else {

			JSONObject json = readJsonFromUrl(env.getProperty("crypto.compare.url"));
			LOG.info("Inside BCH and BTC : " + json.get("data"));
			JSONObject data = new JSONObject(json.get("data").toString());
			JSONObject quotes = new JSONObject(data.get("quotes").toString());

			JSONObject USD = new JSONObject(quotes.get("USD").toString());
			JSONObject BCH = new JSONObject(quotes.get("BCH").toString());
			LOG.info("Price from BTC to USD and BCH : " + BCH.get("price") + "::::::::::::" + USD.get("price"));

			if (tokenDTO.getTypeOfPurchase().equalsIgnoreCase(env.getProperty("btc.payment"))) {
				LOG.info("Inside BTC");
				Double btc = 1.0;
				Double usd = (Double) USD.get("price");
				Double oneUSDValueToBTC = btc / usd;

				value = oneUSDValueToBTC;

			} else if (tokenDTO.getTypeOfPurchase().equalsIgnoreCase(env.getProperty("bch.payment"))) {
				LOG.info("Inside BCH");
				Double bch = (Double) BCH.get("price");
				Double usd = (Double) USD.get("price");
				Double oneUSDValueToBCH = bch / usd;

				value = oneUSDValueToBCH;
			}

		}


		return value;
	}

	

	public Double getCurrencyRateForNGN() throws JSONException, IOException {

		// URL Converts the 1 USD values to NGN

		// JSONObject json =
		// readJsonFromUrl(env.getProperty("ngn.convert.url"));
		TokenDiscountInfo tokenRateInfo = tokenDiscountInfoRepository.findOne(1);
		LOG.info("USD_NGN : " + tokenRateInfo.getNgnValue());

		// String oneNgn = json.get("USD_NGN").toString();
		// JSONObject json1 = new JSONObject(oneNgn);
		// LOG.info("NGN : " + json1.get("val"));
		Double ngn = tokenRateInfo.getNgnValue().doubleValue();
		return ngn;
	}

	public boolean getCurrencyRateForTokensInPurchase(UserRegisterDTO userRegisterDTO)
			throws JSONException, IOException {

		TokenRateInfo tokenRateInfo = tokenRateInfoRopository.findBySaletype(userRegisterDTO.getSaleType());

		TokenDTO tokenDTO = new TokenDTO();
		LOG.info(userRegisterDTO.getTypeOfPurchase());
		if (userRegisterDTO.getTypeOfPurchase() != null && userRegisterDTO.getNoOfTokens() != null) {
			LOG.info("Inside Rate Utils");
			if (!userRegisterDTO.getTypeOfPurchase().equalsIgnoreCase("VCASH")) {
				
				LOG.info("Inside crypto Rate Utils");
				tokenDTO.setTypeOfPurchase(userRegisterDTO.getTypeOfPurchase());
				tokenDTO.setTokenAmount(userRegisterDTO.getNoOfTokens());
				Double value = getCryptoRateFromDollar(tokenDTO);
				if (value != null) {
					Double valueForOneToken = value * tokenRateInfo.getTokenValue().doubleValue();
					Double tokenRate = valueForOneToken * userRegisterDTO.getNoOfTokens();
					
					GasFeeInfo gasFeeInfo = gasFeeInfoRepository.findGasFeeInfoById(1);
					Double withFee = 0.0;
					if(userRegisterDTO.getTypeOfPurchase().equalsIgnoreCase(env.getProperty("eth.payment"))) {
						withFee = gasFeeInfo.getEther();
					} else if(userRegisterDTO.getTypeOfPurchase().equalsIgnoreCase(env.getProperty("bch.payment"))) {
						withFee = gasFeeInfo.getBitcoinCash();
					} else if(userRegisterDTO.getTypeOfPurchase().equalsIgnoreCase(env.getProperty("btc.payment"))) {
						withFee = gasFeeInfo.getBitcoin();
					}
					userRegisterDTO.setAmount(tokenRate + withFee);
				}
				return true;
			} else {
				LOG.info("Inside NGN Rate Utils");
				Double value = getCurrencyRateForNGN();
				if (value != null) {
					Double valueForOneToken = value * tokenRateInfo.getTokenValue().doubleValue();
					Double tokenRate = valueForOneToken * userRegisterDTO.getNoOfTokens();

					userRegisterDTO.setAmount(tokenRate);
					System.out.println("Tokens : " + tokenRate);
				}
			}
			return true;
		}
		return false;
	}

	public boolean getCurrencyRateForTokensInPurchaseFromCrypto(UserRegisterDTO userRegisterDTO)
			throws JSONException, IOException {

		TokenRateInfo tokenRateInfo = tokenRateInfoRopository.findBySaletype(userRegisterDTO.getSaleType());
		
		TokenDiscountInfo tokenDiscountInfo = tokenDiscountInfoRepository.findOne(1);
		TokenDTO tokenDTO = new TokenDTO();
		LOG.info(userRegisterDTO.getTypeOfPurchase());
		GasFeeInfo gasFeeInfo = gasFeeInfoRepository.findGasFeeInfoById(1);

		if (userRegisterDTO.getTypeOfPurchase() != null && userRegisterDTO.getAmount() != null) {
			LOG.info("Inside Rate Utils");

			tokenDTO.setTypeOfPurchase(userRegisterDTO.getTypeOfPurchase());
			tokenDTO.setTokenAmount(userRegisterDTO.getAmount());

			if (userRegisterDTO.getTypeOfPurchase().equalsIgnoreCase(env.getProperty("eth.payment"))) {

				Double withFee = gasFeeInfo.getEther();
				Double ether = userRegisterDTO.getAmount() - withFee;
				Double etherCurrentRateForOneUSD = getCryptoRateFromDollar(tokenDTO);
				
				Double convertingETHToUSD = ether / etherCurrentRateForOneUSD;
				System.out.println("Convertion Value  : " + convertingETHToUSD);
				Double noOfTokens = convertingETHToUSD / tokenRateInfo.getTokenValue().doubleValue();
				
				LOG.info("noOfTokens : " + noOfTokens);
				BigDecimal bd = truncateDecimal(noOfTokens, 8);
				userRegisterDTO.setNoOfTokens(bd.doubleValue());
				return true;

			} else if (userRegisterDTO.getTypeOfPurchase().equalsIgnoreCase(env.getProperty("bch.payment"))) {
				
				Double withFee = gasFeeInfo.getBitcoinCash();
				Double bch = userRegisterDTO.getAmount() - withFee;
				Double bchCurrentRateForOneUSD = getCryptoRateFromDollar(tokenDTO);
				
				Double convertingBCHToUSD = bch / bchCurrentRateForOneUSD;
				System.out.println("Convertion Value  : " + convertingBCHToUSD);
				Double noOfTokens = convertingBCHToUSD / tokenRateInfo.getTokenValue().doubleValue();

				LOG.info("noOfTokens : " + noOfTokens);
				BigDecimal bd = truncateDecimal(noOfTokens, 8);
				userRegisterDTO.setNoOfTokens(bd.doubleValue());
				return true;

			} else if (userRegisterDTO.getTypeOfPurchase().equalsIgnoreCase(env.getProperty("btc.payment"))) {

				Double withFee = gasFeeInfo.getBitcoin();
				Double btc = userRegisterDTO.getAmount() - withFee;
				Double btcCurrentRateForOneUSD = getCryptoRateFromDollar(tokenDTO);
				
				Double convertingBTCToUSD = btc / btcCurrentRateForOneUSD;
				System.out.println("Convertion Value  : " + btcCurrentRateForOneUSD);
				Double noOfTokens = convertingBTCToUSD / tokenRateInfo.getTokenValue().doubleValue();
				
				LOG.info("noOfTokens : " + noOfTokens);
				BigDecimal bd = truncateDecimal(noOfTokens, 8);
				userRegisterDTO.setNoOfTokens(bd.doubleValue());
				return true;

			} else if (userRegisterDTO.getTypeOfPurchase().equalsIgnoreCase(env.getProperty("vtn.payment"))) {

				Double ngn = userRegisterDTO.getAmount();
				Double ngnCurrentRateForOneUSD = tokenDiscountInfo.getNgnValue().doubleValue();
				
				Double convertingNGNToUSD = ngn / ngnCurrentRateForOneUSD;
				System.out.println("Convertion Value  : " + ngnCurrentRateForOneUSD);				
				Double noOfTokens = convertingNGNToUSD / tokenRateInfo.getTokenValue().doubleValue();
				
				LOG.info("noOfTokens : " + noOfTokens.doubleValue());
				BigDecimal bd = truncateDecimal(noOfTokens, 8);
				userRegisterDTO.setNoOfTokens(bd.doubleValue());
				return true;

			}
		}
		return false;
	}
	
	public boolean validateParams(UserRegisterDTO userRegisterDTO) {
		
		if(userRegisterDTO.getTypeOfPurchase() != null && StringUtils.isNotBlank(userRegisterDTO.getTypeOfPurchase())
				&& userRegisterDTO.getSaleType() != null && StringUtils.isNotBlank(userRegisterDTO.getSaleType())) {
			return true;
		}
		return false;
	}
	
}
