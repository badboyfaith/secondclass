package com.example.demo.Controller;

import com.example.demo.model.Feedback;
import com.example.demo.Service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    /**
     * 获取所有反馈，支持分页和排序
     */
    @GetMapping
    public List<Feedback> getAllFeedbacks(@RequestParam(defaultValue = "1") int pageNum,
                                          @RequestParam(defaultValue = "10") int pageSize,
                                          @RequestParam(defaultValue = "fId DESC") String orderBy) {
        return feedbackService.getAllFeedbacks(pageNum, pageSize, orderBy);
    }

    /**
     * 根据ID获取反馈详情
     */
    @GetMapping("/{id}")
    public Feedback getFeedbackById(@PathVariable Long id) {
        return feedbackService.getFeedbackById(id);
    }

    /**
     * 创建新的反馈
     */
    @PostMapping
    public void createFeedback(@RequestBody Feedback feedback) {
        feedbackService.createFeedback(feedback);
    }

    /**
     * 更新反馈
     */
    @PutMapping("/{id}")
    public void updateFeedback(@PathVariable Long id, @RequestBody Feedback feedback) {
        feedback.setfId(id);
        feedbackService.updateFeedback(feedback);
    }

    /**
     * 删除反馈
     */
    @DeleteMapping("/{id}")
    public void deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
    }

    /**
     * 批量删除反馈
     */
    @DeleteMapping("/batch")
    public void batchDeleteFeedbacks(@RequestBody List<Long> ids) {
        feedbackService.batchDeleteFeedbacks(ids);
    }
}
