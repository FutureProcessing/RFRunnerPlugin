package rfplugin.views;

import java.util.ArrayList;
import java.util.List;

public class Group {
	String groupName;
	List<Test> tests = new ArrayList<Test>();

	public Group(String groupName) {
		this.groupName = groupName;
	}
	
	public Group(String groupName, Test test) {
		this.groupName = groupName;
		this.tests.add(test);
	}

	public void AddTest(Test test) {
		tests.add(test);
	}

	public String getGroupName() {
		return groupName;
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
