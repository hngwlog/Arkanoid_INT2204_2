package com.raumania.gameplay.objects.powerup;

public enum PowerUpType {
    ADD_BALL(5.0, "add_ball"),
    EXTEND_PADDLE(5.0, "extend_paddle"),
    IMMORTAL(10.0, "immortal");

    private final double duration;
    private final String name;

    PowerUpType(double duration, String name) {
        this.duration = duration;
        this.name = name;
    }

    public double getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }
}
