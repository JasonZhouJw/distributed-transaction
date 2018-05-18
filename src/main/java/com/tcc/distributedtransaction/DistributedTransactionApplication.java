package com.tcc.distributedtransaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class DistributedTransactionApplication {

	public static void main(String[] args) {
		SpringApplication.run(DistributedTransactionApplication.class, args);
	}
}
