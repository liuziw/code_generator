package com.lzw.geverator.util;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;

public class Utils {


    public static void createFile(String path,Object context,String templateFileName){
        try {
            PrintWriter printWriter=new PrintWriter(path);
            printWriter.println(createContentByTemplate(context,templateFileName));
            printWriter.close();
        }
        catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void createFileExistsNull(String path,Object context,String templateFileName){
        if(new File(path).exists()){
            return;
        }
        createFile(path, context, templateFileName);
    }

    public static String createContentByTemplate(Object context,String templateFileName) throws Exception
    {
        Configuration configuration=new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        configuration.setDefaultEncoding("utf8");
        ClassTemplateLoader templateLoader=new ClassTemplateLoader(Utils.class,"/");
        configuration.setTemplateLoader(templateLoader);
        Template template = configuration.getTemplate(templateFileName);
        ByteArrayOutputStream sb=new ByteArrayOutputStream();
        Writer writer=new OutputStreamWriter(sb);
        template.process(context,writer);
        System.out.println(sb);
        return sb.toString("utf8");
    }


    public static String package2Path(String packageName){
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<packageName.length();i++){
            char c=packageName.charAt(i);
            if(c=='.'){
                sb.append(File.separatorChar);
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
