package com.bizmobia.vgwallet.vgwapp.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author bizmobia1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseModel {

    private Integer statusCode;
    
    private String message;
    
    private String authToken;

    private Integer idVariable;
    
    private String extraVariable;
    
    private Object respObject;
    
    private List<?> respList;
    
    private String[] respArray;

    public ResponseModel(String message, Integer statusCode) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public ResponseModel(Integer statusCode, String message, Object respObject) {
        this.statusCode = statusCode;
        this.message = message;
        this.respObject = respObject;
    }

}
