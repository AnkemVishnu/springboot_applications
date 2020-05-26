/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vishnu.utilities;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author bizmobia1
 */
public class OperationValidation {

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
        } catch (UnsupportedEncodingException | InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
            ex.printStackTrace();
            return null;
        }

    }

    public static String decrypt(String generatedKey, String initVector, String encrypted, int frm) {
        String encryptedAuthToken = encrypted.substring(frm, encrypted.length());
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(generatedKey.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encryptedAuthToken));

            return new String(original);
        } catch (UnsupportedEncodingException | InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
            ex.printStackTrace();
        }

        return "";
    }

    public static String generateEncryptedKey(String mobileNumber) {
        try {
            int len = mobileNumber.length();
            int remains = 16 - len;
            int val = 0;
            String abc = "";
            String abc2 = "";
            if (remains % 2 == 0) {
                val = remains / 2;
                abc = RandomStringUtils.randomNumeric(val);
                abc2 = RandomStringUtils.randomNumeric(val);
            } else {
                val = remains / 2;
                abc = RandomStringUtils.randomNumeric(val);
                abc2 = RandomStringUtils.randomNumeric(val + 1);
            }

            String pwdno = abc + mobileNumber + abc2;
//            String pwdno = "123" + mobileNumber + "123";
            System.out.println("Before Length ---- " + pwdno.length());
            String ss = getHashingvaluemd5_512(pwdno);
            StringBuffer gendynamikey = new StringBuffer();
            for (int i = 0; i < pwdno.length(); i++) {
                try {
                    int p = Integer.parseInt(pwdno.charAt(i) + "") + i;
                    gendynamikey.append(ss.charAt(p) + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//            String frm = String.valueOf(remains);
            System.out.println("After Length ---- " + gendynamikey.toString().length());
            return gendynamikey.toString() + "bizm" + abc + abc2;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String reGenerateEncryptedKey(String mobileNumber, String autho_token) {
        int till = 16 - mobileNumber.length();
        String requiredNumber = autho_token.substring(0, till);

        try {
            int remains = requiredNumber.length();
//            int remains = 16 - len;
            int val = 0;
            String abc = "";
            String abc2 = "";
            if (remains % 2 == 0) {
                val = remains / 2;
                abc = requiredNumber.substring(0, val);
                abc2 = requiredNumber.substring(val, till);
            } else {
                val = remains / 2;
                abc = requiredNumber.substring(0, val);
                abc2 = requiredNumber.substring(val, till);
            }

//            abc = requiredNumber.substring(0, 3);
//            abc2 = requiredNumber.substring(3, 7);
//              String pwdno = "123" + mobileNumber + "123";
            String pwdno = abc + mobileNumber + abc2;
            System.out.println("Before Length ---- " + pwdno.length());
            String ss = getHashingvaluemd5_512(pwdno);
            StringBuffer gendynamikey = new StringBuffer();
            for (int i = 0; i < pwdno.length(); i++) {
                try {
                    int p = Integer.parseInt(pwdno.charAt(i) + "") + i;
                    gendynamikey.append(ss.charAt(p) + "");
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("After Length ---- " + gendynamikey.toString().length());
//            return gendynamikey.toString()+"bizm"+abc+abc2;
            return gendynamikey.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getHashingvaluemd5_512(String mob) {
        StringBuffer hexString = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(mob.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            System.out.println("Digest(in hex format):: " + sb.toString());

            //convert the byte to hex format method 2
            //def9fd27656380d5e401ea0d2a0bb89c
            for (int i = 0; i < byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hexString.toString();
    }

}
