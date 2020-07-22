package com.pacificdataservices.uploadingfiles.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.pacificdataservices.user.User;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface UserService {

	void init();

	/**
	 * returns a a user provided a valid usernane and paswrod
	 */
	 User validate (String username, String password);



}
