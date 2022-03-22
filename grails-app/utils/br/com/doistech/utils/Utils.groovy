package br.com.doistech.utils

import java.math.RoundingMode
import java.text.DecimalFormat

class Utils {

    // Utils.truncar(
    static Double truncar(Double valor) {
        println( valor )
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00")
        decimalFormat.setRoundingMode(RoundingMode.DOWN)
        return decimalFormat.format(valor).toString().toDouble()
    }
}
