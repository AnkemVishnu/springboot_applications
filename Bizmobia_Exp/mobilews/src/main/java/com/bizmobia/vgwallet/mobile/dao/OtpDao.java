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
public interface OtpDao extends JpaRepository<Otp, Long> {

    @Query("SELECT o FROM Otp o WHERE o.statusObj.statusName= :statusname AND o.otpTo= :mobile ORDER BY o.otpGeneratedTime DESC")
    public List<Otp> checkOtpsExpiry(@Param("statusname") String statusname, @Param("mobile") String mobilenumber);

    @Query("SELECT o FROM Otp o WHERE o.statusObj.statusName= :statusname AND o.otpTo= :mobile ORDER BY o.otpGeneratedTime DESC")
    public List<Otp> getOtpsByMobile(@Param("statusname") String statusname, @Param("mobile") String mobilenumber);

}
