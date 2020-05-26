package com.bizmobia.vgwallet.vgwapp.service;

import com.bizmobia.vgwallet.vgwapp.request.RequestModel;
import com.bizmobia.vgwallet.vgwapp.response.ResponseModel;
import com.bizmobia.vgwallet.vgwapp.models.*;

public interface CompanyConfigService {

    public ResponseModel addCompanyConfig(String autho_token, RequestModel respObj);

    public ResponseModel updateCompanyConfig(String autho_token, RequestModel respObj);

    public ResponseModel deleteCompanyConfig(String autho_token, RequestModel respObj);

    public ResponseModel getAllCompanyConfig(String autho_token, RequestModel respObj);

    public ResponseModel getCompanyConfigById(String autho_token, RequestModel respObj);
}
