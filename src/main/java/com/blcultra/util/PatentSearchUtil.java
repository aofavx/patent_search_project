//package com.blcultra.util;
//
//import com.blcultra.support.PatentConstant;
//import com.bonc.usdp.sql4es.jdbc.ESConnection;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.*;
//
//public class PatentSearchUtil {
//
//    private static Log log = LogFactory.getLog(PatentSearchUtil.class);
//    private static final String finaldocid = PatentConstant.finaldocid;
//    private static final String title = PatentConstant.title;
//    private static final String abs = PatentConstant.abs;
//    private static final String claims = PatentConstant.claims;
//    private static final String pdate = PatentConstant.pdate;
//    private static final String description = PatentConstant.description;
//
//    public static Map<String,String> getPatentContentsByDocId(ESConnection esConnection, String docId){
//        Map<String,String> detail = new HashMap<>(4);
//        Statement st=null;
//
//        try {
//            st = esConnection.createStatement();
//
//            String sql = "select title,abs,claims,description from cn where docid='"+docId+"'";
////            log.info("******************查询语句sql："+sql);
//            ResultSet res = st.executeQuery(sql.toString());
//            while (res.next()){
//                detail.put(title, res.getString(1));
//                detail.put(abs, StringUtil.remove2(res.getString(2)));
//                detail.put(claims,StringUtil.remove2(res.getString(3)));
//                detail.put(description,res.getString(4));
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage(),e);
//        }finally {
//            if (st != null){
//                try {
//                    st.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
////        log.info("=========== get origin patent content by docid ,docid is："+ docId + ",detail is :" + detail.toString());
////        log.info("=========== get origin patent content by docid ,docid is："+ docId );
//        detail.put("docid",docId);
////        log.info("********xiabiande ***detail 信息："+detail);
//        return detail;
//
//    }
//
//
//    public static Map<String,String> getContents2(ESConnection esConnection, String docId){
//        Map<String,String> detail = new HashMap<>(3);
//        Statement st=null;
//
//        try {
//            st = esConnection.createStatement();
//
//            String sql = "select title,abs,claims,pdate from en where docid='"+docId+"'";
//            ResultSet res = st.executeQuery(sql.toString());
//            while (res.next()){
//                detail.put(title, res.getString(1));
//                detail.put(abs, StringUtil.remove2(res.getString(2)));
//                detail.put(claims,StringUtil.remove2(res.getString(3)));
//                detail.put(pdate,res.getString(4));
////                contents.add(res.getString(3));
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage(),e);
//        }finally {
//            if (st != null){
//                try {
//                    st.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
////        log.info("=========== get origin patent content by docid ,docid is："+ docId + ",detail is :" + detail.toString());
//        log.info("=========== get origin patent content by docid ,docid is："+ docId );
//        return detail;
//
//    }
//
//
//    public static List<Map<String,String>> getCompareDocIds2(ESConnection esConnection, Map<String,String> contentdetail, int num) throws Exception{
//        List<Map<String,String>> docIds = new ArrayList<>();
//        Map<String,Float> scores = new HashMap<>();
//        Map<String,Map<String,String>> detail = new HashMap<>();
//        List<String> contents2 = new ArrayList<>();
////        contents2.add(contentdetail.get(title));
//        if(! StringUtil.empty(contentdetail.get(abs))){
//            contents2.add(StringUtil.remove2(contentdetail.get(abs)));
//        }
//        if(! StringUtil.empty(contentdetail.get(claims))){
//            contents2.add(StringUtil.remove2(contentdetail.get(claims)));
//        }
//        for (String content:contents2){
//            Statement st=null;
//            try {
//                st = esConnection.createStatement();
//                StringBuilder sql = new StringBuilder();
//
//                int searchNum = num + (num * 3 /4);
////                if(StringUtil.empty(date)){
////                    sql.append("select docid,appid,_score,abs,claims,title from en WHERE _search = ' abs:(")
////                            .append(content).append(") or claims:(").append(content).append(") or description:(")
////                            .append(content).append(") 'limit "+num);
////                }else{
////                    sql.append("select docid,appid,_score,abs,claims,title from en WHERE _search = ' abs:(")
////                            .append(content).append(") or claims:(").append(content).append(") or description:(")
////                            .append(content).append(") ' and pdate < ")
////                            .append(date).append(" limit "+num);
////                }
//
//                if(StringUtil.empty(contentdetail.get(pdate))){
//                    sql.append("select docid,appid,_score,abs,claims,title from en WHERE _search = 'abs:(")
//                            .append(content).append(") or claims:(").append(content).append(") 'limit "+searchNum);
//                }else{
//                    sql.append("select docid,appid,_score,abs,claims,title from en WHERE _search = ' abs:(")
//                            .append(content).append(") or claims:(").append(content).append(") ' and pdate < '")
//                            .append(contentdetail.get(pdate)).append("' limit "+searchNum);
//                }
//
////                System.out.println("====================== search sql :" + sql.toString());
//
//                ResultSet rs = st.executeQuery(sql.toString());
//                int n = 0;
//                while (rs.next()){
//
//                    if(n == num){
//                        break;
//                    }
//
//                    String docId = rs.getString(1);
//                    if(docId.contains("A9") || docId.contains("A2")){
//                        continue;
//                    }
//                    if (StringUtil.empty(docId)){
//                        docId = "";
//                    }
//                    String appId = rs.getString(2);
//                    float score = rs.getFloat(3);
//                    String abs2 = rs.getString(4);
//                    String claims2 = rs.getString(5);
//                    String title2 = rs.getString(6);
//                    String key = appId+"_"+docId;
////                    System.out.println(key + "  : " +score);
//
//                    Map<String,String> detailmap = new HashMap<>(4);
//                    detailmap.put(title,title2);
//                    detailmap.put(abs,abs2);
//                    detailmap.put(claims,claims2);
//                    detail.put(key,detailmap);
//
//                    if (scores.containsKey(key)){
//                        float sco = scores.get(key);
//                        scores.put(key,sco+score);
//                    }else {
//                        scores.put(key,score);
//                    }
//                    n ++;
//                }
//
//            } catch (Exception e) {
//                log.error(e.getMessage(),e);
//            }finally {
//                if (st != null){
//                    try {
//                        st.close();
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//
//        List<Map.Entry<String, Float>> list = new LinkedList<Map.Entry<String,Float>>(scores.entrySet());
//        Collections.sort(list,new Comparator<Map.Entry<String, Float>>() {
//            @Override
//            public int compare(Map.Entry<String, Float> o1,
//                               Map.Entry<String, Float> o2) {
////                System.out.println("compare: " + o1.getKey() + "  : " +((Float)o1.getValue().get("score")).floatValue());
////                System.out.println("compare: " + o2.getKey() + "  : " +((Float)o2.getValue().get("score")).floatValue());
//                return  (o2.getValue()).compareTo ( o1.getValue()) ;
//            }
//        });
//        int length = list.size() > num?num:list.size();
//        for (int i=0;i<length;i++){
//            String key = list.get(i).getKey();
//
//            String id = key.split("_")[1];
//
////            System.out.println(key + "====" + list.get(i).getValue());
//            if (!StringUtil.empty(id)){
//                Map<String,String> details = new HashMap<>(3);
//                details.put(finaldocid,id);
//                details.put(claims,detail.get(key).get(claims)+"");
//                details.put(abs,detail.get(key).get(abs)+"");
//                details.put(title,detail.get(key).get(title)+"");
//                docIds.add(details);
//            }
//
//        }
//        return docIds;
//    }
//}
