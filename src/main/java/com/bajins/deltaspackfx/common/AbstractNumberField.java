package com.bajins.deltaspackfx.common;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * 整数、高精度浮点数、货币 数值输入框的虚拟基类，自动校验输入合法性，自动增加货币符号、千分位
 */
public abstract class AbstractNumberField extends TextField {

    private NumberTypeEnum numberType;

    private static final String DEFAULT_NUMBER_SEPARTOR_FORMAT = ",###";

    private final static DecimalFormatSymbols symbols = new DecimalFormatSymbols();

    public AbstractNumberField(NumberTypeEnum numberType) {
        super();
        this.numberType = numberType;
        setAlignment(Pos.CENTER_RIGHT);
        // 输入时的有效性检查
        addEventFilter(KeyEvent.KEY_TYPED, event -> {

            if (!isValid(getText())) {
                event.consume();
            }
        });
        // 格式化
        textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!isValid(newValue)) {
                setText(oldValue);
            }
            setText(formatValue(getFormatter()));
        });
    }

    /**
     * 格式化数值
     *
     * @param valueFormatter 格式
     * @return
     */
    private String formatValue(final String valueFormatter) {
        if ("-".equals(getText())) {
            return getText();
        }
        String currString = null;
        if (StringUtils.isNotBlank(getText())) {
            if (getText().endsWith(".") || getText().endsWith(getCurrencySymbols())) {
                return getText();
            }
            DecimalFormat numberFormatter = new DecimalFormat(valueFormatter);
            if (NumberTypeEnum.INTEGER == this.numberType) {
                Integer currValue = getIntegerValue();
                currString = numberFormatter.format(currValue);
            } else {
                BigDecimal currValue = getDecimalValue();
                currString = numberFormatter.format(currValue);
            }
        }
        return currString;
    }

    /**
     * 数值有效性检查
     *
     * @param value 带格式的字符串
     * @return
     */
    private boolean isValid(final String value) {
        if (StringUtils.isBlank(value) || value.equals("-")) {
            return true;
        }
        try {
            if (NumberTypeEnum.INTEGER == this.numberType) {
                getIntegerValue();
            } else if (NumberTypeEnum.CURRENCY == this.numberType) {
                getDecimalValue();
            } else {
                getCurrencyValue();
            }
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * 转为整型
     *
     * @return
     */
    protected Integer getIntegerValue() {
        if (StringUtils.isBlank(getText()) || "-".equals(getText())) {
            return null;
        }
        return Integer.valueOf(getText().replace(",", ""));
    }

    /**
     * 转为BigDecimal
     *
     * @return
     */
    protected BigDecimal getDecimalValue() {
        return getDecimalValue('.');
    }

    /**
     * 转为货币
     *
     * @return
     */
    protected BigDecimal getCurrencyValue() {
        return getDecimalValue(getCurrencySeparator());
    }

    private BigDecimal getDecimalValue(final char separator) {
        if (StringUtils.isBlank(getText()) || "-".equals(getText())) {
            return null;
        }
        int pos = getText().indexOf(separator);
        if (pos > -1) {
            final String subStr = getText().substring(pos + 1, getText().length());
            if (subStr.length() > decimalScale()) {
                throw new NumberFormatException("Scale error.");
            }
        }
        return new BigDecimal(getText().replace(",", "").replace(getCurrencySymbols(), ""));
    }

    /**
     * 生成用于格式化数据的字符串
     *
     * @return
     */
    protected String getFormatter() {
        if (this.numberType == null) {
            throw new RuntimeException("Type error.");
        }
        if (NumberTypeEnum.INTEGER == this.numberType) {
            return getIntegerFormatter();
        } else if (NumberTypeEnum.CURRENCY == this.numberType) {
            return getCurrencyFormatter();
        } else {
            return getDecimalFormatter();
        }
    }

    protected String getIntegerFormatter() {
        return DEFAULT_NUMBER_SEPARTOR_FORMAT;
    }

    protected String getCurrencyFormatter() {
        return String.format("%s%s%s", getCurrencySymbols(), DEFAULT_NUMBER_SEPARTOR_FORMAT, getScaleFormatter());
    }

    protected String getDecimalFormatter() {
        return String.format("%s%s", DEFAULT_NUMBER_SEPARTOR_FORMAT, getScaleFormatter());
    }

    public abstract Integer decimalScale();

    /**
     * 为BigDecimal和货币型数据生成小数占位信息，有多少有效小数位就生成多少个占位符
     *
     * @return
     */
    protected String getScaleFormatter() {
        String currFormatter = "";
        if (decimalScale() != 0) {
            if (NumberTypeEnum.CURRENCY == this.numberType) {
                currFormatter += getCurrencySeparator();
            } else {
                currFormatter += ".";
            }
            Integer tempScale = decimalScale();
            while (tempScale > 0) {
                currFormatter += "#";
                tempScale--;
            }
        }
        return currFormatter;
    }

    /**
     * 获取货币符号
     *
     * @return
     */
    protected static String getCurrencySymbols() {
        return symbols.getCurrencySymbol();
    }

    /**
     * 获取货币分隔符
     *
     * @return
     */
    protected static char getCurrencySeparator() {
        return symbols.getMonetaryDecimalSeparator();
    }

    /**
     * 虚拟方法。用于子类返回指定类型的数值
     *
     * @return
     */
    public abstract Object getValue();
}
