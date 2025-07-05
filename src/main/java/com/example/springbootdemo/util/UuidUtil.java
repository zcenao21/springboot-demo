package com.example.springbootdemo.util;

import java.util.UUID;

public class UuidUtil {

    public static String getUUid(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
