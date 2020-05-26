package com.bizmobia.vgwallet.vgwapp.service;

import com.bizmobia.vgwallet.vgwapp.request.RequestModel;
import com.bizmobia.vgwallet.vgwapp.response.ResponseModel;

public interface CustomerService {

   public ResponseModel registration(RequestModel requestModel);

    public ResponseModel otpVerification(RequestModel requestModel);

    public ResponseModel resendOtp(RequestModel requestModel);

    public ResponseModel savePassword(RequestModel requestModel);

    public ResponseModel loginRequest(RequestModel requestModel);
    
    public ResponseModel addCustomer(RequestModel respObj);

    public ResponseModel updateCustomer(RequestModel respObj);

    public ResponseModel deleteCustomer(String autho_token, RequestModel respObj);

    public ResponseModel getAllCustomer(String autho_token, RequestModel respObj);

    public ResponseModel getCustomerById(String autho_token, RequestModel respObj);
}
