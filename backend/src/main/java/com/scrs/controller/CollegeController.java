package com.scrs.controller;/*
 * @date 12/05 18:42
 */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scrs.pojo.College;
import com.scrs.service.CollegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/college")
public class CollegeController {
    @Autowired
    private CollegeService collegeService;

    @RequestMapping("/listCollege")
    public String listCollege(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                              @RequestParam(value = "pageSize", defaultValue = "6") Integer pageSize,
                              Model model, College college) {
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 6;
        }
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<College> queryWrapper = new QueryWrapper<>();
        if (college.getCname() != null) {
            queryWrapper.like("cname", college.getCname());
        }
        List<College> collegeList = collegeService.list(queryWrapper);
        PageInfo<College> pageInfo = new PageInfo(collegeList);
        model.addAttribute("pageInfo", pageInfo);
        return "admin-college-list";
    }

    @RequestMapping("/preSaveCollege")
    public String preSaveCollege(Model model) {

        return "admin-college-save";
    }

    @RequestMapping("/saveCollege")
    public String saveCollege(College college) {

        collegeService.save(college);
        return "redirect:/college/listCollege";
    }

    @RequestMapping("/preUpdateCollege/{id}")
    public String preUpdateCollege(@PathVariable("id") Integer id, Model model) {
        College college = collegeService.getById(id);
        model.addAttribute("college", college);
        return "admin-college-update";
    }

    @RequestMapping("/updateCollege")
    public String updateCollege(College college) {
        collegeService.updateById(college);
        return "redirect:/college/listCollege";
    }


    @RequestMapping("/deleteCollege/{id}")
    public String deleteCollege(@PathVariable("id") Integer id) {
        collegeService.removeById(id);
        return "redirect:/college/listCollege";
    }

    @RequestMapping("/deleteBatchCollege")
    public String deleteBatchCollege(@RequestBody String ids,Model model) {
        String[] split = ids.split(",");
        List<Integer> idList = new ArrayList<>();
        for (String s : split) {
            if (!s.isEmpty()) {
                idList.add(Integer.parseInt(s));
            }
        }
        boolean b = collegeService.removeByIds(idList);
        if (!b) {
            model.addAttribute("error", "批量删除失败");
        }

        return "redirect:/college/listCollege";
    }
}
