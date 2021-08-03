package com.bajins.deltaspackfx.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.Serializable;

public class BaseFormVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String projectPath;
    private String sourcePath;
    private String targetPath;
    private String classesPath;
    private String outPath;
    private Long startTime;
    private Long endTime;

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public String getClassesPath() {
        return classesPath;
    }

    public void setClassesPath(String classesPath) {
        this.classesPath = classesPath;
    }

    public String getOutPath() {
        return outPath;
    }

    public void setOutPath(String outPath) {
        this.outPath = outPath;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object obj) {
        /*CommonsLang3 rhs = (CommonsLang3) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(field1, rhs.field1)
                .append(field2, rhs.field2)
                .append(field3, rhs.field3)
                .isEquals();*/
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        // return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        // 如果需要排除某些字段， 可以使用ReflectionToStringBuilder.toStringExclude方法
        return ReflectionToStringBuilder.toString(this);
    }
}
