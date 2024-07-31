package Banks.TimeMachine;

import Banks.Banks.CentralBank;

import java.time.LocalDate;

public class TimeMachine {
    private static TimeMachine instance;
    private LocalDate date;

    private TimeMachine() {
        this.date = LocalDate.now();
    }

    public static TimeMachine getInstance() {
        if (instance == null) {
            instance = new TimeMachine();
        }
        return instance;
    }

    public void skipDay() {
        date = date.plusDays(1);
        if (date.getDayOfMonth() == 1) {
            CentralBank.getInstance().collectMonthlyIntrest();
        }
        CentralBank.getInstance().addDailyIntrest();
    }

    public void skipDays(int numberOfDays) {
        for (int i = 0; i < numberOfDays; i++) {
            this.skipDay();
        }
    }
}
