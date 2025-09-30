package me.typeflu.calculator.data

sealed interface CalculatorAction {
    data class Digit(val value: Int) : CalculatorAction
    data class Operation(val operation: CalculatorOperation) : CalculatorAction
    data object Decimal : CalculatorAction
    data object Clear : CalculatorAction
    data object Delete : CalculatorAction
    data object Equals : CalculatorAction
    data object ToggleSign : CalculatorAction
    data object Percent : CalculatorAction
    data object MemoryClear : CalculatorAction
    data object MemoryRecall : CalculatorAction
    data object MemoryAdd : CalculatorAction
    data object MemorySubtract : CalculatorAction
}
