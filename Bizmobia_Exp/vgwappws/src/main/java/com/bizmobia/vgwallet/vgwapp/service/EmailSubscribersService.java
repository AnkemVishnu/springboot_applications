package com.bizmobia.vgwallet.vgwapp.service;

import com.bizmobia.vgwallet.vgwapp.request.RequestModel;
import com.bizmobia.vgwallet.vgwapp.response.ResponseModel;
import com.bizmobia.vgwallet.vgwapp.models.*;

public interface EmailSubscribersService {

    public ResponseModel addEmailSubscribers(String autho_token, RequestModel respObj);

    public ResponseModel updateEmailSubscribers(String autho_token, RequestModel respObj);

    public ResponseModel deleteEmailSubscribers(String autho_token, RequestModel respObj);

    public ResponseModel getAllEmailSubscribers(String autho_token, RequestModel respObj);

    public ResponseModel getEmailSubscribersById(String autho_token, RequestModel respObj);
}
