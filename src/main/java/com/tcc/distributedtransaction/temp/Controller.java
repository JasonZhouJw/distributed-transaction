package com.tcc.distributedtransaction.temp;

import com.tcc.distributedtransaction.fortest.ForTestFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/")
@RestController
@ResponseBody
public class Controller {

    @Autowired
    @Qualifier("forTestFeignClient")
    private ForTestFeignClient forTestFeignClient;

    @GetMapping("/say")
    public String say() {
        System.out.println(forTestFeignClient);
        forTestFeignClient.hello("name");
        return "Hello world";
    }
}
