package me.typeflu.calculator.data

import androidx.lifecycle.ViewModel
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CalculatorViewModel : ViewModel() {
    private val mathContext = MathContext(16, RoundingMode.HALF_EVEN)
    private val decimalFormat = DecimalFormat("#,##0.############", DecimalFormatSymbols(Locale.getDefault())).apply {
        isGroupingUsed = true
        maximumFractionDigits = 10
    }
    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Digit -> appendDigit(action.value)
            is CalculatorAction.Operation -> applyOperation(action.operation)
            CalculatorAction.Decimal -> appendDecimal()
            CalculatorAction.Clear -> resetAll()
            CalculatorAction.Delete -> backspace()
            CalculatorAction.Equals -> evaluate()
            CalculatorAction.ToggleSign -> toggleSign()
            CalculatorAction.Percent -> applyPercent()
            CalculatorAction.MemoryClear -> updateMemory(null)
            CalculatorAction.MemoryRecall -> recallMemory()
            CalculatorAction.MemoryAdd -> adjustMemory(true)
            CalculatorAction.MemorySubtract -> adjustMemory(false)
        }
    }

    private fun appendDigit(digit: Int) {
        if (digit !in 0..9) return
        _state.update { current ->
            if (current.isError) return@update CalculatorState(display = digit.toString())
            val base = when {
                current.justEvaluated -> ""
                current.display == "0" && digit == 0 -> return@update current
                current.display == "0" -> ""
                current.display == "-0" -> "-"
                else -> current.display
            }
            val candidate = base + digit
            if (candidate.count { it.isDigit() } > MAX_DIGITS) return@update current
            current.copy(
                display = candidate,
                storedValue = if (current.justEvaluated && current.pendingOperation == null) null else current.storedValue,
                expression = if (current.justEvaluated && current.pendingOperation == null) "" else current.expression,
                justEvaluated = false,
                isError = false,
                lastOperation = if (current.pendingOperation == null) null else current.lastOperation
            )
        }
    }

    private fun appendDecimal() {
        _state.update { current ->
            if (current.isError) return@update CalculatorState(display = "0.")
            val target = when {
                current.justEvaluated -> "0"
                current.display.isEmpty() -> "0"
                current.display == "-0" -> "-0"
                else -> current.display
            }
            if (target.contains('.')) return@update current.copy(justEvaluated = false)
            val next = if (target == "-0") "-0." else "$target."
            current.copy(
                display = next,
                storedValue = if (current.justEvaluated && current.pendingOperation == null) null else current.storedValue,
                expression = if (current.justEvaluated && current.pendingOperation == null) "" else current.expression,
                justEvaluated = false,
                isError = false
            )
        }
    }

    private fun resetAll() {
        _state.value = CalculatorState()
    }

    private fun backspace() {
        _state.update { current ->
            if (current.isError) return@update CalculatorState()
            if (current.justEvaluated) return@update current.copy(display = "0", justEvaluated = false)
            val trimmed = current.display.dropLast(1)
            val normalized = when {
                trimmed.isEmpty() -> "0"
                trimmed == "-" -> "0"
                trimmed == "-0" -> "0"
                else -> trimmed
            }
            current.copy(display = normalized)
        }
    }

    private fun toggleSign() {
        _state.update { current ->
            if (current.isError) return@update current
            val toggled = if (current.display.startsWith('-')) current.display.removePrefix("-") else "-${current.display}"
            val normalized = if (toggled == "-") "0" else toggled
            current.copy(display = normalized, justEvaluated = false)
        }
    }

    private fun applyPercent() {
        _state.update { current ->
            if (current.isError) return@update current
            val input = parseDisplay(current.display)
            val reference = if (current.pendingOperation != null && current.storedValue != null) current.storedValue else BigDecimal.ONE
            val percent = reference.multiply(input, mathContext).divide(BigDecimal(100), mathContext)
            current.copy(display = percent.toPlainStringNormalized(), justEvaluated = false)
        }
    }

    private fun applyOperation(operation: CalculatorOperation) {
        _state.update { current ->
            if (current.isError) return@update CalculatorState(pendingOperation = operation, storedValue = BigDecimal.ZERO, expression = buildExpression(BigDecimal.ZERO, operation), justEvaluated = true)
            val inputValue = parseDisplay(current.display)
            when {
                current.pendingOperation == null -> current.copy(
                    storedValue = inputValue,
                    pendingOperation = operation,
                    expression = buildExpression(inputValue, operation),
                    justEvaluated = true,
                    lastOperand = null,
                    lastOperation = null
                )
                current.justEvaluated -> current.copy(
                    pendingOperation = operation,
                    expression = buildExpression(current.storedValue ?: inputValue, operation)
                )
                else -> {
                    val result = performOperation(current.storedValue, inputValue, current.pendingOperation)
                    if (result == null) CalculatorState(display = "Error", isError = true)
                    else current.copy(
                        storedValue = result,
                        display = result.toPlainStringNormalized(),
                        pendingOperation = operation,
                        expression = buildExpression(result, operation),
                        justEvaluated = true,
                        lastOperand = null,
                        lastOperation = null
                    )
                }
            }
        }
    }

    private fun evaluate() {
        val snapshot = _state.value
        if (snapshot.isError) {
            resetAll()
            return
        }
        when {
            snapshot.pendingOperation != null && snapshot.storedValue != null -> commitEvaluation(snapshot.storedValue, snapshot.pendingOperation, parseDisplay(snapshot.display))
            snapshot.lastOperation != null && snapshot.lastOperand != null -> commitEvaluation(parseDisplay(snapshot.display), snapshot.lastOperation, snapshot.lastOperand)
        }
    }

    private fun commitEvaluation(left: BigDecimal, operation: CalculatorOperation, operand: BigDecimal) {
        val result = performOperation(left, operand, operation)
        if (result == null) {
            _state.value = CalculatorState(display = "Error", isError = true)
        } else {
            _state.update { current ->
                current.copy(
                    display = result.toPlainStringNormalized(),
                    expression = listOf(formatForExpression(left), operation.symbol, formatForExpression(operand), "=").joinToString(" "),
                    storedValue = result,
                    pendingOperation = null,
                    lastOperand = operand,
                    lastOperation = operation,
                    justEvaluated = true,
                    isError = false
                )
            }
        }
    }

    private fun recallMemory() {
        _state.update { current ->
            val memoryValue = current.memory ?: return@update current
            current.copy(display = memoryValue.toPlainStringNormalized(), justEvaluated = false, isError = false)
        }
    }

    private fun adjustMemory(addition: Boolean) {
        _state.update { current ->
            val base = current.memory ?: BigDecimal.ZERO
            val delta = parseDisplay(current.display)
            val next = if (addition) base.add(delta, mathContext) else base.subtract(delta, mathContext)
            current.copy(memory = next)
        }
    }

    private fun updateMemory(value: BigDecimal?) {
        _state.update { current -> current.copy(memory = value) }
    }

    private fun performOperation(left: BigDecimal?, right: BigDecimal, operation: CalculatorOperation?): BigDecimal? {
        if (left == null || operation == null) return right
        return when (operation) {
            CalculatorOperation.Add -> left.add(right, mathContext)
            CalculatorOperation.Subtract -> left.subtract(right, mathContext)
            CalculatorOperation.Multiply -> left.multiply(right, mathContext)
            CalculatorOperation.Divide -> if (right.compareTo(BigDecimal.ZERO) == 0) null else left.divide(right, mathContext)
        }
    }

    private fun parseDisplay(value: String): BigDecimal {
        return value.replace(",", "").replace(" ", "").toBigDecimalOrNull() ?: BigDecimal.ZERO
    }

    private fun buildExpression(value: BigDecimal, operation: CalculatorOperation): String {
        return formatForExpression(value) + " " + operation.symbol
    }

    private fun formatForExpression(value: BigDecimal): String {
        val stripped = value.stripTrailingZeros()
        return decimalFormat.format(stripped)
    }

    private fun BigDecimal.toPlainStringNormalized(): String {
        val plain = stripTrailingZeros().toPlainString()
        return if (plain == "-0") "0" else plain
    }

    companion object {
        private const val MAX_DIGITS = 15
    }
}
