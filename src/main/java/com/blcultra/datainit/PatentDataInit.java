package com.blcultra.datainit;

import com.blcultra.service.impl.PatentXMLAnalysisService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 专利数据文件解析
 * Created by sgy05 on 2019/8/8.
 */
public class PatentDataInit {

    public static void main(String[] args) {

//        String xmlPtah = "E:\\BLCUpload\\1analysis_patentxml\\A47G1900\\";
//        ApplicationContext ac = new FileSystemXmlApplicationContext("classpath:applicationContext.xml");
//        PatentXMLAnalysisService pxas = ac.getBean(PatentXMLAnalysisService.class,"PatentXMLAnalysisService");
//
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
//            ParseWorker pw = new ParseWorker("",barrier);
//            pw.setPfq((PatentFileQueue)ac.getBean("PatentFileQueue"));
//            pw.setPatentXMLAnalysisService(pxas);
//            service.submit(new ParseWorker("", barrier));
//        }


    }



}
