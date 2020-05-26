package com.bizmobia.vgwallet.vgwapp.serviceImpl;

import com.bizmobia.vgwallet.vgwapp.service.EncryptionFile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author Vaibhav
 */
@Service
public class EncryptionFileImpl implements EncryptionFile {

    private static final Logger logger = LoggerFactory.getLogger(EncryptionFileImpl.class);

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    @Override
    public boolean validatePassword(String originalPassword, String storedPassword) {

        try {
            String[] parts = storedPassword.split(":");
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = fromHex(parts[1]);
            byte[] hash = fromHex(parts[2]);

            PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);

            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            byte[] testHash = skf.generateSecret(spec).getEncoded();

            int diff = hash.length ^ testHash.length;

            for (int i = 0; i < hash.length && i < testHash.length; i++) {
                diff |= hash[i] ^ testHash[i];
            }
            return diff == 0;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            logger.error("EncryptionFileImpl.class", "validatePassword()", "Plese try after Sometime " + e.getMessage());
            return false;
        }

    }

    @Override
    public String generateStorngPasswordHash(String password) {
        String str = "";
        try {
            int iterations = 1000;
            char[] chars = password.toCharArray();
            byte[] salt = getSalt().getBytes();

            PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            str = iterations + ":" + toHex(salt) + ":" + toHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            logger.error("EncryptionFileImpl.class", "generateStorngPasswordHash()", "Plese try after Sometime " + e.getMessage());
        }
        return str;
    }

    private static String getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt.toString();
    }

    private static String toHex(byte[] array) throws NoSuchAlgorithmException {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

    private static byte[] fromHex(String hex) throws NoSuchAlgorithmException {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    @Override
    public String encrypt(String key, String initVector, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            System.out.println("encrypted string: "
                    + Base64.encodeBase64String(encrypted));

//            return gson.toJson(Base64.encodeBase64String(encrypted));
            String s2 = gson.toJson(Base64.encodeBase64String(encrypted).toString());
            return s2 = s2.replace("\"", "");

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("EncryptionFileImpl.class", "encrypt()", "Plese try after Sometime " + e.getMessage());
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
            logger.error("EncryptionFileImpl.class", "decrypt()", "Plese try after Sometime " + e.getMessage());
        }

        return null;
    }

    @Override
    public String decryptObject(String key, String initVector, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("EncryptionFileImpl.class", "decryptObject()", "Plese try after Sometime " + e.getMessage());
        }

        return null;
    }

    @Override
    public String getHashingvaluemd5_512(String mob) {
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
            logger.error("EncryptionFileImpl.class", "getHashingvaluemd5_512()", "Plese try after Sometime " + e.getMessage());
        }
        return hexString.toString();
    }

    @Override
    public String getencdsckey(String mob) {
        try {
            String pwdno = "123" + mob + "213";
            String ss = getHashingvaluemd5_512(pwdno);
            StringBuffer gendynamikey = new StringBuffer();
            for (int i = 0; i < 16; i++) {
                int p = Integer.parseInt(pwdno.charAt(i) + "") + i;
                gendynamikey.append(ss.charAt(p) + "");
            }
            return gendynamikey.toString();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("EncryptionFileImpl.class", "getencdsckey()", "Plese try after Sometime " + e.getMessage());
        }
        return null;
    }

    @Override
    public String generateEncryptedKey(String mobileNumber) {
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
                    logger.error("EncryptionFileImpl.class", "generateEncryptedKey()", "Plese try after Sometime " + e.getMessage());
                }
            }
//            String frm = String.valueOf(remains);
            System.out.println("After Length ---- " + gendynamikey.toString().length());
            return gendynamikey.toString() + "bizm" + abc + abc2;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("EncryptionFileImpl.class", "generateEncryptedKey()", "Plese try after Sometime " + e.getMessage());
            return null;
        }
    }

    @Override
    public String reGenerateEncryptedKey(String mobileNumber, String autho_token) {
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
                    logger.error("EncryptionFileImpl.class", "reGenerateEncryptedKey()", "Plese try after Sometime " + e.getMessage());
                }
            }
            System.out.println("After Length ---- " + gendynamikey.toString().length());
//            return gendynamikey.toString()+"bizm"+abc+abc2;
            return gendynamikey.toString();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("EncryptionFileImpl.class", "reGenerateEncryptedKey()", "Plese try after Sometime " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean isUserAcess(String userId, String encGenKey, String autho_token) {
        return decrypt(reGenerateEncryptedKey(userId, autho_token)/*key*/, encGenKey, autho_token, userId.length()).equals(userId);
    }

    @Override
    public Double decriptWalletBalance(String mobileNumber, String encGenKey, String encriptedBalance) {
        return Double.valueOf(decryptObject(getencdsckey(mobileNumber), encGenKey, encriptedBalance));
    }

    @Override
    public String encriptWalletBalance(String mobileNumber, String encGenKey, Double actualBalance) {
        return (encrypt(getencdsckey(mobileNumber), encGenKey, actualBalance.toString()));
    }

//     public static void main(String args[]){
//        String pwdno ="1239701513816213";
//       String ss= getHashingvaluemd5_512(pwdno);
//       StringBuffer gendynamikey = new StringBuffer();
//       for(int i=0;i<16;i++) {
//           int p=Integer.parseInt(pwdno.charAt(i)+"")+i;
//           gendynamikey.append(ss.charAt(p)+"");
//       }
//        System.out.println("com.bizmobia.vibkyc.utils.gerenckey.main()"+ss);
//        System.out.println("com.bizmobia.vibkyc.utils.gerenckey.main()"+gendynamikey.toString());
//    }
}
