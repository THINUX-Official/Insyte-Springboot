package com.insurance.thinux.insytespringboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: THINUX
 * @created: 02-Jan-26 - 11:23 AM
 */

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "Insyte backend is running!";
    }
}
