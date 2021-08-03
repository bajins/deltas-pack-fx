package com.bajins.deltaspackfx.common;

import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * 整数过滤器：输入框只能输入数字
 * https://cmlanche.github.io/2018/07/21/%E8%81%8A%E4%B8%80%E8%81%8AJavaFx%E4%B8%AD%E7%9A%84TextFormater
 */
public class IntegerFilter implements UnaryOperator<TextFormatter.Change> {

    private final static Pattern DIGIT_PATTERN = Pattern.compile("\\d*");

    @Override
    public TextFormatter.Change apply(TextFormatter.Change change) {
        return DIGIT_PATTERN.matcher(change.getText()).matches() ? change : null;
    }
}
