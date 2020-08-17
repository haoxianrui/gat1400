package com.juxingtech.helmet.controller.system;


import cn.hutool.core.util.StrUtil;
import com.juxingtech.helmet.common.result.Result;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class SysUserController {

    public static Map<String, String> tokens;
    public static Map<String, String[]> roles;

    static {
        tokens = new HashMap<>();
        tokens.put("admin", "admin-token");
        tokens.put("guest", "guest-token");

        roles = new HashMap<>();
        roles.put("admin-token", new String[]{"admin"});
        roles.put("guest-token", new String[]{"guest"});
    }

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> map) {
        String username = map.get("username");
        String password = map.get("password");
        String token = tokens.get(username);
        if (StrUtil.isNotBlank(token) && "123456".equals(password)) {
            return Result.success(token);
        }
        return Result.error("账号或密码错误");
    }

    @GetMapping("/info")
    public Result info(String token) {
        String[] arr = roles.get(token);
        return Result.success(arr);
    }


    @PostMapping("/logout")
    public Result logout(){
        return Result.success();
    }
}
