package com.soul.videoserver;

import com.soul.videoserver.entity.User;
import com.soul.videoserver.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VideoserverApplicationTests {

	@Autowired
	UserService userService;
	@Test
	void contextLoads() {

		System.out.println("username:"+userService.getUserList("a").get(0).toString());
	}

}
