package io.github.eggohito.nether_reactor_revisited.duck.gamerule.enter_and_exit_nether;

import net.minecraft.entity.Entity;

import java.util.function.Predicate;

public interface Notifiable {

    Predicate<Entity> TO_BE_NOTIFIED = entity -> !entity.isSpectator()
        && entity instanceof Notifiable notifiable
        && !notifiable.nrr$wasNotified()
        && notifiable.nrr$toBeNotified();

    default boolean nrr$wasNotified() {
        return false;
    }
    default boolean nrr$toBeNotified() {
        return false;
    }

    void nrr$setNotified(boolean notified);
    void nrr$setToNotify(boolean toNotify);

}
