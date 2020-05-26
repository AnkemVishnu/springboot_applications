package com.vishnu.utilities;

import org.apache.commons.lang3.RandomStringUtils;

public class GetMethodsClass {

    public static String getUniqueCode(String name) {
        String nfc;
        if (name.contains(" ")) {
            String[] ses = name.split("\\s");
            if (ses[0].length() >= 3) {
                nfc = ses[0].substring(0, 1);
            } else if (ses[0].length() >= 2) {
                nfc = ses[0].substring(0, 1);
            } else {
                nfc = ses[0].substring(0, 1);
            }

            if (ses[1].length() >= 3) {
                nfc = nfc + ses[1].substring(0, 1);
            } else if (ses[1].length() >= 2) {
                nfc = nfc + ses[1].substring(0, 1);
            } else {
                nfc = nfc + ses[1].substring(0, 1);
            }
        } else {
            if (name.length() >= 3) {
                nfc = name.substring(0, 2);
            } else if (name.length() >= 2) {
                nfc = name.substring(0, 2);
            } else {
                nfc = name.substring(0, 2);
            }
        }
        String unqno = "ViX" + nfc + RandomStringUtils.randomAlphanumeric(8);
        return unqno;
    }
}
