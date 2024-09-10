package com.example.demo.Service;

import com.example.demo.Mapper.PermissionMapper;
import com.example.demo.model.Permission;
import com.example.demo.model.PermissionExample;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    /**
     * 导入Excel并更新表格
     */
    @PostMapping("/import/excel")
    public void importExcelAndUpdate(MultipartFile file) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        List<Permission> permissions = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // 跳过表头
            Permission permission = new Permission();
            permission.setAuthority((int) row.getCell(0).getNumericCellValue());
            permission.setDescribes(row.getCell(1).getStringCellValue());
            permissions.add(permission);
        }

        // 插入或更新权限
        for (Permission permission : permissions) {
            upsertPermission(permission);
        }
    }

    /**
     * 导入CSV并更新表格
     */
    public void importCSVAndUpdate(MultipartFile file) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        List<Permission> permissions = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            Permission permission = new Permission();
            permission.setAuthority(Integer.parseInt(data[0]));
            permission.setDescribes(data[1]);
            permissions.add(permission);
        }

        // 插入或更新权限
        for (Permission permission : permissions) {
            upsertPermission(permission);
        }
    }

    /**
     * 插入或更新权限
     */
    private void upsertPermission(Permission permission) {
        PermissionExample example = new PermissionExample();
        example.createCriteria().andAuthorityEqualTo(permission.getAuthority());

        List<Permission> existingPermissions = permissionMapper.selectByExample(example);

        if (existingPermissions.isEmpty()) {
            // 如果不存在记录则插入
            permissionMapper.insert(permission);
        } else {
            // 如果存在则更新
            Permission existingPermission = existingPermissions.get(0);
            existingPermission.setDescribes(permission.getDescribes());
            permissionMapper.updateByPrimaryKey(existingPermission);
        }
    }

    /**
     * 导出为Excel
     */
    public void exportToExcel(OutputStream outputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Permissions");
        List<Permission> permissions = permissionMapper.selectByExample(new PermissionExample());

        // 创建表头
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Authority");
        header.createCell(1).setCellValue("Describes");

        // 填充数据
        int rowNum = 1;
        for (Permission permission : permissions) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(permission.getAuthority());
            row.createCell(1).setCellValue(permission.getDescribes());
        }

        // 写入到输出流
        workbook.write(outputStream);
        workbook.close();
    }

    /**
     * 导出为CSV
     */
    public void exportToCSV(OutputStream outputStream) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Authority", "Describes"));

        List<Permission> permissions = permissionMapper.selectByExample(new PermissionExample());
        for (Permission permission : permissions) {
            csvPrinter.printRecord(permission.getAuthority(), permission.getDescribes());
        }

        csvPrinter.flush();
        csvPrinter.close();
    }
    /**
     * 创建权限，带业务逻辑验证
     */
    public void createPermission(Permission permission) {
        validatePermission(permission);
        permissionMapper.insert(permission);
    }

    /**
     * 更新权限，带业务逻辑验证
     */
    public void updatePermission(Permission permission) {
        validatePermission(permission);
        permissionMapper.updateByPrimaryKeySelective(permission);
    }

    /**
     * 删除单条权限
     */
    public void deletePermissionById(Integer id) {
        permissionMapper.deleteByPrimaryKey(id);
    }

    /**
     * 获取所有权限
     */
    public List<Permission> getAllPermissions() {
        return permissionMapper.selectByExample(new PermissionExample());
    }

    /**
     * 查询单个权限
     */
    public Permission getPermissionById(Integer id) {
        return permissionMapper.selectByPrimaryKey(id);
    }

    /**
     * 分页和排序查询
     */
    public List<Permission> getPermissionsWithPagination(int pageNum, int pageSize, String sortBy, boolean ascending) {
        PermissionExample example = new PermissionExample();
        example.setOrderByClause(sortBy + (ascending ? " ASC" : " DESC"));
        return permissionMapper.selectByExample(example);
    }

    /**
     * 验证权限对象的合法性
     */
    private void validatePermission(Permission permission) {
        if (permission.getAuthority() == null) {
            throw new IllegalArgumentException("权限 authority 不能为空");
        }
        if (permission.getDescribes() == null || permission.getDescribes().isEmpty()) {
            throw new IllegalArgumentException("描述不能为空");
        }
    }
}
