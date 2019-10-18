/**
 * ProjectName:    patent
 * PackageName:    com.ultra
 * FileName：      PatentXmlAnalysis.java
 * Copyright:      Copyright(C) 2018
 * Company:        北京神州泰岳软件股份有限公司
 * Author:         admin
 * CreateDate:     2018/8/21 12:11
 */

package com.blcultra.util;

import com.blcultra.support.PatentConstant;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatentXmlAnalysis {

    /**
     * 将xml文件解析成List,根据需要解析文件位置
     * 不需要的位置传入false即可
     * @param filePath
     * @return
     */
    public  synchronized static Map<String,Object> AnalysisXmlToList(String filePath,boolean isabstract,boolean iscontent,boolean isclaim
    ){
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
            map.put(PatentConstant.PATENT_CATEGORY,classifies);//专利类别
            if(isabstract){
                List<String> abstractList = new ArrayList<>();
                //获取摘要信息
                List<Node> ab_list= document.selectNodes("cn-patent-document/cn-bibliographic-data/abstract/p");
                if(ab_list != null && ab_list.size() > 0){
                    getNodeText(ab_list,abstractList,"abstract");
//                    all.add("#####################abstract############################");
                    all.addAll(abstractList);
                    map.put(PatentConstant.PATENT_ABSTRACT,String.join(" ",abstractList));
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
                map.put(PatentConstant.PATENT_DESC,String.join(" ",contentList));
            }
           if(isclaim){
               List<String> claimList = new ArrayList<>();
               List<Node> claim_list= document.selectNodes("cn-patent-document/application-body/claims/claim/claim-text");
               if(claim_list != null && claim_list.size() > 0){
                   getNodeText(claim_list,claimList,"claim");
//                   all.add("#####################claim###############################");
                   all.addAll(claimList);
                   map.put(PatentConstant.PATENT_CLAIMS,String.join(" ",claimList));
               }

           }
//            map.put("all",all);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return map;
    }

    /**
     * 将xml文件解析成txt文件，格式为：文件名_所述部分_句子内容
     * @param filePath
     * @param fileName
     * @return
     */
    public  synchronized static List<String> newAnalysisXmlToList(String filePath,String fileName){
        List<String> all = new ArrayList<>();
        try {
            SAXReader reader = GetXmlSAXReader(); //获取SAXReader
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
            List<String> name = new ArrayList<>();
            //使用selectNodes寻找node节点需要引包jaxen
            //获取name（专利名）
            Node nameNode= document.selectSingleNode("cn-patent-document/application-body/description/invention-title ");
            if(StringUtil.empty(nameNode.getText())){
                nameNode= document.selectSingleNode("cn-patent-document/application-body/description/invention-title/b ");
            }
            name.add(fileName+"_name_"+removeTextSpecial(nameNode.getText())+"。");
            all.addAll(name);

            //专利类别
            List<Node> classifyList= document.selectNodes("cn-patent-document/cn-bibliographic-data/classifications-ipcr/classification-ipcr");
            String classifies = "";
            if(classifyList != null && classifyList.size() != 0){
                classifies = getClassificationsNodeText(classifyList);
            }
            if(classifyList == null || classifyList.size() == 0){
                classifies = document.selectSingleNode("cn-patent-document/cn-bibliographic-data/classification-ipc/main-classification").getText();
            }
            all.add(fileName+"_classify_"+classifies);

            List<String> abstractList = new ArrayList<>();
            //获取摘要信息
            List<Node> ab_list= document.selectNodes("cn-patent-document/cn-bibliographic-data/abstract/p");
            if(ab_list != null && ab_list.size() > 0){
                getNodeText(ab_list,abstractList,"abstract",fileName);
                all.addAll(abstractList);
            }

            List<String> contentList = new ArrayList<>();
            //获取内容信息
            List<Node> body_list= document.selectNodes("cn-patent-document/application-body/description/p");
            if(body_list != null && body_list.size() > 0)
                getNodeText(body_list,contentList,"content",fileName);

            //获取内容信息
            List<Node> filed_list= document.selectNodes("cn-patent-document/application-body/description/technical-field/p");
            if(filed_list != null && filed_list.size() > 0)
                getNodeText(filed_list,contentList,"content",fileName);

            List<Node> background_list= document.selectNodes("cn-patent-document/application-body/description/background-art/p");
            if(background_list != null && background_list.size() > 0)
                getNodeText(background_list,contentList,"content",fileName);

            List<Node> disclosure_list= document.selectNodes("cn-patent-document/application-body/description/disclosure/p");
            if(disclosure_list != null && disclosure_list.size() > 0)
                getNodeText(disclosure_list,contentList,"content",fileName);

            List<Node> mode_list= document.selectNodes("cn-patent-document/application-body/description/mode-for-invention/p");
            if(mode_list != null && mode_list.size() > 0)
                getNodeText(mode_list,contentList,"content",fileName);

            List<Node> p_list= document.selectNodes("description/p");
            if(p_list != null && p_list.size() > 0)
                getNodeText(p_list,contentList,"content",fileName);

            all.addAll(contentList);

            List<String> claimList = new ArrayList<>();
            List<Node> claim_list= document.selectNodes("cn-patent-document/application-body/claims/claim/claim-text");
            if(claim_list != null && claim_list.size() > 0){
                getNodeText(claim_list,claimList,"claim",fileName);
                all.addAll(claimList);
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return all;
    }

    public  synchronized static List<String> getPatentNum(String filePath){
        List<String> all = new ArrayList<>();
        try {
            SAXReader reader = GetXmlSAXReader(); //获取SAXReader
            Document document = reader.read(new FileInputStream(new File(filePath)));
            /**
             *   cn-patent-document/cn-bibliographic-data/application-reference/document-id/doc-number 专利号
             *   cn-patent-document/cn-bibliographic-data/classifications-ipcr/classification-ipcr/text  专利分类
             *   或者
             *   cn-patent-document/cn-bibliographic-data/classification-ipc/main-classification 专利分类
             */
            List<String> name = new ArrayList<>();
            //使用selectNodes寻找node节点需要引包jaxen
            //获取name（专利名）
            Node patentNumNode= document.selectSingleNode("cn-patent-document/cn-bibliographic-data/application-reference/document-id/doc-number");
            name.add(patentNumNode.getText().replace(".",""));
            all.addAll(name);

            //专利类别
            List<Node> classifyList= document.selectNodes("cn-patent-document/cn-bibliographic-data/classifications-ipcr/classification-ipcr");
            String classifies = "";
            if(classifyList != null && classifyList.size() != 0){
                classifies = getClassificationsNodeText(classifyList);
            }
            if(classifyList == null || classifyList.size() == 0){
                classifies = document.selectSingleNode("cn-patent-document/cn-bibliographic-data/classification-ipc/main-classification").getText();
            }
            all.add(classifies);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return all;
    }
    /**
     * 将小类的xml文件解析成List,根据需要解析文件位置
     * 不需要的位置传入false即可
     * @param filePath
     * @return
     */
    public  synchronized static List<String> AnalysissmallXmlToList(String filePath,String classify){
        List<String> all = new ArrayList<>();
        try {
            SAXReader reader = GetXmlSAXReader(); //获取SAXReader
            Document document = reader.read(new FileInputStream(new File(filePath)));
            /**
             * 1、首先需要准备三个List，分别为name（专利名）、abstract（摘要）和body（内容）组装数据
             * 2、分别读取节点
             *   description/invention-title 专利名
             *   description/technical-field/p （多节点） 技术领域
             *   description/background-art/p  （多节点）  背景
             *   description/disclosure/p      （多节点） 公开信息
             *   description/description-of-drawings/p  （多节点）示例图说明 (不要)
             *   description/mode-for-invention/p  （多节点） 实施方案
             *
             *   或者
             *   description/p  （多节点） 内容
             * 3、对每个节点进行遍历，取出数据。
             * 4、对数据进行整理，整理过程包括，将数据按照句号进行截取，然后将每个句子装入对象中
             */
            List<String> name = new ArrayList<>();
            //使用selectNodes寻找node节点需要引包jaxen
            //获取name（专利名）
            Node nameNode= document.selectSingleNode("description/invention-title");
            name.add(nameNode.getText()+"。");
            all.add("#####################name################################");
            all.addAll(name);

            //专利类别
            all.add("#####################classify################################");
            all.add(classify);

            List<String> contentList = new ArrayList<>();
            //获取内容信息
            List<Node> filed_list= document.selectNodes("description/technical-field/p");
            if(filed_list != null && filed_list.size() > 0)
                getNodeText(filed_list,contentList,"application-body");

            List<Node> background_list= document.selectNodes("description/background-art/p");
            if(background_list != null && background_list.size() > 0)
                getNodeText(background_list,contentList,"application-body");

            List<Node> disclosure_list= document.selectNodes("description/disclosure/p");
            if(disclosure_list != null && disclosure_list.size() > 0)
                getNodeText(disclosure_list,contentList,"application-body");

            List<Node> mode_list= document.selectNodes("description/mode-for-invention/p");
            if(mode_list != null && mode_list.size() > 0)
                getNodeText(mode_list,contentList,"application-body");

            List<Node> p_list= document.selectNodes("description/p");
            if(p_list != null && p_list.size() > 0)
                getNodeText(p_list,contentList,"application-body");

            all.add("#####################content#############################");
            all.addAll(contentList);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return all;
    }

    public synchronized  static String getClassificationsNodeText(List<Node> nodes) throws Exception {
        String classify = "";
        for (Node node:nodes) {
            String text = node.selectSingleNode("text").getText();
            classify = classify + text +"||";
        }
        return classify.substring(0,classify.length() - 2);
    }

    /**
     * 将xml文件解析成List,根据需要解析文件位置
     * 不需要的位置传入false即可
     * @param filePath
     * @return
     */
    public  synchronized static List<String> AnalysisXmlToList3(String filePath,boolean isabstract,boolean iscontent,boolean isclaim
    ){
        List<String> all = new ArrayList<>();
        try {
            SAXReader reader = GetXmlSAXReader(); //获取SAXReader
            Document document = reader.read(new FileInputStream(new File(filePath)));
            /**
             * 1、首先需要准备三个List，分别为name（专利名）、abstract（摘要）和body（内容）组装数据
             * 2、分别读取节点
             *   cn-patent-document/application-body/description/invention-title 专利名
             *   cn-patent-document/cn-bibliographic-data/abstract/p 摘要
             *   cn-patent-document/application-body/description/p 内容
             *   cn-patent-document/application-body/claims/claim/claim-text 内容
             *   cn-patent-document/cn-bibliographic-data/classifications-ipcr/classification-ipcr/text  专利分类
             * 3、对每个节点进行遍历，取出数据。
             * 4、对数据进行整理，整理过程包括，将数据按照句号进行截取，然后将每个句子装入对象中
             */
            List<String> name = new ArrayList<>();
            //使用selectNodes寻找node节点需要引包jaxen
            //获取name（专利名）
            Node nameNode= document.selectSingleNode("cn-patent-document/application-body/description/invention-title ");
            name.add(nameNode.getText()+"。");
            all.add("#####################name################################");
            all.addAll(name);

            //专利类别
            Node typeNode= document.selectSingleNode("cn-patent-document/cn-bibliographic-data/classifications-ipcr/classification-ipcr/text");
            all.add("#####################classify################################");
            all.add(typeNode.getText());

            if(isabstract){
                List<String> abstractList = new ArrayList<>();
                //获取摘要信息
                List<Node> ab_list= document.selectNodes("cn-patent-document/cn-bibliographic-data/abstract/p");
                getNodeText(ab_list,abstractList,"abstract");
                all.add("#####################abstract############################");
                all.addAll(abstractList);
            }
            if(iscontent){
                List<String> contentList = new ArrayList<>();
                //获取内容信息
                List<Node> body_list= document.selectNodes("cn-patent-document/application-body/description/p");
                getNodeText(body_list,contentList,"application-body");
                all.add("#####################content#############################");
                all.addAll(contentList);
            }
            if(isclaim){
                List<String> claimList = new ArrayList<>();
                List<Node> claim_list= document.selectNodes("cn-patent-document/application-body/claims/claim/claim-text");
                getNodeText(claim_list,claimList,"claim");
                all.add("#####################claim###############################");
                all.addAll(claimList);
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return all;
    }

    public  synchronized static List<String> AnalysisXmlToList2(String filePath,boolean isabstract,boolean iscontent,boolean isclaim
    ){
        List<String> all = new ArrayList<>();
        try {
            SAXReader reader = GetXmlSAXReader(); //获取SAXReader
            Document document = reader.read(new FileInputStream(new File(filePath)));
            /**
             * 1、首先需要准备三个List，分别为name（专利名）、abstract（摘要）和body（内容）组装数据
             * 2、分别读取节点
             *   cn-patent-document/application-body/description/invention-title 专利名
             *   cn-patent-document/cn-bibliographic-data/abstract/p 摘要
             *   cn-patent-document/application-body/description/p 内容
             *   cn-patent-document/application-body/claims/claim/claim-text 内容
             *   cn-patent-document/cn-bibliographic-data/classifications-ipcr/classification-ipcr/text  专利分类
             * 3、对每个节点进行遍历，取出数据。
             * 4、对数据进行整理，整理过程包括，将数据按照句号进行截取，然后将每个句子装入对象中
             */
            List<String> name = new ArrayList<>();
            //使用selectNodes寻找node节点需要引包jaxen
            //获取name（专利名）
            Node nameNode= document.selectSingleNode("cn-patent-document/application-body/description/invention-title ");
            name.add(nameNode.getText()+"。");
            all.addAll(name);

            //专利类别
            Node typeNode= document.selectSingleNode("cn-patent-document/cn-bibliographic-data/classifications-ipcr/classification-ipcr/text");
            all.add(typeNode.getText());

            if(isabstract){
                List<String> abstractList = new ArrayList<>();
                //获取摘要信息
                List<Node> ab_list= document.selectNodes("cn-patent-document/cn-bibliographic-data/abstract/p");
                getNodeText(ab_list,abstractList,"abstract");
                all.add("#####################abstract############################");
                all.addAll(abstractList);
            }
            if(iscontent){
                List<String> contentList = new ArrayList<>();
                //获取内容信息
                List<Node> body_list= document.selectNodes("cn-patent-document/application-body/description/p");
                getNodeText(body_list,contentList,"application-body");
                all.add("#####################content#############################");
                all.addAll(contentList);
            }
            if(isclaim){
                List<String> claimList = new ArrayList<>();
                List<Node> claim_list= document.selectNodes("cn-patent-document/application-body/claims/claim/claim-text");
                getNodeText(claim_list,claimList,"claim");
                all.add("#####################claim###############################");
                all.addAll(claimList);
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return all;
    }

    /**
     * 获取xml中的专利类别，生成update语句
     * @param reader
     * @param xmlDir
     * @return
     */
    public  synchronized static List<String> getXmlClassify(SAXReader reader,String xmlDir ){
        List<String> allList = new ArrayList<>();
        try {
            List<String> pathName = new ArrayList<String>();
            List<String> filenames = FileUtils.iteratorPath(xmlDir,pathName);//遍历xml所在目录，得到xml文件路径信息
            for (String str:filenames ) {
                System.out.println(str);
                Document document = reader.read(new FileInputStream(new File(xmlDir+"\\"+str)));
                //专利类别
                Node typeNode= document.selectSingleNode("cn-patent-document/cn-bibliographic-data/classifications-ipcr/classification-ipcr/text");
                String sql = "UPDATE patent_distance SET classify = '"+typeNode.getText()+"' WHERE patentFile = '"+str+"';";
                allList.add(sql);
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return allList;
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
    /**
     * 读取xml中的namenode，然后获取其后的截取部分的统计
     * @param xmlDir
     * @param n
     * @return
     */
    public  synchronized static List<String> GetXmlPatentNameSplit2(String xmlDir,int n){
        try {
            List<String> pathName = new ArrayList<String>();
            List<String> returnStr = new ArrayList<String>();
            List<String> filenames = FileUtils.iteratorPath(xmlDir,pathName);//遍历xml所在目录，得到xml文件路径信息
            SAXReader reader = GetXmlSAXReader(); //获取SAXReader
            Map<String,Object> patentMap = new HashMap<>();
            for (String str:filenames ) {
                Document document = reader.read(new FileInputStream(new File(xmlDir+"\\"+str)));
                //使用selectNodes寻找node节点需要引包jaxen
                //获取name（专利名）
                Node nameNode= document.selectSingleNode("cn-patent-document/application-body/description/invention-title ");
                String nameText = nameNode.getText();
                if(! StringUtil.empty(nameText)){
                    int m = nameText.length();
                    if(m >= n){
                        String suff = nameText.substring(m - n,m);
                        patentMap.put(suff,patentMap.get(suff) == null ? 1 : (Integer)patentMap.get(suff) + 1);
                    }

                }
            }
            for(String suffix : patentMap.keySet()){
                String s = suffix + "," + patentMap.get(suffix);
                returnStr.add(s);
            }
            return returnStr;
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }
    public synchronized  static String removeTextSpecial(String text) throws Exception {
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
    /**
     * 对节点的text文本进行分析，找出我们需要的句子
     * @param nodes
     * @param list
     * @param word
     * @return
     * @throws Exception
     */
    public synchronized  static Object analysisNodeTextByword(List<Node> nodes,List<String> list,String word,Map<String,Object> map) throws Exception {
        for (Node node:nodes) {
            String text = removeTextSpecial(node.getText());
            String[] ss = text.split("。");
            boolean flag = false;
            for(String s : ss){
                if(s.length() > 1 && s.contains(word)){//首先这一句包含传入的word
                   String[] strs = s.split(word);//可能这一句中包含多个word，所以对句子进行切分
                   for(String str : strs){
                       /**
                        * 判断切分后的句子首先需要包含“的“字，然后取最后一个”的“字以后是否包含”，“
                        * 如果不包含”，“则该句是我们需要的
                        */
                       if(str.contains("的") && ! str.substring(str.lastIndexOf("的")).contains(",")){
                           flag = true;
                       }
                   }
                   if(flag){
                       list.add(s + "。");
                       map.put("count",(Integer)map.get("count")+1);
                   }
                }
                flag = false;
            }
            System.gc();
        }
        return list;
    }

    /**
     * 获取namenode节点的text文本
     * @param reader
     * @param xmlFilePath
     * @return
     * @throws Exception
     */
    public  synchronized static String findAndMatchWord(SAXReader reader ,String xmlFilePath) throws Exception{
        Document document = reader.read(new FileInputStream(new File(xmlFilePath)));
        //使用selectNodes寻找node节点需要引包jaxen
        //获取name（专利名）
        Node nameNode= document.selectSingleNode("cn-patent-document/application-body/description/invention-title ");
        String nameText = nameNode.getText();
        return nameText;
    }

    /**
     * 根据productName遍历查询xmlFilePath文件中的每一行，找出包括productName的sentence
     * @param xmlFilePath
     * @param productName
     * @return
     * @throws Exception
     */
    public  synchronized  List<List<Map<String,String>>> findSentencesContainProductName(String xmlFilePath,String patentName,String productName) throws Exception{
        List<List<Map<String,String>>> all = new ArrayList<>();
        SAXReader reader = GetXmlSAXReader(); //获取SAXReader
        Document document = reader.read(new FileInputStream(new File(xmlFilePath)));
        List<Map<String,String>> abstractMap = getPartNodeTexts(document, "abstract",productName,patentName,xmlFilePath.substring(xmlFilePath.lastIndexOf("\\")+1));
        List<Map<String,String>> contentMap = getPartNodeTexts(document, "content",productName,patentName,xmlFilePath.substring(xmlFilePath.lastIndexOf("\\")+1));
        List<Map<String,String>> claimMap = getPartNodeTexts(document, "claim",productName,patentName,xmlFilePath.substring(xmlFilePath.lastIndexOf("\\")+1));
        all.add(abstractMap);
        all.add(contentMap);
        all.add(claimMap);
        return  all;
    }

    /**
     * 获取namenode节点的text文本
     * @param reader
     * @param xmlFilePath
     * @return
     * @throws Exception
     */
    public  synchronized static String getNodeText(SAXReader reader ,String xmlFilePath) throws Exception{
            Document document = reader.read(new FileInputStream(new File(xmlFilePath)));
            //使用selectNodes寻找node节点需要引包jaxen
            //获取name（专利名）
            Node nameNode= document.selectSingleNode("cn-patent-document/application-body/description/invention-title ");
            String nameText = nameNode.getText();
            return nameText;
    }


    /**
     * 获取不同节点的text文本
     * @param document
     * @param partName
     * @return
     * @throws Exception
     */
    public  synchronized  List<Map<String,String>> getPartNodeTexts(Document document ,String partName,String productName,String patentName,String xmlFlieName) throws Exception{
        List<Map<String,String>> nodeMap = new ArrayList<>();
        if(partName.equals("abstract")){
            //获取摘要信息
            List<Node> ab_list= document.selectNodes("cn-patent-document/cn-bibliographic-data/abstract/p");
            getNodeTextByProductNamew(ab_list,nodeMap,productName,partName,patentName,xmlFlieName);
        }
        if(partName.equals("content")){
            //获取内容信息
            List<Node> body_list= document.selectNodes("cn-patent-document/application-body/description/p");
            getNodeTextByProductNamew(body_list,nodeMap,productName,partName,patentName,xmlFlieName);
        }
        if(partName.equals("claim")){
            List<Node> claim_list= document.selectNodes("cn-patent-document/application-body/claims/claim/claim-text");
            getNodeTextByProductNamew(claim_list,nodeMap,productName,partName,patentName,xmlFlieName);
        }
        return nodeMap;
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

    public synchronized  static Object getNodeText(List<Node> nodes,List<String> list,String type,String fileName) throws Exception {
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
     * 获取包含productName的节点中的文本信息
     * @param nodes
     * @param list
     * @param productName
     * @return
     * @throws Exception
     */
    public synchronized  Object getNodeTextByProductNamew(List<Node> nodes,List<Map<String,String>> list,String productName,String part,String patentName,String xmlFlieName) throws Exception {
        for (Node node:nodes) {
            String text = removeTextSpecial(node.getText());
            String[] ss = text.split("。");
            for(String s : ss){
                if(s.length() > 1 && s.contains(productName)){
                    Map<String,String> map = new HashMap<>();
                    map.put("id", StringUtil.getUUID());
                    map.put("sentence",s + "。");
                    map.put("part",part);
                    map.put("patentFile",xmlFlieName);
                    map.put("patentName",patentName);
                    map.put("productName",productName);
                    list.add(map);
                }
            }
            System.gc();
        }
        return list;
    }
}
