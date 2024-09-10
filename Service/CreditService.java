package com.example.demo.Service;

import com.example.demo.Mapper.CreditMapper;
import com.example.demo.model.Credit;
import com.example.demo.model.CreditExample;
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
public class CreditService {

    @Autowired
    private CreditMapper creditMapper;

    /**
     * 获取所有记录，支持分页和排序
     */
    public List<Credit> getAllCredits(int pageNum, int pageSize, String orderBy) {
        CreditExample example = new CreditExample();
        example.setOrderByClause(orderBy);
        example.setDistinct(true);  // 可以设置是否排重
        // 这里可以结合分页逻辑，比如结合 MyBatis 的 PageHelper 进行分页
        return creditMapper.selectByExample(example);
    }

    /**
     * 根据 ID 查询单条记录
     */
    public Credit getCreditById(Long cId) {
        return creditMapper.selectByPrimaryKey(cId);
    }

    /**
     * 创建记录并进行业务逻辑验证
     */
    @Transactional
    public void createCredit(Credit credit) {
        // 业务逻辑验证
        if (credit.getAtId() == null) {
            throw new IllegalArgumentException("AtId不能为空");
        }
        creditMapper.insertSelective(credit);
    }

    /**
     * 更新记录并进行业务逻辑验证
     */
    @Transactional
    public void updateCredit(Credit credit) {
        // 业务逻辑验证
        if (credit.getcId() == null) {
            throw new IllegalArgumentException("cId不能为空");
        }
        creditMapper.updateByPrimaryKeySelective(credit);
    }

    /**
     * 删除记录
     */
    @Transactional
    public void deleteCredit(Long cId) {
        creditMapper.deleteByPrimaryKey(cId);
    }

    /**
     * 批量删除记录
     */
    @Transactional
    public void batchDeleteCredits(List<Long> ids) {
        CreditExample example = new CreditExample();
        example.createCriteria().andCIdIn(ids);
        creditMapper.deleteByExample(example);
    }

    /**
     * 导入 Excel 文件并插入/更新数据
     */
    @Transactional
    public void importCredits(MultipartFile file) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;  // 跳过表头
                Long cId = (long) row.getCell(0).getNumericCellValue();
                Long atId = (long) row.getCell(1).getNumericCellValue();
                Integer politics = (int) row.getCell(2).getNumericCellValue();
                Integer practice = (int) row.getCell(3).getNumericCellValue();
                Integer society = (int) row.getCell(4).getNumericCellValue();
                Integer innovate = (int) row.getCell(5).getNumericCellValue();
                Integer sports = (int) row.getCell(6).getNumericCellValue();
                Integer work = (int) row.getCell(7).getNumericCellValue();
                Integer skill = (int) row.getCell(8).getNumericCellValue();
                Integer other = (int) row.getCell(9).getNumericCellValue();
                Boolean isDelete = row.getCell(10).getBooleanCellValue();

                Credit credit = new Credit();
                credit.setcId(cId);
                credit.setAtId(atId);
                credit.setPolitics(politics);
                credit.setPractice(practice);
                credit.setSociety(society);
                credit.setInnovate(innovate);
                credit.setSports(sports);
                credit.setWork(work);
                credit.setSkill(skill);
                credit.setOther(other);
                credit.setIsDelete(isDelete);

                // 更新或插入
                Credit existingCredit = creditMapper.selectByPrimaryKey(cId);
                if (existingCredit != null) {
                    creditMapper.updateByPrimaryKeySelective(credit);
                } else {
                    creditMapper.insert(credit);
                }
            }
        }
    }

    /**
     * 导入 CSV 文件并插入/更新数据
     */
    @Transactional
    public void importCreditsFromCsv(MultipartFile file) throws IOException {
        try (Reader reader = new InputStreamReader(file.getInputStream())) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
            for (CSVRecord record : records) {
                Long cId = Long.parseLong(record.get("cId"));
                Long atId = Long.parseLong(record.get("atId"));
                Integer politics = Integer.parseInt(record.get("politics"));
                Integer practice = Integer.parseInt(record.get("practice"));
                Integer society = Integer.parseInt(record.get("society"));
                Integer innovate = Integer.parseInt(record.get("innovate"));
                Integer sports = Integer.parseInt(record.get("sports"));
                Integer work = Integer.parseInt(record.get("work"));
                Integer skill = Integer.parseInt(record.get("skill"));
                Integer other = Integer.parseInt(record.get("other"));
                Boolean isDelete = Boolean.parseBoolean(record.get("isDelete"));

                Credit credit = new Credit();
                credit.setcId(cId);
                credit.setAtId(atId);
                credit.setPolitics(politics);
                credit.setPractice(practice);
                credit.setSociety(society);
                credit.setInnovate(innovate);
                credit.setSports(sports);
                credit.setWork(work);
                credit.setSkill(skill);
                credit.setOther(other);
                credit.setIsDelete(isDelete);

                // 更新或插入
                Credit existingCredit = creditMapper.selectByPrimaryKey(cId);
                if (existingCredit != null) {
                    creditMapper.updateByPrimaryKeySelective(credit);
                } else {
                    creditMapper.insert(credit);
                }
            }
        }
    }

    /**
     * 导出数据到 Excel 文件
     */
    public File exportCreditsToExcel() throws IOException {
        List<Credit> creditsList = creditMapper.selectByExample(new CreditExample());

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Credits");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("cId");
        headerRow.createCell(1).setCellValue("atId");
        headerRow.createCell(2).setCellValue("politics");
        headerRow.createCell(3).setCellValue("practice");
        headerRow.createCell(4).setCellValue("society");
        headerRow.createCell(5).setCellValue("innovate");
        headerRow.createCell(6).setCellValue("sports");
        headerRow.createCell(7).setCellValue("work");
        headerRow.createCell(8).setCellValue("skill");
        headerRow.createCell(9).setCellValue("other");
        headerRow.createCell(10).setCellValue("isDelete");

        int rowNum = 1;
        for (Credit credit : creditsList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(credit.getcId());
            row.createCell(1).setCellValue(credit.getAtId());
            row.createCell(2).setCellValue(credit.getPolitics());
            row.createCell(3).setCellValue(credit.getPractice());
            row.createCell(4).setCellValue(credit.getSociety());
            row.createCell(5).setCellValue(credit.getInnovate());
            row.createCell(6).setCellValue(credit.getSports());
            row.createCell(7).setCellValue(credit.getWork());
            row.createCell(8).setCellValue(credit.getSkill());
            row.createCell(9).setCellValue(credit.getOther());
            row.createCell(10).setCellValue(credit.getIsDelete());
        }

        File file = new File("credits_export.xlsx");
        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        }
        workbook.close();
        return file;
    }

    /**
     * 导出数据到 CSV 文件
     */
    public File exportCreditsToCsv() throws IOException {
        List<Credit> creditsList = creditMapper.selectByExample(new CreditExample());

        File file = new File("credits_export.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("cId", "atId", "politics", "practice", "society", "innovate", "sports", "work", "skill", "other", "isDelete"))) {
            for (Credit credit : creditsList) {
                csvPrinter.printRecord(credit.getcId(), credit.getAtId(), credit.getPolitics(), credit.getPractice(), credit.getSociety(), credit.getInnovate(), credit.getSports(), credit.getWork(), credit.getSkill(), credit.getOther(), credit.getIsDelete());
            }
        }
        return file;
    }
}

