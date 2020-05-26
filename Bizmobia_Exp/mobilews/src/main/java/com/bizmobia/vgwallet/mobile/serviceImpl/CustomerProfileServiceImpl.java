package com.bizmobia.vgwallet.mobile.serviceImpl;

import com.bizmobia.vgwallet.mobile.dao.DevicesDao;
import com.bizmobia.vgwallet.mobile.dao.KycDao;
import com.bizmobia.vgwallet.mobile.dao.OtpDao;
import com.bizmobia.vgwallet.mobile.dao.WalletDao;
import com.bizmobia.vgwallet.mobile.service.CustomerProfileService;
import com.bizmobia.vgwallet.mobile.service.EncryptionFile;
import com.bizmobia.vgwallet.mobile.service.ObjectMapperUtility;
import com.bizmobia.vgwallet.mobile.service.RolesService;
import com.bizmobia.vgwallet.mobile.service.StatusService;
import com.bizmobia.vgwallet.mobile.service.WalletService;
import com.bizmobia.vgwallet.mobile.utilities.AndroidPushNotification;
import com.bizmobia.vgwallet.mobile.utilities.IosPushNotification;
import com.bizmobia.vgwallet.models.Devices;
import com.bizmobia.vgwallet.models.Kyc;
import com.bizmobia.vgwallet.models.Otp;
import com.bizmobia.vgwallet.models.Status;
import com.bizmobia.vgwallet.models.Wallet;
import com.bizmobia.vgwallet.request.ImageRequest;
import com.bizmobia.vgwallet.request.RequestModel;
import com.bizmobia.vgwallet.response.LoginResponse;
import com.bizmobia.vgwallet.response.ResponseModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.mail.MessagingException;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bizmobia.vgwallet.mobile.service.MailAndMessageSender;
import com.bizmobia.vgwallet.mobile.service.WalletTypeService;
import org.slf4j.LoggerFactory;
import com.bizmobia.vgwallet.mobile.service.ImageUploader;
import com.bizmobia.vgwallet.mobile.utilities.VGWappwsApi;
import com.bizmobia.vgwallet.request.ChangePasswordRequest;
import com.bizmobia.vgwallet.request.Customer;
import java.util.regex.Pattern;

/**
 *
 * @author Vaibhav
 */
@Service
@Transactional
public class CustomerProfileServiceImpl implements CustomerProfileService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CustomerProfileServiceImpl.class);

    @Autowired
    private OtpDao otpDao;

    @Autowired
    private KycDao kycDao;

    @Autowired
    private DevicesDao deviceDao;

    @Autowired
    private WalletDao walletDao;

    @Autowired
    private EncryptionFile encryptionFile;

    @Autowired
    private StatusService statusService;

    @Autowired
    private RolesService rolesService;

    @Autowired
    private MailAndMessageSender mailAndMessageSender;

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletTypeService walletTypeService;

    @Autowired
    private ObjectMapperUtility objectMapperUtility;

    @Autowired
    private ImageUploader imageUploaderService;

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public ResponseModel otpForRegistation(Kyc kycObj) throws MessagingException {
        ResponseModel responseModel = new ResponseModel();
        String otp = RandomStringUtils.randomNumeric(6);
        if (otp != null) {
            Otp otpDbResp = null;
            Otp otps = new Otp();
            otps.setCreatedDate(new Date());
            otps.setCreatedBy(kycObj.getRoleobj());
            otps.setOtpGeneratedTime(new Date());
            otps.setOtp(otp);
            otps.setOtpServiceType("Register");
            otps.setOtpTo(kycObj.getMobilenumber());
            otps.setStatusObj(statusService.getStatusByName("NOTExpire"));
            try {
                Calendar c = Calendar.getInstance();
                c.add(Calendar.MINUTE, 5);
                Date fifteenSecondsAgo = c.getTime();
                otps.setOtpExpiryTime(fifteenSecondsAgo);
                otpDbResp = otpDao.save(otps);
            } catch (Exception e) {
                logger.error("CustomerProfileServiceImpl.class", "otpForRegistation()", "Please Try After Sometime" + e.getMessage());
            }
            if (otpDbResp != null) {
                kycObj.setIsfullkyc(Boolean.FALSE);
                kycObj.setIsotpverify(Boolean.FALSE);
                kycObj.setIshalfkyc(Boolean.TRUE);
                kycObj.setRoleobj(rolesService.getRolesByName("Customer"));
                kycObj.setStatusObj(statusService.getStatusByName("InActive"));
                if (kycObj.getIdkyc() != null) {
                    responseModel.setRespObject(kycDao.save(kycObj));
                    responseModel.setStatusCode(0);
                    responseModel.setMessage("KYC Updated Successfully");
                    logger.info("CustomerProfileServiceImpl.class", "otpForRegistation()", "KYC Updated Successfully");
                } else {
                    responseModel.setRespObject(kycDao.saveAndFlush(kycObj));
                    responseModel.setStatusCode(0);
                    responseModel.setMessage("KYC Saved Successfully");
                }
                if (responseModel.getStatusCode() == 0) {
                    responseModel = mailAndMessageSender.sendSmsForOtp(kycObj, otp);
                } else {
                    responseModel.setStatusCode(1);
                    responseModel.setMessage("Failed To Sent OTP");
                    logger.error("CustomerProfileServiceImpl.class", "otpForRegistation()", "Failed To Sent OTP");
                }
            }
        }
        return responseModel;
    }

    @Override
    public String registration(String requestedObj) {
        ResponseModel responseModel = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestedObj);
            if (jsonObj != null) {
                RequestModel respObj = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                if (respObj != null) {
                    Kyc kycObj = objectMapperUtility.jsonToObject(gson.toJson(respObj.getReqObject()), Kyc.class);
                    Long count = kycDao.checkDuplicates(kycObj);
                    if (count == 0) {
                        responseModel = otpForRegistation(kycObj);
//                        List<Otp> otplist = customerProfileDao.checkOtpsExpiry("NOTExpire", kycObj.getMobilenumber());
                    } else {
                        Kyc kycs = kycDao.getKycByMobile(kycObj);
                        if ((kycs.getIsotpverify() == null || !kycs.getIsotpverify()) && (kycs.getPassword() == null || kycs.getPassword().equals(""))) {
                            objectMapperUtility.nullAwareBeanCopy(kycs, kycObj);
                            kycDao.save(kycs);
                            responseModel = otpForRegistation(kycs);
                        } else {
                            responseModel.setStatusCode(2);
                            responseModel.setMessage("Your  Mobile Number or EmailId already register");
                            logger.error("CustomerProfileServiceImpl.class", "registration()", "Your  Mobile Number or EmailId already register");
                        }
                    }
                }
            } else {
                responseModel.setStatusCode(3);
                responseModel.setMessage("Invalid Input And Key");
                logger.error("CustomerProfileServiceImpl.class", "registration()", "Invalid Input And Key");
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseModel.setStatusCode(1);
            responseModel.setMessage("Plese try after Sometime " + e.getMessage());
            logger.error("CustomerProfileServiceImpl.class", "registration()", "Please Try After Sometime" + e.getMessage());
        }
        return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(responseModel)));
    }

    //here otp is in extra variable and mobile number only input comming
    @Override
    public String otpVerification(String requestedObj) {
        ResponseModel responseModel = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestedObj);
            if (jsonObj != null) {
                RequestModel requestModel = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                if (requestModel != null) {
                    Kyc kycObj = objectMapperUtility.jsonToObject(gson.toJson(requestModel.getReqObject()), Kyc.class);
//                  here following getOtpsByMobile() is getting NOTexpire otp Object 
                    List<Otp> otp = null;
                    if (kycObj != null && kycObj.getEmailid() != null) {
                        kycObj = kycDao.getKycByMobileOrEmail(kycObj.getEmailid());
                        otp = otpDao.getOtpsByMobile("NOTExpire", kycObj.getMobilenumber());
                    } else if (kycObj != null && kycObj.getMobilenumber() != null) {
                        kycObj = kycDao.getKycByMobileOrEmail(kycObj.getMobilenumber());
                        otp = otpDao.getOtpsByMobile("NOTExpire", kycObj.getMobilenumber());
                    }
                    if (otp.size() > 0 || otp != null) {
                        if ((otp.get(0).getOtp().equals(requestModel.getExtraVariable()))) {

                            Status st = statusService.getStatusByName("Expire");
                            otp.get(0).setStatusObj(st);
                            otpDao.save(otp.get(0));
                            Kyc updatekyc = kycDao.getKycByMobile(kycObj);
                            String type = "";
                            if (otp.get(0).getOtpServiceType().equals("FORGETPASSWORD")) {
                                type = "FORGETPASSWORD";
                                updatekyc.setIsemailverified(Boolean.TRUE);
                            } else if (otp.get(0).getOtpServiceType().equals("EMAILVERIFY")) {
                                type = "EMAILVERIFY";
                                updatekyc.setIsemailverified(Boolean.TRUE);
                            } else if (otp.get(0).getOtpServiceType().equals("Register")) {
                                type = "Register";
                                updatekyc.setIsotpverify(Boolean.TRUE);
                                updatekyc.setIsmobileverified(Boolean.TRUE);
                            }
                            kycDao.save(updatekyc);
                            responseModel.setStatusCode(0);
                            responseModel.setMessage(type + "  verified successfully");
                            logger.info("CustomerProfileServiceImpl.class", "otpVerification()", type + "  verified successfully");
                        } else {
                            responseModel.setStatusCode(4);
                            responseModel.setMessage("OTP verification failed");
                            logger.error("CustomerProfileServiceImpl.class", "otpVerification()", "OTP verification failed");
                        }
                    } else {
                        responseModel.setStatusCode(3);
                        responseModel.setMessage("No customer found with this mobile number");
                        logger.error("CustomerProfileServiceImpl.class", "otpVerification()", "No customer found with this mobile number");
                    }
                }
            } else {
                responseModel.setStatusCode(3);
                responseModel.setMessage("Invalid Key");
                logger.error("CustomerProfileServiceImpl.class", "otpVerification()", "Invalid Key");
            }
        } catch (Exception e) {
            responseModel.setStatusCode(1);
            responseModel.setMessage("Error while verifying OTP");
            logger.error("CustomerProfileServiceImpl.class", "otpVerification()", "Please Try After Sometime" + e.getMessage());
        }
        return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(responseModel)));
    }

    @Override
    public String resendOtp(String requestedObj) {
        ResponseModel responseModel = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestedObj);
            if (jsonObj != null) {
                RequestModel requestModel = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                if (requestModel != null) {
                    Kyc kycObj = objectMapperUtility.jsonToObject(gson.toJson(requestModel.getReqObject()), Kyc.class);
                    kycObj = kycDao.getKycByMobile(kycObj);
                    if (kycObj != null) {
                        List<Otp> otps = otpDao.getOtpsByMobile("NOTExpire", kycObj.getMobilenumber());//here check otp is Used Or NOt  Checking
                        if (otps.size() == 0) {
                            //here sending new Otp
                            String otp = RandomStringUtils.randomNumeric(6);
                            Otp otpdb = new Otp();
                            otpdb.setCreatedDate(new Date());
                            otpdb.setCreatedBy(kycObj.getRoleobj());
                            otpdb.setOtpGeneratedTime(new Date());
                            otpdb.setOtp(otp);
                            otpdb.setOtpServiceType("Register");
                            otpdb.setOtpTo(kycObj.getMobilenumber());
                            otpdb.setStatusObj(statusService.getStatusByName("NOTExpire"));
                            try {
                                String timestamp = new java.sql.Timestamp(System.currentTimeMillis()).toString();
                                Date ts = java.sql.Timestamp.valueOf(timestamp);
                                Calendar c = Calendar.getInstance();
                                c.add(Calendar.MINUTE, 5);
                                Date fifteenSecondsAgo = c.getTime();
                                otpdb.setOtpExpiryTime(fifteenSecondsAgo);
                                otpDao.save(otpdb);
                                responseModel = mailAndMessageSender.sendSmsForOtp(kycObj, otp);
                            } catch (Exception e) {
                                logger.error("CustomerProfileServiceImpl.class", "resendOtp()", "Please Try After Sometime" + e.getMessage());
                            }

                        } else {
                            //if not expire then resend same otp
                            if (otps != null) {
                                responseModel = mailAndMessageSender.sendSmsForOtp(kycObj, otps.get(0).getOtp());
                            }
                        }
                    } else {
                        responseModel.setStatusCode(3);
                        responseModel.setMessage("No customer found with this mobile or emailid");
                        logger.error("CustomerProfileServiceImpl.class", "resendOtp()", "No customer found with this mobile or emailid");
                    }
                }
            } else {
                responseModel.setStatusCode(3);
                responseModel.setMessage("Invalid Key");
                logger.error("CustomerProfileServiceImpl.class", "resendOtp()", "Invalid Key");
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseModel.setStatusCode(1);
            responseModel.setMessage("Error while sending otp");
            logger.error("CustomerProfileServiceImpl.class", "resendOtp()", "Please Try After Sometime" + e.getMessage());
        }
        return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(responseModel)));
    }

    @Override
    public String savePassword(String requestedObj) {
        ResponseModel responseModel = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestedObj);
            if (jsonObj != null) {
                RequestModel requestModel = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                if (requestModel != null) {
                    Kyc kycObj = objectMapperUtility.jsonToObject(gson.toJson(requestModel.getReqObject()), Kyc.class);
                    Kyc kycObjdb = kycDao.getKycByMobile(kycObj);
                    if (kycObjdb != null) {
                        String generatedSecuredPasswordHash = encryptionFile.generateStorngPasswordHash(kycObj.getPassword());
                        kycObjdb.setPassword(generatedSecuredPasswordHash);
                        kycObjdb.setWrongattempt(0);
                        kycObjdb.setIsfullkyc(Boolean.FALSE);
                        kycObjdb.setIshalfkyc(Boolean.TRUE);
                        kycObjdb.setRoleobj(rolesService.getRolesByName("Customer"));
                        kycObjdb = kycDao.save(kycObjdb);
                        if (kycObjdb != null) {
                            //here wallet is created
                            Wallet walletObj = new Wallet();
                            walletObj.setKycObj(kycObjdb);
                            walletObj.setCreatedBy(kycObjdb.getRoleobj());
                            Status st = statusService.getStatusByName("InActive");
                            walletObj.setStatusObj(st);
                            responseModel = walletService.addWallet(walletObj);
                            if (responseModel.getStatusCode() == 0) {
                                mailAndMessageSender.sendSmsForPassword(kycObjdb, kycObj.getPassword());
                            }

                            Customer customer = new Customer();
                            customer.setFullname(kycObjdb.getFullname());
                            customer.setPhoneNumber(kycObjdb.getMobilenumber());
                            customer.setEmail(kycObjdb.getEmailid());
                            customer.setGender(kycObjdb.getGender());
                            customer.setPassword(kycObjdb.getPassword());
                            customer.setStatus(Boolean.FALSE);
                            RequestModel request = new RequestModel();
                            request.setReqObject(customer);
                            VGWappwsApi.addCustomer(request);
                        }
                    } else {
                        responseModel.setStatusCode(3);
                        responseModel.setMessage("No customer found with this mobile or emailid");
                        logger.error("CustomerProfileServiceImpl.class", "savePassword()", "No customer found with this mobile or emailid");
                    }
                }
            } else {
                responseModel.setStatusCode(3);
                responseModel.setMessage("Invalid Key");
                logger.error("CustomerProfileServiceImpl.class", "savePassword()", "Invalid Key");
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseModel.setStatusCode(1);
            responseModel.setMessage("Error while saving password");
            logger.error("CustomerProfileServiceImpl.class", "savePassword()", "Please Try After Sometime" + e.getMessage());
        }
        return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(responseModel)));
    }

    @Override
    public String loginRequest(String requestedObj) {
        ResponseModel responseModel = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestedObj);
            if (jsonObj != null) {
                RequestModel requestModel = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                if (requestModel != null) {
                    Kyc kycObj = objectMapperUtility.jsonToObject(gson.toJson(requestModel.getReqObject()), Kyc.class);
                    Kyc kycObjdb = kycDao.getKycByMobile(kycObj);
                    if (kycObjdb != null) {
                        if (encryptionFile.validatePassword(kycObj.getPassword(), kycObjdb.getPassword())) {
                            LoginResponse avr = new LoginResponse();
                            avr.setEmailId(kycObjdb.getEmailid());
                            avr.setName(kycObjdb.getFullname());
                            avr.setMobileNumber(kycObjdb.getMobilenumber());
                            avr.setRole(kycObjdb.getRoleobj());
                            avr.setIsEmailVerified(kycObjdb.getIsemailverified());
                            avr.setIsMobileVerified(kycObjdb.getIsmobileverified());
                            avr.setIsOtpVerified(kycObjdb.getIsotpverify());
                            avr.setStatus(kycObjdb.getStatusObj());
                            String[] splitStr = encryptionFile.generateEncryptedKey(kycObjdb.getMobilenumber()).split("bizm");
                            responseModel.setAuthToken(splitStr[1] + encryptionFile.encrypt(splitStr[0], encryptionFile.encGenKey, kycObj.getMobilenumber()));
                            responseModel.setStatusCode(0);
                            responseModel.setRespObject(avr);
                            responseModel.setMessage("Login Success...");
                            logger.info("CustomerProfileServiceImpl.class", "loginRequest()", "Login Success...");
                        } else {
                            responseModel.setStatusCode(3);
                            responseModel.setMessage("Please Give Correct Password");
                            logger.error("CustomerProfileServiceImpl.class", "loginRequest()", "Please Give Correct Password");
                        }
                    } else {
                        responseModel.setStatusCode(3);
                        responseModel.setMessage("No customer found with this mobile");
                        logger.error("CustomerProfileServiceImpl.class", "loginRequest()", "No customer found with this mobile");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseModel.setStatusCode(1);
            responseModel.setMessage("Fail to Login");
            logger.error("CustomerProfileServiceImpl.class", "loginRequest()", "Please Try After Sometime" + e.getMessage());
        }
        return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(responseModel)));
    }

    @Override
    public String makeFullKyc(String autho_token, String requestedObj) {
        ResponseModel responseModel = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestedObj);
            if (jsonObj != null) {
                RequestModel requestObj = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
                String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
                if (decryptdId.equals(requestObj.getUserId())) {
                    String addressurls = null;
                    String idproofurls = null;
                    if (requestObj.getReqList() != null && requestObj.getReqList().size() > 0) {
                        List<ImageRequest> imageRequest = objectMapperUtility.jsonArrayToObjectList(requestObj.getReqList(), ImageRequest.class);
                        if (imageRequest.size() > 0) {
                            for (int i = 0; i < imageRequest.size(); i++) {
                                if (imageRequest.get(i).getImagefileName().equalsIgnoreCase("addressproof")) {
                                    addressurls = imageUploaderService.imageUpload(imageRequest.get(i));
                                }
                                if (imageRequest.get(i).getImagefileName().equalsIgnoreCase("idproof")) {
                                    idproofurls = imageUploaderService.imageUpload(imageRequest.get(i));
                                }
                            }
                        }
                    }
//                    if ((addressurls != null || !addressurls.equals("")) && (idproofurls != null || !idproofurls.equals(""))) {
                    Kyc kycObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), Kyc.class);
                    Kyc halfkycObj = kycDao.getKycByMobile(kycObj);//here check existing(half) kyc
                    if (halfkycObj != null && !halfkycObj.getIsfullkyc()) {
                        objectMapperUtility.nullAwareBeanCopy(halfkycObj, kycObj);
                        halfkycObj.setAdressproofurl(addressurls);
                        halfkycObj.setIsfullkyc(Boolean.TRUE);
                        Status st = statusService.getStatusByName("Active");
                        halfkycObj.setStatusObj(st);
                        halfkycObj.setIdproofurl(idproofurls);
                        Kyc obj = kycDao.save(halfkycObj);
                        if (obj != null) {
                            Wallet myWallets = new Wallet();
                            myWallets.setKycObj(halfkycObj);
                            myWallets.setStatusObj(st);
                            walletService.updateWallet(autho_token, myWallets);

                            customerCopy(obj);
                        }
                        responseModel.setStatusCode(0);
                        responseModel.setMessage("Full Kyc Done Successfully");
                        logger.info("CustomerProfileServiceImpl.class", "makeFullKyc()", "Full Kyc Done Successfully");
                    } else {
                        responseModel.setStatusCode(2);
                        responseModel.setMessage("You have already Full Kyc");
                        logger.error("CustomerProfileServiceImpl.class", "makeFullKyc()", "You have already Full Kyc");
                    }
                } else {
                    responseModel.setStatusCode(3);
                    responseModel.setMessage("Invalid Key");
                    logger.error("CustomerProfileServiceImpl.class", "makeFullKyc()", "Invalid Key");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseModel.setStatusCode(1);
            responseModel.setMessage("Plese try after Sometime " + e.getMessage());
            logger.error("CustomerProfileServiceImpl.class", "makeFullKyc()", "Please Try After Sometime" + e.getMessage());
        }
        return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(responseModel)));
    }

    @Override
    public String deviceRegistration(String autho_token, String requestModel) {
        ResponseModel responseModel = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestModel);
            if (jsonObj != null) {
                RequestModel requestObj = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
                String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
                if (decryptdId.equals(requestObj.getUserId())) {
                    Devices obj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), Devices.class);
                    Kyc k = new Kyc();
                    k.setMobilenumber(requestObj.getExtraVariable());
                    Kyc kycObj = kycDao.getKycByMobile(k);
                    List<Devices> list = deviceDao.getAllDevicesByMobileNo(k);
                    if (list.isEmpty()) {
                        Devices d = new Devices();
                        objectMapperUtility.nullAwareBeanCopy(d, obj);
                        d.setCreatedDate(new Date());
                        d.setKyc(kycObj);
                        deviceDao.saveAndFlush(d);
                        responseModel.setStatusCode(0);
                        responseModel.setMessage("Device Saved Successfully");
                        logger.info("CustomerProfileServiceImpl.class", "deviceRegistration()", "Device Saved Successfully");
                    } else if (list.size() == 1) {
                        objectMapperUtility.nullAwareBeanCopy(list.get(0), obj);
                        deviceDao.save(list.get(0));
                        responseModel.setStatusCode(0);
                        responseModel.setMessage("Device Updated Successfully");
                        logger.info("CustomerProfileServiceImpl.class", "deviceRegistration()", "Device Updated Successfully");
                    } else {
                        String description = "Your Are Logged In Multiple Devices";
                        String notificationtype = "feedback";
                        String title = "Feedback";
                        for (Devices device : list) {
                            if (device != null) {
                                if (device.getDeviceMaker().equals("IOS")) {
                                    IosPushNotification.sendNotificationToIOS(device.getDeviceToken(), title, description, notificationtype, "");
                                } else {
                                    AndroidPushNotification.sendNotificationToAndroid(title, description, device.getDeviceToken(), notificationtype, "");
                                }
                            }
                        }
                        responseModel.setStatusCode(2);
                        responseModel.setMessage(description);
                        logger.error("CustomerProfileServiceImpl.class", "deviceRegistration()", description);
                    }
                } else {
                    responseModel.setStatusCode(3);
                    responseModel.setMessage("Invalid Key");
                    logger.error("CustomerProfileServiceImpl.class", "deviceRegistration()", "Invalid Key");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseModel.setStatusCode(1);
            responseModel.setMessage("Plese try after Sometime " + e.getMessage());
            logger.error("CustomerProfileServiceImpl.class", "deviceRegistration()", "Please Try After Sometime" + e.getMessage());
        }
        return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(responseModel)));
    }

    @Override
    public String forgetPassword(String requestedObj) {
        ResponseModel responseModel = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestedObj);
            if (jsonObj != null) {
                RequestModel requestObj = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                Kyc kycObj = kycDao.getKycByMobileOrEmail(requestObj.getExtraVariable());

                if (kycObj != null) {
                    Wallet wallet = walletDao.getWalletByMobile(kycObj, walletTypeService.walletTypeByRole(kycObj.getRoleobj()));
                    if (wallet != null) {
                        String otp = RandomStringUtils.randomNumeric(6);
                        Otp otps = new Otp();
                        otps.setCreatedDate(new Date());
                        otps.setCreatedBy(kycObj.getRoleobj());
                        otps.setOtpGeneratedTime(new Date());
                        otps.setOtp(otp);
                        otps.setOtpServiceType("FORGETPASSWORD");
                        otps.setOtpTo(kycObj.getMobilenumber());
                        otps.setStatusObj(statusService.getStatusByName("NOTExpire"));
                        try {
                            String timestamp = new java.sql.Timestamp(System.currentTimeMillis()).toString();
                            Date ts = java.sql.Timestamp.valueOf(timestamp);
                            Calendar c = Calendar.getInstance();
                            c.add(Calendar.MINUTE, 5);
                            Date fifteenSecondsAgo = c.getTime();
                            otps.setOtpExpiryTime(fifteenSecondsAgo);
                            Otp obj = otpDao.save(otps);
                            if (obj != null) {
//                                mailAndMessageSender.sendSmsForOtp(kycObj, otp);
                                responseModel = mailAndMessageSender.sendMail(kycObj.getEmailid(), kycObj.getFullname(), obj.getOtp());
                                if (responseModel.getStatusCode() == 0) {

                                    responseModel.setStatusCode(0);
                                    responseModel.setMessage("Success");
                                    responseModel.setExtraVariable("FORGOTEPASSWORD");
                                    logger.info("CustomerProfileServiceImpl.class", "forgetPassword()", "Success");
                                } else {
                                    logger.error("CustomerProfileServiceImpl.class", "Failed TO Send Mail", "Failed");
                                }
                            }
                        } catch (Exception e) {
                            logger.error("CustomerProfileServiceImpl.class", "forgetPassword()", "Please Try After Sometime" + e.getMessage());
                        }
                    } else {
                        responseModel.setMessage("Complete Registration Once");
                        responseModel.setStatusCode(4);
                        logger.error("CustomerProfileServiceImpl.class", "forgetPassword()", "Complete Registration Once");
                    }
                } else {
                    responseModel.setMessage("Your Mobile Number or EmailId is invalid");
                    responseModel.setStatusCode(2);
                    logger.error("CustomerProfileServiceImpl.class", "forgetPassword()", "Your Mobile Number or EmailId is invalid");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseModel.setStatusCode(1);
            responseModel.setMessage("Plese try after Sometime " + e.getMessage());
            logger.error("CustomerProfileServiceImpl.class", "forgetPassword()", "Please Try After Sometime" + e.getMessage());
        }
        return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(responseModel)));
    }

    @Override
    public String setNewPassword(String requestedObj) {
        ResponseModel responseModel = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestedObj);
            if (jsonObj != null) {
                RequestModel requestModel = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                if (requestModel != null) {
                    Kyc kycObj = objectMapperUtility.jsonToObject(gson.toJson(requestModel.getReqObject()), Kyc.class);
                    Kyc kycObjdb = kycDao.getKycByMobile(kycObj);
                    if (kycObjdb != null) {
                        Wallet wallet = walletDao.getWalletByMobile(kycObjdb, walletTypeService.walletTypeByRole(kycObjdb.getRoleobj()));
                        if (wallet != null) {
                            String generatedSecuredPasswordHash = encryptionFile.generateStorngPasswordHash(kycObj.getPassword());
                            kycObjdb.setPassword(generatedSecuredPasswordHash);
                            kycObjdb.setRoleobj(rolesService.getRolesByName("Customer"));
                            Kyc obj = kycDao.save(kycObjdb);
                            if (obj != null) {
                                mailAndMessageSender.sendSmsForPassword(kycObjdb, kycObj.getPassword());
                                customerCopy(obj);
                            }
                        } else {
                            responseModel.setMessage("Complete Registration Once");
                            responseModel.setStatusCode(4);
                            logger.error("CustomerProfileServiceImpl.class", "setNewPassword()", "Complete Registration Once");
                        }
                    } else {
                        responseModel.setMessage("Mobile Number or EmailId Not Found");
                        responseModel.setStatusCode(2);
                        logger.error("CustomerProfileServiceImpl.class", "setNewPassword()", "Mobile Number or EmailId Not Found");
                    }
                }
            } else {
                responseModel.setStatusCode(3);
                responseModel.setMessage("Invalid Key");
                logger.error("CustomerProfileServiceImpl.class", "setNewPassword()", "Invalid Key");
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseModel.setStatusCode(1);
            responseModel.setMessage("Error while verifying OTP");
            logger.error("CustomerProfileServiceImpl.class", "setNewPassword()", "Please Try After Sometime" + e.getMessage());
        }
        return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(responseModel)));
    }

    public static boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."
                + "[a-zA-Z0-9_+&*-]+)*@"
                + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
                + "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null) {
            return false;
        }
        return pat.matcher(email).matches();
    }

    //here mobile needs validations
    @Override
    public String emailVerification(String autho_token, String requestedObj) {
        ResponseModel responseModel = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestedObj);
            if (jsonObj != null) {
                RequestModel requestObj = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
                String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
                if (decryptdId != null && decryptdId.equals(requestObj.getUserId())) {

                    Kyc kycObj = null;
                    if (isValid(requestObj.getExtraVariable())) {
                        System.out.print("Yes");
                        kycObj = kycDao.getKycByMobileOrEmail(requestObj.getExtraVariable());

                        if (kycObj != null) {
                            Wallet wallet = walletDao.getWalletByMobile(kycObj, walletTypeService.walletTypeByRole(kycObj.getRoleobj()));
                            if (wallet != null) {
                                String otp = RandomStringUtils.randomNumeric(6);
                                Otp otps = new Otp();
                                otps.setCreatedDate(new Date());
                                otps.setCreatedBy(kycObj.getRoleobj());
                                otps.setOtpGeneratedTime(new Date());
                                otps.setOtp(otp);
                                otps.setOtpServiceType("EMAILVERIFY");
                                otps.setOtpTo(kycObj.getMobilenumber());
                                otps.setStatusObj(statusService.getStatusByName("NOTExpire"));
                                try {
                                    String timestamp = new java.sql.Timestamp(System.currentTimeMillis()).toString();
                                    Date ts = java.sql.Timestamp.valueOf(timestamp);
                                    Calendar c = Calendar.getInstance();
                                    c.add(Calendar.MINUTE, 5);
                                    Date fifteenSecondsAgo = c.getTime();
                                    otps.setOtpExpiryTime(fifteenSecondsAgo);
                                    Otp obj = otpDao.save(otps);
                                    if (obj != null) {
//                                mailAndMessageSender.sendSmsForOtp(kycObj, otp);
                                        responseModel = mailAndMessageSender.sendMail(kycObj.getEmailid(), kycObj.getFullname(), obj.getOtp());
                                        if (responseModel.getStatusCode() == 0) {

                                            responseModel.setStatusCode(0);
                                            responseModel.setMessage("Success");
                                            responseModel.setExtraVariable("EMAILVERIFY");
                                            logger.info("CustomerProfileServiceImpl.class", "email verify()", "Success");
                                        } else {
                                            logger.error("CustomerProfileServiceImpl.class", "Failed TO Send Mail", "Failed");
                                        }
                                    }
                                } catch (Exception e) {
                                    logger.error("CustomerProfileServiceImpl.class", "email verify()", "Please Try After Sometime" + e.getMessage());
                                }
                            } else {
                                responseModel.setMessage("Complete Registration Once");
                                responseModel.setStatusCode(4);
                                logger.error("CustomerProfileServiceImpl.class", "email verify()", "Complete Registration Once");
                            }
                        } else {
                            responseModel.setMessage("Your Mobile Number or EmailId is invalid");
                            responseModel.setStatusCode(2);
                            logger.error("CustomerProfileServiceImpl.class", "email verify()", "Your Mobile Number or EmailId is invalid");
                        }
                    } else {
                        System.out.print("No");
                        responseModel.setMessage("Your EmailId is invalid");
                        responseModel.setStatusCode(2);
                        logger.error("CustomerProfileServiceImpl.class", "email verify()", "Your EmailId is invalid");
                    }
                } else {
                    responseModel.setMessage("invalid User Access");
                    responseModel.setStatusCode(2);
                    logger.error("CustomerProfileServiceImpl.class", "email verify()", "invalid user accessssss");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseModel.setStatusCode(1);
            responseModel.setMessage("Plese try after Sometime " + e.getMessage());
            logger.error("CustomerProfileServiceImpl.class", "email verify()", "Please Try After Sometime" + e.getMessage());
        }
        return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(responseModel)));
    }

    @Override
    public String changePassword(String autho_token, String requestModel) {
        ResponseModel responseModel = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestModel);
            if (jsonObj != null) {
                RequestModel requestObj = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
                String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
                if (decryptdId != null && decryptdId.equals(requestObj.getUserId())) {
                    Kyc obj = new Kyc();
                    obj.setMobilenumber(requestObj.getUserId());

                    Kyc kyc = kycDao.getKycByMobile(obj);
                    if (kyc != null) {
                        ChangePasswordRequest cpr = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), ChangePasswordRequest.class);
                        if (encryptionFile.validatePassword(cpr.getOldpassword(), kyc.getPassword())) {
                            String generatedSecuredPasswordHash = encryptionFile.generateStorngPasswordHash(cpr.getNewpassword());
                            kyc.setPassword(generatedSecuredPasswordHash);
                            kyc.setRoleobj(rolesService.getRolesByName("Customer"));
                            if (kycDao.save(kyc) != null) {
                                mailAndMessageSender.sendSmsForPassword(kyc, cpr.getNewpassword());
                                customerCopy(kyc);
                            }
                        } else {
                            responseModel.setStatusCode(3);
                            responseModel.setMessage("Please Give Correct Password");
                            logger.error("CustomerProfileServiceImpl.class", "loginRequest()", "Please Give Correct Password");
                        }
                    } else {
                        responseModel.setStatusCode(3);
                        responseModel.setMessage("No customer found with this mobile");
                        logger.error("CustomerProfileServiceImpl.class", "loginRequest()", "No customer found with this mobile");
                    }

                } else {
                    responseModel.setMessage("invalid User Access");
                    responseModel.setStatusCode(2);
                    logger.error("CustomerProfileServiceImpl.class", "email verify()", "invalid user accessssss");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseModel.setStatusCode(1);
            responseModel.setMessage("Plese try after Sometime " + e.getMessage());
            logger.error("CustomerProfileServiceImpl.class", "email verify()", "Please Try After Sometime" + e.getMessage());
        }
        return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(responseModel)));
    }

    @Override
    public String getKycBymobile(String requestedObj) {
        ResponseModel responseModel = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestedObj);
            if (jsonObj != null) {
                RequestModel requestObj = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
//                String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
//                String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
//                if (decryptdId != null && decryptdId.equals(requestObj.getUserId())) {
                Kyc kycObj = null;
                Kyc obj = null;
                kycObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), Kyc.class);
                obj = kycDao.getKycByMobileOrEmail(kycObj.getMobilenumber());
                if (obj != null) {
                    responseModel.setRespObject(obj);
                    responseModel.setStatusCode(0);
                    responseModel.setMessage("Success");
                } else {
                    responseModel.setStatusCode(1);
                    responseModel.setMessage("NO Customer Found ");
                }

//                } else {
//                    responseModel.setMessage("invalid User Access");
//                    responseModel.setStatusCode(2);
//                    logger.error("CustomerProfileServiceImpl.class", "email verify()", "invalid user accessssss");
//                }
            } else {
                responseModel.setMessage("Object Mapping Error");
                responseModel.setStatusCode(2);
                logger.error("CustomerProfileServiceImpl.class", "Object Mapping Error", "Object Mapping Error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseModel.setStatusCode(1);
            responseModel.setMessage("Plese try after Sometime " + e.getMessage());
            logger.error("CustomerProfileServiceImpl.class", "email verify()", "Please Try After Sometime" + e.getMessage());
        }
        return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(responseModel)));

    }

    @Override
    public String webRegistration(String requestedObj) {
        ResponseModel responseModel = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestedObj);
            if (jsonObj != null) {
                RequestModel requestModel = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                if (requestModel != null) {
                    Kyc kycObj = objectMapperUtility.jsonToObject(gson.toJson(requestModel.getReqObject()), Kyc.class);
                    kycObj.setWrongattempt(0);
                    kycObj.setRoleobj(rolesService.getRolesByName("Customer"));
                    kycObj.setStatusObj(statusService.getStatusByName("InActive"));
                    kycObj = kycDao.save(kycObj);
                    if (kycObj != null && kycObj.getIdkyc() != null) {
                        //here wallet is created
                        Wallet walletObj = new Wallet();
                        walletObj.setKycObj(kycObj);
                        walletObj.setCreatedBy(kycObj.getRoleobj());
                        Status st = statusService.getStatusByName("InActive");
                        walletObj.setStatusObj(st);
                        responseModel = walletService.addWallet(walletObj);
                        if (responseModel.getStatusCode() == 0) {
                            mailAndMessageSender.sendSmsForPassword(kycObj, kycObj.getPassword());
                        }
                    } else {
                        responseModel.setStatusCode(1);
                        responseModel.setMessage("Registration Failed");
                        logger.error("webRegistrationServiceImpl.class", "webRegistration()", "Web Registration Failed");
                    }
                }
            } else {
                responseModel.setStatusCode(3);
                responseModel.setMessage("Invalid Key");
                logger.error("webRegistrationServiceImpl.class", "webRegistration()", "Invalid Key");
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseModel.setStatusCode(1);
            responseModel.setMessage("Error while saving password");
            logger.error("webRegistrationServiceImpl.class", "webRegistration()", "Please Try After Sometime");
        }
        return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(responseModel)));
    }

    public ResponseModel customerCopy(Kyc obj) {
        Customer customer = new Customer();
        customer.setFullname(obj.getFullname());
        customer.setPhoneNumber(obj.getMobilenumber());
        customer.setEmail(obj.getEmailid());
        customer.setGender(obj.getGender());
        customer.setPassword(obj.getPassword());
        customer.setStatus(Boolean.TRUE);
        customer.setStreet(obj.getPermanentaddress());
        RequestModel request = new RequestModel();
        request.setReqObject(customer);
        return VGWappwsApi.updateCustomer(request);
    }

}
