package com.bhate.cet.allotment;

/**
 * Created by pb on 11/06/2017.
 */
public class Result {
	private final String type;
	private final Allotment attributes;

	public Result(Allotment attributes) {
		this.attributes = attributes;
		type = "allotment";
	}

	public Allotment getAttributes() {
		return attributes;
	}

	public String getType() {
		return type;
	}

	public int getId() {
		return attributes.getId();
	}
}
