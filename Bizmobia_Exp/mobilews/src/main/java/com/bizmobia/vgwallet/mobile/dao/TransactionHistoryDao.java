package com.bizmobia.vgwallet.mobile.dao;

import com.bizmobia.vgwallet.models.*;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author bizmobia1
 */
@Repository
public interface TransactionHistoryDao extends JpaRepository<TransactionHistory, Long> {

//      FIRST TIME
    @Query("SELECT th FROM TransactionHistory th WHERE (th.fromWallet.kycObj.mobilenumber= :mobile OR th.toWallet.kycObj.mobilenumber= :mobile) AND th.transactionHistoryId > 0 ORDER BY th.transactionDate DESC")
    public List<TransactionHistory> getTransactionHistoryByMobile(@Param("mobile") String mobile, Pageable page);
//      NORMAL
    @Query("SELECT th FROM TransactionHistory th WHERE (th.fromWallet.kycObj.mobilenumber= :mobile OR th.toWallet.kycObj.mobilenumber= :mobile) AND th.transactionHistoryId < :lastId ORDER BY th.transactionDate DESC")
    public List<TransactionHistory> getTransactionHistoryByMobile(@Param("mobile") String mobile, @Param("lastId") Long lastId, Pageable page);

    @Query("SELECT th FROM TransactionHistory th WHERE th.fromWallet.kycObj.mobilenumber= :mobile OR th.toWallet.kycObj.mobilenumber= :mobile ORDER BY th.transactionDate DESC")
    public List<TransactionHistory> getTransactionStatmentByMobile(@Param("mobile") String mobile);

    @Query("SELECT th FROM TransactionHistory th WHERE th.fromWallet.kycObj.mobilenumber= :#{#wallet.kycObj.mobilenumber} and th.transactionDate= current_date()")
    public List<TransactionHistory> todayTransactions(@Param("wallet") Wallet wallet);

}
