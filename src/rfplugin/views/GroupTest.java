package rfplugin.views;

import java.util.ArrayList;
import java.util.List;

public class GroupTest {
	String groupName;
	List<Test> tests = new ArrayList<Test>();

	public GroupTest(String groupName) {
		this.groupName = groupName;
	}

	public void AddTest(Test test) {
		tests.add(test);
	}

	public String getGroupName() {
		return groupName;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof GroupTest))
			return false;
		if (obj == this)
			return true;
		return this.groupName.equals(((GroupTest) obj).groupName);
	}

	public int hashCode() {
		return groupName.length();
	}

	@Override
	public String toString() {
		return groupName;
	}
}
