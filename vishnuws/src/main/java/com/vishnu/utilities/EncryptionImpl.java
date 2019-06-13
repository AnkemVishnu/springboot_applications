package com.vishnu.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import org.springframework.stereotype.Service;

@Service
public class EncryptionImpl implements Encryption {

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    @Override
    public String generateEncryptedKey(String mobileNumber) {
        try {
            int len = mobileNumber.length();
            int remains = 16 - len;
            int val;
            String abc, abc2;
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
            String ss = getHashingvaluemd5_512(pwdno);
            StringBuilder gendynamikey = new StringBuilder();
            for (int i = 0; i < pwdno.length(); i++) {
                try {
                    int p = Integer.parseInt(pwdno.charAt(i) + "") + i;
                    gendynamikey.append(ss.charAt(p)).append("");
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            return gendynamikey.toString() + "vishnu" + abc + abc2;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String encrypt(String key, String initVector, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            System.out.println("encrypted string: " + Base64.encodeBase64String(encrypted));

            return gson.toJson(Base64.encodeBase64String(encrypted)).replace("\"", "");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String reGenerateEncryptedKey(String mobileNumber, String autho_token) {
        try {
            int len = mobileNumber.length();
            String requiredNumber = autho_token.substring(0, len);
            int remains = requiredNumber.length();
            int val;
            String abc, abc2;
            if (remains % 2 == 0) {
                val = remains / 2;
                abc = requiredNumber.substring(0, val);
                abc2 = requiredNumber.substring(val, len);
            } else {
                val = remains / 2;
                abc = requiredNumber.substring(0, val);
                abc2 = requiredNumber.substring(val, len);
            }
            String pwdno = abc + mobileNumber + abc2;
            String ss = getHashingvaluemd5_512(pwdno);
            StringBuilder gendynamikey = new StringBuilder();
            for (int i = 0; i < pwdno.length(); i++) {
                try {
                    int p = Integer.parseInt(pwdno.charAt(i) + "") + i;
                    gendynamikey.append(ss.charAt(p)).append("");
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            return gendynamikey.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String decrypt(String generatedKey, String initVector, String encrypted, int len) {
        int frm = 16 - len;
        String encryptedAuthToken = encrypted.substring(frm, encrypted.length());
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(generatedKey.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encryptedAuthToken));

            return new String(original);
        } catch (UnsupportedEncodingException | InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getHashingvaluemd5_512(String mob) {
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(mob.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            //convert the byte to hex format method 2
            for (int i = 0; i < byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hexString.toString();
    }
}
