package com.example.demo.Mapper;

import com.example.demo.model.AddMaterials;
import com.example.demo.model.AddMaterialsExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface AddMaterialsMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table add_material
     *
     * @mbg.generated Mon Sep 09 14:55:22 CST 2024
     */
    long countByExample(AddMaterialsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table add_material
     *
     * @mbg.generated Mon Sep 09 14:55:22 CST 2024
     */
    int deleteByExample(AddMaterialsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table add_material
     *
     * @mbg.generated Mon Sep 09 14:55:22 CST 2024
     */
    int deleteByPrimaryKey(Long amId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table add_material
     *
     * @mbg.generated Mon Sep 09 14:55:22 CST 2024
     */
    int insert(AddMaterials record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table add_material
     *
     * @mbg.generated Mon Sep 09 14:55:22 CST 2024
     */
    int insertSelective(AddMaterials record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table add_material
     *
     * @mbg.generated Mon Sep 09 14:55:22 CST 2024
     */
    List<AddMaterials> selectByExample(AddMaterialsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table add_material
     *
     * @mbg.generated Mon Sep 09 14:55:22 CST 2024
     */
    AddMaterials selectByPrimaryKey(Long amId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table add_material
     *
     * @mbg.generated Mon Sep 09 14:55:22 CST 2024
     */
    int updateByExampleSelective(@Param("record") AddMaterials record, @Param("example") AddMaterialsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table add_material
     *
     * @mbg.generated Mon Sep 09 14:55:22 CST 2024
     */
    int updateByExample(@Param("record") AddMaterials record, @Param("example") AddMaterialsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table add_material
     *
     * @mbg.generated Mon Sep 09 14:55:22 CST 2024
     */
    int updateByPrimaryKeySelective(AddMaterials record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table add_material
     *
     * @mbg.generated Mon Sep 09 14:55:22 CST 2024
     */
    int updateByPrimaryKey(AddMaterials record);
}