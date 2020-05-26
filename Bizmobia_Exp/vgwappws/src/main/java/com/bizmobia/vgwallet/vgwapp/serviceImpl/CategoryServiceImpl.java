package com.bizmobia.vgwallet.vgwapp.serviceImpl;

import com.bizmobia.vgwallet.vgwapp.service.CategoryService;
import com.bizmobia.vgwallet.vgwapp.dao.CategoryDao;
import com.bizmobia.vgwallet.vgwapp.exception.ResourceNotFoundException;
import com.bizmobia.vgwallet.vgwapp.models.Category;
import com.bizmobia.vgwallet.vgwapp.request.RequestModel;
import com.bizmobia.vgwallet.vgwapp.response.ResponseModel;
import com.bizmobia.vgwallet.vgwapp.service.EncryptionFile;
import com.bizmobia.vgwallet.vgwapp.service.ObjectMapperUtility;
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
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private ObjectMapperUtility objectMapperUtility;

    @Autowired
    private EncryptionFile encryptionFile;

    private final Gson gson = new Gson();

    @Override
    public ResponseModel addCategory(String autho_token, RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
            if (decryptdId.equals(requestObj.getUserId())) {
                Category categoryObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), Category.class);
                statuResponse.setRespObject(categoryDao.save(categoryObj));
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
    public ResponseModel updateCategory(String autho_token, RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
            if (decryptdId.equals(requestObj.getUserId())) {
                Category categoryObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), Category.class);
                Category category = categoryDao.findById(categoryObj.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryObj.getCategoryId()));
                objectMapperUtility.nullAwareBeanCopy(category, categoryObj);
                statuResponse.setRespObject(categoryDao.save(category));
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
    public ResponseModel deleteCategory(String autho_token, RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
            if (decryptdId.equals(requestObj.getUserId())) {
                Category categoryObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), Category.class);
                Category category = categoryDao.findById(categoryObj.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryObj.getCategoryId()));
                categoryDao.delete(category);
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
    public ResponseModel getAllCategory(RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
//            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
//            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
//            if (decryptdId.equals(requestObj.getUserId())) {
            List<Category> categoryList = categoryDao.findAll();
            if (categoryList != null) {
                statuResponse.setStatusCode(0);
                statuResponse.setMessage("Success");
                statuResponse.setRespList(categoryList);
            } else {
                statuResponse.setMessage("No Data Found");
                statuResponse.setStatusCode(3);
            }
//            } else {
//                statuResponse.setMessage("Invalid User Access");
//                statuResponse.setStatusCode(3);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            statuResponse.setStatusCode(1);
        } finally {
            return statuResponse;
        }
    }

    @Override
    public ResponseModel getCategoryById(RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
//            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
//            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
//            if (decryptdId.equals(requestObj.getUserId())) {
            Category categoryObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), Category.class);
            Category resultObj = categoryDao.findById(categoryObj.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryObj.getCategoryId()));

            if (resultObj != null) {
                statuResponse.setMessage("Success");
                statuResponse.setStatusCode(0);
                statuResponse.setRespObject(resultObj);
            }
//            } else {
//                statuResponse.setMessage("Invalid User Access");
//                statuResponse.setStatusCode(3);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            statuResponse.setStatusCode(1);
        } finally {
            return statuResponse;
        }
    }

}
