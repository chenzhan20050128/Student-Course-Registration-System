package com.scrs.controller;/*
 * @date 12/05 15:02
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scrs.common.R;
import com.scrs.pojo.College;
import com.scrs.pojo.Major;
import com.scrs.service.CollegeService;
import com.scrs.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/major")
public class MajorController {
    @Autowired
    private MajorService majorService;

    @Autowired
    private CollegeService collegeService;


    /*
        * 列出专业信息，并支持分页。用户可以通过专业名称和学院过滤列表，并获得分页结果。
     */
    @GetMapping("/listMajor")
    public R<PageInfo> listMajor(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,
            @RequestParam(required = false) String majorName,@RequestParam(required = false) String collegeName) {
        if (pageNum == null || pageNum <= 0){
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0){
            pageSize = 6;
        }
        PageHelper.startPage(pageNum, pageSize);
        LambdaQueryWrapper<Major> queryWrapper = new LambdaQueryWrapper<>();
        if (majorName != null && !majorName.isEmpty()){
            queryWrapper.like(Major::getMname, majorName);
        }
        if (collegeName != null && !collegeName.isEmpty()){
            queryWrapper.like(Major::getCollege, collegeName);
        }

        List<Major> majorList = majorService.list(queryWrapper);
        PageInfo pageInfo = new PageInfo(majorList);
        //model.addAttribute("pageInfo", pageInfo);
        return R.success(pageInfo);
    }

    @GetMapping("/preSaveMajor")
    public R<List<College>> preSaveMajor(){
        List<College> collegeList = collegeService.list(null);
        //model.addAttribute("collegeList", collegeList);
        return R.success(collegeList);
    }

    @PostMapping("/saveMajor")
    public R<String> saveMajor(@RequestBody Major major){
        majorService.save(major);
        return R.success("新增专业成功");
    }

    @GetMapping("/preUpdateMajor/{id}")
    public R<HashMap<String,Object>> preUpdateMajor(@PathVariable Integer id, Model model){
        Major major = majorService.getById(id);
        List<College> collegeList = collegeService.list(null);
        //model.addAttribute("major", major);
        //model.addAttribute("collegeList", collegeList);
        HashMap<String,Object> map = new HashMap<>();
        map.put("major",major);
        map.put("collegeList",collegeList);
        return R.success(map);
    }

    @PostMapping("/updateMajor")
    public R<String> updateMajor(@RequestBody Major major){
        majorService.updateById(major);
        return R.success("更新专业成功");
    }

    @GetMapping("/deleteMajor/{id}")
    public R<String> deleteMajor(@PathVariable Integer id){
        boolean b = majorService.removeById(id);
        if (!b){
            //model.addAttribute("error","删除专业失败");
            return R.error("删除专业失败");
        }
        return R.success("删除专业成功");
    }

    @PostMapping("/deleteBatchMajor")
    public R<String> deleteBatchMajor(@RequestParam String ids){
        String[] split = ids.split(",");
        List<Integer> idList = new java.util.ArrayList<>();
        for (String s: split){
            if (!s.isEmpty()){
                idList.add(Integer.parseInt(s));
            }
        }
        boolean b = majorService.removeByIds(idList);
        if (!b){
            //model.addAttribute("error","批量删除专业失败");
            return R.error("批量删除专业失败");
        }
        return R.success("批量删除专业成功");
    }

}
