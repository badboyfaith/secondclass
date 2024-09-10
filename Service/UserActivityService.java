package com.example.demo.Service;

import com.example.demo.Mapper.UserActivityMapper;
import com.example.demo.model.UserActivity;
import com.example.demo.model.UserActivityExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.io.*;
import java.util.List;
import java.util.Date;
@Service
public class UserActivityService {

    @Autowired
    private UserActivityMapper userActivityMapper;

    /**
     * 获取所有用户活动记录
     */
    public List<UserActivity> getAllUserActivities() {
        UserActivityExample example = new UserActivityExample();
        return userActivityMapper.selectByExample(example);
    }

    /**
     * 通过ID获取单个用户活动记录
     */
    public UserActivity getUserActivityById(Long uaId) {
        return userActivityMapper.selectByPrimaryKey(uaId);
    }

    /**
     * 创建新的用户活动记录
     */
    public void createUserActivity(UserActivity userActivity) {
        userActivityMapper.insertSelective(userActivity);
    }

    /**
     * 更新用户活动记录
     */
    public void updateUserActivity(UserActivity userActivity) {
        userActivityMapper.updateByPrimaryKeySelective(userActivity);
    }

    /**
     * 删除用户活动记录
     */
    public void deleteUserActivity(Long uaId) {
        userActivityMapper.deleteByPrimaryKey(uaId);
    }

    /**
     * 批量插入用户活动记录
     */
    public void insertBatch(List<UserActivity> userActivities) {
        for (UserActivity userActivity : userActivities) {
            userActivityMapper.insertSelective(userActivity);
        }
    }

    /**
     * CSV 导入用户活动记录并更新数据库
     */
    public void importCSV(InputStream inputStream) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            List<UserActivity> userActivities = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                UserActivity userActivity = new UserActivity();
                userActivity.setaId(Long.parseLong(columns[0]));
                userActivity.setTime(new Date(Long.parseLong(columns[1])));
                userActivities.add(userActivity);
            }
            insertBatch(userActivities);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * 导出用户活动记录到 CSV 文件
     */
    public void exportCSV(OutputStream outputStream) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        List<UserActivity> userActivities = getAllUserActivities();
        for (UserActivity userActivity : userActivities) {
            String line = userActivity.getaId() + "," + userActivity.getTime().getTime();
            writer.write(line);
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }
}
