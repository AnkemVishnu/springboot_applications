package com.bizmobia.vgwallet.mobile.service;

import com.bizmobia.vgwallet.request.ImageRequest;
import com.bizmobia.vgwallet.request.RequestModel;
import com.bizmobia.vgwallet.response.ResponseModel;

/**
 *
 * @author Vaibhav
 */
public interface ImageUploader {

    public String imageUpload(ImageRequest imageRequest);

    public ResponseModel instantImageUpload(String autho_token, RequestModel reqModel);

    public ResponseModel instantImageDelete(String autho_token, RequestModel reqModel);
}
