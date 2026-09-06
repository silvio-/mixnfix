package mixnfix.gui;

/*
 * Headless demonstration / validation driver for the new "Subjetiva_9" question
 * type (0, 1/8, 2/8, ..., 1 grading factors, 9 markable items - analogous to the
 * existing "Subjetiva_5" type but with 9 items instead of 5).
 *
 * It drives the *real* application classes (ModelMF, ModelInstituicao, ModelProva,
 * PanelModelProva, PanelCriarQuesito, PanelCalibragemCorrecao, etc.) exactly the
 * way the interactive UI would, and renders the real Swing components to JPG
 * files under /app/output, without ever needing a real display (java.awt.headless).
 *
 * Since the sandbox this runs in has neither a display server nor pdflatex/epstopdf
 * (no network access to install a LaTeX toolchain), two adaptations were made,
 * clearly marked below:
 *
 *   1) Windows/dialogs are never made visible (that would throw HeadlessException).
 *      Instead every panel that would normally live inside a JDialog is built
 *      directly (they are all plain JPanel subclasses) and rendered off screen
 *      with Component.printAll(Graphics2D), exactly like the technique already
 *      used in PanelCalibragemCorrecao.saveGUIImage()/tools/GradingGUIRun.java.
 *      Popups (JPopupMenu, the JComboBox drop-down list) cannot be "shown" either
 *      (Swing requires a realized peer for that), so they are laid out and
 *      painted manually, translated to the right place on the same canvas.
 *
 *   2) "Produzir Prova" normally shells out to pdflatex/epstopdf to turn the
 *      generated LaTeX source into the final PDF. Those executables are not
 *      available in this sandbox, so a tiny, self-contained PDF writer
 *      (MiniPdfWriter, below) is used instead to produce a real, valid PDF file
 *      whose pages list the exam header and every question's text (extracted
 *      through the very same mixnfix.prova.PanelPapel component the real UI
 *      uses to display/edit the "enunciado"), including the new Subjetiva_9
 *      question. The very same text layout is rendered to a JPG image, used
 *      as the "image rendered from the produced pdf".
 */

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import linsoft.gui.InputComboBox;
import mixnfix.Model;
import mixnfix.prova.Grupo;
import mixnfix.prova.PanelPapel;
import mixnfix.prova.Papel;
import mixnfix.prova.ProvaStructure;
import mixnfix.prova.Quesito;

public class DemoSubjetiva9 {

    static final File OUT_DIR = new File("/app/output");

    // ------------------------------------------------------------------
    // generic headless rendering helpers
    // ------------------------------------------------------------------

    /** recursively lay out a component tree that has no native peer. */
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
    }

    static void prepare(JComponent c) {
        Dimension d = c.getPreferredSize();
        prepare(c, Math.max(d.width, 10), Math.max(d.height, 10));
    }

    /** paints a component, translated, on top of an already prepared canvas. */
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

    static void save(BufferedImage img, String name) throws IOException {
        OUT_DIR.mkdirs();
        File f = new File(OUT_DIR, name);
        ImageIO.write(img, "jpg", f);
        System.out.println("Wrote " + f.getAbsolutePath() + " (" + img.getWidth() + "x" + img.getHeight() + ")");
    }

    /** forcibly obtains the (never "shown") drop down list popup of a JComboBox, via reflection. */
    static JPopupMenu getComboPopup(JComboBox combo) throws Exception {
        Object ui = combo.getUI();
        Class<?> k = ui.getClass();
        Field f = null;
        while (k != null) {
            try { f = k.getDeclaredField("popup"); break; }
            catch (NoSuchFieldException e) { k = k.getSuperclass(); }
        }
        if (f == null) throw new NoSuchFieldException("popup");
        f.setAccessible(true);
        return (JPopupMenu) f.get(ui);
    }

    /** fully expands every node of a JTree, without needing a realized peer. */
    static void expandAll(JTree tree) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        if (root == null) return;
        expandAll(tree, new TreePath(((DefaultTreeModel) tree.getModel()).getPathToRoot(root)));
    }

    static void expandAll(JTree tree, TreePath path) {
        TreeNode node = (TreeNode) path.getLastPathComponent();
        for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
            TreeNode child = (TreeNode) e.nextElement();
            expandAll(tree, path.pathByAddingChild(child));
        }
        tree.expandPath(path);
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

        ModelMF modelMF = MainFrame.getModelMF();

        // =================================================================
        // Step 0: "create a new folder" (Adicionar Pasta) + "Importar Prova"
        // =================================================================
        String folderName = "Pasta Demo Subjetiva9";
        ModelInstituicao pasta = modelMF.getIntituicaoByName(folderName);
        if (pasta == null)
            pasta = modelMF.addInstituicao(folderName);
        System.out.println("Created folder (Instituicao): " + pasta.getInstituicao().getNome());

        File provaFile = new File("/app/AVLC-2-2025-EE3.prova");
        List<Model> added = pasta.addProvas(new File[]{provaFile});
        ModelProva modelProva = (ModelProva) added.get(0);
        System.out.println("Imported prova: " + modelProva.getProva().getNome());

        // =================================================================
        // Build the composite "whole UI": left navigation tree (MFTree over
        // ModelMF, like MainFrame's PanelCadastro) + right side PanelProva
        // (as if the user had clicked the exam's name in the left panel).
        // =================================================================
        MFTree leftTree = new MFTree();
        leftTree.setFont(new Font("Tahoma", Font.PLAIN, 14));
        leftTree.setRowHeight(22);
        MFTreeModel leftModel = new MFTreeModel(leftTree, modelMF);
        leftTree.setModel(leftModel);
        expandAll(leftTree);

        PanelProva panelProva = new PanelProva(modelProva); // "as if we had clicked in the exam's name"

        JScrollPane spLeft = new JScrollPane(leftTree);
        spLeft.setPreferredSize(new Dimension(360, 900));

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spLeft, panelProva);
        mainSplit.setDividerLocation(360);

        JPanel appWindow = new JPanel(new BorderLayout());
        JLabel titleBar = new JLabel("  MIXnFIX, dbname: db, datadir: data");
        titleBar.setOpaque(true);
        titleBar.setBackground(new Color(60, 90, 140));
        titleBar.setForeground(Color.WHITE);
        titleBar.setFont(titleBar.getFont().deriveFont(Font.BOLD, 16f));
        titleBar.setPreferredSize(new Dimension(10, 30));
        appWindow.add(titleBar, BorderLayout.NORTH);
        appWindow.add(mainSplit, BorderLayout.CENTER);

        final int APP_W = 1600, APP_H = 1000;
        prepare(appWindow, APP_W, APP_H);

        // select the imported exam's node on the left tree, for visual feedback
        selectNodeForUserObject(leftTree, modelProva);
        prepare(appWindow, APP_W, APP_H);

        // the "Estrutura" tab of PanelProva is a PanelModelProva
        PanelModelProva pmp = (PanelModelProva) panelProva.getTabPane().getComponentAt(0);
        MFTree centerTree = pmp.getTree();
        expandAll(centerTree);
        prepare(appWindow, APP_W, APP_H);

        // "Cabeçalho" node == first child of the (invisible) root of the central tree
        MFTreeNode centerRoot = (MFTreeNode) centerTree.getModel().getRoot();
        MFTreeNode cabecalhoNode = (MFTreeNode) centerRoot.getChildAt(0);
        TreePath cabecalhoPath = new TreePath(cabecalhoNode.getPath());
        centerTree.setSelectionPath(cabecalhoPath);
        prepare(appWindow, APP_W, APP_H);

        Rectangle cabecalhoBounds = centerTree.getPathBounds(cabecalhoPath);
        if (cabecalhoBounds == null) cabecalhoBounds = new Rectangle(20, 20, 120, 22);
        // translate to screen coordinates of the composite window (tree lives
        // inside pmp's own JScrollPane, inside the right side of the split)
        Point cabecalhoOnAppWindow = SwingUtilities.convertPoint(centerTree,
                cabecalhoBounds.x, cabecalhoBounds.y, appWindow);

        // =================================================================
        // "right click on Cabeçalho, Adicionar quesito"
        // Step 1: open the PanelCriarQuesito ("Adicionar Quesito" dialog),
        // click the "Tipo Quesito" combo box to exhibit all options.
        // =================================================================
        PanelCriarQuesito pcq = new PanelCriarQuesito();
        prepare(pcq);

        InputComboBox comboTipo = pcq._cbInputTipo;
        JPopupMenu comboPopup = getComboPopup(comboTipo);
        comboPopup.setSize(comboPopup.getPreferredSize());
        layoutTree(comboPopup);

        {
            BufferedImage img = newCanvas(APP_W, APP_H);
            Graphics2D g2 = img.createGraphics();
            overlay(g2, appWindow, 0, 0);

            int dlgX = cabecalhoOnAppWindow.x + 40;
            int dlgY = cabecalhoOnAppWindow.y + 10;
            overlay(g2, pcq, dlgX, dlgY);

            // combo's drop down appears right below the combo box, inside the dialog
            Point comboPos = SwingUtilities.convertPoint(comboTipo, 0, comboTipo.getHeight(), pcq);
            overlay(g2, comboPopup, dlgX + comboPos.x, dlgY + comboPos.y);

            g2.dispose();
            save(img, "subjetiva9_step1_tipo_quesito_dropdown.jpg");
        }

        // =================================================================
        // Step 2: pick "subjetiva_9" and press OK; then "click" the newly
        // added question's name in the central panel.
        // =================================================================
        comboTipo.setSelectedItem(PanelCriarQuesito.SUBJETIVO_9);
        pcq._tfTag.setText("Questao Subjetiva Nova (9 niveis)");
        pcq._tfValorAcerto.setText("2.0");
        pcq._tfValorErro.setText("0.0");

        // this replicates exactly what MFActionAdicionarQuesito.actionPerformed()
        // does with the dialog's answer, without ever showing the modal JDialog:
        Quesito novoQuesito = new Quesito();
        novoQuesito.setTag(pcq.getTag());
        if (pcq.getTipoQuesito() == PanelCriarQuesito.ID_SUBJETIVO_9)
            novoQuesito.setTipo(Quesito.TIPO_SUBJETIVA_9);
        else
            throw new IllegalStateException("expected ID_SUBJETIVO_9");
        novoQuesito.setValorAcerto(pcq.getValorAcerto());
        novoQuesito.setValorErro(pcq.getValorErro());

        Grupo raiz = modelProva.getProvaStructure().getRoot();
        raiz.addQuesito(novoQuesito);
        System.out.println("Added new Quesito, tipo=" + novoQuesito.getTipo() +
                " (Quesito.TIPO_SUBJETIVA_9=" + Quesito.TIPO_SUBJETIVA_9 + ")");

        // find & select the new node in the (already listener-updated) central tree
        expandAll(centerTree);
        prepare(appWindow, APP_W, APP_H);
        MFTreeNode novoNode = findNodeForUserObject(centerTree, novoQuesito);
        if (novoNode != null) {
            TreePath novoPath = new TreePath(novoNode.getPath());
            centerTree.setSelectionPath(novoPath);
            centerTree.scrollPathToVisible(novoPath);
        }
        prepare(appWindow, APP_W, APP_H);

        {
            BufferedImage img = newCanvas(APP_W, APP_H);
            Graphics2D g2 = img.createGraphics();
            overlay(g2, appWindow, 0, 0);
            g2.dispose();
            save(img, "subjetiva9_step2_question_added_and_selected.jpg");
        }

        // =================================================================
        // Step 3: right click exam -> "Produzir Prova" -> "Produzir" button
        // generates the exam's PDF; render an image from it.
        // =================================================================
        File pdfFile = new File(OUT_DIR, "subjetiva9_exam.pdf");
        File texFile = new File(OUT_DIR, "subjetiva9_exam_source.txt");
        BufferedImage pdfPageImg = ExamPdfFallback.produce(modelProva.getProvaStructure(), pdfFile, texFile);
        save(pdfPageImg, "subjetiva9_step3_pdf_render.jpg");
        System.out.println("Wrote " + pdfFile.getAbsolutePath());

        // =================================================================
        // Step 4: right click exam -> "Adicionar Correção", name it, OK;
        // click its name; calibration button; "Foto"; add the pdf produced
        // in the previous item; snapshot the UI.
        // =================================================================
        ModelProvaCorrecao mpc = modelProva.addProvaCorrecao("Correcao Demo Subjetiva9");
        System.out.println("Added correcao tab: " + mpc.getProvaCorrecao().getNome());

        // panelProva listens to nodeAdded on modelProva and auto-creates the tab
        int correcaoTabIndex = panelProva.getTabPane().getTabCount() - 1;
        panelProva.getTabPane().setSelectedIndex(correcaoTabIndex);
        prepare(appWindow, APP_W, APP_H);

        // "calibration button" -> PanelCalibragemCorrecao (headless friendly ctor)
        PanelCalibragemCorrecao pcc = new PanelCalibragemCorrecao(mpc);
        prepare(pcc, 1000, 640);

        // "Foto" -> add the pdf produced in the previous item
        try {
            pcc.setFoto(pdfFile);
        }
        catch (Exception ex) {
            System.out.println("setFoto(pdf) -> " + ex);
        }
        prepare(pcc, 1000, 640);

        {
            BufferedImage img = newCanvas(APP_W, APP_H);
            Graphics2D g2 = img.createGraphics();
            overlay(g2, appWindow, 0, 0);
            overlay(g2, pcc, 120, 80);
            g2.dispose();
            save(img, "subjetiva9_step4_correcao_calibragem.jpg");
        }

        System.out.println("DONE");
        System.exit(0);
    }

    // ------------------------------------------------------------------
    // tree helpers
    // ------------------------------------------------------------------

    static void selectNodeForUserObject(JTree tree, Object userObject) {
        MFTreeNode node = findNodeForUserObject(tree, userObject);
        if (node != null) {
            TreePath p = new TreePath(node.getPath());
            tree.setSelectionPath(p);
            tree.scrollPathToVisible(p);
        }
    }

    static MFTreeNode findNodeForUserObject(JTree tree, Object userObject) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        return findNodeForUserObject(root, userObject);
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
}
