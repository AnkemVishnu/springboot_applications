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
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping(value = "insertadmin")
    public ResponseModel addAdmin(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return adminService.addAdmin(autho_token, respObj);
    }

    @PostMapping(value = "updateadmin")
    public ResponseModel updateAdmin(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return adminService.updateAdmin(autho_token, respObj);
    }

    @PostMapping(value = "deleteadmin")
    public ResponseModel deleteAdmin(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return adminService.deleteAdmin(autho_token, respObj);
    }

    @PostMapping(value = "getalladmin")
    public ResponseModel getAllAdmin(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return adminService.getAllAdmin(autho_token, respObj);
    }

    @PostMapping(value = "getadminbyid")
    public ResponseModel getAdminById(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return adminService.getAdminById(autho_token, respObj);
    }
}
