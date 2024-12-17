package com.scrs.controller;/*
 * @date 12/05 18:42
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scrs.common.R;
import com.scrs.pojo.College;
import com.scrs.service.CollegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/college")
public class CollegeController {
    @Autowired
    private CollegeService collegeService;

    @GetMapping("/listCollege")
    public R<PageInfo> listCollege(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                           @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,
                           @RequestParam(required = false) String collegeName) {
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 6;
        }
        PageHelper.startPage(pageNum, pageSize);
        LambdaQueryWrapper<College> queryWrapper = new LambdaQueryWrapper<>();
        if (collegeName != null && !collegeName.isEmpty()) {
            queryWrapper.like(College::getCname, collegeName);
        }
        List<College> collegeList = collegeService.list(queryWrapper);
        PageInfo<College> pageInfo = new PageInfo(collegeList);

        return R.success(pageInfo);
    }

    @GetMapping("/preSaveCollege")
    public R<String> preSaveCollege() {

        return R.success("success");
    }

    @PostMapping("/saveCollege")
    public R<String> saveCollege(@RequestBody College college) {
        collegeService.save(college);
        return R.success("save college successfully");
    }

    @GetMapping("/preUpdateCollege/{id}")
    public R<College> preUpdateCollege(@PathVariable("id") Integer id) {
        College college = collegeService.getById(id);
        return R.success(college);
    }

    @PostMapping("/updateCollege")
    public R<String> updateCollege(College college) {
        collegeService.updateById(college);
    return R.success("update college successfully");
    }


    @GetMapping("/deleteCollege/{id}")
    public R<String> deleteCollege(@PathVariable("id") Integer id) {
        boolean b = collegeService.removeById(id);
        if (!b){
            return R.error("delete college failed");
        }
        return R.success("delete college successfully");
    }

    @PostMapping("/deleteBatchCollege")
    public R<String> deleteBatchCollege(@RequestBody String ids) {
        //TODO：传入的字符串格式是 "{ids: "1,2,3,4,5"}"，需要处理一下
        String[] split = ids.substring(8,ids.length() - 2).split(",");
        List<Integer> idList = new ArrayList<>();
        for (String s : split) {
            if (!s.isEmpty()) {
                idList.add(Integer.parseInt(s));
            }
        }
        boolean b = collegeService.removeByIds(idList);
        if (!b) {
            //model.addAttribute("error", "批量删除失败");
            return R.error("delete batch college failed");
        }

        return R.success("delete batch college successfully");
    }
}
