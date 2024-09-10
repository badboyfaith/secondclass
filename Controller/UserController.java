package com.example.demo.Controller;

import com.example.demo.model.User;
import com.example.demo.model.UserWithBLOBs;
import com.example.demo.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 搜索用户，带有分页和排序功能
     */
    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam(defaultValue = "0") int pageNum,
                                  @RequestParam(defaultValue = "10") int pageSize) {
        return userService.searchUsers(null, pageNum, pageSize);
    }

    /**
     * 创建用户，包含业务逻辑验证
     */
    @PostMapping("/create")
    public String createUser(@RequestBody UserWithBLOBs user) {
        userService.createUser(user);
        return "User created successfully!";
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public String updateUser(@RequestBody UserWithBLOBs user) {
        userService.updateUser(user);
        return "User updated successfully!";
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return "User deleted successfully!";
    }

    /**
     * 导入 Excel 文件
     */
    @PostMapping("/import")
    public String importFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            userService.importFromExcel(file);
            return "File imported successfully!";
        } catch (IOException e) {
            return "File import failed!";
        }
    }

    /**
     * 导出用户信息为 CSV 文件
     */
    @GetMapping("/export")
    public void exportToCSV(HttpServletResponse response) {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"users.csv\"");
        try {
            userService.exportToCSV(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
