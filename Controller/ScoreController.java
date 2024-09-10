package com.example.demo.Controller;

import com.example.demo.model.Score;
import com.example.demo.Service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/scores")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    /**
     * 创建成绩记录，带业务逻辑验证
     */
    @PostMapping("/create")
    public String createScore(@RequestBody Score score) {
        scoreService.createScore(score);
        return "成绩记录创建成功";
    }

    /**
     * 更新成绩记录，带业务逻辑验证
     */
    @PutMapping("/update")
    public String updateScore(@RequestBody Score score) {
        scoreService.updateScore(score);
        return "成绩记录更新成功";
    }

    /**
     * 删除成绩记录
     */
    @DeleteMapping("/delete/{id}")
    public String deleteScore(@PathVariable Long id) {
        scoreService.deleteScoreById(id);
        return "成绩记录删除成功";
    }

    /**
     * 获取所有成绩记录
     */
    @GetMapping("/all")
    public List<Score> getAllScores() {
        return scoreService.getAllScores();
    }

    /**
     * 查询单个成绩记录
     */
    @GetMapping("/get/{id}")
    public Score getScoreById(@PathVariable Long id) {
        return scoreService.getScoreById(id);
    }

    /**
     * 分页和排序查询
     */
    @GetMapping("/page")
    public List<Score> getScoresWithPagination(@RequestParam int pageNum,
                                               @RequestParam int pageSize,
                                               @RequestParam String sortBy,
                                               @RequestParam boolean ascending) {
        return scoreService.getScoresWithPagination(pageNum, pageSize, sortBy, ascending);
    }
    /**
     * 导入 Excel 文件
     *
     * @param file Excel 文件
     * @throws IOException 文件处理异常
     */
    @PostMapping("/import/excel")
    public String importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        scoreService.importExcel(file);
        return "Excel file imported successfully!";
    }

    /**
     * 导入 CSV 文件
     *
     * @param file CSV 文件
     * @throws IOException 文件处理异常
     */
    @PostMapping("/import/csv")
    public String importCSV(@RequestParam("file") MultipartFile file) throws IOException {
        scoreService.importCSV(file);
        return "CSV file imported successfully!";
    }

    /**
     * 导出 Excel 文件
     *
     * @param response HTTP 响应
     * @throws IOException 文件处理异常
     */
    @GetMapping("/export/excel")
    public void exportExcel(HttpServletResponse response) throws IOException {
        scoreService.exportExcel(response);
    }

    /**
     * 导出 CSV 文件
     *
     * @param response HTTP 响应
     * @throws IOException 文件处理异常
     */
    @GetMapping("/export/csv")
    public void exportCSV(HttpServletResponse response) throws IOException {
        scoreService.exportCSV(response);
    }
}

