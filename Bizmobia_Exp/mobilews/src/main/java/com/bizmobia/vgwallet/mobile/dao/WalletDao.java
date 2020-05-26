package com.bizmobia.vgwallet.mobile.dao;

import com.bizmobia.vgwallet.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author bizmobia1
 */
@Repository
public interface WalletDao extends JpaRepository<Wallet, Long> {

    @Query("select w from Wallet w where w.kycObj.mobilenumber= :#{#wallet.kycObj.mobilenumber} AND w.accountId= :#{#wallet.accountId}")
    public Wallet checkDuplicatesWallets(@Param("wallet") Wallet wallet);

    @Query("select w from Wallet w where w.kycObj.mobilenumber= :#{#kyc.mobilenumber} and w.walletTypeObj.walletTypeName= :#{#walletType.walletTypeName}")
    public Wallet getWalletByMobile(@Param("kyc") Kyc kyc, @Param("walletType") WalletType walletType);

    @Query("select w.walletBalance from Wallet w where w.kycObj.mobilenumber= :mobile")
    public String getBalanceByMobile(@Param("mobile") String mobile);

    @Query("select w.kycObj from Wallet w where w.kycObj.mobilenumber= :#{#kyc.mobilenumber}")
    public Kyc verify(@Param("kyc") Kyc kyc);

    @Query("SELECT obj FROM Wallet obj WHERE obj.vendorDetails.mobileNumber= :mobile")
    public Wallet getVendorWallet(@Param("mobile") String mobile);

    @Query("select w.kycObj from Wallet w where w.kycObj.mobilenumber= :#{#kyc.mobilenumber} and w.walletTypeObj.walletTypeName= :#{#walletType.walletTypeName}")
    public Wallet getAgentWalletByMobile(@Param("kyc") Kyc kyc, @Param("walletType") WalletType walletType);

}
