package com.bizmobia.vgwallet.vgwapp.serviceImpl;

import com.bizmobia.vgwallet.vgwapp.dao.CrossLoginDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bizmobia.vgwallet.vgwapp.service.CrossLoginService;
import com.bizmobia.vgwallet.vgwapp.dao.CompanyConfigDao;
import com.bizmobia.vgwallet.vgwapp.dao.CustomerDao;
import com.bizmobia.vgwallet.vgwapp.models.CompanyConfig;
import com.bizmobia.vgwallet.vgwapp.models.CrossLogin;
import com.bizmobia.vgwallet.vgwapp.models.Customer;
import com.bizmobia.vgwallet.vgwapp.request.RequestModel;
import com.bizmobia.vgwallet.vgwapp.response.LoginResponse;
import com.bizmobia.vgwallet.vgwapp.response.ResponseModel;
import com.bizmobia.vgwallet.vgwapp.service.EncryptionFile;
import com.bizmobia.vgwallet.vgwapp.service.ObjectMapperUtility;
import com.bizmobia.vgwallet.vgwapp.utilities.DateFormate;
import com.bizmobia.vgwallet.vgwapp.websocket.ChatEndpoint;
import com.bizmobia.vgwallet.vgwapp.websocket.Messenger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Vishnu
 */
@Service
@Transactional
public class CrossLoginServiceImpl implements CrossLoginService {

    private static final Logger logger = LoggerFactory.getLogger(CrossLoginServiceImpl.class);

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    CompanyConfigDao companyConfigDao;

    @Autowired
    CrossLoginDao crossLoginDao;

    @Autowired
    private EncryptionFile encryptionFile;

    @Autowired
    private ObjectMapperUtility objectMapperUtility;

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    @Override
    public String crossLoginRequest(String autho_token, String request) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, request);
            if (jsonObj != null) {
                RequestModel requestObj = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
                String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
                if (decryptdId.equals(requestObj.getUserId())) {
                    CrossLogin cl = crossLoginDao.getCrossLoginByUniqueId(requestObj.getExtraVariable());
                    if (cl != null) {
                        Customer c = new Customer();
                        c.setPhoneNumber(requestObj.getUserId());
                        Customer customers = customerDao.getCustomerByMobileNo(c);
                        cl.setVerified(true);
                        cl.setCustomer(customers);
                        cl = crossLoginDao.save(cl);
                        if (cl != null) {
                            String[] splitStr = encryptionFile.generateEncryptedKey(customers.getPhoneNumber()).split("bizm");
                            statuResponse.setAuthToken(splitStr[1] + encryptionFile.encrypt(splitStr[0], EncryptionFile.initVector, customers.getPhoneNumber()));
                            statuResponse.setStatusCode(0);
                            statuResponse.setRespObject(customers);
                            statuResponse.setMessage("Login Success...");

                            Messenger message = new Messenger();
                            message.setResponse(statuResponse);
                            ChatEndpoint.login(message);

                            statuResponse.setStatusCode(0);
                            statuResponse.setMessage("Success");
                            logger.info("CrossLoginServiceImpl.class", "crossLoginRequest()", "No Cross Login Found");
                        } else {
                            statuResponse.setStatusCode(4);
                            statuResponse.setMessage("Failure");
                            logger.error("CrossLoginServiceImpl.class", "crossLoginRequest()", "Failure");
                        }
                    } else {
                        statuResponse.setStatusCode(2);
                        statuResponse.setMessage("No Cross Login Found");
                        logger.error("CrossLoginServiceImpl.class", "crossLoginRequest()", "No Cross Login Found");
                    }
                } else {
                    statuResponse.setStatusCode(3);
                    statuResponse.setMessage("Invalid User Access");
                    logger.error("CrossLoginServiceImpl.class", "crossLoginRequest()", "Invalid User Access");
                }
            } else {
                statuResponse.setStatusCode(3);
                statuResponse.setMessage("Invalid Key");
                logger.error("CrossLoginServiceImpl.class", "crossLoginRequest()", "Invalid Key");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setStatusCode(1);
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            logger.error("CrossLoginServiceImpl.class", "crossLoginRequest()", "Please Try After Sometime" + e.getMessage());
        } finally {
            return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(statuResponse)));
        }
    }

    @Override
    public ResponseModel crossLoginCheck() {
        ResponseModel statuResponse = new ResponseModel();
        try {
            List<CrossLogin> list = crossLoginDao.findAll();

            CompanyConfig cc = companyConfigDao.findAll().get(0);
            File file;
            Calendar current = DateFormate.getCalendarForNowdate(new Date());
            Calendar rawdate;
            int date;
            if (!list.isEmpty()) {
                for (CrossLogin cl : list) {
                    rawdate = DateFormate.getCalendarForNowdate(cl.getCreatedDate());
                    rawdate.add(Calendar.MINUTE, 10);
                    date = current.getTime().compareTo(rawdate.getTime());
                    if (date < 0) {
                        crossLoginDao.delete(cl);
                    }
                }
            }
            statuResponse.setMessage("Old Cross Login Deleted Successfully");
            statuResponse.setStatusCode(0);
            logger.info("CronJobServiceImpl.class", "crossLoginCheck()", "Old Cross Login Deleted Successfully");
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            statuResponse.setStatusCode(1);
            logger.error("CronJobServiceImpl.class", "crossLoginCheck()", "Fail to Login" + e.getMessage());
        }
        return statuResponse;
    }

}
