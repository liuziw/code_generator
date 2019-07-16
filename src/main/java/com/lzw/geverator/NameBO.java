package com.lzw.geverator;


import lombok.Data;

@Data
public class NameBO {

    private String javaClassName;

    private String javaFieldName;

    private String dataBaseName;

    private String getMethodName;

    private String setMethodName;

}
