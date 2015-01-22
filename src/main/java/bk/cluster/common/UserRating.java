package bk.cluster.common;

import java.util.HashMap;

public class UserRating {
	private HashMap<String, String> movieIdRating;
	
	public HashMap<String, String> getMoveIdRating() {
		return movieIdRating;
	}
	public void setMoveIdRating(HashMap<String, String> movieIdRating) {
		this.movieIdRating = movieIdRating;
	}
}
