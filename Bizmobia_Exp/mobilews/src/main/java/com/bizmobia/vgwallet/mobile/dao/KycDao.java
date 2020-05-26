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
public interface KycDao extends JpaRepository<Kyc, Long> {

    @Query("SELECT obj FROM Kyc obj where obj.ishalfkyc= TRUE and obj.isfullkyc= FALSE")
    public List<Kyc> fullKycCheck();

    @Query("SELECT count(k) FROM Kyc k WHERE k.emailid = :#{#kyc.emailid} OR k.mobilenumber = :#{#kyc.mobilenumber}")
    public Long checkDuplicates(@Param("kyc") Kyc kyc);

    @Query("SELECT kyc FROM Kyc kyc WHERE kyc.mobilenumber = :#{#kyc.mobilenumber}")
    public Kyc getKycByMobile(@Param("kyc") Kyc kyc);
    
    @Query("SELECT kyc FROM Kyc kyc WHERE kyc.mobilenumber = :username OR kyc.emailid = :username")
    public Kyc getKycByMobileOrEmail(@Param("username") String username);

    @Query("SELECT kyc FROM Kyc kyc WHERE kyc.mobilenumber = :mobile")
    public Kyc getCustByMobile(@Param("mobile") String mobile);

}
