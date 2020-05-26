package com.bizmobia.vgwallet.mobile.serviceImpl;

import com.bizmobia.vgwallet.mobile.dao.DevicesDao;
import com.bizmobia.vgwallet.mobile.dao.ElectricityBillingDao;
import com.bizmobia.vgwallet.mobile.dao.TransactionHistoryDao;
import com.bizmobia.vgwallet.mobile.dao.VGWalletDao;
import com.bizmobia.vgwallet.mobile.dao.WalletDao;
import com.bizmobia.vgwallet.mobile.dao.WaterBillingDao;
import com.bizmobia.vgwallet.mobile.service.BillPaymentService;
import com.bizmobia.vgwallet.mobile.service.EncryptionFile;
import com.bizmobia.vgwallet.mobile.service.ObjectMapperUtility;
import com.bizmobia.vgwallet.mobile.service.StatusService;
import com.bizmobia.vgwallet.mobile.service.TransactionHistoryService;
import com.bizmobia.vgwallet.mobile.service.TransactionTypeService;
import com.bizmobia.vgwallet.mobile.service.VGWalletService;
import com.bizmobia.vgwallet.mobile.service.WalletTypeService;
import com.bizmobia.vgwallet.models.ElectricityBilling;
import com.bizmobia.vgwallet.models.Kyc;
import com.bizmobia.vgwallet.models.Status;
import com.bizmobia.vgwallet.models.TransactionHistory;
import com.bizmobia.vgwallet.models.Wallet;
import com.bizmobia.vgwallet.models.WaterBilling;
import com.bizmobia.vgwallet.request.RequestModel;
import com.bizmobia.vgwallet.request.TransactionRequest;
import com.bizmobia.vgwallet.response.BillingPaymentsResponse;
import com.bizmobia.vgwallet.response.ResponseModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Vishnu
 */
@Service
@Transactional
public class BillPaymentServiceImpl implements BillPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(BillPaymentServiceImpl.class);

    @Autowired
    private ObjectMapperUtility objectMapperUtility;

    @Autowired
    private EncryptionFile encryptionFile;

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    @Autowired
    private WalletDao walletDao;

    @Autowired
    private DevicesDao deviceDao;

    @Autowired
    private TransactionHistoryDao transactionHistoryDao;

    @Autowired
    private ElectricityBillingDao electricityBillingDao;

    @Autowired
    private WaterBillingDao waterBillingDao;

    @Autowired
    private StatusService statusService;

    @Autowired
    private TransactionTypeService transactionTypeService;

    @Autowired
    private TransactionHistoryService transactionHistoryService;

    @Autowired
    private WalletTypeService walletTypeService;

    @Autowired
    private VGWalletService vGWalletService;

    @Autowired
    private VGWalletDao vGWalletDao;

    private final Double vatAmt = 16.0;

    public String TransactionId() {
        Calendar cald = Calendar.getInstance();
        cald.setTime(new Date());
        cald.getTimeInMillis();
        String str = String.valueOf(cald.getTimeInMillis()).substring(3);
        return str + RandomStringUtils.randomNumeric(5);
    }
    
    @Override
    public String checkElectricityBill(String autho_token, String requestModel) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestModel);
            if (jsonObj != null) {
                RequestModel requestObj = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
                String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
                if (decryptdId.equals(requestObj.getUserId())) {
                    Kyc kyc = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), Kyc.class);
                    ElectricityBilling eb = electricityBillingDao.getElectricityBilling(kyc);
                    if (eb != null) {
                        BillingPaymentsResponse epr = new BillingPaymentsResponse();
                        if ("PAID".equals(eb.getCurrentStatus().getStatusName()) && "PAID".equals(eb.getArrearStatus().getStatusName())) {
                            epr.setMobileNumber(eb.getMeterReading().getKyc().getMobilenumber());
                            epr.setName(eb.getMeterReading().getKyc().getFullname());
                            epr.setConsumerId(eb.getMeterReading().getConsumerId());
                            epr.setBillingDate(eb.getBillingDate());
                            epr.setPaymentDate(eb.getCreatedDate());
                            epr.setCurrentStatus(eb.getCurrentStatus().getStatusName());

                            statuResponse.setRespObject(epr);
                            statuResponse.setMessage("Payment Done");
                            statuResponse.setStatusCode(0);
                            logger.info("BillPaymentServiceImpl.class", "checkElectricityBill()", "Payment Done");
                        } else {
                            epr.setMobileNumber(eb.getMeterReading().getKyc().getMobilenumber());
                            epr.setName(eb.getMeterReading().getKyc().getFullname());
                            epr.setConsumerId(eb.getMeterReading().getConsumerId());
                            epr.setBillingDate(eb.getBillingDate());
                            epr.setDueDate(eb.getDueDate());
                            epr.setArrearAmount(eb.getArrearAmount());
                            epr.setArrearStatus(eb.getArrearStatus().getStatusName());
                            epr.setCurrentAmount(eb.getCurrentAmount());
                            epr.setCurrentStatus(eb.getCurrentStatus().getStatusName());

                            statuResponse.setRespObject(epr);
                            statuResponse.setExtraVariable(eb.getVendor().getMobileNumber());
                            statuResponse.setMessage("Payment Not Done");
                            statuResponse.setStatusCode(0);
                            logger.error("BillPaymentServiceImpl.class", "checkElectricityBill()", "Payment Not Done");
                        }
                    } else {
                        statuResponse.setStatusCode(1);
                        statuResponse.setMessage("NO Electricity Bill Available");
                        logger.error("BillPaymentServiceImpl.class", "checkElectricityBill()", "NO Electricity Bill Available");
                    }
                } else {
                    statuResponse.setStatusCode(3);
                    statuResponse.setMessage("Invalid User Access");
                    logger.error("BillPaymentServiceImpl.class", "checkElectricityBill()", "Invalid User Access");
                }
            } else {
                statuResponse.setStatusCode(3);
                statuResponse.setMessage("Invalid Key");
                logger.error("BillPaymentServiceImpl.class", "checkElectricityBill()", "Invalid Key");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setStatusCode(1);
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            logger.error("BillPaymentServiceImpl.class", "checkElectricityBill()", "Please Try After Sometime" + e.getMessage());
        } finally {
            return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(statuResponse)));
        }
    }

    @Override
    public String electricityBillPayment(String autho_token, String requestModel) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestModel);
            if (jsonObj != null) {
                RequestModel requestObj = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
                String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
                if (decryptdId.equals(requestObj.getUserId())) {

                    TransactionRequest transactions = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), TransactionRequest.class);
                    Kyc kycObj = new Kyc();
                    kycObj.setMobilenumber(requestObj.getUserId());
                    Wallet fromwallet = walletDao.getWalletByMobile(kycObj, walletTypeService.getWalletTypeByName("Classics"));
                    Wallet toWallet = walletDao.getVendorWallet(transactions.getToNumber());

                    if (fromwallet != null && toWallet != null) {
                        String frmGenkeys = encryptionFile.getencdsckey(fromwallet.getKycObj().getMobilenumber());
                        String frombalstr = encryptionFile.decryptObject(frmGenkeys, encryptionFile.encGenKey, fromwallet.getWalletBalance());

                        if ((frombalstr != null || !frombalstr.equals("")) && (transactions.getAmount() != null && transactions.getAmount() >= 0)) {
                            Double fromBal = Double.parseDouble(frombalstr);

                            Double fromBalBefore = fromBal;//--->this line for insted create variable i use already requested object

                            statuResponse = transactionHistoryService.transactionCheck(transactions.getAmount(), fromwallet);
                            if (statuResponse.getStatusCode() == 0) {

                                if (fromBal >= transactions.getAmount()) {
                                    String toGenkeys = encryptionFile.getencdsckey(toWallet.getVendorDetails().getMobileNumber());
                                    String tobalstr = encryptionFile.decryptObject(toGenkeys, encryptionFile.encGenKey, toWallet.getWalletBalance());
                                    if (tobalstr != null || !tobalstr.equals("")) {
                                        Double toBal = Double.parseDouble(tobalstr);

                                        if (toBal >= 0) {

                                            TransactionHistory trHistory = null;
                                            Double fee = vGWalletService.applicableCharges(transactions.getAmount());
                                            Double vat = (fee * vatAmt) / 100;
                                            if (fromBal > (transactions.getAmount() + fee + vat)) {

                                                fromBal = fromBal - (transactions.getAmount() + fee + vat);

                                                Wallet newWallet = new Wallet();
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
                                                        newWallet.setWalletBalance(fee.toString());
                                                        newWallet.setWalletTypeObj(walletTypeService.getWalletTypeByName("VGWINCOME"));
                                                        newWallet.setAccountId("VGWI");//check starting letter required
                                                        Wallet incomewallet = vGWalletDao.getWalletsPartially(newWallet);

                                                        if (incomewallet != null) {
                                                            newWallet.setWalletTypeObj(incomewallet.getWalletTypeObj());
                                                            newWallet.setAccountId(incomewallet.getAccountId());
                                                            statuResponse = vGWalletService.updateVGWallet(newWallet);//update into VGWMain acc

                                                            if (statuResponse.getStatusCode() == 0) {
                                                                Double fromBalAfter = fromBal;
                                                                toBal = toBal + transactions.getAmount();
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
                                                                        trHistory.setTransactionOperation(transactionTypeService.getTransactionTypeByName("VGW-VGWV"));
                                                                        trHistory.setTransactionamount(transactions.getAmount());
                                                                        trHistory.setTransactionbalancebefore(fromBalBefore);
                                                                        trHistory.setTransactionbalanceafter(fromBalAfter);
                                                                        trHistory.setApplicableCharges(fee + vat);
                                                                        Status st = statusService.getStatusByName("Success");
                                                                        trHistory.setTransationstatus(st);
                                                                        trHistory.setMessage("Electricity Bill Payment");
                                                                        transactionHistoryDao.save(trHistory);
                                                                        
                                                                        Kyc consumer = new Kyc();
                                                                        consumer.setMobilenumber(transactions.getFromNumber());
                                                                        ElectricityBilling eb = electricityBillingDao.getElectricityBilling(consumer);

                                                                        if (eb.getArrearStatus().getStatusName().equals("UNPAID")) {
                                                                            if (Objects.equals(eb.getArrearAmount(), transactions.getAmount())) {
                                                                                eb.setArrearAmount(0.0);
                                                                                eb.setArrearStatus(statusService.getStatusByName("PAID"));
                                                                                eb.setAmount(eb.getAmount() - transactions.getAmount());
                                                                                eb.setCreatedDate(new Date());
                                                                                ElectricityBilling obj = electricityBillingDao.save(eb);
                                                                                if (obj != null) {
                                                                                    statuResponse.setStatusCode(0);
                                                                                    statuResponse.setMessage("Success");
                                                                                } else {
                                                                                    statuResponse.setStatusCode(1);
                                                                                    statuResponse.setMessage("Failure");
                                                                                    
                                                                                }
                                                                            } else if (eb.getArrearAmount() > transactions.getAmount()) {
                                                                                eb.setArrearAmount(eb.getArrearAmount() - transactions.getAmount());
                                                                                eb.setAmount(eb.getAmount() - transactions.getAmount());
                                                                                eb.setCreatedDate(new Date());
                                                                                ElectricityBilling obj = electricityBillingDao.save(eb);
                                                                                if (obj != null) {
                                                                                    statuResponse.setStatusCode(0);
                                                                                    statuResponse.setMessage("Success");
                                                                                } else {
                                                                                    statuResponse.setStatusCode(1);
                                                                                    statuResponse.setMessage("Failure");
                                                                                }
                                                                            } else if (eb.getArrearAmount() < transactions.getAmount()) {
                                                                                Double bal = transactions.getAmount() - eb.getArrearAmount();
                                                                                eb.setArrearAmount(0.0);
                                                                                eb.setArrearStatus(statusService.getStatusByName("PAID"));
                                                                                if (Objects.equals(bal, eb.getCurrentAmount())) {
                                                                                    eb.setCurrentAmount(0.0);
                                                                                    eb.setCurrentStatus(statusService.getStatusByName("PAID"));
                                                                                    eb.setAmount(0.0);
                                                                                    eb.setCreatedDate(new Date());
                                                                                    ElectricityBilling obj = electricityBillingDao.save(eb);
                                                                                    if (obj != null) {
                                                                                        statuResponse.setStatusCode(0);
                                                                                        statuResponse.setMessage("Success");
                                                                                    } else {
                                                                                        statuResponse.setStatusCode(1);
                                                                                        statuResponse.setMessage("Failure");
                                                                                    }
                                                                                } else if (bal < eb.getCurrentAmount()) {
                                                                                    eb.setCurrentAmount(eb.getCurrentAmount() - bal);
                                                                                    eb.setAmount(eb.getAmount() - transactions.getAmount());
                                                                                    eb.setCreatedDate(new Date());
                                                                                    ElectricityBilling obj = electricityBillingDao.save(eb);
                                                                                    if (obj != null) {
                                                                                        statuResponse.setStatusCode(0);
                                                                                        statuResponse.setMessage("Success");
                                                                                    } else {
                                                                                        statuResponse.setStatusCode(1);
                                                                                        statuResponse.setMessage("Failure");
                                                                                    }
                                                                                } else if (bal > eb.getCurrentAmount()) {
                                                                                    eb.setCurrentAmount(0.0);
                                                                                    eb.setCurrentStatus(statusService.getStatusByName("PAID"));
                                                                                    eb.setAmount(0.0);
                                                                                    eb.setCreatedDate(new Date());
                                                                                    ElectricityBilling obj = electricityBillingDao.save(eb);
                                                                                    if (obj != null) {
                                                                                        statuResponse.setStatusCode(0);
                                                                                        statuResponse.setMessage("Success");
                                                                                    } else {
                                                                                        statuResponse.setStatusCode(1);
                                                                                        statuResponse.setMessage("Failure");
                                                                                    }
                                                                                }
                                                                            }
                                                                        } else if (eb.getArrearStatus().getStatusName().equals("PAID") && eb.getCurrentStatus().getStatusName().equals("UNPAID")) {
                                                                            if (Objects.equals(transactions.getAmount(), eb.getCurrentAmount())) {
                                                                                eb.setCurrentAmount(0.0);
                                                                                eb.setCurrentStatus(statusService.getStatusByName("PAID"));
                                                                                eb.setAmount(0.0);
                                                                                eb.setCreatedDate(new Date());
                                                                                ElectricityBilling obj = electricityBillingDao.save(eb);
                                                                                if (obj != null) {
                                                                                    statuResponse.setStatusCode(0);
                                                                                    statuResponse.setMessage("Success");
                                                                                } else {
                                                                                    statuResponse.setStatusCode(1);
                                                                                    statuResponse.setMessage("Failure");
                                                                                }
                                                                            } else if (transactions.getAmount() < eb.getCurrentAmount()) {
                                                                                eb.setCurrentAmount(eb.getCurrentAmount() - transactions.getAmount());
                                                                                eb.setAmount(eb.getAmount() - transactions.getAmount());
                                                                                eb.setCreatedDate(new Date());
                                                                                ElectricityBilling obj = electricityBillingDao.save(eb);
                                                                                if (obj != null) {
                                                                                    statuResponse.setStatusCode(0);
                                                                                    statuResponse.setMessage("Success");
                                                                                } else {
                                                                                    statuResponse.setStatusCode(1);
                                                                                    statuResponse.setMessage("Failure");
                                                                                }
                                                                            } else if (transactions.getAmount() > eb.getCurrentAmount()) {
                                                                                eb.setCurrentAmount(0.0);
                                                                                eb.setCurrentStatus(statusService.getStatusByName("PAID"));
                                                                                eb.setAmount(0.0);
                                                                                eb.setCreatedDate(new Date());
                                                                                ElectricityBilling obj = electricityBillingDao.save(eb);
                                                                                if (obj != null) {
                                                                                    statuResponse.setStatusCode(0);
                                                                                    statuResponse.setMessage("Success");
                                                                                } else {
                                                                                    statuResponse.setStatusCode(1);
                                                                                    statuResponse.setMessage("Failure");
                                                                                }
                                                                            }
                                                                        }
                                                                    } else {
                                                                        trHistory = new TransactionHistory();
                                                                        fromwallet.setWalletBalance(encryptionFile.encrypt(frmGenkeys, encryptionFile.encGenKey, frombalstr));
                                                                        walletDao.save(fromwallet);
                                                                        trHistory.setTransactionDate(new Date());
                                                                        Status st = statusService.getStatusByName("Failed");
                                                                        trHistory.setTransationstatus(st);
                                                                        trHistory.setTransactionId(TransactionId());
                                                                        trHistory.setDevice(deviceDao.getDeviceByMobileNo(fromwallet.getKycObj()));
                                                                        trHistory.setFromWallet(fromwallet);
                                                                        trHistory.setToWallet(toWallet);
                                                                        trHistory.setTransactionOperation(transactionTypeService.getTransactionTypeByName("VGW-VGWV"));
                                                                        trHistory.setTransactionamount(transactions.getAmount());
                                                                        trHistory.setMessage("Electricity Bill Payment");
                                                                        transactionHistoryDao.save(trHistory);
                                                                    }
                                                                } else {
                                                                    statuResponse.setStatusCode(1);
                                                                    statuResponse.setMessage("Please Try After Sometime");
                                                                    logger.error("BillPaymentServiceImpl.class", "electricityBillPayment()", "Please Try After Sometime");
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
                                                statuResponse.setStatusCode(4);
                                                statuResponse.setMessage("Insuficient Fund To Transfer");
                                                logger.error("BillPaymentServiceImpl.class", "electricityBillPayment()", "Insuficient Fund To Transfer");
                                            }
                                        } else {
                                            statuResponse.setStatusCode(4);
                                            statuResponse.setMessage("Please Enter Vaild Amount");
                                            logger.error("BillPaymentServiceImpl.class", "electricityBillPayment()", "Please Enter Vaild Amount");
                                        }
                                    }
                                } else {
                                    statuResponse.setStatusCode(4);
                                    statuResponse.setMessage("Insuficient Fund To Transfer");
                                    logger.error("BillPaymentServiceImpl.class", "electricityBillPayment()", "Insuficient Fund To Transfer");
                                }
                            }
                        }
                    }
                } else {
                    statuResponse.setStatusCode(3);
                    statuResponse.setMessage("Invalid User Access");
                    logger.error("BillPaymentServiceImpl.class", "electricityBillPayment()", "Invalid User Access");
                }
            } else {
                statuResponse.setStatusCode(3);
                statuResponse.setMessage("Invalid Key");
                logger.error("BillPaymentServiceImpl.class", "electricityBillPayment()", "Invalid Key");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setStatusCode(1);
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            logger.error("BillPaymentServiceImpl.class", "electricityBillPayment()", "Please Try After Sometime" + e.getMessage());
        } finally {
            return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(statuResponse)));
        }
    }

    @Override
    public String checkWaterBill(String autho_token, String requestModel) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestModel);
            if (jsonObj != null) {
                RequestModel requestObj = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
                String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
                if (decryptdId.equals(requestObj.getUserId())) {
                    Kyc kyc = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), Kyc.class);
                    WaterBilling eb = waterBillingDao.getWaterBilling(kyc);
                    if (eb != null) {
                        BillingPaymentsResponse epr = new BillingPaymentsResponse();
                        if ("PAID".equals(eb.getCurrentStatus().getStatusName()) && "PAID".equals(eb.getArrearStatus().getStatusName())) {
                            epr.setMobileNumber(eb.getMeterReading().getKyc().getMobilenumber());
                            epr.setName(eb.getMeterReading().getKyc().getFullname());
                            epr.setConsumerId(eb.getMeterReading().getConsumerId());
                            epr.setBillingDate(eb.getBillingDate());
                            epr.setPaymentDate(eb.getCreatedDate());
                            epr.setCurrentStatus(eb.getCurrentStatus().getStatusName());

                            statuResponse.setRespObject(epr);
                            statuResponse.setMessage("Payment Done");
                            statuResponse.setStatusCode(0);
                            logger.info("BillPaymentServiceImpl.class", "checkWaterBill()", "Payment Done");
                        } else {
                            epr.setMobileNumber(eb.getMeterReading().getKyc().getMobilenumber());
                            epr.setName(eb.getMeterReading().getKyc().getFullname());
                            epr.setConsumerId(eb.getMeterReading().getConsumerId());
                            epr.setBillingDate(eb.getBillingDate());
                            epr.setDueDate(eb.getDueDate());
                            epr.setArrearAmount(eb.getArrearAmount());
                            epr.setArrearStatus(eb.getArrearStatus().getStatusName());
                            epr.setCurrentAmount(eb.getCurrentAmount());
                            epr.setCurrentStatus(eb.getCurrentStatus().getStatusName());

                            statuResponse.setRespObject(epr);
                            statuResponse.setExtraVariable(eb.getVendor().getMobileNumber());
                            statuResponse.setMessage("Payment Not Done");
                            statuResponse.setStatusCode(0);
                            logger.error("BillPaymentServiceImpl.class", "checkWaterBill()", "Payment Not Done");
                        }
                    } else {
                        statuResponse.setStatusCode(1);
                        statuResponse.setMessage("NO Water Bill Available");
                        logger.error("BillPaymentServiceImpl.class", "checkWaterBill()", "NO Electricity Bill Available");
                    }
                } else {
                    statuResponse.setStatusCode(3);
                    statuResponse.setMessage("Invalid User Access");
                    logger.error("BillPaymentServiceImpl.class", "checkWaterBill()", "Invalid User Access");
                }
            } else {
                statuResponse.setStatusCode(3);
                statuResponse.setMessage("Invalid Key");
                logger.error("BillPaymentServiceImpl.class", "checkWaterBill()", "Invalid Key");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setStatusCode(1);
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            logger.error("BillPaymentServiceImpl.class", "checkWaterBill()", "Please Try After Sometime" + e.getMessage());
        } finally {
            return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(statuResponse)));
        }
    }

    @Override
    public String waterBillPayment(String autho_token, String requestModel) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String jsonObj = encryptionFile.decryptObject(encryptionFile.symKey, encryptionFile.initVector, requestModel);
            if (jsonObj != null) {
                RequestModel requestObj = objectMapperUtility.jsonToObject(jsonObj, RequestModel.class);
                String gendynamikey = encryptionFile.reGenerateEncryptedKey(requestObj.getUserId(), autho_token);
                String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, requestObj.getUserId().length());
                if (decryptdId.equals(requestObj.getUserId())) {

                    TransactionRequest transactions = objectMapperUtility.jsonToObject(gson.toJson(requestObj.getReqObject()), TransactionRequest.class);
                    Kyc kycObj = new Kyc();
                    kycObj.setMobilenumber(requestObj.getUserId());
                    Wallet fromwallet = walletDao.getWalletByMobile(kycObj, walletTypeService.getWalletTypeByName("Classics"));
                    Wallet toWallet = walletDao.getVendorWallet(transactions.getToNumber());

                    if (fromwallet != null && toWallet != null) {
                        String frmGenkeys = encryptionFile.getencdsckey(fromwallet.getKycObj().getMobilenumber());
                        String frombalstr = encryptionFile.decryptObject(frmGenkeys, encryptionFile.encGenKey, fromwallet.getWalletBalance());

                        if ((frombalstr != null || !frombalstr.equals("")) && (transactions.getAmount() != null && transactions.getAmount() >= 0)) {
                            Double fromBal = Double.parseDouble(frombalstr);

                            Double fromBalBefore = fromBal;//--->this line for insted create variable i use already requested object

                            statuResponse = transactionHistoryService.transactionCheck(transactions.getAmount(), fromwallet);
                            if (statuResponse.getStatusCode() == 0) {

                                if (fromBal >= transactions.getAmount()) {
                                    String toGenkeys = encryptionFile.getencdsckey(toWallet.getVendorDetails().getMobileNumber());
                                    String tobalstr = encryptionFile.decryptObject(toGenkeys, encryptionFile.encGenKey, toWallet.getWalletBalance());
                                    if (tobalstr != null || !tobalstr.equals("")) {
                                        Double toBal = Double.parseDouble(tobalstr);

                                        if (toBal >= 0) {

                                            TransactionHistory trHistory = null;
                                            Double fee = vGWalletService.applicableCharges(transactions.getAmount());
                                            Double vat = (fee * vatAmt) / 100;
                                            if (fromBal > (transactions.getAmount() + fee + vat)) {

                                                fromBal = fromBal - (transactions.getAmount() + fee + vat);

                                                Wallet newWallet = new Wallet();
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
                                                        newWallet.setWalletBalance(fee.toString());
                                                        newWallet.setWalletTypeObj(walletTypeService.getWalletTypeByName("VGWINCOME"));
                                                        newWallet.setAccountId("VGWI");//check starting letter required
                                                        Wallet incomewallet = vGWalletDao.getWalletsPartially(newWallet);

                                                        if (incomewallet != null) {
                                                            newWallet.setWalletTypeObj(incomewallet.getWalletTypeObj());
                                                            newWallet.setAccountId(incomewallet.getAccountId());
                                                            statuResponse = vGWalletService.updateVGWallet(newWallet);//update into VGWMain acc

                                                            if (statuResponse.getStatusCode() == 0) {
                                                                Double fromBalAfter = fromBal;
                                                                toBal = toBal + transactions.getAmount();
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
                                                                        trHistory.setTransactionOperation(transactionTypeService.getTransactionTypeByName("VGW-VGWV"));
                                                                        trHistory.setTransactionamount(transactions.getAmount());
                                                                        trHistory.setTransactionbalancebefore(fromBalBefore);
                                                                        trHistory.setTransactionbalanceafter(fromBalAfter);
                                                                        trHistory.setApplicableCharges(fee + vat);
                                                                        Status st = statusService.getStatusByName("Success");
                                                                        trHistory.setTransationstatus(st);
                                                                        trHistory.setMessage("Water Bill Payment");
                                                                        transactionHistoryDao.save(trHistory);
                                                                        
                                                                        Kyc consumer = new Kyc();
                                                                        consumer.setMobilenumber(transactions.getFromNumber());
                                                                        WaterBilling eb = waterBillingDao.getWaterBilling(consumer);

                                                                        if (eb.getArrearStatus().getStatusName().equals("UNPAID")) {
                                                                            if (Objects.equals(eb.getArrearAmount(), transactions.getAmount())) {
                                                                                eb.setArrearAmount(0.0);
                                                                                eb.setArrearStatus(statusService.getStatusByName("PAID"));
                                                                                eb.setAmount(eb.getAmount() - transactions.getAmount());
                                                                                eb.setCreatedDate(new Date());
                                                                                WaterBilling obj = waterBillingDao.save(eb);
                                                                                if (obj != null) {
                                                                                    statuResponse.setStatusCode(0);
                                                                                    statuResponse.setMessage("Success");
                                                                                } else {
                                                                                    statuResponse.setStatusCode(1);
                                                                                    statuResponse.setMessage("Failure");
                                                                                }
                                                                            } else if (eb.getArrearAmount() > transactions.getAmount()) {
                                                                                eb.setArrearAmount(eb.getArrearAmount() - transactions.getAmount());
                                                                                eb.setAmount(eb.getAmount() - transactions.getAmount());
                                                                                eb.setCreatedDate(new Date());
                                                                                WaterBilling obj = waterBillingDao.save(eb);
                                                                                if (obj != null) {
                                                                                    statuResponse.setStatusCode(0);
                                                                                    statuResponse.setMessage("Success");
                                                                                } else {
                                                                                    statuResponse.setStatusCode(1);
                                                                                    statuResponse.setMessage("Failure");
                                                                                }
                                                                            } else if (eb.getArrearAmount() < transactions.getAmount()) {
                                                                                Double bal = transactions.getAmount() - eb.getArrearAmount();
                                                                                eb.setArrearAmount(0.0);
                                                                                eb.setArrearStatus(statusService.getStatusByName("PAID"));
                                                                                if (Objects.equals(bal, eb.getCurrentAmount())) {
                                                                                    eb.setCurrentAmount(0.0);
                                                                                    eb.setCurrentStatus(statusService.getStatusByName("PAID"));
                                                                                    eb.setAmount(0.0);
                                                                                    eb.setCreatedDate(new Date());
                                                                                    WaterBilling obj = waterBillingDao.save(eb);
                                                                                    if (obj != null) {
                                                                                        statuResponse.setStatusCode(0);
                                                                                        statuResponse.setMessage("Success");
                                                                                    } else {
                                                                                        statuResponse.setStatusCode(1);
                                                                                        statuResponse.setMessage("Failure");
                                                                                    }
                                                                                } else if (bal < eb.getCurrentAmount()) {
                                                                                    eb.setCurrentAmount(eb.getCurrentAmount() - bal);
                                                                                    eb.setAmount(eb.getAmount() - transactions.getAmount());
                                                                                    eb.setCreatedDate(new Date());
                                                                                    WaterBilling obj = waterBillingDao.save(eb);
                                                                                    if (obj != null) {
                                                                                        statuResponse.setStatusCode(0);
                                                                                        statuResponse.setMessage("Success");
                                                                                    } else {
                                                                                        statuResponse.setStatusCode(1);
                                                                                        statuResponse.setMessage("Failure");
                                                                                    }
                                                                                } else if (bal > eb.getCurrentAmount()) {
                                                                                    eb.setCurrentAmount(0.0);
                                                                                    eb.setCurrentStatus(statusService.getStatusByName("PAID"));
                                                                                    eb.setAmount(0.0);
                                                                                    eb.setCreatedDate(new Date());
                                                                                    WaterBilling obj = waterBillingDao.save(eb);
                                                                                    if (obj != null) {
                                                                                        statuResponse.setStatusCode(0);
                                                                                        statuResponse.setMessage("Success");
                                                                                    } else {
                                                                                        statuResponse.setStatusCode(1);
                                                                                        statuResponse.setMessage("Failure");
                                                                                    }
                                                                                }
                                                                            }
                                                                        } else if (eb.getArrearStatus().getStatusName().equals("PAID") && eb.getCurrentStatus().getStatusName().equals("UNPAID")) {
                                                                            if (Objects.equals(transactions.getAmount(), eb.getCurrentAmount())) {
                                                                                eb.setCurrentAmount(0.0);
                                                                                eb.setCurrentStatus(statusService.getStatusByName("PAID"));
                                                                                eb.setAmount(0.0);
                                                                                eb.setCreatedDate(new Date());
                                                                                WaterBilling obj = waterBillingDao.save(eb);
                                                                                if (obj != null) {
                                                                                    statuResponse.setStatusCode(0);
                                                                                    statuResponse.setMessage("Success");
                                                                                } else {
                                                                                    statuResponse.setStatusCode(1);
                                                                                    statuResponse.setMessage("Failure");
                                                                                }
                                                                            } else if (transactions.getAmount() < eb.getCurrentAmount()) {
                                                                                eb.setCurrentAmount(eb.getCurrentAmount() - transactions.getAmount());
                                                                                eb.setAmount(eb.getAmount() - transactions.getAmount());
                                                                                eb.setCreatedDate(new Date());
                                                                                WaterBilling obj = waterBillingDao.save(eb);
                                                                                if (obj != null) {
                                                                                    statuResponse.setStatusCode(0);
                                                                                    statuResponse.setMessage("Success");
                                                                                } else {
                                                                                    statuResponse.setStatusCode(1);
                                                                                    statuResponse.setMessage("Failure");
                                                                                }
                                                                            } else if (transactions.getAmount() > eb.getCurrentAmount()) {
                                                                                eb.setCurrentAmount(0.0);
                                                                                eb.setCurrentStatus(statusService.getStatusByName("PAID"));
                                                                                eb.setAmount(0.0);
                                                                                eb.setCreatedDate(new Date());
                                                                                WaterBilling obj = waterBillingDao.save(eb);
                                                                                if (obj != null) {
                                                                                    statuResponse.setStatusCode(0);
                                                                                    statuResponse.setMessage("Success");
                                                                                } else {
                                                                                    statuResponse.setStatusCode(1);
                                                                                    statuResponse.setMessage("Failure");
                                                                                }
                                                                            }
                                                                        }
                                                                    } else {
                                                                        trHistory = new TransactionHistory();
                                                                        fromwallet.setWalletBalance(encryptionFile.encrypt(frmGenkeys, encryptionFile.encGenKey, frombalstr));
                                                                        walletDao.save(fromwallet);
                                                                        trHistory.setTransactionDate(new Date());
                                                                        Status st = statusService.getStatusByName("Failed");
                                                                        trHistory.setTransationstatus(st);
                                                                        trHistory.setTransactionId(TransactionId());
                                                                        trHistory.setDevice(deviceDao.getDeviceByMobileNo(fromwallet.getKycObj()));
                                                                        trHistory.setFromWallet(fromwallet);
                                                                        trHistory.setToWallet(toWallet);
                                                                        trHistory.setTransactionOperation(transactionTypeService.getTransactionTypeByName("VGW-VGWV"));
                                                                        trHistory.setTransactionamount(transactions.getAmount());
                                                                        trHistory.setMessage("Water Bill Payment");
                                                                        transactionHistoryDao.save(trHistory);
                                                                    }
                                                                } else {
                                                                    statuResponse.setStatusCode(1);
                                                                    statuResponse.setMessage("Please Try After Sometime");
                                                                    logger.error("BillPaymentServiceImpl.class", "waterBillPayment()", "Please Try After Sometime");
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
                                                statuResponse.setStatusCode(4);
                                                statuResponse.setMessage("Insuficient Fund To Transfer");
                                                logger.error("BillPaymentServiceImpl.class", "waterBillPayment()", "Insuficient Fund To Transfer");
                                            }
                                        } else {
                                            statuResponse.setStatusCode(4);
                                            statuResponse.setMessage("Please Enter Vaild Amount");
                                            logger.error("BillPaymentServiceImpl.class", "waterBillPayment()", "Please Enter Vaild Amount");
                                        }
                                    }
                                } else {
                                    statuResponse.setStatusCode(4);
                                    statuResponse.setMessage("Insuficient Fund To Transfer");
                                    logger.error("BillPaymentServiceImpl.class", "waterBillPayment()", "Insuficient Fund To Transfer");
                                }
                            }
                        }
                    }
                } else {
                    statuResponse.setStatusCode(3);
                    statuResponse.setMessage("Invalid User Access");
                    logger.error("BillPaymentServiceImpl.class", "waterBillPayment()", "Invalid User Access");
                }
            } else {
                statuResponse.setStatusCode(3);
                statuResponse.setMessage("Invalid Key");
                logger.error("BillPaymentServiceImpl.class", "waterBillPayment()", "Invalid Key");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setStatusCode(1);
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            logger.error("BillPaymentServiceImpl.class", "waterBillPayment()", "Please Try After Sometime" + e.getMessage());
        } finally {
            return gson.toJson(encryptionFile.encrypt(encryptionFile.symKey, encryptionFile.initVector, gson.toJson(statuResponse)));
        }
    }

}
