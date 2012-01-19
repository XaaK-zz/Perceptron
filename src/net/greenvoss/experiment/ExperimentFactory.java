package net.greenvoss.experiment;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory class - returns a set of Experiments to execute
 */
public class ExperimentFactory {
	public static List<ExperimentBase> getExperiments() {
		ArrayList<ExperimentBase> list = new ArrayList<ExperimentBase>();
		
		list.add(new ExperimentOne());
		//list.add(new ExperimentTwo());
		//list.add(new ExperimentThree());
		//list.add(new ExperimentFour());
		//list.add(new ExperimentFive());
		
		return list;
	}
}
