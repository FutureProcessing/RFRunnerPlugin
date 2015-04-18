package rfplugin.model;

import java.io.File;

public class Test {
	private String testName;
	private File file;
	private String status;
	private long startTime;
	private long endTime;
	
	public Test() {
	}

	public Test(String testName, File file, String status) {
		this.testName = testName;
		this.file = file;
		this.status = status;
	}
	
	public String getTestName(){
		return testName;
	}
	
	public File getFile(){
		return file;
	}
	
	public String getStatus(){
		return status;
	}
	
	public void setStatus(String status){
		this.status = status;
	}
	
	public long getStartTime(){
		return startTime;
	}
	
	public void setStartTime(long startTime){
		this.startTime = startTime;
	}
	
	public long getEndTime(){
		return endTime;
	}
	
	public void setEndTime(long endTime){
		this.endTime = endTime;
	}
}
