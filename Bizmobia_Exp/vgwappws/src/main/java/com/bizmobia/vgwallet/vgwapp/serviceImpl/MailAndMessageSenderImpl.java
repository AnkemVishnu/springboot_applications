package com.bizmobia.vgwallet.vgwapp.serviceImpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bizmobia.vgwallet.vgwapp.models.Customer;
import com.bizmobia.vgwallet.vgwapp.response.ResponseModel;
import com.bizmobia.vgwallet.vgwapp.service.MailAndMessageSender;
import com.bizmobia.vgwallet.vgwapp.utilities.MailSendingMsg;
import com.bizmobia.vgwallet.vgwapp.utilities.SmsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Vaibhav
 */
@Service
@Transactional
public class MailAndMessageSenderImpl implements MailAndMessageSender {

    private static final Logger logger = LoggerFactory.getLogger(MailAndMessageSenderImpl.class);

    public static String vgwteam = "Thank You \n" + "VGWallet Team \n";

    public static String dontshare = "Please Do Not Share With Anyone\n";

    @Override
    public ResponseModel sendSmsForPassword(Customer cust, String str) {
        ResponseModel successResponse = new ResponseModel();
        try {
            String content = "Dear " + cust.getFullname()+ "\n\n\n"
                    + "Your Password For VGWallet is : " + str + "\n\n"
                    + dontshare + "\n" + vgwteam;
            MailSendingMsg.sendMail(cust.getEmail(), content);

            String description = "Dear " + cust.getFullname() + "\n\n"
                    + "Your Password For VGWallet is : " + str + "\n"
                    + dontshare + vgwteam;
            SmsSender.sendSms(cust.getPhoneNumber(), description);

            successResponse.setStatusCode(0);
            successResponse.setMessage("Password Sent Successfully");
            logger.info("MailAndMessageSenderImpl.class", "sendSmsForPassword()", "Success");
        } catch (Exception e) {
            e.printStackTrace();
            successResponse.setStatusCode(1);
            successResponse.setMessage("Failed to send Password");
            logger.error("MailAndMessageSenderImpl.class", "sendSmsForPassword()", "Plese try after Sometime " + e.getMessage());
        }
        return successResponse;
    }

    @Override
    public ResponseModel sendSmsForOtp(Customer cust, String str) {
        ResponseModel successResponse = new ResponseModel();
        try {
            String content = "Dear " + cust.getFullname() + "\n\n\n"
                    + "Your Otp is : " + str + "\n\n"
                    + dontshare + "\n" + vgwteam;
            MailSendingMsg.sendMail(cust.getEmail(), content);

            String description = "Dear " + cust.getFullname() + "\n\n"
                    + "Your Otp is : " + str + "\n"
                    + dontshare + vgwteam;
            SmsSender.sendSms(cust.getPhoneNumber(), description);

            successResponse.setStatusCode(0);
            successResponse.setMessage("Otp Sent Successfully");
            logger.info("MailAndMessageSenderImpl.class", "sendSmsForOtp()", "Success");
        } catch (Exception e) {
            e.printStackTrace();
            successResponse.setStatusCode(1);
            successResponse.setMessage("Failed to send Otp");
            logger.error("MailAndMessageSenderImpl.class", "sendSmsForOtp()", "Plese try after Sometime " + e.getMessage());
        }
        return successResponse;
    }

    //here sending wallet details to user newly register
    @Override
    public ResponseModel sendSmsAndMailForWallet(Customer cust, String str) {
        ResponseModel successResponse = new ResponseModel();
        try {
            String content = "Dear " + cust.getFullname() + "\n\n\n"
                    + "Your VGWallet is created" + "\n\n"
                    + "Your Account ID is : " + str + "\n\n"
                    + vgwteam;
            MailSendingMsg.sendMail(cust.getEmail(), content);

            String description = "Dear " + cust.getFullname() + "\n\n"
                    + "Your VGWallet is created" + "\n"
                    + "Your Account ID is : " + str + "\n"
                    + vgwteam;
            SmsSender.sendSms(cust.getPhoneNumber(), description);

            successResponse.setStatusCode(0);
            successResponse.setMessage("VGWallet Created Successfully");
            logger.info("MailAndMessageSenderImpl.class", "sendSmsAndMailForWallet()", "Success");
        } catch (Exception e) {
            e.printStackTrace();
            successResponse.setStatusCode(1);
            successResponse.setMessage("Failed to Create VGWallet");
            logger.error("MailAndMessageSenderImpl.class", "sendSmsAndMailForWallet()", "Plese try after Sometime " + e.getMessage());
        }
        return successResponse;
    }

    @Override
    public ResponseModel sendMail(String email, String name, String otp) {
        ResponseModel successResponse = new ResponseModel();
        try {
            String content = "Dear " + name + "\n\n\n"
                    + "\n\n"
                    + "Your Email Verification OTP is : " + otp + "\n\n"
                    + vgwteam;
            MailSendingMsg.sendMail(email, content);

            successResponse.setStatusCode(0);
            successResponse.setMessage("Mail Send Successfully");
            logger.info("MailAndMessageSenderImpl.class", "sendSmsAndMailForWallet()", "Success");
        } catch (Exception e) {
            e.printStackTrace();
            successResponse.setStatusCode(1);
            successResponse.setMessage("Fail to Send Mail");
            logger.error("MailAndMessageSenderImpl.class", "sendSmsAndMailForWallet()", "Plese try after Sometime " + e.getMessage());
        }
        return successResponse;
    }

}
