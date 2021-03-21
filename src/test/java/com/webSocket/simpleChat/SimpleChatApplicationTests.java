package com.webSocket.simpleChat;

import com.webSocket.simpleChat.util.MailSenderUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class SimpleChatApplicationTests {
	@MockBean
	private MailSenderUtil mailSender;

	@Test
	void contextLoads() {
	}

}
