package com.bizmobia.vgwallet.mobile.controller;

import com.bizmobia.vgwallet.mobile.service.BillPaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Vishnu
 */
@RestController
@RequestMapping("/")
public class BillPaymentController {

    private static final Logger logger = LoggerFactory.getLogger(BillPaymentController.class);

    @Autowired
    private BillPaymentService service;

    @PostMapping(value = "/checkelectricitybill")
    public @ResponseBody
    String checkElectricityBill(@RequestHeader("auth_token") String autho_token, @RequestBody String requestModel) {
        return service.checkElectricityBill(autho_token, requestModel);
    }

    @PostMapping(value = "/electricitybillpayment")
    public @ResponseBody
    String electricityBillPayment(@RequestHeader("auth_token") String autho_token, @RequestBody String requestModel) {
        return service.electricityBillPayment(autho_token, requestModel);
    }

    @PostMapping(value = "/checkwaterbill")
    public @ResponseBody
    String checkWaterBill(@RequestHeader("auth_token") String autho_token, @RequestBody String requestModel) {
        return service.checkWaterBill(autho_token, requestModel);
    }

    @PostMapping(value = "/waterbillpayment")
    public @ResponseBody
    String waterBillPayment(@RequestHeader("auth_token") String autho_token, @RequestBody String requestModel) {
        return service.waterBillPayment(autho_token, requestModel);
    }

}
