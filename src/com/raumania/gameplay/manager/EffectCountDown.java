package com.raumania.gameplay.manager;

import com.raumania.gameplay.objects.powerup.PowerUpType;

public class EffectCountDown {
    private double startTime;
    private final double duration;
    private final PowerUpType effectType;

    public EffectCountDown(double startTime, double duration, PowerUpType effectType) {
        this.startTime = startTime;
        this.duration = duration;
        this.effectType = effectType;
    }

    public double getTimeRemaining(double now) {
        return Math.max(0.0, duration - (now - startTime));
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getDuration() {
        return duration;
    }

    public PowerUpType getEffectType() {
        return effectType;
    }
}
