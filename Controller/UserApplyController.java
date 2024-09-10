package com.example.demo.Controller;

import com.example.demo.model.UserApply;
import com.example.demo.Service.UserApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/userApply")
public class UserApplyController {

    @Autowired
    private UserApplyService userApplyService;

    /**
     * 获取所有用户申请信息
     * @return List<UserApply>
     */
    @GetMapping("/all")
    public List<UserApply> getAllUserApply() {
        return userApplyService.getAllUserApply();
    }

    /**
     * 根据ID获取用户申请
     * @param id 用户申请ID
     * @return UserApply
     */
    @GetMapping("/{id}")
    public UserApply getUserApplyById(@PathVariable Long id) {
        return userApplyService.getUserApplyById(id);
    }

    /**
     * 创建新的用户申请
     * @param userApply 用户申请对象
     * @return 成功或失败消息
     */
    @PostMapping("/create")
    public String createUserApply(@RequestBody UserApply userApply) {
        userApplyService.createUserApply(userApply);
        return "创建成功";
    }

    /**
     * 更新用户申请
     * @param userApply 用户申请对象
     * @return 成功或失败消息
     */
    @PutMapping("/update")
    public String updateUserApply(@RequestBody UserApply userApply) {
        userApplyService.updateUserApply(userApply);
        return "更新成功";
    }

    /**
     * 删除用户申请（逻辑删除）
     * @param id 用户申请ID
     * @return 成功或失败消息
     */
    @DeleteMapping("/delete/{id}")
    public String deleteUserApply(@PathVariable Long id) {
        userApplyService.deleteUserApply(id);
        return "删除成功";
    }

    /**
     * 分页查询用户申请信息
     * @param page 当前页
     * @param size 每页数量
     * @return List<UserApply>
     */
    @GetMapping("/page")
    public List<UserApply> getUserApplyByPage(@RequestParam int page, @RequestParam int size) {
        return userApplyService.getUserApplyByPage(page, size);
    }
    // 上传CSV文件
    @PostMapping("/import/csv")
    public String importCSV(@RequestParam("file") MultipartFile file) {
        try {
            userApplyService.importCSV(file);
            return "CSV文件导入成功";
        } catch (IOException e) {
            return "CSV文件导入失败: " + e.getMessage();
        }
    }

    // 上传Excel文件
    @PostMapping("/import/excel")
    public String importExcel(@RequestParam("file") MultipartFile file) {
        try {
            userApplyService.importExcel(file);
            return "Excel文件导入成功";
        } catch (IOException e) {
            return "Excel文件导入失败: " + e.getMessage();
        }
    }

    // 下载CSV文件
    @GetMapping("/export/csv")
    public void exportCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"userApply.csv\"");
        userApplyService.exportCSV(response.getOutputStream());
    }

    // 下载Excel文件
    @GetMapping("/export/excel")
    public void exportExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"userApply.xlsx\"");
        userApplyService.exportExcel(response.getOutputStream());
    }
}
