package com.bizmobia.vgwallet.mobile.dao;

import com.bizmobia.vgwallet.models.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author bizmobia1
 */
@Repository
public interface RequestMoneyDao extends JpaRepository<RequestMoney, Long> {

    @Query("select rm from RequestMoney rm where rm.status.statusName= 'Requested' and rm.fromWallet.accountId= :#{#wallet.accountId} OR rm.toWallet.accountId= :#{#wallet.accountId}")
    public List<RequestMoney> getAllRequestedMoney(@Param("wallet") Wallet wallet);

    @Query("select rm from RequestMoney rm where rm.status.statusName= 'Requested'")
    public List<RequestMoney> getAllActiveRequestMoney();
    
    @Query("select rm from RequestMoney rm where rm.status.statusName= 'Expired'")
    public List<RequestMoney> getAllExpiredRequestMoney();

}
