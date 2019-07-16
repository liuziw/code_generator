package com.lzw.geverator.enums;

import java.sql.Types;

public enum  FieldTypeEnum {

    Long("Long"),
    Integer("Integer"),
    Character("Character"),
    Date("java.util.Date"),
    Double("Double"),
    Float("Float"),
    Number("Number"),
    String("String"),
    VARBINARY("byte[]");


    private final String fieldType;

    private FieldTypeEnum(String fieldType) {
        this.fieldType = fieldType;
    }



    public String toString(){
        return this.getFieldType();
    }

    public java.lang.String getFieldType() {
        return fieldType;
    }


    public static FieldTypeEnum getType(int type){
        switch (type){
            case Types.BIGINT:
                return Long;
            case Types.BIT:
                return Integer;
            case Types.CHAR:
                return String;
            case Types.DATE:
                return Date;
            case Types.DOUBLE:
                return Double;
            case Types.FLOAT:
                return Float;
            case Types.INTEGER:
                return Integer;
            case Types.NUMERIC:
                return Number;
            case Types.NVARCHAR:
                return String;
            case Types.TIME:
                return Date;
            case Types.TIMESTAMP:
                return Date;
            case Types.TINYINT:
                return Integer;
            case Types.VARCHAR:
                return String;
            case Types.LONGVARCHAR:
                return String;
            case Types.LONGVARBINARY:
                return String;
            case Types.VARBINARY:
                return VARBINARY;
            default:
                throw new RuntimeException("不支持的sql类型:"+type);
        }
    }

}
