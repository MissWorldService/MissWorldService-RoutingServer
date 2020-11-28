package org.mws.app;

import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.stereotype.Controller
public class Controller {

    @GetMapping("/another")
    public String anotherPage(){
        return "another";
    }
}
