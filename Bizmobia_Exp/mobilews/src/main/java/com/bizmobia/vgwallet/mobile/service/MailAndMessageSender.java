package com.bizmobia.vgwallet.mobile.service;

import com.bizmobia.vgwallet.models.Kyc;
import com.bizmobia.vgwallet.response.ResponseModel;

/**
 *
 * @author Vaibhav
 */
public interface MailAndMessageSender {

    public ResponseModel sendSmsForPassword(Kyc kyc, String str);

    public ResponseModel sendSmsForOtp(Kyc kycObj, String str);

    public ResponseModel sendSmsAndMailForWallet(Kyc kycObj, String str);

    public ResponseModel sendMail(String email, String name, String otp);

}
