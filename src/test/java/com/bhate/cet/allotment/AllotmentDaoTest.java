package com.bhate.cet.allotment;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by pb on 15/06/2017.
 */
public class AllotmentDaoTest {

	private AllotmentDao allotmentDao;
	private List<List<String>> records;

	@Before
	public void setUp() throws Exception {
		allotmentDao = new AllotmentDao();
		records = allotmentDao.getRecords("engg_cutoff_2016.pdf");
	}

	@Test
	public void itShouldFetchRecords() throws IOException {
		assertThat(records.size(), is(1756));
	}

	@Test
	public void itShouldHaveLessThan26Columns() throws IOException {
		records.stream()
			   .forEach(s -> assertThat(s.size(), is(lessThan(26))));
	}

	@Test
	public void itShouldHaveQuotaRow() throws IOException {

		final long distinctQuotaRowCount = distinctQuotaRows(records).count();
		assertThat(distinctQuotaRowCount, is(1L));
	}

	@Test
	public void itShouldHaveQuotaColumn() throws IOException {
		final long quotaCount = distinctQuotaRows(records).findFirst()
														  .get()
														  .stream()
														  .distinct()
														  .count();
		assertThat(quotaCount, is(24L));
	}

	@Test
	public void itShouldHaveBranches() throws IOException {
		final long branchCount = records.stream()
										.filter(s -> s.size() == 25)
										.map(s -> s.get(0))
										.distinct()
										.count();
		assertThat(branchCount, is(36L));
	}

	@Test
	public void itShouldHaveColleges() throws IOException {
		final long collegeCount = records.stream()
										 .filter(s -> s.size() == 2)
										 .count();
		assertThat(collegeCount, is(243L));
	}

	private Stream<List<String>> distinctQuotaRows(List<List<String>> records) {
		return records.stream()
					  .filter(s -> s.size() == 24)
					  .distinct();
	}


	@Test
	public void itShouldConvertPdf() throws IOException {
//		final List<List<String>> records = allotmentDao.getRecords();
//
//		final String str = records.stream().filter(s -> s.size() == 2)
//								  .map(row -> row.stream()
//												 .map(s -> "\"" + s + "\"")
//												 .collect(Collectors.joining(",")))
//								  .collect(Collectors.joining("\n"));
//		System.out.println(str);
	}


}
