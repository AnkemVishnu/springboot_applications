package com.bizmobia.vgwallet.mobile.serviceImpl;

import com.bizmobia.vgwallet.mobile.utilities.MailSendingMsg;
import com.bizmobia.vgwallet.mobile.utilities.SmsSender;
import com.bizmobia.vgwallet.models.Kyc;
import com.bizmobia.vgwallet.response.ResponseModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bizmobia.vgwallet.mobile.service.MailAndMessageSender;
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
    public ResponseModel sendSmsForPassword(Kyc kyc, String str) {
        ResponseModel successResponse = new ResponseModel();
        try {
            String content = "Dear " + kyc.getFullname() + "\n\n\n"
                    + "Your Password For VGWallet is : " + str + "\n\n"
                    + dontshare + "\n" + vgwteam;
            MailSendingMsg.sendMail(kyc.getEmailid(), content);

            String description = "Dear " + kyc.getFullname() + "\n\n"
                    + "Your Password For VGWallet is : " + str + "\n"
                    + dontshare + vgwteam;
            SmsSender.sendSms(kyc.getMobilenumber(), description);

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
    public ResponseModel sendSmsForOtp(Kyc kyc, String str) {
        ResponseModel successResponse = new ResponseModel();
        try {
            String content = "Dear " + kyc.getFullname() + "\n\n\n"
                    + "Your Otp is : " + str + "\n\n"
                    + dontshare + "\n" + vgwteam;
            MailSendingMsg.sendMail(kyc.getEmailid(), content);

            String description = "Dear " + kyc.getFullname() + "\n\n"
                    + "Your Otp is : " + str + "\n"
                    + dontshare + vgwteam;
            SmsSender.sendSms(kyc.getMobilenumber(), description);

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
    public ResponseModel sendSmsAndMailForWallet(Kyc kyc, String str) {
        ResponseModel successResponse = new ResponseModel();
        try {
            String content = "Dear " + kyc.getFullname() + "\n\n\n"
                    + "Your VGWallet is created" + "\n\n"
                    + "Your Account ID is : " + str + "\n\n"
                    + vgwteam;
            MailSendingMsg.sendMail(kyc.getEmailid(), content);

            String description = "Dear " + kyc.getFullname() + "\n\n"
                    + "Your VGWallet is created" + "\n"
                    + "Your Account ID is : " + str + "\n"
                    + vgwteam;
            SmsSender.sendSms(kyc.getMobilenumber(), description);

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
