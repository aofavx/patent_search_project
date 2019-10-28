package com.blcultra;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = {"com.blcultra"})
@MapperScan(value ={"com.blcultra.dao"})
public class PatentSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(PatentSearchApplication.class, args);
	}

}
