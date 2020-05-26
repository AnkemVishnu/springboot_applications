package com.vishnu.utilities;

import com.bizmobia.vgwallet.models.City;
import com.bizmobia.vgwallet.models.State;
import com.bizmobia.vgwallet.request.RequestModel;
import com.google.gson.Gson;
import java.util.Date;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Vishnu
 */
public class Encryptor {

    public static String encrypt(String key, String initVector, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            System.out.println("encrypted string: "
                    + Base64.encodeBase64String(encrypted));

            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String decrypt(String key, String initVector, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
//        String key = "Bar12345Bar12345"; // 128 bit key
//        String initVector = "RandomInitVector"; // 16 bytes IV

        String key = "Biz@123Bizm@bia1"; // 128 bit key
        String initVector = "bizmobiabizmobia"; // 16 bytes IV

        RequestModel req = new RequestModel();
        req.setUserId("8886954131");

        City c = new City();
        c.setCityName("aaaaa");
        c.setCreatedDate(new Date());
        State s = new State();
        s.setStateId(1);
        c.setStateObj(s);
        c.setLatitude(11.0);
        c.setLongitude(12.0);
        req.setReqObject(c);
//        System.out.println(decrypt(key, initVector,
//                encrypt(key, initVector, "Hello World")));
        System.out.println(encrypt(key, initVector, gson.toJson(req)));
        String encObj = encrypt(key, initVector, req.toString());
//        key = key + "f";
        System.out.println(decrypt(key, initVector,
                encObj));
    }
}
