package com.bizmobia.vgwallet.mobile.service;

import com.bizmobia.vgwallet.models.*;

/**
 *
 * @author bizmobia1
 */
public interface WalletTypeService {

    public WalletType getWalletTypeByName(String name);
    
    public WalletType walletTypeByRole(Roles role);

}
