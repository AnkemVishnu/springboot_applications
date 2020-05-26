package com.bizmobia.vgwallet.mobile.utilities;

import com.bizmobia.vgwallet.request.RequestModel;
import com.bizmobia.vgwallet.response.ResponseModel;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Vishnu
 */
public class VGWappwsApi {

    private static ResponseModel respObj;
    private static RestTemplate rt;

    private static String URI = "http://159.65.146.244:8080/vgwappws/";
//    private static String URI = "http://localhost:8084/vgwappws/";

    public static ResponseModel addCustomer(RequestModel requestModel) {
        try {
            rt = new RestTemplate();
            respObj = rt.postForObject(URI + "insertcustomer", requestModel, ResponseModel.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return respObj;
    }

    public static ResponseModel updateCustomer(RequestModel requestModel) {
        try {
            rt = new RestTemplate();
            respObj = rt.postForObject(URI + "updatecustomer", requestModel, ResponseModel.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return respObj;
    }

}
