package com.example.demo.model;

public class Materials {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column materials.m_id
     *
     * @mbg.generated Mon Sep 09 14:55:22 CST 2024
     */
    private Long mId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column materials.file
     *
     * @mbg.generated Mon Sep 09 14:55:22 CST 2024
     */
    private String file;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column materials.is_delete
     *
     * @mbg.generated Mon Sep 09 14:55:22 CST 2024
     */
    private Boolean isDelete;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column materials.m_id
     *
     * @return the value of materials.m_id
     *
     * @mbg.generated Mon Sep 09 14:55:22 CST 2024
     */
    public Long getmId() {
        return mId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column materials.m_id
     *
     * @param mId the value for materials.m_id
     *
     * @mbg.generated Mon Sep 09 14:55:22 CST 2024
     */
    public void setmId(Long mId) {
        this.mId = mId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column materials.file
     *
     * @return the value of materials.file
     *
     * @mbg.generated Mon Sep 09 14:55:22 CST 2024
     */
    public String getFile() {
        return file;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column materials.file
     *
     * @param file the value for materials.file
     *
     * @mbg.generated Mon Sep 09 14:55:22 CST 2024
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column materials.is_delete
     *
     * @return the value of materials.is_delete
     *
     * @mbg.generated Mon Sep 09 14:55:22 CST 2024
     */
    public Boolean getIsDelete() {
        return isDelete;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column materials.is_delete
     *
     * @param isDelete the value for materials.is_delete
     *
     * @mbg.generated Mon Sep 09 14:55:22 CST 2024
     */
    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }
}