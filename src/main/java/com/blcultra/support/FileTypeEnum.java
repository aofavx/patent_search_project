package com.blcultra.support;

/**
 * FILE TYPE ENUM CLASS
 * Created by sgy05 on 2019/8/21.
 */
public enum FileTypeEnum {
    FILE_TYPE_ZIP("application/zip", ".zip"),
    FILE_TYPE_RAR("application/octet-stream", ".rar"),
    FILE_TYPE_XML("application/octet-stream", ".xml");
    public String type;
    public String fileSuffix;


    FileTypeEnum(String type, String fileSuffix) {
        this.type = type;
        this.fileSuffix = fileSuffix;
    }

    public static String getFileSuffix(String type) {
        for (FileTypeEnum orderTypeEnum : FileTypeEnum.values()) {
            if (orderTypeEnum.type.equals(type)) {
                return orderTypeEnum.fileSuffix;
            }
        }
        return null;
    }
}