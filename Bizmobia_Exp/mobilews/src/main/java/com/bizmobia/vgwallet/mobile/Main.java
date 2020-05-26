package com.bizmobia.vgwallet.mobile;

import com.bizmobia.vgwallet.mobile.utilities.Consumption;
import com.bizmobia.vgwallet.response.Message;
import com.bizmobia.vgwallet.response.OtpInput;

/**
 *
 * @author BizMobia23
 */
public class Main {

    public static void main(String[] args) {
        OtpInput otpInput = new OtpInput();
        otpInput.setFrom("VirtualGoal");
        otpInput.setText("Hello, We have done integration of otp in ewallet mobile app, BizMobia");
        otpInput.setTo("9527245706");
//        otpInput.setTo("+243"+"992006667");
        Message verifyOtp = Consumption.verifyOtp(otpInput);
        System.out.println("hello");
    }
}
