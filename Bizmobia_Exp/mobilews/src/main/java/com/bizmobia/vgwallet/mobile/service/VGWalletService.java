package com.bizmobia.vgwallet.mobile.service;

import com.bizmobia.vgwallet.models.Wallet;
import com.bizmobia.vgwallet.response.ResponseModel;

/**
 *
 * @author Vaibhav
 */
public interface VGWalletService {

    public ResponseModel updateVGWallet(Wallet walletObj);

    public Double applicableCharges(Double amt);

}
