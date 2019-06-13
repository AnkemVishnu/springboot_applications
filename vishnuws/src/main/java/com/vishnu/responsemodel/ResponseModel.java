package com.vishnu.responsemodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseModel {

    private Integer statusCode;
    private String message;
    private Object respObject;

    public ResponseModel(Integer statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

}
