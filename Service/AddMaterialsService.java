package com.example.demo.Service;

import com.example.demo.Mapper.AddMaterialsMapper;
import com.example.demo.model.AddMaterials;
import com.example.demo.model.AddMaterialsExample;
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
public class AddMaterialsService {

    @Autowired
    private AddMaterialsMapper addMaterialsMapper;

    /**
     * 获取所有记录，支持分页和排序
     */
    public List<AddMaterials> getAllMaterials(int pageNum, int pageSize, String orderBy) {
        AddMaterialsExample example = new AddMaterialsExample();
        example.setOrderByClause(orderBy);
        example.setDistinct(true);  // 可以设置是否排重
        // 在这里可以加入分页逻辑，比如结合MyBatis的PageHelper库进行分页
        return addMaterialsMapper.selectByExample(example);
    }

    /**
     * 根据ID查询单条记录
     */
    public AddMaterials getMaterialById(Long amId) {
        return addMaterialsMapper.selectByPrimaryKey(amId);
    }

    /**
     * 创建记录并进行业务逻辑验证
     */
    @Transactional
    public void createMaterial(AddMaterials material) {
        // 业务逻辑验证
        if (material.getmId() == null || material.getAdId() == null) {
            throw new IllegalArgumentException("mId和adId不能为空");
        }
        addMaterialsMapper.insertSelective(material);
    }

    /**
     * 更新记录并进行业务逻辑验证
     */
    @Transactional
    public void updateMaterial(AddMaterials material) {
        // 业务逻辑验证
        if (material.getAmId() == null) {
            throw new IllegalArgumentException("amId不能为空");
        }
        addMaterialsMapper.updateByPrimaryKeySelective(material);
    }

    /**
     * 删除记录
     */
    @Transactional
    public void deleteMaterial(Long amId) {
        addMaterialsMapper.deleteByPrimaryKey(amId);
    }

    /**
     * 批量删除记录
     */
    @Transactional
    public void batchDeleteMaterials(List<Long> ids) {
        AddMaterialsExample example = new AddMaterialsExample();
        example.createCriteria().andAmIdIn(ids);
        addMaterialsMapper.deleteByExample(example);
    }

    /**
     * 导入Excel文件并插入/更新数据
     */
    @Transactional
    public void importMaterials(MultipartFile file) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;  // 跳过表头
                Long amId = (long) row.getCell(0).getNumericCellValue();
                Long mId = (long) row.getCell(1).getNumericCellValue();
                Long adId = (long) row.getCell(2).getNumericCellValue();
                Boolean isDelete = row.getCell(3).getBooleanCellValue();

                AddMaterials material = new AddMaterials();
                material.setAmId(amId);
                material.setmId(mId);
                material.setAdId(adId);
                material.setIsDelete(isDelete);

                // 更新或插入
                AddMaterials existingMaterial = addMaterialsMapper.selectByPrimaryKey(amId);
                if (existingMaterial != null) {
                    addMaterialsMapper.updateByPrimaryKeySelective(material);
                } else {
                    addMaterialsMapper.insert(material);
                }
            }
        }
    }

    /**
     * 导入CSV文件并插入/更新数据
     */
    @Transactional
    public void importMaterialsFromCsv(MultipartFile file) throws IOException {
        try (Reader reader = new InputStreamReader(file.getInputStream())) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
            for (CSVRecord record : records) {
                Long amId = Long.parseLong(record.get("amId"));
                Long mId = Long.parseLong(record.get("mId"));
                Long adId = Long.parseLong(record.get("adId"));
                Boolean isDelete = Boolean.parseBoolean(record.get("isDelete"));

                AddMaterials material = new AddMaterials();
                material.setAmId(amId);
                material.setmId(mId);
                material.setAdId(adId);
                material.setIsDelete(isDelete);

                // 更新或插入
                AddMaterials existingMaterial = addMaterialsMapper.selectByPrimaryKey(amId);
                if (existingMaterial != null) {
                    addMaterialsMapper.updateByPrimaryKeySelective(material);
                } else {
                    addMaterialsMapper.insert(material);
                }
            }
        }
    }

    /**
     * 导出数据到Excel文件
     */
    public File exportMaterialsToExcel() throws IOException {
        List<AddMaterials> materialsList = addMaterialsMapper.selectByExample(new AddMaterialsExample());

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Materials");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("amId");
        headerRow.createCell(1).setCellValue("mId");
        headerRow.createCell(2).setCellValue("adId");
        headerRow.createCell(3).setCellValue("isDelete");

        int rowNum = 1;
        for (AddMaterials material : materialsList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(material.getAmId());
            row.createCell(1).setCellValue(material.getmId());
            row.createCell(2).setCellValue(material.getAdId());
            row.createCell(3).setCellValue(material.getIsDelete());
        }

        File file = new File("materials_export.xlsx");
        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        }
        workbook.close();
        return file;
    }

    /**
     * 导出数据到CSV文件
     */
    public File exportMaterialsToCsv() throws IOException {
        List<AddMaterials> materialsList = addMaterialsMapper.selectByExample(new AddMaterialsExample());

        File file = new File("materials_export.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("amId", "mId", "adId", "isDelete"))) {
            for (AddMaterials material : materialsList) {
                csvPrinter.printRecord(material.getAmId(), material.getmId(), material.getAdId(), material.getIsDelete());
            }
        }
        return file;
    }
}
