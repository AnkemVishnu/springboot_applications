package com.bizmobia.vgwallet.mobile.serviceImpl;

import com.bizmobia.vgwallet.mobile.dao.DevicesDao;
import com.bizmobia.vgwallet.mobile.dao.KycDao;
import com.bizmobia.vgwallet.mobile.dao.RequestMoneyDao;
import com.bizmobia.vgwallet.mobile.dao.WalletDao;
import com.bizmobia.vgwallet.mobile.service.EncryptionFile;
import com.bizmobia.vgwallet.mobile.service.ObjectMapperUtility;
import com.bizmobia.vgwallet.mobile.service.StatusService;
import com.bizmobia.vgwallet.mobile.service.WalletService;
import com.bizmobia.vgwallet.mobile.service.WalletTypeService;
import com.bizmobia.vgwallet.mobile.utilities.AndroidPushNotification;
import com.bizmobia.vgwallet.mobile.utilities.IosPushNotification;
import com.bizmobia.vgwallet.models.*;
import com.bizmobia.vgwallet.response.*;
import com.bizmobia.vgwallet.request.*;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bizmobia.vgwallet.mobile.service.MailAndMessageSender;
import com.bizmobia.vgwallet.mobile.utilities.SmsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author bizmobia13
 */
@Service
@Transactional
public class WalletServiceImpl implements WalletService {

    private static final Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);

    @Autowired
    private WalletDao walletDao;

    @Autowired
    private KycDao kycDao;

    @Autowired
    private RequestMoneyDao requestMoneyDao;

    @Autowired
    private DevicesDao deviceDao;

    @Autowired
    private WalletTypeService walletTypeService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private EncryptionFile encryptionFile;

    @Autowired
    private MailAndMessageSender mailAndMessageSender;

    @Autowired
    private ObjectMapperUtility objectMapperUtility;

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    @Override
    public ResponseModel addWallet(Wallet walletObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            walletObj.setCreatedDate(new Date());
            //here create wallet id
            walletObj.setAccountId("VGW" + String.format("%010d", walletObj.getKycObj().getIdkyc()));
            //creates encrypted keys by using mobile
            String genkeys = encryptionFile.getencdsckey(walletObj.getKycObj().getMobilenumber());
            Double walletbal = 0.0;
            String encrykey = encryptionFile.encrypt(genkeys, encryptionFile.encGenKey, walletbal.toString());
            walletObj.setWalletBalance(encrykey);
            WalletType walletType = walletTypeService.getWalletTypeByName("Classics");//here need to do dynamic when proper user comming (userwise)
            if (walletType != null) {
                walletObj.setWalletTypeObj(walletType);
                Wallet w = walletDao.checkDuplicatesWallets(walletObj);
                if (w == null) {
                    walletObj.setIsVendorWallet(Boolean.FALSE);
                    Wallet obj = walletDao.save(walletObj);
                    if (obj != null) {
                        statuResponse.setStatusCode(0);
                    } else {
                        statuResponse.setStatusCode(1);
                    }
                    statuResponse = mailAndMessageSender.sendSmsAndMailForWallet(walletObj.getKycObj(), walletObj.getAccountId());
                } else {
                    statuResponse = mailAndMessageSender.sendSmsAndMailForWallet(w.getKycObj(), w.getAccountId());
                }
            } else {
                statuResponse.setStatusCode(1);
                statuResponse.setMessage("Error WalletType Fetch (addWallet)");
                logger.error("WalletServiceImpl.class", "addWallet()", "Error WalletType Fetch (addWallet)");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            statuResponse.setStatusCode(1);
            logger.error("WalletServiceImpl.class", "addWallet()", "Please Try After Sometime" + e.getMessage());
        }
        return statuResponse;
    }

    @Override
    public ResponseModel updateWallet(String autho_token, Wallet walletsObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(walletsObj.getKycObj().getMobilenumber(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, (walletsObj.getKycObj().getMobilenumber().length()));
            if (decryptdId.equals(walletsObj.getKycObj().getMobilenumber())) {
                Wallet myWallets = walletDao.getWalletByMobile(walletsObj.getKycObj(), walletsObj.getWalletTypeObj());
                myWallets.setStatusObj(walletsObj.getStatusObj());
                Wallet obj = walletDao.save(myWallets);
                if (obj != null) {
                    statuResponse.setStatusCode(0);
                } else {
                    statuResponse.setStatusCode(1);
                }
            } else {
                statuResponse.setMessage("Invalid User Access");
                statuResponse.setStatusCode(2);
                logger.error("WalletServiceImpl.class", "updateWallet()", "Error WalletType Fetch (addWallet)");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            statuResponse.setStatusCode(1);
            logger.error("WalletServiceImpl.class", "updateWallet()", "Please Try After Sometime" + e.getMessage());
        }
        return statuResponse;
    }

    @Override
    public String contactSync(String autho_token, String requestModel) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestModel);
            if (jsonObj != null) {
                RequestModel requestObj = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
                String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
                if (decryptdId.equals(requestObj.getUserId())) {
                    List<String> list = objectMapperUtility.jsonArrayToObjectList(requestObj.getReqList(), String.class);
                    Kyc obj;
                    Kyc kyc;
                    List<Kyc> resplist = new ArrayList<>();
                    for (String mobile : list) {
                        if (!requestObj.getUserId().equals(mobile)) {
                            obj = kycDao.getCustByMobile(mobile);
                            if (obj != null) {
                                kyc = new Kyc();
                                kyc.setMobilenumber(obj.getMobilenumber());
                                kyc.setFullname(obj.getFullname());
                                resplist.add(kyc);
                            }
                        }
                    }
                    statuResponse.setRespList(resplist);
                    statuResponse.setMessage("Contacts To Be Synced");
                    statuResponse.setStatusCode(0);
                    logger.info("WalletServiceImpl.class", "contactSync()", "Contacts To Be Synced");
                } else {
                    statuResponse.setMessage("Invalid User Access");
                    statuResponse.setStatusCode(2);
                    logger.error("WalletServiceImpl.class", "contactSync()", "Invalid User Access");
                }
            } else {
                statuResponse.setStatusCode(3);
                statuResponse.setMessage("Invalid Key");
                logger.error("WalletServiceImpl.class", "contactSync()", "Invalid Key");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            statuResponse.setStatusCode(1);
            logger.error("WalletServiceImpl.class", "contactSync()", "Please Try After Sometime" + e.getMessage());
        } finally {
            return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(statuResponse)));
        }
    }

    @Override
    public String checkBalance(String autho_token, String requestModel) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestModel);
            if (jsonObj != null) {
                RequestModel requestObj = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
                String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
                if (decryptdId.equals(requestObj.getUserId())) {
                    Kyc kycObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), Kyc.class);
                    String genkeys = encryptionFile.getencdsckey(kycObj.getMobilenumber());
                    String str = walletDao.getBalanceByMobile(kycObj.getMobilenumber());
                    if (str != null) {
                        String bal = encryptionFile.decryptObject(genkeys, encryptionFile.encGenKey, str);
                        statuResponse.setStatusCode(0);
                        statuResponse.setMessage("Get Balance Successful");
                        statuResponse.setExtraVariable(bal);
                        logger.info("WalletServiceImpl.class", "checkBalance()", "Get Balance Successful");
                    } else {
                        statuResponse.setStatusCode(1);
                        statuResponse.setMessage("No Customer Found");
                        logger.error("WalletServiceImpl.class", "checkBalance()", "No Customer Found");
                    }
                } else {
                    statuResponse.setMessage("Invalid User Access");
                    statuResponse.setStatusCode(2);
                    logger.error("WalletServiceImpl.class", "checkBalance()", "Invalid User Access");
                }
            } else {
                statuResponse.setStatusCode(3);
                statuResponse.setMessage("Invalid Key");
                logger.error("WalletServiceImpl.class", "checkBalance()", "Invalid Key");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            statuResponse.setStatusCode(1);
            logger.error("WalletServiceImpl.class", "checkBalance()", "Please Try After Sometime" + e.getMessage());
        } finally {
            return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(statuResponse)));
        }
    }

    @Override
    public String requestMoney(String autho_token, String requestModel) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestModel);
            if (jsonObj != null) {
                RequestModel requestObj = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
                String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
                if (decryptdId.equals(requestObj.getUserId())) {
                    TransactionRequest transaction = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), TransactionRequest.class);

                    Kyc kf = kycDao.getCustByMobile(transaction.getFromNumber());
                    Kyc kt = kycDao.getCustByMobile(transaction.getToNumber());

                    Wallet fromWallet = walletDao.getWalletByMobile(kf, walletTypeService.walletTypeByRole(kf.getRoleobj()));
                    Wallet toWallet = walletDao.getWalletByMobile(kt, walletTypeService.walletTypeByRole(kt.getRoleobj()));

                    Kyc k;

                    if (toWallet != null) {
                        RequestMoney requestMoney = new RequestMoney();
                        requestMoney.setToWallet(toWallet);
                        requestMoney.setFromWallet(fromWallet);
                        requestMoney.setAmount(transaction.getAmount());
                        requestMoney.setMessage(transaction.getMessage());
                        requestMoney.setStatus(statusService.getStatusByName("Requested"));
                        requestMoney = requestMoneyDao.save(requestMoney);

                        if (requestMoney != null) {
                            Devices device = deviceDao.getDeviceByMobileNo(toWallet.getKycObj());
                            String notificationtype = "request";
                            String title = "Request";
                            String description = fromWallet.getKycObj().getFullname() + "(" + fromWallet.getKycObj().getMobilenumber() + ") Has Requested " + transaction.getAmount();
                            SmsSender.sendSms(transaction.getToNumber(), description);
                            k = new Kyc();
                            k.setMobilenumber(fromWallet.getKycObj().getMobilenumber());
                            k.setFullname(fromWallet.getKycObj().getFullname());
                            if (device != null) {
                                if (device.getDeviceMaker().equals("IOS")) {
                                    IosPushNotification.sendNotificationToIOS(device.getDeviceToken(), title, description, notificationtype, k);
                                } else {
                                    AndroidPushNotification.sendNotificationToAndroid(title, description, device.getDeviceToken(), notificationtype, k);
                                }
                            }
                            statuResponse.setMessage(toWallet.getKycObj().getFullname() + " is Notified Successfully");
                            statuResponse.setStatusCode(0);
                            logger.info("WalletServiceImpl.class", "requestMoney()", toWallet.getKycObj().getFullname() + " is Notified Successfully");
                        } else {
                            statuResponse.setMessage(toWallet.getKycObj().getFullname() + " Could'nt be Notified Successfully");
                            statuResponse.setStatusCode(1);
                            logger.error("WalletServiceImpl.class", "requestMoney()", toWallet.getKycObj().getFullname() + " Could'nt be Notified Successfully");
                        }
                    } else {
                        statuResponse.setMessage("No User Exists With No. " + transaction.getToNumber());
                        statuResponse.setStatusCode(1);
                        logger.error("WalletServiceImpl.class", "requestMoney()", "No User Exists With No. " + transaction.getToNumber());
                    }
                } else {
                    statuResponse.setMessage("Invalid User Access");
                    statuResponse.setStatusCode(2);
                    logger.error("WalletServiceImpl.class", "requestMoney()", "Invalid User Access");
                }
            } else {
                statuResponse.setStatusCode(3);
                statuResponse.setMessage("Invalid Key");
                logger.error("WalletServiceImpl.class", "requestMoney()", "Invalid Key");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            statuResponse.setStatusCode(1);
            logger.error("WalletServiceImpl.class", "requestMoney()", "Please Try After Sometime" + e.getMessage());
        } finally {
            return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(statuResponse)));
        }
    }

    @Override
    public String getAllRequestMoney(String autho_token, String requestModel) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestModel);
            if (jsonObj != null) {
                RequestModel requestObj = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
                String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
                if (decryptdId.equals(requestObj.getUserId())) {
                    Kyc k = kycDao.getCustByMobile(requestObj.getUserId());

                    Wallet wallet = walletDao.getWalletByMobile(k, walletTypeService.walletTypeByRole(k.getRoleobj()));
                    if (wallet != null) {
                        List<RequestMoney> list = requestMoneyDao.getAllRequestedMoney(wallet);
                        if (!list.isEmpty()) {
                            List<RequestMoneyResponse> resp = new ArrayList<>();
                            Kyc fromKyc;
                            Kyc toKyc;

                            for (RequestMoney obj : list) {
                                fromKyc = new Kyc();
                                fromKyc.setMobilenumber(obj.getFromWallet().getKycObj().getMobilenumber());
                                fromKyc.setFullname(obj.getFromWallet().getKycObj().getFullname());

                                toKyc = new Kyc();
                                toKyc.setMobilenumber(obj.getToWallet().getKycObj().getMobilenumber());
                                toKyc.setFullname(obj.getToWallet().getKycObj().getFullname());

                                if (fromKyc.getMobilenumber().equals(requestObj.getUserId())) {
                                    resp.add(new RequestMoneyResponse(fromKyc, toKyc, obj.getAmount(), obj.getMessage(), "Self", obj.getRequestMoneyId()));
                                } else {
                                    resp.add(new RequestMoneyResponse(fromKyc, toKyc, obj.getAmount(), obj.getMessage(), "Other", obj.getRequestMoneyId()));
                                }
                            }
                            statuResponse.setRespList(resp);
                            statuResponse.setMessage("Requests Found");
                            statuResponse.setStatusCode(0);
                            logger.info("WalletServiceImpl.class", "getAllRequestMoney()", "Requests Found");
                        } else {
                            statuResponse.setMessage("No Requests");
                            statuResponse.setStatusCode(1);
                            logger.error("WalletServiceImpl.class", "getAllRequestMoney()", "No Requests");
                        }
                    } else {
                        statuResponse.setMessage("No User Exists With No. " + requestObj.getUserId());
                        statuResponse.setStatusCode(1);
                        logger.error("WalletServiceImpl.class", "getAllRequestMoney()", "No User Exists With No. " + requestObj.getUserId());
                    }
                } else {
                    statuResponse.setMessage("Invalid User Access");
                    statuResponse.setStatusCode(2);
                    logger.error("WalletServiceImpl.class", "getAllRequestMoney()", "Invalid User Access");
                }
            } else {
                statuResponse.setStatusCode(3);
                statuResponse.setMessage("Invalid Key");
                logger.error("WalletServiceImpl.class", "getAllRequestMoney()", "Invalid Key");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            statuResponse.setStatusCode(1);
            logger.error("WalletServiceImpl.class", "getAllRequestMoney()", "Please Try After Sometime" + e.getMessage());
        } finally {
            return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(statuResponse)));
        }
    }

    @Override
    public String addMoneyWallet(String autho_token, String requestModel) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestModel);
            if (jsonObj != null) {
                RequestModel requestObj = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
                String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
                if (decryptdId.equals(requestObj.getUserId())) {
                    Kyc kycObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), Kyc.class);
                    String genkeys = encryptionFile.getencdsckey(kycObj.getMobilenumber());
                    String bal = encryptionFile.decryptObject(genkeys, encryptionFile.encGenKey, walletDao.getBalanceByMobile(kycObj.getMobilenumber()));
                    if (Double.parseDouble(requestObj.getExtraVariable()) > 0) {
                        Double amt = Double.parseDouble(bal) + Double.parseDouble(requestObj.getExtraVariable());
                        bal = encryptionFile.encrypt(genkeys, encryptionFile.encGenKey, amt.toString());

                        Wallet wallet = walletDao.getWalletByMobile(kycObj, walletTypeService.getWalletTypeByName("Classics"));
                        if (wallet != null) {
                            wallet.setWalletBalance(bal);
                            walletDao.save(wallet);
                        }
                    } else {
                        statuResponse.setMessage("Invalid Amount");
                        statuResponse.setStatusCode(1);
                        logger.error("WalletServiceImpl.class", "addMoneyWallet()", "Invalid Amount");
                    }
                } else {
                    statuResponse.setMessage("Invalid User Access");
                    statuResponse.setStatusCode(2);
                    logger.error("WalletServiceImpl.class", "addMoneyWallet()", "Invalid User Access");
                }
            } else {
                statuResponse.setStatusCode(3);
                statuResponse.setMessage("Invalid Key");
                logger.error("WalletServiceImpl.class", "addMoneyWallet()", "Invalid Key");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            statuResponse.setStatusCode(1);
            logger.error("WalletServiceImpl.class", "addMoneyWallet()", "Please Try After Sometime" + e.getMessage());
        } finally {
            return encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(statuResponse));
        }
    }

}
