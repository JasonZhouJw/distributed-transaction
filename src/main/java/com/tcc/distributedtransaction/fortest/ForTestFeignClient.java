package com.tcc.distributedtransaction.fortest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "localhost")
public interface ForTestFeignClient {

    @PostMapping("/")
    String hello(String name);
}
