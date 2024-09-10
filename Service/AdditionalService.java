package com.example.demo.Service;

import com.example.demo.Mapper.AdditionalMapper;
import com.example.demo.model.Additional;
import com.example.demo.model.AdditionalExample;
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
public class AdditionalService {

    @Autowired
    private AdditionalMapper additionalMapper;

    /**
     * 创建新的附加信息记录，带有业务逻辑验证
     */
    @Transactional
    public int createAdditional(Additional additional) {
        validateAdditional(additional);  // 业务逻辑验证
        return additionalMapper.insertSelective(additional);
    }

    /**
     * 更新附加信息记录，带有业务逻辑验证
     */
    @Transactional
    public int updateAdditional(Additional additional) {
        validateAdditional(additional);  // 业务逻辑验证
        return additionalMapper.updateByPrimaryKeySelective(additional);
    }

    /**
     * 根据主键删除附加信息记录
     */
    @Transactional
    public int deleteAdditional(Long adId) {
        return additionalMapper.deleteByPrimaryKey(adId);
    }

    /**
     * 根据主键查询附加信息记录
     */
    public Additional getAdditionalById(Long adId) {
        return additionalMapper.selectByPrimaryKey(adId);
    }

    /**
     * 查询所有附加信息记录，支持分页和排序
     */
    public List<Additional> getAllAdditionals(AdditionalExample example) {
        return additionalMapper.selectByExample(example);
    }

    /**
     * 根据条件统计附加信息记录数量
     */
    public long countAdditionals(AdditionalExample example) {
        return additionalMapper.countByExample(example);
    }

    /**
     * 导出附加信息记录到CSV文件
     */
    public void exportAdditionalsToCSV(AdditionalExample example, Writer writer) throws IOException {
        List<Additional> additionals = additionalMapper.selectByExample(example);
        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("ID", "File", "IsDelete"))) {
            for (Additional additional : additionals) {
                csvPrinter.printRecord(additional.getAdId(), additional.getFile(), additional.getIsDelete());
            }
        }
    }

    /**
     * 导出附加信息记录到Excel文件
     */
    public void exportAdditionalsToExcel(AdditionalExample example, OutputStream outputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Additionals");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("File");
        header.createCell(2).setCellValue("IsDelete");

        List<Additional> additionals = additionalMapper.selectByExample(example);
        int rowIdx = 1;

        for (Additional additional : additionals) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(additional.getAdId());
            row.createCell(1).setCellValue(additional.getFile());
            row.createCell(2).setCellValue(additional.getIsDelete());
        }

        workbook.write(outputStream);
    }

    /**
     * 从Excel文件中导入附加信息记录
     */
    public void importAdditionalsFromExcel(InputStream inputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        List<Additional> additionals = new ArrayList<>();

        for (Row row : sheet) {
            Additional additional = new Additional();
            additional.setFile(row.getCell(0).getStringCellValue());
            additional.setIsDelete(row.getCell(1).getBooleanCellValue());
            additionals.add(additional);
        }

        for (Additional additional : additionals) {
            createAdditional(additional);
        }
    }

    /**
     * 业务逻辑验证
     */
    private void validateAdditional(Additional additional) {
        if (additional.getFile() == null || additional.getFile().isEmpty()) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        // 其他业务逻辑验证...
    }
}

