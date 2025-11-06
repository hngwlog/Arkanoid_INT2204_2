package com.raumania.gameplay.objects.brick;



public class BrickFactory {
    public static Brick createBrick(String brickName, double x, double y, int color) {
        return switch (brickName) {
            case "normal" -> new NormalBrick(x, y, Brick.BRICK_WIDTH, Brick.BRICK_HEIGHT, color);
            case "strong" -> new StrongBrick(x, y, Brick.BRICK_WIDTH, Brick.BRICK_HEIGHT);
            case "invisible" ->
                    new InvisibleBrick(x, y, Brick.BRICK_WIDTH, Brick.BRICK_HEIGHT, color);
            case "empty" -> null;
            default -> throw new IllegalArgumentException("Brick type not exist");
        };
    }
}
