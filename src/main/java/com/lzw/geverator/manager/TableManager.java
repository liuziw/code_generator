package com.lzw.geverator.manager;


import com.lzw.geverator.bo.TableBO;

public interface TableManager {
    public TableBO getTable(String packageName, String tableName);
}
