package com.example.demo.Controller;

import com.example.demo.model.Activity;
import com.example.demo.model.ActivityExample;
import com.example.demo.Service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    /**
     * 创建一条新活动记录，带有业务逻辑验证
     */
    @PostMapping
    public String createActivity(@RequestBody Activity activity) {
        activityService.createActivity(activity);
        return "活动创建成功";
    }

    /**
     * 更新一条活动记录，带有业务逻辑验证
     */
    @PutMapping("/{id}")
    public String updateActivity(@PathVariable("id") Long id, @RequestBody Activity activity) {
        activity.setaId(id);
        activityService.updateActivity(activity);
        return "活动更新成功";
    }

    /**
     * 根据主键删除一条活动记录
     */
    @DeleteMapping("/{id}")
    public String deleteActivity(@PathVariable("id") Long id) {
        activityService.deleteActivity(id);
        return "活动删除成功";
    }

    /**
     * 根据主键查询一条活动记录
     */
    @GetMapping("/{id}")
    public Activity getActivityById(@PathVariable("id") Long id) {
        return activityService.getActivityById(id);
    }

    /**
     * 查询所有活动记录，支持分页和排序
     */
    @GetMapping
    public List<Activity> getAllActivities(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        ActivityExample example = new ActivityExample();
        if (sortBy != null && order != null) {
            String orderByClause = sortBy + " " + order;
            example.setOrderByClause(orderByClause);
        }
        return activityService.getAllActivities(example);
    }

    /**
     * 根据条件统计活动记录数量
     */
    @GetMapping("/count")
    public long countActivities() {
        ActivityExample example = new ActivityExample();
        return activityService.countActivities(example);
    }

    /**
     * 对活动记录进行聚合操作，返回积分总和
     */
    @GetMapping("/aggregate/points")
    public float aggregatePoints() {
        ActivityExample example = new ActivityExample();
        return activityService.aggregatePoints(example);
    }

    /**
     * 导入活动记录
     */
    @PostMapping("/import")
    public String importActivities(@RequestBody List<Activity> activities) {
        activityService.importActivities(activities);
        return "活动记录导入成功";
    }

    /**
     * 导出活动记录到CSV文件
     */
    @GetMapping("/export/csv")
    public void exportActivitiesToCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"activities.csv\"");
        OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
        ActivityExample example = new ActivityExample();
        activityService.exportActivitiesToCSV(example, writer);
        writer.flush();
    }

    /**
     * 导出活动记录到Excel文件
     */
    @GetMapping("/export/excel")
    public void exportActivitiesToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"activities.xlsx\"");
        ActivityExample example = new ActivityExample();
        activityService.exportActivitiesToExcel(example, response.getOutputStream());
    }
}

