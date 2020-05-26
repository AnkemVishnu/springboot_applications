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
public interface TransactionTypeDao extends JpaRepository<TransactionType, Long> {

    @Query("select s from TransactionType s where s.transactionTypeName= :name")
    public TransactionType getTransactionTypeByName(@Param("name") String name);
    
}
