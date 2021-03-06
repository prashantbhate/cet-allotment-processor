package com.bhate.cet.allotment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * Created by pb on 15/06/2017.
 */
public class CSVProcessor {

	public static String[] HEADERS = {"", "1G", "1K", "1R", "2AG", "2AK", "2AR", "2BG", "2BK", "2BR", "3AG", "3AK", "3AR", "3BG", "3BK", "3BR", "GM", "GMK", "GMR", "SCG", "SCK", "SCR", "STG", "STK", "STR"};

	static String cleanup(String content) {
		content = content.replaceAll(",,(\\d{5,6})(\\d{5,6})(\\d{5,6}),,", ",$1,$2,$3,");
		content = content.replaceAll(",,(\\d{5,6})(\\d{5,6}),,(\\d{5,6})(\\d{5,6})", ",$1,$2,$3,$4");
		content = content.replaceAll("(\\d{5,6})(\\d{5,6}),,(\\d{5,6})(\\d{5,6}),,", "$1,$2,$3,$4,");
		content = content.replaceAll("(\\d{5,6})(\\d{5,6}),,", "$1,$2,");
		content = content.replaceAll(",,(\\d{5,6})(\\d{5,6})", ",$1,$2");
		content = content.replaceAll(",,(\\d{5,6}) ", ",$1,");
		return content;
	}
	private List<Allotment> buildAllotmentData(List<CSVRecord> records) {
		List<Allotment> list = new ArrayList<>();
		String collegeName = null;
		for (CSVRecord record : records) {
			// System.out.printf("%10d |",record.getRecordNumber());
			// for (String s : record) {
			// System.out.printf(" |%10.10s| ",s);
			// }
			final String name = getCollegeName(record);
			if (name != null) {
				collegeName = name;
			}
			final String branchName = getBranchName(record);

			if (branchName != null) {
				for (int i = 1; i < record.size(); i++) {
					String s = record.get(i);
					if (!s.contains("--")) {

						final Allotment data = new Allotment(collegeName, branchName, HEADERS[i], s);
						list.add(data);
					}
				}
			}
		}
		return list;
	}

	private String getBranchName(CSVRecord record) {
		final Pattern pattern = Pattern.compile("^[A-Z][A-Z] ");
		String input = record.get(0);
		if (pattern.matcher(input)
				   .find()) {
			return input;
		}
		return null;
	}

	private String getCollegeName(CSVRecord record) {
		final Pattern pattern = Pattern.compile("E\\d\\d\\d");
		for (String s : record) {
			if (pattern.matcher(s)
					   .find()) {
				return s;
			}
		}
		return null;
	}

	private InputStream getInputStream() {
		final String fileName = "engg_cutoff_2016.csv";
		return getClass().getClassLoader()
						 .getResourceAsStream(fileName);
	}

	public List<Allotment> getAllAllotments1() {
		System.out.println("--caching--");
		List<Allotment> allotments = Collections.emptyList();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8))) {
			final List<CSVRecord> records = reader.lines()
												  .map(CSVProcessor::cleanup)
												  .map(this::getCsvRecords)
												  .flatMap(Collection::stream)
												  .collect(Collectors.toList());
			allotments = buildAllotmentData(records);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return allotments;
	}

	private List<CSVRecord> getCsvRecords(String content) {
		try {
			Reader in = new StringReader(content);
			final List<CSVRecord> records = CSVFormat.EXCEL.parse(in)
														   .getRecords();
			return records;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

}
