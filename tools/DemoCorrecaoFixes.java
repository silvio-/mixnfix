package mixnfix.gui;

/*
 * Headless validation driver of the four fixes of the grading (correction)
 * module:
 *
 *   1) "Importar Alunos" makes the imported students visible to the grading
 *      module ("Adicionar Correção" -> tab "Alunos" -> button "+" ->
 *      "Adicionar Alunos" popup);
 *   2) after "Corrigir"/"Fechar" the names of the graded students show up
 *      immediately on the "Correções" tab, without restarting the application
 *      or selecting another exam;
 *   3) repeated copies of the exam of one student (repeated files, or several
 *      pictures of the same answer sheet) are discarded instead of producing
 *      repeated entries;
 *   4) "Relatório PDF" produces the PDF (utf8 input encoding, pdflatex command
 *      taken from the settings panel, no MiKTeX only -include-directory /
 *      -aux-directory options).
 *
 * It drives the *real* application classes (ModelMF, ModelInstituicao,
 * ModelProva, ModelProvaCorrecao, PanelProvaCorrecao, MFActionImportarAlunos,
 * MFActionColetarProvas, PanelProduzirRelatorio, ...) exactly the way the
 * interactive UI does and renders the real Swing components to JPG files under
 * /app/output, without a display (java.awt.headless).
 *
 * Sandbox adaptations (this machine has no display server, no LaTeX toolchain
 * and no /home/silvio/mixnfix/TesteMixnFix directory), all of them confined to
 * this driver, never to src/:
 *
 *   a) windows/dialogs are never made visible (that would throw a
 *      HeadlessException): the panels they contain (all plain JPanels) are
 *      built directly and painted off screen with Component.printAll(), the
 *      same technique of tools/GradingGUIRun.java and tools/DemoSubjetiva9.java;
 *   b) the pictures of the answered exams: the repository only ships two
 *      pictures (img1.jpg, img2.jpg) and both are the exam of the *same*
 *      student (matrícula 20250025918). The four files that are "chosen on the
 *      file chooser" are therefore prepared in a temporary directory that
 *      plays the role of /home/silvio/mixnfix/TesteMixnFix: two of them are the
 *      original pictures, two are copies of img2.jpg on which the marks of the
 *      "Identificação" field were repainted (through the very same cell map and
 *      homography the grading module uses) so that they are read as the exams
 *      of two other students of the imported list. One of the four files is a
 *      second copy of an exam already present in the batch, which is what
 *      exercises fix (3);
 *   c) pdflatex is replaced by mixnfix.gui.MiniLatexPdf (tools/MiniLatexPdf.java)
 *      exposed on the PATH as an executable named "pdflatex" by
 *      scripts/run_correcao_fixes_test.sh. It accepts exactly the options TeX
 *      Live's pdflatex accepts (and rejects -include-directory/-aux-directory
 *      as TeX Live does), refuses a latin1 encoded document holding UTF-8 text
 *      with the very error message reported ("! Package inputenc Error:
 *      Keyboard character used is undefined"), and typesets the report into a
 *      real PDF file. The .tex source it compiles is the one produced by the
 *      application itself.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import linsoft.gui.util.PanelChooseObjects;
import mixnfix.Model;
import mixnfix.modelo.Aluno;

public class DemoCorrecaoFixes {

    static File OUT_DIR = new File(System.getProperty("mixnfix.output.dir", "/app/output"));

    static final int APP_W = 1600, APP_H = 1000;

    // ------------------------------------------------------------------
    // headless rendering helpers (same as tools/DemoSubjetiva9.java)
    // ------------------------------------------------------------------

    static void layoutTree(Component c) {
        if (c instanceof Container) {
            Container container = (Container) c;
            container.doLayout();
            for (Component child : container.getComponents())
                layoutTree(child);
        }
    }

    static void prepare(JComponent c, int w, int h) {
        c.setSize(w, h);
        layoutTree(c);
        layoutTree(c); // split panes need a second pass to honour the divider
    }

    static void prepare(JComponent c) {
        Dimension d = c.getPreferredSize();
        prepare(c, Math.max(d.width, 10), Math.max(d.height, 10));
    }

    static void overlay(Graphics2D canvas, JComponent c, int x, int y) {
        Graphics2D g = (Graphics2D) canvas.create();
        g.translate(x, y);
        c.printAll(g);
        g.dispose();
    }

    static BufferedImage newCanvas(int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, w, h);
        g2.dispose();
        return img;
    }

    static void save(BufferedImage img, String name) throws Exception {
        OUT_DIR.mkdirs();
        File f = new File(OUT_DIR, name);
        ImageIO.write(img, "jpg", f);
        System.out.println("Wrote " + f.getAbsolutePath() + " (" + img.getWidth() + "x" + img.getHeight() + ")");
    }

    static void expandAll(javax.swing.JTree tree) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        if (root == null) return;
        expandAll(tree, new TreePath(((DefaultTreeModel) tree.getModel()).getPathToRoot(root)));
    }

    static void expandAll(javax.swing.JTree tree, TreePath path) {
        TreeNode node = (TreeNode) path.getLastPathComponent();
        for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
            TreeNode child = (TreeNode) e.nextElement();
            expandAll(tree, path.pathByAddingChild(child));
        }
        tree.expandPath(path);
    }

    static void selectNodeForUserObject(javax.swing.JTree tree, Object userObject) {
        MFTreeNode node = findNodeForUserObject((TreeNode) tree.getModel().getRoot(), userObject);
        if (node != null) {
            TreePath p = new TreePath(node.getPath());
            tree.setSelectionPath(p);
            tree.scrollPathToVisible(p);
        }
    }

    static MFTreeNode findNodeForUserObject(TreeNode node, Object userObject) {
        if (node instanceof MFTreeNode) {
            MFTreeNode mn = (MFTreeNode) node;
            if (mn.getUserObject() == userObject)
                return mn;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            MFTreeNode r = findNodeForUserObject(node.getChildAt(i), userObject);
            if (r != null) return r;
        }
        return null;
    }

    /** lets every pending Swing task (posted with invokeLater) run. */
    static void flushEDT() throws Exception {
        for (int i = 0; i < 3; i++)
            SwingUtilities.invokeAndWait(new Runnable() { public void run() {} });
    }

    /** a JPanel that looks like the modal dialog it would live in. */
    static JPanel fakeDialog(String title, JComponent content, int w, int h) {
        JPanel dialog = new JPanel(new BorderLayout());
        JLabel bar = new JLabel("  " + title);
        bar.setOpaque(true);
        bar.setBackground(new Color(60, 90, 140));
        bar.setForeground(Color.WHITE);
        bar.setFont(bar.getFont().deriveFont(Font.BOLD, 14f));
        bar.setPreferredSize(new Dimension(10, 26));
        dialog.add(bar, BorderLayout.NORTH);
        dialog.add(content, BorderLayout.CENTER);
        dialog.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        prepare(dialog, w, h);
        return dialog;
    }

    // ------------------------------------------------------------------
    // main
    // ------------------------------------------------------------------

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        OUT_DIR.mkdirs();

        App.getConfiguracao().setProperty(ConfiguracaoMIXnFIX.dbname, "db");
        App.getConfiguracao().setProperty(ConfiguracaoMIXnFIX.datadir, "data");
        new File("data").mkdirs();

        StringBuilder log = new StringBuilder();

        ModelMF modelMF = MainFrame.getModelMF();

        // the navigation tree of the left panel is built first, exactly like
        // MainFrame/PanelCadastro do, so that it follows every model change
        MFTree leftTree = new MFTree();
        leftTree.setFont(new Font("Tahoma", Font.PLAIN, 14));
        leftTree.setRowHeight(22);
        leftTree.setModel(new MFTreeModel(leftTree, modelMF));

        // =================================================================
        // "create a new folder" (Adicionar Pasta) + "Importar Prova"
        // =================================================================
        String folderName = "Pasta AVLC 2025";
        ModelInstituicao pasta = modelMF.getIntituicaoByName(folderName);
        if (pasta == null)
            pasta = modelMF.addInstituicao(folderName);
        log.append("Pasta (Instituicao): " + pasta.getInstituicao().getNome() + "\n");

        File provaFile = new File(System.getProperty("mixnfix.prova", "/app/AVLC-2-2025-EE3.prova"));
        List<Model> added = pasta.addProvas(new File[] { provaFile });
        ModelProva modelProva = (ModelProva) added.get(0);
        log.append("Prova importada: " + modelProva.getProva().getNome() + "\n");

        // "Adicionar Correção" -> nome "c1" -> OK
        ModelProvaCorrecao mpc = modelProva.addProvaCorrecao("c1");
        log.append("Correcao adicionada: " + mpc.getProvaCorrecao().getNome() + "\n");

        // =================================================================
        // the whole window: navigation tree on the left, and on the right the
        // panel of the correction "c1" (as if its name had been clicked)
        // =================================================================
        PanelProvaCorrecao panelCorrecao = new PanelProvaCorrecao(mpc);

        JScrollPane spLeft = new JScrollPane(leftTree);
        spLeft.setPreferredSize(new Dimension(380, 900));
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spLeft, panelCorrecao);
        mainSplit.setDividerLocation(380);

        JPanel appWindow = new JPanel(new BorderLayout());
        JLabel titleBar = new JLabel("  MIXnFIX, dbname: db, datadir: data");
        titleBar.setOpaque(true);
        titleBar.setBackground(new Color(60, 90, 140));
        titleBar.setForeground(Color.WHITE);
        titleBar.setFont(titleBar.getFont().deriveFont(Font.BOLD, 16f));
        titleBar.setPreferredSize(new Dimension(10, 30));
        appWindow.add(titleBar, BorderLayout.NORTH);
        appWindow.add(mainSplit, BorderLayout.CENTER);
        prepare(appWindow, APP_W, APP_H);

        expandAll(leftTree);
        selectNodeForUserObject(leftTree, mpc);
        // the lists of the correction take a good half of the panel
        for (JSplitPane sp : splitPanes(panelCorrecao))
            if (sp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT)
                sp.setDividerLocation(620);
        prepare(appWindow, APP_W, APP_H);

        // =================================================================
        // ITEM 1: right click on the folder -> "Importar Alunos" -> pick the
        // file -> OK. Then click the correction "c1", tab "Alunos", button "+"
        // and snapshot the "Adicionar Alunos" popup.
        // =================================================================
        File alunosFile = new File(System.getProperty("mixnfix.alunos", "/app/FAL-2-2025-Alunos.txt"));
        new MFActionImportarAlunos(pasta).matricularAlunos2(alunosFile);   // "Importar Alunos"
        log.append("Alunos importados de " + alunosFile.getName() + ": " + pasta.getAlunos().size() + "\n");

        expandAll(leftTree);
        prepare(appWindow, APP_W, APP_H);

        // tab "Alunos"
        JTabbedPane tabs = findTabbedPane(panelCorrecao);
        tabs.setSelectedIndex(1);
        prepare(appWindow, APP_W, APP_H);

        // button "+": content offered by the "Adicionar Alunos" dialog
        List<Aluno> disponiveis = panelCorrecao.getAlunosDisponiveis();
        log.append("\"Adicionar Alunos\" oferece " + disponiveis.size() + " alunos\n");
        if (disponiveis.isEmpty())
            log.append("*** FALHA: a lista de alunos importados nao chegou ao modulo de correcao\n");

        PanelChooseObjects pco = new PanelChooseObjects(new Object[0],
                                                        disponiveis.toArray(),
                                                        panelCorrecao.newAlunoListCellRenderer());
        JPanel dlgAlunos = fakeDialog("Adicionar Alunos", withOkCancel(pco), 900, 520);

        {
            BufferedImage img = newCanvas(APP_W, APP_H);
            Graphics2D g2 = img.createGraphics();
            overlay(g2, appWindow, 0, 0);
            Point p = SwingUtilities.convertPoint(panelCorrecao, 60, 90, appWindow);
            overlay(g2, dlgAlunos, p.x, p.y);
            g2.dispose();
            save(img, "correcao_fix1_adicionar_alunos.jpg");
        }

        // ">>" all of them and "OK": the students of the list become the
        // students of the correction (needed for the grading below)
        mpc.addAlunos(disponiveis);
        flushEDT();
        prepare(appWindow, APP_W, APP_H);
        log.append("Alunos da correcao c1: " + mpc.getNumAlunos() + "\n");

        // =================================================================
        // ITEM 2: "Corrigir" over four jpg files, then "Fechar"
        // =================================================================
        File fotosDir = new File(System.getProperty("mixnfix.fotos.dir", "/tmp/TesteMixnFix"));
        File fotos[] = FotosDeTeste.prepararFotos(modelProva.getProvaStructure(), fotosDir, log);

        log.append("Fotos escolhidas no seletor de arquivos (\"Corrigir\"):\n");
        for (File f : fotos)
            log.append("   " + f.getAbsolutePath() + "\n");

        MFActionColetarProvas corrigir = new MFActionColetarProvas(
            mpc, mixnfix.folharesposta.GeradorFolhaRespostas.ID);
        corrigir.coletar(fotos);   // the worker of the "Corrigir" button
        // "Fechar" closes the "Acompanhamento" dialog; the panel below is the
        // one that must already show the graded students.
        flushEDT();

        tabs.setSelectedIndex(0);  // tab "Correções"
        prepare(appWindow, APP_W, APP_H);
        flushEDT();
        // "click" the first graded exam, so the snapshot also shows its
        // correction (image of the answer sheet and detailed grade)
        JList listEntradas = listaDeEntradas(panelCorrecao);
        if (listEntradas != null && listEntradas.getModel().getSize() > 0)
            listEntradas.setSelectedIndex(0);
        flushEDT();
        prepare(appWindow, APP_W, APP_H);

        log.append("Entradas (provas corrigidas): " + mpc.getNumEntradas() + "\n");
        for (ModelEntradaProvaCorrecao m : mpc.getEntradasProvaCorrecaoOrdenadas())
            log.append("   (" + m.getAluno().getMatricula() + ") " + m.getAluno().getNome() + "\n");

        // what the lists of the panel show right after "Fechar" (this is what
        // used to stay stale until the panel was rebuilt)
        log.append("Lista \"Correções\" do painel, logo apos \"Fechar\": " +
                   listEntradas.getModel().getSize() + " itens\n");
        for (int i = 0; i < listEntradas.getModel().getSize(); i++) {
            ModelEntradaProvaCorrecao m = (ModelEntradaProvaCorrecao) listEntradas.getModel().getElementAt(i);
            log.append("   (" + m.getAluno().getMatricula() + ") " + m.getAluno().getNome() + "\n");
        }
        JList listSemEntrada = listaDeAlunosSemEntrada(panelCorrecao);
        log.append("Lista de alunos sem correcao do painel: " +
                   (listSemEntrada == null? -1: listSemEntrada.getModel().getSize()) +
                   " (esperado " + (mpc.getNumAlunos() - mpc.getNumAlunosComEntrada()) + ")\n");

        {
            BufferedImage img = newCanvas(APP_W, APP_H);
            Graphics2D g2 = img.createGraphics();
            overlay(g2, appWindow, 0, 0);
            g2.dispose();
            save(img, "correcao_fix2_e_fix3_apos_corrigir.jpg");
        }

        // =================================================================
        // ITEM 3: "Relatório PDF" -> the pdf goes to the output directory
        // =================================================================
        File pdfFile = new File(OUT_DIR, "relatorio_c1.pdf");
        PanelProduzirRelatorio panelRelatorio = new PanelProduzirRelatorio(mpc);
        panelRelatorio.setArquivoDeSaida(pdfFile.getAbsolutePath());
        prepare(panelRelatorio, 420, 470);

        File produced = panelRelatorio.produzirPDF();   // button "Produzir"
        if (produced != null && produced.exists())
            log.append("Relatorio PDF: " + produced.getAbsolutePath() + " (" + produced.length() + " bytes)\n");
        else
            log.append("*** FALHA: pdflatex nao produziu o relatorio\n");

        // evidence of fix (4): the command really used and the encoding really
        // declared by the generated LaTeX source
        File tex = new File(mixnfix.Controller.TMP_DIR, "relatorio_c1.tex");
        log.append("Comando de compilacao (painel de configuracao): " +
                   App.getConfiguracao().getCommandCompileTEX2PDF(
                       mixnfix.Controller.TMP_DIR, OUT_DIR.getAbsolutePath(), tex.getName()) + "\n");
        if (tex.exists()) {
            java.nio.file.Files.copy(tex.toPath(), new File(OUT_DIR, tex.getName()).toPath(),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            for (String l : new String(java.nio.file.Files.readAllBytes(tex.toPath()), "UTF-8").split("\n"))
                if (l.contains("inputenc"))
                    log.append("Fonte LaTeX (escrito em UTF-8): " + l + "\n");
        }

        {
            BufferedImage img = newCanvas(APP_W, APP_H);
            Graphics2D g2 = img.createGraphics();
            overlay(g2, appWindow, 0, 0);
            Point p = SwingUtilities.convertPoint(panelCorrecao, 260, 120, appWindow);
            overlay(g2, fakeDialog("Produzir Prova", panelRelatorio, 460, 500), p.x, p.y);
            g2.dispose();
            save(img, "correcao_fix4_relatorio_pdf.jpg");
        }

        PrintWriter pw = new PrintWriter(new File(OUT_DIR, "correcao_fixes_report.txt"), "UTF-8");
        pw.print(log.toString());
        pw.close();
        System.out.println(log.toString());

        System.out.println("DONE");
        System.exit(0);
    }

    /** the OK/Cancel buttons of DialgoChooseObjects, for the rendering. */
    static JComponent withOkCancel(JComponent content) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(content, BorderLayout.CENTER);
        JPanel buttons = new JPanel();
        javax.swing.JButton ok = new javax.swing.JButton("OK");
        javax.swing.JButton cancel = new javax.swing.JButton("Cancel");
        ok.setPreferredSize(new Dimension(70, 26));
        cancel.setPreferredSize(new Dimension(70, 26));
        buttons.add(ok);
        buttons.add(cancel);
        p.add(buttons, BorderLayout.SOUTH);
        return p;
    }

    /** every JSplitPane inside a container. */
    static List<JSplitPane> splitPanes(Container c) {
        List<JSplitPane> result = new ArrayList<JSplitPane>();
        collectSplitPanes(c, result);
        return result;
    }

    static void collectSplitPanes(Container c, List<JSplitPane> result) {
        for (Component child : c.getComponents()) {
            if (child instanceof JSplitPane)
                result.add((JSplitPane) child);
            if (child instanceof Container)
                collectSplitPanes((Container) child, result);
        }
    }

    /** the list of graded exams (entries) of the "Correções" tab. */
    static JList listaDeEntradas(Container c) {
        List<JList> lists = new ArrayList<JList>();
        collectLists(c, lists);
        for (JList l : lists) {
            if (l.getModel().getSize() > 0 &&
                l.getModel().getElementAt(0) instanceof ModelEntradaProvaCorrecao)
                return l;
        }
        return null;
    }

    /** the list of students with no graded exam yet ("Correções" tab, top). */
    static JList listaDeAlunosSemEntrada(Container c) {
        List<JList> lists = new ArrayList<JList>();
        collectLists(c, lists);
        for (JList l : lists) {
            if (l.getModel().getSize() > 0 && l.getModel().getElementAt(0) instanceof Aluno)
                return l;
        }
        return null;
    }

    static void collectLists(Container c, List<JList> result) {
        for (Component child : c.getComponents()) {
            if (child instanceof JList)
                result.add((JList) child);
            if (child instanceof Container)
                collectLists((Container) child, result);
        }
    }

    static JTabbedPane findTabbedPane(Container c) {
        for (Component child : c.getComponents()) {
            if (child instanceof JTabbedPane)
                return (JTabbedPane) child;
            if (child instanceof Container) {
                JTabbedPane r = findTabbedPane((Container) child);
                if (r != null) return r;
            }
        }
        return null;
    }

    static List<File> asList(File files[]) {
        List<File> l = new ArrayList<File>();
        for (File f : files) l.add(f);
        return l;
    }
}
