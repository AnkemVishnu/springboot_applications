package com.bizmobia.vgwallet.vgwapp.controller;

import com.bizmobia.vgwallet.vgwapp.service.*;
import com.bizmobia.vgwallet.vgwapp.request.*;
import com.bizmobia.vgwallet.vgwapp.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping(value = "insertpayment")
    public ResponseModel addPayment(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return paymentService.addPayment(autho_token, respObj);
    }

    @PostMapping(value = "updatepayment")
    public ResponseModel updatePayment(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return paymentService.updatePayment(autho_token, respObj);
    }

    @PostMapping(value = "deletepayment")
    public ResponseModel deletePayment(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return paymentService.deletePayment(autho_token, respObj);
    }

    @PostMapping(value = "getallpayment")
    public ResponseModel getAllPayment(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return paymentService.getAllPayment(autho_token, respObj);
    }

    @PostMapping(value = "getpaymentbyid")
    public ResponseModel getPaymentById(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return paymentService.getPaymentById(autho_token, respObj);
    }
}
