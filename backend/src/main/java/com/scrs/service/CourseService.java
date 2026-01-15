package com.scrs.service;

import com.alibaba.druid.stat.JdbcSqlStatValue;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scrs.pojo.Course;

public interface CourseService extends IService<Course> {
    JdbcSqlStatValue updateStats();
}
