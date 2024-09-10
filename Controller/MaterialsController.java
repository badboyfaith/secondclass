package com.example.demo.Controller;

import com.example.demo.model.Materials;
import com.example.demo.Service.MaterialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/materials")
public class MaterialsController {

    @Autowired
    private MaterialsService materialsService;

    // 搜索与分页查询
    @GetMapping("/search")
    public List<Materials> searchMaterials(@RequestParam String keyword, @RequestParam int page, @RequestParam int size) {
        return materialsService.searchMaterials(keyword, page, size);
    }

    // 添加新材料
    @PostMapping("/add")
    public String addMaterial(@RequestBody Materials material) {
        materialsService.addMaterial(material);
        return "材料添加成功";
    }

    // 更新材料
    @PutMapping("/update")
    public String updateMaterial(@RequestBody Materials material) {
        materialsService.updateMaterial(material);
        return "材料更新成功";
    }

    // 获取单个材料
    @GetMapping("/{id}")
    public Materials getMaterial(@PathVariable("id") Long id) {
        return materialsService.getMaterialById(id);
    }

    // 获取所有材料
    @GetMapping("/all")
    public List<Materials> getAllMaterials(@RequestParam int page, @RequestParam int size, @RequestParam String orderBy) {
        return materialsService.getAllMaterials(page, size, orderBy);
    }

    // 批量删除材料
    @DeleteMapping("/delete")
    public String deleteMaterials(@RequestBody List<Long> ids) {
        materialsService.deleteMaterials(ids);
        return "材料删除成功";
    }

    // 导入Excel并更新表格
    @PostMapping("/import/excel")
    public String importExcelAndUpdate(@RequestParam("file") MultipartFile file) throws IOException {
        materialsService.importExcelAndUpdate(file);
        return "Excel文件导入并更新成功";
    }

    // 导入CSV并更新表格
    @PostMapping("/import/csv")
    public String importCSVAndUpdate(@RequestParam("file") MultipartFile file) throws IOException {
        materialsService.importCSVAndUpdate(file);
        return "CSV文件导入并更新成功";
    }

    // 导出Excel
    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=materials.xlsx");
        materialsService.exportToExcel(response.getOutputStream());
    }

    // 导出CSV
    @GetMapping("/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=materials.csv");
        materialsService.exportToCSV(response.getOutputStream());
    }
}

