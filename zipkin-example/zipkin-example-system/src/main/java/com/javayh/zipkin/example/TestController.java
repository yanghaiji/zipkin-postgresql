package com.javayh.zipkin.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2023-05-23
 */
@RestController
@RequestMapping(value = "test")
public class TestController {

    @GetMapping(value = "/user")
    public Map<String, String> user() {
        Map<String, String> user = new HashMap<>();
        user.put("username", "yanghaiji");
        user.put("password", "654321");
        return user;
    }
}
