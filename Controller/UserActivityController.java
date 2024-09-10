package com.example.demo.Controller;

import com.example.demo.model.UserActivity;
import com.example.demo.Service.UserActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@RestController
@RequestMapping("/userActivity")
public class UserActivityController {

    @Autowired
    private UserActivityService userActivityService;

    /**
     * 获取所有用户活动记录
     */
    @GetMapping("/list")
    public List<UserActivity> getAllUserActivities() {
        return userActivityService.getAllUserActivities();
    }

    /**
     * 通过ID获取单个用户活动记录
     */
    @GetMapping("/{id}")
    public UserActivity getUserActivity(@PathVariable("id") Long id) {
        return userActivityService.getUserActivityById(id);
    }

    /**
     * 创建新的用户活动记录
     */
    @PostMapping("/create")
    public ResponseEntity<String> createUserActivity(@RequestBody UserActivity userActivity) {
        userActivityService.createUserActivity(userActivity);
        return ResponseEntity.ok("用户活动创建成功");
    }

    /**
     * 更新用户活动记录
     */
    @PutMapping("/update")
    public ResponseEntity<String> updateUserActivity(@RequestBody UserActivity userActivity) {
        userActivityService.updateUserActivity(userActivity);
        return ResponseEntity.ok("用户活动更新成功");
    }

    /**
     * 删除用户活动记录
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUserActivity(@PathVariable("id") Long id) {
        userActivityService.deleteUserActivity(id);
        return ResponseEntity.ok("用户活动删除成功");
    }

    /**
     * CSV 文件导入用户活动记录
     */
    @PostMapping("/import")
    public ResponseEntity<String> importUserActivities(@RequestParam("file") MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            userActivityService.importCSV(inputStream);
            return ResponseEntity.ok("CSV 文件导入成功");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("CSV 导入失败");
        }
    }

    /**
     * 导出用户活动记录为 CSV 文件
     */
    @GetMapping("/export")
    public void exportUserActivities(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"user_activities.csv\"");
        try (OutputStream outputStream = response.getOutputStream()) {
            userActivityService.exportCSV(outputStream);
        }
    }
}
