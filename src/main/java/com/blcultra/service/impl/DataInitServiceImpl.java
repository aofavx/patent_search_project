//package com.blcultra.service.impl;
//
//import com.blcultra.datainit.PatentFileQueue;
//import com.blcultra.service.DataInitService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.concurrent.CyclicBarrier;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//
///**
// * Created by sgy05 on 2019/8/9.
// */
//@Service(value = "dataInitService")
//public class DataInitServiceImpl implements DataInitService {
//
//    @Autowired
//    private PatentXMLAnalysisService pxas;
//    @Autowired
//    private PatentFileQueue pfq;
//    @Override
//    public String init(String xmlpath) {
//
//        String xmlPtah = "E:\\BLCUpload\\1analysis_patentxml\\A47G1900\\";
//        int poolnum = 5;
//        //创建线程池
//        final ScheduledExecutorService service = Executors.newScheduledThreadPool(poolnum);
//
//        //记录解析开始时间
//        final long startTime = System.currentTimeMillis();
//
//        pxas.selectAllFile(xmlPtah);
//        // 线程协作类：等待所有任务都执行完成后，主线程则执行收尾工作
//        CyclicBarrier barrier = new CyclicBarrier(poolnum, new Runnable() {
//            @Override
//            public void run() {
//                long endTime = System.currentTimeMillis();
//
//                //关闭线程池
//                service.shutdown();
//
//                //统计操作总耗时
//                System.out.println(Thread.currentThread().getName() + " all work is done ,cost:"
//                        + (endTime - startTime) / 1000 + ("(s)"));
//            }
//        });
//        // 根据文件切分块数，提交N个解析任务
//        for (int i = 0;i<5 ;i++) {
//            ParseWorker pw = new ParseWorker(xmlPtah,barrier);
//            pw.setPfq(pfq);
//            pw.setPatentXMLAnalysisService(pxas);
//            service.submit(pw);
//        }
//
//
//        return null;
//    }
//}
