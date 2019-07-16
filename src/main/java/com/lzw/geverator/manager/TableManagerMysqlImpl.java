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

public class TableManagerMysqlImpl extends BaseTableManager {


    @Override
    public TableBO getTable(String packageName, String tableName) {
        Connection conn=this.getConn();
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            String dataBaseName = conn.getCatalog();
            //查询表注释的语句
            String sql = "SELECT TABLE_COMMENT FROM information_schema.TABLES WHERE  table_schema = '"+dataBaseName+"' AND table_name='"+tableName+"'";
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);
            ResultSet rs = metaData.getTables(null, "%", tableName, new String[]{"TABLE"});
            TableBO tableBO=null;
            if(rs.next() && resultSet != null && resultSet.next()){
                tableBO=new TableBO();
                tableBO.setPackageName(packageName);
                String tName=StringUtils.lowerCase(rs.getString("TABLE_NAME"));
                tableBO.setTableName(tName);
                tableBO.setDataBaseName(tName);
                NameUtils.setNames(tableBO,tName);
                String tableComment = StringUtils.lowerCase(resultSet.getString("TABLE_COMMENT"));
                tableBO.setTableComment(tableComment);
            }
            else{
                throw new RuntimeException("没有找到表名");
            }






            rs=metaData.getColumns(null,"%",tableName,"%");
            tableBO.setFields(new ArrayList<>());
            tableBO.setEnums(new ArrayList<>());
            tableBO.setBusinessFields(new ArrayList<>());
            tableBO.setDbType("mysql");
            tableBO.setCurrentTime("CURRENT_TIMESTAMP");
            while(rs.next()){

                String columnName=StringUtils.lowerCase(rs.getString("COLUMN_NAME"));
                Integer columnType=rs.getInt("DATA_TYPE");
                String remark=rs.getString("REMARKS");
                if(remark==null) remark="";
                remark=remark.replaceAll("\\s","");
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




                boolean bool=isEnum(fieldBO.getJavaFieldName(),remark);

                //枚举
                if(bool)
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


            rs=metaData.getPrimaryKeys(conn.getCatalog(),null,tableBO.getTableName());
            //println(metaData.getPrimaryKeys(conn.getCatalog(),null,tableBO.getTableName()));
            if(rs.next()){
                String pkName=rs.getString("COLUMN_NAME");
                for(FieldBO fieldBO:tableBO.getFields()){
                    if(StringUtils.equalsIgnoreCase(pkName,fieldBO.getFieldName())){
                        tableBO.setPrimaryKey(fieldBO);
                        break;
                    }
                }
            }
            else{
                throw  new RuntimeException("该表没有主键");
            }

            tableBO.setPrimaryKeyName(tableBO.getPrimaryKey().getFieldName());

            //super.println(rs);
            System.out.println(tableBO);
            tableBO.setBusinessFields(new ArrayList<>());
            for(FieldBO fieldBO:tableBO.getFields()){


                if(
                        !StringUtils.equalsIgnoreCase(fieldBO.getJavaFieldName(),"gmtCreate")&&
                        !StringUtils.equalsIgnoreCase(fieldBO.getJavaFieldName(),"gmtModified")&&
                        !StringUtils.equalsIgnoreCase(fieldBO.getJavaFieldName(),"isDeleted")){
                    tableBO.getBusinessFields().add(fieldBO);
                }
            }

            if(tableBO.getFields().size()-tableBO.getBusinessFields().size()!=3){
                throw new RuntimeException("必须要如下四个字段：gmtCreate,gmtModified,isDeleted");
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


    private boolean isEnum(String fieldName,String remark) {
        if(StringUtils.equalsIgnoreCase("isDeleted",fieldName)){
            return false;
        }
        boolean bool=StringUtils.contains(remark,"枚举定义");
        return bool;
    }


    public static void main(String[] args){
        TableManagerMysqlImpl tableManagerMysql=new TableManagerMysqlImpl();
        tableManagerMysql.setDriverClass("com.mysql.jdbc.Driver");
        tableManagerMysql.setPassword("admin@1");
        tableManagerMysql.setUser("gdmzdev");
        tableManagerMysql.setUrl("jdbc:mysql://192.168.10.214:9090/gdga_jpms?allowMultiQueries=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull");
        tableManagerMysql.getTable("com.hyzs.gz.test","word_data");
    }
}
