package me.typeflu.calculator.viewmodel // Changed package name

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.DecimalFormat
import java.text.NumberFormat

class CalculatorViewModel : ViewModel() {
    private val _display = MutableStateFlow("0")
    val display: StateFlow<String> = _display.asStateFlow()

    private var operand1: String? = null
    private var operation: String? = null
    private var clearDisplayForNextDigit = true
    private var lastInputWasOperator = false

    private val numberFormatter: NumberFormat = DecimalFormat("#,###.##########")

    private fun formatDisplayString(value: String): String {
        if (value == "Error" || value.isBlank() || value == "0") return value
        if (value.endsWith(".")) {
            val numPart = value.substring(0, value.length - 1)
            if (numPart.isEmpty()) return "0."
            if (numPart == "-") return "-0."
            return try {
                val parsed = numPart.replace(",", "").toDouble()
                numberFormatter.format(parsed) + "."
            } catch (e: NumberFormatException) {
                value
            }
        }
        return try {
            val doubleVal = value.replace(",", "").toDouble()
            if (doubleVal % 1 == 0.0) {
                numberFormatter.format(doubleVal.toLong())
            } else {
                numberFormatter.format(doubleVal)
            }
        } catch (e: NumberFormatException) {
            value
        }
    }

    fun onButtonClick(label: String) {
        when (label) {
            "AC" -> {
                _display.value = "0"
                operand1 = null
                operation = null
                clearDisplayForNextDigit = true
                lastInputWasOperator = false
            }
            "±" -> {
                if (_display.value != "Error") {
                    val currentValue = _display.value
                    if (currentValue.startsWith("-")) {
                        _display.value = currentValue.substring(1)
                    } else if (currentValue != "0"){
                        _display.value = "-$currentValue"
                    }
                }
            }
            "%" -> {
                if (_display.value != "Error") {
                    try {
                        val currentValue = _display.value.replace(",", "").toDouble()
                        val result = currentValue / 100
                        _display.value = formatDisplayString(result.toString().take(12))
                    } catch (e: NumberFormatException) {
                        _display.value = "Error"
                    }
                    clearDisplayForNextDigit = true
                    lastInputWasOperator = true
                }
            }
            "÷", "×", "-", "+" -> {
                if (_display.value != "Error") {
                    val currentDisplayValue = _display.value.replace(",", "")
                    if (!lastInputWasOperator) {
                        if (operand1 == null || clearDisplayForNextDigit) {
                            operand1 = currentDisplayValue
                        } else if (operation != null) {
                            val result = performCalculation(operand1!!, currentDisplayValue, operation!!)
                            _display.value = formatDisplayString(result)
                            operand1 = if (result != "Error") result.replace(",", "") else null
                        }
                    }
                    operation = label
                    clearDisplayForNextDigit = true
                    lastInputWasOperator = true
                }
            }
            "=" -> {
                if (operand1 != null && operation != null && _display.value != "Error" && !lastInputWasOperator) {
                    val operand2 = _display.value.replace(",", "")
                    val result = performCalculation(operand1!!, operand2, operation!!)
                    _display.value = formatDisplayString(result)
                    operand1 = if (result != "Error") result.replace(",", "") else null
                    clearDisplayForNextDigit = true
                    lastInputWasOperator = true
                }
            }
            "." -> {
                val currentUnformattedDisplay = _display.value.replace(",", "")
                if (clearDisplayForNextDigit) {
                    _display.value = "0."
                    clearDisplayForNextDigit = false
                } else if (!currentUnformattedDisplay.contains(".") && _display.value != "Error") {
                    _display.value = formatDisplayString(currentUnformattedDisplay + ".")
                }
                lastInputWasOperator = false
            }
            else -> { // Digit input
                if (_display.value == "Error") return

                val currentUnformattedDisplay = _display.value.replace(",", "")
                val newDisplayValue = if (clearDisplayForNextDigit || currentUnformattedDisplay == "0") {
                    label
                } else {
                    if (currentUnformattedDisplay.length < 9) {
                        currentUnformattedDisplay + label
                    } else {
                        currentUnformattedDisplay
                    }
                }
                _display.value = formatDisplayString(newDisplayValue)
                clearDisplayForNextDigit = false
                lastInputWasOperator = false
            }
        }
    }

    private fun performCalculation(op1String: String, op2String: String, currentOperation: String): String {
        return try {
            val op1 = op1String.replace(",", "").toDouble()
            val op2 = op2String.replace(",", "").toDouble()
            val result = when (currentOperation) {
                "+" -> op1 + op2
                "-" -> op1 - op2
                "×" -> op1 * op2
                "÷" -> {
                    if (op2 == 0.0) return "Error"
                    op1 / op2
                }
                else -> return "Error"
            }
            
            val resultString = result.toString()
            if (result % 1 == 0.0 && !resultString.contains("E", ignoreCase = true)) {
                result.toLong().toString().take(15)
            } else {
                DecimalFormat("0.##########").format(result).take(15)
            }
        } catch (e: NumberFormatException) {
            "Error"
        }
    }
}
