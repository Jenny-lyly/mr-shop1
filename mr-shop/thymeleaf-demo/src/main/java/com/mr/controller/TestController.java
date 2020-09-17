package com.mr.controller;

import com.mr.entity.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;

/**
 * @ClassName TestController
 * @Description: TestController
 * @Author jinluying
 * @create: 2020-09-14 20:00
 * @Version V1.0
 **/
@Controller
public class TestController {

    @GetMapping("test")
    public String test(ModelMap map){
        map.put("name","lihongyang");
        map.put("sex","女");
        return "test";
    }

    @GetMapping("testStudent")
    public String testStudent(ModelMap map){

        Student student = new Student();
        student.setAge(18);
        student.setName("lihongyang");
        student.setLikeColor("<font color='red'>红色</font>");
        map.put("stu",student);
        return "test";
    }

    @GetMapping("list")
    public String list(ModelMap map){
        Student s1=new Student("111",18,"red");
        Student s2=new Student("222",19,"yellow");
        Student s3=new Student("333",16,"green");
        Student s4=new Student("444",17,"pink");
        Student s5=new Student("555",38,"blue");
        //转为list
        map.put("stuList", Arrays.asList(s1,s2,s3,s4,s5));
        return "test";
    }
}
