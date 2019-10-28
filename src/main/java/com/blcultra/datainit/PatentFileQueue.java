package com.blcultra.datainit;

import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingQueue;

/**
 *存储专利文件名队列
 */
@Service("patentFileQueue")
public class PatentFileQueue {
	private LinkedBlockingQueue<String> lbq;
	private volatile static PatentFileQueue fileQueue;
	
	public PatentFileQueue() {
		lbq = new LinkedBlockingQueue<String>();
	}
	
	public static PatentFileQueue getInstance() {
		if (fileQueue == null) {
			synchronized (PatentFileQueue.class) {
				if (fileQueue == null) {
					fileQueue = new PatentFileQueue();
				}
			}
		}
		return fileQueue;
	}
	
	public void offer(String bson){
		lbq.offer(bson);
	}
	
	public String poll(){
		return lbq.poll();
	}
	
	public int size(){
		return lbq.size();
	}
}
