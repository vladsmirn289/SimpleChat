package com.webSocket.simpleChat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
public class SimpleChatApplication {
	public static void main(String[] args) {
		SpringApplication.run(SimpleChatApplication.class, args);
	}
}
