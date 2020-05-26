package com.bizmobia.vgwallet.vgwapp.service;

import com.bizmobia.vgwallet.vgwapp.request.RequestModel;
import com.bizmobia.vgwallet.vgwapp.response.ResponseModel;
import com.bizmobia.vgwallet.vgwapp.models.*;

public interface CustomerOrderService {

    public ResponseModel addCustomerOrder(String autho_token, RequestModel respObj);

    public ResponseModel updateCustomerOrder(String autho_token, RequestModel respObj);

    public ResponseModel deleteCustomerOrder(String autho_token, RequestModel respObj);

    public ResponseModel getAllCustomerOrder(String autho_token, RequestModel respObj);

    public ResponseModel getCustomerOrderById(String autho_token, RequestModel respObj);
}
