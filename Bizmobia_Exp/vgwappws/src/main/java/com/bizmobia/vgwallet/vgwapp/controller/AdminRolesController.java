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
public class AdminRolesController {

    @Autowired
    private AdminRolesService adminrolesService;

    @PostMapping(value = "insertadminroles")
    public ResponseModel addAdminRoles(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return adminrolesService.addAdminRoles(autho_token, respObj);
    }

    @PostMapping(value = "updateadminroles")
    public ResponseModel updateAdminRoles(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return adminrolesService.updateAdminRoles(autho_token, respObj);
    }

    @PostMapping(value = "deleteadminroles")
    public ResponseModel deleteAdminRoles(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return adminrolesService.deleteAdminRoles(autho_token, respObj);
    }

    @PostMapping(value = "getalladminroles")
    public ResponseModel getAllAdminRoles(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return adminrolesService.getAllAdminRoles(autho_token, respObj);
    }

    @PostMapping(value = "getadminrolesbyid")
    public ResponseModel getAdminRolesById(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return adminrolesService.getAdminRolesById(autho_token, respObj);
    }
}
