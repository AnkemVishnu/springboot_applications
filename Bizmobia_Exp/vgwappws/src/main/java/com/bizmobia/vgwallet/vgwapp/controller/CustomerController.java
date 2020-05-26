package com.bizmobia.vgwallet.vgwapp.controller;

import com.bizmobia.vgwallet.vgwapp.service.*;
import com.bizmobia.vgwallet.vgwapp.request.*;
import com.bizmobia.vgwallet.vgwapp.response.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
@RequestMapping("/")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private EncryptionFile encryptionFile;

    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    @PostMapping(value = "registration")
    public @ResponseBody
    ResponseModel registration(@RequestBody RequestModel requestModel) {
        return customerService.registration(requestModel);
    }

    @PostMapping(value = "otpverification")
    public @ResponseBody
    ResponseModel otpVerification(@RequestBody RequestModel requestModel) {
        return customerService.otpVerification(requestModel);
    }

    @PostMapping(value = "resendotp")
    public @ResponseBody
    ResponseModel resendOtp(@RequestBody RequestModel requestModel) {
        return customerService.resendOtp(requestModel);
    }

    @PostMapping(value = "savepassword")
    public @ResponseBody
    ResponseModel savePassword(@RequestBody RequestModel requestModel) {
        return customerService.savePassword(requestModel);
    }

    @PostMapping(value = "login")
    public @ResponseBody
    ResponseModel loginRequest(@RequestBody RequestModel requestModel) {
        return customerService.loginRequest(requestModel);
    }

    @PostMapping(value = "insertcustomer")
    public ResponseModel addCustomer(@RequestBody RequestModel respObj) {
        return customerService.addCustomer(respObj);
    }

    @PostMapping(value = "updatecustomer")
    public ResponseModel updateCustomer(@RequestBody RequestModel respObj) {
        return customerService.updateCustomer(respObj);
    }

    @PostMapping(value = "deletecustomer")
    public ResponseModel deleteCustomer(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return customerService.deleteCustomer(autho_token, respObj);
    }

    @PostMapping(value = "getallcustomer")
    public ResponseModel getAllCustomer(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return customerService.getAllCustomer(autho_token, respObj);
    }

    @PostMapping(value = "getcustomerbyid")
    public ResponseModel getCustomerById(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return customerService.getCustomerById(autho_token, respObj);
    }
}
