package com.bizmobia.vgwallet.mobile.service;

import com.bizmobia.vgwallet.models.Wallet;
import com.bizmobia.vgwallet.response.ResponseModel;

/**
 *
 * @author Vishnu
 */
public interface TransactionHistoryService {
    
    public String transactionsHistory(String autho_token, String requestModel);
    
    public String transactionsStatement(String autho_token, String requestModel);
    
    public ResponseModel transactionCheck(Double amount, Wallet wallet);
    
}
