package com.webSocket.simpleChat.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtil {
    private static final Logger logger = LoggerFactory.getLogger(ValidateUtil.class);

    private static final Set<String> VALID_EXTENSIONS = new HashSet<>(
            Arrays.asList("jpg", "gif", "png"));

    public static Map<String, String> validateUserInfo(String login, String email,
                                                 String newPassword, String repeatPassword,
                                                 MultipartFile avatar) {
        Map<String, String> errors = new HashMap<>();
        if (login == null || login.isEmpty()) {
            logger.warn("Login cannot be empty!");
            errors.put("loginError", "Login cannot be empty");
        }

        Pattern emailPattern = Pattern.compile("^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)" +
                "|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)" +
                "+[a-zA-Z]{2,}))$", Pattern.CASE_INSENSITIVE);
        if (email != null && !email.isEmpty()) {
            Matcher matcher = emailPattern.matcher(email);
            if (!matcher.find()) {
                logger.warn("Email incorrect!");
                errors.put("emailError", "Email incorrect");
            }
        }

        if (newPassword != null && repeatPassword != null && !newPassword.equals(repeatPassword)) {
            logger.warn("Password mismatch!");
            errors.put("passwordError", "Password mismatch");
        }

        if (avatar != null) {
            String[] filenameParts = avatar.getOriginalFilename().split("\\.");
            if ( filenameParts.length < 2 || !VALID_EXTENSIONS.contains(filenameParts[1]) ) {
                logger.warn("Invalid extension of avatar!");
                errors.put("extensionError", "Invalid extension of avatar");
            }

            if (avatar.getSize()/1024.0 > 800) {
                logger.warn("Max size of avatar must be 800K!");
                errors.put("sizeError", "Max size of avatar must be 800K");
            }
        }

        return errors;
    }
}
