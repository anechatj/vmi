package com.vmi.policyapi;

import org.springframework.boot.SpringApplication;

public class TestPolicyApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(PolicyApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
