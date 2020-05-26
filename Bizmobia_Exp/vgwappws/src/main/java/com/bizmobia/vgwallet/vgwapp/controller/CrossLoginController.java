package com.bizmobia.vgwallet.vgwapp.controller;

import com.bizmobia.vgwallet.vgwapp.response.ResponseModel;
import com.bizmobia.vgwallet.vgwapp.service.CrossLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
public class CrossLoginController {

    private static final Logger logger = LoggerFactory.getLogger(CrossLoginController.class);

    @Autowired
    private CrossLoginService service;

    @PostMapping(value = "/crossloginrequest")
    public @ResponseBody
    String crossLoginRequest(@RequestHeader("auth_token") String autho_token, @RequestBody String requestModel) {
        return service.crossLoginRequest(autho_token, requestModel);
    }

    @GetMapping(value = "/crosslogincheck")
    public @ResponseBody
    ResponseModel crossLoginRequest() {
        return service.crossLoginCheck();
    }
}
