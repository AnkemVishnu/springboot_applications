package com.bizmobia.vgwallet.vgwapp.serviceImpl;

import com.bizmobia.vgwallet.vgwapp.dao.*;
import com.bizmobia.vgwallet.vgwapp.exception.*;
import com.bizmobia.vgwallet.vgwapp.service.*;
import com.bizmobia.vgwallet.vgwapp.models.*;
import com.bizmobia.vgwallet.vgwapp.request.*;
import com.bizmobia.vgwallet.vgwapp.response.*;
import com.google.gson.Gson;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author bizmobia12
 */
@Service
@Transactional
public class AdminRolesServiceImpl implements AdminRolesService {

    @Autowired
    private AdminRolesDao adminrolesDao;
    @Autowired
    private ObjectMapperUtility objectMapperUtility;

    @Autowired
    private EncryptionFile encryptionFile;

    private final Gson gson = new Gson();

    @Override
    public ResponseModel addAdminRoles(String autho_token, RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
            if (decryptdId.equals(requestObj.getUserId())) {
                AdminRoles adminrolesObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), AdminRoles.class);
                statuResponse.setRespObject(adminrolesDao.save(adminrolesObj));
                statuResponse.setStatusCode(0);
                statuResponse.setMessage("Successfully Inserted");
            } else {
                statuResponse.setMessage("Invalid User Access");
                statuResponse.setStatusCode(3);
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            statuResponse.setStatusCode(1);
        } finally {
            return statuResponse;
        }
    }

    @Override
    public ResponseModel updateAdminRoles(String autho_token, RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
            if (decryptdId.equals(requestObj.getUserId())) {
                AdminRoles adminrolesObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), AdminRoles.class);
                AdminRoles adminroles = adminrolesDao.findById(adminrolesObj.getAdminRoleId())
                        .orElseThrow(() -> new ResourceNotFoundException("AdminRoles", "id", adminrolesObj.getAdminRoleId()));
                objectMapperUtility.nullAwareBeanCopy(adminroles, adminrolesObj);
                statuResponse.setRespObject(adminrolesDao.save(adminroles));
                statuResponse.setStatusCode(0);
                statuResponse.setMessage("Updated Successful");
            } else {
                statuResponse.setMessage("Invalid User Access");
                statuResponse.setStatusCode(3);
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            statuResponse.setStatusCode(1);
        } finally {
            return statuResponse;
        }
    }

    @Override
    public ResponseModel deleteAdminRoles(String autho_token, RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
            if (decryptdId.equals(requestObj.getUserId())) {
                AdminRoles adminrolesObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), AdminRoles.class);
                AdminRoles adminroles = adminrolesDao.findById(adminrolesObj.getAdminRoleId())
                        .orElseThrow(() -> new ResourceNotFoundException("AdminRoles", "id", adminrolesObj.getAdminRoleId()));
                adminrolesDao.delete(adminroles);
                ResponseEntity.ok().build();
                statuResponse.setStatusCode(0);
                statuResponse.setMessage("Deleted Successful");
            } else {
                statuResponse.setMessage("Invalid User Access");
                statuResponse.setStatusCode(3);
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            statuResponse.setStatusCode(1);
        } finally {
            return statuResponse;
        }
    }

    @Override
    public ResponseModel getAllAdminRoles(String autho_token, RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
            if (decryptdId.equals(requestObj.getUserId())) {
                List<AdminRoles> adminrolesList = adminrolesDao.findAll();
                if (adminrolesList != null) {
                    statuResponse.setStatusCode(0);
                    statuResponse.setMessage("Success");
                    statuResponse.setRespList(adminrolesList);
                } else {
                    statuResponse.setMessage("No Data Found");
                    statuResponse.setStatusCode(3);
                }
            } else {
                statuResponse.setMessage("Invalid User Access");
                statuResponse.setStatusCode(3);
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            statuResponse.setStatusCode(1);
        } finally {
            return statuResponse;
        }
    }

    @Override
    public ResponseModel getAdminRolesById(String autho_token, RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
            if (decryptdId.equals(requestObj.getUserId())) {
                AdminRoles adminrolesObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), AdminRoles.class);
                AdminRoles resultObj = adminrolesDao.findById(adminrolesObj.getAdminRoleId())
                        .orElseThrow(() -> new ResourceNotFoundException("AdminRoles", "id", adminrolesObj.getAdminRoleId()));

                if (resultObj != null) {
                    statuResponse.setMessage("Success");
                    statuResponse.setStatusCode(0);
                    statuResponse.setRespObject(resultObj);
                }
            } else {
                statuResponse.setMessage("Invalid User Access");
                statuResponse.setStatusCode(3);
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            statuResponse.setStatusCode(1);
        } finally {
            return statuResponse;
        }
    }

}
