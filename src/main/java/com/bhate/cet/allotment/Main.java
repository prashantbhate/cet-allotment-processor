package com.bhate.cet.allotment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

public class Main {
	public static void main(String[] args) throws IOException {
		final AllotmentProcessor processor = new AllotmentProcessor();
		final List<Allotment> process = processor.getAllAllotments("engg_cutoff_2016.pdf");

	}

	private static void writeCSV(File file, List<Allotment> list) {
		try {
			try (Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
				out.write(Allotment.csvHeader());
				list.stream()
					.forEach(data -> {
						try {
							out.write(data.toCSV());
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
