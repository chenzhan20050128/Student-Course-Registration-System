package com.scrs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scrs.pojo.Course;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {

}
