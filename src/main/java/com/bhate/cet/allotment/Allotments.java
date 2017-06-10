package com.bhate.cet.allotment;

import java.util.List;

/**
 * Created by pb on 14/06/2017.
 */
public class Allotments {
	private final List<Allotment> allotments;

	public Allotments(List<Allotment> allotments) {
		this.allotments = allotments;
	}

	public List<Allotment> getAllotments() {
		return allotments;
	}
}
