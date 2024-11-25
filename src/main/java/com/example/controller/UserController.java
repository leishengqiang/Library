package com.example.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.entity.Back;
import com.example.entity.Borrow;
import com.example.entity.User;
import com.example.service.IBackService;
import com.example.service.IBorrowService;
import com.example.service.IUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lei
 * @since 2024-11-09
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IBackService backService;
    @Autowired
    private IBorrowService borrowService;

    @GetMapping("/{url}")
    public String redirect(@PathVariable("url") String url){
        return "/user/"+url;
    }

    @GetMapping("logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "login";
    }
    @PostMapping("/add")
    public String add(User user){
        this.userService.save(user);
        return "redirect:/sysadmin/userList";
    }

    @GetMapping("/findById/{id}")
    public String findById(@PathVariable("id") Integer id, Model model){
        User user = this.userService.getById(id);
        model.addAttribute("user", user);
        return "/sysadmin/updateUser";
    }

    @PostMapping("/update")
    public String update(User user){
        this.userService.updateById(user);
        return "redirect:/sysadmin/userList";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id){
        QueryWrapper<Borrow> borrowQueryWrapper=new QueryWrapper<>();
        borrowQueryWrapper.eq("uid",id);
        List<Borrow> borrows = this.borrowService.list(borrowQueryWrapper);
        for (Borrow borrow : borrows) {
            QueryWrapper<Back> backQueryWrapper=new QueryWrapper<>();
            backQueryWrapper.eq("brid",borrow.getId());
            this.backService.remove(backQueryWrapper);
        }
        this.borrowService.remove(borrowQueryWrapper);
        this.userService.removeById(id);
        return "redirect:/sysadmin/userList";
    }
}
