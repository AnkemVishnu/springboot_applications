package com.bizmobia.vgwallet.vgwapp.request;

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
public class RequestModel {

    private String userId;
    
    private Object userType;

    private Integer idVariable;
    
    private String extraVariable;
    
    private Object reqObject;
    
    private List<?> reqList;
    
    private String[] reqArray;

}
