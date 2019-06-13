package com.vishnu.utilities;

public interface Encryption {

    public static final String encGenKey = "ankemvenkatavishnuchaitanya";

    public String generateEncryptedKey(String mobileNumber);

    public String encrypt(String key, String initVector, String value);

    public String reGenerateEncryptedKey(String mobileNumber, String autho_token);

    public String decrypt(String generatedKey, String initVector, String encrypted, int frm);

}
