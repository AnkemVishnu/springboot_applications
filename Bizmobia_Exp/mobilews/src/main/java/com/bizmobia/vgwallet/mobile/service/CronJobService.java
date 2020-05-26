package com.bizmobia.vgwallet.mobile.service;

import com.bizmobia.vgwallet.response.ResponseModel;

/**
 *
 * @author Vishnu
 */
public interface CronJobService {

    public ResponseModel fullKycCheck();
    
    public ResponseModel requestMoneyCheck();

}
