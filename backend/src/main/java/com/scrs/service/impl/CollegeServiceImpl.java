package com.scrs.service.impl;/*
 * @date 12/05 12:40
 */

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scrs.mapper.CollegeMapper;
import com.scrs.pojo.College;
import com.scrs.service.CollegeService;
import org.springframework.stereotype.Service;

@Service
public class CollegeServiceImpl extends ServiceImpl<CollegeMapper, College> implements CollegeService {
}
