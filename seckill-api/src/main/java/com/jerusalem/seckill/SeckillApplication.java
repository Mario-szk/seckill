package com.jerusalem.seckill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.jerusalem.seckill"})
@MapperScan("com.jerusalem.seckill.dao")
public class SeckillApplication {

    public static void main( String[] args ) {
        SpringApplication.run(SeckillApplication.class,args);
    }
}
