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
public class CustomerOrderController {

    @Autowired
    private CustomerOrderService customerorderService;

    @PostMapping(value = "insertcustomerorder")
    public ResponseModel addCustomerOrder(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return customerorderService.addCustomerOrder(autho_token, respObj);
    }

    @PostMapping(value = "updatecustomerorder")
    public ResponseModel updateCustomerOrder(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return customerorderService.updateCustomerOrder(autho_token, respObj);
    }

    @PostMapping(value = "deletecustomerorder")
    public ResponseModel deleteCustomerOrder(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return customerorderService.deleteCustomerOrder(autho_token, respObj);
    }

    @PostMapping(value = "getallcustomerorder")
    public ResponseModel getAllCustomerOrder(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return customerorderService.getAllCustomerOrder(autho_token, respObj);
    }

    @PostMapping(value = "getcustomerorderbyid")
    public ResponseModel getCustomerOrderById(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return customerorderService.getCustomerOrderById(autho_token, respObj);
    }
}
