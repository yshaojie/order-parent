package com.jsjy.order.manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * Created by shaojieyue on 8/30/15.
 */
@Controller
public class HelloController {
    @RequestMapping("/order/hello")
    @ResponseBody
    public String helloWorld() {
        return "hello work. the time:"+new Date();
    }
}
