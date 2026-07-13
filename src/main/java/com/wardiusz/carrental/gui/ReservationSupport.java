package com.wardiusz.carrental.gui;

import com.wardiusz.carrental.model.Reservation;

public final class ReservationSupport {

    private ReservationSupport() { }

    public static String identifier(Reservation r) {
        return r.getId();
    }

    public static String shortLabel(Reservation r) {
        return identifier(r) + "   —   " + r.getStatus() + "   —   " + r.getCreationDate().format(UiHelper.DATE);
    }
}
