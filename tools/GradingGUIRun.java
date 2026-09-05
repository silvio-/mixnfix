package mixnfix.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.zip.ZipFile;

import mixnfix.prova.Parser;
import mixnfix.prova.ProvaStructure;

/**
 * Headless driver of the grading module.
 *
 * Loads an exam (.prova) and the pictures of the answered exams, then, for
 * each picture, calls the routines associated with the "Corrigir" and the
 * "ID &amp; Code" buttons of the grading GUI and saves:
 *
 *   /app/output/<picture>.jpg                 composed verification image
 *   /app/output/gui_<picture>_corrigir.jpg    GUI after "Corrigir"
 *   /app/output/gui_<picture>_id_code.jpg     GUI after "ID & Code"
 *   /app/output/grading_cpu_times.txt         cpu times
 */
public class GradingGUIRun {

    static ProvaStructure loadProva(String provaFile) throws Exception {
        File tmp = File.createTempFile("prova", ".xml");
        ZipFile zf = new ZipFile(provaFile);
        InputStream in = zf.getInputStream(zf.getEntry("prova.xml"));
        FileOutputStream out = new FileOutputStream(tmp);
        byte buf[] = new byte[8192];
        int r;
        while ((r = in.read(buf)) > 0) out.write(buf, 0, r);
        out.close(); in.close(); zf.close();
        Parser p = new Parser(tmp.getAbsolutePath());
        tmp.delete();
        return p.getProva();
    }

    static void configure() {
        // parameters of the image processing (the ones shown on the GUI)
        String v[][] = {
            {"thresholds", "50,60,70,80,90,100,110,120"},
            {"minPixelWidth", "2"}, {"maxPixelWidth", "15"},
            {"minPixelHeight", "2"}, {"maxPixelHeight", "15"},
            {"minNumPixels", "4"}, {"maxNumPixels", "120"},
            {"minPixelDensity", "0.4"},
            {"numClosest", "2"},
            {"minSide", "150"},
            {"angletol", "5"},
            {"targetRadius", "30"},
            {"correctSideRatio", "1.0"},
            {"sideRatioTolerance", "0.9"},
            {"phase", "0"},
            {"controlPointsRadius", "20"},
            {"gaplevel", "20.0"},
            {"separationlevel", "100.0"},
            {"searchRegionLeftMargin", "0"}, {"searchRegionRightMargin", "1"},
            {"searchRegionTopMargin", "0"}, {"searchRegionBottomMargin", "1"}
        };
        for (String[] kv : v)
            App.setProperty(kv[0], kv[1]);
    }

    /** short description of the sample pictures of the repository. */
    static String describe(String base) {
        if ("img1".equals(base)) return "severely deformed exam";
        if ("img2".equals(base)) return "weakly deformed exam";
        return "exam picture";
    }

    public static void main(String args[]) throws Exception {
        System.setProperty("java.awt.headless", "true");
        configure();

        ProvaStructure prova = loadProva(args[0]);
        PanelCalibragemCorrecao panel = new PanelCalibragemCorrecao(prova);

        File outDir = new File(PanelCalibragemCorrecao.getOutputDir());
        outDir.mkdirs();

        ThreadMXBean mx = ManagementFactory.getThreadMXBean();
        StringBuilder report = new StringBuilder();
        report.append("MIXnFIX grading module - control point detection and grading times\n");
        report.append("(machine: " + System.getProperty("os.name") + ", java " + System.getProperty("java.version") + ")\n\n");

        for (int a = 1; a < args.length; a++) {
            File foto = new File(args[a]);
            String base = foto.getName().replaceAll("\\.[^.]*$", "");
            panel.setFoto(foto);

            // warm up the JIT so that the reported time is the steady
            // state one (mass grading processes hundreds of exams)
            panel.corrigir();
            panel.findIDandMFCode();

            long cpu0 = mx.getCurrentThreadCpuTime();
            long wall0 = System.nanoTime();
            panel.corrigir();                       // "Corrigir" button
            long cpu1 = mx.getCurrentThreadCpuTime();
            long wall1 = System.nanoTime();

            panel.saveGUIImage("gui_" + base + "_corrigir", 1280, 900);

            long cpu2 = mx.getCurrentThreadCpuTime();
            long wall2 = System.nanoTime();
            panel.findIDandMFCode();                // "ID & Code" button
            long cpu3 = mx.getCurrentThreadCpuTime();
            long wall3 = System.nanoTime();

            panel.saveGUIImage("gui_" + base + "_id_code", 1280, 900);

            double corrigirCpu = (cpu1 - cpu0) / 1.0e6;
            double corrigirWall = (wall1 - wall0) / 1.0e6;
            double idCpu = (cpu3 - cpu2) / 1.0e6;
            double idWall = (wall3 - wall2) / 1.0e6;

            report.append(String.format("%s   (%s)%n", foto.getAbsolutePath(), describe(base)));
            report.append(String.format("  control points located: %d/21 dots, threshold %d, rms %.2f pixels%n",
                                        mixnfix.ProcessImage._lastFitControlPoints,
                                        mixnfix.ProcessImage._lastFitThreshold,
                                        mixnfix.ProcessImage._lastFitRms));
            report.append(String.format("  Corrigir  (control point detection + fit): cpu %8.2f ms   wall %8.2f ms%n", corrigirCpu, corrigirWall));
            report.append(String.format("  ID & Code (sampling + decoding)          : cpu %8.2f ms   wall %8.2f ms%n", idCpu, idWall));
            report.append(String.format("  total grading                            : cpu %8.2f ms   wall %8.2f ms%n%n",
                                        corrigirCpu + idCpu, corrigirWall + idWall));
        }

        // a copy of the last composed frame with the canonical name
        try {
            File last = new File(outDir, new File(args[args.length - 1]).getName().replaceAll("\\.[^.]*$", "") + ".jpg");
            File canonical = new File(outDir, "composed_result.jpg");
            if (last.exists())
                java.nio.file.Files.copy(last.toPath(), canonical.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
        catch (Exception ex) { ex.printStackTrace(); }

        PrintWriter pw = new PrintWriter(new File(outDir, "grading_cpu_times.txt"), "UTF-8");
        pw.print(report.toString());
        pw.close();
        System.out.println(report.toString());
    }
}
