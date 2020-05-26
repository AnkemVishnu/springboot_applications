package com.bizmobia.vgwallet.mobile.serviceImpl;

import com.bizmobia.vgwallet.mobile.dao.TransactionHistoryDao;
import com.bizmobia.vgwallet.mobile.service.EncryptionFile;
import com.bizmobia.vgwallet.mobile.service.ObjectMapperUtility;
import com.bizmobia.vgwallet.mobile.service.TransactionHistoryService;
import com.bizmobia.vgwallet.models.TransactionHistory;
import com.bizmobia.vgwallet.models.Wallet;
import com.bizmobia.vgwallet.request.RequestModel;
import com.bizmobia.vgwallet.request.TransactionHistoryRequest;
import com.bizmobia.vgwallet.response.ResponseModel;
import com.bizmobia.vgwallet.response.Transactions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Vishnu
 */
@Service
@Transactional
public class TransactionHistoryServiceImpl implements TransactionHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionHistoryServiceImpl.class);

    @Autowired
    private TransactionHistoryDao transactionHistoryDao;

    @Autowired
    private EncryptionFile encryptionFile;

    @Autowired
    private ObjectMapperUtility objectMapperUtility;

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    @Override
    public String transactionsHistory(String autho_token, String requestModel) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestModel);
            if (jsonObj != null) {
                RequestModel requestObj = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
                String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
                if (decryptdId.equals(requestObj.getUserId())) {
                    TransactionHistoryRequest thr = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), TransactionHistoryRequest.class);
                    List<TransactionHistory> thlist;
                    if (thr.getMinimum() == 0) {
                        thlist = transactionHistoryDao.getTransactionHistoryByMobile(thr.getMobileNumber(), PageRequest.of(0, thr.getCount()));
                    } else {
                        thlist = transactionHistoryDao.getTransactionHistoryByMobile(thr.getMobileNumber(), thr.getMinimum(), PageRequest.of(0, thr.getCount()));
                    }
                    List<Transactions> list = new ArrayList<>();
                    Transactions tx;
                    String[] payment;
                    if (!thlist.isEmpty()) {
                        for (TransactionHistory th : thlist) {
                            tx = new Transactions();
                            tx.setId(th.getTransactionHistoryId());
                            tx.setTransactionId(th.getTransactionId());
                            tx.setTransactionDate(th.getTransactionDate());
                            tx.setTransactionStatus(th.getTransationstatus().getStatusName());
                            if (th.getFromWallet().getIsVendorWallet()) {
                                tx.setFromMobileNumber(th.getFromWallet().getVendorDetails().getMobileNumber());
                                tx.setFromName(th.getFromWallet().getVendorDetails().getName());
                            } else {
                                tx.setFromMobileNumber(th.getFromWallet().getKycObj().getMobilenumber());
                                tx.setFromName(th.getFromWallet().getKycObj().getFullname());
                            }
                            if (th.getToWallet().getIsVendorWallet()) {
                                tx.setToMobileNumber(th.getToWallet().getVendorDetails().getMobileNumber());
                                tx.setToName(th.getToWallet().getVendorDetails().getName());
                            } else {
                                tx.setToMobileNumber(th.getToWallet().getKycObj().getMobilenumber());
                                tx.setToName(th.getToWallet().getKycObj().getFullname());
                            }
                            if (th.getFromWallet().getKycObj().getMobilenumber().equals(thr.getMobileNumber())) {
                                tx.setCrdt(false);
                            } else {
                                tx.setCrdt(true);
                            }
                            tx.setBalance(th.getTransactionbalanceafter());
                            payment = th.getTransactionOperation().getTransactionTypeName().split("-");
                            tx.setPaymentBy(payment[0]);
                            tx.setPaymentTo(payment[1]);
                            tx.setAmount(th.getTransactionamount());
                            list.add(tx);
                        }
                        statuResponse.setStatusCode(0);
                        statuResponse.setMessage("Wallet Verified");
                        statuResponse.setRespList(list);
                        logger.info("TransactionHistoryServiceImpl.class", "transactionsHistory()", "Wallet Verified");
                    } else {
                        statuResponse.setStatusCode(3);
                        statuResponse.setMessage("No Transactions Found");
                        logger.error("TransactionHistoryServiceImpl.class", "transactionsHistory()", "No Transactions Found");
                    }
                } else {
                    statuResponse.setStatusCode(3);
                    statuResponse.setMessage("Invalid User Access");
                    logger.error("TransactionHistoryServiceImpl.class", "transactionsHistory()", "Invalid User Access");
                }
            } else {
                statuResponse.setStatusCode(3);
                statuResponse.setMessage("Invalid Key");
                logger.error("TransactionHistoryServiceImpl.class", "transactionsHistory()", "Invalid Key");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setStatusCode(1);
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            logger.error("TransactionHistoryServiceImpl.class", "transactionsHistory()", "Please Try After Sometime" + e.getMessage());
        } finally {
            return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(statuResponse)));
        }
    }

    @Override
    public String transactionsStatement(String autho_token, String requestModel) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestModel);
            if (jsonObj != null) {
                RequestModel requestObj = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
                String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
                if (decryptdId.equals(requestObj.getUserId())) {
                    List<TransactionHistory> thlist = transactionHistoryDao.getTransactionStatmentByMobile(requestObj.getExtraVariable());
                    List<Transactions> list = new ArrayList<>();
                    Transactions tx;
                    String[] payment;
                    if (!thlist.isEmpty()) {
                        for (TransactionHistory th : thlist) {
                            tx = new Transactions();
                            tx.setTransactionId(th.getTransactionId());
                            tx.setTransactionDate(th.getTransactionDate());
                            tx.setTransactionStatus(th.getTransationstatus().getStatusName());
                            if (th.getFromWallet().getIsVendorWallet()) {
                                tx.setFromMobileNumber(th.getFromWallet().getVendorDetails().getMobileNumber());
                                tx.setFromName(th.getFromWallet().getVendorDetails().getName());
                            } else {
                                tx.setFromMobileNumber(th.getFromWallet().getKycObj().getMobilenumber());
                                tx.setFromName(th.getFromWallet().getKycObj().getFullname());
                            }
                            if (th.getToWallet().getIsVendorWallet()) {
                                tx.setToMobileNumber(th.getToWallet().getVendorDetails().getMobileNumber());
                                tx.setToName(th.getToWallet().getVendorDetails().getName());
                            } else {
                                tx.setToMobileNumber(th.getToWallet().getKycObj().getMobilenumber());
                                tx.setToName(th.getToWallet().getKycObj().getFullname());
                            }
                            if (th.getFromWallet().getKycObj().getMobilenumber().equals(requestObj.getUserId())) {
                                tx.setCrdt(false);
                            } else {
                                tx.setCrdt(true);
                            }
                            payment = th.getTransactionOperation().getTransactionTypeName().split("-");
                            tx.setPaymentBy(payment[0]);
                            tx.setPaymentTo(payment[1]);
                            tx.setAmount(th.getTransactionamount());
                            tx.setBalance(th.getTransactionbalanceafter());
                            list.add(tx);
                        }
                        statuResponse.setStatusCode(0);
                        statuResponse.setMessage("Wallet Verified");
                        statuResponse.setRespList(list);
                        logger.info("TransactionHistoryServiceImpl.class", "transactionsStatement()", "Wallet Verified");
                    } else {
                        statuResponse.setStatusCode(3);
                        statuResponse.setMessage("No Transactions Found");
                        logger.error("TransactionHistoryServiceImpl.class", "transactionsStatement()", "No Transactions Found");
                    }
                } else {
                    statuResponse.setStatusCode(3);
                    statuResponse.setMessage("Invalid User Access");
                    logger.error("TransactionHistoryServiceImpl.class", "transactionsStatement()", "Invalid User Access");
                }
            } else {
                statuResponse.setStatusCode(3);
                statuResponse.setMessage("Invalid Key");
                logger.error("TransactionHistoryServiceImpl.class", "transactionsStatement()", "Invalid Key");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setStatusCode(1);
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            logger.error("TransactionHistoryServiceImpl.class", "transactionsStatement()", "Please Try After Sometime" + e.getMessage());
        } finally {
            return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(statuResponse)));
        }
    }

    @Override
    public ResponseModel transactionCheck(Double amount, Wallet wallet) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            List<TransactionHistory> list = transactionHistoryDao.todayTransactions(wallet);
            Double todayAmount = 0.0;
            Integer todayCount = 0;
            if (!list.isEmpty()) {
                for (TransactionHistory obj : list) {
                    if (obj.getTransationstatus().getStatusName().equals("Success")) {
                        todayAmount = todayAmount + obj.getTransactionamount();
                        todayCount++;
                    }
                }
                if ((todayAmount + amount) <= wallet.getWalletTypeObj().getAmountPerDay()) {
                    if ((todayCount + 1) <= wallet.getWalletTypeObj().getTransactionsPerDay()) {
                        statuResponse.setStatusCode(0);
                        statuResponse.setMessage("Good To Go");
                        logger.info("TransactionHistoryServiceImpl.class", "transactionCheck()", "Good To Go");
                    } else {
                        statuResponse.setStatusCode(2);
                        statuResponse.setMessage("Cannot Exceed Daily Transactions Limit");
                        logger.error("TransactionHistoryServiceImpl.class", "transactionCheck()", "Cannot Exceed Daily Transactions Limit");
                    }
                } else {
                    statuResponse.setStatusCode(2);
                    statuResponse.setMessage("Cannot Exceed Daily Amount Limit");
                    logger.error("TransactionHistoryServiceImpl.class", "transactionCheck()", "Cannot Exceed Daily Amount Limit");
                }
            } else {
                statuResponse.setStatusCode(0);
                statuResponse.setMessage("No Transactions Today");
                logger.error("TransactionHistoryServiceImpl.class", "transactionCheck()", "No Transactions Today");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setStatusCode(1);
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            logger.error("TransactionHistoryServiceImpl.class", "transactionCheck()", "Please Try After Sometime" + e.getMessage());
        }
        return statuResponse;
    }

}
