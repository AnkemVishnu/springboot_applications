package com.bizmobia.vgwallet.mobile.controller;

import com.bizmobia.vgwallet.mobile.service.CustomerProfileService;
import com.bizmobia.vgwallet.mobile.service.EncryptionFile;
import com.bizmobia.vgwallet.request.RequestModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Vaibhav
 */
@RestController
@RequestMapping("/")
public class CustomerProfileController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerProfileController.class);

    @Autowired
    private EncryptionFile encryptionFile;

    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    @Autowired
    private CustomerProfileService service;

    @PostMapping("/registration")
    public @ResponseBody
    String registration(@RequestBody String requestModel) {
        return service.registration(requestModel);
    }

    @PostMapping("/otpverification")
    public @ResponseBody
    String otpVerification(@RequestBody String requestModel) {
        return service.otpVerification(requestModel);
    }

    @PostMapping("/emailverification")
    public @ResponseBody
    String emailVerification(@RequestHeader("auth_token") String autho_token, @RequestBody String requestModel) {
        return service.emailVerification(autho_token, requestModel);
    }

    @PostMapping("/resendotp")
    public @ResponseBody
    String resendOtp(@RequestBody String requestModel) {
        return service.resendOtp(requestModel);
    }

    @PostMapping("/savepassword")
    public @ResponseBody
    String savePassword(@RequestBody String requestModel) {
        return service.savePassword(requestModel);
    }

    @PostMapping("/webregistration")
    public @ResponseBody
    String webRegistration(@RequestBody String requestModel) {
        return service.webRegistration(requestModel);
    }

    @PostMapping("/login")
    public @ResponseBody
    String loginRequest(@RequestBody String requestModel) {
        return service.loginRequest(requestModel);
    }

    @PostMapping("/customerkyc")
    public @ResponseBody
    String customerFullKyc(@RequestHeader("auth_token") String autho_token, @RequestBody String requestModel) {
        return service.makeFullKyc(autho_token, requestModel);
    }

    @PostMapping("/deviceregistration")
    public @ResponseBody
    String deviceRegistration(@RequestHeader("auth_token") String autho_token, @RequestBody String requestModel) {
        return service.deviceRegistration(autho_token, requestModel);
    }

    @PostMapping("/forgetpassword")
    public @ResponseBody
    String forgetPassword(@RequestBody String requestModel) {
        return service.forgetPassword(requestModel);
    }

    //for this url is for Only For Forget password
    @PostMapping("/createnewpassword")
    public @ResponseBody
    String setNewPassword(@RequestHeader("auth_token") String autho_token, @RequestBody String requestModel) {
        return service.setNewPassword(requestModel);
    }

    @PostMapping("/changepassword")
    public @ResponseBody
    String changePassword(@RequestHeader("auth_token") String autho_token, @RequestBody String requestModel) {
        return service.changePassword(autho_token, requestModel);
    }

    @PostMapping("/encrypturl")
    public @ResponseBody
    String makeEncrypt(@RequestBody RequestModel requestModel) {
        String req = gson.toJson(requestModel);
        return encryptionFile.encrypt("Biz@123Bizm@bia1", "bizmobiabizmobia", req);
    }

    @PostMapping("/decrypturl")
    public @ResponseBody
    String makeDecrypt(@RequestBody String encrypted) {
        return encryptionFile.decryptObject("Biz@123Bizm@bia1", "bizmobiabizmobia", encrypted);
    }

    @PostMapping("/getkycbymobile")
    public @ResponseBody
    String getKycBymobile(@RequestBody String requestModel) {
        return service.getKycBymobile(requestModel);
    }
//    @PostMapping("/getkycbymobile")
//    public @ResponseBody
//    String getKycBymobile(@RequestHeader("auth_token") String autho_token, @RequestBody String requestModel) {
//        return service.getKycBymobile(autho_token, requestModel);
//    }

}
