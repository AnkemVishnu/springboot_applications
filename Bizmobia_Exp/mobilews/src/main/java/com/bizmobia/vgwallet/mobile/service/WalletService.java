package com.bizmobia.vgwallet.mobile.service;

import com.bizmobia.vgwallet.models.Wallet;
import com.bizmobia.vgwallet.response.ResponseModel;

public interface WalletService {

    public ResponseModel addWallet(Wallet wallet);

    public ResponseModel updateWallet(String autho_token, Wallet walletsObj);
    
    public String contactSync(String autho_token, String requestModel);
    
    public String checkBalance(String autho_token, String requestModel);

    public String requestMoney(String autho_token, String requestModel);
    
    public String getAllRequestMoney(String autho_token, String requestModel);

    public String addMoneyWallet(String autho_token, String encdata);
    
}
