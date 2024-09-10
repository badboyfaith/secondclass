package com.example.demo.Service;

import com.example.demo.Mapper.ActivityTypeMapper;
import com.example.demo.model.ActivityType;
import com.example.demo.model.ActivityTypeExample;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

@Service
public class ActivityTypeService {

    @Autowired
    private ActivityTypeMapper activityTypeMapper;

    /**
     * 创建一条新的活动类型记录，带有业务逻辑验证
     */
    @Transactional
    public int createActivityType(ActivityType activityType) {
        validateActivityType(activityType); // 业务逻辑验证
        return activityTypeMapper.insertSelective(activityType);
    }

    /**
     * 更新活动类型记录，带有业务逻辑验证
     */
    @Transactional
    public int updateActivityType(ActivityType activityType) {
        validateActivityType(activityType); // 业务逻辑验证
        return activityTypeMapper.updateByPrimaryKeySelective(activityType);
    }

    /**
     * 根据主键删除一条活动类型记录
     */
    @Transactional
    public int deleteActivityType(Long atId) {
        return activityTypeMapper.deleteByPrimaryKey(atId);
    }

    /**
     * 根据主键查询一条活动类型记录
     */
    public ActivityType getActivityTypeById(Long atId) {
        return activityTypeMapper.selectByPrimaryKey(atId);
    }

    /**
     * 查询所有活动类型记录，支持分页和排序
     */
    public List<ActivityType> getAllActivityTypes(ActivityTypeExample example) {
        return activityTypeMapper.selectByExample(example);
    }

    /**
     * 根据条件统计活动类型记录数量
     */
    public long countActivityTypes(ActivityTypeExample example) {
        return activityTypeMapper.countByExample(example);
    }

    /**
     * 导出活动类型记录到CSV文件
     */
    public void exportActivityTypesToCSV(ActivityTypeExample example, Writer writer) throws IOException {
        List<ActivityType> activityTypes = activityTypeMapper.selectByExample(example);
        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("ID", "Module", "Name"))) {
            for (ActivityType activityType : activityTypes) {
                csvPrinter.printRecord(activityType.getAtId(), activityType.getAtModule(), activityType.getAtName());
            }
        }
    }

    /**
     * 导出活动类型记录到Excel文件
     */
    public void exportActivityTypesToExcel(ActivityTypeExample example, OutputStream outputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("ActivityTypes");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Module");
        header.createCell(2).setCellValue("Name");

        List<ActivityType> activityTypes = activityTypeMapper.selectByExample(example);
        int rowIdx = 1;

        for (ActivityType activityType : activityTypes) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(activityType.getAtId());
            row.createCell(1).setCellValue(activityType.getAtModule());
            row.createCell(2).setCellValue(activityType.getAtName());
        }

        workbook.write(outputStream);
    }

    /**
     * 从Excel文件中导入活动类型记录
     */
    public void importActivityTypesFromExcel(InputStream inputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        List<ActivityType> activityTypes = new ArrayList<>();

        for (Row row : sheet) {
            ActivityType activityType = new ActivityType();
            activityType.setAtModule(row.getCell(0).getStringCellValue());
            activityType.setAtName(row.getCell(1).getStringCellValue());
            activityTypes.add(activityType);
        }

        for (ActivityType activityType : activityTypes) {
            createActivityType(activityType);
        }
    }

    /**
     * 业务逻辑验证
     */
    private void validateActivityType(ActivityType activityType) {
        if (activityType.getAtName() == null || activityType.getAtName().isEmpty()) {
            throw new IllegalArgumentException("活动类型名称不能为空");
        }
        // 其他业务逻辑验证...
    }
}
