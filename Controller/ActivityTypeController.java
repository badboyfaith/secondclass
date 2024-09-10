package com.example.demo.Controller;

import com.example.demo.model.ActivityType;
import com.example.demo.model.ActivityTypeExample;
import com.example.demo.Service.ActivityTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

@RestController
@RequestMapping("/api/activity-types")
public class ActivityTypeController {

    @Autowired
    private ActivityTypeService activityTypeService;

    /**
     * 创建一条新活动类型记录，带有业务逻辑验证
     */
    @PostMapping
    public String createActivityType(@RequestBody ActivityType activityType) {
        activityTypeService.createActivityType(activityType);
        return "活动类型创建成功";
    }

    /**
     * 更新一条活动类型记录，带有业务逻辑验证
     */
    @PutMapping("/{id}")
    public String updateActivityType(@PathVariable("id") Long id, @RequestBody ActivityType activityType) {
        activityType.setAtId(id);
        activityTypeService.updateActivityType(activityType);
        return "活动类型更新成功";
    }

    /**
     * 删除一条活动类型记录
     */
    @DeleteMapping("/{id}")
    public String deleteActivityType(@PathVariable("id") Long id) {
        activityTypeService.deleteActivityType(id);
        return "活动类型删除成功";
    }

    /**
     * 查询单个活动类型记录
     */
    @GetMapping("/{id}")
    public ActivityType getActivityTypeById(@PathVariable("id") Long id) {
        return activityTypeService.getActivityTypeById(id);
    }

    /**
     * 查询所有活动类型记录，支持分页和排序
     */
    @GetMapping
    public List<ActivityType> getAllActivityTypes(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        ActivityTypeExample example = new ActivityTypeExample();
        if (sortBy != null && order != null) {
            String orderByClause = sortBy + " " + order;
            example.setOrderByClause(orderByClause);
        }
        return activityTypeService.getAllActivityTypes(example);
    }

    /**
     * 导出活动类型记录到CSV文件
     */
    @GetMapping("/export/csv")
    public void exportActivityTypesToCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"activity_types.csv\"");
        OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
        ActivityTypeExample example = new ActivityTypeExample();
        activityTypeService.exportActivityTypesToCSV(example, writer);
        writer.flush();
    }

    /**
     * 导出活动类型记录到Excel文件
     */
    @GetMapping("/export/excel")
    public void exportActivityTypesToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"activity_types.xlsx\"");
        ActivityTypeExample example = new ActivityTypeExample();
        activityTypeService.exportActivityTypesToExcel(example, response.getOutputStream());
    }

    /**
     * 根据条件统计活动类型记录数量
     */
    @GetMapping("/count")
    public long countActivityTypes() {
        ActivityTypeExample example = new ActivityTypeExample();
        return activityTypeService.countActivityTypes(example);
    }
}
