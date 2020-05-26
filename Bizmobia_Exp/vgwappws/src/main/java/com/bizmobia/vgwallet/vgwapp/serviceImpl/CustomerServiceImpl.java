package com.bizmobia.vgwallet.vgwapp.serviceImpl;

import com.bizmobia.vgwallet.vgwapp.utilities.VGWmobilewsApi;
import com.bizmobia.vgwallet.vgwapp.dao.*;
import com.bizmobia.vgwallet.vgwapp.exception.*;
import com.bizmobia.vgwallet.vgwapp.service.*;
import com.bizmobia.vgwallet.vgwapp.models.*;
import com.bizmobia.vgwallet.vgwapp.request.*;
import com.bizmobia.vgwallet.vgwapp.response.*;
import com.google.gson.Gson;
import java.util.List;
import javax.mail.MessagingException;
import javax.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author bizmobia12
 */
@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private ObjectMapperUtility objectMapperUtility;

    @Autowired
    private MailAndMessageSender mailAndMessageSender;

    @Autowired
    private EncryptionFile encryptionFile;

    private final Gson gson = new Gson();

    public ResponseModel otpForRegistation(Customer customers) throws MessagingException {
        ResponseModel statuResponse = new ResponseModel();
        String otp = RandomStringUtils.randomNumeric(6);
        if (customers.getOtp() == null) {
            customers.setOtp(otp);
            customerDao.save(customers);
            statuResponse.setStatusCode(0);
            statuResponse.setMessage("Otp Created");
        } else {
            customers.setOtp(otp);
            customerDao.save(customers);
            statuResponse.setStatusCode(0);
            statuResponse.setMessage("Otp Created");
        }
        if (statuResponse.getStatusCode() == 0) {
            statuResponse = mailAndMessageSender.sendSmsForOtp(customers, otp);
        }
        return statuResponse;
    }

    @Override
    public ResponseModel registration(RequestModel requestModel) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            Customer customerObj = objectMapperUtility.jsonToObject(gson.toJson(requestModel.getReqObject()), Customer.class);
            Customer cnt = customerDao.getCustomerByMobileNo(customerObj);
            if (cnt == null) {
                customerObj.setStatus(Boolean.FALSE);
                statuResponse = otpForRegistation(customerObj);
            } else {
                if (cnt.getPassword() == null && !cnt.getStatus()) {
                    objectMapperUtility.nullAwareBeanCopy(cnt, customerObj);
                    statuResponse = otpForRegistation(cnt);
                } else {
                    statuResponse.setMessage("Your  Mobile Number or EmailId already register");
                    statuResponse.setStatusCode(2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime ");
            statuResponse.setStatusCode(1);
        }
        return statuResponse;
    }

    @Override
    public ResponseModel otpVerification(RequestModel requestModel) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            Customer customers = objectMapperUtility.jsonToObject(gson.toJson(requestModel.getReqObject()), Customer.class);
            Customer custObj = customerDao.getCustomerByMobileNo(customers);
            if (custObj != null) {
                if ((custObj.getOtp() == customers.getOtp()) || String.valueOf(custObj.getOtp()).equals((String.valueOf(customers.getOtp())))) {
                    statuResponse.setStatusCode(0);
                    statuResponse.setMessage("OTP verified successfully");
                } else {
                    statuResponse.setStatusCode(1);
                    statuResponse.setMessage("OTP verification failed");
                }
            } else {
                statuResponse.setStatusCode(10);
                statuResponse.setMessage("No customer found with this mobile number");
            }
        } catch (Exception e) {
            statuResponse.setStatusCode(11);
            statuResponse.setMessage("Error while verifying OTP");
        }
        return statuResponse;
    }

    @Override
    public ResponseModel resendOtp(RequestModel requestModel) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            Customer customers = objectMapperUtility.jsonToObject(gson.toJson(requestModel.getReqObject()), Customer.class);
            Customer custObj = customerDao.getCustomerByMobileNo(customers);
            if (custObj != null) {
                String otp = RandomStringUtils.randomNumeric(6);
                custObj.setOtp(otp);
                customerDao.save(custObj);
                statuResponse = mailAndMessageSender.sendSmsForOtp(custObj, otp);
                mailAndMessageSender.sendMail(custObj.getEmail(), custObj.getFullname(), otp);
                statuResponse.setStatusCode(0);
                statuResponse.setMessage("Otp Send Successfully");
            } else {
                statuResponse.setStatusCode(0);
                statuResponse.setMessage("No customer found with this mobile or emailid");
            }
        } catch (Exception e) {
            statuResponse.setStatusCode(10);
            statuResponse.setMessage("Error while sending otp");
        } finally {
            return statuResponse;
        }

    }

    @Override
    public ResponseModel savePassword(RequestModel requestModel) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            Customer customers = objectMapperUtility.jsonToObject(gson.toJson(requestModel.getReqObject()), Customer.class);
            Customer custObj = customerDao.getCustomerByMobileNo(customers);
            if (custObj != null) {
                String[] splitStr = encryptionFile.generateEncryptedKey(custObj.getPhoneNumber()).split("bizm");
                String generatedSecuredPasswordHash = encryptionFile.generateStorngPasswordHash(customers.getPassword());
                Kyc kycObj = new Kyc();
                kycObj.setIsfullkyc(Boolean.FALSE);
                kycObj.setIsotpverify(Boolean.TRUE);
                kycObj.setIshalfkyc(Boolean.TRUE);
                kycObj.setPostalCode(custObj.getPostalCode());
                kycObj.setLocality(custObj.getCityName());
                kycObj.setGender(custObj.getGender());
                kycObj.setFullname(custObj.getFullname());
                kycObj.setIsmobileverified(Boolean.TRUE);
                kycObj.setIsotpverify(Boolean.TRUE);
                kycObj.setEmailid(custObj.getEmail());
                kycObj.setMobilenumber(custObj.getPhoneNumber());
                kycObj.setPassword(generatedSecuredPasswordHash);
                kycObj.setPresentaddress(custObj.getStreet());
                RequestModel req = new RequestModel();
                req.setReqObject(kycObj);
                String apiResp = VGWmobilewsApi.webRegister(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(req)));
                String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, apiResp);
                if (jsonObj != null) {
                    ResponseModel respModel = objectMapperUtility.jsonToObject(jsonObj, ResponseModel.class);
                    if (respModel.getStatusCode() == 0) {
                        custObj.setStatus(Boolean.TRUE);
                        custObj.setPassword(generatedSecuredPasswordHash);
                        customerDao.save(custObj);
                        statuResponse.setStatusCode(0);
                        statuResponse.setMessage("password Successfully saved");
                    } else {
                        statuResponse.setStatusCode(1);
                        statuResponse.setMessage("Failed to Register");
                    }
                } else {
                    statuResponse.setStatusCode(1);
                    statuResponse.setMessage("Invalid Input");
                }

            } else {
                statuResponse.setStatusCode(2);
                statuResponse.setMessage("No customer found with this mobile or emailid");
            }

        } catch (Exception e) {
            statuResponse.setStatusCode(1);
            statuResponse.setMessage("Error while saving password");
        } finally {
            return statuResponse;
        }

    }

    @Override
    public ResponseModel loginRequest(RequestModel requestModel) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            Customer customers = objectMapperUtility.jsonToObject(gson.toJson(requestModel.getReqObject()), Customer.class);
            Customer customersDb = customerDao.getCustomerByMobileNo(customers);
            if (customersDb != null) {
                if (customersDb.getStatus()) {//checking customer is active or not
                    if (encryptionFile.validatePassword(customers.getPassword(), customersDb.getPassword())) {
                        String[] splitStr = encryptionFile.generateEncryptedKey(customers.getPhoneNumber()).split("bizm");
                        statuResponse.setAuthToken(splitStr[1] + encryptionFile.encrypt(splitStr[0], EncryptionFile.initVector, customers.getPhoneNumber()));
                        statuResponse.setStatusCode(0);
                        statuResponse.setRespObject(customersDb);
                        statuResponse.setMessage("Login Success...");
                    } else {
                        statuResponse.setStatusCode(1);
                        statuResponse.setMessage("Please Give Correct Password");
                    }
                } else {
                    statuResponse.setStatusCode(1);
                    statuResponse.setMessage("Please Contact Your Help Provider");
                }
            } else {
                statuResponse.setStatusCode(10);
                statuResponse.setMessage("Please Give Correct User Name");
            }
        } catch (Exception e) {
            statuResponse.setStatusCode(10);
            statuResponse.setMessage("Please Try After Sometimes");
        }
        return statuResponse;
    }

    @Override
    public ResponseModel addCustomer(RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            Customer customerObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), Customer.class);
            Customer c = customerDao.save(customerObj);
            if (c != null) {
                statuResponse.setRespObject(c);
                statuResponse.setStatusCode(0);
                statuResponse.setMessage("Successfully Inserted");
            } else {
                statuResponse.setStatusCode(2);
                statuResponse.setMessage("Failed To Inserted");
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
    public ResponseModel updateCustomer(RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            Customer customerObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), Customer.class);
            Customer customer = customerDao.findById(customerObj.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerObj.getCustomerId()));
            objectMapperUtility.nullAwareBeanCopy(customer, customerObj);
            Customer c = customerDao.save(customer);
            if (c != null) {
                statuResponse.setRespObject(c);
                statuResponse.setStatusCode(0);
                statuResponse.setMessage("Updated Successful");
            } else {
                statuResponse.setStatusCode(2);
                statuResponse.setMessage("Failed To Update");
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
    public ResponseModel deleteCustomer(String autho_token, RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
            if (decryptdId.equals(requestObj.getUserId())) {
                Customer customerObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), Customer.class);
                Customer customer = customerDao.findById(customerObj.getCustomerId())
                        .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerObj.getCustomerId()));
                customerDao.delete(customer);
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
    public ResponseModel getAllCustomer(String autho_token, RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
            if (decryptdId.equals(requestObj.getUserId())) {
                List<Customer> customerList = customerDao.findAll();
                if (customerList != null) {
                    statuResponse.setStatusCode(0);
                    statuResponse.setMessage("Success");
                    statuResponse.setRespList(customerList);
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
    public ResponseModel getCustomerById(String autho_token, RequestModel requestObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
            if (decryptdId.equals(requestObj.getUserId())) {
                Customer customerObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), Customer.class);
                Customer resultObj = customerDao.findById(customerObj.getCustomerId())
                        .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerObj.getCustomerId()));

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
