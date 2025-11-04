package com.raumania.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class AStarInstructorTest {

    @Test
    public void testEmptyGridDiagonalAllowed() {
        boolean[][] grid = new boolean[5][5];
        for (int r = 0; r < 5; r++) for (int c = 0; c < 5; c++) grid[r][c] = true;

        List<int[]> path = AStarInstructor.findPath(grid, 0, 0, 4, 4, true);
        Assertions.assertFalse(path.isEmpty(), "Path should be found in empty grid");
        int[] start = path.get(0);
        int[] end = path.get(path.size() - 1);
        Assertions.assertArrayEquals(new int[]{0, 0}, start);
        Assertions.assertArrayEquals(new int[]{4, 4}, end);
    }

    @Test
    public void testBlockedGridNoPath() {
        boolean[][] grid = new boolean[3][3];
        for (int r = 0; r < 3; r++) for (int c = 0; c < 3; c++) grid[r][c] = true;
        // block entire middle row
        grid[1][0] = false;
        grid[1][1] = false;
        grid[1][2] = false;

        List<int[]> path = AStarInstructor.findPath(grid, 0, 0, 2, 2, false);
        Assertions.assertTrue(path.isEmpty(), "No path should exist when middle row is blocked");
    }
}
