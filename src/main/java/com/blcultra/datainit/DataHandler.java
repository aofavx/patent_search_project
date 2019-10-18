package com.blcultra.datainit;

import com.blcultra.support.PatentConstant;
import com.blcultra.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


/**
 * Created by sgy05 on 2019/8/8.
 */
public class DataHandler {

    private DBHelper db;

    private PreparedStatement statement;

    private String sql = "insert into patent_info(pid, pname, applynum, applytime, applicant,uploader,pstate,uploadpath) values(?,?,?,?,?,?,?,?)";
    private long totalLine = 0;
    private int counter = 0;
    private int maxBatch = 100;

    public DataHandler() {
        db = new DBHelper();
        db.openConnection();

//        try {
//            statement = db.conn.prepareStatement(sql);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    /**
     *
     * @param map    解析的文本内容
     * @throws SQLException
     */
    public void handle(Map<String,Object> map,String uid,String filepath,String pid) throws SQLException {
        totalLine++;
        counter++;
        try {
            String sql1 = "insert into patent_info(pid, pname, applynum, applytime, applicant,uploader,pstate,uploadpath) values(?,?,?,?,?,?,?,?)";
            PreparedStatement statement1 = db.conn.prepareStatement(sql1);
            String sql2 = "insert into patent_text_detail(ptextid, pid, pdetail) values(?,?,?)";
            PreparedStatement statement2 = db.conn.prepareStatement(sql2);


            String applicant = String.valueOf(map.get(PatentConstant.PATENT_APPLICANT));//获取申请人姓名
            String applicantnum = String.valueOf(map.get(PatentConstant.PATENT_APPLICANTNUM));//获取专利申请号
            String applicanttime = String.valueOf(map.get(PatentConstant.PATENT_APPLICANTTIME));//获取专利申请时间
            String patentname = String.valueOf(map.get(PatentConstant.PATENT_NAME));//获取专利名


            List<String> listcontents = (List<String>) map.get("all");
            StringBuffer stringBuffer = new StringBuffer();
            for (String s : listcontents) {
                stringBuffer.append(s+"\r");
            }
            statement2.setObject(1, StringUtil.getUUID());
            statement2.setObject(2,pid);
            statement2.setObject(3,stringBuffer.toString());
            statement2.addBatch();
            //pid, pname, applynum, applytime, applicant,uploader,pstate,uploadpath
            statement1.setObject(1,pid);
            statement1.setObject(2,patentname);
            statement1.setObject(3, applicantnum);
            statement1.setObject(4,applicanttime);
            statement1.setObject(5,applicant);
            statement1.setObject(6,uid);
            statement1.setObject(7,true);
            statement1.setObject(8,filepath);
            statement1.addBatch();
            System.out.println("********************数据放入statement中****************************");
            System.out.println("***********累计数字countert*********"+counter);
            // 达到一个批次最大值，入库
            if (counter == maxBatch) {
                System.out.println("*****************达到一个批次最大值，进行入库操作***************");
                int[] r= statement1.executeBatch();
                int[] m = statement2.executeBatch();
                System.out.println(r.length);
                System.out.println(m.length);
                statement1.clearBatch();
                statement2.clearBatch();
                counter = 0;
            }

            //最后一行则收尾
            if(map.isEmpty() && counter>0) {
                System.out.println("********************满足最后一个文件的条件********************");
                statement1.executeBatch();
                statement2.executeBatch();
                statement1.clearBatch();
                statement2.clearBatch();
                statement1.close();
                statement2.close();
                db.closeConnection();
                System.out.println(Thread.currentThread().getName()+" parse total line is:"+totalLine);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
