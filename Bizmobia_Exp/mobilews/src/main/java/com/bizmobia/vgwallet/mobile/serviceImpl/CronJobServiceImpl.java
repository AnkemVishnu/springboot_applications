package com.bizmobia.vgwallet.mobile.serviceImpl;

import com.bizmobia.vgwallet.mobile.dao.DevicesDao;
import com.bizmobia.vgwallet.mobile.dao.KycDao;
import com.bizmobia.vgwallet.mobile.dao.RequestMoneyDao;
import com.bizmobia.vgwallet.mobile.service.CronJobService;
import com.bizmobia.vgwallet.mobile.service.StatusService;
import com.bizmobia.vgwallet.mobile.utilities.AndroidPushNotification;
import com.bizmobia.vgwallet.mobile.utilities.DateFormate;
import com.bizmobia.vgwallet.mobile.utilities.IosPushNotification;
import com.bizmobia.vgwallet.mobile.utilities.SmsSender;
import com.bizmobia.vgwallet.models.Devices;
import com.bizmobia.vgwallet.models.Kyc;
import com.bizmobia.vgwallet.models.RequestMoney;
import com.bizmobia.vgwallet.response.ResponseModel;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
public class CronJobServiceImpl implements CronJobService {

    private static final Logger logger = LoggerFactory.getLogger(CronJobServiceImpl.class);

    @Autowired
    private KycDao kycDao;

    @Autowired
    private DevicesDao deviceDao;

    @Autowired
    private RequestMoneyDao requestMoneyDao;

    @Autowired
    private StatusService statusService;

    @Override
    public ResponseModel fullKycCheck() {
        ResponseModel statuResponse = new ResponseModel();
        try {
            List<Kyc> list = kycDao.fullKycCheck();
            if (!list.isEmpty()) {
                String notificationtype = "notification";
                String title = "Notification";
                String description;
                Devices device;
                for (Kyc kyc : list) {
                    description = kyc.getFullname() + "(" + kyc.getMobilenumber() + ") Please Complete Your KYC \n" + "Regards Virtual Goal Wallet.";

                    SmsSender.sendSms(kyc.getMobilenumber(), description);

                    device = deviceDao.getDeviceByMobileNo(kyc);

                    if (device != null) {
                        if (device.getDeviceMaker().equals("IOS")) {
                            IosPushNotification.sendNotificationToIOS(device.getDeviceToken(), title, description, notificationtype, "");
                        } else {
                            AndroidPushNotification.sendNotificationToAndroid(title, description, device.getDeviceToken(), notificationtype, "");
                        }
                    }
                }
                statuResponse.setMessage("User With No Full Kyc Are Notified Successfully");
                statuResponse.setStatusCode(0);
                logger.info("CronJobServiceImpl.class", "fullKycCheck()", "User With No Full Kyc Are Notified Successfully");
            } else {
                statuResponse.setMessage("All Users Kyc Procedure Is Complete");
                statuResponse.setStatusCode(2);
                logger.error("CronJobServiceImpl.class", "fullKycCheck()", "All Users Kyc Procedure Is Complete");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            statuResponse.setStatusCode(1);
            logger.error("CronJobServiceImpl.class", "fullKycCheck()", "Fail to Login" + e.getMessage());
        }
        return statuResponse;
    }

    @Override
    public ResponseModel requestMoneyCheck() {
        ResponseModel statuResponse = new ResponseModel();
        try {
            List<RequestMoney> listA = requestMoneyDao.getAllActiveRequestMoney();
            List<RequestMoney> listE = requestMoneyDao.getAllExpiredRequestMoney();

            Calendar current = DateFormate.getCalendarForNowdate(new Date());
            Calendar rawdate;
            int date;
            if (!listA.isEmpty()) {
                for (RequestMoney rmA : listA) {
                    rawdate = DateFormate.getCalendarForNowdate(rmA.getCreatedDate());
                    rawdate.add(Calendar.MINUTE, 10);
                    date = current.getTime().compareTo(rawdate.getTime());
                    if (date < 0) {
                        rmA.setStatus(statusService.getStatusByName("Expired"));
                        requestMoneyDao.save(rmA);
                    }
                }
            }
            if (!listE.isEmpty()) {
                for (RequestMoney rmE : listE) {
                    rawdate = DateFormate.getCalendarForNowdate(rmE.getCreatedDate());
                    rawdate.add(Calendar.DATE, 5);
                    date = current.getTime().compareTo(rawdate.getTime());
                    if (date < 0) {
                        requestMoneyDao.delete(rmE);
                    }
                }
            }
            statuResponse.setMessage("User With No Full Kyc Are Notified Successfully");
            statuResponse.setStatusCode(0);
            logger.info("CronJobServiceImpl.class", "requestMoneyCheck()", "User With No Full Kyc Are Notified Successfully");
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            statuResponse.setStatusCode(1);
            logger.error("CronJobServiceImpl.class", "requestMoneyCheck()", "Fail to Login" + e.getMessage());
        }
        return statuResponse;
    }

}
