package com.lzw.geverator.bo;

import lombok.Data;

@Data
public class EnumItemBO extends NameBO {

    private String itemName;

    private String itemCode;

    private String itemDesc;

    private String stringCode;
}
