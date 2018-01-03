package edu.iris.dmc.extensions.utils;

import edu.iris.dmc.criteria.Quality;

public class QualityLookup {

	public static Quality getQualityFromChar(Character c) {
		if (c == null) 
			return Quality.B;
		else if (c == 'B') 
			return Quality.B;
		else if (c == 'D')
			return Quality.D;
		else if (c == 'E')
			return Quality.E;
		else if (c == 'M') 
			return Quality.M;
		else if (c == 'Q') 
			return Quality.Q;
		else if (c == 'R')
			return Quality.R;
		else
			return Quality.B;
		
	}
}
