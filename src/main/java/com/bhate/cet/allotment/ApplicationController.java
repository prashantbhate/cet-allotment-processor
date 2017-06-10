package com.bhate.cet.allotment;

import static java.util.Comparator.comparingLong;
import static java.util.Comparator.naturalOrder;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class ApplicationController {


	@Autowired
	private AllotmentProcessor allotmentProcessor;


	@RequestMapping("/allotments")
	public Allotments getAllotments(@RequestParam(value = "quota", defaultValue = "") String quota,
									@RequestParam(value = "branch", defaultValue = "") String branch,
									@RequestParam(value = "college", defaultValue = "") String collegeName,
									@RequestParam(value = "lowCutoffRank", defaultValue = "0") int lowCutoffRank,
									@RequestParam(value = "highCutoffRank", defaultValue = "90000") int highCutoffRank) {
		System.out.printf("quota=%s branch=%s collegeName=%s %n", quota, branch, collegeName);
		final List<Allotment> list = allotmentProcessor.getAllAllotments();
		final Pattern quotaPattern = Pattern.compile(quota);
		final Pattern branchPattern = Pattern.compile(quota);
		final Pattern collegePattern = Pattern.compile(quota);


		final Predicate<Allotment> dataFilter = data -> {
			if (data.cutoffRank < highCutoffRank)
				if (data.cutoffRank > lowCutoffRank)
								return true;
			return false;
		};
		final Predicate<Allotment> regexFilter = data -> {
			if (quotaPattern.matcher(data.quota)
							.find())
				if (branchPattern.matcher(data.branchName)
								 .find())
					if (collegePattern.matcher(data.collegeName)
									  .find())
								return true;
			return false;
		};
		final Predicate<Allotment> substringFilter = data -> {
			if (data.quota.contains(quota))
				if (data.branchName.contains(branch))
					if (data.collegeName.contains(collegeName))
						return true;
			return false;
		};

		List<Allotment> allotments = list.stream()
//										 .filter(dataFilter)
//										 .filter(substringFilter)
										 .sorted(comparingLong(d -> d.cutoffRank))
//										 .limit(15)
										 .collect(toList());
		return new Allotments(allotments);
	}

	@RequestMapping("/allotments1")
	public List<Result> getAllotments1() {
		final List<Allotment> list = allotmentProcessor.getAllAllotments();
		List<Result> results = list.stream()
								   .sorted(comparingLong(d -> d.cutoffRank))
								   .limit(15)
								   .map(Result::new)
								   .collect(toList());
		return results;
	}

	@RequestMapping("/colleges")
	public Set<String> getColleges() {
		return mapAllotmentWith(d -> d.collegeName);
	}

	private Set<String> mapAllotmentWith(Function<Allotment, String> mapper) {
		final List<Allotment> allotments = allotmentProcessor.getAllAllotments();
		Set<String> colleges = allotments.stream()
										 .map(mapper)
										 .distinct()
										 .sorted(naturalOrder())
										 .collect(toSet());
		return colleges;
	}

	@RequestMapping("/branches")
	public Set<String> getBranches() {
		return mapAllotmentWith(d -> d.branchName);
	}

	@RequestMapping("/quotas")
	public Set<String> getQuotas() {
		return mapAllotmentWith(d -> d.quota);
	}

}
