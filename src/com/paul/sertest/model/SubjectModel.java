package com.paul.sertest.model;

/**
 * Token ����ģ��
 * @author XY
 *
 */
public class SubjectModel {

	private int workerId;
	private String workerName;
	private String workerPosition;
	
	
	public SubjectModel(int uid, String uty,String utx) {
		super();
		this.workerId = uid;
		this.workerName = uty;
		this.workerPosition = utx;
	}

	public int getworkerId() {
		return workerId;
	}

	public void setworkerId(int uid) {
		this.workerId = uid;
	}

	public String getworkerName() {
		return workerName;
	}

	public void setworkerName(String uty) {
		this.workerName = uty;
	}
	
	public String getworkerPosition() {
		return workerPosition;
	}

	public void setworkerPosition(String utx) {
		this.workerPosition = utx;
	}
}
