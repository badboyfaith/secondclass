package com.example.demo.Service;


import com.example.demo.Mapper.ScoreMapper;
import com.example.demo.model.Score;
import com.example.demo.model.ScoreExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


@Service
public class ScoreService {

    @Autowired
    private ScoreMapper scoreMapper;

    /**
     * 创建成绩记录，带业务逻辑验证
     */
    public void createScore(Score score) {
        validateScore(score);
        scoreMapper.insert(score);
    }

    /**
     * 更新成绩记录，带业务逻辑验证
     */
    public void updateScore(Score score) {
        validateScore(score);
        scoreMapper.updateByPrimaryKeySelective(score);
    }

    /**
     * 删除单条成绩记录
     */
    public void deleteScoreById(Long id) {
        scoreMapper.deleteByPrimaryKey(id);
    }

    /**
     * 获取所有成绩记录
     */
    public List<Score> getAllScores() {
        return scoreMapper.selectByExample(new ScoreExample());
    }

    /**
     * 查询单个成绩记录
     */
    public Score getScoreById(Long id) {
        return scoreMapper.selectByPrimaryKey(id);
    }

    /**
     * 分页和排序查询成绩记录
     */
    public List<Score> getScoresWithPagination(int pageNum, int pageSize, String sortBy, boolean ascending) {
        ScoreExample example = new ScoreExample();
        example.setOrderByClause(sortBy + (ascending ? " ASC" : " DESC"));
        return scoreMapper.selectByExample(example);
    }

    /**
     * 验证成绩记录的合法性
     */
    private void validateScore(Score score) {
        if (score.getAtId() == null) {
            throw new IllegalArgumentException("成绩关联的 at_id 不能为空");
        }
        if (score.getPolitics() == null || score.getPractice() == null || score.getSociety() == null) {
            throw new IllegalArgumentException("成绩的主要项不能为 null");
        }
    }
    /**
     * 导入 Excel 文件并插入或更新数据库中的记录
     *
     * @param file Excel 文件
     * @throws IOException 文件处理异常
     */
    public void importExcel(MultipartFile file) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);  // 获取第一张表
        List<Score> scores = new ArrayList<>();

        for (Row row : sheet) {
            if (row.getRowNum() == 0) { // 跳过表头
                continue;
            }
            Score score = new Score();
            score.setAtId((long) row.getCell(0).getNumericCellValue());
            score.setPolitics((float) row.getCell(1).getNumericCellValue());
            score.setPractice((float) row.getCell(2).getNumericCellValue());
            score.setSociety((float) row.getCell(3).getNumericCellValue());
            score.setInnovate((float) row.getCell(4).getNumericCellValue());
            score.setSports((float) row.getCell(5).getNumericCellValue());
            score.setWork((float) row.getCell(6).getNumericCellValue());
            score.setSkill((float) row.getCell(7).getNumericCellValue());
            score.setOther((float) row.getCell(8).getNumericCellValue());
            scores.add(score);
        }
        scores.forEach(scoreMapper::insert);  // 插入数据库
        workbook.close();
    }

    /**
     * 导入 CSV 文件并插入或更新数据库中的记录
     *
     * @param file CSV 文件
     * @throws IOException 文件处理异常
     */
    public void importCSV(MultipartFile file) throws IOException {
        Reader reader = new InputStreamReader(file.getInputStream());
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
        List<Score> scores = new ArrayList<>();

        for (CSVRecord record : csvParser) {
            Score score = new Score();
            score.setAtId(Long.parseLong(record.get("atId")));
            score.setPolitics(Float.parseFloat(record.get("politics")));
            score.setPractice(Float.parseFloat(record.get("practice")));
            score.setSociety(Float.parseFloat(record.get("society")));
            score.setInnovate(Float.parseFloat(record.get("innovate")));
            score.setSports(Float.parseFloat(record.get("sports")));
            score.setWork(Float.parseFloat(record.get("work")));
            score.setSkill(Float.parseFloat(record.get("skill")));
            score.setOther(Float.parseFloat(record.get("other")));
            scores.add(score);
        }
        scores.forEach(scoreMapper::insert);  // 插入数据库
        csvParser.close();
    }

    /**
     * 导出为 Excel 文件
     *
     * @param response HTTP 响应，返回 Excel 文件
     * @throws IOException 文件处理异常
     */
    public void exportExcel(HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Scores");

        // 创建表头
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("AtId");
        header.createCell(1).setCellValue("Politics");
        header.createCell(2).setCellValue("Practice");
        header.createCell(3).setCellValue("Society");
        header.createCell(4).setCellValue("Innovate");
        header.createCell(5).setCellValue("Sports");
        header.createCell(6).setCellValue("Work");
        header.createCell(7).setCellValue("Skill");
        header.createCell(8).setCellValue("Other");

        List<Score> scores = scoreMapper.selectByExample(null);
        int rowNum = 1;
        for (Score score : scores) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(score.getAtId());
            row.createCell(1).setCellValue(score.getPolitics());
            row.createCell(2).setCellValue(score.getPractice());
            row.createCell(3).setCellValue(score.getSociety());
            row.createCell(4).setCellValue(score.getInnovate());
            row.createCell(5).setCellValue(score.getSports());
            row.createCell(6).setCellValue(score.getWork());
            row.createCell(7).setCellValue(score.getSkill());
            row.createCell(8).setCellValue(score.getOther());
        }

        // 设置响应头，下载文件
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=scores.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /**
     * 导出为 CSV 文件
     *
     * @param response HTTP 响应，返回 CSV 文件
     * @throws IOException 文件处理异常
     */
    public void exportCSV(HttpServletResponse response) throws IOException {
        List<Score> scores = scoreMapper.selectByExample(null);
        Writer writer = response.getWriter();
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("AtId", "Politics", "Practice", "Society", "Innovate", "Sports", "Work", "Skill", "Other"));

        for (Score score : scores) {
            csvPrinter.printRecord(
                    score.getAtId(),
                    score.getPolitics(),
                    score.getPractice(),
                    score.getSociety(),
                    score.getInnovate(),
                    score.getSports(),
                    score.getWork(),
                    score.getSkill(),
                    score.getOther()
            );
        }

        // 设置响应头，下载文件
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=scores.csv");
        csvPrinter.flush();
        csvPrinter.close();
    }
}

