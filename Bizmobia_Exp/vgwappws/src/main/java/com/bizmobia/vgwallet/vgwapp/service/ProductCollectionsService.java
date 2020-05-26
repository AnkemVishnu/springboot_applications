package com.bizmobia.vgwallet.vgwapp.service;

import com.bizmobia.vgwallet.vgwapp.request.RequestModel;
import com.bizmobia.vgwallet.vgwapp.response.ResponseModel;

public interface ProductCollectionsService {

    public ResponseModel addProductCollections(String autho_token, RequestModel respObj);

    public ResponseModel updateProductCollections(String autho_token, RequestModel respObj);

    public ResponseModel deleteProductCollections(String autho_token, RequestModel respObj);

    public ResponseModel getAllProductCollections(RequestModel respObj);

    public ResponseModel getProductCollectionsById(RequestModel respObj);
}
