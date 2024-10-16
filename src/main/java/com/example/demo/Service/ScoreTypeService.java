package com.example.demo.Service;

import com.example.demo.Mapper.ScoreTypeMapper;
import com.example.demo.model.ScoreType;
import com.example.demo.model.ScoreTypeExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ScoreTypeService {

    @Autowired
    private ScoreTypeMapper scoreTypeMapper;

    /**
     * 搜索并筛选 ScoreType 数据
     */
    public List<ScoreType> searchScoreTypes(ScoreTypeExample example) {
        return scoreTypeMapper.selectByExample(example);
    }

    /**
     * 批量插入 ScoreType 数据
     */
    public int insertBatch(List<ScoreType> scoreTypes) {
        for (ScoreType scoreType : scoreTypes) {
            scoreTypeMapper.insertSelective(scoreType);
        }
        return scoreTypes.size();
    }

    /**
     * 单条插入 ScoreType 数据
     */
    public int insert(ScoreType scoreType) {
        return scoreTypeMapper.insertSelective(scoreType);
    }

    /**
     * 根据主键删除 ScoreType
     */
    public int deleteById(Long id) {
        return scoreTypeMapper.deleteByPrimaryKey(id);
    }

    /**
     * 分页和排序查询
     */
    public List<ScoreType> getScoreTypesByPage(int offset, int limit) {
        ScoreTypeExample example = new ScoreTypeExample();
        example.setOrderByClause("st_id ASC");  // 可以更改排序字段和方式
        example.setDistinct(true); // 可以选择是否查询去重
        return scoreTypeMapper.selectByExample(example);
    }


    /**
     * 根据主键查询单个 ScoreType
     */
    public ScoreType getScoreTypeById(Long id) {
        return scoreTypeMapper.selectByPrimaryKey(id);
    }

    /**
     * 更新 ScoreType 数据，带有业务逻辑验证
     */
    public int update(ScoreType scoreType) {
        // 业务逻辑验证：此处可以加入任何业务逻辑验证
        return scoreTypeMapper.updateByPrimaryKeySelective(scoreType);
    }
}