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
public class CompanyConfigController {

    @Autowired
    private CompanyConfigService companyconfigService;

    @PostMapping(value = "insertcompanyconfig")
    public ResponseModel addCompanyConfig(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return companyconfigService.addCompanyConfig(autho_token, respObj);
    }

    @PostMapping(value = "updatecompanyconfig")
    public ResponseModel updateCompanyConfig(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return companyconfigService.updateCompanyConfig(autho_token, respObj);
    }

    @PostMapping(value = "deletecompanyconfig")
    public ResponseModel deleteCompanyConfig(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return companyconfigService.deleteCompanyConfig(autho_token, respObj);
    }

    @PostMapping(value = "getallcompanyconfig")
    public ResponseModel getAllCompanyConfig(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return companyconfigService.getAllCompanyConfig(autho_token, respObj);
    }

    @PostMapping(value = "getcompanyconfigbyid")
    public ResponseModel getCompanyConfigById(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return companyconfigService.getCompanyConfigById(autho_token, respObj);
    }
}
