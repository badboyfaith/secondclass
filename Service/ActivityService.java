package com.example.demo.Service;

import com.example.demo.model.Activity;
import com.example.demo.model.ActivityExample;
import com.example.demo.Mapper.ActivityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private ActivityMapper activityMapper;

    /**
     * 创建一条新活动记录，带有业务逻辑验证
     */
    @Transactional
    public int createActivity(Activity activity) {
        // 业务逻辑验证
        validateActivity(activity);
        return activityMapper.insertSelective(activity);
    }

    /**
     * 更新一条活动记录，带有业务逻辑验证
     */
    @Transactional
    public int updateActivity(Activity activity) {
        // 业务逻辑验证
        validateActivity(activity);
        return activityMapper.updateByPrimaryKeySelective(activity);
    }

    /**
     * 根据主键删除一条活动记录
     */
    @Transactional
    public int deleteActivity(Long aId) {
        return activityMapper.deleteByPrimaryKey(aId);
    }

    /**
     * 根据主键查询一条活动记录
     */
    public Activity getActivityById(Long aId) {
        return activityMapper.selectByPrimaryKey(aId);
    }

    /**
     * 查询所有活动记录，支持分页和排序
     */
    public List<Activity> getAllActivities(ActivityExample example) {
        return activityMapper.selectByExample(example);
    }

    /**
     * 根据条件统计活动记录数量
     */
    public long countActivities(ActivityExample example) {
        return activityMapper.countByExample(example);
    }

    /**
     * 从Excel文件中导入活动记录
     */
    public void importActivitiesFromExcel(InputStream inputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        List<Activity> activities = new ArrayList<>();

        for (Row row : sheet) {
            Activity activity = new Activity();
            activity.setaName(row.getCell(0).getStringCellValue());
            activity.setContent(row.getCell(1).getStringCellValue());
            // 更多字段解析...
            activities.add(activity);
        }

        importActivities(activities);
    }

    /**
     * 导出活动记录到CSV文件
     */
    public void exportActivitiesToCSV(ActivityExample example, Writer writer) throws IOException {
        List<Activity> activities = activityMapper.selectByExample(example);

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("ID", "Name", "Content", "Start Time", "End Time"))) {
            for (Activity activity : activities) {
                csvPrinter.printRecord(activity.getaId(), activity.getaName(), activity.getContent(), activity.getStartTime(), activity.getEndTime());
            }
        }
    }

    /**
     * 导出活动记录到Excel文件
     */
    public void exportActivitiesToExcel(ActivityExample example, OutputStream outputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Activities");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Name");
        header.createCell(2).setCellValue("Content");

        List<Activity> activities = activityMapper.selectByExample(example);
        int rowIdx = 1;

        for (Activity activity : activities) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(activity.getaId());
            row.createCell(1).setCellValue(activity.getaName());
            row.createCell(2).setCellValue(activity.getContent());
        }

        workbook.write(outputStream);
    }

    /**
     * 批量导入活动记录
     */
    public void importActivities(List<Activity> activities) {
        for (Activity activity : activities) {
            createActivity(activity);
        }
    }

    /**
     * 对活动记录进行聚合操作（例如积分总和）
     */
    public float aggregatePoints(ActivityExample example) {
        List<Activity> activities = activityMapper.selectByExample(example);
        return activities.stream().map(Activity::getcPoint).reduce(0f, Float::sum);
    }

    /**
     * 业务逻辑验证
     */
    private void validateActivity(Activity activity) {
        if (activity.getaName() == null || activity.getaName().isEmpty()) {
            throw new IllegalArgumentException("活动名称不能为空");
        }
        // 其他业务逻辑验证...
    }
}

