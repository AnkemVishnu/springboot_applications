package com.vishnu.controller;

import com.vishnu.requestmodel.RequestModel;
import com.vishnu.responsemodel.ResponseModel;
import com.vishnu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("/userlogin")
    public @ResponseBody
    ResponseModel userLogin(@RequestBody RequestModel requestModel) {
        return service.userLogin(requestModel);
    }
    
    @PostMapping("/getalluserfiles")
    public @ResponseBody
    ResponseModel getAllUserFiles(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel requestModel) {
        return service.getAllUserFiles(autho_token, requestModel);
    }
    
    @PostMapping("/fileupload")
    public @ResponseBody
    ResponseModel fileUpload(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel requestModel) {
        return service.fileUpload(autho_token, requestModel);
    }
    
    @PostMapping("/filedownload")
    public @ResponseBody
    ResponseModel fileDownload(@RequestHeader("auth_token") String autho_token, @RequestBody RequestModel requestModel) {
        return service.fileDownload(autho_token, requestModel);
    }

}
