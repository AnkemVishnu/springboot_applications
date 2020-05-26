package com.bizmobia.vgwallet.mobile.service;

/**
 *
 * @author Vishnu
 */
public interface BillPaymentService {

    public String checkElectricityBill(String autho_token, String requestModel);
    
    public String electricityBillPayment(String autho_token, String requestModel);
    
    public String checkWaterBill(String autho_token, String requestModel);
    
    public String waterBillPayment(String autho_token, String requestModel);

}
