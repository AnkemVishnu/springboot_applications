package com.bizmobia.vgwallet.vgwapp.websocket;

import com.bizmobia.vgwallet.vgwapp.response.ResponseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Vishnu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Messenger {

    private Integer statusCode;
    private String message;
    private String session;
    private String uniqueId;
    private ResponseModel response;

}
