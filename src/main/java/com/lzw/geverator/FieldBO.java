package com.lzw.geverator;

import com.lzw.geverator.enums.FieldTypeEnum;
import lombok.Data;

@Data
public class FieldBO extends NameBO {

    private String fieldName;

    private FieldTypeEnum type;

    private String fieldDesc;

    private boolean autoIncrement;



}
