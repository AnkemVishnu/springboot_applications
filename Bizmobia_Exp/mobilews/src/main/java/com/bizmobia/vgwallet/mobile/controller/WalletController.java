package com.bizmobia.vgwallet.mobile.controller;

import com.bizmobia.vgwallet.mobile.service.WalletService;
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
public class WalletController {

    private static final Logger logger = LoggerFactory.getLogger(WalletController.class);

    @Autowired
    private WalletService service;

    @PostMapping("/checkbalance")
    public @ResponseBody
    String checkBalance(@RequestHeader("auth_token") String autho_token, @RequestBody String requestModel) {
        return service.checkBalance(autho_token, requestModel);
    }

    @PostMapping(value = "/requestmoney")
    public @ResponseBody
    String requestMoney(@RequestHeader("auth_token") String autho_token, @RequestBody String requestModel) {
        return service.requestMoney(autho_token, requestModel);
    }

    @PostMapping(value = "/getallrequestmoney")
    public @ResponseBody
    String getAllRequestMoney(@RequestHeader("auth_token") String autho_token, @RequestBody String requestModel) {
        return service.getAllRequestMoney(autho_token, requestModel);
    }

    @PostMapping(value = "/contactsync")
    public @ResponseBody
    String contactSync(@RequestHeader("auth_token") String autho_token, @RequestBody String requestModel) {
        return service.contactSync(autho_token, requestModel);
    }

    //this is test Api
    @PostMapping(value = "/addmoney")
    public @ResponseBody
    String addMoneyWallet(@RequestHeader("auth_token") String autho_token, @RequestBody String encdata) {
        return service.addMoneyWallet(autho_token, encdata);
    }
}
