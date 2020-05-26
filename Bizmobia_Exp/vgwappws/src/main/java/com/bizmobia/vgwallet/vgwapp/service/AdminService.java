package com.bizmobia.vgwallet.vgwapp.service;

;

import com.bizmobia.vgwallet.vgwapp.request.RequestModel;
import com.bizmobia.vgwallet.vgwapp.response.ResponseModel;



public interface AdminService {

    public ResponseModel addAdmin(String autho_token, RequestModel respObj);

    public ResponseModel updateAdmin(String autho_token, RequestModel respObj);

    public ResponseModel deleteAdmin(String autho_token, RequestModel respObj);

    public ResponseModel getAllAdmin(String autho_token, RequestModel respObj);

    public ResponseModel getAdminById(String autho_token, RequestModel respObj);
}
