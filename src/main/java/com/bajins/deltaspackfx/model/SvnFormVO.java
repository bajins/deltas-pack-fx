package com.bajins.deltaspackfx.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.Serializable;
import java.util.Set;

public class SvnFormVO extends BaseFormVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String svnUrl;
    private String svnUser;
    private String svnPwd;
    private Set<String> developer;
    private Set<Long> svnVersion;

    public String getSvnUrl() {
        return svnUrl;
    }

    public void setSvnUrl(String svnUrl) {
        this.svnUrl = svnUrl;
    }

    public String getSvnUser() {
        return svnUser;
    }

    public void setSvnUser(String svnUser) {
        this.svnUser = svnUser;
    }

    public String getSvnPwd() {
        return svnPwd;
    }

    public void setSvnPwd(String svnPwd) {
        this.svnPwd = svnPwd;
    }

    public Set<String> getDeveloper() {
        return developer;
    }

    public void setDeveloper(Set<String> developer) {
        this.developer = developer;
    }

    public Set<Long> getSvnVersion() {
        return svnVersion;
    }

    public void setSvnVersion(Set<Long> svnVersion) {
        this.svnVersion = svnVersion;
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
