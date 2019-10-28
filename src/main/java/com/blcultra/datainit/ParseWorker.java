//package com.blcultra.datainit;
//
//import com.blcultra.service.impl.PatentXMLAnalysisService;
//import com.blcultra.support.PatentConstant;
//
//import java.io.ByteArrayOutputStream;
//import java.io.RandomAccessFile;
//import java.nio.MappedByteBuffer;
//import java.nio.channels.FileChannel.MapMode;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.CyclicBarrier;
//
//
///**
// * 解析线程任务
// * @author admin
// *
// */
//public class ParseWorker implements Runnable{
//
//    /**
//     * 线程写作控制类
//     */
//    private CyclicBarrier barrier;
//    private PatentFileQueue pfq;
//
//    /**
//     * 数据处理类
//     */
//    private DataHandler handler;
//    /**
//     * 文件存储路径
//     */
//    private String path;
//
//    private PatentXMLAnalysisService pxas;
//    /**
//     * 构造函数：文件解析必须的信息
//     * @param barrier
//     */
//    public ParseWorker(String path ,CyclicBarrier barrier) {
//        this.path = path;
//        this.barrier = barrier;
//
//        this.handler = new DataHandler();
//    }
//
//    public void setPfq(PatentFileQueue pfq) {
//        this.pfq = pfq;
//    }
//
//    public  void setPatentXMLAnalysisService(PatentXMLAnalysisService pxas){
//        this.pxas = pxas;
//    }
//    /**
//     *
//     */
//    public void run() {
//        try {
//
//            long start = System.currentTimeMillis();
//
//            while(pfq.getInstance().size() != 0){
//                String dataname = pfq.getInstance().poll();
//                String[] split = dataname.split("_");
//                String filename = split[0];
//                int num = Integer.parseInt(split[1]);
//                int size = Integer.parseInt(split[2]);
//                pfq.getInstance().size();
//
//                try{
//                    Map<String,Object> map = pxas.AnalysisXmlToList(path+"\\"+filename,
//                            true,true,true);
//
////                    handler.handle(map,num,size);
//
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//
//            //数据处理完成，通知栅栏修改状态
//            long end = System.currentTimeMillis();
//            barrier.await();
//
//            System.out.println(Thread.currentThread().getName()+"Finish ed:"+(end-start)/1000);
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
