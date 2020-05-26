package com.bizmobia.vgwallet.mobile.service;

/**
 *
 * @author Vaibhav
 */
public interface WalletToWalletService {

    public String walletToWallet(String autho_token, String respObj);

    public String verifyPerson(String autho_token, String respObj);

    public String cashoutAtAgentPoint(String autho_token, String respObj);

    public String agentToAgentTransfer(String autho_token, String respObj);

}
