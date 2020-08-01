package com.kaly.controller;

import com.alibaba.fastjson.JSONObject;
import com.kaly.bean.Student;
import com.kaly.common.ResultInfo;
import com.kaly.pageGrade.ConnectJWGL;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 国瑚 on 2020/7/13.
 */
@Controller
public class StudentController {

    ConnectJWGL connectJWGL = new ConnectJWGL();

    @RequestMapping("/login")
    private String a(Student student, Model model, HttpSession session) throws Exception {
        ConnectJWGL connectJWGL = new ConnectJWGL();
        if(student.getYhm() == null || student.getMm() == null){
            return "index";
        }
        connectJWGL.setStuNum(student.getYhm());
        connectJWGL.setPassword(student.getMm());
        connectJWGL.init();
        String message = connectJWGL.beginLogin();
        if (message == "欢迎登陆"){
            session.setAttribute("connectJWGL",connectJWGL);
            model.addAttribute("xn", Integer.parseInt(student.getYhm())/100000000+2000);
            Calendar date = Calendar.getInstance();//获取当前年份
            String year = String.valueOf(date.get(Calendar.YEAR));
            model.addAttribute("year", year);
            return "info.html";
        }
        model.addAttribute("yhm", student.getYhm());
        model.addAttribute("message",message);
        return "index";
    }

    @RequestMapping("/getGrade")
    @ResponseBody
    private String get(Student student,HttpSession session) throws Exception {
        ConnectJWGL connectJWGL = (ConnectJWGL) session.getAttribute("connectJWGL");
        String grade = connectJWGL.getStudentGradeJson(student.getYear(),student.getTerm());
        return grade;
    }

    @RequestMapping("/Detail")
    @ResponseBody
    private String getGradeDetail(Student student,String jxb_id,String kcmc,HttpSession session) throws Exception {
        ConnectJWGL connectJWGL = (ConnectJWGL) session.getAttribute("connectJWGL");
        return JSONObject.toJSONString(new ResultInfo(0,"数据获取成功",connectJWGL.getStudentGradeDetail(student.getYear(),student.getTerm(),jxb_id,kcmc)));
    }

    @RequestMapping("/loginOut")
    @ResponseBody
    private String loginOut(HttpSession session) throws Exception {
        ConnectJWGL connectJWGL = (ConnectJWGL) session.getAttribute("connectJWGL");
        connectJWGL.logout();
        session.removeAttribute("connectJWGL");
        return JSONObject.toJSONString(new ResultInfo(0,"成功退出"));
    }
}
