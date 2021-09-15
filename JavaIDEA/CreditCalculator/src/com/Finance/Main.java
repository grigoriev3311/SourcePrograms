package com.Finance;

// Кредитный калькулятор с возможностью расчета плановых платежей с учетом частичных
// досрочных погашений и изменений ставок
// В конструктор ListDebtInto необходимо передать сумму кредита, процентную ставку и
// срок кредита в месяцах
// С помощью процедуры AddChangePlanPayments этого же класса создается точка изменения
// графика платежей (частичное досрочное погашение или изменении процентной ставки)

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.lang.Exception;

public class Main {

    public static void main(String[] args) {

        Calendar startDate = new GregorianCalendar();
        //Calendar finishDate = new GregorianCalendar();
        //DebtInfoOfMonth firstDebt;
        ListDebtInto listDebtInto;

        startDate.setTime(new Date());
        //finishDate.setTime(new Date());
        //finishDate.add(Calendar.MONTH, 1);

        listDebtInto = new ListDebtInto(startDate, 14000000, 8, 12*30);

        // Создание точек изменения процентной ставки
        try {
             listDebtInto.AddChangePlanPayments(new GregorianCalendar(2022, 4, 20), 500000, 0, -1);
             //listDebtInto.AddChangePlanPayments(new GregorianCalendar(2022, 5, 20), 15000, 1, -1);
             //listDebtInto.AddChangePlanPayments(new GregorianCalendar(2022, 4, 15), -1, -1, 1);
        }
        catch (Exception ex)
        {
            System.out.println("Exception: " + ex.getMessage());
        }

        listDebtInto.CalculationPlan();

        //listDebtInto.PrintArrayChangePlanPayments();

        listDebtInto.PrintArrayDebtInfo();

    }
}
