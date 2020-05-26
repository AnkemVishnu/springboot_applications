package com.bizmobia.vgwallet.vgwapp.serviceImpl;

import com.bizmobia.vgwallet.vgwapp.dao.ProductCollectionsDao;
import com.bizmobia.vgwallet.vgwapp.service.ProductService;
import com.bizmobia.vgwallet.vgwapp.dao.ProductDao;
import com.bizmobia.vgwallet.vgwapp.exception.ResourceNotFoundException;
import com.bizmobia.vgwallet.vgwapp.models.Product;
import com.bizmobia.vgwallet.vgwapp.models.ProductCollections;
import com.bizmobia.vgwallet.vgwapp.request.RequestModel;
import com.bizmobia.vgwallet.vgwapp.response.CollectionWiseProduct;
import com.bizmobia.vgwallet.vgwapp.response.ResponseModel;
import com.bizmobia.vgwallet.vgwapp.service.EncryptionFile;
import com.bizmobia.vgwallet.vgwapp.service.ObjectMapperUtility;
import com.google.gson.Gson;
import java.util.ArrayList;
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
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ProductCollectionsDao productCollectionsDao;

    @Autowired
    private ObjectMapperUtility objectMapperUtility;

    @Autowired
    private EncryptionFile encryptionFile;

    private final Gson gson = new Gson();

    @Override
    public ResponseModel addProduct(String autho_token, RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
            if (decryptdId.equals(requestObj.getUserId())) {
                Product productObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), Product.class);
                Product p = productDao.checkDuplicatePcode(productObj.getProductCode());
                if (p == null) {
                    statuResponse.setRespObject(productDao.save(productObj));
                    statuResponse.setStatusCode(0);
                    statuResponse.setMessage("Successfully Inserted");
                } else {
                    statuResponse.setStatusCode(2);
                    statuResponse.setMessage("Duplicate Product Code");
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
    public ResponseModel updateProduct(String autho_token, RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
            if (decryptdId.equals(requestObj.getUserId())) {
                Product productObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), Product.class);
                Product product = productDao.findById(productObj.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productObj.getProductId()));
                objectMapperUtility.nullAwareBeanCopy(product, productObj);
                statuResponse.setRespObject(productDao.save(product));
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
    public ResponseModel deleteProduct(String autho_token, RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
            if (decryptdId.equals(requestObj.getUserId())) {
                Product productObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), Product.class);
                Product product = productDao.findById(productObj.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productObj.getProductId()));
                productDao.delete(product);
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
    public ResponseModel getAllProduct(RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
//            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
//            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
//            if (decryptdId.equals(requestObj.getUserId())) {
            List<Product> productList = productDao.findAll();
            if (productList != null) {
                statuResponse.setStatusCode(0);
                statuResponse.setMessage("Success");
                statuResponse.setRespList(productList);
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
    public ResponseModel getProductById(RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
//            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
//            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
//            if (decryptdId.equals(requestObj.getUserId())) {
            Product productObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), Product.class);
            Product resultObj = productDao.findById(productObj.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productObj.getProductId()));

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

    @Override
    public ResponseModel getAllProductCollectionWise(RequestModel respObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            List<CollectionWiseProduct> list = new ArrayList<>();
            List<ProductCollections> collectionList = productCollectionsDao.findAll();
            List<Product> products;
            for (ProductCollections collection : collectionList) {
                products = productDao.getAllProductCollectionWise(collection);
                if (!products.isEmpty()) {
                    list.add(new CollectionWiseProduct(collection, products));
                }
            }
            if (!list.isEmpty()) {
                statuResponse.setStatusCode(0);
                statuResponse.setMessage("Success");
                statuResponse.setRespList(list);
            } else {
                statuResponse.setMessage("No Data Found");
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
