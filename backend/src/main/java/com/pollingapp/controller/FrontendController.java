package com.pollingapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontendController {

    // Match all GET requests that DO NOT start with /api and do not contain a dot (like .js, .css files)
    @RequestMapping(value = "/{path:[^\\.]*}")
    public String redirect() {
        // Forward to the React frontend index.html
        return "forward:/index.html";
    }
}
