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
