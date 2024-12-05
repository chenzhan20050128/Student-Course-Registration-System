package com.scrs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scrs.pojo.Major;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
public interface MajorMapper extends BaseMapper<Major> {

}
