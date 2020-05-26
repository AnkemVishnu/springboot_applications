package com.bizmobia.vgwallet.vgwapp.request;

import com.bizmobia.vgwallet.vgwapp.response.OtpStatus;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuccessOtpResponse implements Serializable {

    private String to;
    
    private String smsCount;
    
    private String messageId;
    
    private OtpStatus status;

}
