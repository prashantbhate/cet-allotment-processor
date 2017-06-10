package com.bhate.cet.allotment;

import java.util.concurrent.atomic.AtomicInteger;

public class Allotment
{	private static AtomicInteger COUNTER = new AtomicInteger();

	final String collegeName;
	final String branchName;
	final String quota;
	final long cutoffRank;
	private final int id;

	public Allotment(String collegeName, String branchName, String quota, String cutoffRank) {
		this.collegeName = collegeName;
		this.branchName = branchName;
		this.quota = quota;
		this.cutoffRank = Long.valueOf(cutoffRank);
		id = COUNTER.incrementAndGet();
	}

	public static String csvHeader() {
		return String.format("\"%s\",\"%s\",\"%s\",\"%s\"\n", "CutoffRank", "BranchName", "CollegeName", "Quota");
	}

	//@JsonProperty("college-name")
	public String getCollegeName() {
		return collegeName;
	}

	//@JsonProperty("branch-name")
	public String getBranchName() {
		return branchName;
	}

	public String getQuota() {
		return quota;
	}

	//@JsonProperty("cutoff-rank")
	public long getCutoffRank() {
		return cutoffRank;
	}

	@Override
	public String toString() {
		return String.format("%-14.14s %4d %-60.60s %-3.3s", branchName, cutoffRank, collegeName, quota);
	}

	public String toCSV() {
		return String.format("\"%s\",\"%s\",\"%s\",\"%s\"\n", branchName, cutoffRank, collegeName, quota);
	}

	public int getId() {
		return id;
	}
}