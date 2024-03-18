package io.github.eggohito.nether_reactor_revisited.duck.gamerule.enter_and_exit_nether;

public interface Notifiable {
    default boolean nrr$wasNotified() {
        return false;
    }
    void nrr$setNotified(boolean notified);
}
