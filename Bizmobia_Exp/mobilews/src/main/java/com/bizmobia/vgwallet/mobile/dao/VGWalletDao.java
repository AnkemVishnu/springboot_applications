package com.bizmobia.vgwallet.mobile.dao;

import com.bizmobia.vgwallet.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Vaibhav
 */
@Repository
public interface VGWalletDao extends JpaRepository<Wallet, Long> {

    @Query("select w from Wallet w where w.walletTypeObj.walletTypeName= 'VGWM' AND w.accountId LIKE 'VGWM%'")
    public Wallet getMainWallets();

    @Query("select w from Wallet w where w.walletTypeObj.walletTypeName= 'VGWBankVat' AND w.accountId LIKE 'VGWB%'")
    public Wallet getMainBankWallets();

    @Query("select w from Wallet w where w.walletTypeObj.walletTypeName= 'VGWINCOME' AND w.accountId LIKE 'VGWI%'")
    public Wallet getIncomeWallets();

    @Query("select w from Wallet w where w.walletTypeObj.walletTypeName= :#{#wallet.walletTypeObj.walletTypeName} AND w.accountId LIKE :#{#wallet.accountId}")
    public Wallet getWalletsByAccIDAndType(@Param("wallet") Wallet wallet);

    @Query("select w from Wallet w where w.walletTypeObj.walletTypeName= :#{#newWallet.walletTypeObj.walletTypeName} AND w.accountId LIKE :#{#newWallet.accountId}%")
    public Wallet getWalletsPartially(@Param("newWallet") Wallet newWallet);

}
