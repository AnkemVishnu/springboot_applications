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
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping(value = "insertcart")
    public ResponseModel addCart(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return cartService.addCart(autho_token, respObj);
    }

    @PostMapping(value = "updatecart")
    public ResponseModel updateCart(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return cartService.updateCart(autho_token, respObj);
    }

    @PostMapping(value = "deletecart")
    public ResponseModel deleteCart(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return cartService.deleteCart(autho_token, respObj);
    }

    @PostMapping(value = "getallcart")
    public ResponseModel getAllCart(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return cartService.getAllCart(autho_token, respObj);
    }

    @PostMapping(value = "getcartbyid")
    public ResponseModel getCartById(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return cartService.getCartById(autho_token, respObj);
    }
}
