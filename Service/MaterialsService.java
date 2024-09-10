package com.example.demo.Service;

import com.example.demo.Mapper.MaterialsMapper;
import com.example.demo.model.Materials;
import com.example.demo.model.MaterialsExample;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class MaterialsService {

    @Autowired
    private MaterialsMapper materialsMapper;

    // 搜索与筛选，分页排序
    public List<Materials> searchMaterials(String keyword, int page, int size) {
        MaterialsExample example = new MaterialsExample();
        example.setDistinct(true);
        example.setOrderByClause("m_id DESC");
        example.createCriteria().andFileLike("%" + keyword + "%");
        return materialsMapper.selectByExample(example);
    }

    // 批量操作：删除多个材料
    public void deleteMaterials(List<Long> ids) {
        MaterialsExample example = new MaterialsExample();
        example.createCriteria().andMIdIn(ids);
        materialsMapper.deleteByExample(example);
    }

    // 单条插入材料
    public void addMaterial(Materials material) {
        // 业务逻辑验证：检查是否存在相同文件
        MaterialsExample example = new MaterialsExample();
        example.createCriteria().andFileEqualTo(material.getFile());
        long count = materialsMapper.countByExample(example);
        if (count == 0) {
            materialsMapper.insert(material);
        } else {
            throw new RuntimeException("文件已存在");
        }
    }

    // 单条更新材料，带有业务逻辑验证
    public void updateMaterial(Materials material) {
        // 验证文件是否已经存在
        MaterialsExample example = new MaterialsExample();
        example.createCriteria().andFileEqualTo(material.getFile()).andMIdNotEqualTo(material.getmId());
        long count = materialsMapper.countByExample(example);
        if (count == 0) {
            materialsMapper.updateByPrimaryKey(material);
        } else {
            throw new RuntimeException("文件名称已存在");
        }
    }

    // 查询单个材料
    public Materials getMaterialById(Long mId) {
        return materialsMapper.selectByPrimaryKey(mId);
    }

    // 获取所有材料（带分页和排序）
    public List<Materials> getAllMaterials(int page, int size, String orderBy) {
        MaterialsExample example = new MaterialsExample();
        example.setOrderByClause(orderBy);
        return materialsMapper.selectByExample(example);
    }

    // 导入Excel文件并更新表格
    public void importExcelAndUpdate(MultipartFile file) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        List<Materials> materialsList = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // 跳过标题行
            Materials material = new Materials();
            material.setFile(row.getCell(0).getStringCellValue());
            material.setIsDelete(false); // 默认设置未删除
            materialsList.add(material);
        }

        // 批量插入或更新数据
        for (Materials material : materialsList) {
            upsertMaterial(material);
        }
    }

    // 导入CSV文件并更新表格
    public void importCSVAndUpdate(MultipartFile file) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        List<Materials> materialsList = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            Materials material = new Materials();
            material.setFile(data[0]);
            material.setIsDelete(false); // 默认设置未删除
            materialsList.add(material);
        }

        // 批量插入或更新数据
        for (Materials material : materialsList) {
            upsertMaterial(material);
        }
    }

    // 插入或更新材料
    private void upsertMaterial(Materials material) {
        MaterialsExample example = new MaterialsExample();
        example.createCriteria().andFileEqualTo(material.getFile());

        List<Materials> existingMaterials = materialsMapper.selectByExample(example);

        if (existingMaterials.isEmpty()) {
            // 如果不存在该记录，则插入
            materialsMapper.insert(material);
        } else {
            // 如果已经存在该记录，则更新
            Materials existingMaterial = existingMaterials.get(0);
            existingMaterial.setFile(material.getFile());
            existingMaterial.setIsDelete(false); // 更新时确保未删除状态
            materialsMapper.updateByPrimaryKey(existingMaterial);
        }
    }

    // 导出为Excel文件
    public void exportToExcel(OutputStream outputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Materials");
        List<Materials> materialsList = materialsMapper.selectByExample(new MaterialsExample());

        // 创建表头
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("文件名称");

        // 填充数据
        int rowNum = 1;
        for (Materials material : materialsList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(material.getFile());
        }

        // 输出到指定流
        workbook.write(outputStream);
        workbook.close();
    }

    // 导出为CSV文件
    public void exportToCSV(OutputStream outputStream) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("文件名称"));

        List<Materials> materialsList = materialsMapper.selectByExample(new MaterialsExample());
        for (Materials material : materialsList) {
            csvPrinter.printRecord(material.getFile());
        }

        csvPrinter.flush();
        csvPrinter.close();
    }
}

