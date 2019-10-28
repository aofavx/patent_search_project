package com.blcultra.datainit;

import com.blcultra.service.impl.PatentXMLAnalysisService;
import com.blcultra.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class PatentXMLAnalysisThread extends Thread{

    private final static Logger logger = LoggerFactory.getLogger(PatentXMLAnalysisThread.class);

    private DataHandler handler;
    private String path;
    private String uid;

    private PatentFileQueue pfq;
    public PatentXMLAnalysisThread(String uid){
        this.uid=uid;
        this.handler = new DataHandler();
    }

    private PatentXMLAnalysisService pxas;

    public PatentXMLAnalysisService getPxas() {
        return pxas;
    }

    public void setPxas(PatentXMLAnalysisService pxas) {
        this.pxas = pxas;
    }

    public void setPfq(PatentFileQueue pfq) {
        this.pfq = pfq;
    }

    @Override
    public void run() {
        Map<String,Object> map = new HashMap<>();
        String dataname =null;
        String pid =null;
        while(pfq.getInstance().size() != 0){
            dataname = pfq.getInstance().poll();

            try{
                map = pxas.AnalysisXmlToList(dataname,
                        true,true,true);

                pid = StringUtil.getUUID();
                handler.handle(map,uid,dataname,pid);
                map.clear();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            System.out.println("==================================================================================");
            System.out.println(Thread.currentThread().getName()+"&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&队列中还有0个元素  "+map.isEmpty());
            System.out.println("==================================================================================");
            handler.handle(map,uid,dataname,pid);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
