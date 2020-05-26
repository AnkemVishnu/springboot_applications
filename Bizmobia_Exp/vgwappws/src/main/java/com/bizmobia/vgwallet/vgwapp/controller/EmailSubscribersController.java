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
public class EmailSubscribersController {

    @Autowired
    private EmailSubscribersService emailsubscribersService;

    @PostMapping(value = "insertemailsubscribers")
    public ResponseModel addEmailSubscribers(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return emailsubscribersService.addEmailSubscribers(autho_token, respObj);
    }

    @PostMapping(value = "updateemailsubscribers")
    public ResponseModel updateEmailSubscribers(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return emailsubscribersService.updateEmailSubscribers(autho_token, respObj);
    }

    @PostMapping(value = "deleteemailsubscribers")
    public ResponseModel deleteEmailSubscribers(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return emailsubscribersService.deleteEmailSubscribers(autho_token, respObj);
    }

    @PostMapping(value = "getallemailsubscribers")
    public ResponseModel getAllEmailSubscribers(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return emailsubscribersService.getAllEmailSubscribers(autho_token, respObj);
    }

    @PostMapping(value = "getemailsubscribersbyid")
    public ResponseModel getEmailSubscribersById(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return emailsubscribersService.getEmailSubscribersById(autho_token, respObj);
    }
}
