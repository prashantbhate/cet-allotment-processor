package com.bhate.cet.allotment;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by pb on 15/06/2017.
 */
public class AllotmentProcessorTest {

	private AllotmentProcessor processor;
	private List<Allotment> allAllotments;

	@Before
	public void setUp() throws Exception {
		processor = new AllotmentProcessor();
		processor.setAllotmentDao(new AllotmentDao());
		allAllotments = processor.getAllAllotments("engg_cutoff_2016.pdf");
	}

	@Test
	public void getAllAllotmentsCSV() throws Exception {
		//		allAllotments.stream()
		//					 .map(Allotment::toCSV)
		//					 .forEach(System.out::print);
	}

	@Test
	public void getAllAllotments() throws Exception {
		assertStreamElementCount(s -> String.valueOf(s.id), 10025L);
	}

	@Test
	public void getAllAllotmentsBranches() throws Exception {
		assertStreamElementCount(s -> s.branchName, 35L);
	}

	@Test
	public void getAllAllotmentsQuota() throws Exception {
		assertStreamElementCount(s -> s.quota, 24L);
	}

	private Stream<String> assertStreamElementCount(Function<Allotment, String> mapper, long expectedCount) {
		final Stream<String> distinct = allAllotments.stream()
													 .map(mapper)
													 .distinct();
		assertThat(distinct.count(), is(expectedCount));
		return distinct;
	}

	@Test
	public void getAllAllotmentsColleges() throws Exception {
		assertStreamElementCount(s -> s.collegeName, 243L);
	}

}