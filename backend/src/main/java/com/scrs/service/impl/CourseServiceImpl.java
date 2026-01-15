package com.scrs.service.impl;
/*
 * @date 12/05 13:48
 */

import com.alibaba.druid.stat.JdbcSqlStatValue;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scrs.mapper.CourseMapper;
import com.scrs.pojo.Course;
import com.scrs.service.CourseService;
import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Override
    public JdbcSqlStatValue updateStats() {
        return null;
    }
}
