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
public class CartItemsController {

    @Autowired
    private CartItemsService cartitemsService;

    @PostMapping(value = "insertcartitems")
    public ResponseModel addCartItems(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return cartitemsService.addCartItems(autho_token, respObj);
    }

    @PostMapping(value = "updatecartitems")
    public ResponseModel updateCartItems(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return cartitemsService.updateCartItems(autho_token, respObj);
    }

    @PostMapping(value = "deletecartitems")
    public ResponseModel deleteCartItems(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return cartitemsService.deleteCartItems(autho_token, respObj);
    }

    @PostMapping(value = "getallcartitems")
    public ResponseModel getAllCartItems(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return cartitemsService.getAllCartItems(autho_token, respObj);
    }

    @PostMapping(value = "getcartitemsbyid")
    public ResponseModel getCartItemsById(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return cartitemsService.getCartItemsById(autho_token, respObj);
    }
}
