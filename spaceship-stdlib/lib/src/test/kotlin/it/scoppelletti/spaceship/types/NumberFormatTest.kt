@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.spaceship.types

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import kotlin.test.Test

class NumberFormatTest {

    @Test
    fun numberFormat() {
        Locale.ITALY.print()
        Locale.UK.print()
        Locale.US.print()
    }

    private fun Locale.print() {
        var fmt: NumberFormat

        println("# Locale=${this.displayName}")
        println("## NumberFormat")
        fmt = NumberFormat.getNumberInstance(this)
        fmt.print()

        println("## IntegerFormat")
        fmt = NumberFormat.getIntegerInstance(this)
        fmt.print()

        println("## PercentFormat")
        fmt = NumberFormat.getPercentInstance(this)
        fmt.print()

        println("## CurrencyFormat")
        fmt = NumberFormat.getCurrencyInstance(this)
        fmt.print()
    }

    private fun NumberFormat.print() {
        val decimalFmt = this as DecimalFormat

        println("maximumIntegerDigits=${this.maximumIntegerDigits}")
        println("minimumIntegerDigits=${this.minimumIntegerDigits}")
        print("isDecimalSeparatorAlwaysShown=")
        println(decimalFmt.isDecimalSeparatorAlwaysShown)
        println("maximumFractionDigits=${this.maximumFractionDigits}")
        println("minimumFractionDigits=${this.minimumFractionDigits}")
        println("roundingMode=${this.roundingMode}")
        println("isGroupingUsed=${this.isGroupingUsed}")
        println("groupingSize=${decimalFmt.groupingSize}")
        println("positivePrefix=${decimalFmt.positivePrefix}")
        println("positiveSuffix=${decimalFmt.positiveSuffix}")
        println("negativePrefix=${decimalFmt.negativePrefix}")
        println("negativeSuffix=${decimalFmt.negativeSuffix}")
        println("isParseIntegerOnly=${this.isParseIntegerOnly}")
        println("isParseBigDecimal=${decimalFmt.isParseBigDecimal}")
        println("multiplier=${decimalFmt.multiplier}")
        decimalFmt.decimalFormatSymbols.print()
        this.currency.print()
    }

    private fun DecimalFormatSymbols.print() {
        println("### DecimalFormatSymbols" )
        println("digit=${this.digit}")
        println("zeroDigit=${this.zeroDigit}")
        println("minusSign=${this.minusSign}")
        println("patternSeparator=${this.patternSeparator}")
        println("decimalSeparator=${this.decimalSeparator}")
        println("groupingSeparator=${this.groupingSeparator}")
        println("exponentSeparator=${this.exponentSeparator}")
        println("percent=${this.percent}")
        println("perMill=${this.perMill}")
        println("infinity=${this.infinity}")
        println("naN=${this.naN}")
        println("currencySymbol=${this.currencySymbol}")
        print("internationalCurrencySymbol=")
        println(this.internationalCurrencySymbol)
        println("monetaryDecimalSeparator=${this.monetaryDecimalSeparator}")
    }

    private fun Currency.print() {
        println("### Currency")
        println("displayName=${this.displayName}")
        println("currencyCode=${this.currencyCode}")
        println("numericCode=${this.numericCode}")
        println("symbol=${this.symbol}")
        println("defaultFractionDigits=${this.defaultFractionDigits}")
    }
}