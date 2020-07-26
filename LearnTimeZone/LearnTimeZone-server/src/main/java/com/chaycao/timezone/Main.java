package com.chaycao.timezone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.util.Date;
import java.util.TimeZone;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Main {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        SpringApplication.run(Main.class);

//        // 1.java.util.Date
//        Date date = new Date();
//        // 2.long
//        // 1595660847175
//        long dateTime = date.getTime();
//        // 3.String
//        // Sat Jul 25 15:07:27 CST 2020
//        String dateStr = date.toString();

    }
}
