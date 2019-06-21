package com.app.files.manage.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileManageService {

	private static final Logger logger = LogManager.getLogger(FileManageService.class);

	public static final String FILES_LOCATION = "uploaded-files";

	public String saveFile(MultipartFile file) {
		try {
			Path mypath = Paths.get(FILES_LOCATION);
			if (!Files.exists(mypath)) {
				Files.createDirectory(mypath);
			}
			String fileName = StringUtils.cleanPath(file.getOriginalFilename());
			System.out.println("File Name::: " + fileName);
			if (file.isEmpty()) {
				throw new RuntimeException("Failed to store empty file " + fileName);
			}
			// This is a security check
			if (fileName.contains("..")) {
				throw new RuntimeException(
						"Cannot store file with relative path outside current directory " + fileName);
			}

			try (InputStream inputStream = file.getInputStream()) {
				Path filePath = Paths.get(FILES_LOCATION + "/" + fileName.substring(0, fileName.lastIndexOf(".")));
				if (!Files.exists(filePath)) {
					Files.createDirectory(filePath);
				}
				InputStream is = file.getInputStream();
				Files.copy(is, Paths.get(filePath + "/" + fileName), StandardCopyOption.REPLACE_EXISTING);
				// splitFile(filePath + "/" + fileName);
				splitPdf(filePath + "/" + fileName,
						FILES_LOCATION + "/" + fileName.substring(0, fileName.lastIndexOf(".")));
				if (new File(filePath + "/" + fileName).delete()) {
					System.out.println("temp directory deleted from Project root directory");
				} else
					System.out.println("temp directory doesn't exist or not empty in the project root directory");
				return "success";

			}
		} catch (Exception exception) {
			logger.error("Exception:::: " + exception.getMessage());
			return "failure";
		}
	}

	private void splitPdf(String path, String filePath) {
		try (PDDocument document = PDDocument.load(new File(path))) {
			// Instantiating Splitter class
			Splitter splitter = new Splitter();

			// splitting the pages of a PDF document
			List<PDDocument> Pages = splitter.split(document);

			// Creating an iterator
			Iterator<PDDocument> iterator = Pages.listIterator();

			// Saving each page as an individual document
			int i = 1;
			while (iterator.hasNext()) {
				PDDocument pd = iterator.next();
				String outFile = path.substring(0, path.indexOf(".pdf")) + "-" + String.format("%03d", i) + ".pdf";
				pd.save(outFile);
				i++;
			}

		} catch (IOException e) {
			System.err.println("Exception while trying to read pdf document - " + e);
		}
	}
}
