package com.bizmobia.vgwallet.vgwapp.service;

import com.bizmobia.vgwallet.vgwapp.request.RequestModel;
import com.bizmobia.vgwallet.vgwapp.response.ResponseModel;

public interface ProductService {

    public ResponseModel addProduct(String autho_token, RequestModel respObj);

    public ResponseModel updateProduct(String autho_token, RequestModel respObj);

    public ResponseModel deleteProduct(String autho_token, RequestModel respObj);

    public ResponseModel getAllProduct(RequestModel respObj);

    public ResponseModel getProductById(RequestModel respObj);
    
    public ResponseModel getAllProductCollectionWise(RequestModel respObj);
}
