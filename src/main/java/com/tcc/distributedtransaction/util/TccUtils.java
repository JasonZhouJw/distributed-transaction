package com.tcc.distributedtransaction.util;

public class TccUtils {

    public static String getBeanName(Class clazz) {
        String name = clazz.getSimpleName();
        if (clazz.isInterface() && name.startsWith("I")) {
            name = name.substring(1);
        }
        int upperIndex = 0;
        while (true) {
            char oneChar = name.charAt(upperIndex);
            if (oneChar < 'A' || oneChar > 'Z') {
                break;
            }
            upperIndex++;
        }
        return name.substring(0, upperIndex).toLowerCase() + name.substring(upperIndex);
    }
}
