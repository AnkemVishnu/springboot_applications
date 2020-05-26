package com.bizmobia.vgwallet.mobile.service;

/**
 *
 * @author Vaibhav
 */
public interface CustomerProfileService {

    public String registration(String requestModel);

    public String otpVerification(String requestModel);

    public String resendOtp(String requestModel);

    public String savePassword(String requestModel);

    public String loginRequest(String requestModel);

    public String makeFullKyc(String autho_token, String requestModel);

    public String deviceRegistration(String autho_token, String requestModel);

    public String forgetPassword(String requestModel);

    public String setNewPassword(String requestModel);

    public String emailVerification(String autho_token, String requestModel);
    
    public String changePassword(String autho_token, String requestModel);

    public String getKycBymobile(String requestModel);

    public String webRegistration(String requestModel);

}
