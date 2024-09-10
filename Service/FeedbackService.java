package com.example.demo.Service;

import com.example.demo.Mapper.FeedbackMapper;
import com.example.demo.model.Feedback;
import com.example.demo.model.FeedbackExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackMapper feedbackMapper;

    /**
     * 获取所有反馈，支持分页和排序
     */
    public List<Feedback> getAllFeedbacks(int pageNum, int pageSize, String orderBy) {
        FeedbackExample example = new FeedbackExample();
        example.setOrderByClause(orderBy);
        example.setDistinct(true);
        // 可以结合分页工具（如PageHelper）处理分页
        return feedbackMapper.selectByExample(example);
    }

    /**
     * 根据ID获取反馈详情
     */
    public Feedback getFeedbackById(Long fId) {
        return feedbackMapper.selectByPrimaryKey(fId);
    }

    /**
     * 创建反馈记录并进行业务逻辑验证
     */
    @Transactional
    public void createFeedback(Feedback feedback) {
        // 业务逻辑验证
        if (feedback.getuId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (feedback.getContent() == null || feedback.getContent().isEmpty()) {
            throw new IllegalArgumentException("反馈内容不能为空");
        }
        feedbackMapper.insertSelective(feedback);
    }

    /**
     * 更新反馈记录并进行业务逻辑验证
     */
    @Transactional
    public void updateFeedback(Feedback feedback) {
        // 业务逻辑验证
        if (feedback.getfId() == null) {
            throw new IllegalArgumentException("反馈ID不能为空");
        }
        feedbackMapper.updateByPrimaryKeySelective(feedback);
    }

    /**
     * 删除反馈记录
     */
    @Transactional
    public void deleteFeedback(Long fId) {
        feedbackMapper.deleteByPrimaryKey(fId);
    }

    /**
     * 批量删除反馈记录
     */
    @Transactional
    public void batchDeleteFeedbacks(List<Long> ids) {
        FeedbackExample example = new FeedbackExample();
        example.createCriteria().andFIdIn(ids);
        feedbackMapper.deleteByExample(example);
    }
}

