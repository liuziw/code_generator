package com.lzw.geverator.util;

import com.lzw.geverator.bo.NameBO;
import org.apache.commons.lang3.StringUtils;

public class NameUtils {



    /**
     * 将数据库的名称信息转换成DO对象的名称信息.
     * <PRE>
     *      转换规则:下划线转驼峰,如:tu_e_app=>TuEApp,user_name=>userName
     *      数据库的名称信息包含:
     *          1.表名
     *          2.字段名
     *      DO对象名称信息包含:
     *          1.类名
     *          2.属性名
     * </PRE>
     * @param dbName 数据库的名称信息
     * @param isFirstUpper 首字母是否大写
     * @return
     * @author ashan
     * @date 2017/6/29
     */
    public static String dbName2DOName(String dbName,boolean isFirstUpper){
        if(StringUtils.isBlank(dbName))
        {
            return dbName;
        }
        StringBuilder sb=new StringBuilder();
        char lastChar=dbName.charAt(0);
        //首字母
        sb.append(isFirstUpper?Character.toUpperCase(lastChar):Character.toLowerCase(lastChar));
        for(int i=1;i<dbName.length();i++)
        {
            char c=dbName.charAt(i);
            //如果不划线
            if(c!='_')
            {
                //如果上一字母是下划线,大写,否则下写
                sb.append(lastChar=='_'?Character.toUpperCase(c):Character.toLowerCase(c));
            }
            lastChar=c;
        }
        return sb.toString();
    }


    public static String dbName2ClassName(String dbName)
    {
        return dbName2DOName(dbName,true);
    }

    public static String dbName2FieldName(String dbName)
    {
        return dbName2DOName(dbName,false);
    }

    public static String dbName2GetName(String dbName)
    {
        return "get"+dbName2DOName(dbName,true);
    }

    public static String dbName2SetName(String dbName)
    {
        return "set"+dbName2DOName(dbName,true);
    }

    public static void setNames(NameBO nameBO, String dbName){
        nameBO.setJavaClassName(NameUtils.dbName2ClassName(dbName));
        nameBO.setJavaFieldName(NameUtils.dbName2FieldName(dbName));
        nameBO.setSetMethodName(NameUtils.dbName2SetName(dbName));
        nameBO.setGetMethodName(NameUtils.dbName2GetName(dbName));
        nameBO.setDataBaseName(dbName);
    }
}
