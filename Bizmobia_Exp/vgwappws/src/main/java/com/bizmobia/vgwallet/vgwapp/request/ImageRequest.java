package com.bizmobia.vgwallet.vgwapp.request;

import lombok.Data;

/**
 *
 * @author bizmobia1
 */
@Data
public class ImageRequest {

    private String filename;
    
    private String filetype;
    
    private String value;
    
    private String imagefileName;//check which type of image is uploaded
    
    private String originalwidth;
    
    private String originalheight;

}
