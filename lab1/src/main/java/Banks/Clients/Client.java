package Banks.Clients;

import Banks.Accounts.BaseAccount;
import Banks.Banks.Bank;
import Banks.Events.Event;
import Banks.Events.VerificationChangeEvent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Client implements EventListener {
    private final UUID id;
    private final String firstName;
    private final String lastName;
    private String address;
    private String passport;
    private Boolean verified = false;
    private final List<BaseAccount> accounts;
    protected final List<String> notificationHistory;

    public Client(String firstName, String lastName, String address, String passport) {
        this.id = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.passport = passport;
        this.accounts = new ArrayList<>();
        this.notificationHistory = new ArrayList<>();
        updateVerification();
    }

    public Client(String firstName, String lastName) {
        this(firstName, lastName, "", "");
    }

    public void setAddress(String address) {
        this.address = address;
        updateVerification();
    }

    public void setPassport(String passport) {
        this.passport = passport;
        updateVerification();
    }

    public void addAccount(BaseAccount account) {
        if (!accounts.contains(account)) {
            accounts.add(account);
            if (verified) {
                update(new VerificationChangeEvent(true));
            }
        }
    }

    public void removeAccount(BaseAccount account) {
        accounts.remove(account);
    }

    private void updateVerification() {
        if (!verified && !(address.isBlank() || passport.isBlank())) {
            verified = true;
            update(new VerificationChangeEvent(true));
        }
    }

    public void subscribeForNotifications(Bank bank) {
        bank.addSubscriber(this);
    }

    public void unsubscribeFromNotifications(Bank bank) {
        bank.removeSubscriber(this);
    }

    @Override
    public void update(Event event) {
        for (BaseAccount account: accounts) {
            account.processEvent(event);
        }
    }

    @Override
    public void notify(String message) {
        System.out.println("[NOTIFICATION FOR " + firstName + ' ' + lastName + "] " + message);
        notificationHistory.add(message);
    }
}
