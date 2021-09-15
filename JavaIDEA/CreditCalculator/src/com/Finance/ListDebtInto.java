package com.Finance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.lang.Exception;

public class ListDebtInto {

    private ArrayList<DebtInfoOfMonth> arrayDebtInfo = null;
    private ArrayList<ChangePlanPayments> arrayChangePlanPayments = null;
    private Calendar creditDate;
    private double creditSum;
    private double rate;
    private int userCountDuration;
    private int realCountDuration = 0;
    private double currentCreditSum;
    private double currentRate;
    private int currentCountDuration;
    private int CountChangePlanPayments = 0;
    private double annuityPay;
    private double summaryBasicSum = 0;
    private double summaryPercentSum = 0;
    private double annuityIndex;
    private double rateShare;
    private double balanceDebt;

    public ListDebtInto(Calendar creditDate, double creditSum, double rate, int userCountDuration){

        this.creditDate = creditDate;
        this.creditSum = creditSum;
        this.rate = rate;
        this.userCountDuration = userCountDuration;

        currentCreditSum = creditSum;
        currentRate = rate;
        currentCountDuration = userCountDuration;

    }

    public void AddChangePlanPayments (Calendar dateRepayments, double sumRepayments, int typeRepayments, double rate) throws Exception {

        ChangePlanPayments changePlanPayments = new ChangePlanPayments(dateRepayments, sumRepayments, typeRepayments, rate);

        if(dateRepayments.before(creditDate)){
            throw new Exception("Дата " + ChangePlanPayments.formaterDate.format(changePlanPayments.GetChangeDate().getTime()) + " не может быть задана ранее даты выдачи кредита");
        }

        if(arrayChangePlanPayments == null){
            arrayChangePlanPayments = new ArrayList<ChangePlanPayments>();
        }

        for(ChangePlanPayments tempPlanPayments : arrayChangePlanPayments){
            if(tempPlanPayments.equals(changePlanPayments)){
                throw new Exception("Дата " + ChangePlanPayments.formaterDate.format(changePlanPayments.GetChangeDate().getTime()) + " уже существует в списке");
            }

        }

        arrayChangePlanPayments.add(CountChangePlanPayments, changePlanPayments);
        CountChangePlanPayments ++;

    }

    public void CalculationPlan(){

        double percentSum;
        Calendar payDate = creditDate;
        arrayDebtInfo = new ArrayList<DebtInfoOfMonth>(userCountDuration);

        CalculationAnnuity();
        balanceDebt = creditSum;

        SortChangePlanPayments();

        // Пока не списана вся ссудная задолженнность
        while(balanceDebt > 0){
            percentSum = GetPercentSum(payDate);
            payDate.add(Calendar.MONTH, 1);
            balanceDebt -= (annuityPay - percentSum);
            if(balanceDebt < 0){
                annuityPay += balanceDebt;
                balanceDebt = 0;
            }
            arrayDebtInfo.add(realCountDuration, new DebtInfoOfMonth(new GregorianCalendar(payDate.get(Calendar.YEAR), payDate.get(Calendar.MONTH), payDate.get(Calendar.DAY_OF_MONTH)), annuityPay, percentSum, balanceDebt));
            summaryBasicSum += (annuityPay - percentSum);
            summaryPercentSum += percentSum;
            realCountDuration++;
            //Защита от зацикливания
            if(realCountDuration > 10000){
                break;
            }
        }
    }

    private double GetPercentSum(Calendar payDate) {

        double percentSum = 0;
        Calendar date1 = new GregorianCalendar(payDate.get(Calendar.YEAR), payDate.get(Calendar.MONTH), payDate.get(Calendar.DAY_OF_MONTH));
        Calendar date2 = new GregorianCalendar(payDate.get(Calendar.YEAR), payDate.get(Calendar.MONTH), payDate.get(Calendar.DAY_OF_MONTH));
        Calendar tempDate1 = new GregorianCalendar(payDate.get(Calendar.YEAR), payDate.get(Calendar.MONTH), payDate.get(Calendar.DAY_OF_MONTH));
        Calendar tempDate2;

        date2.add(Calendar.MONTH, 1);

        if(arrayChangePlanPayments != null && !arrayChangePlanPayments.isEmpty()) {
            for(ChangePlanPayments changePlanPayments : arrayChangePlanPayments) {
                if(changePlanPayments.CheckDate(date1, date2)){
                    tempDate2 = changePlanPayments.GetChangeDate();
                    percentSum += CalculationPercentSum(tempDate1, tempDate2);
                    if(changePlanPayments.GetSumRepayments() > 0){
                        //Погашение уже расчитанных процентов за счет досрочного платежа
                        balanceDebt -= (changePlanPayments.GetSumRepayments() - percentSum);
                        percentSum = 0;
                        if(balanceDebt < 0) {
                            balanceDebt = 0;
                        }
                        if(changePlanPayments.GetTypeRepayments() == 0) {
                            currentCreditSum = balanceDebt;
                            currentCountDuration -= realCountDuration;
                        }
                    }
                    if(changePlanPayments.GetNewRate() > 0) {
                        currentRate = changePlanPayments.GetNewRate();
                    }
                    CalculationAnnuity();
                    //Сдвигаем дату начала диапазона на следующий период
                    tempDate1.add(Calendar.DAY_OF_YEAR, (tempDate2.get(Calendar.DAY_OF_YEAR) - tempDate1.get(Calendar.DAY_OF_YEAR)));
                }
            }

        }

        // Последний период, если еще не расчитан
        if(tempDate1.get(Calendar.DAY_OF_YEAR) <= date2.get(Calendar.DAY_OF_YEAR) || tempDate1.get(Calendar.YEAR) <= date2.get(Calendar.YEAR)) {
            CalculationAnnuity();
            percentSum += CalculationPercentSum(tempDate1, date2);
        }

        return percentSum;

    }

    private double CalculationPercentSum(Calendar date1, Calendar date2){

        double percentSum = 0;
        int countDay, countDayAdd;

        // Учет перехода года
        if(date1.get(Calendar.YEAR) != date2.get(Calendar.YEAR)) {
            countDay = date1.getActualMaximum(Calendar.DAY_OF_YEAR) - date1.get(Calendar.DAY_OF_YEAR);
            percentSum = (((balanceDebt/100)*currentRate)/date1.getActualMaximum(Calendar.DAY_OF_YEAR))*countDay;
            countDayAdd = date2.get(Calendar.DAY_OF_YEAR);
            percentSum += (((balanceDebt/100)*currentRate)/date2.getActualMaximum(Calendar.DAY_OF_YEAR))*countDayAdd;
            countDay += countDayAdd;
        } else {
            countDay = date2.get(Calendar.DAY_OF_YEAR) - date1.get(Calendar.DAY_OF_YEAR);
            percentSum = (((balanceDebt/100)*currentRate)/date1.getActualMaximum(Calendar.DAY_OF_YEAR))*countDay;
        }

        return percentSum;

    }

    private void CalculationAnnuity(){
        rateShare = (currentRate/100)/12;
        annuityIndex = rateShare * Math.pow(1 + rateShare, currentCountDuration) / (Math.pow(1 + rateShare, currentCountDuration) - 1);
        annuityPay = Math.round(currentCreditSum * annuityIndex * 100.00)/100.00;
    }

    void SortChangePlanPayments(){
        if(arrayChangePlanPayments != null && !arrayChangePlanPayments.isEmpty()) {
            arrayChangePlanPayments.sort(new ChangePlanPaymentsComparator());
        }
    }

    public void PrintArrayChangePlanPayments(){

        if(arrayChangePlanPayments != null && !arrayChangePlanPayments.isEmpty()) {
            for (ChangePlanPayments changePlanPayments : arrayChangePlanPayments) {
                System.out.println(changePlanPayments);
            }
        }

    }

    public void PrintArrayDebtInfo(){

        if(arrayDebtInfo != null && !arrayDebtInfo.isEmpty()) {
            for (DebtInfoOfMonth debtInfoOfMonth : arrayDebtInfo) {
                System.out.println(debtInfoOfMonth);
            }
        }

        System.out.println("Общая задолженность: " + DebtInfoOfMonth.formaterDecimal.format(summaryBasicSum) + " Сумма процентов: " + DebtInfoOfMonth.formaterDecimal.format(summaryPercentSum));

    }

}