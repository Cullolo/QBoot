package cn.example.mp.test.controller;


import cn.example.mp.test.annotation.MyInterceptAnno;
import cn.example.mp.test.common.MyException;
import cn.example.mp.test.entity.Rule;
import cn.example.mp.test.entity.User;
import cn.example.mp.test.mapper.UserMapper;
import cn.example.mp.test.service.RuleService;
import cn.example.mp.test.service.UserServiceImpl;
import cn.example.mp.test.util.HttpClientUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @Description: TODO
 * @Author: xianpei.qin 
 * @Date: 2020/4/28 17:40
 */
@Controller
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RuleService ruleService;

    @GetMapping("/getList")
    @ResponseBody
    @MyInterceptAnno
    public List<User> getUserList() throws IllegalAccessException {
        List<User> list = null;
        try {
            list = userService.list();
            int count = userService.count();

            User user = list.get(0);
            Class<? extends User> aClass = user.getClass();

            Field[] fields = aClass.getFields();

            Field[] declaredFields = aClass.getDeclaredFields();

            for (User user1 : list) {
                for (Field field : declaredFields) {
                    TableId annotation = field.getAnnotation(TableId.class);
                    field.setAccessible(true);
                    //暴力访问私有属性，默认为false，不可访问私有属性
                    field.setAccessible(true);
                    if(null!=annotation){
                        String name = field.getName();
                        System.out.println(name);
                        System.out.println(field.get(user1));
                        System.out.println("----------");
                    }
                }

            }
            System.out.println("目标方法count="+count);
        }catch (Exception e){
            System.out.println(e.toString());
        }
        return list;
    }

    @GetMapping("/getHolle")
    @ResponseBody
    public String getHolleKit( ){

        return "hello";
    }

    @GetMapping("getUserTest")
    @ResponseBody
    private User getUserTest(){

        User user = userMapper.selectById("1");

        return user;
    }

}

