package com.example.demo.Service;

import com.example.demo.Mapper.ScoreTypeMapper;
import com.example.demo.model.ScoreType;
import com.example.demo.model.ScoreTypeExample;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScoreTypeService {

    @Autowired
    private ScoreTypeMapper scoreTypeMapper;

    /**
     * 搜索并筛选 ScoreType 数据
     */
    public List<ScoreType> searchScoreTypes(ScoreTypeExample example) {
        return scoreTypeMapper.selectByExample(example);
    }

    /**
     * 批量插入 ScoreType 数据
     */
    public int insertBatch(List<ScoreType> scoreTypes) {
        for (ScoreType scoreType : scoreTypes) {
            scoreTypeMapper.insertSelective(scoreType);
        }
        return scoreTypes.size();
    }

    /**
     * 单条插入 ScoreType 数据
     */
    public int insert(ScoreType scoreType) {
        return scoreTypeMapper.insertSelective(scoreType);
    }

    /**
     * 根据主键删除 ScoreType
     */
    public int deleteById(Long id) {
        return scoreTypeMapper.deleteByPrimaryKey(id);
    }

    /**
     * 分页和排序查询
     */
    public List<ScoreType> getScoreTypesByPage(int offset, int limit) {
        ScoreTypeExample example = new ScoreTypeExample();
        example.setOrderByClause("st_id ASC");  // 可以更改排序字段和方式
        example.setDistinct(true); // 可以选择是否查询去重
        return scoreTypeMapper.selectByExample(example);
    }

    /**
     * Excel 导入数据
     */
    public void importExcel(MultipartFile file) throws IOException {
        List<ScoreType> scoreTypes = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // 跳过标题行
                ScoreType scoreType = new ScoreType();
                scoreType.setAtId((long) row.getCell(0).getNumericCellValue());
                scoreType.setsValue((float) row.getCell(1).getNumericCellValue());
                scoreTypes.add(scoreType);
            }
        }
        insertBatch(scoreTypes);
    }

    /**
     * CSV 导入数据
     */
    public void importCSV(InputStream inputStream) throws IOException {
        Reader reader = null;
        try {
            reader = new InputStreamReader(inputStream);
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
            List<ScoreType> scoreTypes = new ArrayList<>();
            for (CSVRecord record : records) {
                ScoreType scoreType = new ScoreType();
                scoreType.setAtId(Long.parseLong(record.get("atId")));
                scoreType.setsValue(Float.parseFloat(record.get("sValue")));
                scoreTypes.add(scoreType);
            }
            insertBatch(scoreTypes);
        } finally {
            if (reader != null) {
                reader.close(); // 手动关闭资源
            }
        }
    }

    /**
     * Excel 导出数据
     */
    public void exportExcel(OutputStream outputStream) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("ScoreTypes");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("atId");
            headerRow.createCell(1).setCellValue("sValue");

            List<ScoreType> scoreTypes = scoreTypeMapper.selectByExample(new ScoreTypeExample());
            int rowNum = 1;
            for (ScoreType scoreType : scoreTypes) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(scoreType.getAtId());
                row.createCell(1).setCellValue(scoreType.getsValue());
            }
            workbook.write(outputStream);
        }
    }

    /**
     * CSV 导出数据
     */
    public void exportCSV(OutputStream outputStream) throws IOException {
        try (Writer writer = new OutputStreamWriter(outputStream);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("atId", "sValue"))) {
            List<ScoreType> scoreTypes = scoreTypeMapper.selectByExample(new ScoreTypeExample());
            for (ScoreType scoreType : scoreTypes) {
                csvPrinter.printRecord(scoreType.getAtId(), scoreType.getsValue());
            }
        }
    }

    /**
     * 根据主键查询单个 ScoreType
     */
    public ScoreType getScoreTypeById(Long id) {
        return scoreTypeMapper.selectByPrimaryKey(id);
    }

    /**
     * 更新 ScoreType 数据，带有业务逻辑验证
     */
    public int update(ScoreType scoreType) {
        // 业务逻辑验证：此处可以加入任何业务逻辑验证
        return scoreTypeMapper.updateByPrimaryKeySelective(scoreType);
    }
}

