package com.bizmobia.vgwallet.vgwapp.service;

import com.bizmobia.vgwallet.vgwapp.request.RequestModel;
import com.bizmobia.vgwallet.vgwapp.response.ResponseModel;
import com.bizmobia.vgwallet.vgwapp.models.*;

public interface CartService {

    public ResponseModel addCart(String autho_token, RequestModel respObj);

    public ResponseModel updateCart(String autho_token, RequestModel respObj);

    public ResponseModel deleteCart(String autho_token, RequestModel respObj);

    public ResponseModel getAllCart(String autho_token, RequestModel respObj);

    public ResponseModel getCartById(String autho_token, RequestModel respObj);
}
