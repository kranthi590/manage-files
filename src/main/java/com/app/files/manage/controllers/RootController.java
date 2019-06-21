package com.app.files.manage.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.app.files.manage.services.FileManageService;

@Controller
public class RootController {

	public static final String FILES_LOCATION = "uploaded-files";

	@Autowired
	private FileManageService fileManageService;

	@GetMapping({ "/", "/files" })
	public String hello(Model model,
			@RequestParam(value = "name", required = false, defaultValue = "World") String name) {
		model.addAttribute("name", name);
		return "dashboard";
	}

	@GetMapping({ "/list-files" })
	@ResponseBody
	public Map<String, Object> listFiles(Model model,
			@RequestParam(value = "name", required = false, defaultValue = "World") String name) {
		model.addAttribute("name", name);
		Map<String, List<String>> files = listDirectory(FILES_LOCATION);
		HashMap<String, Object> response = new HashMap<>();
		response.put("status", "SUCCESS");
		response.put("data", files);
		return response;
	}

	@GetMapping({ "/upload-file" })
	public String uploadFile(Model model,
			@RequestParam(value = "name", required = false, defaultValue = "World") String name) {
		model.addAttribute("name", name);
		return "upload-files";
	}

	@RequestMapping(value = "/hello", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Map<String, String> sayHello() {
		HashMap<String, String> map = new HashMap<>();
		map.put("key", "value");
		map.put("foo", "bar");
		map.put("aa", "bb");
		return map;
	}

	@PostMapping("/uploadFile")
	@ResponseBody
	public Map<String, String> uploadFile(@RequestParam("file") MultipartFile file) {
		String status = fileManageService.saveFile(file);
		HashMap<String, String> map = new HashMap<>();
		map.put("status", status.toUpperCase());
		return map;
	}

	public Map<String, List<String>> listDirectory(String location) {
		File file = new File(location);
		File[] content = file.listFiles();

		Map<String, List<String>> files = new HashMap<>();
		for (File f : content) {
			if (f.isDirectory()) {
				File[] filesInDir = f.listFiles();
				List<String> fileNames = new LinkedList<>();
				for (File f1 : filesInDir) {
					fileNames.add(f1.getAbsolutePath());
				}
				files.put(f.getName(), fileNames);
			}
		}
		return files;
	}

	@RequestMapping(value = "/downloadPdf", method = RequestMethod.GET, produces = "application/pdf")
	public ResponseEntity<InputStreamResource> downloadPDFFile(
			@RequestParam(value = "path", required = true) String path) throws IOException {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		File file2Upload = new File(path);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file2Upload));
		return ResponseEntity.ok().headers(headers).contentLength(file2Upload.length())
				.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
	}

}
