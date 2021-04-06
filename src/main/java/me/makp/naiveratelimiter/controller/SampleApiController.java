package me.makp.naiveratelimiter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SampleApiController {

    @GetMapping("/api/{tenant}")
    public Map<String, String> getTest(@PathVariable String tenant) {
        return Map.of("status", "OK");
    }
}
