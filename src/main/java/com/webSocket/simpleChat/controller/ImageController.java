package com.webSocket.simpleChat.controller;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/images")
public class ImageController {
    private final static Logger logger = LoggerFactory.getLogger(ImageController.class);

    @Value("${uploadPath}")
    private String path;

    @GetMapping("/{filename}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable String filename) {
        logger.info("Loading avatar");

        File avatar = new File(path + filename);
        byte[] image;
        try {
            image = IOUtils.toByteArray(new FileInputStream(avatar));
        } catch (IOException e) {
            logger.error(e.toString());
            return ResponseEntity.notFound().build();
        }

        String[] splitFilename = filename.split("\\.");
        String extension = splitFilename[splitFilename.length-1];
        MediaType mediaType;
        if (extension.equals("png")) {
            mediaType = MediaType.IMAGE_PNG;
        } else if (extension.equals("jpg")) {
            mediaType = MediaType.IMAGE_JPEG;
        } else if (extension.equals("gif")) {
            mediaType = MediaType.IMAGE_GIF;
        } else {
            logger.warn("Image not png, jpg and gif!");
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().contentType(mediaType).body(image);
    }
}
