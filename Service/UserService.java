package com.example.demo.Service;

import com.example.demo.Mapper.UserMapper;
import com.example.demo.model.User;
import com.example.demo.model.UserWithBLOBs;
import com.example.demo.model.UserExample;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 搜索用户信息并带有分页功能
     */
    public List<User> searchUsers(UserExample example, int pageNum, int pageSize) {
        example.setOrderByClause("id DESC");
        return userMapper.selectByExample(example);
    }

    /**
     * 插入单条用户信息，包含业务逻辑验证
     */
    public int createUser(UserWithBLOBs user) {
        // 在插入之前可以做一些业务逻辑验证，比如字段是否为空等
        if (user.getAuthenticationString() == null || user.getAuthenticationString().isEmpty()) {
            throw new IllegalArgumentException("Authentication string 不能为空");
        }
        return userMapper.insert(user);
    }

    /**
     * 更新用户信息，包含业务逻辑验证
     */
    public int updateUser(UserWithBLOBs user) {
        // 验证是否满足更新条件
        if (user.getAuthenticationString() == null || user.getAuthenticationString().isEmpty()) {
            throw new IllegalArgumentException("Authentication string 不能为空");
        }
        return userMapper.updateByPrimaryKey(user);
    }

    /**
     * 删除单条用户记录
     */
    public int deleteUser(String userId) {
        return userMapper.deleteByPrimaryKey(userId);
    }

    /**
     * 导入 Excel 文件并更新到数据库
     */
    public void importFromExcel(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                UserWithBLOBs user = new UserWithBLOBs();
                user.setAuthenticationString(row.getCell(0).getStringCellValue());
                user.setSslCipher(row.getCell(1).getStringCellValue().getBytes());

                // 插入或更新到数据库
                userMapper.insert(user);
            }
        }
    }

    /**
     * 导出用户信息到 CSV 文件
     */
    public void exportToCSV(OutputStream outputStream) throws IOException {
        List<UserWithBLOBs> users = userMapper.selectByExampleWithBLOBs(new UserExample());
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("authenticationString", "sslCipher"))) {

            for (UserWithBLOBs user : users) {
                csvPrinter.printRecord(user.getAuthenticationString(), new String(user.getSslCipher()));
            }
            csvPrinter.flush();
        }
    }
}
