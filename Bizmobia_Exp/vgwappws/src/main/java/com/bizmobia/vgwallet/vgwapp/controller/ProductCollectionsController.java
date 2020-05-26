package com.bizmobia.vgwallet.vgwapp.controller;

import com.bizmobia.vgwallet.vgwapp.service.ProductCollectionsService;
import com.bizmobia.vgwallet.vgwapp.request.RequestModel;
import com.bizmobia.vgwallet.vgwapp.response.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/")
public class ProductCollectionsController {

    @Autowired
    private ProductCollectionsService productcollectionsService;

    @PostMapping(value = "insertproductcollections")
    public ResponseModel addProductCollections(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return productcollectionsService.addProductCollections(autho_token, respObj);
    }

    @PostMapping(value = "updateproductcollections")
    public ResponseModel updateProductCollections(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return productcollectionsService.updateProductCollections(autho_token, respObj);
    }

    @PostMapping(value = "deleteproductcollections")
    public ResponseModel deleteProductCollections(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return productcollectionsService.deleteProductCollections(autho_token, respObj);
    }

//    @PostMapping(value = "getallproductcollections")
//    public ResponseModel getAllProductCollections(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
//        return productcollectionsService.getAllProductCollections(autho_token, respObj);
//    }
//
//    @PostMapping(value = "getproductcollectionsbyid")
//    public ResponseModel getProductCollectionsById(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
//        return productcollectionsService.getProductCollectionsById(autho_token, respObj);
//    }
    @PostMapping(value = "getallproductcollections")
    public ResponseModel getAllProductCollections(@RequestBody RequestModel respObj) {
        return productcollectionsService.getAllProductCollections(respObj);
    }

    @PostMapping(value = "getproductcollectionsbyid")
    public ResponseModel getProductCollectionsById(@RequestBody RequestModel respObj) {
        return productcollectionsService.getProductCollectionsById(respObj);
    }
}
