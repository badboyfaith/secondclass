package com.example.demo.Service;

import com.example.demo.Mapper.ApplyMapper;
import com.example.demo.model.Apply;
import com.example.demo.model.ApplyExample;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

@Service
public class ApplyService {

    @Autowired
    private ApplyMapper applyMapper;

    /**
     * 获取所有记录，支持分页和排序
     */
    public List<Apply> getAllApplications(int pageNum, int pageSize, String orderBy) {
        ApplyExample example = new ApplyExample();
        example.setOrderByClause(orderBy);
        example.setDistinct(true);  // 可以设置是否排重
        // 这里可以结合分页逻辑，比如结合 MyBatis 的 PageHelper 进行分页
        return applyMapper.selectByExample(example);
    }

    /**
     * 根据 ID 查询单条记录
     */
    public Apply getApplicationById(Long apId) {
        return applyMapper.selectByPrimaryKey(apId);
    }

    /**
     * 创建记录并进行业务逻辑验证
     */
    @Transactional
    public void createApplication(Apply application) {
        // 业务逻辑验证
        if (application.getPhoto() == null) {
            throw new IllegalArgumentException("Photo不能为空");
        }
        applyMapper.insertSelective(application);
    }

    /**
     * 更新记录并进行业务逻辑验证
     */
    @Transactional
    public void updateApplication(Apply application) {
        // 业务逻辑验证
        if (application.getApId() == null) {
            throw new IllegalArgumentException("apId不能为空");
        }
        applyMapper.updateByPrimaryKeySelective(application);
    }

    /**
     * 删除记录
     */
    @Transactional
    public void deleteApplication(Long apId) {
        applyMapper.deleteByPrimaryKey(apId);
    }

    /**
     * 批量删除记录
     */
    @Transactional
    public void batchDeleteApplications(List<Long> ids) {
        ApplyExample example = new ApplyExample();
        example.createCriteria().andApIdIn(ids);
        applyMapper.deleteByExample(example);
    }

    /**
     * 导入 Excel 文件并插入/更新数据
     */
    @Transactional
    public void importApplications(MultipartFile file) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;  // 跳过表头
                Long apId = (long) row.getCell(0).getNumericCellValue();
                String photo = row.getCell(1).getStringCellValue();
                Boolean isDelete = row.getCell(2).getBooleanCellValue();

                Apply application = new Apply();
                application.setApId(apId);
                application.setPhoto(photo);
                application.setIsDelete(isDelete);

                // 更新或插入
                Apply existingApplication = applyMapper.selectByPrimaryKey(apId);
                if (existingApplication != null) {
                    applyMapper.updateByPrimaryKeySelective(application);
                } else {
                    applyMapper.insert(application);
                }
            }
        }
    }

    /**
     * 导入 CSV 文件并插入/更新数据
     */
    @Transactional
    public void importApplicationsFromCsv(MultipartFile file) throws IOException {
        try (Reader reader = new InputStreamReader(file.getInputStream())) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
            for (CSVRecord record : records) {
                Long apId = Long.parseLong(record.get("apId"));
                String photo = record.get("photo");
                Boolean isDelete = Boolean.parseBoolean(record.get("isDelete"));

                Apply application = new Apply();
                application.setApId(apId);
                application.setPhoto(photo);
                application.setIsDelete(isDelete);

                // 更新或插入
                Apply existingApplication = applyMapper.selectByPrimaryKey(apId);
                if (existingApplication != null) {
                    applyMapper.updateByPrimaryKeySelective(application);
                } else {
                    applyMapper.insert(application);
                }
            }
        }
    }

    /**
     * 导出数据到 Excel 文件
     */
    public File exportApplicationsToExcel() throws IOException {
        List<Apply> applicationsList = applyMapper.selectByExample(new ApplyExample());

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Applications");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("apId");
        headerRow.createCell(1).setCellValue("photo");
        headerRow.createCell(2).setCellValue("isDelete");

        int rowNum = 1;
        for (Apply application : applicationsList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(application.getApId());
            row.createCell(1).setCellValue(application.getPhoto());
            row.createCell(2).setCellValue(application.getIsDelete());
        }

        File file = new File("applications_export.xlsx");
        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        }
        workbook.close();
        return file;
    }

    /**
     * 导出数据到 CSV 文件
     */
    public File exportApplicationsToCsv() throws IOException {
        List<Apply> applicationsList = applyMapper.selectByExample(new ApplyExample());

        File file = new File("applications_export.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("apId", "photo", "isDelete"))) {
            for (Apply application : applicationsList) {
                csvPrinter.printRecord(application.getApId(), application.getPhoto(), application.getIsDelete());
            }
        }
        return file;
    }
}
