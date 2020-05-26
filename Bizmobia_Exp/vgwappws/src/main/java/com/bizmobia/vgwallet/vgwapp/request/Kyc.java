package com.bizmobia.vgwallet.vgwapp.request;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Kyc implements Serializable {

    private Long idkyc;
    private String fullname;
    private String fathername;
    private String gender;
    private String maritalstatus;
    private Date dateofbirth;
    private String identificationnumber;
    private String idproofnumber;
    private String idproofurl;
    private String adressproofurl;
    private String presentaddress;
    private String permanentaddress;
    private String countryName;
    private String postalCode;
    private String locality;
    private String mobilenumber;
    private String password;
    private String emailid;
    private Double grossincome;
    private String occupation;
    private Boolean ishalfkyc;
    private Boolean isotpverify;
    private Boolean isfullkyc;
    private Boolean ismobileverified;
    private Boolean isemailverified;
    private Integer wrongattempt;

}
