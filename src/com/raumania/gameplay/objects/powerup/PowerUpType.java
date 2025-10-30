package com.raumania.gameplay.objects.powerup;

public enum PowerUpType {
    ADD_BALL(5.0),
    EXTEND_PADDLE(5.0),
    IMMORTAL(10.0);

    private final double duration;

    PowerUpType(double duration) {
        this.duration = duration;
    }

    public double getDuration() {
        return duration;
    }
}
