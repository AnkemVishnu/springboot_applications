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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author bizmobia12
 */
@Service
@Transactional
public class CartItemsServiceImpl implements CartItemsService {

    @Autowired
    private CartItemsDao cartitemsDao;
    @Autowired
    private ObjectMapperUtility objectMapperUtility;

    @Autowired
    private EncryptionFile encryptionFile;

    private final Gson gson = new Gson();

    @Override
    public ResponseModel addCartItems(String autho_token, RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
            if (decryptdId.equals(requestObj.getUserId())) {
                CartItems cartitemsObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), CartItems.class);
                statuResponse.setRespObject(cartitemsDao.save(cartitemsObj));
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
    public ResponseModel updateCartItems(String autho_token, RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
            if (decryptdId.equals(requestObj.getUserId())) {
                CartItems cartitemsObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), CartItems.class);
                CartItems cartitems = cartitemsDao.findById(cartitemsObj.getCartItemsId())
                        .orElseThrow(() -> new ResourceNotFoundException("CartItems", "id", cartitemsObj.getCartItemsId()));
                objectMapperUtility.nullAwareBeanCopy(cartitems, cartitemsObj);
                statuResponse.setRespObject(cartitemsDao.save(cartitems));
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
    public ResponseModel deleteCartItems(String autho_token, RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
            if (decryptdId.equals(requestObj.getUserId())) {
                CartItems cartitemsObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), CartItems.class);
                CartItems cartitems = cartitemsDao.findById(cartitemsObj.getCartItemsId())
                        .orElseThrow(() -> new ResourceNotFoundException("CartItems", "id", cartitemsObj.getCartItemsId()));
                cartitemsDao.delete(cartitems);
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
    public ResponseModel getAllCartItems(String autho_token, RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
            if (decryptdId.equals(requestObj.getUserId())) {
                List<CartItems> cartitemsList = cartitemsDao.findAll();
                if (cartitemsList != null) {
                    statuResponse.setStatusCode(0);
                    statuResponse.setMessage("Success");
                    statuResponse.setRespList(cartitemsList);
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
    public ResponseModel getCartItemsById(String autho_token, RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
            if (decryptdId.equals(requestObj.getUserId())) {
                CartItems cartitemsObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), CartItems.class);
                CartItems resultObj = cartitemsDao.findById(cartitemsObj.getCartItemsId())
                        .orElseThrow(() -> new ResourceNotFoundException("CartItems", "id", cartitemsObj.getCartItemsId()));

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
