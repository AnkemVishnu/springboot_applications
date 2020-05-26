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
public interface DevicesDao extends JpaRepository<Devices, Long> {

    @Query("SELECT d FROM Devices d WHERE d.kyc.mobilenumber= :#{#kyc.mobilenumber}")
    public List<Devices> getAllDevicesByMobileNo(@Param("kyc") Kyc kyc);

    @Query("SELECT d FROM Devices d WHERE d.kyc.mobilenumber= :#{#kyc.mobilenumber}")
    public Devices getDeviceByMobileNo(@Param("kyc") Kyc kyc);
    
}
