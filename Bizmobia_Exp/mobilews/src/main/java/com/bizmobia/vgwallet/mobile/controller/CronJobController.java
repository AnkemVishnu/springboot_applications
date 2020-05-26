package com.bizmobia.vgwallet.mobile.controller;

import com.bizmobia.vgwallet.mobile.service.CronJobService;
import com.bizmobia.vgwallet.response.ResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Vishnu
 */
@RestController
@RequestMapping("/")
public class CronJobController {

    private static final Logger logger = LoggerFactory.getLogger(CronJobController.class);

    @Autowired
    private CronJobService service;

    @GetMapping(value = "/fullkyccheck")
    public @ResponseBody
    ResponseModel fullKycCheck() {
        return service.fullKycCheck();
    }

    @GetMapping(value = "/requestmoneycheck")
    public @ResponseBody
    ResponseModel requestMoneyCheck() {
        return service.requestMoneyCheck();
    }

}
