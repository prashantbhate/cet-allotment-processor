package com.bhate.cet.allotment;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by pb on 11/06/2017.
 */
public class Result {
	static AtomicInteger COUNTER = new AtomicInteger();
	private final int id;
	private final String type;
	private final Allotment attributes;

	public Result(Allotment attributes) {
		this.id = COUNTER.incrementAndGet();
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
		return id;
	}
}
