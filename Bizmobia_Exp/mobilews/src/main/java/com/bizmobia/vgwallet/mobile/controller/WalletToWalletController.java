package com.bizmobia.vgwallet.mobile.controller;

import com.bizmobia.vgwallet.mobile.service.WalletToWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author Vaibhav
 */
@RestController
@RequestMapping("/")
public class WalletToWalletController {

    private static final Logger logger = LoggerFactory.getLogger(WalletToWalletController.class);

    @Autowired
    private WalletToWalletService service;

    @PostMapping(value = "/verifymobile")
    public @ResponseBody
    String verifyPerson(@RequestHeader("auth_token") String autho_token, @RequestBody String respObj) {
        return service.verifyPerson(autho_token, respObj);
    }

    @PostMapping(value = "/vgwtovgw")
    public @ResponseBody
    String debitMoney(@RequestHeader("auth_token") String autho_token, @RequestBody String respObj) {
        return service.walletToWallet(autho_token, respObj);
    }

    @PostMapping(value = "/customercashoutatagentpoint")
    public @ResponseBody
    String cashoutAtAgentPoint(@RequestHeader("auth_token") String autho_token, @RequestBody String respObj) {
        return service.cashoutAtAgentPoint(autho_token, respObj);
    }

    @PostMapping(value = "/vgwatovgwa")
    public @ResponseBody
    String agentToAgentTransfer(@RequestHeader("auth_token") String autho_token, @RequestBody String respObj) {
        return service.agentToAgentTransfer(autho_token, respObj);
    }

}
