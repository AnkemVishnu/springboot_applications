package com.bizmobia.vgwallet.vgwapp.service;

import com.bizmobia.vgwallet.vgwapp.request.ImageRequest;
import com.bizmobia.vgwallet.vgwapp.request.RequestModel;
import com.bizmobia.vgwallet.vgwapp.response.ResponseModel;

/**
 *
 * @author Vaibhav
 */
public interface ImageUploader {

    public String imageUpload(ImageRequest imageRequest);

    public ResponseModel instantImageUpload(String autho_token, RequestModel reqModel);

    public ResponseModel instantImageDelete(String autho_token, RequestModel reqModel);
}
