//package com.pacificdataservices.uploadingfiles;
//
//import java.util.List;
//
//import javax.transaction.Transactional;
//
//import org.hibernate.Query;
////import org.hibernate.Query;
//import org.hibernate.Session;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import com.pacificdataservices.user.User;
//
//@Component
//@Repository
//@EnableAutoConfiguration
//@Transactional
//@EnableTransactionManagement
//// TODO only get ApsSrcRuleSets used in demand
//
//
//
//
//public class NaUserService  extends BaseDataServices {
//	Logger logger = LoggerFactory.getLogger(getClass());
//@Autowired
//	public User getUser(String username, String password) {
//		if ("admin".equals(username)) {
//			return new User("admin");
//		}
//		throw new IllegalArgumentException("username must be 'admin'");
//
//	}
//}
