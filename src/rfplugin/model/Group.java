package rfplugin.model;

import java.util.ArrayList;
import java.util.List;

import rfplugin.views.Test;

public class Group {
	private String groupName;
	private List<Test> tests = new ArrayList<Test>();

	public Group(String groupName) {
		this.groupName = groupName;
	}
	
	public Group(String groupName, Test test) {
		this.groupName = groupName;
		this.tests.add(test);
	}

	public String getGroupName() {
		return groupName;
	}
	
	public List<Test> getTests(){
		return tests;
	}
	
	public void setTests(List<Test> tests){
		this.tests = tests;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Group))
			return false;
		if (obj == this)
			return true;
		return this.groupName.equals(((Group) obj).groupName);
	}

	public int hashCode() {
		return groupName.length();
	}

	@Override
	public String toString() {
		return groupName;
	}
}
