package org.mws.app;

import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.web.bind.annotation.RestController
public class RestController {
    @GetMapping("/")
    public String mainPage(){
        return "Hello there!";
    }


}
