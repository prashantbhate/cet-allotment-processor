package com.bhate.cet.allotment;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by pb on 11/06/2017.
 */
public class CSVProcessorTest {

	private CSVProcessor processor;

	@Before
	public void setUp() throws Exception {
		processor = new CSVProcessor();
	}

	@Test
	public void cleanupShouldRemoveDataErrors1() throws Exception {
		invokeAndAssert(",,111187115454123428,,", ",111187,115454,123428,");
	}

	@Test
	public void cleanupShouldRemoveDataErrors2() throws Exception {
		// @formatter:off
		invokeAndAssert(
				",," +
				"107796" +
				"110848" +
				",," +
				"111860" +
				"126327",
				"," +
				"107796," +
				"110848," +
				"111860," +
				"126327"
		);
		// @formatter:on
	}

	@Test
	public void cleanupShouldRemoveDataErrors3() throws Exception {
		// @formatter:off
		invokeAndAssert(
			"" +
				"115805" +
				"115992" +
				",," +
				"122163" +
				"125834" +
				",,",
			"" +
				"115805," +
				"115992," +
				"122163," +
				"125834,");
		// @formatter:on
	}

	@Test
	public void cleanupShouldRemoveDataErrors4() throws Exception {
		// @formatter:off
		invokeAndAssert("103478104141,," , "103478,104141,");
		// @formatter:on
	}

	@Test
	public void cleanupShouldRemoveDataErrors5() throws Exception {
		// @formatter:off
		invokeAndAssert(",,103017119798,54585," , ",103017,119798,54585,");
		// @formatter:on
	}

	private void invokeAndAssert(String content, String expected) throws IOException {
		final String actual = processor.cleanup(content);
		assertThat(actual, is(expected));
	}

}