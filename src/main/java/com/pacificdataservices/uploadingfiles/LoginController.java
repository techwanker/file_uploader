<<<<<<< HEAD
package com.pacificdataservices.uploadingfiles;

import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pacificdataservices.uploadingfiles.storage.StorageFileNotFoundException;
import com.pacificdataservices.uploadingfiles.storage.StorageService;

@Controller
public class LoginController {

	private final StorageService storageService;

	@Autowired
	public LoginController(StorageService storageService) {
		this.storageService = storageService;
	}
	
	@GetMapping("/login")
	public String login(Model model) throws IOException {

//		model.addAttribute("files", storageService.loadAll().map(
//				path -> MvcUriComponentsBuilder.fromMethodName(Logincontroller.class,
//						"serveFile", path.getFileName().toString()).build().toUri().toString())
//				.collect(Collectors.toList()));
//
		return "loginForm";
	}



}
=======
package com.pacificdataservices.uploadingfiles;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pacificdataservices.uploadingfiles.storage.StorageService;

@Controller
public class LoginController {

	private transient Logger logger =  LoggerFactory.getLogger(getClass());
	private final StorageService storageService;

	@Autowired
	public LoginController(StorageService storageService) {
		this.storageService = storageService;
	}

	@GetMapping("/login")
	public String login(Model model) throws IOException {

		//		model.addAttribute("files", storageService.loadAll().map(
		//				path -> MvcUriComponentsBuilder.fromMethodName(Logincontroller.class,
		//						"serveFile", path.getFileName().toString()).build().toUri().toString())
		//				.collect(Collectors.toList()));
		//i
		logger.info("returning a login form");
		return "loginForm";
	}

	@GetMapping("/validate")
	public String validate(

			@RequestParam(value="username") String username,
			@RequestParam(value="password") String password)
	{
		logger.info("validating");
		return "admin".equals(username) ? "true" : "false";
	}



}
>>>>>>> refs/remotes/origin/master
