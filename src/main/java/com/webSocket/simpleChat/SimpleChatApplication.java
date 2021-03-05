package com.webSocket.simpleChat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class SimpleChatApplication {
	public static void main(String[] args) {
		SpringApplication.run(SimpleChatApplication.class, args);
	}
}
