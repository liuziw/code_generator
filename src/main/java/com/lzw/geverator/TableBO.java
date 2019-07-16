package com.lzw.geverator;

import lombok.Data;

import java.util.List;

@Data
public class TableBO extends NameBO {

    private String packageName;

    private String tableName;

    private String tableComment;

    private FieldBO primaryKey;

    private List<FieldBO> fields;

    private List<FieldBO> businessFields;

    private List<EnumBO> enums;

    private String dbType;

    private String currentTime;

    private String primaryKeyName;
}
