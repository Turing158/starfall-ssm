package com.starfall.Util;

import java.util.Random;

public class GetCode {
    public String getcode(){
        char[] num_letter = "abcdefghijklmnobqrstuvwxyz23456789".toCharArray();
        Random r = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(num_letter[r.nextInt(num_letter.length)]);
        }
        return code.toString();
    }
}
