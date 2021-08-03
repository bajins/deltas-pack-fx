package com.bajins.deltaspackfx.common;

public class IntegerField extends AbstractNumberField {

    public IntegerField() {
        super(NumberTypeEnum.INTEGER);
    }

    @Override
    public Integer decimalScale() {
        return Integer.valueOf(0);
    }

    @Override
    public Integer getValue() {
        return getIntegerValue();
    }
}
