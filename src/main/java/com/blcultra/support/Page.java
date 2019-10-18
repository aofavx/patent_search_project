package com.blcultra.support;


import java.util.List;
import java.util.Map;

public class Page {
    private Object obj;//条件bean
    private int pageSize;//分页数
    private int pageNow;//当前页
    private int pageCount;//总页数
    private int total;//总条数
    private int queryStart;//起始页
    private int queryEnd;//结束页
    /** 查询条件 */
    private Object criteriaObj;
    /**  查询条件 map*/
    private Map<String,Object> param;
    /** 结果集 */
    private List<?> resultList;

    public Object getCriteriaObj() {
        return criteriaObj;
    }

    public void setCriteriaObj(Object criteriaObj) {
        this.criteriaObj = criteriaObj;
    }

    public Page() {

    }

    public Page(int pageNow, int pageSize) {
        this.pageSize = pageSize;
        this.pageNow = pageNow;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public void setQueryStart(int queryStart) {
        this.queryStart = queryStart;
    }

    public void setQueryEnd(int queryEnd) {
        this.queryEnd = queryEnd;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public Page(int pageSize, int pageNow, int total) {
        this.pageSize = pageSize;
        this.pageNow = pageNow;
        this.total = total;
    }
    public Page(int pageSize, int pageNow, Object obj) {
        this.pageSize = pageSize;
        this.pageNow = pageNow;
        this.obj = obj;
    }
    public Page(int pageSize, int pageNow, Map<String,Object> param){
        this.pageSize = pageSize;
        this.pageNow = pageNow;
        this.param = param;
    }
    public Page(int pageSize, int pageNow, int pageCount, int total) {
        this.pageSize = pageSize;
        this.pageNow = pageNow;
        this.pageCount = pageCount;
        this.total = total;
    }


    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNow() {
        return pageNow;
    }

    public void setPageNow(int pageNow) {
        this.pageNow = pageNow;
    }

    public int getPageCount() {
        if (pageSize == 0) {
            return 0;
        }
        if (total % pageSize == 0) {
            pageCount = total / pageSize;
        } else {
            pageCount = (total / pageSize) + 1;
        }
        return pageCount;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getQueryStart() {
        if(pageNow!=0&&pageSize!=0)
            queryStart = (pageNow - 1) * pageSize;
        return queryStart;
    }

    public int getQueryEnd() {
        queryEnd = queryStart + pageSize;
        return queryEnd;
    }

    public List<?> getResultList() {
        return resultList;
    }

    public void setResultList(List<?> resultList) {
        this.resultList = resultList;
    }


    public void setPage(Map map,String id,Integer pageNow, Integer pageSize,Page page){
        if (null != id){
            //map = new HashMap<String,String>();
            map.put("id",id);
        }
        page.setPage(map,pageNow,pageSize,page);

    }

    public void setPage(Map map, Integer pageNow, Integer pageSize, Page page){
        if(pageNow != null && pageSize != null){
            page.setPageNow(pageNow);
            page.setPageSize(pageSize);
            map.put("queryStart",page.getQueryStart());
            map.put("pageSize",page.getPageSize());
        }else{
            page.setPageNow(1);
            page.setPageSize(0);
        }
        page.setParam(map);
    }

}
