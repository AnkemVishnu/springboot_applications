package com.vishnu.serviceImpl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vishnu.dao.UserFileDao;
import com.vishnu.dao.UsersDao;
import com.vishnu.model.UserFile;
import com.vishnu.model.Users;
import com.vishnu.requestmodel.FileRequest;
import com.vishnu.requestmodel.LoginRequest;
import com.vishnu.requestmodel.RequestModel;
import com.vishnu.responsemodel.LoginResponse;
import com.vishnu.responsemodel.ResponseModel;
import com.vishnu.service.UserService;
import com.vishnu.utilities.Encryption;
import com.vishnu.utilities.ObjectMapping;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersDao userDao;

    @Autowired
    private UserFileDao fileDao;

    @Autowired
    private Encryption encryption;

    @Autowired
    private ObjectMapping objectMapping;

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    @Override
    public ResponseModel userLogin(RequestModel requestModel) {
        ResponseModel responseModel;
        try {
            LoginRequest request = objectMapping.jsonToObject(gson.toJson(requestModel.getReqObject()), LoginRequest.class);

            Users user = userDao.validateUser(request);
            if (user != null) {
                String[] splitStr = encryption.generateEncryptedKey(user.getMobileNumber()).split("vishnu");

                String autho_token = splitStr[1] + encryption.encrypt(splitStr[0], encryption.encGenKey, user.getMobileNumber());

                LoginResponse response = new LoginResponse();
                response.setName(user.getName());
                response.setMobileNumber(user.getMobileNumber());
                response.setAutho_token(autho_token);

                responseModel = new ResponseModel(0, "Login Success", response);
            } else {
                responseModel = new ResponseModel(2, "User Not Found !");
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseModel = new ResponseModel(1, "Please Try After Sometime !");
        }
        return responseModel;
    }

    @Override
    public ResponseModel getAllUserFiles(String autho_token, RequestModel requestModel) {
        ResponseModel responseModel;
        try {
            String gendynamikey = encryption.reGenerateEncryptedKey(requestModel.getUserId(), autho_token);
            String decryptdId = encryption.decrypt(gendynamikey, encryption.encGenKey, autho_token, requestModel.getUserId().length());
            if (decryptdId.equals(requestModel.getUserId())) {
                List<UserFile> files = fileDao.getAllUserFiles(requestModel.getUserId());

                if (!files.isEmpty()) {
                    responseModel = new ResponseModel(0, "User Files Found !", files);
                } else {
                    responseModel = new ResponseModel(0, "User Files Not Found !");
                }
            } else {
                responseModel = new ResponseModel(3, "Invalid User Access !");
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseModel = new ResponseModel(1, "Please Try After Sometime !");
        }
        return responseModel;
    }

    @Override
    public ResponseModel fileUpload(String autho_token, RequestModel requestModel) {
        ResponseModel responseModel;
        try {
            String gendynamikey = encryption.reGenerateEncryptedKey(requestModel.getUserId(), autho_token);
            String decryptdId = encryption.decrypt(gendynamikey, encryption.encGenKey, autho_token, requestModel.getUserId().length());
            if (decryptdId.equals(requestModel.getUserId())) {
                FileRequest fileRequest = objectMapping.jsonToObject(gson.toJson(requestModel.getReqObject()), FileRequest.class);

                String serverPath = "/var/www/html/xyz_project/public_html/files_uploaded/";
                String serverUrl = "https://www.xyz_project.com/files_uploaded/";

                String actualFileName = fileRequest.getFilename();
                String extension = actualFileName.substring(actualFileName.lastIndexOf("."), actualFileName.length());

                String filename = RandomStringUtils.randomNumeric(8) + extension;

                String filePath = serverPath + filename;
                String fileUrl = serverUrl + filename;

                byte[] byteArray = Base64.decodeBase64(fileRequest.getValue());
                FileOutputStream fos = null;
                try {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        fos = new FileOutputStream(file);
                        file.createNewFile();
                        fos.write(byteArray);
                        fos.flush();
                    }

                    Users user = userDao.getUserByMobile(requestModel.getUserId());

                    UserFile userFile = new UserFile();
                    userFile.setFile(fileUrl);
                    userFile.setFileName(filename);
                    userFile.setUser(user);

                    UserFile result = fileDao.save(userFile);
                    if (result != null) {
                        responseModel = new ResponseModel(0, "File Uploaded Successfully !");
                    } else {
                        responseModel = new ResponseModel(2, "File Upload Failed !");
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                    responseModel = new ResponseModel(1, "Please Try After Sometime !");
                } finally {
                    if (fos != null) {
                        fos.close();
                    }
                }
            } else {
                responseModel = new ResponseModel(3, "Invalid User Access !");
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseModel = new ResponseModel(1, "Please Try After Sometime !");
        }
        return responseModel;
    }

    @Override
    public ResponseModel fileDownload(String autho_token, RequestModel requestModel) {
        ResponseModel responseModel;
        try {
            String gendynamikey = encryption.reGenerateEncryptedKey(requestModel.getUserId(), autho_token);
            String decryptdId = encryption.decrypt(gendynamikey, encryption.encGenKey, autho_token, requestModel.getUserId().length());
            if (decryptdId.equals(requestModel.getUserId())) {
                UserFile userFile = objectMapping.jsonToObject(gson.toJson(requestModel.getReqObject()), UserFile.class);

                InputStream inputStream = new URL(userFile.getFile()).openStream();
                Files.copy(inputStream, Paths.get("C:/Users/username/Downloads/" + userFile.getFileName()), StandardCopyOption.REPLACE_EXISTING);

                responseModel = new ResponseModel(0, "File Downloaded !");
            } else {
                responseModel = new ResponseModel(3, "Invalid User Access !");
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseModel = new ResponseModel(1, "Please Try After Sometime !");
        }
        return responseModel;
    }

}
