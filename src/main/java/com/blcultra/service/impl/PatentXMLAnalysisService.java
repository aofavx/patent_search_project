/**
 * ProjectName:    patent
 * PackageName:    com.ultra.service.impl
 * FileName：      PatentTFIDFServiceImpl.java
 * Copyright:      Copyright(C) 2018
 * Company:        北京神州泰岳软件股份有限公司
 * Author:         admin
 * CreateDate:     2018/9/8 9:09
 */

package com.blcultra.service.impl;

import com.blcultra.datainit.PatentFileQueue;
import com.blcultra.support.PatentConstant;
import com.blcultra.util.FileUtil;
import com.blcultra.util.StringUtil;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("PatentXMLAnalysisService")
public class PatentXMLAnalysisService {
    private final static Logger logger = LoggerFactory.getLogger(PatentXMLAnalysisService.class);

    @Autowired
    private PatentFileQueue pfq;

    public void  offerToQueue(List<String> list){

        for (String filefullpath : list) {
            pfq.getInstance().offer(filefullpath);
        }
    }


    /**
     * 将待处理的文件的文件名存储到队列中
     * @param xmlDir
     * @throws Exception
     */
    public void selectAllFile(String xmlDir) {

        List<String> pathName = new ArrayList<String>();
        List<String> filenames = FileUtil.iteratorPath(xmlDir,pathName);//遍历xml所在目录，得到xml文件路径信息
        int size = filenames.size();
        System.out.println("********************存入队列开始********************");
        for (int i = 0; i < filenames.size(); i++) {
            int n = i+1;
//            System.out.println("第"+n +"个文件");
            String filename = filenames.get(i);
            int m = i+1;
            pfq.getInstance().offer(filename+"_"+m+"_"+size);
        }
        System.out.println("*************存入队列结束*****************");
//        //首先查询所有文件
//        logger.error("=====文件存入队列===========开始==========");
//        for(String file: filenames){
//            logger.error("oooooooooooo"+file+"==========");
//            pfq.getInstance().offer(file);//xml文件
//        }
//        logger.error("=====文件存入队列===========结束==========");
    }

    /**
     * 获取SAXReader 用以读取xml文件
     * @return
     * @throws Exception
     */
    public  synchronized static SAXReader GetXmlSAXReader()throws Exception{
        SAXReader reader = new SAXReader();
        reader.setValidation(false);
        //取消dtd验证
        reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        return reader;
    }

    public  synchronized static Map<String,Object> AnalysisXmlToList(String filePath,
                                           boolean isabstract, boolean iscontent, boolean isclaim){
        Map<String,Object> map = new HashMap<>();
        List<String> all = new ArrayList<>();
        try {
            SAXReader reader = GetXmlSAXReader(); //获取SAXReader
            Document document = reader.read(new FileInputStream(new File(filePath)));
            /**
             * 1、首先需要准备三个List，分别为name（专利名）、abstract（摘要）和body（内容）组装数据
             * 2、分别读取节点
             *   cn-patent-document/application-body/description/invention-title 专利名
             *
             *   cn-patent-document/cn-bibliographic-data/abstract/p 摘要
             *   cn-patent-document/application-body/description/p 内容
             *   cn-patent-document/application-body/description/technical-field/p （多节点） 技术领域
             *   cn-patent-document/application-body/description/background-art/p  （多节点）  背景
             *   cn-patent-document/application-body/description/disclosure/p      （多节点） 公开信息
             *   cn-patent-document/application-body/description/mode-for-invention/p  （多节点） 实施方案
             *
             *   cn-patent-document/application-body/claims/claim/claim-text 内容
             *   cn-patent-document/cn-bibliographic-data/classifications-ipcr/classification-ipcr/text  专利分类
             *   或者
             *   cn-patent-document/cn-bibliographic-data/classification-ipc/main-classification 专利分类
             * 3、对每个节点进行遍历，取出数据。
             * 4、对数据进行整理，整理过程包括，将数据按照句号进行截取，然后将每个句子装入对象中
             */
            Node node = document.selectSingleNode("cn-patent-document/cn-bibliographic-data/cn-parties/cn-applicants/cn-applicant/addressbook/name");
            //申请人
//            List<String> applicants = new ArrayList<>();
//            applicants.add(removeTextSpecial(node.getText())+"。");
            String applicant = removeTextSpecial(node.getText());
            map.put(PatentConstant.PATENT_APPLICANT,applicant);
//            all.addAll(applicants);
            //申请号
            Node applicantnum = document.selectSingleNode("cn-patent-document/cn-bibliographic-data/application-reference/document-id/doc-number");
            String num = removeTextSpecial(applicantnum.getText());
//            all.add(num);
            map.put(PatentConstant.PATENT_APPLICANTNUM,num);
            Node applicanttime = document.selectSingleNode("cn-patent-document/cn-bibliographic-data/application-reference/document-id/date");
            String time = removeTextSpecial(applicanttime.getText());
            map.put(PatentConstant.PATENT_APPLICANTTIME,time);

            List<String> name = new ArrayList<>();
            //使用selectNodes寻找node节点需要引包jaxen
            //获取name（专利名）
            Node nameNode= document.selectSingleNode("cn-patent-document/application-body/description/invention-title ");
            name.add(removeTextSpecial(nameNode.getText())+"。");
//            all.add("#####################name################################");
            all.addAll(name);
            map.put(PatentConstant.PATENT_NAME,removeTextSpecial(nameNode.getText()));//专利名
            //专利类别
            List<Node> classifyList= document.selectNodes("cn-patent-document/cn-bibliographic-data/classifications-ipcr/classification-ipcr");
            String classifies = "";
            if(classifyList != null && classifyList.size() != 0){
                classifies = getClassificationsNodeText(classifyList);
            }
            if(classifyList == null || classifyList.size() == 0){
                classifies = document.selectSingleNode("cn-patent-document/cn-bibliographic-data/classification-ipc/main-classification").getText();
            }
//            all.add("#####################classify################################");
            all.add(classifies);

            if(isabstract){
                List<String> abstractList = new ArrayList<>();
                //获取摘要信息
                List<Node> ab_list= document.selectNodes("cn-patent-document/cn-bibliographic-data/abstract/p");
                if(ab_list != null && ab_list.size() > 0){
                    getNodeText(ab_list,abstractList,"abstract");
//                    all.add("#####################abstract############################");
                    all.addAll(abstractList);
                }

            }
            if(iscontent){
                List<String> contentList = new ArrayList<>();
                //获取内容信息
                List<Node> body_list= document.selectNodes("cn-patent-document/application-body/description/p");
                if(body_list != null && body_list.size() > 0)
                    getNodeText(body_list,contentList,"application-body");

                //获取内容信息
                List<Node> filed_list= document.selectNodes("cn-patent-document/application-body/description/technical-field/p");
                if(filed_list != null && filed_list.size() > 0)
                    getNodeText(filed_list,contentList,"application-body");

                List<Node> background_list= document.selectNodes("cn-patent-document/application-body/description/background-art/p");
                if(background_list != null && background_list.size() > 0)
                    getNodeText(background_list,contentList,"application-body");

                List<Node> disclosure_list= document.selectNodes("cn-patent-document/application-body/description/disclosure/p");
                if(disclosure_list != null && disclosure_list.size() > 0)
                    getNodeText(disclosure_list,contentList,"application-body");

                List<Node> mode_list= document.selectNodes("cn-patent-document/application-body/description/mode-for-invention/p");
                if(mode_list != null && mode_list.size() > 0)
                    getNodeText(mode_list,contentList,"application-body");

                List<Node> p_list= document.selectNodes("description/p");
                if(p_list != null && p_list.size() > 0)
                    getNodeText(p_list,contentList,"application-body");

//                all.add("#####################content#############################");
                all.addAll(contentList);
            }
            if(isclaim){
                List<String> claimList = new ArrayList<>();
                List<Node> claim_list= document.selectNodes("cn-patent-document/application-body/claims/claim/claim-text");
                if(claim_list != null && claim_list.size() > 0){
                    getNodeText(claim_list,claimList,"claim");
//                   all.add("#####################claim###############################");
                    all.addAll(claimList);
                }

            }
            map.put("all",all);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return map;
    }
    /**
     * 获取节点中的文本信息
     * @param nodes
     * @param list
     * @param type
     * @return
     * @throws Exception
     */
    public synchronized  static Object getNodeText(List<Node> nodes,List<String> list,String type) throws Exception {
        for (Node node:nodes) {
            String text = removeTextSpecial(node.getText());
            list.add(text);
//            String[] ss = text.split("。");
//            for(String s : ss){
//                if(s.length() > 1){
//                    list.add(s + "。");
//                }
//            }
            System.gc();
        }
        return list;
    }


    /**
     * 将xml文件解析成txt文件，格式为：文件名_所述部分_句子内容
     * @param filePath
     * @param fileName
     * @return
     */
    public  synchronized  List<String> newAnalysisXmlToList(String filePath,String fileName){
        List<String> all = new ArrayList<>();
        try {
            SAXReader reader = new SAXReader();
            reader.setValidation(false);
            //取消dtd验证
            reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            Document document = reader.read(new FileInputStream(new File(filePath)));
            /**
             * 1、首先需要准备三个List，分别为name（专利名）、abstract（摘要）和body（内容）组装数据
             * 2、分别读取节点
             *   cn-patent-document/application-body/description/invention-title 专利名
             *   或者cn-patent-document/application-body/description/invention-title/b
             *
             *   cn-patent-document/cn-bibliographic-data/abstract/p 摘要
             *   cn-patent-document/application-body/description/p 内容
             *   cn-patent-document/application-body/description/technical-field/p （多节点） 技术领域
             *   cn-patent-document/application-body/description/background-art/p  （多节点）  背景
             *   cn-patent-document/application-body/description/disclosure/p      （多节点） 公开信息
             *   cn-patent-document/application-body/description/mode-for-invention/p  （多节点） 实施方案
             *
             *   cn-patent-document/application-body/claims/claim/claim-text 内容
             *   cn-patent-document/cn-bibliographic-data/classifications-ipcr/classification-ipcr/text  专利分类
             *   或者
             *   cn-patent-document/cn-bibliographic-data/classification-ipc/main-classification 专利分类
             * 3、对每个节点进行遍历，取出数据。
             * 4、对数据进行整理，整理过程包括，将数据按照句号进行截取，然后将每个句子装入对象中
             */
            logger.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>解析XML文件>>>>>>>>>>>>>>>>>>>>>>开始>>>>>>>>>>>>>>>>>>>");
            List<String> name = new ArrayList<>();
            //使用selectNodes寻找node节点需要引包jaxen
            //获取name（专利名）
            Node nameNode= document.selectSingleNode("cn-patent-document/application-body/description/invention-title ");
            if(StringUtil.empty(nameNode.getText())){
                nameNode= document.selectSingleNode("cn-patent-document/application-body/description/invention-title/b ");
            }
            name.add(fileName+"_name_"+this.removeTextSpecial(nameNode.getText())+"。");
            all.addAll(name);

            //专利类别
            List<Node> classifyList= document.selectNodes("cn-patent-document/cn-bibliographic-data/classifications-ipcr/classification-ipcr");
            String classifies = "";
            if(classifyList != null && classifyList.size() != 0){
                classifies = getClassificationsNodeText(classifyList);
            }else{
                classifies = document.selectSingleNode("cn-patent-document/cn-bibliographic-data/classification-ipc/main-classification").getText();
            }
            all.add(fileName+"_classify_"+classifies);

            List<String> abstractList = new ArrayList<>();
            //获取摘要信息
            List<Node> ab_list= document.selectNodes("cn-patent-document/cn-bibliographic-data/abstract/p");
            if(ab_list != null && ab_list.size() > 0){
                this.getNodeText(ab_list,abstractList,"abstract",fileName);
                all.addAll(abstractList);
            }

            List<String> contentList = new ArrayList<>();
            //获取内容信息
            List<Node> body_list= document.selectNodes("cn-patent-document/application-body/description/p");
            if(body_list != null && body_list.size() > 0)
                this.getNodeText(body_list,contentList,"content",fileName);

            //获取内容信息
            List<Node> filed_list= document.selectNodes("cn-patent-document/application-body/description/technical-field/p");
            if(filed_list != null && filed_list.size() > 0)
                this.getNodeText(filed_list,contentList,"content",fileName);

            List<Node> background_list= document.selectNodes("cn-patent-document/application-body/description/background-art/p");
            if(background_list != null && background_list.size() > 0)
                this.getNodeText(background_list,contentList,"content",fileName);

            List<Node> disclosure_list= document.selectNodes("cn-patent-document/application-body/description/disclosure/p");
            if(disclosure_list != null && disclosure_list.size() > 0)
                this.getNodeText(disclosure_list,contentList,"content",fileName);

            List<Node> mode_list= document.selectNodes("cn-patent-document/application-body/description/mode-for-invention/p");
            if(mode_list != null && mode_list.size() > 0)
                this.getNodeText(mode_list,contentList,"content",fileName);

            List<Node> p_list= document.selectNodes("description/p");
            if(p_list != null && p_list.size() > 0)
                this.getNodeText(p_list,contentList,"content",fileName);

            all.addAll(contentList);

            List<String> claimList = new ArrayList<>();
            List<Node> claim_list= document.selectNodes("cn-patent-document/application-body/claims/claim/claim-text");
            if(claim_list != null && claim_list.size() > 0){
                this.getNodeText(claim_list,claimList,"claim",fileName);
                all.addAll(claimList);
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        logger.error("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<解析XML文件<<<<<<<<<<<<<<<<<<<<<<结束<<<<<<<<<<<<<<<<<<<");
        return all;
    }

    /**
     * 获取xml中的节点信息，并对节点信息进行处理
     * @param nodes
     * @param list
     * @param type
     * @param fileName
     * @return
     * @throws Exception
     */
    public synchronized  Object getNodeText(List<Node> nodes,List<String> list,String type,String fileName) throws Exception {
        for (Node node:nodes) {
            String text = removeTextSpecial(node.getText());
            String[] ss = text.split("。");
            for(String s : ss){
                if(s.length() > 1){
                    list.add(fileName+"_"+ type+"_"+s + "。");
                }
            }
            System.gc();
        }
        return list;
    }

    /**
     * 将解析后得到的List输出到txt文件中
     * @param filepath
     * @param s
     * @throws Exception
     */
    public synchronized void writeFile(String filepath, List<String> s) throws Exception {
        File file = new File(filepath);
        if (!file.exists()) {
            file.createNewFile();
        }
        PrintWriter pw = new PrintWriter(file);
        try {
            for (String st : s) {
                if(! st.equals("  。")){
                    pw.println(st);
                }
            }
            pw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取专利号节点信息
     * @param nodes
     * @return
     * @throws Exception
     */
    public static synchronized  String getClassificationsNodeText(List<Node> nodes) throws Exception {
        String classify = "";
        for (Node node:nodes) {
            String text = node.selectSingleNode("text").getText();
            classify = classify + text +"||";
        }
        return classify.substring(0,classify.length() - 2);
    }
    /**
     * 替换xml中的特殊字符
     * @param text
     * @return
     * @throws Exception
     */
    public static synchronized String removeTextSpecial(String text) throws Exception {
        return text.replaceAll("\n","")
                .replaceAll("\t","")
                .replaceAll("\r","")
                .replaceAll("\t","")
                .replaceAll("\r","")
                .replaceAll(" ","")
                .replaceAll("  ","")
                .replaceAll("    ","")
                .replaceAll("     ","")
                .replaceAll("      ","")
                .replaceAll("       ","")
                .replaceAll("        ","")
                .replaceAll("         ","")
                .replaceAll("          ","")
                .replaceAll("           ","")
                .replaceAll("                ","").trim();
    }

}
