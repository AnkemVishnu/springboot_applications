package com.vishnu.service;

import com.vishnu.requestmodel.RequestModel;
import com.vishnu.responsemodel.ResponseModel;

public interface UserService {

    public ResponseModel userLogin(RequestModel requestModel);

    public ResponseModel getAllUserFiles(String autho_token, RequestModel requestModel);

    public ResponseModel fileUpload(String autho_token, RequestModel requestModel);

    public ResponseModel fileDownload(String autho_token, RequestModel requestModel);

}
