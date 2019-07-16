package com.lzw.geverator.service;

import com.lzw.geverator.bo.EnumBO;
import com.lzw.geverator.bo.TableBO;
import com.lzw.geverator.manager.BaseTableManager;
import com.lzw.geverator.manager.TableManagerMysqlImpl;
import com.lzw.geverator.manager.TableManagerOracleImpl;
import com.lzw.geverator.util.Utils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Properties;

public class DaoHelperService {


    public void createDOFile(String path, TableBO tableBO){

    }


    public void createFiles(Properties props){
        String path=props.getProperty("path");
        String tables=props.getProperty("tables");
        String packageName=props.getProperty("packageName");
        String url=props.getProperty("url");
        String user=props.getProperty("user");
        String password=props.getProperty("password");
        String driverName=props.getProperty("driverName");
        BaseTableManager baseTableManager=null;
        if(StringUtils.contains(driverName,"mysql")){
            baseTableManager=new TableManagerMysqlImpl();
        }
        else if(StringUtils.contains(driverName,"oracle")){
            baseTableManager=new TableManagerOracleImpl();
        }
        if(baseTableManager==null){
            throw new RuntimeException("不支持的数据库");
        }
        baseTableManager.setDriverClass(driverName);
        baseTableManager.setUrl(url);
        baseTableManager.setPassword(password);
        baseTableManager.setUser(user);


        String javaPath=path+ File.separatorChar+"src"+File.separatorChar+"main"+File.separatorChar+"java";
        String resourcePath=path+ File.separatorChar+"src"+File.separatorChar+"main"+File.separatorChar+"resources";

        //DO对象文件路径
        String doPath=javaPath+File.separatorChar+ Utils.package2Path(packageName)+File.separatorChar+"dbo";
        createDir(doPath);

        //枚举文件路径
        String enumPath=javaPath+File.separatorChar+ Utils.package2Path(packageName)+File.separatorChar+"enums";
        createDir(enumPath);

        //mapper文件路径
        String mapperPath=javaPath+File.separatorChar+ Utils.package2Path(packageName)+File.separatorChar+"mapper";
        createDir(mapperPath);

        String mapperXmlPath=resourcePath+File.separatorChar+ Utils.package2Path(packageName)+File.separatorChar+"mapper";
        createDir(mapperXmlPath);

        //DaoManager文件路径
        String immutableDaoManagerPath=javaPath+File.separatorChar+ Utils.package2Path(packageName)+File.separatorChar+"daomanager"+File.separatorChar+"immutable";
        createDir(immutableDaoManagerPath);

        String immutableDaoManagerImplPath=javaPath+File.separatorChar+ Utils.package2Path(packageName)+File.separatorChar+"daomanager"+File.separatorChar+"immutable"+File.separatorChar+"impl";
        createDir(immutableDaoManagerImplPath);


        String daoManagerPath=javaPath+File.separatorChar+ Utils.package2Path(packageName)+File.separatorChar+"daomanager";
        createDir(daoManagerPath);

        String daoManagerImplPath=javaPath+File.separatorChar+ Utils.package2Path(packageName)+File.separatorChar+"daomanager"+File.separatorChar+"impl";
        createDir(daoManagerImplPath);


        String daoPath=javaPath+File.separatorChar+ Utils.package2Path(packageName)+File.separatorChar+"dao";
        createDir(daoPath);

        String daoXmlPath=resourcePath+File.separatorChar+ Utils.package2Path(packageName)+File.separatorChar+"dao";
        createDir(daoXmlPath);


        //String dboServicePath=javaPath+File.separatorChar+ Utils.package2Path(packageName)+File.separatorChar+"service"+File.separatorChar+"dbo";
        //createDir(dboServicePath);

        //String dboServiceImplPath=javaPath+File.separatorChar+ Utils.package2Path(packageName)+File.separatorChar+"service"+File.separatorChar+"dbo"+File.separatorChar+"impl";
        //createDir(dboServiceImplPath);

        //String doControllerPath=javaPath+File.separatorChar+ Utils.package2Path(packageName)+File.separatorChar+"web"+File.separatorChar+"controller"+File.separatorChar+"dbo";
        //createDir(doControllerPath);


        String[] tableArr=StringUtils.split(tables,",");

        for(String tabName:tableArr){
            TableBO tableBO = baseTableManager.getTable(packageName, tabName);
            Utils.createFile(doPath+File.separatorChar+tableBO.getJavaClassName()+"DO.java",tableBO,"db.template");
            for(EnumBO enumBO:tableBO.getEnums()){
                Utils.createFile(enumPath+File.separatorChar+enumBO.getJavaClassName()+"Enum.java",enumBO,"enum.template");
            }
            Utils.createFile(immutableDaoManagerPath+File.separatorChar+"Immutable"+tableBO.getJavaClassName()+"DaoManager.java",tableBO,"immutable_daomanager.template");
            Utils.createFile(immutableDaoManagerImplPath+File.separatorChar+"Immutable"+tableBO.getJavaClassName()+"DaoManagerImpl.java",tableBO,"immutable_daomanagerimpl.template");
            Utils.createFile(mapperPath+File.separatorChar+tableBO.getJavaClassName()+"Mapper.java",tableBO,"mapper.template");
            Utils.createFile(mapperXmlPath+File.separatorChar+tableBO.getJavaClassName()+"Mapper.xml",tableBO,"mapper_xml.template");


            Utils.createFileExistsNull(daoManagerPath+File.separatorChar+tableBO.getJavaClassName()+"DaoManager.java",tableBO,"daomanager.template");
            Utils.createFileExistsNull(daoManagerImplPath+File.separatorChar+tableBO.getJavaClassName()+"DaoManagerImpl.java",tableBO,"daomanager_impl.template");

            Utils.createFileExistsNull(daoPath+File.separatorChar+tableBO.getJavaClassName()+"Dao.java",tableBO,"dao.template");
            Utils.createFileExistsNull(daoXmlPath+File.separatorChar+tableBO.getJavaClassName()+"Dao.xml",tableBO,"dao_xml.template");

            if(tableBO.getTableComment().contains("cache")){
                Utils.createFile(immutableDaoManagerPath+File.separatorChar+"Immutable"+tableBO.getJavaClassName()+"CacheManager.java",tableBO,"immutable_cachemanager.template");
                Utils.createFile(immutableDaoManagerImplPath+File.separatorChar+"Immutable"+tableBO.getJavaClassName()+"CacheManagerImpl.java",tableBO,"immutable_cachemanagerimpl.template");
                Utils.createFileExistsNull(daoManagerPath+File.separatorChar+tableBO.getJavaClassName()+"CacheManager.java",tableBO,"cachemanager.template");
                Utils.createFileExistsNull(daoManagerImplPath+File.separatorChar+tableBO.getJavaClassName()+"CacheManagerImpl.java",tableBO,"cachemanager_impl.template");
                Utils.createFile(daoManagerImplPath+File.separatorChar+tableBO.getJavaClassName()+"RedisDao.java",tableBO,"redisdao.template");
            }


            //Utils.createFile(dboServicePath+File.separatorChar+tableBO.getJavaClassName()+"DOService.java",tableBO,"dboService.template");
           //Utils.createFile(dboServiceImplPath+File.separatorChar+tableBO.getJavaClassName()+"DOServiceImpl.java",tableBO,"dboServiceImpl.template");
            //Utils.createFile(doControllerPath+File.separatorChar+"DBO"+tableBO.getJavaClassName()+"Controller.java",tableBO,"doController.template");

        }
    }

    private static void createDir(String path){
        File pathFile =new File(path);
        if(!pathFile.exists()){
            pathFile.mkdirs();
        }
    }


    public static void createCode(Properties props){
        new DaoHelperService().createFiles(props);
    }


    public static void main(String[] args){

        Properties prop=new Properties();
        prop.setProperty("path","E:\\sutdy\\wind\\test");
        prop.setProperty("packageName","com.lzw.wind.test");
        prop.setProperty("tables","user,dept");
        prop.setProperty("url","jdbc:mysql://172.16.8.18:3306/tibmas2?allowMultiQueries=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull");
        prop.setProperty("driverName","com.mysql.jdbc.Driver");
        prop.setProperty("user","user");
        prop.setProperty("password","admin@1");
        DaoHelperService.createCode(prop);
    }
}