package com.bizmobia.vgwallet.mobile.controller;

import com.bizmobia.vgwallet.mobile.service.TransactionHistoryService;
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
public class TransactionHistoryController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionHistoryController.class);

    @Autowired
    private TransactionHistoryService service;

    @PostMapping("/transactionshistory")
    public @ResponseBody
    String transactionsHistory(@RequestHeader("auth_token") String autho_token, @RequestBody String requestModel) {
        return service.transactionsHistory(autho_token, requestModel);
    }

    @PostMapping("/transactionsstatement")
    public @ResponseBody
    String transactionsStatement(@RequestHeader("auth_token") String autho_token, @RequestBody String requestModel) {
        return service.transactionsStatement(autho_token, requestModel);
    }

}
