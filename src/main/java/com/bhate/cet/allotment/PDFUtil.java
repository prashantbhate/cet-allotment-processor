package com.bhate.cet.allotment;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.itextpdf.text.pdf.parser.Vector;

/**
 * Created by pb on 16/06/2017.
 */
public class PDFUtil {
	public static Table<Float, Float, String> extractTableFromPDF(String fileName) {
		try (InputStream in = getInputStream(fileName)) {
			final PdfReader reader = new PdfReader(in);
			MyTextExtractionStrategy st = new MyTextExtractionStrategy();
			final int numberOfPages = reader.getNumberOfPages();
			for (int i = 0; i < numberOfPages; i++) {
				PdfTextExtractor.getTextFromPage(reader, i + 1, st);
				st.offset += 2000;
			}
			return st.records;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static InputStream getInputStream(String fileName) {
		return PDFUtil.class.getClassLoader()
							.getResourceAsStream(fileName);
	}

	private static class MyTextExtractionStrategy implements TextExtractionStrategy {

		Table<Float, Float, String> records = TreeBasedTable.create();
		private float offset;
		private float maxX;
		private float maxY;

		@Override
		public String getResultantText() {
			return null;
		}

		@Override
		public void beginTextBlock() {

		}

		@Override
		public void renderText(TextRenderInfo renderInfo) {
			final Vector point = renderInfo.getBaseline()
										   .getStartPoint();
			final float x = point.get(Vector.I1);
			final float y = point.get(Vector.I2);

			maxX = Math.max(x, maxX);
			maxY = Math.max(y, maxY);
			final String text = renderInfo.getText();

			records.put(x + offset, y, text);

		}

		@Override
		public void endTextBlock() {

		}

		@Override
		public void renderImage(ImageRenderInfo renderInfo) {

		}
	}

}
