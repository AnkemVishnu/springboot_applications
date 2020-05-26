package com.bizmobia.vgwallet.vgwapp.controller;

import com.bizmobia.vgwallet.vgwapp.service.CategoryService;
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
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping(value = "insertcategory")
    public ResponseModel addCategory(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return categoryService.addCategory(autho_token, respObj);
    }

    @PostMapping(value = "updatecategory")
    public ResponseModel updateCategory(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return categoryService.updateCategory(autho_token, respObj);
    }

    @PostMapping(value = "deletecategory")
    public ResponseModel deleteCategory(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel respObj) {
        return categoryService.deleteCategory(autho_token, respObj);
    }

    @PostMapping(value = "getallcategory")
    public ResponseModel getAllCategory(@RequestBody RequestModel respObj) {
        return categoryService.getAllCategory(respObj);
    }

    @PostMapping(value = "getcategorybyid")
    public ResponseModel getCategoryById(@RequestBody RequestModel respObj) {
        return categoryService.getCategoryById(respObj);
    }
}
