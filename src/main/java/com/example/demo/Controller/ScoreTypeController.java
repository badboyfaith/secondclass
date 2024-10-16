package com.example.demo.Controller;

import com.example.demo.Service.ScoreTypeService;
import com.example.demo.model.ScoreType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
     * 批量创建 ScoreType 数据
     */
    @PostMapping("/createBatch")
    public String createBatch(@RequestBody List<ScoreType> scoreTypes) {
        scoreTypeService.insertBatch(scoreTypes);
        return "批量创建成功";
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



}

