package com.bizmobia.vgwallet.vgwapp.service;

;

import com.bizmobia.vgwallet.vgwapp.request.RequestModel;
import com.bizmobia.vgwallet.vgwapp.response.ResponseModel;



public interface AdminRolesService {

    public ResponseModel addAdminRoles(String autho_token, RequestModel respObj);

    public ResponseModel updateAdminRoles(String autho_token, RequestModel respObj);

    public ResponseModel deleteAdminRoles(String autho_token, RequestModel respObj);

    public ResponseModel getAllAdminRoles(String autho_token, RequestModel respObj);

    public ResponseModel getAdminRolesById(String autho_token, RequestModel respObj);
}
