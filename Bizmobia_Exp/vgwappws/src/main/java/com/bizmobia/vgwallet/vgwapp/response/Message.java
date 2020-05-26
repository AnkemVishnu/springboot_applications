package com.bizmobia.vgwallet.vgwapp.response;

import com.bizmobia.vgwallet.vgwapp.request.SuccessOtpResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author BizMobia23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private List<SuccessOtpResponse> messages;

}
