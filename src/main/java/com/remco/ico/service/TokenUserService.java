package com.remco.ico.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

import com.remco.ico.DTO.SaleTypeDTO;
import com.remco.ico.DTO.TokenDTO;
import com.remco.ico.DTO.UserRegisterDTO;

@Service
public interface TokenUserService {
	
	public boolean checkWalletAddressEquals(TokenDTO tokenDTO) throws Exception;
	public boolean TransferCoin(TokenDTO tokenDTO) throws Exception;
	public boolean checkMainbalance(TokenDTO tokenDTO) throws Exception;
//	public TokenDTO getTokenRate();
//	public boolean updateTokenRate(TokenDTO tokenDTO);
//	public TokenDTO getDiscountPercent();
//	public boolean updateDiscount(TokenDTO tokenDTO);
	public List<TokenDTO> transactionHistory(TokenDTO tokenDTO, String etherWalletAddress) throws Exception;
	public Double balanceTokens(TokenDTO tokenDTO) throws Exception;
	public BigDecimal etherBalance(TokenDTO tokenDTO) throws Exception;
	public String burnTokens(TokenDTO tokenDTO);
//	public TokenDTO getBonusPercent();
//	public boolean updateBonus(TokenDTO tokenDTO);
//	public TokenDTO getVestingDiscount();
//	public boolean updateVestingDisc(TokenDTO tokenDTO);
	public boolean updateSaleDates(TokenDTO tokenDTO);
	public TokenDTO getSaleDates();
	public List<SaleTypeDTO> getTokenValues(TokenDTO tokenDTO);
	public boolean updateOffers(TokenDTO tokenDTO);
	public boolean saveTokenDetails(TokenDTO tokenDTO) throws Exception;
	public boolean isTokenTransApproved(TokenDTO tokenDTO) throws Exception;
	public boolean adminTokenBalance(TokenDTO tokenDTO) throws Exception;
	public String validateVestingTokens(TokenDTO tokenDTO) throws Exception;
	public boolean ValidateTokenBalanceForBurn(TokenDTO tokenDTO) throws Exception;
	public boolean tokenPurchaseCancellationInVTN(TokenDTO tokenDTO);
	public boolean adminSoldBalance(TokenDTO tokenDTO) throws Exception;
	public boolean multipleTokenTransfer(TokenDTO tokenDTO);
	public boolean promoTokenTransfer(UserRegisterDTO userRegisterDTO);
	
}
