package mixnfix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import mixnfix.ProcessImage.CellMap;
import mixnfix.ProcessImage.ControlPoint;
import mixnfix.ProcessImage.Region;

/**
 * Robust and efficient detection of the answer sheet control points.
 *
 * <p>The answer sheet carries 21 control dots laid out on a regular
 * 3 x 7 grid (three vertical columns of seven dots each). The four
 * outermost dots (top-left, top-right, bottom-right and bottom-left)
 * delimit the marking area. As the sheet is photographed with a hand held
 * camera, the picture of that grid is the image of a planar grid under a
 * <em>projective</em> transformation (a homography). With a strong
 * perspective the outer quadrilateral is far from a rectangle: opposite
 * sides are neither parallel nor of similar length and the angles are far
 * from 90 degrees.</p>
 *
 * <p>The legacy algorithm searched the four outer dots with a
 * combinatorial O(n^4) enumeration constrained by <em>rectangle</em>
 * hypotheses (90 degree angles within a few degrees of tolerance, a fixed
 * side ratio and a parallelogram test). Those hypotheses only hold for
 * weakly deformed pictures, hence severely deformed exams were rejected.
 * In addition the dot candidates were restricted to the few blobs closest
 * to the four corners of the picture, which is also a perspective
 * dependent assumption.</p>
 *
 * <p>This class replaces those tests by a projective invariant search:</p>
 * <ol>
 *   <li>all valid blobs (dot candidates) are collected;</li>
 *   <li>their convex hull is computed: under any perspective the four
 *       outer control dots are extreme points of the dot cloud, hence
 *       hull vertices (a second hull layer is peeled if needed);</li>
 *   <li>the quadrilaterals made of four extreme points are enumerated in
 *       decreasing area order, keeping only the largest ones;</li>
 *   <li>each hypothesis (4 rotations x 2 orientations) defines a
 *       homography from theoretical sheet coordinates to the picture;
 *       it is scored by mapping <em>all</em> 21 theoretical control
 *       points and counting how many fall onto a detected blob;</li>
 *   <li>the winning hypothesis is refined by a least squares homography
 *       computed from every matched dot and each control point is finally
 *       snapped to its blob.</li>
 * </ol>
 *
 * <p>Cost: O(n log n) for the hull plus a bounded number of hypotheses
 * (at most {@link #MAX_HYPOTHESES}), each verified in O(numControlPoints)
 * with O(1) nearest blob queries through a uniform spatial hash grid.
 * This is orders of magnitude cheaper than the old O(n^4) enumeration,
 * which matters when grading large numbers of exams.</p>
 */
public class ControlPointDetector {

    /** Maximum number of quadrilateral hypotheses actually verified. */
    public static final int MAX_HYPOTHESES = 1500;

    /** Maximum number of extreme points used as corner candidates. */
    public static final int MAX_CORNER_CANDIDATES = 24;

    /** A hypothesis must cover at least this fraction of the candidate hull area. */
    public static final double MIN_AREA_FRACTION = 0.25;

    /** Fraction of the control points that must be matched to accept a fit. */
    public static final double MIN_INLIER_FRACTION = 0.80;

    /** print some information about the fitting process. */
    public static boolean VERBOSE = false;

    // ------------------------------------------------------------------
    // result of a fit
    // ------------------------------------------------------------------
    public static class Fit {
        public double[] H;          // homography: theoretical -> image
        public int inliers;
        public double error = Double.MAX_VALUE;   // mean square distance of the matched dots
        public boolean phaseOk;
        public boolean accurate;    // the matched dots really lie on the projective grid

        // context needed to snap the control points afterwards
        ArrayList<Region> valid;
        Grid grid;
        double tol;
        int numControlPoints;

        public double rms() { return Math.sqrt(Math.max(error, 0)); }

        /** ranking: accurate fits first, then the number of matched dots,
         *  then the historical phase convention, then the residual. */
        public boolean better(Fit other) {
            if (other == null) return true;
            if (this.accurate != other.accurate) return this.accurate;
            if (this.inliers != other.inliers) return this.inliers > other.inliers;
            if (this.phaseOk != other.phaseOk) return this.phaseOk;
            return this.error < other.error;
        }
    }

    // ------------------------------------------------------------------
    // uniform spatial hash grid over the blob centers (O(1) queries)
    // ------------------------------------------------------------------
    private static class Grid {
        final double cell, minx, miny;
        final int nx, ny;
        final int[] head, next;
        final double[] px, py;

        Grid(double[] px, double[] py, int n, double cell) {
            this.px = px;
            this.py = py;
            this.cell = Math.max(cell, 1.0e-6);
            double mnx = Double.MAX_VALUE, mny = Double.MAX_VALUE;
            double mxx = -Double.MAX_VALUE, mxy = -Double.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                if (px[i] < mnx) mnx = px[i];
                if (px[i] > mxx) mxx = px[i];
                if (py[i] < mny) mny = py[i];
                if (py[i] > mxy) mxy = py[i];
            }
            minx = mnx;
            miny = mny;
            nx = Math.max(1, (int) ((mxx - mnx) / this.cell) + 1);
            ny = Math.max(1, (int) ((mxy - mny) / this.cell) + 1);
            head = new int[nx * ny];
            Arrays.fill(head, -1);
            next = new int[n];
            for (int i = 0; i < n; i++) {
                int b = bucketY(py[i]) * nx + bucketX(px[i]);
                next[i] = head[b];
                head[b] = i;
            }
        }

        private int bucketX(double x) { return Math.min(nx - 1, Math.max(0, (int) Math.floor((x - minx) / cell))); }
        private int bucketY(double y) { return Math.min(ny - 1, Math.max(0, (int) Math.floor((y - miny) / cell))); }

        /** index of the closest blob at distance <= radius, or -1. */
        int closest(double x, double y, double radius, double[] distOut) {
            int cx0 = bucketX(x - radius), cx1 = bucketX(x + radius);
            int cy0 = bucketY(y - radius), cy1 = bucketY(y + radius);
            int best = -1;
            double bestD2 = radius * radius;
            for (int cy = cy0; cy <= cy1; cy++) {
                int row = cy * nx;
                for (int cx = cx0; cx <= cx1; cx++) {
                    for (int i = head[row + cx]; i >= 0; i = next[i]) {
                        double dx = px[i] - x, dy = py[i] - y;
                        double d2 = dx * dx + dy * dy;
                        if (d2 <= bestD2) { bestD2 = d2; best = i; }
                    }
                }
            }
            if (best >= 0 && distOut != null) distOut[0] = Math.sqrt(bestD2);
            return best;
        }
    }

    // ------------------------------------------------------------------
    // linear algebra
    // ------------------------------------------------------------------

    /** Gaussian elimination with partial pivoting; the solution is left in b. */
    private static boolean solve(double[][] a, double[] b, int n) {
        for (int col = 0; col < n; col++) {
            int piv = col;
            double max = Math.abs(a[col][col]);
            for (int r = col + 1; r < n; r++) {
                double v = Math.abs(a[r][col]);
                if (v > max) { max = v; piv = r; }
            }
            if (max < 1.0e-12) return false;
            if (piv != col) {
                double[] t = a[piv]; a[piv] = a[col]; a[col] = t;
                double tb = b[piv]; b[piv] = b[col]; b[col] = tb;
            }
            double d = a[col][col];
            for (int r = col + 1; r < n; r++) {
                double f = a[r][col] / d;
                if (f == 0.0) continue;
                for (int c = col; c < n; c++) a[r][c] -= f * a[col][c];
                b[r] -= f * b[col];
            }
        }
        for (int r = n - 1; r >= 0; r--) {
            double s = b[r];
            for (int c = r + 1; c < n; c++) s -= a[r][c] * b[c];
            b[r] = s / a[r][r];
        }
        return true;
    }

    /**
     * Homography (3x3 with h[8] = 1) mapping the four source points onto
     * the four destination points. Arrays hold x0,y0,...,x3,y3.
     */
    public static double[] homographyFrom4Points(double[] src, double[] dst) {
        double[][] a = new double[8][];
        double[] b = new double[8];
        for (int i = 0; i < 4; i++) {
            double x = src[2 * i], y = src[2 * i + 1];
            double u = dst[2 * i], v = dst[2 * i + 1];
            a[2 * i] = new double[] { x, y, 1, 0, 0, 0, -x * u, -y * u };
            b[2 * i] = u;
            a[2 * i + 1] = new double[] { 0, 0, 0, x, y, 1, -x * v, -y * v };
            b[2 * i + 1] = v;
        }
        if (!solve(a, b, 8)) return null;
        return new double[] { b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7], 1.0 };
    }

    /**
     * Least squares homography from n >= 4 correspondences, computed on
     * normalized coordinates (Hartley normalization) for stability.
     */
    public static double[] homographyLeastSquares(double[] src, double[] dst, int n) {
        if (n < 4) return null;
        double[] Ts = normalizingTransform(src, n);
        double[] Td = normalizingTransform(dst, n);
        double[][] a = new double[8][8];
        double[] rhs = new double[8];
        double[] row = new double[8];
        for (int i = 0; i < n; i++) {
            double x = Ts[0] * src[2 * i] + Ts[2];
            double y = Ts[1] * src[2 * i + 1] + Ts[3];
            double u = Td[0] * dst[2 * i] + Td[2];
            double v = Td[1] * dst[2 * i + 1] + Td[3];

            row[0] = x; row[1] = y; row[2] = 1; row[3] = 0; row[4] = 0; row[5] = 0;
            row[6] = -x * u; row[7] = -y * u;
            accumulate(a, rhs, row, u);

            row[0] = 0; row[1] = 0; row[2] = 0; row[3] = x; row[4] = y; row[5] = 1;
            row[6] = -x * v; row[7] = -y * v;
            accumulate(a, rhs, row, v);
        }
        if (!solve(a, rhs, 8)) return null;
        double[] Hn = new double[] { rhs[0], rhs[1], rhs[2], rhs[3], rhs[4], rhs[5], rhs[6], rhs[7], 1.0 };
        double[] TsM = new double[] { Ts[0], 0, Ts[2], 0, Ts[1], Ts[3], 0, 0, 1 };
        double[] TdInv = new double[] { 1.0 / Td[0], 0, -Td[2] / Td[0], 0, 1.0 / Td[1], -Td[3] / Td[1], 0, 0, 1 };
        return mul(TdInv, mul(Hn, TsM));
    }

    private static void accumulate(double[][] a, double[] rhs, double[] row, double value) {
        for (int r = 0; r < 8; r++) {
            double rr = row[r];
            if (rr == 0.0) continue;
            for (int c = 0; c < 8; c++) a[r][c] += rr * row[c];
            rhs[r] += rr * value;
        }
    }

    /** {sx, sy, tx, ty} such that x' = sx*x + tx and y' = sy*y + ty. */
    private static double[] normalizingTransform(double[] p, int n) {
        double cx = 0, cy = 0;
        for (int i = 0; i < n; i++) { cx += p[2 * i]; cy += p[2 * i + 1]; }
        cx /= n; cy /= n;
        double dx = 0, dy = 0;
        for (int i = 0; i < n; i++) { dx += Math.abs(p[2 * i] - cx); dy += Math.abs(p[2 * i + 1] - cy); }
        dx /= n; dy /= n;
        double sx = (dx > 1.0e-9 ? 1.0 / dx : 1.0);
        double sy = (dy > 1.0e-9 ? 1.0 / dy : 1.0);
        return new double[] { sx, sy, -sx * cx, -sy * cy };
    }

    private static double[] mul(double[] A, double[] B) {
        double[] C = new double[9];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                double s = 0;
                for (int k = 0; k < 3; k++) s += A[3 * i + k] * B[3 * k + j];
                C[3 * i + j] = s;
            }
        return C;
    }

    /** apply the homography H to (x,y), result in out[0], out[1]. */
    public static void apply(double[] H, double x, double y, double[] out) {
        double w = H[6] * x + H[7] * y + H[8];
        if (Math.abs(w) < 1.0e-12) w = (w < 0 ? -1.0e-12 : 1.0e-12);
        out[0] = (H[0] * x + H[1] * y + H[2]) / w;
        out[1] = (H[3] * x + H[4] * y + H[5]) / w;
    }

    // ------------------------------------------------------------------
    // geometry
    // ------------------------------------------------------------------

    /** convex hull (Andrew monotone chain); returns point indices in ccw order. */
    private static int[] convexHull(final double[] px, final double[] py, int n) {
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, (i, j) -> (px[i] != px[j] ? Double.compare(px[i], px[j]) : Double.compare(py[i], py[j])));
        int[] hull = new int[2 * n + 1];
        int k = 0;
        for (int ii = 0; ii < n; ii++) {
            int i = idx[ii];
            while (k >= 2 && cross(px, py, hull[k - 2], hull[k - 1], i) <= 0) k--;
            hull[k++] = i;
        }
        int lower = k + 1;
        for (int ii = n - 2; ii >= 0; ii--) {
            int i = idx[ii];
            while (k >= lower && cross(px, py, hull[k - 2], hull[k - 1], i) <= 0) k--;
            hull[k++] = i;
        }
        return Arrays.copyOf(hull, Math.max(k - 1, 0));
    }

    private static double cross(double[] px, double[] py, int o, int a, int b) {
        return (px[a] - px[o]) * (py[b] - py[o]) - (py[a] - py[o]) * (px[b] - px[o]);
    }

    private static double quadArea(double[] q) {
        double s = 0;
        for (int i = 0; i < 4; i++) {
            int j = (i + 1) % 4;
            s += q[2 * i] * q[2 * j + 1] - q[2 * j] * q[2 * i + 1];
        }
        return Math.abs(s) / 2.0;
    }

    private static double polygonArea(double[] xs, double[] ys, int n) {
        double s = 0;
        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            s += xs[i] * ys[j] - xs[j] * ys[i];
        }
        return Math.abs(s) / 2.0;
    }

    // ------------------------------------------------------------------
    // main entry point
    // ------------------------------------------------------------------

    /**
     * Fit the theoretical control point grid of <code>map</code> to the
     * blobs detected on the picture. On success every control point gets
     * its practical (image) position and the blob it was snapped to.
     *
     * @param regions head of the linked list of blobs of a given threshold
     * @param map     cell map holding the theoretical control points
     * @param imgW    picture width
     * @param imgH    picture height
     * @return true when the grid was located
     */
    public static Fit detect(Region regions, CellMap map, int imgW, int imgH) {

        // ---------------- collect valid blobs ----------------
        ArrayList<Region> valid = new ArrayList<Region>();
        for (Region r = regions; r != null; r = r.next) {
            if (r.validRegion()) {
                r.calculateCenter();
                valid.add(r);
            }
        }
        int n = valid.size();
        int numCP = map.numControlPoints;
        if (n < 4 || numCP < 4) {
            if (VERBOSE) System.out.println("control points: only " + n + " valid blobs");
            return null;
        }

        double[] px = new double[n];
        double[] py = new double[n];
        for (int i = 0; i < n; i++) {
            Region r = valid.get(i);
            px[i] = r.centerx;
            py[i] = r.centery;
        }

        double diagonal = Math.sqrt((double) imgW * imgW + (double) imgH * imgH);
        double tol = Math.max(ProcessImage._RADIUS_CONTROL_POINT_TOLERANCE, 0.015 * diagonal);
        Grid grid = new Grid(px, py, n, tol);

        double[] tx = new double[numCP];
        double[] ty = new double[numCP];
        for (int i = 0; i < numCP; i++) {
            tx[i] = map.cps[i].x;
            ty[i] = map.cps[i].y;
        }

        // the four outer control points (ids 0..3 = TL, TR, BR, BL)
        double[] theoretical = new double[] {
            map.cps[0].x, map.cps[0].y,
            map.cps[1].x, map.cps[1].y,
            map.cps[2].x, map.cps[2].y,
            map.cps[3].x, map.cps[3].y
        };

        // ---------------- search the outer quadrilateral ----------------
        Fit best = null;
        boolean[] used = new boolean[n];
        int[] candidates = null;
        for (int layer = 0; layer < 2; layer++) {
            candidates = extremePoints(px, py, n, used, candidates);
            if (candidates == null || candidates.length < 4) break;
            Fit f = search(px, py, candidates, theoretical, tx, ty, numCP, grid, tol, imgW, imgH, n);
            if (f != null && f.better(best)) best = f;
            if (best != null && best.inliers >= numCP) break; // every dot matched
        }

        if (best == null) return null;

        // ------------- local RANSAC over the matched dots ---------------
        // The hypothesis only fixes the four outer dots. When one of them
        // is missing (a faint dot may not be detected at a given
        // threshold) the mapping is biased and a few control points get
        // snapped onto neighbouring marks. A local RANSAC over the
        // correspondences found so far, scored with a strict tolerance on
        // the whole grid, recovers the exact projective mapping supported
        // by the largest consistent set of dots.
        double[] H = best.H;
        double strictTol = Math.max(2.0, 0.2 * tol);

        int[] corrCp = new int[numCP];
        int[] corrBlob = new int[numCP];
        double[] out = new double[2];
        double[] dist = new double[1];

        int m = 0;
        for (int i = 0; i < numCP; i++) {
            apply(H, tx[i], ty[i], out);
            int j = grid.closest(out[0], out[1], tol, dist);
            if (j >= 0) { corrCp[m] = i; corrBlob[m] = j; m++; }
        }

        if (m >= 4) {
            int[] stamp = new int[n];
            int[] generation = new int[] { 0 };
            double theoArea = 0.02 * Math.abs((map.cps[1].x - map.cps[0].x) * (map.cps[3].y - map.cps[0].y));
            double[] s4 = new double[8];
            double[] d4 = new double[8];
            Fit bestLocal = null;
            int evaluated = 0;
            ransac:
            for (int a = 0; a < m - 3 && evaluated < MAX_HYPOTHESES; a++)
                for (int b = a + 1; b < m - 2 && evaluated < MAX_HYPOTHESES; b++)
                    for (int c = b + 1; c < m - 1 && evaluated < MAX_HYPOTHESES; c++)
                        for (int d = c + 1; d < m && evaluated < MAX_HYPOTHESES; d++) {
                            int[] q = new int[] { a, b, c, d };
                            for (int i = 0; i < 4; i++) {
                                int ci = corrCp[q[i]];
                                s4[2 * i] = tx[ci];  s4[2 * i + 1] = ty[ci];
                                int bi = corrBlob[q[i]];
                                d4[2 * i] = px[bi];  d4[2 * i + 1] = py[bi];
                            }
                            if (quadArea(s4) < theoArea) continue;  // degenerate (collinear) sample
                            double[] Hs = homographyFrom4Points(s4, d4);
                            if (Hs == null) continue;
                            evaluated++;
                            Fit f = score(Hs, tx, ty, numCP, grid, strictTol, stamp, generation,
                                          (bestLocal == null ? 0 : bestLocal.inliers));
                            if (f.better(bestLocal)) bestLocal = f;
                            if (bestLocal.inliers >= numCP) break ransac; // every dot explained
                        }
            if (bestLocal != null && bestLocal.inliers >= 4) {
                bestLocal.phaseOk = best.phaseOk;
                best = bestLocal;
                H = best.H;
            }
        }

        // ------------- least squares refinement over the inliers ---------
        double[] srcBuf = new double[2 * numCP];
        double[] dstBuf = new double[2 * numCP];
        for (int iter = 0; iter < 3; iter++) {
            int k = 0;
            for (int i = 0; i < numCP; i++) {
                apply(H, tx[i], ty[i], out);
                int j = grid.closest(out[0], out[1], strictTol, dist);
                if (j >= 0) {
                    srcBuf[2 * k] = tx[i];  srcBuf[2 * k + 1] = ty[i];
                    dstBuf[2 * k] = px[j];  dstBuf[2 * k + 1] = py[j];
                    k++;
                }
            }
            if (k < 4) break;
            double[] Hr = homographyLeastSquares(srcBuf, dstBuf, k);
            if (Hr == null) break;
            H = Hr;
        }

        // ------------- final accuracy and matching tolerance -------------
        double err = 0;
        int count = 0;
        for (int i = 0; i < numCP; i++) {
            apply(H, tx[i], ty[i], out);
            int j = grid.closest(out[0], out[1], strictTol, dist);
            if (j >= 0) { err += dist[0] * dist[0]; count++; }
        }
        double rms = (count > 0 ? Math.sqrt(err / count) : Double.MAX_VALUE);

        // once the mapping is accurate only blobs really close to the
        // prediction are accepted: this avoids snapping a control point
        // onto a neighbouring student mark
        double finalTol = Math.min(strictTol, Math.max(2.0, 6.0 * rms));

        m = 0;
        err = 0;
        for (int i = 0; i < numCP; i++) {
            apply(H, tx[i], ty[i], out);
            int j = grid.closest(out[0], out[1], finalTol, dist);
            if (j >= 0) { err += dist[0] * dist[0]; m++; }
        }

        best.H = H;
        best.inliers = m;
        best.error = (m > 0 ? err / m : Double.MAX_VALUE);
        best.valid = valid;
        best.grid = grid;
        best.tol = finalTol;
        best.numControlPoints = numCP;
        best.accurate = (m >= 4 && best.rms() <= Math.max(2.0, 0.15 * tol));

        if (VERBOSE)
            System.out.format("control points: fit with %d/%d dots, rms %.2f pixels (blobs: %d)%n",
                              best.inliers, numCP, best.rms(), n);

        return best;
    }

    /**
     * Write the practical (image) positions of every control point of the
     * map using the given fit: dots that were detected are snapped to the
     * blob center, the remaining ones keep the projective prediction (a
     * homography is exact for a planar target).
     *
     * @return the number of control points snapped to a real blob
     */
    public static int applyFit(Fit fit, CellMap map) {
        if (fit == null) return 0;
        double[] out = new double[2];
        double[] dist = new double[1];
        int found = 0;
        for (int i = 0; i < map.numControlPoints; i++) {
            ControlPoint p = map.cps[i];
            apply(fit.H, p.x, p.y, out);
            int j = fit.grid.closest(out[0], out[1], fit.tol, dist);
            if (j >= 0) {
                Region r = fit.valid.get(j);
                p.xx = r.centerx;
                p.yy = r.centery;
                p.region = r;
                found++;
            }
            else {
                p.xx = out[0];
                p.yy = out[1];
                p.region = null;
            }
        }
        return found;
    }

    /**
     * Convenience entry point: detect and apply in one call.
     *
     * @return true when the control point grid was located
     */
    public static boolean fit(Region regions, CellMap map, int imgW, int imgH) {
        Fit f = detect(regions, map, imgW, imgH);
        if (f == null) return false;
        int minInliers = Math.max(4, (int) Math.ceil(MIN_INLIER_FRACTION * map.numControlPoints));
        if (f.inliers < minInliers) return false;
        return applyFit(f, map) >= minInliers;
    }

    /**
     * Extreme points of the cloud: vertices of the convex hull of the
     * points not used yet (hull peeling). Points of the previous layers
     * are kept in the candidate set.
     */
    private static int[] extremePoints(final double[] px, final double[] py, int n, boolean[] used, int[] previous) {
        int remaining = 0;
        for (int i = 0; i < n; i++) if (!used[i]) remaining++;
        if (remaining < 3) return previous;
        double[] qx = new double[remaining];
        double[] qy = new double[remaining];
        int[] back = new int[remaining];
        int k = 0;
        for (int i = 0; i < n; i++) if (!used[i]) { qx[k] = px[i]; qy[k] = py[i]; back[k] = i; k++; }
        int[] hull = convexHull(qx, qy, remaining);
        ArrayList<Integer> list = new ArrayList<Integer>();
        if (previous != null) for (int i : previous) list.add(i);
        for (int i : hull) {
            int gi = back[i];
            if (!used[gi]) { used[gi] = true; list.add(gi); }
        }
        if (list.size() > MAX_CORNER_CANDIDATES) { // keep the most extreme ones
            double cx = 0, cy = 0;
            for (int i : list) { cx += px[i]; cy += py[i]; }
            cx /= list.size(); cy /= list.size();
            final double fcx = cx, fcy = cy;
            Collections.sort(list, (i, j) -> Double.compare(
                (px[j] - fcx) * (px[j] - fcx) + (py[j] - fcy) * (py[j] - fcy),
                (px[i] - fcx) * (px[i] - fcx) + (py[i] - fcy) * (py[i] - fcy)));
            while (list.size() > MAX_CORNER_CANDIDATES) list.remove(list.size() - 1);
        }
        int[] result = new int[list.size()];
        for (int i = 0; i < result.length; i++) result[i] = list.get(i);
        return result;
    }

    /** a quadrilateral hypothesis: four candidate points and its area. */
    private static class Hypothesis implements Comparable<Hypothesis> {
        int a, b, c, d;
        double area;
        public int compareTo(Hypothesis o) { return Double.compare(o.area, this.area); } // decreasing area
    }

    /**
     * Enumerate the quadrilaterals formed by the candidate (extreme)
     * points, from the largest to the smallest, and verify each of them
     * against the whole theoretical control point grid.
     */
    private static Fit search(final double[] px, final double[] py, int[] cand,
                              double[] theoretical, double[] tx, double[] ty, int numCP,
                              Grid grid, double tol, int imgW, int imgH, int numBlobs) {

        int h = cand.length;
        int[] order = sortAroundCentroid(px, py, cand);
        double[] hx = new double[h];
        double[] hy = new double[h];
        for (int i = 0; i < h; i++) { hx[i] = px[order[i]]; hy[i] = py[order[i]]; }
        double totalArea = polygonArea(hx, hy, h);
        if (totalArea <= 0) return null;

        double minSide = ProcessImage._MIN_SIDE * 0.25; // very loose sanity check

        ArrayList<Hypothesis> hyps = new ArrayList<Hypothesis>();
        double[] quad = new double[8];
        for (int a = 0; a < h - 3; a++)
            for (int b = a + 1; b < h - 2; b++)
                for (int c = b + 1; c < h - 1; c++)
                    for (int d = c + 1; d < h; d++) {
                        quad[0] = hx[a]; quad[1] = hy[a];
                        quad[2] = hx[b]; quad[3] = hy[b];
                        quad[4] = hx[c]; quad[5] = hy[c];
                        quad[6] = hx[d]; quad[7] = hy[d];
                        double area = quadArea(quad);
                        if (area < MIN_AREA_FRACTION * totalArea) continue;
                        if (minSideLength(quad) < minSide) continue;
                        Hypothesis hp = new Hypothesis();
                        hp.a = a; hp.b = b; hp.c = c; hp.d = d; hp.area = area;
                        hyps.add(hp);
                    }
        if (hyps.isEmpty()) return null;
        Collections.sort(hyps);

        // The theoretical corners TL,TR,BR,BL have a fixed orientation; the
        // hypothesis vertices are taken in convex position order, so only
        // the rotation is unknown once the orientations are matched. This
        // halves the number of homographies to evaluate.
        double theoreticalSign = signedArea(theoretical);

        int[] stamp = new int[numBlobs];
        int[] generation = new int[] { 0 };

        Fit best = null;
        int evaluated = 0;
        double[] corners = new double[8];
        for (Hypothesis hp : hyps) {
            if (evaluated >= MAX_HYPOTHESES) break;
            int[] q = new int[] { hp.a, hp.b, hp.c, hp.d };
            for (int i = 0; i < 4; i++) { corners[2 * i] = hx[q[i]]; corners[2 * i + 1] = hy[q[i]]; }
            boolean reverse = (signedArea(corners) * theoreticalSign < 0);
            {
                for (int rot = 0; rot < 4; rot++) {
                    for (int i = 0; i < 4; i++) {
                        int j = (!reverse ? q[(rot + i) % 4] : q[(rot + 4 - i) % 4]);
                        corners[2 * i] = hx[j];
                        corners[2 * i + 1] = hy[j];
                    }
                    double[] H = homographyFrom4Points(theoretical, corners);
                    if (H == null) continue;
                    evaluated++;
                    Fit f = score(H, tx, ty, numCP, grid, tol, stamp, generation,
                                  (best == null ? 0 : best.inliers));
                    f.phaseOk = phaseMatches(corners, imgW, imgH);
                    if (f.better(best)) best = f;
                    if (best.inliers >= numCP && best.phaseOk) return best; // perfect fit
                }
            }
        }
        if (VERBOSE && best != null)
            System.out.println("control points: best hypothesis has " + best.inliers + "/" + numCP +
                               " inliers after " + evaluated + " evaluations");
        return best;
    }

    /** signed area of a quadrilateral given as x0,y0,...,x3,y3 */
    private static double signedArea(double[] q) {
        double s = 0;
        for (int i = 0; i < 4; i++) {
            int j = (i + 1) % 4;
            s += q[2 * i] * q[2 * j + 1] - q[2 * j] * q[2 * i + 1];
        }
        return s / 2.0;
    }

    private static double minSideLength(double[] q) {
        double min = Double.MAX_VALUE;
        for (int i = 0; i < 4; i++) {
            int j = (i + 1) % 4;
            double dx = q[2 * i] - q[2 * j], dy = q[2 * i + 1] - q[2 * j + 1];
            double d = Math.sqrt(dx * dx + dy * dy);
            if (d < min) min = d;
        }
        return min;
    }

    /**
     * Score a homography: map every theoretical control point and count
     * how many <em>distinct</em> blobs are hit within the tolerance.
     */
    private static Fit score(double[] H, double[] tx, double[] ty, int numCP,
                             Grid grid, double tol, int[] stamp, int[] generation) {
        return score(H, tx, ty, numCP, grid, tol, stamp, generation, 0);
    }

    /**
     * Same as above with a branch and bound cut: the evaluation is
     * abandoned as soon as the hypothesis cannot reach
     * <code>minToBeat</code> matched dots.
     */
    private static Fit score(double[] H, double[] tx, double[] ty, int numCP,
                             Grid grid, double tol, int[] stamp, int[] generation, int minToBeat) {
        Fit f = new Fit();
        f.H = H;
        int gen = ++generation[0];
        double[] out = new double[2];
        double[] d = new double[1];
        int in = 0;
        double err = 0;
        for (int i = 0; i < numCP; i++) {
            if (in + (numCP - i) < minToBeat) { // cannot win anymore
                f.inliers = in;
                f.error = Double.MAX_VALUE;
                return f;
            }
            apply(H, tx[i], ty[i], out);
            int j = grid.closest(out[0], out[1], tol, d);
            if (j >= 0 && stamp[j] != gen) {
                stamp[j] = gen;
                in++;
                err += d[0] * d[0];
            }
        }
        f.inliers = in;
        f.error = (in > 0 ? err / in : Double.MAX_VALUE);
        return f;
    }

    /**
     * Preference (used only to break ties) reproducing the historical
     * "phase" convention: with phase p, the corner assigned to the
     * theoretical top-left point is the one p steps (in the quadrilateral
     * ordering) after the corner closest to the origin of the picture.
     */
    private static boolean phaseMatches(double[] corners, int imgW, int imgH) {
        int closest = 0;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < 4; i++) {
            double dx = corners[2 * i], dy = corners[2 * i + 1];
            double d = dx * dx + dy * dy;
            if (d < min) { min = d; closest = i; }
        }
        int phase = ((ProcessImage._PHASE % 4) + 4) % 4;
        return closest == (4 - phase) % 4;
    }

    private static int[] sortAroundCentroid(final double[] px, final double[] py, int[] cand) {
        int h = cand.length;
        double cx = 0, cy = 0;
        for (int i : cand) { cx += px[i]; cy += py[i]; }
        cx /= h; cy /= h;
        Integer[] tmp = new Integer[h];
        for (int i = 0; i < h; i++) tmp[i] = cand[i];
        final double fcx = cx, fcy = cy;
        Arrays.sort(tmp, (i, j) -> Double.compare(Math.atan2(py[i] - fcy, px[i] - fcx),
                                                  Math.atan2(py[j] - fcy, px[j] - fcx)));
        int[] r = new int[h];
        for (int i = 0; i < h; i++) r[i] = tmp[i];
        return r;
    }
}
