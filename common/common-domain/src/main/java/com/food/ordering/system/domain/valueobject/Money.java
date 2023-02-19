package com.food.ordering.system.domain.valueobject;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public record Money(BigDecimal amount) {

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    private static final MathContext mathContext = new MathContext(2, RoundingMode.HALF_EVEN);

    public boolean isGreaterThanZero() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isGreaterThan(Money money) {
        return amount != null && amount.compareTo(money.amount()) > 0;
    }

    public Money add(Money money) {
        return new Money(this.amount.add(money.amount(), mathContext));
    }

    public Money subtract(Money money) {
        return new Money(this.amount.subtract(money.amount(), mathContext));
    }

    public Money multiply(int multiplier) {
        return new Money(this.amount.multiply(new BigDecimal(multiplier), mathContext));
    }

}
