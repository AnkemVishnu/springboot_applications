package com.bizmobia.vgwallet.vgwapp.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Vishnu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String name;
    private String mobileNumber;
    private String emailId;
    private String role;
    private Boolean isEmailVerified;
    private Boolean isMobileVerified;
    private Boolean isOtpVerified;
    private String status;

}
