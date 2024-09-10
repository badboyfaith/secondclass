package com.example.demo.Controller;

import com.example.demo.model.Additional;
import com.example.demo.model.AdditionalExample;
import com.example.demo.Service.AdditionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

@RestController
@RequestMapping("/api/additionals")
public class AdditionalController {

    @Autowired
    private AdditionalService additionalService;

    /**
     * 创建一条新的附加信息记录，带有业务逻辑验证
     */
    @PostMapping
    public String createAdditional(@RequestBody Additional additional) {
        additionalService.createAdditional(additional);
        return "附加信息创建成功";
    }

    /**
     * 更新一条附加信息记录，带有业务逻辑验证
     */
    @PutMapping("/{id}")
    public String updateAdditional(@PathVariable("id") Long id, @RequestBody Additional additional) {
        additional.setAdId(id);
        additionalService.updateAdditional(additional);
        return "附加信息更新成功";
    }

    /**
     * 删除一条附加信息记录
     */
    @DeleteMapping("/{id}")
    public String deleteAdditional(@PathVariable("id") Long id) {
        additionalService.deleteAdditional(id);
        return "附加信息删除成功";
    }

    /**
     * 查询单个附加信息记录
     */
    @GetMapping("/{id}")
    public Additional getAdditionalById(@PathVariable("id") Long id) {
        return additionalService.getAdditionalById(id);
    }

    /**
     * 查询所有附加信息记录，支持分页和排序
     */
    @GetMapping
    public List<Additional> getAllAdditionals(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        AdditionalExample example = new AdditionalExample();
        if (sortBy != null && order != null) {
            String orderByClause = sortBy + " " + order;
            example.setOrderByClause(orderByClause);
        }
        return additionalService.getAllAdditionals(example);
    }

    /**
     * 导出附加信息记录到CSV文件
     */
    @GetMapping("/export/csv")
    public void exportAdditionalsToCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"additionals.csv\"");
        OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
        AdditionalExample example = new AdditionalExample();
        additionalService.exportAdditionalsToCSV(example, writer);
        writer.flush();
    }

    /**
     * 导出附加信息记录到Excel文件
     */
    @GetMapping("/export/excel")
    public void exportAdditionalsToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"additionals.xlsx\"");
        AdditionalExample example = new AdditionalExample();
        additionalService.exportAdditionalsToExcel(example, response.getOutputStream());
    }

    /**
     * 根据条件统计附加信息记录数量
     */
    @GetMapping("/count")
    public long countAdditionals() {
        AdditionalExample example = new AdditionalExample();
        return additionalService.countAdditionals(example);
    }
}

