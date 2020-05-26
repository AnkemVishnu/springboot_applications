package com.bizmobia.vgwallet.mobile.utilities;

import com.bizmobia.vgwallet.response.Message;
import com.bizmobia.vgwallet.response.OtpInput;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class Consumption {

    private static String authtoken = "Basic VmlydHVhbEdvYWw6c05EMmIyLl8=";

    private static HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", authtoken);
        return headers;
    }

    public static Message verifyOtp(OtpInput otpInput) {
        Message message;
        try {
            HttpHeaders headers = createHttpHeaders();
//            RequestModel respObj = new RequestModel();
//            respObj.setUserId(mobilenumber);
//            respObj.setRespOject(otpVerificationRequest);
            String uri = "https://wnej8.api.infobip.com/sms/1/text/single";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            HttpEntity<OtpInput> request = new HttpEntity<>(otpInput, headers);
            message = restTemplate.postForObject(uri, request, Message.class);
        } catch (Exception e) {
            message = new Message();
            e.printStackTrace();
        }
        return message;
    }
}
