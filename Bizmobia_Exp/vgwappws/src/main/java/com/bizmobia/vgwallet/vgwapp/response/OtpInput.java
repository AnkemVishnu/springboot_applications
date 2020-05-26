package com.bizmobia.vgwallet.vgwapp.response;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpInput implements Serializable {

    private String from;
    
    private String to;
    
    private String text;

}
