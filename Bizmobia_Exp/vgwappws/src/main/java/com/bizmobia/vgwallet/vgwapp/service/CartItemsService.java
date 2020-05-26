package com.bizmobia.vgwallet.vgwapp.service;

import com.bizmobia.vgwallet.vgwapp.response.ResponseModel;
import com.bizmobia.vgwallet.vgwapp.request.RequestModel;

public interface CartItemsService {

    public ResponseModel addCartItems(String autho_token, RequestModel respObj);

    public ResponseModel updateCartItems(String autho_token, RequestModel respObj);

    public ResponseModel deleteCartItems(String autho_token, RequestModel respObj);

    public ResponseModel getAllCartItems(String autho_token, RequestModel respObj);

    public ResponseModel getCartItemsById(String autho_token, RequestModel respObj);
}
