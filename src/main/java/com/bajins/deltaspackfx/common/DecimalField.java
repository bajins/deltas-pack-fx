package com.bajins.deltaspackfx.common;

import java.math.BigDecimal;

public class DecimalField extends AbstractNumberField {
    private Integer scale = 2;

    public DecimalField() {
        super(NumberTypeEnum.DECIMAL);
    }

    public DecimalField(final Integer scale) {
        this();
        if (scale < 0) {
            throw new NumberFormatException("Scale must great than equals to 0.");
        }
        this.scale = scale;
    }

    @Override
    public Integer decimalScale() {
        return this.scale;
    }

    @Override
    public BigDecimal getValue() {
        return getDecimalValue();
    }
}
