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
public interface WalletTypeDao extends JpaRepository<WalletType, Long> {

    @Query("select s from WalletType s where s.walletTypeName= :name")
    public WalletType getWalletTypeByName(@Param("name") String name);
    
}
