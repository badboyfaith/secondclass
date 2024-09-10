package com.example.demo.Controller;

import com.example.demo.model.Apply;
import com.example.demo.Service.ApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/applications")
public class ApplyController {

    @Autowired
    private ApplyService applyService;

    /**
     * 获取所有记录（分页与排序）
     */
    @GetMapping
    public List<Apply> getAllApplications(@RequestParam(defaultValue = "1") int pageNum,
                                          @RequestParam(defaultValue = "10") int pageSize,
                                          @RequestParam(defaultValue = "apId ASC") String orderBy) {
        return applyService.getAllApplications(pageNum, pageSize, orderBy);
    }

    /**
     * 根据 ID 获取单个记录
     */
    @GetMapping("/{id}")
    public Apply getApplicationById(@PathVariable("id") Long id) {
        return applyService.getApplicationById(id);
    }

    /**
     * 创建记录
     */
    @PostMapping
    public ResponseEntity<String> createApplication(@RequestBody Apply application) {
        applyService.createApplication(application);
        return ResponseEntity.ok("创建成功");
    }

    /**
     * 更新记录
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateApplication(@PathVariable("id") Long id, @RequestBody Apply application) {
        application.setApId(id);
        applyService.updateApplication(application);
        return ResponseEntity.ok("更新成功");
    }

    /**
     * 删除记录
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteApplication(@PathVariable("id") Long id) {
        applyService.deleteApplication(id);
        return ResponseEntity.ok("删除成功");
    }

    /**
     * 批量删除记录
     */
    @DeleteMapping("/batch")
    public ResponseEntity<String> batchDeleteApplications(@RequestBody List<Long> ids) {
        applyService.batchDeleteApplications(ids);
        return ResponseEntity.ok("批量删除成功");
    }

    /**
     * 导入 Excel 文件
     */
    @PostMapping("/import/excel")
    public ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        applyService.importApplications(file);
        return ResponseEntity.ok("Excel 导入成功");
    }

    /**
     * 导入 CSV 文件
     */
    @PostMapping("/import/csv")
    public ResponseEntity<String> importCsv(@RequestParam("file") MultipartFile file) throws IOException {
        applyService.importApplicationsFromCsv(file);
        return ResponseEntity.ok("CSV 导入成功");
    }

    /**
     * 导出数据到 Excel 文件
     */
    @GetMapping("/export/excel")
    public ResponseEntity<File> exportExcel() throws IOException {
        File file = applyService.exportApplicationsToExcel();
        return ResponseEntity.ok(file);
    }

    /**
     * 导出数据到 CSV 文件
     */
    @GetMapping("/export/csv")
    public ResponseEntity<File> exportCsv() throws IOException {
        File file = applyService.exportApplicationsToCsv();
        return ResponseEntity.ok(file);
    }
}

