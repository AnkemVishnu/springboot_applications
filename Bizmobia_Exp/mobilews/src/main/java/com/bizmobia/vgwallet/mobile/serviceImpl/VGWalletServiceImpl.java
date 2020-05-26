package com.bizmobia.vgwallet.mobile.serviceImpl;

import com.bizmobia.vgwallet.mobile.dao.ApplicableChargesDao;
import com.bizmobia.vgwallet.mobile.dao.VGWalletDao;
import com.bizmobia.vgwallet.mobile.service.EncryptionFile;
import com.bizmobia.vgwallet.mobile.service.ObjectMapperUtility;
import com.bizmobia.vgwallet.mobile.service.VGWalletService;
import com.bizmobia.vgwallet.models.ApplicableCharges;
import com.bizmobia.vgwallet.models.Wallet;
import com.bizmobia.vgwallet.response.ResponseModel;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Vaibhav
 */
@Service
@Transactional
public class VGWalletServiceImpl implements VGWalletService {

    private static final Logger logger = LoggerFactory.getLogger(VGWalletServiceImpl.class);

    @Autowired
    private VGWalletDao vGWalletDao;

    @Autowired
    private ApplicableChargesDao applicableChargesDaoDao;

    @Autowired
    private EncryptionFile encryptionFile;

    @Override
    public ResponseModel updateVGWallet(Wallet walletsObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            Wallet myWallets = vGWalletDao.getWalletsByAccIDAndType(walletsObj);//here needs accID And MainWalletTypes
            String frmGenkeys = encryptionFile.getencdsckey("8886954131");
            String bkbalstr = encryptionFile.decryptObject(frmGenkeys, encryptionFile.encGenKey, myWallets.getWalletBalance());
            Double addbal = Double.parseDouble(walletsObj.getWalletBalance());//direct set to str
            if ((bkbalstr != null || !bkbalstr.equals("")) && ((walletsObj.getWalletBalance() != null) && (addbal >= 0))) {
                Double fromBal = Double.parseDouble(bkbalstr);
                if (fromBal >= 0) {
                    fromBal = fromBal + addbal;
                    myWallets.setWalletBalance(encryptionFile.encrypt(frmGenkeys, encryptionFile.encGenKey, fromBal.toString()));
                    Wallet obj = vGWalletDao.save(myWallets);
                    if (obj != null) {
                        statuResponse.setStatusCode(0);
                    } else {
                        statuResponse.setStatusCode(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            statuResponse.setStatusCode(1);
            logger.error("VGWalletServiceImpl.class", "updateVGWallet()", "Please Try After Sometime" + e.getMessage());
        }
        return statuResponse;
    }

    @Override
    public Double applicableCharges(Double amt) {
        Double fees = 0.0;
        try {
            List<ApplicableCharges> list = applicableChargesDaoDao.findAll();
            if (list != null || !list.isEmpty()) {
                for (ApplicableCharges applicableCharges : list) {
                    if (amt > applicableCharges.getFromAmount() && amt < applicableCharges.getToAmount()) {
                        fees = applicableCharges.getCharges();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("VGWalletServiceImpl.class", "applicableCharges()", "Please Try After Sometime" + e.getMessage());
        }
        return fees;
    }
}
