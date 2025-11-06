package com.raumania.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Simple A* pathfinding implementation for rectangular boolean grids.
 *
 * <p>Usage: provide a 2D boolean grid where true = walkable, false = blocked. The algorithm returns
 * a list of int[2] coordinates {row, col} from start to goal (inclusive). If no path exists, an
 * empty list is returned.
 */
public class AStarInstructor {

    private static final int[] DIR4_R = {-1, 1, 0, 0};
    private static final int[] DIR4_C = {0, 0, -1, 1};
    private static final int[] DIR8_R = {-1, -1, -1, 0, 0, 1, 1, 1};
    private static final int[] DIR8_C = {-1, 0, 1, -1, 1, -1, 0, 1};

    /**
     * Finds a path on a grid using A*.
     *
     * @param passable boolean grid [rows][cols], true = walkable
     * @param sr start row
     * @param sc start col
     * @param gr goal row
     * @param gc goal col
     * @param allowDiagonals whether 8-neighbour moves are allowed
     * @return list of {row,col} from start to goal inclusive, or empty list if none
     */
    public static List<int[]> findPath(
            boolean[][] passable, int sr, int sc, int gr, int gc, boolean allowDiagonals) {
        if (passable == null || passable.length == 0) return Collections.emptyList();
        int rows = passable.length;
        int cols = passable[0].length;
        if (!inBounds(sr, sc, rows, cols) || !inBounds(gr, gc, rows, cols))
            return Collections.emptyList();
        if (!passable[sr][sc] || !passable[gr][gc]) return Collections.emptyList();
        if (sr == gr && sc == gc) {
            List<int[]> single = new ArrayList<>();
            single.add(new int[] {sr, sc});
            return single;
        }

        int n = rows * cols;
        double[] g = new double[n];
        int[] parent = new int[n];
        boolean[] closed = new boolean[n];
        for (int i = 0; i < n; i++) {
            g[i] = Double.POSITIVE_INFINITY;
            parent[i] = -1;
        }

        int startIdx = idx(sr, sc, cols);
        int goalIdx = idx(gr, gc, cols);
        g[startIdx] = 0.0;
        double h0 = heuristic(sr, sc, gr, gc, allowDiagonals);
        PriorityQueue<Node> open = new PriorityQueue<>();
        open.add(new Node(startIdx, h0));

        while (!open.isEmpty()) {
            Node cur = open.poll();
            int curIdx = cur.idx;
            if (closed[curIdx]) continue;
            closed[curIdx] = true;

            if (curIdx == goalIdx) break;

            int cr = curIdx / cols;
            int cc = curIdx % cols;

            if (allowDiagonals) {
                for (int k = 0; k < DIR8_R.length; k++) {
                    int nr = cr + DIR8_R[k];
                    int nc = cc + DIR8_C[k];
                    considerNeighbor(
                            passable,
                            rows,
                            cols,
                            cr,
                            cc,
                            nr,
                            nc,
                            g,
                            parent,
                            closed,
                            goalIdx,
                            open,
                            allowDiagonals);
                }
            } else {
                for (int k = 0; k < DIR4_R.length; k++) {
                    int nr = cr + DIR4_R[k];
                    int nc = cc + DIR4_C[k];
                    considerNeighbor(
                            passable,
                            rows,
                            cols,
                            cr,
                            cc,
                            nr,
                            nc,
                            g,
                            parent,
                            closed,
                            goalIdx,
                            open,
                            allowDiagonals);
                }
            }
        }

        if (parent[goalIdx] == -1 && startIdx != goalIdx) {
            return Collections.emptyList();
        }

        // Reconstruct
        List<int[]> path = new ArrayList<>();
        int cur = goalIdx;
        while (cur != -1) {
            int r = cur / cols;
            int c = cur % cols;
            path.add(new int[] {r, c});
            if (cur == startIdx) break;
            cur = parent[cur];
        }
        Collections.reverse(path);
        return path;
    }

    private static void considerNeighbor(
            boolean[][] passable,
            int rows,
            int cols,
            int cr,
            int cc,
            int nr,
            int nc,
            double[] g,
            int[] parent,
            boolean[] closed,
            int goalIdx,
            PriorityQueue<Node> open,
            boolean diag) {
        if (!inBounds(nr, nc, rows, cols)) return;
        int nidx = idx(nr, nc, cols);
        if (closed[nidx]) return;
        if (!passable[nr][nc]) return;

        double moveCost = ((nr == cr || nc == cc) ? 1.0 : Math.sqrt(2));
        double tentative = g[idx(cr, cc, cols)] + moveCost;
        if (tentative < g[nidx]) {
            g[nidx] = tentative;
            parent[nidx] = idx(cr, cc, cols);
            int gr = goalIdx / cols;
            int gc = goalIdx % cols;
            double f = tentative + heuristic(nr, nc, gr, gc, diag);
            open.add(new Node(nidx, f));
        }
    }

    private record Node(int idx,double f) implements Comparable<Node> {

        @Override
            public int compareTo(Node o) {
                return Double.compare(this.f, o.f);
            }
        }

    private static double heuristic(int r1, int c1, int r2, int c2, boolean diag) {
        int dx = Math.abs(r1 - r2);
        int dy = Math.abs(c1 - c2);
        if (diag) {
            // Euclidean distance
            return Math.hypot(dx, dy);
        } else {
            // Manhattan
            return dx + dy;
        }
    }

    private static boolean inBounds(int r, int c, int rows, int cols) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    private static int idx(int r, int c, int cols) {
        return r * cols + c;
    }
}
