package com.Finance;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;

public class DebtInfoOfMonth {

    public static SimpleDateFormat formaterDate = new SimpleDateFormat("dd.MM.yyyy");
    public static DecimalFormat formaterDecimal = new DecimalFormat("0.00");
    private Calendar paymentDate;
    private double paySum;
    private double percentSum;
    private double balanceDebt;

    public DebtInfoOfMonth(Calendar paymentDate, double paySum, double percentSum, double balanceDebt){
        this.paymentDate = paymentDate;
        this.paySum = paySum;
        this.percentSum = percentSum;
        this.balanceDebt = balanceDebt;
    }

    @Override
    public String toString(){
        return formaterDate.format(paymentDate.getTime()) + " Плановый платеж: " + formaterDecimal.format(paySum) + " Основной долг: " + formaterDecimal.format(paySum-percentSum)
                + " Проценты : " + formaterDecimal.format(percentSum) + " Остаток долга: " + formaterDecimal.format(balanceDebt);
    }

}
