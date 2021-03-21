package com.webSocket.simpleChat.controller;

import com.webSocket.simpleChat.util.MailSenderUtil;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileInputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
@PropertySource("classpath:/application.properties")
public class ImageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Value("${uploadPath}")
    private String path;

    @MockBean
    private MailSenderUtil mailSender;

    @Test
    public void shouldLoadAvatar() throws Exception {
        File file = new File(path + "user-male-circle.png");
        mockMvc.perform(get("/images/user-male-circle.png"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(IOUtils.toByteArray(new FileInputStream(file))));

        file = new File(path + "avatar.jpg");
        mockMvc.perform(get("/images/avatar.jpg"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(IOUtils.toByteArray(new FileInputStream(file))));

        file = new File(path + "avatar.gif");
        mockMvc.perform(get("/images/avatar.gif"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_GIF))
                .andExpect(content().bytes(IOUtils.toByteArray(new FileInputStream(file))));

        mockMvc.perform(get("/images/avatar.txt"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
