package com.bizmobia.vgwallet.vgwapp.controller;

import com.bizmobia.vgwallet.vgwapp.request.RequestModel;
import com.bizmobia.vgwallet.vgwapp.response.ResponseModel;
import com.bizmobia.vgwallet.vgwapp.service.ImageUploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Vishnu
 */
@RestController
@RequestMapping("/")
public class ImageUploaderController {

    @Autowired
    private ImageUploader service;

    @PostMapping(value = "instantimageupload")
    public ResponseModel instantImageUpload(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return service.instantImageUpload(autho_token, respObj);
    }

    @PostMapping(value = "instantimagedelete")
    public ResponseModel instantImageDelete(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return service.instantImageDelete(autho_token, respObj);
    }
}
