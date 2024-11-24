package com.Px4.ChatAPI.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

//@Controller
public class ReactAppController {

//    @RequestMapping(value = { "/", "/{x:[\\w\\-]+}", "/{x:^(?!api$).*$}/*/{y:[\\w\\-]+}","/error"  })
//    @RequestMapping(value = {
//            "/",
//            "/{x:[\\w\\-]+}",
//            "/{x:^(?!api$|ws\\/.*).*$}/*/{y:[\\w\\-]+}",
//            "/error"
//    })
    public String getIndex(HttpServletRequest request) {
        return "/index.html";
    }

}
//package com.Px4.ChatAPI.controllers;
//
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//@RequestMapping(value = {"/{path:^(?!ws$).*}", "/**/{path:^(?!api|ws).*}"})
//public class ReactAppController {
//    @RequestMapping(value = "/**")
//    public String getIndex(HttpServletRequest request) {
//        return "/index.html";
//    }
//}
