package com.bizmobia.vgwallet.vgwapp.websocket;

import com.google.gson.Gson;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 *
 * @author Vishnu
 */
public class MessageDecoder implements Decoder.Text<Messenger> {
 
    private static Gson gson = new Gson();
 
    @Override
    public Messenger decode(String s) throws DecodeException {
        return gson.fromJson(s, Messenger.class);
    }
 
    @Override
    public boolean willDecode(String s) {
        return (s != null);
    }
 
    @Override
    public void init(EndpointConfig endpointConfig) {
        // Custom initialization logic
    }
 
    @Override
    public void destroy() {
        // Close resources
    }
}