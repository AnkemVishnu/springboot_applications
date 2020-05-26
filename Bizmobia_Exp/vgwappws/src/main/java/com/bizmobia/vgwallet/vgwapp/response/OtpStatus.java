package com.bizmobia.vgwallet.vgwapp.response;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpStatus implements Serializable {

    private String groupId;
    
    private String groupName;
    
    private String id;
    
    private String name;
    
    private String description;
    
    private String action;

}
