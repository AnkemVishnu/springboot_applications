package com.bizmobia.vgwallet.vgwapp.service;

import com.bizmobia.vgwallet.vgwapp.models.Customer;
import com.bizmobia.vgwallet.vgwapp.response.ResponseModel;

/**
 *
 * @author Vaibhav
 */
public interface MailAndMessageSender {

    public ResponseModel sendSmsForPassword(Customer cust, String str);

    public ResponseModel sendSmsForOtp(Customer custObj, String str);

    public ResponseModel sendSmsAndMailForWallet(Customer custObj, String str);

    public ResponseModel sendMail(String email, String name, String otp);

}
