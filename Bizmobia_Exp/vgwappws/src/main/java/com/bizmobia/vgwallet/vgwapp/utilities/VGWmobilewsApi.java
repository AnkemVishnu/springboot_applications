package com.bizmobia.vgwallet.vgwapp.utilities;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Vaibhav
 */
public class VGWmobilewsApi {

    private static String respObj;
    private static RestTemplate rt;
    private static String URI = "http://159.65.146.244:8080/vgwmobilews/";
//    private static String URI = "http://localhost:8084/vgwmobilews/";

//    public static ResponseModel getKycByMobile(String requestModel) {
//        try {
//            rt = new RestTemplate();
//            respObj = rt.postForObject(URI + "getkycbymobile", requestModel, ResponseModel.class);
//        } catch (RestClientException e) {
//            respObj.setStatusCode(1);
//            respObj.setMessage("Exception Form API Call");
//        }
//        return respObj;
//    }
    
    public static String webRegister(String requestModel) {
        try {
            rt = new RestTemplate();
            respObj = rt.postForObject(URI + "webregistration", requestModel, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return respObj;
    }
    
}
