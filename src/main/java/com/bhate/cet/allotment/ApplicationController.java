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

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.collect.ImmutableList;

@RestController
@CrossOrigin
@Validated
@RequestMapping(value = "/api/v1.2")
public class ApplicationController {
	public static final ImmutableList<String> CUTOFF_FILES = ImmutableList.of("engg_cutoff_2016.pdf", "engg_cutoff_mock_2017.pdf");
	@Autowired
	private AllotmentProcessor allotmentProcessor;

	@DeleteMapping(value = "/logout", produces = "application/json; charset=UTF-8")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void logout(HttpSession session) {
		session.invalidate();
	}

	@GetMapping(value = "/login", produces = "application/json; charset=UTF-8")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void login() {
	}


	@GetMapping(value = "/cutoff/files", produces = "application/json; charset=UTF-8")
	public List<String> cutoffFiles() {
		return CUTOFF_FILES;
	}

	@GetMapping(value = "/cutoff/files/current", produces = "application/json; charset=UTF-8")
	@ResponseStatus(HttpStatus.OK)
	public String currentFile(@SessionAttribute("MY_CUTOFF_FILE") final String cutoffFile) {
		return "{\"fileName\":\"" + cutoffFile + "\"}";
	}

	@PostMapping(value = "/cutoff/files/current", produces = "application/json; charset=UTF-8")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> login(@RequestParam(value = "cutoffFile", defaultValue = "") @Valid
										@javax.validation.constraints.Pattern(regexp = "engg_cutoff_2016.pdf|engg_cutoff_mock_2017.pdf")
											String cutoffFile,
										HttpSession session,
										UriComponentsBuilder b) {
		session.setAttribute("MY_CUTOFF_FILE", cutoffFile);
		UriComponents uriComponents = b.path("/cutoff/files/current")
									   .buildAndExpand();
		return ResponseEntity.created(uriComponents.toUri())
							 .build();
	}

	@GetMapping(value = "/allotments", produces = "application/json; charset=UTF-8")
	public Allotments getAllotments(@RequestParam(value = "quota", defaultValue = "", required = false) String quota,
									@RequestParam(value = "branch", defaultValue = "", required = false) String branch,
									@RequestParam(value = "college", defaultValue = "", required = false)
										String college,
									@RequestParam(value = "lowCutoffRank", defaultValue = "0", required = false)
										int lowCutoffRank,
									@RequestParam(value = "highCutoffRank", defaultValue = "90000", required = false)
										int highCutoffRank,
									@RequestParam(value = "limit", defaultValue = "15", required = false) int limit,
									@RequestParam(value = "useRegexFilter", defaultValue = "false", required = false)
										boolean useRegexFilter,
									@SessionAttribute("MY_CUTOFF_FILE") final String cutoffFile) {
		System.out.printf("quota=%s branch=%s college=%s %n", quota, branch, college);
		final List<Allotment> list = allotmentProcessor.getAllAllotments(cutoffFile);
		final Predicate<Allotment> dataFilter = data -> {
			if (data.cutoffRank < highCutoffRank)
				if (data.cutoffRank > lowCutoffRank)
					return true;
			return false;
		};
		final Predicate<Allotment> predicate;
		if (useRegexFilter) {
			final Predicate<Allotment> regexFilter = regexFilter(quota, branch, college);
			predicate = regexFilter;
		} else {
			final Predicate<Allotment> substringFilter = data -> {
				if (data.quota.contains(quota))
					if (data.branchName.contains(branch))
						if (data.collegeName.contains(college))
							return true;
				return false;
			};
			predicate = substringFilter;
		}
		List<Allotment> allotments = list.stream()
										 .filter(dataFilter)
										 .filter(predicate)
										 .sorted(comparingLong(d -> d.cutoffRank))
										 .limit(limit)
										 .collect(toList());
		return new Allotments(allotments);
	}

	private Predicate<Allotment> regexFilter(String quota,
											 String branch,
											 String college) {
		final Pattern quotaPattern = Pattern.compile(quota);
		final Pattern branchPattern = Pattern.compile(branch);
		final Pattern collegePattern = Pattern.compile(college);
		return data -> {
			if (quotaPattern.matcher(data.quota)
							.find())
				if (branchPattern.matcher(data.branchName)
								 .find())
					if (collegePattern.matcher(data.collegeName)
									  .find())
						return true;
			return false;
		};
	}

	@GetMapping(value = "/jsonapi/allotments", produces = "application/json; charset=UTF-8")
	public List<Result> getAllotments1(@SessionAttribute("MY_CUTOFF_FILE") final String cutoffFile) {
		final List<Allotment> list = allotmentProcessor.getAllAllotments(cutoffFile);
		List<Result> results = list.stream()
								   .sorted(comparingLong(d -> d.cutoffRank))
								   .limit(15)
								   .map(Result::new)
								   .collect(toList());
		return results;
	}

	@GetMapping(value = "/colleges", produces = "application/json; charset=UTF-8")
	public Set<String> getColleges(@SessionAttribute("MY_CUTOFF_FILE") final String cutoffFile) {
		return mapAllotmentWith(d -> d.collegeName, cutoffFile);
	}

	private Set<String> mapAllotmentWith(Function<Allotment, String> mapper,
										 String cutoffFile) {
		final List<Allotment> allotments = allotmentProcessor.getAllAllotments(cutoffFile);
		Set<String> colleges = allotments.stream()
										 .map(mapper)
										 .distinct()
										 .sorted(naturalOrder())
										 .collect(toSet());
		return colleges;
	}

	@GetMapping(value = "/branches", produces = "application/json; charset=UTF-8")
	public Set<String> getBranches(@SessionAttribute("MY_CUTOFF_FILE") final String cutoffFile) {
		return mapAllotmentWith(d -> d.branchName, cutoffFile);
	}

	@GetMapping(value = "/quotas", produces = "application/json; charset=UTF-8")
	public Set<String> getQuotas(@SessionAttribute("MY_CUTOFF_FILE") final String cutoffFile) {
		return mapAllotmentWith(d -> d.quota, cutoffFile);
	}
}