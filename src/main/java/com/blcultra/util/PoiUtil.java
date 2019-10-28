package com.blcultra.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.xwpf.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


public class PoiUtil {

	/**
	 * 替换Word里面的变量
	 *
	 * @param pactUrl
	 * @param storeList
	 * @param wordPath
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public static String replaceParams(String pactUrl, Map<String,Object> params, List<Map<String,Object>> storeList,
									   String wordPath, String uuid) throws Exception {
		InputStream in = null;
		OutputStream os = null;
		try {
//			File file = new File(pactUrl);
			Resource resource = new ClassPathResource("file/report-template.docx");
			File file = resource.getFile();
			in = new FileInputStream(file);
			XWPFDocument docx = new XWPFDocument(in);
			PoiUtil util = new PoiUtil();
			// 替换段落里的变量
			util.replaceInpara(docx, params);

			// 替换
			util.replaceStoreTable(docx, storeList);

			// 存放在临时目录中
			String tempFile = wordPath + File.separator + uuid +"report" + ".docx";
			File _file = new File(wordPath);
			// 如果文件夹不存在，则创建之
			if (!_file.exists()) {
				_file.mkdirs();
			}
			os = new FileOutputStream(tempFile);
			docx.write(os);
			return tempFile;
		} catch (FileNotFoundException e) {
			throw new Exception("对应的协议模板丢失！");
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("将协议中的变量进行替换出错！");
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 复制表格里的一行
	 *
	 * @param sourceRow
	 * @param targetRow
	 * @throws Exception
	 */
	private void copyRow(XWPFTableRow sourceRow, XWPFTableRow targetRow) throws Exception {
		List<XWPFTableCell> sourceCells = sourceRow.getTableCells();
		int size = sourceCells.size();
//		targetRow.removeCell(0);
		XWPFTableCell cell =null;
		for (int i = 0; i < size; i++) {
			XWPFTableCell sourceCell = sourceCells.get(i);
			if (i==0){
			cell = targetRow.getCell(i);
			}else {
				cell = targetRow.createCell();
			}
			String str = sourceCell.getText();
			setCellText(sourceCell, cell, str);
		}
	}

	/**
	 * 将某个实体类转换成Map<K,V> 利用反射来做
	 */
	private Map<String, Object> transformBean(Object bean) throws Exception {
		if (bean != null) {
			Map<String, Object> beanMap = new HashMap<String, Object>();
			// 获得类的运行时
			Class<?> c = bean.getClass();
			// 获得类的属性
			Field[] cfs = c.getDeclaredFields();
			for (Field cf : cfs) {
				// 属性的名称
				String cfName = cf.getName();
				// 如果是serialVersionUID则直接跳过
				if ("serialVersionUID".equals(cfName)) {
					continue;
				}
				// 属性对应的get方法
				String methodName = "get" + cfName.substring(0, 1).toUpperCase() + cfName.substring(1);
				Method getMethod = c.getMethod(methodName, new Class[] {});
				// 获得属性对应的值
				Object cfValue = getMethod.invoke(bean, new Object[] {});
				beanMap.put(cfName, cfValue);
			}
			return beanMap;
		} else {
			return null;
		}
	}

	/**
	 * 替换文档里的变量
	 *
	 * @param docx
	 * @param params
	 */
	private void replaceInpara(XWPFDocument docx, Map<String, Object> params) {
		Iterator<XWPFParagraph> iterator = docx.getParagraphsIterator();
		XWPFParagraph para;
		while (iterator.hasNext()) {
			para = iterator.next();
			this.replaceInpara(para, params);
		}
	}

	private void replaceInpara(XWPFParagraph para, Map<String, Object> params) {
		List<XWPFRun> runs = null;
		Set<String> keySet = params.keySet();
		String paraStr = para.getParagraphText();
		if (this.findText(paraStr, keySet)) {
			runs = para.getRuns();
			for (int i = 0; i < runs.size(); i++) {
				XWPFRun run = runs.get(i);
				String runText = run.toString();
				Iterator<String> ite = keySet.iterator();
				while (ite.hasNext()) {
					String str = ite.next();
					if (runText.equals(str)) {
						runText = params.get(str) + "";
						break;
					}
				}
				para.removeRun(i);
				XWPFRun _run = para.insertNewRun(i);
				_run.setText(runText);
				_run.setFontSize(12);
			}
		}
	}

	private boolean findText(String str, Set<String> keySet) {
		for (String key : keySet) {
			if (str.contains(key)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 设置单元格文字属性
	 *
	 * @param tmpCell
	 * @param cell
	 * @param text
	 * @throws Exception
	 */
	public void setCellText(XWPFTableCell tmpCell, XWPFTableCell cell, String text) throws Exception {
		XWPFParagraph tmpP = tmpCell.getParagraphs().get(0);
		XWPFParagraph cellP = cell.getParagraphs().get(0);
		XWPFRun tmpR = null;
		if (tmpP.getRuns() != null && tmpP.getRuns().size() > 0) {
			tmpR = tmpP.getRuns().get(0);
		}
		XWPFRun cellR = cellP.createRun();
		cellR.setText(text);
	}

	/**
	 * 替换
	 *
	 * @param docx
	 * @param beanList
	 * @throws Exception
	 */
	private void replaceStoreTable(XWPFDocument docx, List<Map<String,Object>> beanList) throws Exception {
		// 获得文档中的table列表
		List<XWPFTable> tables = docx.getTables();
		// 遍历表格，侦测其中第三行的第一个单元格，内容为type，则这个就是我们要替换变量的表格
		XWPFTable table = null;
		for (XWPFTable xt : tables) {
			XWPFTableRow row = xt.getRow(2);
			XWPFTableCell cell = row.getCell(0);
			String text = cell.getText();
			if (text.contains("type")) {
				// 取得对于的table
				table = xt;
				break;
			}
		}
		if (table == null) {
			return;
		}
		// 取得table的第三行，作为模板行
		XWPFTableRow modelRow = table.getRow(2);
		//
		int size = beanList.size();
		for (int i = 0; i < size; i++) {
			// 在table中新增一行
			XWPFTableRow row = table.createRow();
			// 将模板行复制到新增行中
			this.copyRow(modelRow, row);

			Map<String,Object> params = beanList.get(i);
			// 获得行中的列
			List<XWPFTableCell> cells = row.getTableCells();
			for (int j = 0; j < cells.size(); j++) {
				// 获得某一列
				XWPFTableCell cell = cells.get(j);
				// 获得列中的内容
				List<XWPFParagraph> paras = cell.getParagraphs();
				for (XWPFParagraph para : paras) {
					// 对内容进行参数替换
					this.replaceInpara(para, params);
				}
			}
		}
		// 删除模板表格
		table.removeRow(2);
	}

}
