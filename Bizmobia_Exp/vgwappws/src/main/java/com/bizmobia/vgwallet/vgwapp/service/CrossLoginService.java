package com.bizmobia.vgwallet.vgwapp.service;

import com.bizmobia.vgwallet.vgwapp.response.ResponseModel;

/**
 *
 * @author Vishnu
 */
public interface CrossLoginService {

    public String crossLoginRequest(String autho_token, String request);

    public ResponseModel crossLoginCheck();
}
