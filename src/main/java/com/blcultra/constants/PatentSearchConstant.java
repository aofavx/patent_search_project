package com.blcultra.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sgy05 on 2019/10/12.
 */
public class PatentSearchConstant {


    public static final String onestopsearch = "1";//一站式检索type类型代表值

    public static final String  autosearch = "2";//自动检索（包含自动检索和评测检索）type类型代表值
    public static final String  autosearch_bm25_bert = "2000";
    public static final String  autosearch_es_bert = "2001";
    public static final String  autosearch_bm25 = "2002";

    public static final String classify = "3";//分类

    public static final List<String> file_tags = Arrays.asList("X","Y","A","R","E");

    public static final String basesearchtype="1000";//基础检索类型
    public static final String boolsearchtype="1001";//布尔检索表达式

    public static final String structsearchtype = "1002";//结构检索表达式
    public static final String structexpression = "";//布尔表达式语句

    public static final String graphsearchtype = "1003";//图谱检索表达式
    //(n)的氨基酸{print($1)}
    public static final String graphexpression_prefix = "(n)的";//图谱表达式语句
    public static final String graphexpression_suffix = "{print($1)}";//图谱表达式语句
//(饮料杯1∨饮料容器1)∧(小杯1∨大杯1)
    public static final String expressionprefix = "(";
    public static final String expressionsuffix = ")";
    public static final String expressionintersection = "∧";//交集
    public static final String expressionunion = "∨";//并集

    //(n)的(~){$2=[碗 玻璃杯 底座];print($1)}
    public static final String jiegou_prefix = "(n)的(~){$2=[";
    public static final String jiegou_suffix = "];print($1)}";



    public static final int graphsearchsize = 20;


}
