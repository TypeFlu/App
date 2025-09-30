package me.typeflu.calculator.data

import java.math.BigDecimal

data class CalculatorState(
    val display: String = "0",
    val expression: String = "",
    val pendingOperation: CalculatorOperation? = null,
    val storedValue: BigDecimal? = null,
    val memory: BigDecimal? = null,
    val lastOperand: BigDecimal? = null,
    val lastOperation: CalculatorOperation? = null,
    val justEvaluated: Boolean = false,
    val isError: Boolean = false
) {
    val hasMemory: Boolean get() = memory != null
}
