package com.scrs.controller;/*
 * @date 12/05 15:02
 */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scrs.pojo.College;
import com.scrs.pojo.Major;
import com.scrs.service.CollegeService;
import com.scrs.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/major")
public class MajorController {
    @Autowired
    private MajorService majorService;

    @Autowired
    private CollegeService collegeService;


    /*
        * 列出专业信息，并支持分页。用户可以通过专业名称和学院过滤列表，并获得分页结果。
     */
    @RequestMapping("/listMajor")
    public String listMajor(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,
            Model model,Major major, College college){
        if (pageNum == null || pageNum <= 0){
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0){
            pageSize = 6;
        }
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
        if (major.getMname() != null){
            queryWrapper.like("mname", major.getMname());
        }
        if (major.getCollege() != null){
            queryWrapper.like("college", major.getCollege());
        }
        List<Major> majorList = majorService.list(queryWrapper);
        PageInfo pageInfo = new PageInfo(majorList);
        model.addAttribute("pageInfo", pageInfo);
        return "admin-major-list";
    }

    @RequestMapping("/preSaveMajor")
    public String preSaveMajor(Model model){
        List<College> collegeList = collegeService.list(null);
        model.addAttribute("collegeList", collegeList);
        return "admin-major-save";
    }

    @RequestMapping("/saveMajor")
    public String saveMajor(Major major){
        majorService.save(major);
        return "redirect:/major/listMajor";
    }

    @RequestMapping("/preUpdateMajor/{id}")
    public String preUpdateMajor(@PathVariable Integer id, Model model){
        Major major = majorService.getById(id);
        List<College> collegeList = collegeService.list(null);
        model.addAttribute("major", major);
        model.addAttribute("collegeList", collegeList);
        return "admin-major-update";
    }

    @RequestMapping("/updateMajor")
    public String updateMajor(Major major){
        majorService.updateById(major);
        return "redirect:/major/listMajor";
    }

    @RequestMapping("/deleteMajor/{id}")
    public String deleteMajor(@PathVariable Integer id,Model model){
        boolean b = majorService.removeById(id);
        if (!b){
            model.addAttribute("error","删除专业失败");
        }
        return "redirect:/major/listMajor";
    }

    @RequestMapping("/deleteBatchMajor")
    public String deleteBatchMajor(@RequestParam String ids, Model model){
        String[] split = ids.split(",");
        List<Integer> idList = new java.util.ArrayList<>();
        for (String s: split){
            if (!s.isEmpty()){
                idList.add(Integer.parseInt(s));
            }
        }
        boolean b = majorService.removeByIds(idList);
        if (!b){
            model.addAttribute("error","批量删除专业失败");
        }
        return "redirect:/major/listMajor";
    }

}
