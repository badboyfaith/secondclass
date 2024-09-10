package com.example.demo.Controller;

import com.example.demo.model.Credit;
import com.example.demo.Service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api/credits")
public class CreditController {

    @Autowired
    private CreditService creditService;

    /**
     * 获取所有记录，支持分页和排序
     */
    @GetMapping
    public List<Credit> getAllCredits(@RequestParam(defaultValue = "1") int pageNum,
                                      @RequestParam(defaultValue = "10") int pageSize,
                                      @RequestParam(defaultValue = "cId DESC") String orderBy) {
        return creditService.getAllCredits(pageNum, pageSize, orderBy);
    }

    /**
     * 根据 ID 查询单条记录
     */
    @GetMapping("/{id}")
    public Credit getCreditById(@PathVariable Long id) {
        return creditService.getCreditById(id);
    }

    /**
     * 创建记录
     */
    @PostMapping
    public void createCredit(@RequestBody Credit credit) {
        creditService.createCredit(credit);
    }

    /**
     * 更新记录
     */
    @PutMapping("/{id}")
    public void updateCredit(@PathVariable Long id, @RequestBody Credit credit) {
        credit.setcId(id);
        creditService.updateCredit(credit);
    }

    /**
     * 删除记录
     */
    @DeleteMapping("/{id}")
    public void deleteCredit(@PathVariable Long id) {
        creditService.deleteCredit(id);
    }

    /**
     * 批量删除记录
     */
    @DeleteMapping("/batch")
    public void batchDeleteCredits(@RequestBody List<Long> ids) {
        creditService.batchDeleteCredits(ids);
    }

    /**
     * 导入 Excel 文件
     */
    @PostMapping("/import/excel")
    public void importCreditsFromExcel(@RequestParam("file") MultipartFile file) throws IOException {
        creditService.importCredits(file);
    }

    /**
     * 导入 CSV 文件
     */
    @PostMapping("/import/csv")
    public void importCreditsFromCsv(@RequestParam("file") MultipartFile file) throws IOException {
        creditService.importCreditsFromCsv(file);
    }

    /**
     * 导出数据到 Excel 文件
     */
    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportCreditsToExcel() throws IOException {
        File file = creditService.exportCreditsToExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return ResponseEntity.ok().headers(headers).body(fileContent);
    }

    /**
     * 导出数据到 CSV 文件
     */
    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportCreditsToCsv() throws IOException {
        File file = creditService.exportCreditsToCsv();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return ResponseEntity.ok().headers(headers).body(fileContent);
    }
}
