package com.bizmobia.vgwallet.vgwapp.service;

import com.bizmobia.vgwallet.vgwapp.request.RequestModel;
import com.bizmobia.vgwallet.vgwapp.response.ResponseModel;

public interface CategoryService {

    public ResponseModel addCategory(String autho_token, RequestModel respObj);

    public ResponseModel updateCategory(String autho_token, RequestModel respObj);

    public ResponseModel deleteCategory(String autho_token, RequestModel respObj);

    public ResponseModel getAllCategory(RequestModel respObj);

    public ResponseModel getCategoryById(RequestModel respObj);
}
