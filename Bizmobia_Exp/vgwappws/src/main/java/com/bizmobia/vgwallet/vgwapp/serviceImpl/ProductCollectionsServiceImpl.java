package com.bizmobia.vgwallet.vgwapp.serviceImpl;

import com.bizmobia.vgwallet.vgwapp.service.ProductCollectionsService;
import com.bizmobia.vgwallet.vgwapp.dao.ProductCollectionsDao;
import com.bizmobia.vgwallet.vgwapp.exception.ResourceNotFoundException;
import com.bizmobia.vgwallet.vgwapp.models.ProductCollections;
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
public class ProductCollectionsServiceImpl implements ProductCollectionsService {

    @Autowired
    private ProductCollectionsDao productcollectionsDao;
    @Autowired
    private ObjectMapperUtility objectMapperUtility;

    @Autowired
    private EncryptionFile encryptionFile;

    private final Gson gson = new Gson();

    @Override
    public ResponseModel addProductCollections(String autho_token, RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
            if (decryptdId.equals(requestObj.getUserId())) {
                ProductCollections productcollectionsObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), ProductCollections.class);
                statuResponse.setRespObject(productcollectionsDao.save(productcollectionsObj));
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
    public ResponseModel updateProductCollections(String autho_token, RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
            if (decryptdId.equals(requestObj.getUserId())) {
                ProductCollections productcollectionsObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), ProductCollections.class);
                ProductCollections productcollections = productcollectionsDao.findById(productcollectionsObj.getProductCollectionsId())
                        .orElseThrow(() -> new ResourceNotFoundException("ProductCollections", "id", productcollectionsObj.getProductCollectionsId()));
                objectMapperUtility.nullAwareBeanCopy(productcollections, productcollectionsObj);
                statuResponse.setRespObject(productcollectionsDao.save(productcollections));
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
    public ResponseModel deleteProductCollections(String autho_token, RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
            if (decryptdId.equals(requestObj.getUserId())) {
                ProductCollections productcollectionsObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), ProductCollections.class);
                ProductCollections productcollections = productcollectionsDao.findById(productcollectionsObj.getProductCollectionsId())
                        .orElseThrow(() -> new ResourceNotFoundException("ProductCollections", "id", productcollectionsObj.getProductCollectionsId()));
                productcollectionsDao.delete(productcollections);
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
    public ResponseModel getAllProductCollections(RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
//            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
//            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
//            if (decryptdId.equals(requestObj.getUserId())) {
            List<ProductCollections> productcollectionsList = productcollectionsDao.findAll();
            if (productcollectionsList != null) {
                statuResponse.setStatusCode(0);
                statuResponse.setMessage("Success");
                statuResponse.setRespList(productcollectionsList);
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
    public ResponseModel getProductCollectionsById(RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
//            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
//            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
//            if (decryptdId.equals(requestObj.getUserId())) {
            ProductCollections productcollectionsObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), ProductCollections.class);
            ProductCollections resultObj = productcollectionsDao.findById(productcollectionsObj.getProductCollectionsId())
                    .orElseThrow(() -> new ResourceNotFoundException("ProductCollections", "id", productcollectionsObj.getProductCollectionsId()));

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
