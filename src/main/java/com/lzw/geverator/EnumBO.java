package com.lzw.geverator;

import com.lzw.geverator.enums.FieldTypeEnum;
import lombok.Data;

import java.util.List;

@Data
public class EnumBO extends NameBO {

    private String packageName;

    private String enumName;

    private List<EnumItemBO> items;

    private FieldTypeEnum fieldType;
}