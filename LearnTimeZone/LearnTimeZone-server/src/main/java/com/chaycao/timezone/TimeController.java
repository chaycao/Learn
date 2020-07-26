package com.chaycao.timezone;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin
@RestController
public class TimeController {

    @PostMapping("/time")
    public List<Data> test(@RequestBody TimeDto dto) {
        Date startTime = dto.getStartTime();
        Date endTime = dto.getEndTime();
        System.out.println(startTime);
        System.out.println(endTime);

        // 格林时间（0）
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdfGreen = new SimpleDateFormat(format);
        sdfGreen.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        System.out.println("格林时间：" + sdfGreen.format(startTime) + "至" + sdfGreen.format(endTime));

        // 北京时间（+8）
        SimpleDateFormat sdfBeijing = new SimpleDateFormat(format);
        sdfBeijing.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        System.out.println("北京时间：" + sdfBeijing.format(startTime) + "至" + sdfBeijing.format(endTime));

        // 太平洋时间（-8）
        SimpleDateFormat sdfPacific = new SimpleDateFormat(format);
        sdfPacific.setTimeZone(TimeZone.getTimeZone("GMT-8"));
        System.out.println("太平洋时间：" + sdfPacific.format(startTime) + "至" + sdfPacific.format(endTime));

        List<Data> dataList = queryDate(dto);

        return dataList;
    }

    private List<Data> queryDate(TimeDto dto) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost/test?useSSL=false&useUnicode=true&characterEncoding=UTF8&allowPublicKeyRetrieval=true");
//        dataSource.setUrl("jdbc:mysql://localhost/test?useSSL=false&useUnicode=true&characterEncoding=UTF8&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai");
        dataSource.setUsername("root");
        dataSource.setPassword("caoniezi");

        Date startTime = dto.getStartTime();
        Date endTime = dto.getEndTime();

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String sql = "SELECT * FROM data WHERE create_time >= ? and create_time <= ?";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(
                sql,
                new Object[]{startTime, endTime});
        List<Data> dataList = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            Data data = new Data();
            data.setId((Integer) map.get("id"));
            data.setContent((String) map.get("content"));
            data.setCreateTime((Date) map.get("create_time"));
            dataList.add(data);
        }
        return dataList;
    }


}
