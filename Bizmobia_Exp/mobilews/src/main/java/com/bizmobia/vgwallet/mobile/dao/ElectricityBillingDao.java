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
public interface ElectricityBillingDao extends JpaRepository<ElectricityBilling, Long> {

    @Query("select eb from ElectricityBilling eb where eb.meterReading.kyc.mobilenumber= :#{#kyc.mobilenumber}")
    public ElectricityBilling getElectricityBilling(@Param("kyc") Kyc kyc);
    
}
