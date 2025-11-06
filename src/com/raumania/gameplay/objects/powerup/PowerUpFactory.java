package com.raumania.gameplay.objects.powerup;

public class PowerUpFactory {
    /**
     * Spawns a random power-up at the specified (x, y) position.
     *
     * @param x the x-coordinate to spawn the power-up
     * @param y the y-coordinate to spawn the power-up
     * @param width the width of the power-up
     * @param height the height of the power-up
     * @param chance the probability (0.0 to 1.0) of spawning a power-up
     *
     * @return a new PowerUp instance or null if no power-up is
     */
    public static PowerUp createRandomPowerUp(
            double x, double y, double width, double height, double chance) {
        // threshold of create a powerup or not
        if (Math.random() > chance) {
            return null;
        }

        PowerUpType[] types = PowerUpType.values();
        int randomIndex = (int) (Math.random() * types.length);
        PowerUpType randomType = types[randomIndex];

        return switch (randomType) {
            case ADD_BALL -> new AddBallPowerUp(x, y, width, height);
            case EXTEND_PADDLE -> new ExtendPaddlePowerUp(x, y, width, height);
            case IMMORTAL -> new ImmortalPowerUp(x, y, width, height);
            default -> throw new IllegalArgumentException("Unknown PowerUpType: " + randomType);
        };
    }
}