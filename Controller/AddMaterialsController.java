package com.example.demo.Controller;

import com.example.demo.model.AddMaterials;
import com.example.demo.Service.AddMaterialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/add-materials")
public class AddMaterialsController {

    @Autowired
    private AddMaterialsService addMaterialsService;

    /**
     * 获取所有记录（分页与排序）
     */
    @GetMapping
    public List<AddMaterials> getAllMaterials(@RequestParam(defaultValue = "1") int pageNum,
                                              @RequestParam(defaultValue = "10") int pageSize,
                                              @RequestParam(defaultValue = "amId ASC") String orderBy) {
        return addMaterialsService.getAllMaterials(pageNum, pageSize, orderBy);
    }

    /**
     * 根据ID获取单个记录
     */
    @GetMapping("/{id}")
    public AddMaterials getMaterialById(@PathVariable("id") Long id) {
        return addMaterialsService.getMaterialById(id);
    }

    /**
     * 创建记录
     */
    @PostMapping
    public ResponseEntity<String> createMaterial(@RequestBody AddMaterials material) {
        addMaterialsService.createMaterial(material);
        return ResponseEntity.ok("创建成功");
    }

    /**
     * 更新记录
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateMaterial(@PathVariable("id") Long id, @RequestBody AddMaterials material) {
        material.setAmId(id);
        addMaterialsService.updateMaterial(material);
        return ResponseEntity.ok("更新成功");
    }

    /**
     * 删除记录
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMaterial(@PathVariable("id") Long id) {
        addMaterialsService.deleteMaterial(id);
        return ResponseEntity.ok("删除成功");
    }

    /**
     * 批量删除记录
     */
    @DeleteMapping("/batch")
    public ResponseEntity<String> batchDeleteMaterials(@RequestBody List<Long> ids) {
        addMaterialsService.batchDeleteMaterials(ids);
        return ResponseEntity.ok("批量删除成功");
    }

    /**
     * 导入Excel文件
     */
    @PostMapping("/import/excel")
    public ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        addMaterialsService.importMaterials(file);
        return ResponseEntity.ok("Excel导入成功");
    }

    /**
     * 导入CSV文件
     */
    @PostMapping("/import/csv")
    public ResponseEntity<String> importCsv(@RequestParam("file") MultipartFile file) throws IOException {
        addMaterialsService.importMaterialsFromCsv(file);
        return ResponseEntity.ok("CSV导入成功");
    }

    /**
     * 导出数据到Excel文件
     */
    @GetMapping("/export/excel")
    public ResponseEntity<File> exportExcel() throws IOException {
        File file = addMaterialsService.exportMaterialsToExcel();
        return ResponseEntity.ok(file);
    }

    /**
     * 导出数据到CSV文件
     */
    @GetMapping("/export/csv")
    public ResponseEntity<File> exportCsv() throws IOException {
        File file = addMaterialsService.exportMaterialsToCsv();
        return ResponseEntity.ok(file);
    }
}

