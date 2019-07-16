package com.lzw.geverator.manager;

import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.Properties;

public abstract class BaseTableManager implements TableManager {

    private String url;

    private String driverClass;

    private String user;

    private String password;




    protected Connection getConn(){
        try {
            Class.forName(this.driverClass);
            Properties props =new Properties();
            props.put("user", user);
            props.put("password", password);
            props.put("remarksReporting","true");
            return DriverManager.getConnection(url,props);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    protected void close(Connection con){
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }



    protected void println(ResultSet rs){
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            while(rs.next()){
                int columnCount = metaData.getColumnCount();
                System.out.println("###################################");
                for(int i=1;i<=columnCount;i++){
                    String name=metaData.getColumnName(i);
                    Object value=rs.getObject(name);
                    System.out.println(StringUtils.rightPad(name,30)+":"+value);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }





    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
