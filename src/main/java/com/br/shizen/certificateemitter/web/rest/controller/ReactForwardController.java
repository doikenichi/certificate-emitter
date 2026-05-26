package com.br.shizen.certificateemitter.web.rest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ReactForwardController {

    @GetMapping(value = {
            "/",
            "/{path:^(?!api|img|assets|css|js).*$}"
    })
    public String forward(@PathVariable String path) {
        return "forward:/index.html";
    }
}
