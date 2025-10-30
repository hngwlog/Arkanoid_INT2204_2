package com.raumania.gameplay.manager;

public class EffectCountDown {
    public double startTime;
    public double duration;
    public String effectType;

    public EffectCountDown(double startTime, double duration, String effectType) {
        this.startTime = startTime;
        this.duration = duration;
        this.effectType = effectType;
    }

    public double getTimeRemaining(double now) {
        return Math.max(0.0, duration - (now - startTime));
    }
}
