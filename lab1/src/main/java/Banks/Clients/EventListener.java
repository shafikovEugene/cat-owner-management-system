package Banks.Clients;

import Banks.Events.Event;

public interface EventListener {
    void update(Event event);
    void notify(String message);
}
