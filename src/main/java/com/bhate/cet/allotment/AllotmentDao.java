package com.bhate.cet.allotment;

import static com.bhate.cet.allotment.PDFUtil.extractTableFromPDF;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.google.common.collect.Table;

/**
 * Created by pb on 15/06/2017.
 */
@Repository
public class AllotmentDao {
	public List<List<String>> getRecords(String fileName) {
		Table<Float, Float, String> records = extractTableFromPDF(fileName);
		return records.rowMap()
					  .values()
					  .stream()
					  .map(row -> new ArrayList<>(row.values()))
					  .collect(toList());
	}

}
