package com.example.demo.Controller;

import com.example.demo.model.ScoreType;
import com.example.demo.Service.ScoreTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/scoreType")
public class ScoreTypeController {

    @Autowired
    private ScoreTypeService scoreTypeService;

    /**
     * 获取所有 ScoreType 数据
     */
    @GetMapping("/all")
    public List<ScoreType> getAll() {
        return scoreTypeService.getScoreTypesByPage(0, 100);
    }

    /**
     * 根据 ID 获取单个 ScoreType 数据
     */
    @GetMapping("/{id}")
    public ScoreType getById(@PathVariable Long id) {
        return scoreTypeService.getScoreTypeById(id);
    }

    /**
     * 创建新的 ScoreType 数据
     */
    @PostMapping("/create")
    public String create(@RequestBody ScoreType scoreType) {
        scoreTypeService.insert(scoreType);
        return "创建成功";
    }

    /**
     * 更新现有的 ScoreType 数据
     */
    @PutMapping("/update")
    public String update(@RequestBody ScoreType scoreType) {
        scoreTypeService.update(scoreType);
        return "更新成功";
    }

    /**
     * 删除 ScoreType 数据
     */
    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        scoreTypeService.deleteById(id);
        return "删除成功";
    }

    /**
     * Excel 导入数据
     */
    @PostMapping("/import/excel")
    public String importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        scoreTypeService.importExcel(file);
        return "Excel 导入成功";
    }

    /**
     * CSV 导入数据
     */
    @PostMapping("/import/csv")
    public String importCSV(@RequestParam("file") MultipartFile file) throws IOException {
        scoreTypeService.importCSV(file.getInputStream());
        return "CSV 导入成功";
    }

    /**
     * Excel 导出数据
     */
    @GetMapping("/export/excel")
    public void exportExcel(HttpServletResponse response) throws IOException {
        response.setHeader("Content-Disposition", "attachment; filename=score_types.xlsx");
        scoreTypeService.exportExcel(response.getOutputStream());
    }

    /**
     * CSV 导出数据
     */
    @GetMapping("/export/csv")
    public void exportCSV(HttpServletResponse response) throws IOException {
        response.setHeader("Content-Disposition", "attachment; filename=score_types.csv");
        scoreTypeService.exportCSV(response.getOutputStream());
    }
}

