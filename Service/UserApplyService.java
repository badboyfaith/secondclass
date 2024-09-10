package com.example.demo.Service;

import com.example.demo.Mapper.UserApplyMapper;
import com.example.demo.model.UserApply;
import com.example.demo.model.UserApplyExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.ArrayList;
@Service
public class UserApplyService {

    @Autowired
    private UserApplyMapper userApplyMapper;

    /**
     * 获取所有用户申请信息
     * @return List<UserApply>
     */
    public List<UserApply> getAllUserApply() {
        UserApplyExample example = new UserApplyExample();
        return userApplyMapper.selectByExample(example);
    }

    /**
     * 根据主键查询用户申请
     * @param id 用户申请ID
     * @return UserApply
     */
    public UserApply getUserApplyById(Long id) {
        return userApplyMapper.selectByPrimaryKey(id);
    }

    /**
     * 创建用户申请
     * @param userApply 用户申请对象
     */
    @Transactional
    public void createUserApply(UserApply userApply) {
        // 业务逻辑验证：检查申请是否已经存在等
        userApplyMapper.insertSelective(userApply);
    }

    /**
     * 更新用户申请信息
     * @param userApply 用户申请对象
     */
    @Transactional
    public void updateUserApply(UserApply userApply) {
        // 业务逻辑验证：检查申请是否符合更新条件
        userApplyMapper.updateByPrimaryKeySelective(userApply);
    }

    /**
     * 删除用户申请
     * @param id 用户申请ID
     */
    @Transactional
    public void deleteUserApply(Long id) {
        // 逻辑删除处理
        UserApply userApply = new UserApply();
        userApply.setUapId(id);
        userApply.setIsDelete(true);
        userApplyMapper.updateByPrimaryKeySelective(userApply);
    }

    /**
     * 分页查询用户申请
     * @param page 页码
     * @param size 每页显示数量
     * @return List<UserApply>
     */
    public List<UserApply> getUserApplyByPage(int page, int size) {
        UserApplyExample example = new UserApplyExample();
        // 分页处理：在MyBatis配置文件中配置分页插件或手动分页
        example.setOrderByClause("uap_id ASC limit " + (page - 1) * size + ", " + size);
        return userApplyMapper.selectByExample(example);
    }
    // 导入CSV文件
    public void importCSV(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
            List<UserApply> userApplyList = new ArrayList<>();
            for (CSVRecord record : parser) {
                UserApply userApply = new UserApply();
                userApply.setuId(Long.parseLong(record.get("u_id")));
                userApply.setApId(Long.parseLong(record.get("ap_id")));
                userApply.setIsDelete(Boolean.parseBoolean(record.get("is_delete")));
                userApplyList.add(userApply);
            }
            for (UserApply userApply : userApplyList) {
                userApplyMapper.insert(userApply);
            }
        }
    }

    // 导入Excel文件
    public void importExcel(MultipartFile file) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<UserApply> userApplyList = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // 跳过表头
                }
                UserApply userApply = new UserApply();
                userApply.setuId((long) row.getCell(0).getNumericCellValue());
                userApply.setApId((long) row.getCell(1).getNumericCellValue());
                userApply.setIsDelete(row.getCell(2).getBooleanCellValue());
                userApplyList.add(userApply);
            }
            for (UserApply userApply : userApplyList) {
                userApplyMapper.insert(userApply);
            }
        }
    }

    // 导出CSV文件
    public void exportCSV(OutputStream outputStream) throws IOException {
        List<UserApply> userApplyList = userApplyMapper.selectByExample(null);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            CSVFormat format = CSVFormat.DEFAULT.withHeader("u_id", "ap_id", "is_delete");
            for (UserApply userApply : userApplyList) {
                writer.write(userApply.getuId() + "," + userApply.getApId() + "," + userApply.getIsDelete() + "\n");
            }
        }
    }

    // 导出Excel文件
    public void exportExcel(OutputStream outputStream) throws IOException {
        List<UserApply> userApplyList = userApplyMapper.selectByExample(null);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("UserApply");

        // 创建表头
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("u_id");
        header.createCell(1).setCellValue("ap_id");
        header.createCell(2).setCellValue("is_delete");

        // 写入数据
        int rowIndex = 1;
        for (UserApply userApply : userApplyList) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(userApply.getuId());
            row.createCell(1).setCellValue(userApply.getApId());
            row.createCell(2).setCellValue(userApply.getIsDelete());
        }

        workbook.write(outputStream);
        workbook.close();
    }
}

