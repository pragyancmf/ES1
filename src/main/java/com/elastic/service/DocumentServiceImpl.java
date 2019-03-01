package com.elastic.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Iterator;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import com.elastic.dto.Student;

@Component
public class DocumentServiceImpl {

	public Student getStudentDataFromPDF(String path) throws IOException {

		//File file = new File("C:\\Users\\harshg\\Desktop\\student2.pdf");
		File file = new File(path);
		PDDocument document = PDDocument.load(file);
		PDFTextStripper pdfStripper = new PDFTextStripper();
		String text = pdfStripper.getText(document);

		//System.out.println(text);
		/*
		 * String lines[] = text.split("\\r?\\n"); Map<String, Object> studentParameters
		 * = new HashMap<String, Object>();
		 * 
		 * Stream.of(lines).forEach(line -> { String[] s = line.split(":");
		 * studentParameters.put(s[0].trim().toLowerCase(), s[1].trim());
		 * 
		 * });
		 * 
		 * Student student = new Student((String)studentParameters.get("name"),
		 * Integer.parseInt((String)studentParameters.get("rollnumber")),
		 * Integer.parseInt((String)studentParameters.get("classname")),
		 * (String)studentParameters.get("section"));
		 * System.out.println(student.toString());
		 */

		//PDFRenderer renderer = new PDFRenderer(document);
		//BufferedImage image = renderer.renderImage(1);
		//ImageIO.write(image, "JPEG", new File("C:/Users/harshg/Desktop/myimage.jpg"));


		PDPageTree pages = document.getDocumentCatalog().getPages();
		Iterator iter = pages.iterator();
		while( iter.hasNext() )
		{
			PDPage page = (PDPage)iter.next();
			PDResources resources = page.getResources();
			resources.getXObjectNames().forEach(cosName -> {
				System.out.println(cosName);
			});;
			/*
			 * if( images != null ) { Iterator imageIter = images.keySet().iterator();
			 * while( imageIter.hasNext() ) { String key = (String)imageIter.next();
			 * PDXObjectImage image = (PDXObjectImage)images.get( key ); String name =
			 * getUniqueFileName( key, image.getSuffix() ); System.out.println(
			 * "Writing image:" + name ); image.write2file( name ); } }
			 */
		}
		document.close();
		return null;
	}

	public String getTextFromPDF(String path) throws IOException {

		//File file = new File("C:\\Users\\harshg\\Desktop\\student2.pdf");
		File file = new File(path);
		PDDocument document = PDDocument.load(file);
		PDFTextStripper pdfStripper = new PDFTextStripper();
		String text = pdfStripper.getText(document);

		System.out.println(text);

		document.close();
		return text;
	}

	public String readData() throws IOException {

		String fileName = "C:/Users/harshg/Desktop/srk.txt";
		Path path = Paths.get(fileName);
		byte[] bytes = Files.readAllBytes(path);
		String text = new String(bytes);

		/*
		 * PDDocument document = new PDDocument(); PDPage blankPage = new PDPage();
		 * document.addPage( blankPage );
		 * 
		 * PDDocumentInformation pdd = document.getDocumentInformation();
		 * 
		 * pdd.setAuthor("Pragyan"); pdd.setTitle("Shahrukh Khan");
		 * pdd.setSubject("Example document");
		 * pdd.setModificationDate(Calendar.getInstance());
		 * pdd.setKeywords("actors, pdf");
		 * 
		 * PDPageContentStream contentStream = new PDPageContentStream(document,
		 * blankPage); contentStream.beginText();
		 * contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
		 * contentStream.newLineAtOffset(25, 500); contentStream.showText(text);
		 * 
		 * contentStream.endText(); contentStream.close();
		 * 
		 * document.save("C:/Users/harshg/Desktop/srk2.pdf");
		 */
		//document.close();
		System.out.println(text);
		return text;
	}

}
