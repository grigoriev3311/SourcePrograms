package com.Finance;

import java.util.Comparator;

public class ChangePlanPaymentsComparator implements Comparator<ChangePlanPayments> {

    public int compare(ChangePlanPayments date1, ChangePlanPayments date2) {
        return date1.compareTo(date2);
    }

}
