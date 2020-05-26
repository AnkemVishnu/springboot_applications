package com.bizmobia.vgwallet.vgwapp.service;

import com.bizmobia.vgwallet.vgwapp.request.RequestModel;
import com.bizmobia.vgwallet.vgwapp.response.ResponseModel;
import com.bizmobia.vgwallet.vgwapp.models.*;

public interface PaymentService {

    public ResponseModel addPayment(String autho_token, RequestModel respObj);

    public ResponseModel updatePayment(String autho_token, RequestModel respObj);

    public ResponseModel deletePayment(String autho_token, RequestModel respObj);

    public ResponseModel getAllPayment(String autho_token, RequestModel respObj);

    public ResponseModel getPaymentById(String autho_token, RequestModel respObj);
}
