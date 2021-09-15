package com.Finance;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ChangePlanPayments implements Comparable<ChangePlanPayments> {

    public static SimpleDateFormat formaterDate = new SimpleDateFormat("dd.MM.yyyy");
    public static DecimalFormat formaterDecimal = new DecimalFormat("0.00");
    private Calendar changeDate;
    private double sumRepayments; // Сумма досрочного погашения, иначе -1
    private int typeRepayments; // 0 - уменьшение ссуды, 1 - уменьшение срока или -1, если нет суммы
    private double newRate; // Новая процентная ставка, иначе -1

    public ChangePlanPayments(Calendar changeDate, double sumRepayments, int typeRepayments, double newRate){
        this.changeDate = changeDate;
        this.sumRepayments = sumRepayments;
        this.typeRepayments = typeRepayments;
        this.newRate = newRate;
    }

    public boolean CheckDate(Calendar checkValue1, Calendar checkValue2){
        return (changeDate.after(checkValue1) & changeDate.before(checkValue2));
    }

    public Calendar GetChangeDate(){return changeDate;};

    public double GetSumRepayments(){return sumRepayments;};

    public int GetTypeRepayments(){return typeRepayments;};

    public double GetNewRate(){return newRate;};

    @Override
    public String toString(){
        return formaterDate.format(changeDate.getTime()) + " Сумма доср. погаш.: " + formaterDecimal.format(sumRepayments) + " Тип доср. погаш.: " + typeRepayments + " Ставка : " + formaterDecimal.format(newRate);
    }

    @Override
    public int compareTo(ChangePlanPayments changePlanPayments) {

        Calendar thisDate = this.changeDate;
        Calendar compareDate = changePlanPayments.GetChangeDate();

        if(thisDate.get(Calendar.DAY_OF_YEAR) == compareDate.get(Calendar.DAY_OF_YEAR) & thisDate.get(Calendar.YEAR) == compareDate.get(Calendar.YEAR)){
            return 0;
        } else if(thisDate.get(Calendar.DAY_OF_YEAR) < compareDate.get(Calendar.DAY_OF_YEAR) & thisDate.get(Calendar.YEAR) <= compareDate.get(Calendar.YEAR)) {
            return -1;
        } else {
            return 1;
        }

    }

    @Override
    public boolean equals(Object object) {

        if(this == object) {
            return true;
        }

        if(object == null || getClass() != object.getClass()) {
            return false;
        }

        if(compareTo((ChangePlanPayments) object) == 0){
            return true;
        }

        return false;
    }

}
