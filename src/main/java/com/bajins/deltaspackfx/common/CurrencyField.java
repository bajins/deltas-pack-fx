package com.bajins.deltaspackfx.common;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * 货币
 */
public class CurrencyField extends AbstractNumberField {

    public CurrencyField() {
        super(NumberTypeEnum.CURRENCY);
    }

    @Override
    public Integer decimalScale() {
        Locale locale = Locale.getDefault();
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(locale);
        return formatter.getCurrency().getDefaultFractionDigits();
    }

    @Override
    public BigDecimal getValue() {
        return getCurrencyValue();
    }
}
