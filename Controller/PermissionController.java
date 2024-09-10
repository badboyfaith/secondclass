package com.example.demo.Controller;

import com.example.demo.model.Permission;
import com.example.demo.Service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 创建权限，带业务逻辑验证
     */
    @PostMapping("/create")
    public String createPermission(@RequestBody Permission permission) {
        permissionService.createPermission(permission);
        return "权限创建成功";
    }

    /**
     * 更新权限，带业务逻辑验证
     */
    @PutMapping("/update")
    public String updatePermission(@RequestBody Permission permission) {
        permissionService.updatePermission(permission);
        return "权限更新成功";
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/delete/{id}")
    public String deletePermission(@PathVariable Integer id) {
        permissionService.deletePermissionById(id);
        return "权限删除成功";
    }

    /**
     * 获取所有权限
     */
    @GetMapping("/all")
    public List<Permission> getAllPermissions() {
        return permissionService.getAllPermissions();
    }

    /**
     * 查询单个权限
     */
    @GetMapping("/get/{id}")
    public Permission getPermissionById(@PathVariable Integer id) {
        return permissionService.getPermissionById(id);
    }

    /**
     * 分页和排序查询
     */
    @GetMapping("/page")
    public List<Permission> getPermissionsWithPagination(@RequestParam int pageNum,
                                                         @RequestParam int pageSize,
                                                         @RequestParam String sortBy,
                                                         @RequestParam boolean ascending) {
        return permissionService.getPermissionsWithPagination(pageNum, pageSize, sortBy, ascending);
    }

    /**
     * 导入Excel并更新
     */
    @PostMapping("/import/excel")
    public String importExcelAndUpdate(@RequestParam("file") MultipartFile file) throws IOException {
        permissionService.importExcelAndUpdate(file);
        return "Excel文件导入并更新成功";
    }

    /**
     * 导入CSV并更新
     */
    @PostMapping("/import/csv")
    public String importCSVAndUpdate(@RequestParam("file") MultipartFile file) throws IOException {
        permissionService.importCSVAndUpdate(file);
        return "CSV文件导入并更新成功";
    }

    /**
     * 导出为Excel文件
     */
    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=permissions.xlsx");
        permissionService.exportToExcel(response.getOutputStream());
    }

    /**
     * 导出为CSV文件
     */
    @GetMapping("/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=permissions.csv");
        permissionService.exportToCSV(response.getOutputStream());
    }
}

