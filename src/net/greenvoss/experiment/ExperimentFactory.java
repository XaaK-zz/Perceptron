package net.greenvoss.experiment;

import java.util.ArrayList;
import java.util.List;

public class ExperimentFactory {
	public static List<ExperimentBase> getExperiments() {
		ArrayList<ExperimentBase> list = new ArrayList<ExperimentBase>();
		
		list.add(new ExperimentOne());
		
		return list;
	}
}
