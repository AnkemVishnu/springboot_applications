package com.bizmobia.vgwallet.mobile.serviceImpl;

import com.bizmobia.vgwallet.mobile.dao.DevicesDao;
import com.bizmobia.vgwallet.mobile.dao.KycDao;
import com.bizmobia.vgwallet.mobile.dao.RequestMoneyDao;
import com.bizmobia.vgwallet.mobile.dao.TransactionHistoryDao;
import com.bizmobia.vgwallet.mobile.dao.VGWalletDao;
import com.bizmobia.vgwallet.mobile.dao.WalletDao;
import com.bizmobia.vgwallet.mobile.exception.ResourceNotFoundException;
import com.bizmobia.vgwallet.mobile.service.EncryptionFile;
import com.bizmobia.vgwallet.mobile.service.ObjectMapperUtility;
import com.bizmobia.vgwallet.mobile.service.StatusService;
import com.bizmobia.vgwallet.mobile.service.TransactionTypeService;
import com.bizmobia.vgwallet.mobile.service.VGWalletService;
import com.bizmobia.vgwallet.mobile.service.WalletToWalletService;
import com.bizmobia.vgwallet.mobile.service.WalletTypeService;
import com.bizmobia.vgwallet.models.Kyc;
import com.bizmobia.vgwallet.models.RequestMoney;
import com.bizmobia.vgwallet.models.TransactionHistory;
import com.bizmobia.vgwallet.models.Wallet;
import com.bizmobia.vgwallet.models.WalletType;
import com.bizmobia.vgwallet.request.RequestModel;
import com.bizmobia.vgwallet.response.ResponseModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Vaibhav
 */
@Service
@Transactional
public class WalletToWalletServiceImpl implements WalletToWalletService {

    private static final Logger logger = LoggerFactory.getLogger(WalletToWalletServiceImpl.class);

    @Autowired
    private ObjectMapperUtility objectMapperUtility;

    @Autowired
    private EncryptionFile encryptionFile;

    @Autowired
    private WalletDao walletDao;

    @Autowired
    private RequestMoneyDao requestMoneyDao;

    @Autowired
    private DevicesDao deviceDao;

    @Autowired
    private TransactionHistoryDao transactionHistoryDao;

    @Autowired
    private StatusService statusService;

    @Autowired
    private TransactionTypeService transactionTypeService;

    @Autowired
    private WalletTypeService walletTypeService;

    @Autowired
    private VGWalletService vGWalletService;

    @Autowired
    private KycDao kycDao;

    @Autowired
    private VGWalletDao vGWalletDao;

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private final Double vatAmt = 16.0;

    @Override
    public String verifyPerson(String autho_token, String requestedObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestedObj);
            if (jsonObj != null) {
                RequestModel requestObj = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
                String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
                if (decryptdId.equals(requestObj.getUserId())) {
                    Kyc kycObj = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), Kyc.class);
                    Kyc obj = walletDao.verify(kycObj);
                    if (obj != null) {
                        Kyc k = new Kyc();
                        k.setFullname(obj.getFullname());
                        k.setMobilenumber(obj.getMobilenumber());
                        k.setEmailid(obj.getEmailid());
                        statuResponse.setRespObject(k);
                        statuResponse.setStatusCode(0);
                        statuResponse.setMessage("Wallet Verified");
                    } else {
                        statuResponse.setStatusCode(1);
                        statuResponse.setMessage("No Wallet Found");
                    }
                } else {
                    statuResponse.setMessage("Invalid User Access");
                    statuResponse.setStatusCode(2);
                    logger.error("WalletToWalletServiceImpl.class", "verifyPerson()", "Invalid User Access");
                }
            } else {
                statuResponse.setStatusCode(3);
                statuResponse.setMessage("Invalid Key");
                logger.error("WalletToWalletServiceImpl.class", "verifyPerson()", "Invalid Key");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            statuResponse.setStatusCode(1);
            logger.error("WalletToWalletServiceImpl.class", "updateVGWallet()", "Please Try After Sometime" + e.getMessage());
        } finally {
            return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(statuResponse)));
        }
    }

    public String TransactionId() {
        Calendar cald = Calendar.getInstance();
        cald.setTime(new Date());
        cald.getTimeInMillis();
        String str = String.valueOf(cald.getTimeInMillis()).substring(3);
        return str + RandomStringUtils.randomNumeric(5);
    }

    //set transaction history object amont and to customer object
    @Override
    public String walletToWallet(String autho_token, String requestedObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestedObj);
            if (jsonObj != null) {
                RequestModel requestObj = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                if (requestObj != null) {
                    String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
                    String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
                    if (decryptdId != null && decryptdId.equals(requestObj.getUserId())) {

                        TransactionHistory transactions = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), TransactionHistory.class);
                        Kyc kycObj = new Kyc();
                        kycObj.setMobilenumber(requestObj.getUserId());
                        kycDao.getCustByMobile(requestObj.getUserId());
                        WalletType walletType = walletTypeService.getWalletTypeByName("Classics");
                        Wallet fromwallet = walletDao.getWalletByMobile(kycObj, walletType);
                        Wallet toWallet = walletDao.getWalletByMobile(transactions.getToWallet().getKycObj(), walletType);

                        if (fromwallet != null && toWallet != null) {
                            Boolean limitamt = false;
                            if (fromwallet.getKycObj().getIsfullkyc()) {
                                limitamt = true;
                            } else {
                                if (transactions.getTransactionamount() <= 100) {
                                    limitamt = true;
                                } else {
                                    limitamt = false;
                                }
                            }
                            if (limitamt) {
                                String frmGenkeys = encryptionFile.getencdsckey(fromwallet.getKycObj().getMobilenumber());
                                String frombalstr = encryptionFile.decryptObject(frmGenkeys, encryptionFile.encGenKey, fromwallet.getWalletBalance());

                                if ((frombalstr != null || !frombalstr.equals("")) && (transactions.getTransactionamount() != null && transactions.getTransactionamount() >= 0)) {
                                    Double fromBal = Double.parseDouble(frombalstr);
                                    transactions.setTransactionbalancebefore(fromBal);//--->this line for insted create variable i use already requested object

                                    if (fromBal >= transactions.getTransactionamount()) {
                                        String toGenkeys = encryptionFile.getencdsckey(toWallet.getKycObj().getMobilenumber());
                                        String tobalstr = encryptionFile.decryptObject(toGenkeys, encryptionFile.encGenKey, toWallet.getWalletBalance());

                                        if (tobalstr != null || !tobalstr.equals("")) {
                                            Double toBal = Double.parseDouble(tobalstr);

                                            if (toBal >= 0) {
                                                TransactionHistory trHistory = null;
                                                Double fee = vGWalletService.applicableCharges(transactions.getTransactionamount());
                                                Double vat = (fee * vatAmt) / 100;

                                                if (fromBal > (transactions.getTransactionamount() + fee + vat)) {

                                                    fromBal = fromBal - (transactions.getTransactionamount() + fee + vat);

                                                    Wallet newWallet = new Wallet();
                                                    newWallet.setKycObj(fromwallet.getKycObj());
                                                    newWallet.setWalletBalance(vat.toString());
                                                    newWallet.setWalletTypeObj(walletTypeService.getWalletTypeByName("VGWBankVat"));
                                                    newWallet.setAccountId("VGWB");//check starting letter required
                                                    Wallet bkwallet = vGWalletDao.getWalletsPartially(newWallet);

                                                    if (bkwallet != null) {
                                                        newWallet.setWalletTypeObj(bkwallet.getWalletTypeObj());
                                                        newWallet.setAccountId(bkwallet.getAccountId());
                                                        statuResponse = vGWalletService.updateVGWallet(newWallet);//update into bankvat acc

                                                        if (statuResponse.getStatusCode() == 0) {
                                                            newWallet = new Wallet();
                                                            newWallet.setKycObj(fromwallet.getKycObj());
                                                            newWallet.setWalletBalance(fee.toString());
                                                            newWallet.setWalletTypeObj(walletTypeService.getWalletTypeByName("VGWINCOME"));
                                                            newWallet.setAccountId("VGWI");//check starting letter required
                                                            Wallet incomewallet = vGWalletDao.getWalletsPartially(newWallet);
                                                            newWallet.setWalletTypeObj(incomewallet.getWalletTypeObj());
                                                            newWallet.setAccountId(incomewallet.getAccountId());
                                                            statuResponse = vGWalletService.updateVGWallet(newWallet);//update into VGWMain acc
                                                            if (statuResponse.getStatusCode() == 0) {
                                                                transactions.setTransactionbalanceafter(fromBal);
                                                                toBal = toBal + transactions.getTransactionamount();
                                                                fromwallet.setWalletBalance(encryptionFile.encrypt(frmGenkeys, encryptionFile.encGenKey, fromBal.toString()));
                                                                toWallet.setWalletBalance(encryptionFile.encrypt(toGenkeys, encryptionFile.encGenKey, toBal.toString()));
                                                                Wallet wf = walletDao.save(fromwallet);
                                                                if (wf != null) {
                                                                    Wallet wt = walletDao.save(toWallet);
                                                                    if (wt != null) {
                                                                        trHistory = new TransactionHistory();
                                                                        trHistory.setTransactionDate(new Date());
                                                                        trHistory.setTransactionId(TransactionId());
                                                                        trHistory.setDevice(deviceDao.getDeviceByMobileNo(fromwallet.getKycObj()));
                                                                        trHistory.setFromWallet(fromwallet);
                                                                        trHistory.setToWallet(toWallet);
                                                                        trHistory.setTransactionOperation(transactionTypeService.getTransactionTypeByName("VGW-VGW"));
                                                                        trHistory.setTransactionamount(transactions.getTransactionamount());
                                                                        trHistory.setTransactionbalancebefore(Double.parseDouble(new DecimalFormat(".##").format(transactions.getTransactionbalancebefore())));
                                                                        trHistory.setTransactionbalanceafter(Double.parseDouble(new DecimalFormat(".##").format(transactions.getTransactionbalanceafter())));
                                                                        trHistory.setApplicableCharges(fee + vat);
                                                                        trHistory.setTransationstatus(statusService.getStatusByName("Success"));
                                                                        trHistory.setMessage(transactions.getMessage());
                                                                        TransactionHistory th = transactionHistoryDao.save(trHistory);
                                                                        if (th != null) {
                                                                            statuResponse.setStatusCode(0);
                                                                            statuResponse.setMessage("Transaction Successfull");
                                                                            if (requestObj.getExtraVariable() != null) {
                                                                                RequestMoney rm = requestMoneyDao.getOne(Long.parseLong(requestObj.getExtraVariable()));
                                                                                if (rm != null) {
                                                                                    requestMoneyDao.delete(rm);
                                                                                    if (ResponseEntity.ok().build().getStatusCodeValue() == 200) {
                                                                                        statuResponse.setStatusCode(0);
                                                                                    } else {
                                                                                        statuResponse.setStatusCode(1);
                                                                                    }
                                                                                }
                                                                            }
                                                                        } else {
                                                                            statuResponse.setStatusCode(1);
                                                                        }
                                                                    } else {
                                                                        trHistory = new TransactionHistory();
                                                                        fromwallet.setWalletBalance(encryptionFile.encrypt(frmGenkeys, encryptionFile.encGenKey, frombalstr));
                                                                        walletDao.save(fromwallet);
                                                                        trHistory.setTransactionDate(new Date());
                                                                        trHistory.setTransationstatus(statusService.getStatusByName("Failed"));
                                                                        trHistory.setTransactionId(TransactionId());
                                                                        trHistory.setDevice(deviceDao.getDeviceByMobileNo(fromwallet.getKycObj()));
                                                                        trHistory.setFromWallet(fromwallet);
                                                                        trHistory.setToWallet(toWallet);
                                                                        trHistory.setTransactionOperation(transactionTypeService.getTransactionTypeByName("VGW-VGW"));
                                                                        trHistory.setTransactionamount(transactions.getTransactionamount());
                                                                        trHistory.setMessage(transactions.getMessage());
//                                                trHistory.setTransactionbalancebefore(transactions.getTransactionbalancebefore());
//                                                trHistory.setTransactionbalanceafter(transactions.getTransactionbalanceafter());
                                                                        TransactionHistory th = transactionHistoryDao.save(trHistory);
                                                                        if (th != null) {
                                                                            if (requestObj.getExtraVariable() != null) {
                                                                                RequestMoney rm = requestMoneyDao.getOne(Long.parseLong(requestObj.getExtraVariable()));
                                                                                if (rm != null) {
                                                                                    requestMoneyDao.delete(rm);
                                                                                    if (ResponseEntity.ok().build().getStatusCodeValue() == 200) {
                                                                                        statuResponse.setStatusCode(0);
                                                                                    } else {
                                                                                        statuResponse.setStatusCode(1);
                                                                                    }
                                                                                }
                                                                            }
                                                                        } else {
                                                                            statuResponse.setStatusCode(1);
                                                                        }
                                                                    }
                                                                } else {
                                                                    statuResponse.setStatusCode(1);
                                                                    statuResponse.setMessage("Please Try After Sometime");
                                                                    logger.error("WalletToWalletServiceImpl.class", "walletToWallet()", "Please Try After Sometime");
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    statuResponse.setStatusCode(4);
                                                    statuResponse.setMessage("Insuficient Fund To Transfer");
                                                    logger.error("WalletToWalletServiceImpl.class", "walletToWallet()", "Insuficient Fund To Transfer");
                                                }
                                            } else {
                                                statuResponse.setStatusCode(4);
                                                statuResponse.setMessage("Please Enter Vaild Amount");
                                                logger.error("WalletToWalletServiceImpl.class", "walletToWallet()", "Please Enter Vaild Amount");
                                            }
                                        }
                                    } else {
                                        statuResponse.setStatusCode(4);
                                        statuResponse.setMessage("Insuficient Fund To Transfer");
                                        logger.error("WalletToWalletServiceImpl.class", "walletToWallet()", "Insuficient Fund To Transfer");
                                    }
                                }
                            } else {
                                statuResponse.setMessage("You can transfer up to $100 to other VGW Account,FullKyc Not Done");
                                statuResponse.setStatusCode(2);
                                logger.error("WalletToWalletServiceImpl.class", "walletToWallet()", "You can transfer up to $100 to other VGW Account,FullKyc Not Done");
                                throw new ResourceNotFoundException("IshalfKyc", "False", "FromKyc");
                            }
                        }
                    } else {
                        statuResponse.setMessage("Invalid User Access");
                        statuResponse.setStatusCode(2);
                        logger.error("WalletToWalletServiceImpl.class", "walletToWallet()", "Invalid User Access");
                    }
                } else {
                    statuResponse.setMessage("Invalid Input");
                    statuResponse.setStatusCode(2);
                    logger.error("WalletToWalletServiceImpl.class", "walletToWallet()", "Invalid Input");
                }
            } else {
                statuResponse.setStatusCode(3);
                statuResponse.setMessage("Invalid Key");
                logger.error("WalletToWalletServiceImpl.class", "walletToWallet()", "Invalid Key");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            statuResponse.setStatusCode(1);
            logger.error("WalletToWalletServiceImpl.class", "updateVGWallet()", "Please Try After Sometime" + e.getMessage());
        } finally {
            return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(statuResponse)));
        }
    }

    @Override
    public String cashoutAtAgentPoint(String autho_token, String requestedObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestedObj);
            if (jsonObj != null) {
                RequestModel requestObj = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                if (requestObj != null) {
                    String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
                    String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
                    if (decryptdId != null && decryptdId.equals(requestObj.getUserId())) {
                        TransactionHistory transactions = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), TransactionHistory.class);
                        Kyc kycObj = new Kyc();
                        kycObj.setMobilenumber(requestObj.getUserId());
                        Wallet fromCustwallet = walletDao.getWalletByMobile(kycObj, walletTypeService.getWalletTypeByName("Classics"));
                        WalletType walletType = walletTypeService.getWalletTypeByName("Agent");
                        Wallet toAgentWallet = null;
                        if (walletType != null) {
                            toAgentWallet = walletDao.getAgentWalletByMobile(transactions.getToWallet().getKycObj(), walletType);
                            if (fromCustwallet != null && toAgentWallet != null) {
                                Boolean limitamt;
                                if (fromCustwallet.getKycObj().getIsfullkyc()) {
                                    limitamt = true;
                                } else {
                                    limitamt = transactions.getTransactionamount() <= 100;
                                }
                                if (limitamt) {
                                    String frmGenkeys = encryptionFile.getencdsckey(fromCustwallet.getKycObj().getMobilenumber());
                                    String frombalstr = encryptionFile.decryptObject(frmGenkeys, encryptionFile.encGenKey, fromCustwallet.getWalletBalance());

                                    if ((frombalstr != null || !frombalstr.equals("")) && (transactions.getTransactionamount() != null && transactions.getTransactionamount() >= 0)) {
                                        Double fromBal = Double.parseDouble(frombalstr);
                                        transactions.setTransactionbalancebefore(fromBal);//--->this line for insted create variable i use already requested object

                                        if (fromBal >= transactions.getTransactionamount()) {
                                            String toGenkeys = encryptionFile.getencdsckey(toAgentWallet.getKycObj().getMobilenumber());
                                            String tobalstr = encryptionFile.decryptObject(toGenkeys, encryptionFile.encGenKey, toAgentWallet.getWalletBalance());

                                            if (tobalstr != null || !tobalstr.equals("")) {
                                                Double toBal = Double.parseDouble(tobalstr);

                                                if (toBal >= 0) {
                                                    TransactionHistory trHistory = null;
                                                    Double fee = vGWalletService.applicableCharges(transactions.getTransactionamount());
                                                    Double vat = (fee * vatAmt) / 100;

                                                    if (fromBal > (transactions.getTransactionamount() + fee + vat)) {

                                                        fromBal = fromBal - (transactions.getTransactionamount() + fee + vat);

                                                        Wallet newWallet = new Wallet();
                                                        newWallet.setKycObj(fromCustwallet.getKycObj());
                                                        newWallet.setWalletBalance(vat.toString());
                                                        newWallet.setWalletTypeObj(walletTypeService.getWalletTypeByName("VGWBankVat"));
                                                        newWallet.setAccountId("VGWB");//check starting letter required
                                                        Wallet bkwallet = vGWalletDao.getWalletsPartially(newWallet);

                                                        if (bkwallet != null) {
                                                            newWallet.setWalletTypeObj(bkwallet.getWalletTypeObj());
                                                            newWallet.setAccountId(bkwallet.getAccountId());
                                                            statuResponse = vGWalletService.updateVGWallet(newWallet);//update into bankvat acc

                                                            if (statuResponse.getStatusCode() == 0) {
                                                                newWallet = new Wallet();
                                                                newWallet.setKycObj(fromCustwallet.getKycObj());
                                                                newWallet.setWalletBalance(fee.toString());
                                                                newWallet.setWalletTypeObj(walletTypeService.getWalletTypeByName("VGWINCOME"));
                                                                newWallet.setAccountId("VGWI");//check starting letter required
                                                                Wallet incomewallet = vGWalletDao.getWalletsPartially(newWallet);
                                                                newWallet.setWalletTypeObj(incomewallet.getWalletTypeObj());
                                                                newWallet.setAccountId(incomewallet.getAccountId());
                                                                statuResponse = vGWalletService.updateVGWallet(newWallet);//update into VGWIncome acc
                                                                if (statuResponse.getStatusCode() == 0) {
                                                                    transactions.setTransactionbalanceafter(fromBal);
                                                                    toBal = toBal + transactions.getTransactionamount();
                                                                    fromCustwallet.setWalletBalance(encryptionFile.encrypt(frmGenkeys, encryptionFile.encGenKey, fromBal.toString()));
                                                                    toAgentWallet.setWalletBalance(encryptionFile.encrypt(toGenkeys, encryptionFile.encGenKey, toBal.toString()));
                                                                    Wallet wf = walletDao.save(fromCustwallet);
                                                                    if (wf != null) {
                                                                        Wallet wt = walletDao.save(toAgentWallet);
                                                                        if (wt != null) {
                                                                            trHistory = new TransactionHistory();
                                                                            trHistory.setTransactionDate(new Date());
                                                                            trHistory.setTransactionId(TransactionId());
                                                                            trHistory.setDevice(deviceDao.getDeviceByMobileNo(fromCustwallet.getKycObj()));
                                                                            trHistory.setFromWallet(fromCustwallet);
                                                                            trHistory.setToWallet(toAgentWallet);
                                                                            trHistory.setTransactionOperation(transactionTypeService.getTransactionTypeByName("VGW-VGWA"));
                                                                            trHistory.setTransactionamount(transactions.getTransactionamount());
                                                                            trHistory.setTransactionbalancebefore(Double.parseDouble(new DecimalFormat(".##").format(transactions.getTransactionbalancebefore())));
                                                                            trHistory.setTransactionbalanceafter(Double.parseDouble(new DecimalFormat(".##").format(transactions.getTransactionbalanceafter())));
                                                                            trHistory.setApplicableCharges(fee + vat);
                                                                            trHistory.setTransationstatus(statusService.getStatusByName("Success"));
                                                                            trHistory.setMessage(transactions.getMessage());
                                                                            TransactionHistory th = transactionHistoryDao.save(trHistory);
                                                                            if (th != null) {
                                                                                statuResponse.setStatusCode(0);
                                                                                statuResponse.setMessage("Transaction Successfull");
                                                                                if (requestObj.getExtraVariable() != null) {
                                                                                    RequestMoney rm = requestMoneyDao.getOne(Long.parseLong(requestObj.getExtraVariable()));
                                                                                    if (rm != null) {
                                                                                        requestMoneyDao.delete(rm);
                                                                                        if (ResponseEntity.ok().build().getStatusCodeValue() == 200) {
                                                                                            statuResponse.setStatusCode(0);
                                                                                        } else {
                                                                                            statuResponse.setStatusCode(1);
                                                                                        }
                                                                                    }
                                                                                }
                                                                            } else {
                                                                                statuResponse.setStatusCode(1);
                                                                            }
                                                                        } else {
                                                                            trHistory = new TransactionHistory();
                                                                            fromCustwallet.setWalletBalance(encryptionFile.encrypt(frmGenkeys, encryptionFile.encGenKey, frombalstr));
                                                                            walletDao.save(fromCustwallet);
                                                                            trHistory.setTransactionDate(new Date());
                                                                            trHistory.setTransationstatus(statusService.getStatusByName("Failed"));
                                                                            trHistory.setTransactionId(TransactionId());
                                                                            trHistory.setDevice(deviceDao.getDeviceByMobileNo(fromCustwallet.getKycObj()));
                                                                            trHistory.setFromWallet(fromCustwallet);
                                                                            trHistory.setToWallet(toAgentWallet);
                                                                            trHistory.setTransactionOperation(transactionTypeService.getTransactionTypeByName("VGW-VGW"));
                                                                            trHistory.setTransactionamount(transactions.getTransactionamount());
                                                                            trHistory.setMessage(transactions.getMessage());
//                                                trHistory.setTransactionbalancebefore(transactions.getTransactionbalancebefore());
//                                                trHistory.setTransactionbalanceafter(transactions.getTransactionbalanceafter());
                                                                            TransactionHistory th = transactionHistoryDao.save(trHistory);
                                                                            if (th != null) {
                                                                                if (requestObj.getExtraVariable() != null) {
                                                                                    RequestMoney rm = requestMoneyDao.getOne(Long.parseLong(requestObj.getExtraVariable()));
                                                                                    if (rm != null) {
                                                                                        requestMoneyDao.delete(rm);
                                                                                        if (ResponseEntity.ok().build().getStatusCodeValue() == 200) {
                                                                                            statuResponse.setStatusCode(0);
                                                                                        } else {
                                                                                            statuResponse.setStatusCode(1);
                                                                                        }
                                                                                    }
                                                                                }
                                                                            } else {
                                                                                statuResponse.setStatusCode(1);
                                                                            }
                                                                        }
                                                                    } else {
                                                                        statuResponse.setStatusCode(1);
                                                                        statuResponse.setMessage("Please Try After Sometime");
                                                                        logger.error("WalletToWalletServiceImpl.class", "cashoutAtAgentPoint()", "Please Try After Sometime");
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        statuResponse.setStatusCode(4);
                                                        statuResponse.setMessage("Insuficient Fund To Transfer");
                                                        logger.error("WalletToWalletServiceImpl.class", "cashoutAtAgentPoint()", "Insuficient Fund To Transfer");
                                                    }
                                                } else {
                                                    statuResponse.setStatusCode(4);
                                                    statuResponse.setMessage("Please Enter Vaild Amount");
                                                    logger.error("WalletToWalletServiceImpl.class", "cashoutAtAgentPoint()", "Please Enter Vaild Amount");
                                                }
                                            }
                                        } else {
                                            statuResponse.setStatusCode(4);
                                            statuResponse.setMessage("Insuficient Fund To Transfer");
                                            logger.error("WalletToWalletServiceImpl.class", "cashoutAtAgentPoint()", "Insuficient Fund To Transfer");
                                        }
                                    }
                                } else {
                                    statuResponse.setMessage("You can transfer up to $100 to other VGW Account,FullKyc Not Done");
                                    statuResponse.setStatusCode(2);
                                    logger.error("WalletToWalletServiceImpl.class", "cashoutAtAgentPoint()", "You can transfer up to $100 to other VGW Account,FullKyc Not Done");
                                    throw new ResourceNotFoundException("IshalfKyc", "False", "FromKyc");
                                }
                            }

                        }

                    } else {
                        statuResponse.setMessage("Invalid User Access");
                        statuResponse.setStatusCode(2);
                        logger.error("WalletToWalletServiceImpl.class", "cashoutAtAgentPoint()", "Invalid User Access");
                    }
                } else {
                    statuResponse.setStatusCode(3);
                    statuResponse.setMessage("Invalid Input");
                    logger.error("WalletToWalletServiceImpl.class", "cashoutAtAgentPoint()", "Invalid Input");
                }

            } else {
                statuResponse.setStatusCode(3);
                statuResponse.setMessage("Invalid Key");
                logger.error("WalletToWalletServiceImpl.class", "cashoutAtAgentPoint()", "Invalid Key");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            statuResponse.setStatusCode(1);
            logger.error("WalletToWalletServiceImpl.class", "updateVGWallet()", "Please Try After Sometime" + e.getMessage());
        } finally {
            return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(statuResponse)));
        }

    }

    //this is for agent to agent transfer
    @Override
    public String agentToAgentTransfer(String autho_token, String requestedObj) {

        ResponseModel statuResponse = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestedObj);
            if (jsonObj != null) {
                RequestModel requestObj = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
                String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
                if (decryptdId != null && decryptdId.equals(requestObj.getUserId())) {
                    TransactionHistory transactions = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), TransactionHistory.class);
                    Kyc kycObj = new Kyc();
                    kycObj.setMobilenumber(requestObj.getUserId());
                    kycDao.getCustByMobile(requestObj.getUserId());
                    WalletType walletType = walletTypeService.getWalletTypeByName("Agent");
                    Wallet fromwallet = walletDao.getWalletByMobile(kycObj, walletType);
                    Wallet toWallet = walletDao.getWalletByMobile(transactions.getToWallet().getKycObj(), walletType);

                    if (fromwallet != null && toWallet != null) {
                        Boolean limitamt = false;
                        if (fromwallet.getKycObj().getIsfullkyc()) {
                            limitamt = true;
                        } else {
                            if (transactions.getTransactionamount() <= 100) {
                                limitamt = true;
                            } else {
                                limitamt = false;
                            }
                        }
                        if (limitamt) {
                            String frmGenkeys = encryptionFile.getencdsckey(fromwallet.getKycObj().getMobilenumber());
                            String frombalstr = encryptionFile.decryptObject(frmGenkeys, encryptionFile.encGenKey, fromwallet.getWalletBalance());

                            if ((frombalstr != null || !frombalstr.equals("")) && (transactions.getTransactionamount() != null && transactions.getTransactionamount() >= 0)) {
                                Double fromBal = Double.parseDouble(frombalstr);
                                transactions.setTransactionbalancebefore(fromBal);//--->this line for insted create variable i use already requested object

                                if (fromBal >= transactions.getTransactionamount()) {
                                    String toGenkeys = encryptionFile.getencdsckey(toWallet.getKycObj().getMobilenumber());
                                    String tobalstr = encryptionFile.decryptObject(toGenkeys, encryptionFile.encGenKey, toWallet.getWalletBalance());

                                    if (tobalstr != null || !tobalstr.equals("")) {
                                        Double toBal = Double.parseDouble(tobalstr);

                                        if (toBal >= 0) {
                                            TransactionHistory trHistory = null;
                                            Double fee = vGWalletService.applicableCharges(transactions.getTransactionamount());
                                            Double vat = (fee * vatAmt) / 100;

                                            if (fromBal > (transactions.getTransactionamount() + fee + vat)) {

                                                fromBal = fromBal - (transactions.getTransactionamount() + fee + vat);

                                                Wallet newWallet = new Wallet();
                                                newWallet.setKycObj(fromwallet.getKycObj());
                                                newWallet.setWalletBalance(vat.toString());
                                                newWallet.setWalletTypeObj(walletTypeService.getWalletTypeByName("VGWBankVat"));
                                                newWallet.setAccountId("VGWB");//check starting letter required
                                                Wallet bkwallet = vGWalletDao.getWalletsPartially(newWallet);

                                                if (bkwallet != null) {
                                                    newWallet.setWalletTypeObj(bkwallet.getWalletTypeObj());
                                                    newWallet.setAccountId(bkwallet.getAccountId());
                                                    statuResponse = vGWalletService.updateVGWallet(newWallet);//update into bankvat acc

                                                    if (statuResponse.getStatusCode() == 0) {
                                                        newWallet = new Wallet();
                                                        newWallet.setKycObj(fromwallet.getKycObj());
                                                        newWallet.setWalletBalance(fee.toString());
                                                        newWallet.setWalletTypeObj(walletTypeService.getWalletTypeByName("VGWINCOME"));
                                                        newWallet.setAccountId("VGWI");//check starting letter required
                                                        Wallet incomewallet = vGWalletDao.getWalletsPartially(newWallet);
                                                        newWallet.setWalletTypeObj(incomewallet.getWalletTypeObj());
                                                        newWallet.setAccountId(incomewallet.getAccountId());
                                                        statuResponse = vGWalletService.updateVGWallet(newWallet);//update into VGWMain acc
                                                        if (statuResponse.getStatusCode() == 0) {
                                                            transactions.setTransactionbalanceafter(fromBal);
                                                            toBal = toBal + transactions.getTransactionamount();
                                                            fromwallet.setWalletBalance(encryptionFile.encrypt(frmGenkeys, encryptionFile.encGenKey, fromBal.toString()));
                                                            toWallet.setWalletBalance(encryptionFile.encrypt(toGenkeys, encryptionFile.encGenKey, toBal.toString()));
                                                            Wallet wf = walletDao.save(fromwallet);
                                                            if (wf != null) {
                                                                Wallet wt = walletDao.save(toWallet);
                                                                if (wt != null) {
                                                                    trHistory = new TransactionHistory();
                                                                    trHistory.setTransactionDate(new Date());
                                                                    trHistory.setTransactionId(TransactionId());
                                                                    trHistory.setDevice(deviceDao.getDeviceByMobileNo(fromwallet.getKycObj()));
                                                                    trHistory.setFromWallet(fromwallet);
                                                                    trHistory.setToWallet(toWallet);
                                                                    trHistory.setTransactionOperation(transactionTypeService.getTransactionTypeByName("VGWA-VGWA"));
                                                                    trHistory.setTransactionamount(transactions.getTransactionamount());
                                                                    trHistory.setTransactionbalancebefore(Double.parseDouble(new DecimalFormat(".##").format(transactions.getTransactionbalancebefore())));
                                                                    trHistory.setTransactionbalanceafter(Double.parseDouble(new DecimalFormat(".##").format(transactions.getTransactionbalanceafter())));
                                                                    trHistory.setApplicableCharges(fee + vat);
                                                                    trHistory.setTransationstatus(statusService.getStatusByName("Success"));
                                                                    trHistory.setMessage(transactions.getMessage());
                                                                    TransactionHistory th = transactionHistoryDao.save(trHistory);
                                                                    if (th != null) {
                                                                        statuResponse.setStatusCode(0);
                                                                        statuResponse.setMessage("Transaction Successfull");
                                                                        if (requestObj.getExtraVariable() != null) {
                                                                            RequestMoney rm = requestMoneyDao.getOne(Long.parseLong(requestObj.getExtraVariable()));
                                                                            if (rm != null) {
                                                                                requestMoneyDao.delete(rm);
                                                                                if (ResponseEntity.ok().build().getStatusCodeValue() == 200) {
                                                                                    statuResponse.setStatusCode(0);
                                                                                } else {
                                                                                    statuResponse.setStatusCode(1);
                                                                                }
                                                                            }
                                                                        }
                                                                    } else {
                                                                        statuResponse.setStatusCode(1);
                                                                    }
                                                                } else {
                                                                    trHistory = new TransactionHistory();
                                                                    fromwallet.setWalletBalance(encryptionFile.encrypt(frmGenkeys, encryptionFile.encGenKey, frombalstr));
                                                                    walletDao.save(fromwallet);
                                                                    trHistory.setTransactionDate(new Date());
                                                                    trHistory.setTransationstatus(statusService.getStatusByName("Failed"));
                                                                    trHistory.setTransactionId(TransactionId());
                                                                    trHistory.setDevice(deviceDao.getDeviceByMobileNo(fromwallet.getKycObj()));
                                                                    trHistory.setFromWallet(fromwallet);
                                                                    trHistory.setToWallet(toWallet);
                                                                    trHistory.setTransactionOperation(transactionTypeService.getTransactionTypeByName("VGW-VGW"));
                                                                    trHistory.setTransactionamount(transactions.getTransactionamount());
                                                                    trHistory.setMessage(transactions.getMessage());
//                                                trHistory.setTransactionbalancebefore(transactions.getTransactionbalancebefore());
//                                                trHistory.setTransactionbalanceafter(transactions.getTransactionbalanceafter());
                                                                    TransactionHistory th = transactionHistoryDao.save(trHistory);
                                                                    if (th != null) {
                                                                        if (requestObj.getExtraVariable() != null) {
                                                                            RequestMoney rm = requestMoneyDao.getOne(Long.parseLong(requestObj.getExtraVariable()));
                                                                            if (rm != null) {
                                                                                requestMoneyDao.delete(rm);
                                                                                if (ResponseEntity.ok().build().getStatusCodeValue() == 200) {
                                                                                    statuResponse.setStatusCode(0);
                                                                                } else {
                                                                                    statuResponse.setStatusCode(1);
                                                                                }
                                                                            }
                                                                        }
                                                                    } else {
                                                                        statuResponse.setStatusCode(1);
                                                                    }
                                                                }
                                                            } else {
                                                                statuResponse.setStatusCode(1);
                                                                statuResponse.setMessage("Please Try After Sometime");
                                                                logger.error("WalletToWalletServiceImpl.class", "walletToWallet()", "Please Try After Sometime");
                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
                                                statuResponse.setStatusCode(4);
                                                statuResponse.setMessage("Insuficient Fund To Transfer");
                                                logger.error("WalletToWalletServiceImpl.class", "walletToWallet()", "Insuficient Fund To Transfer");
                                            }
                                        } else {
                                            statuResponse.setStatusCode(4);
                                            statuResponse.setMessage("Please Enter Vaild Amount");
                                            logger.error("WalletToWalletServiceImpl.class", "walletToWallet()", "Please Enter Vaild Amount");
                                        }
                                    }
                                } else {
                                    statuResponse.setStatusCode(4);
                                    statuResponse.setMessage("Insuficient Fund To Transfer");
                                    logger.error("WalletToWalletServiceImpl.class", "walletToWallet()", "Insuficient Fund To Transfer");
                                }
                            }
                        } else {
                            statuResponse.setMessage("You can transfer up to $100 to other VGW Account,FullKyc Not Done");
                            statuResponse.setStatusCode(2);
                            logger.error("WalletToWalletServiceImpl.class", "walletToWallet()", "You can transfer up to $100 to other VGW Account,FullKyc Not Done");
                            throw new ResourceNotFoundException("IshalfKyc", "False", "FromKyc");
                        }
                    }
                } else {
                    statuResponse.setMessage("Invalid User Access");
                    statuResponse.setStatusCode(2);
                    logger.error("WalletToWalletServiceImpl.class", "walletToWallet()", "Invalid User Access");
                }
            } else {
                statuResponse.setStatusCode(3);
                statuResponse.setMessage("Invalid Key");
                logger.error("WalletToWalletServiceImpl.class", "walletToWallet()", "Invalid Key");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            statuResponse.setStatusCode(1);
            logger.error("WalletToWalletServiceImpl.class", "updateVGWallet()", "Please Try After Sometime" + e.getMessage());
        } finally {
            return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(statuResponse)));
        }

    }
    
}
