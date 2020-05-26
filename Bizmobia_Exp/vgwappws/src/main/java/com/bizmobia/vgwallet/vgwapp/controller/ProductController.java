package com.bizmobia.vgwallet.vgwapp.controller;

import com.bizmobia.vgwallet.vgwapp.service.ProductService;
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
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping(value = "insertproduct")
    public ResponseModel addProduct(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return productService.addProduct(autho_token, respObj);
    }

    @PostMapping(value = "updateproduct")
    public ResponseModel updateProduct(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return productService.updateProduct(autho_token, respObj);
    }

    @PostMapping(value = "deleteproduct")
    public ResponseModel deleteProduct(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return productService.deleteProduct(autho_token, respObj);
    }

//    @PostMapping(value = "getallproduct")
//    public ResponseModel getAllProduct(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
//        return productService.getAllProduct(autho_token, respObj);
//    }
//
//    @PostMapping(value = "getproductbyid")
//    public ResponseModel getProductById(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
//        return productService.getProductById(autho_token, respObj);
//    }
    
    @PostMapping(value = "getallproduct")
    public ResponseModel getAllProduct(@RequestBody RequestModel respObj) {
        return productService.getAllProduct(respObj);
    }

    @PostMapping(value = "getproductbyid")
    public ResponseModel getProductById(@RequestBody RequestModel respObj) {
        return productService.getProductById(respObj);
    }
    
    @PostMapping(value = "getallproductcollectionwise")
    public ResponseModel getAllProductCollectionWise(@RequestBody RequestModel respObj) {
        return productService.getAllProductCollectionWise(respObj);
    }
}
