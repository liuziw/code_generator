package com.lzw.geverator.manager;

import com.lzw.geverator.bo.EnumBO;
import com.lzw.geverator.bo.EnumItemBO;
import com.lzw.geverator.bo.FieldBO;
import com.lzw.geverator.bo.TableBO;
import com.lzw.geverator.enums.FieldTypeEnum;
import com.lzw.geverator.util.NameUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TableManagerOracleImpl extends BaseTableManager {

    @Override
    public TableBO getTable(String packageName, String tableName) {
        Connection conn=this.getConn();
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getTables(null, "%", StringUtils.upperCase(tableName), new String[]{"TABLE"});
            //super.println(rs);
           // rs = metaData.getTables(null, "%", StringUtils.upperCase(tableName), new String[]{"TABLE"});
            TableBO tableBO=null;
            if(rs.next()){
                tableBO=new TableBO();
                tableBO.setPackageName(packageName);
                String tName= StringUtils.lowerCase(rs.getString("TABLE_NAME"));
                tableBO.setTableName(tName);
                tableBO.setDataBaseName(tName);
                NameUtils.setNames(tableBO,tName);
                tableBO.setDbType("oracle");
                tableBO.setCurrentTime("sysdate");
            }
            else{
                throw new RuntimeException("没有找到表名");
            }
            rs=metaData.getColumns(null,"%",StringUtils.upperCase(tableName),"%");
            //super.println(rs);
           // rs=metaData.getColumns(null,"%",StringUtils.upperCase(tableName),"%");
            tableBO.setFields(new ArrayList<>());
            tableBO.setEnums(new ArrayList<>());
            tableBO.setBusinessFields(new ArrayList<>());
            while(rs.next()){
                String columnName=StringUtils.lowerCase(rs.getString("COLUMN_NAME"));
                Integer columnType=rs.getInt("DATA_TYPE");
                String remark=rs.getString("REMARKS");
                if(remark==null) remark="";
                remark=remark.replaceAll("\\s","");
                String typeName=rs.getString("TYPE_NAME");
                int DECIMAL_DIGITS=rs.getInt("DECIMAL_DIGITS");
                int COLUMN_SIZE=rs.getInt("COLUMN_SIZE");
                if(StringUtils.equalsIgnoreCase("number",typeName)){
                    if(DECIMAL_DIGITS>0){
                        columnType= Types.DOUBLE;
                    }
                    else if(COLUMN_SIZE>=10){
                        columnType= Types.BIGINT;
                    }
                    else{
                        columnType= Types.INTEGER;
                    }
                }

                Boolean autoIncrement=rs.getBoolean("IS_AUTOINCREMENT");
                FieldBO fieldBO=new FieldBO();

                fieldBO.setFieldName(columnName);
                NameUtils.setNames(fieldBO,columnName);
                fieldBO.setFieldDesc(remark);
                fieldBO.setType(FieldTypeEnum.getType(columnType));
                fieldBO.setAutoIncrement(autoIncrement==null?false:autoIncrement.booleanValue());
                tableBO.getFields().add(fieldBO);
                if(StringUtils.equalsIgnoreCase(fieldBO.getFieldName(),"id")){
                    tableBO.setPrimaryKey(fieldBO);
                }



                //枚举
                if(StringUtils.contains(remark,"枚举定义") && !StringUtils.equals("isDeleted",fieldBO.getJavaFieldName()))
                {
                    EnumBO enumBO=new EnumBO();
                    enumBO.setDataBaseName(columnName);
                    NameUtils.setNames(enumBO,columnName);
                    enumBO.setPackageName(tableBO.getPackageName());
                    enumBO.setEnumName(enumBO.getJavaClassName());
                    enumBO.setItems(new ArrayList<>());
                    enumBO.setFieldType(fieldBO.getType());
                    tableBO.getEnums().add(enumBO);

                    String reg="[\\w]+\\([\\w]{1,1000},[\\S]+\\)[;|；]*";
                    Pattern r = Pattern.compile(reg);
                    Matcher m = r.matcher(remark);
                    if(m.find())
                    {
                        String string=m.group(0);

                        String[] ss=StringUtils.split(string,";");
                        if(ss==null||ss.length==1)
                        {
                            ss=StringUtils.split(string,"；");
                        }

                        for(String content:ss)
                        {
                            EnumItemBO enumItemBO=new EnumItemBO();
                            String tmp=StringUtils.substringBefore(content,"(");
                            enumItemBO.setItemName(tmp);
                            String codeDesc=StringUtils.substringBetween(content,"(",")");
                            enumItemBO.setItemCode(StringUtils.substringBefore(codeDesc,","));
                            enumItemBO.setItemDesc(StringUtils.substringAfter(codeDesc,","));
                            if(fieldBO.getType().equals(FieldTypeEnum.String))
                            {
                                enumItemBO.setStringCode("\""+enumItemBO.getItemCode()+"\"");
                            }
                            else
                            {
                                enumItemBO.setStringCode(enumItemBO.getItemCode());
                            }
                            enumBO.getItems().add(enumItemBO);
                        }
                    }
                }
            }
            System.out.println(tableBO);
            tableBO.setBusinessFields(new ArrayList<>());
            for(FieldBO fieldBO:tableBO.getFields()){
                if(!StringUtils.equalsIgnoreCase(fieldBO.getJavaFieldName(),"id")&&
                        !StringUtils.equalsIgnoreCase(fieldBO.getJavaFieldName(),"gmtCreate")&&
                        !StringUtils.equalsIgnoreCase(fieldBO.getJavaFieldName(),"gmtModified")&&
                        !StringUtils.equalsIgnoreCase(fieldBO.getJavaFieldName(),"isDeleted")){
                    tableBO.getBusinessFields().add(fieldBO);
                }
            }
            if(tableBO.getFields().size()-tableBO.getBusinessFields().size()!=4){
                throw new RuntimeException("必须要如下四个字段：id,gmtCreate,gmtModified,isDeleted");
            }


            return tableBO;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally {
            super.close(conn);
        }
    }
}
