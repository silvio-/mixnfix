package mixnfix.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

import jxl.write.WriteException;
import linsoft.gui.util.DialgoChooseObjects;
import linsoft.gui.util.PanelChooseObjects;
import linsoft.gui.util.ViewEspera;
import mixnfix.Controller;
import mixnfix.Library;
import mixnfix.MFI2Java;
import mixnfix.Model;
import mixnfix.composicaoprova.Prova2TeX;
import mixnfix.extRepositorio.ExtensaoRepositorio;
import mixnfix.folharesposta.BinaryField;
import mixnfix.folharesposta.Cell;
import mixnfix.folharesposta.CellMap;
import mixnfix.folharesposta.ControlPoint;
import mixnfix.folharesposta.Field;
import mixnfix.folharesposta.IFolhaResposta;
import mixnfix.folharesposta.MultiField;
import mixnfix.folharesposta.OptionField;
import mixnfix.folharesposta.Quadrilateral;
import mixnfix.modelo.Aluno;
import mixnfix.modelo.AlunoTurma;
import mixnfix.modelo.Instituicao;
import mixnfix.modelo.Prova;
import mixnfix.prova.Grupo;
import mixnfix.prova.Imagem;
import mixnfix.prova.ItemQuesito;
import mixnfix.prova.NoProva;
import mixnfix.prova.NoProvaPermutavel;
import mixnfix.prova.Papel;
import mixnfix.prova.ProvaStructure;
import mixnfix.prova.Quesito;
import mixnfix.reports.ModelInstituicaoReport;
import mixnfix.reports.ModelProvaCorrecaoReport;
import mixnfix.reports.ModelProvaReport;


public class MFActions {
    public MFActions() {
    }
}

class MFActionAddCurso
    extends AbstractAction {
    Instituicao _instituicao;
    public MFActionAddCurso(Instituicao i) {
        super("Adicionar Curso", Images.Instituicao16x16);
        _instituicao = i;
    }

    public void actionPerformed(ActionEvent e) {
        Object result = JOptionPane.showInputDialog("Novo Curso");
        if (result == null)
            return;
        try {
            App.getRepositorio().inserirCurso( (String) result, _instituicao);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

class MFActionRelatorioFaltas
    extends AbstractAction {
    ModelInstituicao _model;
    public MFActionRelatorioFaltas(ModelInstituicao model) {
        super("Gerar Relatório de Faltas", Images.Instituicao16x16);
        _model = model;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            String report = _model.getRelatoriosFalta(_model);
            System.out.println(report);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

class MFActionAddProvaCorrecao
    extends AbstractAction {
    ModelProva _modelProva;
    public MFActionAddProvaCorrecao(ModelProva p) {
        super("Adicionar Correção", Images.Correcao16x16);
        _modelProva = p;
    }

    public void actionPerformed(ActionEvent e) {
        Object result = JOptionPane.showInputDialog("Nome da Correção");
        String nome = (String) result;
        if (nome == null)
            return;
        try {
            if (_modelProva.getProvaCorrecaoByName(nome) != null) {
                JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, "Já existe correção com nome "+nome, "Mensagem", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            _modelProva.addProvaCorrecao( (String) result);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

class MFActionRemoverCorrecoesProva
    extends AbstractAction {
    ModelProva _modelProva;
    public MFActionRemoverCorrecoesProva(ModelProva modelProva) {
        super("Remover Correções", Images.Prova16x16);
        _modelProva = modelProva;
    }

    public void actionPerformed(ActionEvent e) {
        if (JOptionPane.showConfirmDialog(MainFrame.MAIN_FRAME, "Deseja remover todas as correções da prova " +
                                          _modelProva.getProva().getNome() + "?",
                                          "Confirmação", JOptionPane.YES_NO_OPTION) ==
            JOptionPane.NO_OPTION)
            return;
        try {
            ArrayList<ModelProvaCorrecao> correcoes = _modelProva.getProvasCorrecoes();

            for (ModelProvaCorrecao correcao : correcoes)
                correcao.remover(false);
        }
        catch (IOException ex1) {
            ex1.printStackTrace();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        catch (RuntimeException rte) {
            rte.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, rte.getMessage());
        }
    }
}

class MFActionRemoverColetasProva
    extends AbstractAction {
    ModelProva _modelProva;
    public MFActionRemoverColetasProva(ModelProva modelProva) {
        super("Remover Coletas", Images.Prova16x16);
        _modelProva = modelProva;
    }

    public void actionPerformed(ActionEvent e) {
        if (JOptionPane.showConfirmDialog(MainFrame.MAIN_FRAME, "Deseja remover todas as coletas da prova " +
                                          _modelProva.getProva().getNome() + "?",
                                          "Confirmação", JOptionPane.YES_NO_OPTION) ==
            JOptionPane.NO_OPTION)
            return;
        try {
            ArrayList<ModelColetaQuestionario> coletas = _modelProva.getColetasQuestionario();

            for (ModelColetaQuestionario coleta : coletas)
                coleta.remover(false);
        }
        catch (IOException ex1) {
            ex1.printStackTrace();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        catch (RuntimeException rte) {
            rte.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, rte.getMessage());
        }
    }
}

class MFActionRemoverProva
    extends AbstractAction {
    ModelProva _modelProva;
    public MFActionRemoverProva(ModelProva modelProva) {
        super("Remover Prova", Images.Prova16x16);
        _modelProva = modelProva;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            if (JOptionPane.showConfirmDialog(MainFrame.MAIN_FRAME, "Deseja remover prova " +
                                              _modelProva.getProva().getNome() + "?",
                                              "Confirmação", JOptionPane.YES_NO_OPTION) ==
                JOptionPane.NO_OPTION)
                return;

            _modelProva.remover(false);
        }
        catch (IOException ex1) {
            ex1.printStackTrace();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        catch (RuntimeException rte) {
            rte.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, rte.getMessage());
        }
    }
}


class MFActionRemoverProvaRecursivamente
    extends AbstractAction {
    ModelProva _modelProva;
    public MFActionRemoverProvaRecursivamente(ModelProva modelProva) {
        super("Remover Prova Recursivamente", Images.Prova16x16);
        _modelProva = modelProva;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            if (JOptionPane.showConfirmDialog(MainFrame.MAIN_FRAME, "Deseja remover prova " +
                                              _modelProva.getProva().getNome() + "?" +
                                              " Toda a hierarquia será apagada.",
                                              "Confirmação", JOptionPane.YES_NO_OPTION) ==
                JOptionPane.NO_OPTION)
                return;

            _modelProva.remover(true);
        }
        catch (IOException ex1) {
            ex1.printStackTrace();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        catch (RuntimeException rte) {
            rte.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, rte.getMessage());
        }
    }
}


class MFActionRemoverProvaCorrecao
    extends AbstractAction {
    ModelProvaCorrecao _modelProvaCorrecao;
    public MFActionRemoverProvaCorrecao(ModelProvaCorrecao modelProvaCorrecao) {
        super("Remover Correção", Images.Prova16x16);
        _modelProvaCorrecao = modelProvaCorrecao;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            if (JOptionPane.showConfirmDialog(MainFrame.MAIN_FRAME, "Deseja remover correção " +
                                              _modelProvaCorrecao.getProvaCorrecao().getNome() + "?",
                                              "Confirmação", JOptionPane.YES_NO_OPTION) ==
                JOptionPane.NO_OPTION)
                return;
            _modelProvaCorrecao.remover(false);
        }
        catch (IOException ex1) {
            ex1.printStackTrace();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        catch (RuntimeException rte) {
            rte.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, rte.getMessage());
        }
    }
}


class MFActionRemoverProvaCorrecaoRecursivamente
    extends AbstractAction {
    ModelProvaCorrecao _modelProvaCorrecao;
    public MFActionRemoverProvaCorrecaoRecursivamente(ModelProvaCorrecao modelProvaCorrecao) {
        super("Remover Correção Recursivamente", Images.Prova16x16);
        _modelProvaCorrecao = modelProvaCorrecao;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            if (JOptionPane.showConfirmDialog(MainFrame.MAIN_FRAME, "Deseja remover correção " +
                                              _modelProvaCorrecao.getProvaCorrecao().getNome() + "?" +
                                              " Toda a hierarquia será apagada.",
                                              "Confirmação", JOptionPane.YES_NO_OPTION) ==
                JOptionPane.NO_OPTION)
                return;
            _modelProvaCorrecao.remover(true);

        }
        catch (IOException ex1) {
            ex1.printStackTrace();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        catch (RuntimeException rte) {
            rte.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, rte.getMessage());
        }
    }
}


class MFActionRenomearProva
    extends AbstractAction {
    Prova _prova;
    public MFActionRenomearProva(Prova prova) {
        super("Renomear Prova", Images.Prova16x16);
        _prova = prova;
    }

    public void actionPerformed(ActionEvent e) {
        String result = JOptionPane.showInputDialog(MainFrame.MAIN_FRAME, "Novo nome para a prova", _prova.getNome());
        if (result == null)
            return;
        try {
            _prova.setNome(result);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

class MFActionRenomearProvaCorrecao
    extends AbstractAction {
    ModelProvaCorrecao _model;
    public MFActionRenomearProvaCorrecao(ModelProvaCorrecao m) {
        super("Renomear Correção", Images.Prova16x16);
        _model = m;
    }

    public void actionPerformed(ActionEvent e) {
        String result = JOptionPane.showInputDialog(MainFrame.MAIN_FRAME, "Novo nome para a correção", _model.getProvaCorrecao().getNome());
        if (result == null)
            return;
        try {
            _model.getProvaCorrecao().setNome(result);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

class MFActionRemoverInstituicaoRecursivamente
    extends AbstractAction {
    ModelInstituicao _instituicao;

    public MFActionRemoverInstituicaoRecursivamente(ModelInstituicao  instituicao) {
        super("Remover Pasta Recursivamente", Images.Instituicao16x16);
        _instituicao = instituicao;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            if (JOptionPane.showConfirmDialog(MainFrame.MAIN_FRAME, "Deseja remover pasta " +
                                              _instituicao.getInstituicao().getNome() + "?" +
                                              " Toda a hierarquia será apagada.",
                                              "Confirmação", JOptionPane.YES_NO_OPTION) ==
                JOptionPane.NO_OPTION)
                return;

            _instituicao.remover(true);
        }
        catch (SQLException sqle) {
            System.out.println("Não foi possível remover pasta: "+_instituicao.getInstituicao().getNome()+ " SQL Error");
            sqle.printStackTrace();
        }
        catch (IOException ioe) {
            System.out.println("Não foi possível remover pasta: "+_instituicao.getInstituicao().getNome()+ " IO Error");
            ioe.printStackTrace();
        }
        catch (RuntimeException rte) {
            rte.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, rte.getMessage());
        }
    }
}


class MFActionRemoverInstituicao
    extends AbstractAction {
    ModelInstituicao _instituicao;

    public MFActionRemoverInstituicao(ModelInstituicao  instituicao) {
        super("Remover Pasta", Images.Instituicao16x16);
        _instituicao = instituicao;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            if (JOptionPane.showConfirmDialog(MainFrame.MAIN_FRAME, "Deseja remover pasta " +
                                              _instituicao.getInstituicao().getNome() + "?",
                                              "Confirmação", JOptionPane.YES_NO_OPTION) ==
                JOptionPane.NO_OPTION)
                return;

            // MainFrame.getModelMF().removerInstituicao(_instituicao);
            _instituicao.remover(false);
        }
        catch (SQLException sqle) {
            System.out.println("Não foi possível remover pasta: "+_instituicao.getInstituicao().getNome()+ " SQL Error");
            sqle.printStackTrace();
        }
        catch (IOException ioe) {
            System.out.println("Não foi possível remover pasta: "+_instituicao.getInstituicao().getNome()+ " IO Error");
            ioe.printStackTrace();
        }
        catch (RuntimeException rte) {
            rte.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, rte.getMessage());
        }
    }
}


class MFActionImportarProvas
    extends AbstractAction {

    ModelInstituicao _modelInstituicao;
    public MFActionImportarProvas(ModelInstituicao modelInstituicao) {
        super("Importar Provas", Images.Prova16x16);
        _modelInstituicao = modelInstituicao;
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        fc.setSelectedFile(new File(App.getProperty("provasdir")));

        fc.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                return f.getName().endsWith(".prova");
            }

            public String getDescription() {
                return "Provas (.prova)";
            }
        });

        int r = fc.showOpenDialog(MainFrame.MAIN_FRAME);
        if (r == JFileChooser.APPROVE_OPTION) {
            App.setProperty("provasdir", fc.getSelectedFile().getAbsolutePath());
            try {
                _modelInstituicao.addProvas(fc.getSelectedFiles());
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}

class MFActionNovaProva
    extends AbstractAction {

    ModelInstituicao _modelInstituicao;
    public MFActionNovaProva(ModelInstituicao modelInstituicao) {
        super("Criar Nova Prova", Images.Prova16x16);
        _modelInstituicao = modelInstituicao;
    }

    public void actionPerformed(ActionEvent e) {
        String nome = (String) JOptionPane.showInputDialog(MainFrame.MAIN_FRAME, "Nome da nova prova");
        if (nome == null)
            return;
        try {
            if (_modelInstituicao.getProvaByName(nome) != null) {
                JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, "Já existe prova com nome "+nome, "Mensagem", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            _modelInstituicao.addNewProva(nome);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

class MFActionLockNoProvaPermutavel
    extends AbstractAction {

    NoProvaPermutavel _np;

    public MFActionLockNoProvaPermutavel(NoProvaPermutavel np) {
        super("Travar/Destravar", Images.Prova16x16);
        _np = np;
    }

    public void actionPerformed(ActionEvent e) {
        if (_np.isTravado())
            _np.setTravado(false);
        else
            _np.setTravado(true);
    }

}

class MFActionImportarAlunos
    extends AbstractAction {

    ModelInstituicao _modelInstituicao;
    public MFActionImportarAlunos(ModelInstituicao instituicao) {
        super("Importar Alunos", Images.Turma16x16);
        _modelInstituicao = instituicao;
    }


    private void matricularAlunos2(File f) {
        try {
            ArrayList<Aluno> alunos = new ArrayList<Aluno> ();
            ArrayList<AlunoTurma> alunosTurmas = new ArrayList<AlunoTurma> ();

            
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "utf-8"));
            
            // BufferedReader br = new BufferedReader(new FileReader(f,"8895_1"));
            String line;
            int lineNumber = 0;

            while ( (line = br.readLine()) != null) {
                lineNumber++;
                try {
                    StringTokenizer t = new StringTokenizer(line, "\t");
                    String matricula = t.nextToken().trim();
                    StringBuffer nomeBuffer = new StringBuffer(t.nextToken().trim());
                    int index = 0;
                    while (true) {
                        index = nomeBuffer.indexOf("'", index);
                        if (index == -1)
                            break;
                        else {
                            nomeBuffer.replace(index, index + 1, "\\'");
                            index = index + 2;
                        }
                    }

                    String nome = nomeBuffer.toString();

                    ModelAluno mAluno = _modelInstituicao.getAlunoByMatricula(matricula);
                    if (mAluno != null) {
                        System.out.println("Já existe aluno com matrícula " + matricula + "(" + mAluno.getAluno().getNome() + ")");
                        continue;
                    }

                    Aluno aluno = new Aluno(0, nome, matricula, null);

                    // matricular aluno na turma
                    while (t.hasMoreTokens()) {
                        String turmaName = t.nextToken().trim();

                        ModelTurma modelTurma = _modelInstituicao.getTurmaByName(turmaName);
                        if (modelTurma == null) {
                            modelTurma = _modelInstituicao.addNewTurma(turmaName);
                        }

                        AlunoTurma alunoTurma = new AlunoTurma(aluno, modelTurma.getTurma());
                        alunosTurmas.add(alunoTurma);
                    }

                    alunos.add(aluno);
                }
                catch (Exception ex) {
                    System.out.println("<Alerta> Linha " + lineNumber + " nao pode ser importada");
                }
            }
            System.out.println("Adicionando alunos e alunosTurmas no banco");
            ExtensaoRepositorio.inserirAlunos(alunos, alunosTurmas, _modelInstituicao.getInstituicao());

            System.out.println("Adicionando alunos no modelo");
            _modelInstituicao.addAlunos(alunos);

            System.out.println("Particionando Alunos em Turma");
            HashMap<ModelTurma, ArrayList<ModelAluno>> mapaTurmaAlunos = new HashMap<ModelTurma, ArrayList<ModelAluno>> ();
            for (AlunoTurma alunoTurma: alunosTurmas) {
                ModelTurma modelTurma = _modelInstituicao.getTurmaByName(alunoTurma.getTurma_AlunoTurma().getNome());
                if (modelTurma == null)
                    continue;
                ModelAluno modelAluno = _modelInstituicao.getAlunoByMatricula(alunoTurma.getAluno_AlunoTurma().getMatricula());
                if (modelAluno == null)
                    continue;
                ArrayList<ModelAluno> alunosT = mapaTurmaAlunos.get(modelTurma);
                if (alunosT != null)
                    alunosT.add(modelAluno);
            }

            System.out.println("Adicionando alunosTurmas no modelo");
            Iterator iterator =  mapaTurmaAlunos.keySet().iterator();
            while (iterator.hasNext()) {
                ModelTurma turma = (ModelTurma) iterator.next();
                ArrayList<ModelAluno> alunosT = mapaTurmaAlunos.get(turma);
                if (alunosT == null)
                    continue;
                turma.matricularAlunos(alunosT);
            }

        }
        catch (FileNotFoundException ex1) {
            ex1.printStackTrace();
        }
        catch (IOException ex1) {
            ex1.printStackTrace();
        }
        catch (SQLException ex1) {
            ex1.printStackTrace();
        }


    }

    File _f = null;
    public void actionPerformed(ActionEvent e) {

        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        fc.setSelectedFile(new File(App.getProperty(ConfiguracaoMIXnFIX.alunosdir)));

        int r = fc.showOpenDialog(MainFrame.MAIN_FRAME);
        if (r == JFileChooser.APPROVE_OPTION) {
            _f = fc.getSelectedFile();

            long t0 = System.currentTimeMillis();

            // file
            App.setProperty(ConfiguracaoMIXnFIX.alunosdir, fc.getSelectedFile().getAbsolutePath());

            // abrir a prova
            System.out.println("Importando alunos " + _f.getAbsolutePath() + "...");

            ViewEspera ve = new ViewEspera(MainFrame.MAIN_FRAME);
            Object o = ve.doWork(new linsoft.gui.util.IWorker() {
                public Object doWork() {
                    matricularAlunos2(_f);
                    return null;
                }
            }
            , "Importando alunos...", 160, 120, Images.MIXnFIX_transparent);



            long deltaT = System.currentTimeMillis() - t0;
            System.out.println("Tempo para adicionar a galera (mseg): " + deltaT);

        }
    }
}


class MFActionExportarAlunos
    extends AbstractAction {

    ModelInstituicao _modelInstituicao;
    public MFActionExportarAlunos(ModelInstituicao instituicao) {
        super("Exportar Alunos", Images.Turma16x16);
        _modelInstituicao = instituicao;
    }

    private void exportarAlunos(String filename) {
        StringBuffer sb = new StringBuffer();
        ArrayList<ModelAluno> alunos = null;
        try {
            alunos = _modelInstituicao.getAlunos();
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        for (ModelAluno modelAluno : alunos) {
            Aluno aluno = modelAluno.getAluno();

            sb.append(aluno.getMatricula() + "\t" + aluno.getNome() + "\t");

            ArrayList<ModelTurma> turmas = null;
            try {
                turmas = modelAluno.getTurmas();
            }
            catch (SQLException sql) {
                sql.printStackTrace();
            }
            for (int i = 0; i < turmas.size(); i++) {
                ModelTurma turma = turmas.get(i);
                sb.append(turma.getTurma().getNome());
                if (i != turmas.size() - 1)
                    sb.append("\t");
            }
            sb.append("\n");
        }

        PrintWriter file = null;
        try {
            file = new PrintWriter(new FileOutputStream(filename));
        }
        catch (FileNotFoundException ex) {
        }
        file.println(sb.toString());
        file.close();

    }

    String _filename = null;
    public void actionPerformed(ActionEvent e) {

        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(false);
        fc.setSelectedFile(new File(App.getProperty(ConfiguracaoMIXnFIX.arquivoExportacaoAluno)));

        int r = fc.showSaveDialog(MainFrame.MAIN_FRAME);
        if (r == JFileChooser.APPROVE_OPTION) {
            _filename = fc.getSelectedFile().getAbsolutePath();

            App.getProperty(ConfiguracaoMIXnFIX.arquivoExportacaoAluno,fc.getSelectedFile().getAbsolutePath());

            
            ViewEspera ve = new ViewEspera(MainFrame.MAIN_FRAME);
            Object o = ve.doWork(new linsoft.gui.util.IWorker() {
                public Object doWork() {
                    exportarAlunos(_filename);
                    return null;
                }
            }
            , "Exportando alunos...", 160, 120, Images.MIXnFIX_transparent);

        }
    }

}

class MFActionSalvarProva
    extends AbstractAction {

    ModelProva _modelProva;
    public MFActionSalvarProva(ModelProva modelProva) {
        super("Salvar Prova", Images.Prova16x16);
        _modelProva = modelProva;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            if (!_modelProva.getStructureChanged()) {
                // JOptionPane.showMessageDialog( (Component) e.getSource(), "Já está salvo");
                return;
            }

            // gravar
            _modelProva.gravar();

            //
            // JOptionPane.showMessageDialog( (Component) e.getSource(), "Arquivo salvo!");
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        catch (HeadlessException ex) {
            ex.printStackTrace();
        }
        catch (Exception ex) {
            /** @todo Handle this exception */
            ex.printStackTrace();
        }

    }
}

class MFActionTrocarResposta
    extends AbstractAction {

    Quesito _quesito;
    public MFActionTrocarResposta(Quesito quesito) {
        super("Trocar Resposta");
        _quesito = quesito;
    }

    public void actionPerformed(ActionEvent e) {
        Object o = JOptionPane.showInputDialog( (Component) e.getSource(), "Resposta da questão: " + _quesito.getTag(), _quesito.getTextRespostaCorreta());
        if (o == null)
            return;

        if (_quesito.getTipo() == Quesito.TIPO_FALSO_VERDADEIRO) {
            String st = (String) o;
            int j = 0;
            for (int i = 0; i < st.length() && j < _quesito.getItensCount(); i++) {
                if ( ("" + st.charAt(i)).toUpperCase().charAt(0) == 'V') {
                    _quesito.getItem(j++).setProperty("resposta", "v");
                }
                else if ( ("" + st.charAt(i)).toUpperCase().charAt(0) == 'F') {
                    _quesito.getItem(j++).setProperty("resposta", "f");
                }
                else if ( ("" + st.charAt(i)).toUpperCase().charAt(0) == '_') {
                    _quesito.getItem(j++).setProperty("resposta", "");
                }
            }
        }
        else {
            String st = (String) o;
            _quesito.setProperty("resposta", st);
        }
        _quesito.fireModelUpdate();
    }
}

class MFActionTrocarValorAcerto
    extends AbstractAction {

    ArrayList<Quesito> _quesitos;

    public MFActionTrocarValorAcerto(Quesito quesito) {
        super("Trocar Valor Acerto");
        _quesitos = new ArrayList<Quesito>();
        _quesitos.add(quesito);
    }

    public MFActionTrocarValorAcerto(ArrayList<Quesito> quesitos) {
        super("Trocar Valor Acerto");
        _quesitos = quesitos;
    }

    public void actionPerformed(ActionEvent e) {
        if (_quesitos.size() == 0) {
            JOptionPane.showMessageDialog( (Component) e.getSource(), "Não há quesitos selecionados!");
            return;
        }

        Quesito q = _quesitos.get(0);
        String label = _quesitos.size() == 1 ? "Valor: " + q.getTag() : "Valor: múltiplos quesitos";
        double valor = q.getValorAcerto();

        Object o = JOptionPane.showInputDialog( (Component) e.getSource(), label, String.format("%.3f", valor));
        if (o == null)
            return;

        String st = (String) o;
        double acerto = 0;
        try {
            acerto = Float.parseFloat(st);
        }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, "Número inválido");
            return;
        }

        for (Quesito qq: _quesitos) {
            qq.setValorAcerto(acerto);
        }
    }
}

class MFActionTrocarIndice
    extends AbstractAction {

    ModelProva _model;
    public MFActionTrocarIndice(ModelProva p) {
        super("Trocar Indice");
        _model = p;
    }

    public void actionPerformed(ActionEvent e) {
        Object o = JOptionPane.showInputDialog( (Component) e.getSource(), "Novo Índice", _model.getProvaStructure().getIndex());
        if (o == null)
            return;

        String st = (String) o;
        int index = Integer.parseInt(st);

        _model.getProvaStructure().setIndex(index);
        _model.fireModelUpdate();
    }
}

class MFActionTrocarNumDigitosID
    extends AbstractAction {

    ModelProva _model;
    public MFActionTrocarNumDigitosID(ModelProva p) {
        super("Trocar Num. Digitos ID");
        _model = p;
    }

    public void actionPerformed(ActionEvent e) {
        Object o = JOptionPane.showInputDialog( (Component) e.getSource(), "Trocar Num. Digitos ID", _model.getProvaStructure().getNumDigitosID());
        if (o == null)
            return;

        String st = (String) o;
        int index = Integer.parseInt(st);

        _model.getProvaStructure().setNumDigitosID(index);
        _model.fireModelUpdate();
    }
}

class MFActionTrocarValorErro
    extends AbstractAction {

    ArrayList<Quesito> _quesitos;

    public MFActionTrocarValorErro(Quesito quesito) {
        super("Trocar Valor Erro");
        _quesitos = new ArrayList<Quesito>();
        _quesitos.add(quesito);
    }

    public MFActionTrocarValorErro(ArrayList<Quesito> quesitos) {
        super("Trocar Valor Erro");
        _quesitos = quesitos;
    }

    public void actionPerformed(ActionEvent e) {
        if (_quesitos.size() == 0) {
            JOptionPane.showMessageDialog( (Component) e.getSource(), "Não há quesitos selecionados!");
            return;
        }

        Quesito q = _quesitos.get(0);
        String label = _quesitos.size() == 1 ? "Valor: " + q.getTag() : "Valor: múltiplos quesitos";
        double valor = q.getValorFalha();

        Object o = JOptionPane.showInputDialog( (Component) e.getSource(), label, String.format("%.3f", valor));
        if (o == null)
            return;

        String st = (String) o;
        double falha = 0;
        try {
            falha = Float.parseFloat(st);
        }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, "Número inválido");
            return;
        }

        for (Quesito qq: _quesitos) {
            qq.setValorErro(falha);
        }
    }
}

class MFActionAdicionarGrupo
    extends AbstractAction {
    Grupo _grupo;
    public MFActionAdicionarGrupo(Grupo grupo) {
        super("Adicionar Grupo", Images.Grupo);
        _grupo = grupo;
    }

    public void actionPerformed(ActionEvent e) {
        Object o = JOptionPane.showInputDialog( (Component) e.getSource(), "Adicionar Grupo");
        if (o == null)
            return;
        Grupo g = new Grupo();
        g.setTag( (String) o);
        _grupo.addGrupo(g);
    }
}

class MFActionAlterarGrupo
    extends AbstractAction {
    Grupo _grupo;
    public MFActionAlterarGrupo(Grupo grupo) {
        super("Alterar Grupo", Images.Grupo);
        _grupo = grupo;
    }

    public void actionPerformed(ActionEvent e) {
        Object o = JOptionPane.showInputDialog( (Component) e.getSource(), "Adicionar Grupo", _grupo.getTag());
        if (o == null)
            return;
        _grupo.setTag( (String) o);
    }
}


class MFActionAdicionarInstituicao
    extends AbstractAction {
    ModelMF _modelMF;
    public MFActionAdicionarInstituicao(ModelMF modelMF) {
        super("Adicionar Pasta", Images.Grupo);
        _modelMF = modelMF;
    }

    public void actionPerformed(ActionEvent e) {
        Object o = JOptionPane.showInputDialog( (Component) e.getSource(), "Adicionar Pasta");
        if (o == null)
            return;
        try {
            String nome = (String) o;
            if (_modelMF.getIntituicaoByName(nome) != null) {
                JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, "Já existe pasta com nome "+nome, "Mensagem", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            _modelMF.addInstituicao("" + o);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

class MFActionRemoverQuesito
    extends AbstractAction {
    Quesito _quesito;
    public MFActionRemoverQuesito(Quesito quesito) {
        super("Remover Quesito", Images.Grupo);
        _quesito = quesito;
    }

    public void actionPerformed(ActionEvent e) {
        int code = JOptionPane.showConfirmDialog(MainFrame.MAIN_FRAME, "Tem certeza que deseja remover o quesito " + _quesito.getTag() + "?");
        if (code == JOptionPane.OK_OPTION) {
            _quesito.remover();
        }
    }
}

class MFActionRemoverItemQuesito
    extends AbstractAction {
    ItemQuesito _itemQuesito;
    public MFActionRemoverItemQuesito(ItemQuesito i) {
        super("Remover Item Quesito", Images.Grupo);
        _itemQuesito = i;
    }

    public void actionPerformed(ActionEvent e) {
        int code = JOptionPane.showConfirmDialog(MainFrame.MAIN_FRAME, "Tem certeza que deseja remover o item " + _itemQuesito.getTag() + "?");
        if (code == JOptionPane.OK_OPTION) {
            _itemQuesito.remover();
        }
    }
}

class MFActionAdicionarItemQuesito
    extends AbstractAction {
    Quesito _quesito;
    public MFActionAdicionarItemQuesito(Quesito q) {
        super("Adiicionar Item Quesito", Images.Grupo);
        _quesito = q;
    }

    public void actionPerformed(ActionEvent e) {
        Object o = JOptionPane.showInputDialog( (Component) e.getSource(), "Adicionar Item Quesito");
        if (o == null)
            return;
        ItemQuesito itemQuesito = new ItemQuesito();
        itemQuesito.setTag( (String) o);
        _quesito.addItemQuesito(itemQuesito);
    }
}

class MFActionAlterarItemQuesito
    extends AbstractAction {
    ItemQuesito _itemQuesito;
    public MFActionAlterarItemQuesito(ItemQuesito q) {
        super("Alterar Item Quesito", Images.Grupo);
        _itemQuesito = q;
    }

    public void actionPerformed(ActionEvent e) {
        Object o = JOptionPane.showInputDialog( (Component) e.getSource(), "Alterar Item Quesito");
        if (o == null)
            return;
        _itemQuesito.setTag( (String) o);
    }
}

class MFActionAdicionarQuesito
    extends AbstractAction {
    Grupo _grupo;
    public MFActionAdicionarQuesito(Grupo grupo) {
        super("Adicionar Quesito", Images.Quesito);
        _grupo = grupo;
    }

    public void actionPerformed(ActionEvent e) {

        PanelCriarQuesito p = new PanelCriarQuesito();
        p.run(MainFrame.MAIN_FRAME);
        if (!p.isOk())
            return;

        Quesito q = new Quesito();
        q.setTag(p.getTag());
        if (p.getTipoQuesito() == PanelCriarQuesito.ID_ALTERNATIVAS) {
            q.setTipo(Quesito.TIPO_ALTERNATIVAS);
        }
        else if (p.getTipoQuesito() == PanelCriarQuesito.ID_FALSO_VERDADEIRO) {
            q.setTipo(Quesito.TIPO_FALSO_VERDADEIRO);
        }
        else if (p.getTipoQuesito() == PanelCriarQuesito.ID_NUMERICO) {
            q.setTipo(Quesito.TIPO_NUMERICO_99);
        }
        else if (p.getTipoQuesito() == PanelCriarQuesito.ID_SUBJETIVO) {
            q.setTipo(Quesito.TIPO_SUBJETIVA_5);
        }
        else if (p.getTipoQuesito() == PanelCriarQuesito.ID_SUBJETIVO_9) {
            q.setTipo(Quesito.TIPO_SUBJETIVA_9);
        }
        else throw new RuntimeException("Tipo de quesito inválido");

        q.setValorAcerto(p.getValorAcerto());
        q.setValorErro(p.getValorErro());
        _grupo.addQuesito(q);

        //
        if ( (p.getTipoQuesito() == PanelCriarQuesito.ID_ALTERNATIVAS) || (p.getTipoQuesito() == PanelCriarQuesito.ID_FALSO_VERDADEIRO)) {
            for (int i = 0; i < p.getNumItens(); i++) {
                ItemQuesito iq = new ItemQuesito();
                iq.setTag("item " + (i + 1));
                q.addItemQuesito(iq);
            }
        }
    }
}

class MFActionCopiarPontoProva
    extends AbstractAction {

    ModelProva _model;
    public MFActionCopiarPontoProva(ModelProva m) {
        super("Exportar Prova",  Images.Prova16x16);
        _model = m;
    }

    public void actionPerformed(ActionEvent e) {

        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        fc.setSelectedFile(new File(App.getProperty("pontoprovafilename")));

        fc.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                StringTokenizer st = new StringTokenizer(f.getName(), ".");
                String last = null;
                while (st.hasMoreTokens()) {
                    last = st.nextToken();
                }
                if (last != null) {
                    last = last.toLowerCase();
                    if ("prova".equals(last))
                        return true;
                }
                return false;
            }

            public String getDescription() {
                return "Provas (.prova)";
            }
        });

        int r = fc.showSaveDialog(MainFrame.MAIN_FRAME);
        if (r == JFileChooser.APPROVE_OPTION) {
            App.setProperty("pontoprovafilename", fc.getSelectedFile().getAbsolutePath());
            try {
                Library.copyFile(new File(App.getConfiguracao().getProperty(ConfiguracaoMIXnFIX.datadir)+"/"+_model.getProva().getFonte()), fc.getSelectedFile());
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog( (Component) e.getSource(), "Arquivo " + fc.getSelectedFile().getName() + " gravado com sucesso!");
        }
    }
}

class MFActionCopiarPontoProvaDeTodaUmaInstituicao
    extends AbstractAction {

    ModelInstituicao _model;
    public MFActionCopiarPontoProvaDeTodaUmaInstituicao(ModelInstituicao m) {
        super("Exportar Todas as Provas da Instituição",  Images.Prova16x16);
        _model = m;
    }

    public void actionPerformed(ActionEvent e) {

        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(false);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setSelectedFile(new File(App.getProperty("directoryofpontoprovas")));

        int r = fc.showSaveDialog(MainFrame.MAIN_FRAME);
        if (r == JFileChooser.APPROVE_OPTION) {
            App.setProperty("directoryofpontoprovas", fc.getSelectedFile().getAbsolutePath());
            try {
                for (ModelProva mp : _model.getProvas()) {
                    System.out.println("");
                    String outFileName = mp.getProva().getNome();
                    if (!outFileName.endsWith(".prova"))
                        outFileName += ".prova";

                    /**
                     * @todo escolher diretório.
                     */
                    System.out.println("Saving... "+fc.getSelectedFile().getAbsolutePath()+"/"+outFileName);
                    Library.copyFile(new File(App.getConfiguracao().getProperty(ConfiguracaoMIXnFIX.datadir)+"/"+mp.getProva().getFonte()), new File(fc.getSelectedFile().getAbsolutePath()+"/"+outFileName));
                }
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog( (Component) e.getSource(), "Arquivo " + fc.getSelectedFile().getName() + " gravado com sucesso!");
        }
    }
}


class MFActionImprimirPDFs
    extends AbstractAction {

    public MFActionImprimirPDFs() {
        super("Imprimir Múltiplos PDFs",Images.Prova16x16);
    }

    public void actionPerformed(ActionEvent e) {
        PanelChooseDirectory pcd = new PanelChooseDirectory();
        pcd.run(MainFrame.MAIN_FRAME);
        if (pcd.isOk()) {
            File path = new File(pcd.getOutputDir());

            ArrayList<File> files = new ArrayList<File> ();
            for (File f : path.listFiles()) {
                if (f.getName().indexOf(".pdf") >= 0)
                    files.add(f);
            }

            // sort files
            Collections.sort(files, new Comparator() {
                public int compare(Object o1, Object o2) {
                    File f1 = (File) o1;
                    File f2 = (File) o2;
                    return f1.getName().compareTo(f2.getName());
                }

                public boolean equals(Object obj) {
                    return false;
                }
            });

            System.out.println("Printing PDFs in " + path.getAbsolutePath());
            int count = 1;
            for (File f : files) {
                System.out.println("Printing file " + (count++) + ": " + f.getName());

                // Print to default printer
                String command = "java -cp . PDFPrint " + path.getAbsolutePath() + " " + f.getName();
                try {
                    Library.executeCommand(command, true);
                    Thread.currentThread().sleep(1500);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, "Problema no envio para impressão");
                    return;
                }
                //ShellExec.shellExecute("print", f.getName(), "", path.getAbsolutePath());
            }
        }
    }
}

class MFActionCopyModelToClipboard
    extends AbstractAction {
    private static ModelProva _clipboardModelProva;
    private static Model _clipboard;

    public static Model getClipboard() {
        return _clipboard;
    }

    public static ModelProva getClipboardModelProva() {
        return _clipboardModelProva;
    }

    public static void resetClipboard() {
        _clipboard = null;
        _clipboardModelProva = null;
    }

    public static boolean isEmpty() {
        return (_clipboard == null);
    }

    private Model _model; // grupo ou quesito
    public MFActionCopyModelToClipboard(Model m) {
        super("Copiar para clipboard");
        if (m instanceof Grupo || m instanceof Quesito) {
            _model = m;
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (_model instanceof Grupo) {
            _clipboardModelProva = (ModelProva) _model.getAscendentByClass(ModelProva.class);
            _clipboard = ( (Grupo) _model).getCopy();
        }
        else if (_model instanceof Quesito) {
            _clipboardModelProva = (ModelProva) _model.getAscendentByClass(ModelProva.class);
            _clipboard = ( (Quesito) _model).getCopy();
        }
    }
}

class MFActionPasteModelOnClipboard
    extends AbstractAction {
    private Grupo _grupo; // grupo ou quesito
    public MFActionPasteModelOnClipboard(Grupo g) {
        super("Colar do clipboard");
        if (MFActionCopyModelToClipboard.isEmpty())
            this.setEnabled(false);
        _grupo = g;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            hardwork();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void hardwork() throws IOException, Exception {
        if (MFActionCopyModelToClipboard.isEmpty()) {
            JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, "Clipboard está vazio!");
            return;
        }

        Model m = MFActionCopyModelToClipboard.getClipboard();
        ModelProva mpSource = MFActionCopyModelToClipboard.getClipboardModelProva();
        copiarImagensSeNecessario(mpSource, m);
        if (m instanceof Quesito) {
            Quesito q = (Quesito) m;
            _grupo.addQuesito(q);
            q.fireInitStructure();
        }
        else if (m instanceof Grupo) {
            Grupo g = (Grupo) m;
            _grupo.addGrupo(g);
            g.fireInitStructure();
        }
        MFActionCopyModelToClipboard.resetClipboard();
    }

    private void copiarImagensSeNecessario(ModelProva mpSource, Model m) throws IOException, Exception {
        ModelProva mpTarget = (ModelProva) _grupo.getAscendentByClass(ModelProva.class);
        if (mpTarget == mpSource)return;
        List<String> names = getImageFileNames(m);
        for (String name : names) {
            String freshName = mpTarget.assureImageFileName(name);
            Library.copyFile(new File(mpSource.getPath() + "/" + name), new File(mpTarget.getPath() + "/" + freshName));
        }
    }

    public List<String> getImageFileNames(Model source) {

        Stack<Model> nodes = new Stack<Model> ();
        ArrayList<String> names = new ArrayList<String> ();

        nodes.add(source);

        ArrayList<Papel> papeis = new ArrayList<Papel> ();
        while (!nodes.isEmpty()) {
            Model m = nodes.pop();
            if (m instanceof Quesito) {
                Quesito q = (Quesito) m;
                Papel p;
                p = q.getEnunciado();
                if (p != null)
                    papeis.add(p);
                p = q.getSolucao();
                if (p != null)
                    papeis.add(p);

                // add childs
                for (Model child: q.getChilds())
                    nodes.push(child);
            }
            else if (m instanceof ItemQuesito) {
                ItemQuesito iq = (ItemQuesito) m;
                Papel p;
                p = iq.getEnunciado();
                if (p != null)
                    papeis.add(p);
            }
            else if (m instanceof Grupo) {
                Grupo g = (Grupo) m;
                Papel p = g.getEnunciado();
                if (p != null)
                    papeis.add(p);

                // add childs
                for (Model child: g.getChilds())
                    nodes.push(child);
            }
        }

        for (Papel p : papeis) {
            for (Model m : p.getContents()) {
                if (m instanceof Imagem) {
                    Imagem i = (Imagem) m;
                    String name = new File(i.getProperty("src")).getName();
                    if (!names.contains(name))
                        names.add(name);
                }
            }
        }
        return names;
    }
}

/**
 * Salvar um quesito como arquivo .quesito (zip de quesito.xml + imagens)
 * no banco de questões para reuso em provas futuras.
 */
class MFActionSalvarQuesitoNoBanco
    extends AbstractAction {
    private Quesito _quesito;

    public MFActionSalvarQuesitoNoBanco(Quesito q) {
        super("Salvar no Banco de Questões", Images.Quesito);
        _quesito = q;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            hardwork();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, "Erro ao salvar quesito: " + ex.getMessage());
        }
    }

    private void hardwork() throws Exception {
        // nome do arquivo (default = tag do quesito, limpo de chars inválidos)
        String sugestao = _quesito.getTag();
        if (sugestao != null)
            sugestao = sugestao.replaceAll("[^a-zA-Z0-9._ -]", "_").trim();
        Object o = JOptionPane.showInputDialog(MainFrame.MAIN_FRAME, "Nome do arquivo (sem extensão):", sugestao);
        if (o == null)
            return;
        String nome = ((String) o).trim();
        if (nome.isEmpty())
            return;

        File bancoDir = new File(App.getConfiguracao().getProperty(ConfiguracaoMIXnFIX.bancoquesitosdir));
        bancoDir.mkdirs();
        File outFile = new File(bancoDir, nome + ".quesito");
        if (outFile.exists()) {
            int code = JOptionPane.showConfirmDialog(MainFrame.MAIN_FRAME,
                "Já existe " + outFile.getName() + ". Sobrescrever?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (code != JOptionPane.YES_OPTION)
                return;
        }

        // a prova-mãe guarda o diretório temp onde as imagens estão descompactadas
        ModelProva mp = (ModelProva) _quesito.getAscendentByClass(ModelProva.class);
        File sourcePath = mp.getPath();

        // coletar nomes de imagens referenciadas por este quesito
        List<String> imageNames = collectImageNames(_quesito);

        // montar num diretório temporário e zipar
        File tmpDir = new File(Controller.TMP_DIR, "__mixnfix_banco_" + System.currentTimeMillis());
        tmpDir.mkdirs();
        try {
            PrintWriter pw = new PrintWriter(new File(tmpDir, "quesito.xml"), "UTF-8");
            ModelProva.gerarXMLQuesito(_quesito, pw);
            pw.flush();
            pw.close();

            for (String img : imageNames) {
                Library.copyFile(new File(sourcePath, img), new File(tmpDir, img));
            }

            ArrayList<String> names = new ArrayList<String>(imageNames);
            names.add("quesito.xml");
            Controller.zipFiles(tmpDir, names, outFile.getAbsolutePath());
        }
        finally {
            for (File f : tmpDir.listFiles()) f.delete();
            tmpDir.delete();
        }

        JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, "Quesito salvo em: " + outFile.getAbsolutePath());
    }

    static List<String> collectImageNames(Quesito q) {
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<Papel> papeis = new ArrayList<Papel>();
        if (q.getEnunciado() != null) papeis.add(q.getEnunciado());
        if (q.getSolucao() != null) papeis.add(q.getSolucao());
        for (Model child : q.getChilds()) {
            ItemQuesito iq = (ItemQuesito) child;
            if (iq.getEnunciado() != null) papeis.add(iq.getEnunciado());
        }
        for (Papel p : papeis) {
            for (Model m : p.getContents()) {
                if (m instanceof Imagem) {
                    String src = ((Imagem) m).getProperty("src");
                    if (src != null) {
                        String name = new File(src).getName();
                        if (!names.contains(name)) names.add(name);
                    }
                }
            }
        }
        return names;
    }
}

/**
 * Importar um quesito do banco de questões e inseri-lo no grupo selecionado.
 */
class MFActionImportarQuesitoDoBanco
    extends AbstractAction {
    private Grupo _grupo;

    public MFActionImportarQuesitoDoBanco(Grupo g) {
        super("Importar do Banco de Questões", Images.Quesito);
        _grupo = g;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            hardwork();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, "Erro ao importar quesito: " + ex.getMessage());
        }
    }

    private void hardwork() throws Exception {
        File bancoDir = new File(App.getConfiguracao().getProperty(ConfiguracaoMIXnFIX.bancoquesitosdir));
        if (!bancoDir.isDirectory()) {
            JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, "Banco de questões vazio (" + bancoDir.getAbsolutePath() + ")");
            return;
        }

        ArrayList<String> opts = new ArrayList<String>();
        for (File f : bancoDir.listFiles()) {
            if (f.isFile() && f.getName().endsWith(".quesito"))
                opts.add(f.getName().substring(0, f.getName().length() - ".quesito".length()));
        }
        if (opts.isEmpty()) {
            JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, "Banco de questões vazio (" + bancoDir.getAbsolutePath() + ")");
            return;
        }
        Collections.sort(opts);

        Object sel = JOptionPane.showInputDialog(MainFrame.MAIN_FRAME, "Escolha o quesito:", "Banco de Questões",
            JOptionPane.PLAIN_MESSAGE, null, opts.toArray(), opts.get(0));
        if (sel == null)
            return;

        File quesitoFile = new File(bancoDir, sel + ".quesito");
        File tmpDir = new File(Controller.TMP_DIR, "__mixnfix_banco_" + System.currentTimeMillis());
        tmpDir.mkdirs();
        try {
            Controller.unzip(quesitoFile.getAbsolutePath(), tmpDir.getAbsolutePath());

            mixnfix.prova.Parser parser = new mixnfix.prova.Parser(new File(tmpDir, "quesito.xml").getAbsolutePath());
            ArrayList<Quesito> qs = parser.getProva().getQuesitos();
            if (qs.isEmpty()) {
                JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, "Arquivo não contém quesito.");
                return;
            }
            Quesito q = qs.get(0).getCopy();

            // copiar imagens para o diretório de trabalho da prova destino,
            // renomeando em caso de colisão (atualiza o src no modelo)
            ModelProva mpTarget = (ModelProva) _grupo.getAscendentByClass(ModelProva.class);
            ArrayList<Papel> papeis = new ArrayList<Papel>();
            if (q.getEnunciado() != null) papeis.add(q.getEnunciado());
            if (q.getSolucao() != null) papeis.add(q.getSolucao());
            for (Model child : q.getChilds()) {
                ItemQuesito iq = (ItemQuesito) child;
                if (iq.getEnunciado() != null) papeis.add(iq.getEnunciado());
            }
            for (Papel p : papeis) {
                for (Model m : p.getContents()) {
                    if (m instanceof Imagem) {
                        Imagem img = (Imagem) m;
                        String src = img.getProperty("src");
                        if (src == null) continue;
                        String name = new File(src).getName();
                        String fresh = mpTarget.assureImageFileName(name);
                        Library.copyFile(new File(tmpDir, name), new File(mpTarget.getPath(), fresh));
                        img.setProperty("src", fresh);
                    }
                }
            }

            _grupo.addQuesito(q);
            q.fireInitStructure();
        }
        finally {
            for (File f : tmpDir.listFiles()) f.delete();
            tmpDir.delete();
        }
    }
}

class MFActionProduzirProva
    extends AbstractAction {
    ModelProva _model;
    public MFActionProduzirProva(ModelProva m) {
        super("Produzir Prova",Images.Prova16x16);
        _model = m;
    }

    public void actionPerformed(ActionEvent e) {
        JDialog d = new JDialog(MainFrame.MAIN_FRAME, "Produzir Prova", true);
        d.setContentPane(new PanelProduzirProva(_model));
        linsoft.gui.util.Library.resizeAndCenterWindow(d, 420, 510);
        d.setVisible(true);
    }
}

class MFActionProduzirQuestionario
    extends AbstractAction {
    ModelProva _model;
    public MFActionProduzirQuestionario(ModelProva m) {
        super("Produzir Questionário");
        _model = m;
    }

    public void actionPerformed(ActionEvent e) {
        JDialog d = new JDialog(MainFrame.MAIN_FRAME, "Produzir Questionário", true);
        d.setContentPane(new PanelProduzirQuestionario(_model));
        linsoft.gui.util.Library.resizeAndCenterWindow(d, 420, 510);
        d.setVisible(true);
    }
}

class MFActionRelatorioPDFCorrecoesProva
    extends AbstractAction {
    ModelProva _model;
    public MFActionRelatorioPDFCorrecoesProva(ModelProva m) {
        super("Gerar Relatório Correções");
        _model = m;
    }

    public void actionPerformed(ActionEvent e) {
        JDialog d = new JDialog(MainFrame.MAIN_FRAME, "Relatório da Prova", true);
        d.setContentPane(new PanelProduzirRelatorioModelProva(_model));
        linsoft.gui.util.Library.resizeAndCenterWindow(d, 420, 470);
        d.setVisible(true);
    }
}

class MFActionAdjustImageNames
    extends AbstractAction {
    private ModelMF _modelMF;
    public MFActionAdjustImageNames(ModelMF m) {
        super("Adjust Image Names");
        _modelMF = m;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            _modelMF.adjustImageFileNames();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

class MFActionChangeQuesitoValues
    extends AbstractAction {
    private Quesito _quesito;
    public MFActionChangeQuesitoValues(Quesito q) {
        super("Alterar valores da questão");
        _quesito = q;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            actionHardWork();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private boolean _ok;
    private void actionHardWork() throws SQLException {
        int tipo = _quesito.getTipo();
        switch (tipo) {
            case Quesito.TIPO_ALTERNATIVAS:
                tipo = PanelCriarQuesito.ID_ALTERNATIVAS;
                break;
            case Quesito.TIPO_FALSO_VERDADEIRO:
                tipo = PanelCriarQuesito.ID_FALSO_VERDADEIRO;
                break;
            case Quesito.TIPO_NUMERICO_99:
                tipo = PanelCriarQuesito.ID_NUMERICO;
                break;
            case Quesito.TIPO_SUBJETIVA_5:
                tipo = PanelCriarQuesito.ID_SUBJETIVO;
                break;
            case Quesito.TIPO_SUBJETIVA_9:
                tipo = PanelCriarQuesito.ID_SUBJETIVO_9;
                break;
        }
        PanelCriarQuesito p = new PanelCriarQuesito();
        p.set(_quesito.getTag(), tipo, (double) _quesito.getValorAcerto(), (double) _quesito.getValorFalha(), _quesito.getItensCount());
        p.run(MainFrame.MAIN_FRAME);
        if (!p.isOk())
            return;

        _quesito.setTag(p.getTag());
        if (p.getTipoQuesito() == PanelCriarQuesito.ID_ALTERNATIVAS) {
            _quesito.setTipo(Quesito.TIPO_ALTERNATIVAS);
        }
        else if (p.getTipoQuesito() == PanelCriarQuesito.ID_FALSO_VERDADEIRO) {
            _quesito.setTipo(Quesito.TIPO_FALSO_VERDADEIRO);
        }
        else if (p.getTipoQuesito() == PanelCriarQuesito.ID_NUMERICO) {
            _quesito.setTipo(Quesito.TIPO_NUMERICO_99);
        }
        else if (p.getTipoQuesito() == PanelCriarQuesito.ID_SUBJETIVO) {
            _quesito.setTipo(Quesito.TIPO_SUBJETIVA_5);
        }
        else if (p.getTipoQuesito() == PanelCriarQuesito.ID_SUBJETIVO_9) {
            _quesito.setTipo(Quesito.TIPO_SUBJETIVA_9);
        }
        else throw new RuntimeException("Tipo de quesito inválido");

        _quesito.setValorAcerto(p.getValorAcerto());
        _quesito.setValorErro(p.getValorErro());

    }
}

class MFActionSetDefaultTags
    extends AbstractAction {
    private ProvaStructure _provaStructure;
    public MFActionSetDefaultTags(ProvaStructure ps) {
        super("Rotular tags padrões");
        _provaStructure = ps;
    }

    public void actionPerformed(ActionEvent e) {
        _provaStructure.setDefaultsTags();
    }
}

class MFActionSetPrefixoNumericoNosTagsDosQuesitos
    extends AbstractAction {
    private ProvaStructure _provaStructure;
    public MFActionSetPrefixoNumericoNosTagsDosQuesitos(ProvaStructure ps) {
        super("Prefixo Numerico nos Tags dos Quesitos");
        _provaStructure = ps;
    }

    public void actionPerformed(ActionEvent e) {
        _provaStructure.setPrefixoNumericoNosTagsDosQuesitos();
    }
}

class MFActionRemoverGrupo
    extends AbstractAction {
    private Grupo _grupo;
    public MFActionRemoverGrupo(Grupo grupo) {
        super("Remover grupo");
        _grupo = grupo;
    }

    public void actionPerformed(ActionEvent e) {
        int code = JOptionPane.showConfirmDialog(MainFrame.MAIN_FRAME, "Tem certeza que deseja remover o grupo " + _grupo.getTag() + "?");
        if (code == JOptionPane.OK_OPTION) {
            _grupo.remover();
        }
    }
}

class MFActionMoverPraFrente
    extends AbstractAction {
    private NoProva _noProva;
    public MFActionMoverPraFrente(NoProva n) {
        super(">>>>>");
        _noProva = n;
    }

    public void actionPerformed(ActionEvent e) {
        _noProva.moverPraFrente();
    }
}

class MFActionMoverPraTras
    extends AbstractAction {
    private NoProva _noProva;
    public MFActionMoverPraTras(NoProva n) {
        super("<<<<<");
        _noProva = n;
    }

    public void actionPerformed(ActionEvent e) {
        _noProva.moverPraTras();
    }
}

class MFActionGerarRelatorio
    extends AbstractAction {
    private ModelInstituicao _modelInstituicao;
    public MFActionGerarRelatorio(ModelInstituicao modelInstituicao) {
        super("Gerar Relatório EXCEL", Images.Instituicao16x16);
        _modelInstituicao = modelInstituicao;
    }

    public void actionPerformed(ActionEvent e) {

        final JFileChooser chooser = new JFileChooser();

        chooser.setMultiSelectionEnabled(false);
        chooser.setSelectedFile(new File(App.getProperty(ConfiguracaoMIXnFIX.instituicaoreport)));

        // Initialize title with current directory
        File curDir = chooser.getCurrentDirectory();
        chooser.setDialogTitle("" + curDir.getAbsolutePath());

        // Add listener on chooser to detect changes to current directory
        chooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
                    File curDir = chooser.getCurrentDirectory();

                    chooser.setDialogTitle("" + curDir.getAbsolutePath());
                }
            }
        });

        chooser.setMultiSelectionEnabled(false);
        chooser.setSelectedFile(new File(App.getProperty(ConfiguracaoMIXnFIX.instituicaoreport)));


        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                if (f.getName().endsWith(".xls"))
                    return true;
                return false;
            }

            public String getDescription() {
                return "XLS (.xls)";
            }
        });

        int r = chooser.showOpenDialog(MainFrame.MAIN_FRAME);
        if (r == JFileChooser.APPROVE_OPTION) {
            App.setProperty(ConfiguracaoMIXnFIX.instituicaoreport, chooser.getSelectedFile().getAbsolutePath());

            ArrayList<ModelProvaCorrecao> mpcs = new ArrayList<ModelProvaCorrecao> ();
            List<ModelProva> modelsProvas = null;
            try {
                modelsProvas = _modelInstituicao.getProvas();
            }
            catch (SQLException sqle) {
                sqle.printStackTrace();
            }
            for (ModelProva modelProva : modelsProvas) {
                List<ModelProvaCorrecao> modelsProvasCorrecoes = null;
                try {
                    modelsProvasCorrecoes = modelProva.getProvasCorrecoes();
                }
                catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
                for (ModelProvaCorrecao modelProvaCorrecao : modelsProvasCorrecoes) {
                    mpcs.add(modelProvaCorrecao);
                }
            }

            ListCellRenderer renderer = new DefaultListCellRenderer() {
                public java.awt.Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    ModelProvaCorrecao x = (ModelProvaCorrecao) value;
                    this.setIcon(Images.Aluno16x16);
                    this.setText(x.getModelProva().getProva().getNome() + "-" + x.getProvaCorrecao().getNome());
                    return this;
                }
            };

            DialgoChooseObjects d = new DialgoChooseObjects(MainFrame.MAIN_FRAME, "Escolher Provas", true, new Vector(), mpcs, renderer);
            linsoft.gui.util.Library.resizeAndCenterWindow(d, 500, 400);
            d.setVisible(true);
            if (!d.isOk())
                return;

            ArrayList<ModelProvaCorrecao> mpcsSelecionados = (ArrayList<ModelProvaCorrecao>) d.getSelectedObjects();

            try {
                ModelInstituicaoReport.gerarRelatorioExcel(_modelInstituicao, mpcsSelecionados,  chooser.getSelectedFile().getAbsolutePath());

                
                // open PDF
                String command = App.getConfiguracao().getCommandOpenXLS(chooser.getSelectedFile().getAbsolutePath());
                Library.executeCommand(command, false);
                
                //process.waitFor();

            }
            catch (WriteException ex) {
                ex.printStackTrace();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
			catch (Exception e1) {
			// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }

    }
}


class MFActionGerarRelatorioProvaCorrecaoEstatisticaPorQuestao
        extends AbstractAction {
        private ModelProvaCorrecao _modelProvaCorrecao;
        public MFActionGerarRelatorioProvaCorrecaoEstatisticaPorQuestao(ModelProvaCorrecao modelProvaCorrecao) {
            super("Gerar Relatório de Estatística por Questão", Images.Instituicao16x16);
            _modelProvaCorrecao = modelProvaCorrecao;
        }

        public void actionPerformed(ActionEvent e) {

            final JFileChooser chooser = new JFileChooser();

            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setSelectedFile(new File(App.getProperty(ConfiguracaoMIXnFIX.estatisticaPorQuestaoDirReport)));

            // Initialize title with current directory
            File curDir = chooser.getCurrentDirectory();
            chooser.setDialogTitle("" + curDir.getAbsolutePath());

            // Add listener on chooser to detect changes to current directory
            chooser.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
                        File curDir = chooser.getCurrentDirectory();

                        chooser.setDialogTitle("" + curDir.getAbsolutePath());
                    }
                }
            });

            int r = chooser.showOpenDialog(MainFrame.MAIN_FRAME);
            if (r == JFileChooser.APPROVE_OPTION) {

                String outputFile = chooser.getSelectedFile().getAbsolutePath();

                App.setProperty(ConfiguracaoMIXnFIX.estatisticaPorQuestaoDirReport, outputFile);

                try {
                    // String baseName = "relatorio"+_model.getProva().getId();
                    String baseName = _modelProvaCorrecao.getProva().getNome() + "_repq";
                    String baseDir  = chooser.getSelectedFile().getAbsolutePath().replace('\\','/');

                    PrintWriter pw = new PrintWriter(Controller.TMP_DIR+baseName+".tex","utf-8");
                    pw.println(ModelProvaCorrecaoReport.getRelatorioEstatistaPorQuestao(_modelProvaCorrecao));
                    pw.close();


                    System.out.println(baseDir + baseName + ".pdf");

                    
                    
                    
                    
                    String command = App.getConfiguracao().getCommandCompileTEX2PDF(Controller.TMP_DIR, baseDir, "\"" + Controller.TMP_DIR + "/" + baseName + ".tex\"");
                    String dir = Controller.TMP_DIR.replace("\\","/").replace(" ","\\ ");
                    int status = Library.executeCommand(command,dir,null,true);
                    if (status != 0) {
                        JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, "Problema no arquivo .tex ao rodar o comando pdflatex");
                        return;
                    }            

                    // open PDF
                    command = App.getConfiguracao().getCommandOpenPDF(baseDir +"/" +baseName + ".pdf");
                    Library.executeCommand(command, false);
                    
                    
                    
                    
                    
                    
//                    // _prova.setProperty();
//                    int status = Library.executeCommand("pdflatex -halt-on-error" +
//                                                    " -include-directory=" + Controller.TMP_DIR +
//                                                    " -output-directory=" + baseDir +
//                                                    " -aux-directory=" + Controller.TMP_DIR +
//                                                    " " + Controller.TMP_DIR + baseName + ".tex", true);
//
//                    if (status != 0) {
//                        JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, "Problema no arquivo .tex ao rodar o comando pdflatex");
//                        return;
//                    }
//
//                    String st = mixnfix.gui.App.getProperty("acrobat");
//                    if (st == null || "".equals(st)) {
//                        st = "C:/Program Files/Adobe/Acrobat 7.0/Reader/AcroRd32.exe";
//                        mixnfix.gui.App.setProperty("acrobat", st);
//                    }
//                    Library.executeCommand("\"" + st + "\" " + baseDir + "/"+ baseName + ".pdf", false);

                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }
    }

class MFActionGerarRelatorioProvaEstatisticaPorQuestao
        extends AbstractAction {
        private ModelProva _modelProva;
        public MFActionGerarRelatorioProvaEstatisticaPorQuestao(ModelProva modelProva) {
            super("Gerar Relatório de Estatística por Questão", Images.Instituicao16x16);
            _modelProva = modelProva;
        }

        public void actionPerformed(ActionEvent e) {

            final JFileChooser chooser = new JFileChooser();

            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setSelectedFile(new File(App.getProperty("estatisticaPorQuestaoDirReport")));

            // Initialize title with current directory
            File curDir = chooser.getCurrentDirectory();
            chooser.setDialogTitle("" + curDir.getAbsolutePath());

            // Add listener on chooser to detect changes to current directory
            chooser.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
                        File curDir = chooser.getCurrentDirectory();

                        chooser.setDialogTitle("" + curDir.getAbsolutePath());
                    }
                }
            });

            int r = chooser.showOpenDialog(MainFrame.MAIN_FRAME);
            if (r == JFileChooser.APPROVE_OPTION) {

                String outputFile = chooser.getSelectedFile().getAbsolutePath();

                App.setProperty(ConfiguracaoMIXnFIX.estatisticaPorQuestaoDirReport, outputFile);

                try {
                    // String baseName = "relatorio"+_model.getProva().getId();
                    String baseName = _modelProva.getProva().getNome() + "_repq";
                    String baseDir  = chooser.getSelectedFile().getAbsolutePath().replace('\\','/');

                    PrintWriter pw = new PrintWriter(Controller.TMP_DIR+baseName+".tex","utf-8");
                    pw.println(ModelProvaReport.getRelatorioEstatistaPorQuestao(_modelProva));
                    pw.close();


                    System.out.println(baseDir + baseName + ".pdf");

                    
                    String command = App.getConfiguracao().getCommandCompileTEX2PDF(Controller.TMP_DIR, baseDir, "\"" + Controller.TMP_DIR + "/" + baseName + ".tex\"");
                    String dir = Controller.TMP_DIR.replace("\\","/").replace(" ","\\ ");
                    int status = Library.executeCommand(command,dir,null,true);
                    if (status != 0) {
                        JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, "Problema no arquivo .tex ao rodar o comando pdflatex");
                        return;
                    }            

                    // open PDF
                    command = App.getConfiguracao().getCommandOpenPDF(baseDir +"/" +baseName + ".pdf");
                    Library.executeCommand(command, false);                    
                    
                    
                    
//                    // _prova.setProperty();
//                    int status = Library.executeCommand("pdflatex -halt-on-error" +
//                                                    " -include-directory=" + Controller.TMP_DIR +
//                                                    " -output-directory=" + baseDir +
//                                                    " -aux-directory=" + Controller.TMP_DIR +
//                                                    " " + Controller.TMP_DIR + baseName + ".tex", true);
//
//                    if (status != 0) {
//                        JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, "Problema no arquivo .tex ao rodar o comando pdflatex");
//                        return;
//                    }
//
//                    status = Library.executeCommand("pdflatex -halt-on-error" +
//                                                    " -include-directory=" + Controller.TMP_DIR +
//                                                    " -output-directory=" + baseDir +
//                                                    " -aux-directory=" + Controller.TMP_DIR +
//                                                    " " + Controller.TMP_DIR + baseName + ".tex", true);
//
//                    if (status != 0) {
//                        JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME,  "Problema no arquivo .tex ao rodar o comando pdflatex");
//                        return;
//                    }
//
//
//
//                    String st = mixnfix.gui.App.getProperty("acrobat");
//                    if (st == null || "".equals(st)) {
//                        st = "C:/Program Files/Adobe/Acrobat 7.0/Reader/AcroRd32.exe";
//                        mixnfix.gui.App.setProperty("acrobat", st);
//                    }
//                    Library.executeCommand("\"" + st + "\" " + baseDir + "/"+ baseName + ".pdf", false);

                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }
}

class MFCopiarEnunciadoDosQuesitos
    extends AbstractAction {
    private ModelProva _modelProva;
    public MFCopiarEnunciadoDosQuesitos(ModelProva m) {
        super("Copiar Enunciados dos Quesitos");
        _modelProva = m;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            hardwork(e);
        }
        catch (Exception x) {
            x.printStackTrace();
        }
    }

    private void hardwork(ActionEvent e) throws Exception {
        ModelMF m = (ModelMF) _modelProva.getModeInstituicao().getModelMF();

        ModelInstituicao mi = null;

        { // choose instituicao
            ArrayList<ModelInstituicao> list = m.getInstituicoes();
            HashMap<String, ModelInstituicao> map = new HashMap<String, ModelInstituicao> ();
            Object names[] = new Object[list.size()];
            int k = 0;
            for (ModelInstituicao i : list) {
                String name = i.getInstituicao().getNome() + " (" + i.getInstituicao().getId() + ")";
                map.put(name, i);
                names[k++] = name;
            }
            Object choice = JOptionPane.showInputDialog(
                (Component) e.getSource(),
                "Escolha Instituiçao?",
                "Instituição",
                JOptionPane.QUESTION_MESSAGE,
                null,
                names,
                names[list.indexOf(_modelProva.getModeInstituicao())]);
            if (choice == null)
                return;

            mi = map.get(choice);


        } // choose instituicao

        ModelProva provaSource = null;
        { // choose instituicao
            ArrayList<ModelProva> list = mi.getProvas();
            HashMap<String, ModelProva> map = new HashMap<String, ModelProva> ();
            Object names[] = new Object[list.size()];
            int k = 0;
            for (ModelProva i : list) {
                String name = i.getProva().getNome() + " (" + i.getProva().getId() + ")";
                map.put(name, i);
                names[k++] = name;
            }
            Object choice = JOptionPane.showInputDialog(
                (Component) e.getSource(),
                "Escolha Prova?",
                "Prova",
                JOptionPane.QUESTION_MESSAGE,
                null,
                names,
                null);
            if (choice == null)
                return;

            provaSource = map.get(choice);

        } // choose instituicao


        if (provaSource == null)
            throw new RuntimeException("OOOOpppppsssss");



        ArrayList<Quesito> quesitosS = provaSource.getProvaStructure().getQuesitos();
        ArrayList<Quesito> quesitosT = _modelProva.getProvaStructure().getQuesitos();

        for (int i=0;i<Math.min(quesitosS.size(),quesitosT.size());i++) {
            Quesito qS = quesitosS.get(i);
            Quesito qT = quesitosT.get(i);
            copiarImagensSeNecessario(_modelProva, provaSource, qS);
            qT.setEnunciado(qS.getEnunciado().getCopy());
        }
    }

    private void copiarImagensSeNecessario(ModelProva mpTarget, ModelProva mpSource, Model m) throws IOException, Exception {
        if (mpTarget == mpSource)return;
        List<String> names = getImageFileNames(m);
        for (String name : names) {
            String freshName = mpTarget.assureImageFileName(name);
            Library.copyFile(new File(mpSource.getPath() + "/" + name), new File(mpTarget.getPath() + "/" + freshName));
        }
    }

    public List<String> getImageFileNames(Model source) {

        Stack<Model> nodes = new Stack<Model> ();
        ArrayList<String> names = new ArrayList<String> ();

        nodes.add(source);

        ArrayList<Papel> papeis = new ArrayList<Papel> ();
        while (!nodes.isEmpty()) {
            Model m = nodes.pop();
            if (m instanceof Quesito) {
                Quesito q = (Quesito) m;
                Papel p;
                p = q.getEnunciado();
                if (p != null)
                    papeis.add(p);
                p = q.getSolucao();
                if (p != null)
                    papeis.add(p);

                // add childs
                for (Model child: q.getChilds())
                    nodes.push(child);
            }
            else if (m instanceof ItemQuesito) {
                ItemQuesito iq = (ItemQuesito) m;
                Papel p;
                p = iq.getEnunciado();
                if (p != null)
                    papeis.add(p);
            }
            else if (m instanceof Grupo) {
                Grupo g = (Grupo) m;
                Papel p = g.getEnunciado();
                if (p != null)
                    papeis.add(p);

                // add childs
                for (Model child: g.getChilds())
                    nodes.push(child);
            }
        }

        for (Papel p : papeis) {
            for (Model m : p.getContents()) {
                if (m instanceof Imagem) {
                    Imagem i = (Imagem) m;
                    String name = new File(i.getProperty("src")).getName();
                    if (!names.contains(name))
                        names.add(name);
                }
            }
        }
        return names;
    }
}

class MFActionTrocarValorAcertoDeTodosOsQuesitos
    extends AbstractAction {
    private ProvaStructure _provaStructure;
    public MFActionTrocarValorAcertoDeTodosOsQuesitos(ProvaStructure ps) {
        super("Trocar valor acerto de todos os quesitos");
        _provaStructure = ps;
    }

    public void actionPerformed(ActionEvent e) {
        ArrayList<Quesito> qs = _provaStructure.getQuesitos();
        if (qs.size() == 0) {
            JOptionPane.showMessageDialog( (Component) e.getSource(), "Nao há quesitos nesta prova!");
            return;
        }

        Object o = JOptionPane.showInputDialog( (Component) e.getSource(), "Valor: ", String.format("%.3f", qs.get(0).getValorAcerto()));
        if (o == null)
            return;

       String st = (String) o;
       double acerto = 0;
       try {
           acerto = Float.parseFloat(st);
       }
       catch (NumberFormatException ex) {
           JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, "Número inválido");
           return;
       }

       for (Quesito q: qs) {
           q.setValorAcerto(acerto);
       }
    }
}

class MFActionTrocarValorErroDeTodosOsQuesitos
    extends AbstractAction {
    private ProvaStructure _provaStructure;
    public MFActionTrocarValorErroDeTodosOsQuesitos(ProvaStructure ps) {
        super("Trocar valor do erro de todos os quesitos");
        _provaStructure = ps;
    }

    public void actionPerformed(ActionEvent e) {
        ArrayList<Quesito> qs = _provaStructure.getQuesitos();
        if (qs.size() == 0) {
            JOptionPane.showMessageDialog( (Component) e.getSource(), "Nao há quesitos nesta prova!");
            return;
        }

        Object o = JOptionPane.showInputDialog( (Component) e.getSource(), "Valor: ", String.format("%.3f", qs.get(0).getValorAcerto()));
        if (o == null)
            return;

       String st = (String) o;
       double acerto = 0;
       try {
           acerto = Float.parseFloat(st);
       }
       catch (NumberFormatException ex) {
           JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, "Número inválido");
           return;
       }

       for (Quesito q: qs) {
           q.setValorErro(acerto);
       }
    }
}


class MFActionReportStatistics
    extends AbstractAction {
    ModelProva _modelProva;
    public MFActionReportStatistics(ModelProva p) {
        super("Gerar Relatório de Estatísticas", Images.Correcao16x16);
        _modelProva = p;
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);

        String file1 = App.getProperty(ConfiguracaoMIXnFIX.reportstatistics);

        String path = (new File(file1)).getParentFile().getAbsolutePath().replace('\\', '/');
        String provaname = _modelProva.getProva().getNome();
        fc.setSelectedFile(new File(path + "/" + provaname + ".pdf"));

        fc.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                StringTokenizer st = new StringTokenizer(f.getName(), ".");
                String last = null;
                while (st.hasMoreTokens()) {
                    last = st.nextToken();
                }
                if (last != null) {
                    last = last.toLowerCase();
                    if ("pdf".equals(last)) {
                        return true;
                    }
                }
                return false;
            }

            public String getDescription() {
                return "PDF (.pdf)";
            }
        });

        int r = fc.showSaveDialog(MainFrame.MAIN_FRAME);
        if (r == JFileChooser.APPROVE_OPTION) {
            try {
                App.setProperty(ConfiguracaoMIXnFIX.reportstatistics, fc.getSelectedFile().getAbsolutePath());
                ReportStatistics rs = new ReportStatistics(_modelProva);

                String workdir = _modelProva.getPath().getCanonicalPath().replace('\\','/') + "/";
                String baseName = fc.getSelectedFile().getName().replaceAll(".pdf","");

                rs.write(workdir + baseName + ".tex");

                String outputdir = fc.getSelectedFile().getParentFile().getCanonicalPath().replace('\\', '/')+"/";

                //
                // compile TEX to PDF
                String command = App.getConfiguracao().getCommandCompileTEX2PDF(workdir, outputdir, "\"" + workdir +"/"+ baseName + ".tex\"");
                String dir = workdir.replace("\\","/").replace(" ","\\ ");
                int status = Library.executeCommand(command,dir,null,true);
                if (status != 0) {
                    JOptionPane.showMessageDialog((Component) e.getSource(), "Problema no arquivo .tex ao rodar o comando pdflatex");
                    return;
                }            

                // open PDF
                command = App.getConfiguracao().getCommandOpenPDF(outputdir + baseName + ".pdf");
                Library.executeCommand(command, false);                
                
                
//                // _provaStructure.setProperty();
//                int status = Library.executeCommand("pdflatex -halt-on-error" +
//                    " -include-directory=\"" + workdir +"\" " +
//                    " -output-directory=\"" + outputdir +"\" " +
//                    " -aux-directory=\"" + workdir +"\" " +
//                    " \"" + workdir + baseName + ".tex\" ", true);
//
//                if (status != 0) {
//                    JOptionPane.showMessageDialog( (Component) e.getSource(), "Problema no arquivo .tex ao rodar o comando pdflatex");
//                    return;
//                }
//
//                String st = mixnfix.gui.App.getProperty("acrobat");
//                if (st == null || "".equals(st)) {
//                    st = "C:/Program Files/Adobe/Acrobat 7.0/Reader/AcroRd32.exe";
//                    mixnfix.gui.App.setProperty("acrobat", st);
//                }
//
//                Library.executeCommand("\"" + st + "\" " + outputdir + baseName +".pdf", false);
            }
            catch (IOException ex1) {
                ex1.printStackTrace();
            }
            catch (SQLException ex1) {
                ex1.printStackTrace();
            }
            catch (Exception ex1) {
                ex1.printStackTrace();
            }
        }
    }
}

class MFActionReportStatisticsAll
    extends AbstractAction {
    ModelInstituicao _modelInstituicao;
    public MFActionReportStatisticsAll(ModelInstituicao p) {
        super("Gerar Relatório de Estatísticas", Images.Correcao16x16);
        _modelInstituicao = p;
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        String file1 = App.getProperty(ConfiguracaoMIXnFIX.reportstatisticsall);
        String path = (new File(file1)).getAbsolutePath().replace('\\', '/');

        fc.setSelectedFile(new File(path));
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int r = fc.showSaveDialog(MainFrame.MAIN_FRAME);
        if (r == JFileChooser.APPROVE_OPTION) {
            try {
                App.setProperty(ConfiguracaoMIXnFIX.reportstatisticsall, fc.getSelectedFile().getAbsolutePath());
                String outputdir = fc.getSelectedFile().getCanonicalPath().replace('\\', '/')+"/";

                for (ModelProva mp: _modelInstituicao.getProvas()) {
                    ReportStatistics rs = new ReportStatistics(mp);

                    String workdir = mp.getPath().getCanonicalPath().replace('\\','/') + "/";
                    String baseName = mp.getProva().getNome();

                    rs.write(workdir + baseName + ".tex");

                    //
                    // compile TEX to PDF
                    String command = App.getConfiguracao().getCommandCompileTEX2PDF(workdir, outputdir, "\"" + workdir +"/"+ baseName + ".tex\"");
                    String dir = workdir.replace("\\","/").replace(" ","\\ ");
                    int status = Library.executeCommand(command,dir,null,true);
                    if (status != 0) {
                        JOptionPane.showMessageDialog((Component) e.getSource(), "Problema no arquivo .tex ao rodar o comando pdflatex");
                        return;
                    }            

                    // open PDF
                    command = App.getConfiguracao().getCommandOpenPDF(outputdir + baseName + ".pdf");
                    Library.executeCommand(command, false); 
                    
  //                    // _provaStructure.setProperty();
//                    int status = Library.executeCommand("pdflatex -halt-on-error" +
//                        " -include-directory=\"" + workdir +"\" " +
//                        " -output-directory=\"" + outputdir +"\" " +
//                        " -aux-directory=\"" + workdir +"\" " +
//                        " \"" + workdir + baseName + ".tex\" ", true);
//
//                    if (status != 0) {
//                        JOptionPane.showMessageDialog( (Component) e.getSource(), "Problema no arquivo .tex ao rodar o comando pdflatex");
//                        return;
//                    }
                }

                JOptionPane.showMessageDialog( (Component) e.getSource(), "Sucesso!");
            }
            catch (IOException ex1) {
                ex1.printStackTrace();
            }
            catch (SQLException ex1) {
                ex1.printStackTrace();
            }
            catch (Exception ex1) {
                ex1.printStackTrace();
            }
        }
    }
}

class MFActionTrocarNumeroDeDigitosQuesitoNumerico
    extends AbstractAction {
    private Quesito _quesito;
    public MFActionTrocarNumeroDeDigitosQuesitoNumerico(Quesito q) {
        super("Alterar núm. dígitos quesito numérico");
        _quesito = q;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            actionHardWork();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private boolean _ok;
    private void actionHardWork() throws SQLException {
        int tipo = _quesito.getTipo();
        if (tipo != Quesito.TIPO_NUMERICO_99) {
            JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME,"Não é quesito numérico");
            return;
        }

        String st =JOptionPane.showInputDialog(MainFrame.MAIN_FRAME,"Digitos",""+_quesito.getNumDigitosQuesitoNumerico());
        if (st == null)
            return;

        int n = 2;
        try {
            n = Integer.parseInt(st);
        }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME,"Número inválido: "+st);
            return;
        }
        _quesito.setNumDigitosQuesitoNumerico(n);
    }
}

class MFActionAddColetaQuestionario
    extends AbstractAction {
    ModelProva _modelProva;
    public MFActionAddColetaQuestionario(ModelProva p) {
        super("Adicionar Coleta de Questionário", Images.Correcao16x16);
        _modelProva = p;
    }

    public void actionPerformed(ActionEvent e) {
        Object result = JOptionPane.showInputDialog("Nome da Coleta do Questionário");
        String nome = (String) result;
        if (nome == null)
            return;
        try {
            if (_modelProva.getColetaQuestionarioByName(nome) != null) {
                JOptionPane.showMessageDialog(MainFrame.MAIN_FRAME, "Já existe coleta com nome "+nome, "Mensagem", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            _modelProva.addColetaQuestionario((String) result);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

class MFActionInserirAlunos
    extends AbstractAction {
    ModelInstituicao _modelInstituicao;
    public MFActionInserirAlunos(ModelInstituicao modelInstituicao) {
        super("Inserir Aluno(s)", Images.Aluno16x16);
        _modelInstituicao = modelInstituicao;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            PanelModelAluno pma = new PanelModelAluno(_modelInstituicao);
            pma.run(MainFrame.MAIN_FRAME);
        }
        catch (SQLException ex1) {
            ex1.printStackTrace();
        }
    }
}

class MFActionAtualizarAluno
    extends AbstractAction {
    ModelAluno _modelAluno;
    public MFActionAtualizarAluno(ModelAluno modelAluno) {
        super("Alterar Aluno(s)", Images.Aluno16x16);
        _modelAluno = modelAluno;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            PanelModelAluno pma = new PanelModelAluno(_modelAluno);
            pma.run(MainFrame.MAIN_FRAME);
        }
        catch (SQLException ex1) {
            ex1.printStackTrace();
        }
    }
}

class MFActionAtualizarTurma
    extends AbstractAction {
    ModelTurma _modelTurma;
    public MFActionAtualizarTurma(ModelTurma modelTurma) {
        super("Alterar Turma(s)", Images.Turma16x16);
        _modelTurma = modelTurma;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            PanelModelTurma pma = new PanelModelTurma(_modelTurma);
            pma.run(MainFrame.MAIN_FRAME);
        }
        catch (SQLException ex1) {
            ex1.printStackTrace();
        }
    }
}

class MFActionInserirTurmas
    extends AbstractAction {
    ModelInstituicao _modelInstituicao;
    public MFActionInserirTurmas(ModelInstituicao modelInstituicao) {
        super("Inserir Turma(s)", Images.Turma16x16);
        _modelInstituicao = modelInstituicao;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            PanelModelTurma pma = new PanelModelTurma(_modelInstituicao);
            pma.run(MainFrame.MAIN_FRAME);
        }
        catch (SQLException ex1) {
            ex1.printStackTrace();
        }
    }
}

class MFActionColetarQuestionarios extends AbstractAction {

    ModelColetaQuestionario _modelColetaQuestionario;
    int _tipoFolhaResposta;

    public MFActionColetarQuestionarios(ModelColetaQuestionario modelColetaQuestionario, int tipoFolhaResposta) {
        super("Coletar Questionarios", Images.ColetaQuestionario);
        _modelColetaQuestionario = modelColetaQuestionario;
        _tipoFolhaResposta = tipoFolhaResposta;
    }

    File[] _files;
    public void actionPerformed(ActionEvent e) {

        _files = getFotos();
        if(_files == null || _files.length == 0)
            return;

        Thread t = new Thread(new Runnable() {
            public void run() {
                coletar(_files);
            }
        });

        _panelAcompanhamento.run(MainFrame.MAIN_FRAME,t);

    }

    IFolhaResposta _fr;
    ArrayList<EQ> _bufferEQ = new ArrayList<EQ>();
    static byte _data[] = new byte[10000000];
    boolean _cancel = false;
    PanelAcompanhamento _panelAcompanhamento = new PanelAcompanhamento();


    class PanelAcompanhamento extends JPanel {

        JTextArea _ta = new JTextArea();
        JButton _btn;
        String _st;
        boolean _finished = false;

        public PanelAcompanhamento() {
            super();
            _ta.setFont(new java.awt.Font(_ta.getFont().getFontName(),java.awt.Font.PLAIN,11));
            _ta.setBackground(Color.BLACK);
            _ta.setForeground(Color.YELLOW);
            JPanel panelButtons = new JPanel();
            _btn = new JButton("Cancel");
            _btn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (_finished) {
                        getTopLevelAncestor().setVisible(false);
                    }
                    else {
                        _cancel = true;
                    }
                }
            });
            panelButtons.add(_btn);
            this.setLayout(new BorderLayout());
            this.add(new JScrollPane(_ta),BorderLayout.CENTER);
            this.add(panelButtons,BorderLayout.SOUTH);
        }

        public void writeUI(String st)  {
            _st = st;
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        _ta.append(_st);
                        _ta.append("\n");
                    }
                });
            }
            catch (InvocationTargetException ex) {
                ex.printStackTrace();
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        public void finish() {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        _btn.setText("Fechar");
                        _finished = true;
                    }
                });
            }
            catch (InvocationTargetException ex) {
                ex.printStackTrace();
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        private Thread _thread;
        private boolean _first = true;
        public void run(JFrame frame, Thread t) {
            _thread = t;
            JDialog d = new JDialog(frame, "Acompanhamento", true);
            d.setContentPane(_panelAcompanhamento);
            d.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            d.addWindowListener(new WindowListener() {
                public void windowOpened(WindowEvent e) {}
                public void windowClosing(WindowEvent e){}
                public void windowClosed(WindowEvent e){}
                public void windowIconified(WindowEvent e){}
                public void windowDeiconified(WindowEvent e){}
                public void windowActivated(WindowEvent e){
                    if (_first) {
                        System.out.println("Starting thread...");
                        _thread.start();
                        _first = false;
                    }
                }
                public void windowDeactivated(WindowEvent e){}
            });

            linsoft.gui.util.Library.resizeAndCenterWindow(d, 640,300);
            d.setVisible(true);
        }
    }

    private File[] getFotos() {
        JFileChooser jfc = new JFileChooser();
        jfc.setSelectedFile(new File(App.getProperty("fotosdir")));
        jfc.setMultiSelectionEnabled(true);
        jfc.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                String name = f.getName().toLowerCase();
                return (name.endsWith(".jpg")) ||
                       (name.endsWith(".jpeg"));
            }
            public String getDescription() {
                return "Fotos (.jpg ou .jpeg)";
            }
        });

        int result = jfc.showOpenDialog(MainFrame.MAIN_FRAME);
        if (result == JFileChooser.APPROVE_OPTION) {

            App.setProperty("fotosdir",jfc.getSelectedFile().getAbsolutePath());

            File[] fs = jfc.getSelectedFiles();
            return fs;
        }
        return null;
    }

    private void coletar(File files[]) {
        try {
            coletarHardWork(files);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex1) {
            ex1.printStackTrace();
        }
        _panelAcompanhamento.finish();
    }

    private void coletarHardWork(File files[]) throws SQLException, IOException {
        // {......... do once setup
        _fr = mixnfix.folharesposta.FabricaDeFolhaDeResposta.newFolhaResposta(
            _modelColetaQuestionario.getModelProva().getProvaStructure(),
            _tipoFolhaResposta);

        if (_fr == null)
            throw new RuntimeException("OOoooooppppsss");

        CellMap map = _fr.getCellMapFixo();
        MFI2Java.newCellMap(map.getW(), map.getH(), 50, 1000);
        for (ControlPoint cp : map.getControlPoints()) {
            MFI2Java.addControlPoint(cp.getId(), cp.getX() - map.getX0(), cp.getY() - map.getY0());
        }

        for (Quadrilateral q: map.getQuads()) {
            MFI2Java.addQuad(q.getP0().getId(),q.getP1().getId(),q.getP2().getId(),q.getP3().getId());
        }

        System.out.println("Set Contraints");
        PanelParametrosProcessamentoImagem _params = new PanelParametrosProcessamentoImagem();
        MFI2Java.setConstraints(
            _params.getThresholds(),
            _params.getMinPixelWidth(),
            _params.getMaxPixelWidth(),
            _params.getMinPixelHeight(),
            _params.getMaxPixelHeight(),
            _params.getMinNumPixels(),
            _params.getMaxNumPixels(),
            _params.getPixelDensity(),
            _params.getNumClosest(),
            _params.getMinSide(),
            _params.getAngleTolerance(),
            _params.getTargetRadius(),
            _params.getCorrectSideRatio(),
            _params.getSideRatioTolerance(),
            _params.getPhase(),
            _params.getControlPointRadius(),
            _params.getLeftMargin(),
            _params.getRightMargin(),
            _params.getTopMargin(),
            _params.getBottomMargin());
        // do once setup .......}

        int count = 0;
        int countSucessos = 0;
        int countFalhasLeituras = 0;
        int countFalhasIdentificacao = 0;

        long t0 = System.currentTimeMillis();

        for (File f : files) {

            if (_cancel) {
                _panelAcompanhamento.writeUI("Cancelando...");
                break;
            }

            byte status;
            try {
                status = processar(f);
            }
            catch (IOException ex) {
                status = EQ.PROCESSAMENTO_LEITURA_IMAGEM_FALHOU;
            }

            count++;
            if (status == EQ.PROCESSAMENTO_SUCESSO)
                countSucessos++;
            else if (status == EQ.PROCESSAMENTO_LEITURA_IMAGEM_FALHOU)
                countFalhasLeituras++;
            else if (status == EQ.PROCESSAMENTO_IMAGEM_FALHOU)
                countFalhasIdentificacao++;

            _panelAcompanhamento.writeUI(String.format("%4d/%-4d   Sucesso: %4d   FalhaPC: %4d   FalhaL: %4d  Tempo: %.2f seg.",
                                                       count,
                                                       files.length,
                                                       countSucessos,
                                                       countFalhasIdentificacao,
                                                       countFalhasLeituras,
                                                       (System.currentTimeMillis() - t0) / 1000.0));

            int batchSize = 50;
            if (_bufferEQ.size() >= batchSize) {
                _panelAcompanhamento.writeUI(String.format("Saving..."));
                long tSave = System.currentTimeMillis();
                _modelColetaQuestionario.addEntradasColetaQuestionario(_bufferEQ);
                long tf = System.currentTimeMillis();
                tSave = tf - tSave;
                _panelAcompanhamento.writeUI(String.format("Saved %d registers on database on %.3f seg. Total time until now %.3f seg. Time per reg. %.3f seg.", batchSize, tSave / 1000.0, (tf - t0) / 1000.0, (tf - t0) / (1000.0 * batchSize)));
                _bufferEQ.clear();
            }

        }

        if (_bufferEQ.size() > 0) {
            _panelAcompanhamento.writeUI("Saving last registers on database");
            _modelColetaQuestionario.addEntradasColetaQuestionario(_bufferEQ);
            _bufferEQ.clear();
        }

        _panelAcompanhamento.writeUI("Processamento Finalizado!");
        _panelAcompanhamento.writeUI(String.format("Fotos                    :  %d",count));
        _panelAcompanhamento.writeUI(String.format("Sucesso                  :  %d (%.0f%%)",countSucessos,100.0*countSucessos/(double)count));
        _panelAcompanhamento.writeUI(String.format("Falha Pontos de Controle :  %d (%.0f%%)",countFalhasIdentificacao,100.0*countFalhasIdentificacao/(double)count));
        _panelAcompanhamento.writeUI(String.format("Falha Leitura            :  %d (%.0f%%)",countFalhasLeituras,100.0*countFalhasLeituras/(double)count));
        _panelAcompanhamento.writeUI(String.format("Tempo                    :  %.2f seg.",(System.currentTimeMillis()-t0)/1000.0));
        _panelAcompanhamento.writeUI(String.format("Tempo por Foto           :  %.2f seg./foto",(System.currentTimeMillis()-t0)/(count*1000.0)));
        _panelAcompanhamento.writeUI(String.format("Fotos por Segundo        :  %.2f fotos/seg",(count*1000.0)/(System.currentTimeMillis()-t0)));
        _panelAcompanhamento.writeUI(String.format("Fotos por Hora           :  %.2f fotos/hora",(count*1000.0*3600)/(System.currentTimeMillis()-t0)));

    }

    private byte processar(File fotoFile) throws IOException {

        BufferedImage image = ImageIO.read(fotoFile);

        double controlPoints[] = new double[1000];
        _data = MFI2Java.ensureBuffer(_data, image);
        MFI2Java.loadImageToBuffer(image, _data);
        boolean b = MFI2Java.fitToImage(_data, image.getWidth(), image.getHeight(), controlPoints);

        if (!b) {
            return EQ.PROCESSAMENTO_IMAGEM_FALHOU;
        }

        CellMap mapFixo = _fr.getCellMapFixo();

        for (ControlPoint cp : mapFixo.getControlPoints()) {
            cp.setImageXY(controlPoints[2 * cp.getId()], controlPoints[2 * cp.getId() + 1]);
        }

        // data to save on buffer
        PanelParametrosProcessamentoImagem _params = new PanelParametrosProcessamentoImagem();
        int threshold = 0;
        int phase = _params.getPhase();
        int ncps = mapFixo.getNumControlPoints();
        double cps[] = new double[2*ncps];
        System.arraycopy(controlPoints,0,cps,0,2*ncps);
        // data to save on buffer

        _fr.inicializarQuesitos(0);

        { // obter intensidades da imagem para a parte variável da prova
            CellMap mapVariavel = _fr.getCellMapVariavel();
            double[] x = new double[2];
            double[] y = new double[2];
            for (Cell cell : mapVariavel.getCells()) {
                x[0] = cell.getX() - mapVariavel.getX0();
                x[1] = cell.getY() - mapVariavel.getY0();
                double intensity = MFI2Java.sampleCell(
                    x[0], x[1],
                    cell.getW0(), cell.getH0(),
                    cell.getW1(), cell.getH1(),
                    cell.getW2(), cell.getH2(),
                    cell.getWhiteSampleSet());
                cell.setImageIntensity(intensity);
                MFI2Java.tranformPoints(x, y, 1);
                cell.setImageXY(y[0], y[1]);
                // System.out.println(String.format("Mapping of (%.3f,%.3f) -> (%.3f,%.3f)", x[0], x[1], y[0], y[1]));
            }
        } // obter intensidades da imagem para a parte variável da prova

        { // calcular os campos marcados para cada quesito
            CellMap cellMapVariavel = _fr.getCellMapVariavel();

            HashMap<String, Integer> mapCampoValor = new HashMap<String, Integer> ();

            java.util.List<Field> fields = cellMapVariavel.getAllFields();
            for (Field f : fields) {
                if (f instanceof OptionField) {
                    OptionField of = (OptionField) f;
                    of.evaluateFromImageIntensity(_params.getGapLevel());
                    mapCampoValor.put(of.getNome(), of.getValue());
                    // System.out.println("" + of.getNome() + " = " + of.getValue());
                }
                else if (f instanceof BinaryField) {
                    BinaryField bf = (BinaryField) f;
                    bf.evaluateFromImageIntensity(_params.getSeparationLevel());
                }
            }

            // data to save on buffer
            _bufferEQ.add(new EQ(EQ.PROCESSAMENTO_SUCESSO, fotoFile, threshold, phase, _tipoFolhaResposta, cps, mapCampoValor, _modelColetaQuestionario));
            // data to save on buffer

            return EQ.PROCESSAMENTO_SUCESSO;
        }
    }
}


class MFActionRemoverEntradasColetaQuestionario extends AbstractAction {
    ModelColetaQuestionario _modelColetaQuestionario;
    ArrayList<ModelEntradaColetaQuestionario> _entradas;

    public MFActionRemoverEntradasColetaQuestionario(ModelColetaQuestionario mcq, ArrayList<ModelEntradaColetaQuestionario> entradas) {
        super("Remover Entradas", Images.ColetaQuestionario);
        _modelColetaQuestionario = mcq;
        _entradas = entradas;
    }

    public void actionPerformed(ActionEvent e) {
            try {
                this._modelColetaQuestionario.removeEntradasColetaQuestionario(_entradas);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            }
    }
}


class MFActionColetarProvas extends AbstractAction {

    ModelProvaCorrecao _modelProvaCorrecao;

    int _tipoFolhaResposta;

    public MFActionColetarProvas(ModelProvaCorrecao modelProvaCorrecao, int tipoFolhaResposta) {
        super("Coletar Questionarios", Images.Prova16x16);
        _modelProvaCorrecao = modelProvaCorrecao;
        _tipoFolhaResposta = tipoFolhaResposta;
    }

    File[] _files;
    public void actionPerformed(ActionEvent e) {

        _files = getFotos();
        if(_files == null || _files.length == 0)
            return;

        Thread t = new Thread(new Runnable() {
            public void run() {
                coletar(_files);
            }
        });

        _panelAcompanhamento.run(MainFrame.MAIN_FRAME,t);

    }

    IFolhaResposta _fr;
    ArrayList<EC> _bufferEC = new ArrayList<EC>();
    static byte _data[] = new byte[10000000];
    boolean _cancel = false;
    PanelAcompanhamento _panelAcompanhamento = new PanelAcompanhamento();

    class PanelAcompanhamento extends JPanel {

        JTextArea _ta = new JTextArea();
        JButton _btn;
        String _st;
        boolean _finished = false;

        public PanelAcompanhamento() {
            super();
            _ta.setFont(new java.awt.Font(_ta.getFont().getFontName(),java.awt.Font.PLAIN,11));
            _ta.setBackground(Color.BLACK);
            _ta.setForeground(Color.GREEN);
            JPanel panelButtons = new JPanel();
            _btn = new JButton("Cancel");
            _btn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (_finished) {
                        getTopLevelAncestor().setVisible(false);
                    }
                    else {
                        _cancel = true;
                    }
                }
            });
            panelButtons.add(_btn);
            this.setLayout(new BorderLayout());
            this.add(new JScrollPane(_ta),BorderLayout.CENTER);
            this.add(panelButtons,BorderLayout.SOUTH);
        }

        public void writeUI(String st)  {
            _st = st;
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        _ta.append(_st);
                        _ta.append("\n");
                    }
                });
            }
            catch (InvocationTargetException ex) {
                ex.printStackTrace();
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        public void finish() {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        _btn.setText("Fechar");
                        _finished = true;
                    }
                });
            }
            catch (InvocationTargetException ex) {
                ex.printStackTrace();
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        private Thread _thread;
        private boolean _first = true;
        public void run(JFrame frame, Thread t) {
            _thread = t;
            JDialog d = new JDialog(frame, "Acompanhamento", true);
            d.setContentPane(_panelAcompanhamento);
            d.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            d.addWindowListener(new WindowListener() {
                public void windowOpened(WindowEvent e) {}
                public void windowClosing(WindowEvent e){}
                public void windowClosed(WindowEvent e){}
                public void windowIconified(WindowEvent e){}
                public void windowDeiconified(WindowEvent e){}
                public void windowActivated(WindowEvent e){
                    if (_first) {
                        System.out.println("Starting thread...");
                        _thread.start();
                        _first = false;
                    }
                }
                public void windowDeactivated(WindowEvent e){}
            });

            linsoft.gui.util.Library.resizeAndCenterWindow(d, 640,300);
            d.setVisible(true);
        }
    }

    private File[] getFotos() {
        JFileChooser jfc = new JFileChooser();
        jfc.setSelectedFile(new File(App.getProperty("fotosdir")));
        jfc.setMultiSelectionEnabled(true);
        jfc.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                String name = f.getName().toLowerCase();
                return (name.endsWith(".jpg")) ||
                       (name.endsWith(".jpeg"));
            }
            public String getDescription() {
                return "Fotos (.jpg ou .jpeg)";
            }
        });

        int result = jfc.showOpenDialog(MainFrame.MAIN_FRAME);
        if (result == JFileChooser.APPROVE_OPTION) {

            App.setProperty("fotosdir",jfc.getSelectedFile().getAbsolutePath());

            File[] fs = jfc.getSelectedFiles();
            return fs;
        }
        return null;
    }

    private void coletar(File files[]) {
        try {
            coletarHardWork(files);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex1) {
            ex1.printStackTrace();
        }
        _panelAcompanhamento.finish();
    }

    HashSet<String> _matriculasOk = new HashSet<String>();
    HashSet<String> _matriculasFalhas = new HashSet<String>();

    private void coletarHardWork(File files[]) throws SQLException, IOException {

        // {......... do once setup
        _fr = mixnfix.folharesposta.FabricaDeFolhaDeResposta.newFolhaResposta(
            _modelProvaCorrecao.getModelProva().getProvaStructure(),
            _tipoFolhaResposta);

        if (_fr == null)
            throw new RuntimeException("OOoooooppppsss");

        CellMap map = _fr.getCellMapFixo();
        MFI2Java.newCellMap(map.getW(), map.getH(), 50, 1000);
        for (ControlPoint cp : map.getControlPoints()) {
            MFI2Java.addControlPoint(cp.getId(), cp.getX() - map.getX0(), cp.getY() - map.getY0());
        }

        for (Quadrilateral q: map.getQuads()) {
            MFI2Java.addQuad(q.getP0().getId(),q.getP1().getId(),q.getP2().getId(),q.getP3().getId());
        }

        System.out.println("Set Contraints");
        PanelParametrosProcessamentoImagem _params = new PanelParametrosProcessamentoImagem();
        MFI2Java.setConstraints(
            _params.getThresholds(),
            _params.getMinPixelWidth(),
            _params.getMaxPixelWidth(),
            _params.getMinPixelHeight(),
            _params.getMaxPixelHeight(),
            _params.getMinNumPixels(),
            _params.getMaxNumPixels(),
            _params.getPixelDensity(),
            _params.getNumClosest(),
            _params.getMinSide(),
            _params.getAngleTolerance(),
            _params.getTargetRadius(),
            _params.getCorrectSideRatio(),
            _params.getSideRatioTolerance(),
            _params.getPhase(),
            _params.getControlPointRadius(),
            _params.getLeftMargin(),
            _params.getRightMargin(),
            _params.getTopMargin(),
            _params.getBottomMargin());
        // do once setup .......}

        _matriculasOk.clear();
        _matriculasFalhas.clear();
        _bufferEC.clear();

        int count = 0;
        int sucessos = 0;
        int falhamatricula = 0;
        int falhatipo = 0;
        int falhaprocessamento = 0;
        int duplicatas = 0;

        // contar o tempo
        long t0 = System.currentTimeMillis();

        // carregar mapa com matricula -> aluno
        mountMapMatricula2MAPC();

        for (File f : files) {

            if (_cancel) {
                _panelAcompanhamento.writeUI("Cancelando...");
                break;
            }

            byte status;
            try {
                status = processar(f);
            }
            catch (IOException ex) {
                status = EC.PROCESSAMENTO_IMAGEM_FALHOU;
            }

            count++;
            if (status == EC.PROCESSAMENTO_SUCESSO)
                sucessos++;
            else if (status == EC.PROCESSAMENTO_IMAGEM_FALHOU)
                falhaprocessamento++;
            else if (status == EC.PROCESSAMENTO_TIPO_PROVA_FALHOU)
                falhatipo++;
            else if (status == EC.PROCESSAMENTO_MATRICULA_FALHOU)
                falhamatricula++;
            else if (status == EC.PROCESSAMENTO_DUPLICATA)
                duplicatas++;

            _panelAcompanhamento.writeUI(String.format("%4d/%-4d   Suc. %4d   FPC. %4d   FT. %4d  FM. %4d  FD. %4d  T: %.2f seg.",
                                                       count,
                                                       files.length,
                                                       sucessos,
                                                       falhaprocessamento,
                                                       falhatipo,
                                                       falhamatricula,
                                                       duplicatas,
                                                       (System.currentTimeMillis() - t0) / 1000.0));

            int batchSize = 50;
            if (_bufferEC.size() >= batchSize) {
                _panelAcompanhamento.writeUI(String.format("Saving..."));
                long tSave = System.currentTimeMillis();
                _modelProvaCorrecao.addEntradasProvaCorrecao(_bufferEC);
                long tf = System.currentTimeMillis();
                tSave = tf - tSave;
                _panelAcompanhamento.writeUI(String.format("Saved %d registers on database on %.3f seg. Total time until now %.3f seg. Time per reg. %.3f seg.", batchSize, tSave / 1000.0, (tf - t0) / 1000.0, (tf - t0) / (1000.0 * batchSize)));
                _bufferEC.clear();
            }

        }

        if (_bufferEC.size() > 0) {
            _panelAcompanhamento.writeUI("Saving last registers on database");
            _modelProvaCorrecao.addEntradasProvaCorrecao(_bufferEC);
            _bufferEC.clear();
        }

        _panelAcompanhamento.writeUI("Processamento Finalizado!");
        _panelAcompanhamento.writeUI(String.format("Fotos                    :  %d",count));
        _panelAcompanhamento.writeUI(String.format("Sucesso                  :  %d (%.0f%%)",sucessos,100.0*sucessos/(double)count));
        _panelAcompanhamento.writeUI(String.format("Falha Pontos de Controle :  %d (%.0f%%)",falhaprocessamento,100.0*falhaprocessamento/(double)count));
        _panelAcompanhamento.writeUI(String.format("Falha Tipo               :  %d (%.0f%%)",falhatipo,100.0*falhatipo/(double)count));
        _panelAcompanhamento.writeUI(String.format("Falha Matrícula          :  %d (%.0f%%)",falhamatricula,100.0*falhamatricula/(double)count));
        _panelAcompanhamento.writeUI(String.format("Mat. Duplicata           :  %d (%.0f%%)",duplicatas,100.0*duplicatas/(double)count));
        _panelAcompanhamento.writeUI(String.format("Tempo                    :  %.2f seg.",(System.currentTimeMillis()-t0)/1000.0));
        _panelAcompanhamento.writeUI(String.format("Tempo por Foto           :  %.2f seg./foto",(System.currentTimeMillis()-t0)/(count*1000.0)));
        _panelAcompanhamento.writeUI(String.format("Fotos por Segundo        :  %.2f fotos/seg",(count*1000.0)/(System.currentTimeMillis()-t0)));
        _panelAcompanhamento.writeUI(String.format("Fotos por Hora           :  %.2f fotos/hora",(count*1000.0*3600)/(System.currentTimeMillis()-t0)));

    }

    HashMap<String,ModelAlunoProvaCorrecao> _mapMatricula2MAPC = new HashMap<String,ModelAlunoProvaCorrecao>();
    private void mountMapMatricula2MAPC() {
        _mapMatricula2MAPC.clear();
        java.util.List<ModelAlunoProvaCorrecao> list = _modelProvaCorrecao.getAlunosProvaCorrecao();
        for (ModelAlunoProvaCorrecao mapc: list) {
            _mapMatricula2MAPC.put(mapc.getAluno().getMatricula(),mapc);
        }
    }

    public String reverse(String st) {
        String result = "";
        for (int i=st.length()-1;i>=0;i--) {
            result += st.charAt(i);
        }
        return result;
    }

    private byte processar(File fotoFile) throws IOException {

        BufferedImage image = ImageIO.read(fotoFile);

        double controlPoints[] = new double[1000];
        _data = MFI2Java.ensureBuffer(_data, image);
        MFI2Java.loadImageToBuffer(image, _data);
        boolean b = MFI2Java.fitToImage(_data, image.getWidth(), image.getHeight(), controlPoints);

        if (!b) {
            return EC.PROCESSAMENTO_IMAGEM_FALHOU;
        }

        CellMap mapFixo = _fr.getCellMapFixo();

        for (ControlPoint cp : mapFixo.getControlPoints()) {
            cp.setImageXY(controlPoints[2 * cp.getId()], controlPoints[2 * cp.getId() + 1]);
        }

        // data to save on buffer
        PanelParametrosProcessamentoImagem _params = new PanelParametrosProcessamentoImagem();
        int threshold = 0;
        int phase = _params.getPhase();
        int ncps = mapFixo.getNumControlPoints();
        double cps[] = new double[2*ncps];
        System.arraycopy(controlPoints,0,cps,0,2*ncps);
        // data to save on buffer

        { // obter intensidades da imagem
            double[] x = new double[2];
            double[] y = new double[2];
            ArrayList<Cell> cells = new ArrayList<Cell> ();
            mapFixo.getField(CellMap.MULTICAMPO_ID).getCells(cells);
            mapFixo.getField(CellMap.MULTICAMPO_CODIGO_MIXNFIX).getCells(cells);
            for (Cell cell : cells) {
                x[0] = cell.getX() - mapFixo.getX0();
                x[1] = cell.getY() - mapFixo.getY0();
                double intensity = MFI2Java.sampleCell(
                    x[0], x[1],
                    cell.getW0(), cell.getH0(),
                    cell.getW1(), cell.getH1(),
                    cell.getW2(), cell.getH2(),
                    cell.getWhiteSampleSet());
                cell.setImageIntensity(intensity);
                MFI2Java.tranformPoints(x, y, 1);
                cell.setImageXY(y[0], y[1]);
                // System.out.println(Strin_fr.format("Mapping of (%.3f,%.3f) -> (%.3f,%.3f)", x[0], x[1], y[0], y[1]));
            }
        } // obter intensidades da imagem

        // verificar se é possível obter o código da prova
        BinaryField mfCode = (BinaryField) mapFixo.getField(CellMap.MULTICAMPO_CODIGO_MIXNFIX);
        boolean codeword[] = new boolean[mfCode.getNumCells()];
        mfCode.evaluateFromImageIntensity(_params.getSeparationLevel());
        mfCode.getValue(codeword);
        int indice = Prova2TeX.readnumber(codeword, 0, 20);
        int tipowithgolay = Prova2TeX.readnumber(codeword, 20, 24);
        int tipo = Prova2TeX.calcularNumeroNoCodigoDeGolay(tipowithgolay);

        // obter o identificador do aluno
        MultiField mfId = (MultiField) mapFixo.getField(CellMap.MULTICAMPO_ID);
        String idAluno = "";
        for (Field f : mfId.getFields()) {
            OptionField of = (OptionField) f;
            of.evaluateFromImageIntensity(_params.getGapLevel());
            int digit = of.getValue();
            if (digit == OptionField.BLANK || digit == OptionField.BLANK || digit == OptionField.NOT_EVALUATED)
                idAluno += "?";
            else
                idAluno += digit;
        }

        //@todo adjust this thing!s
//        // gambiarra para casar nao a matricula completa, mas substrings
//        int count = 0;
//        for (String matricula: _mapMatricula2MAPC.keySet()) {
//            if (idAluno.indexOf(matricula) != -1) {
//                System.out.println("Matricula: "+matricula+" casa com "+ idAluno);
//                idAluno = matricula;
//                count++;
//            }
//            /*
//            else if (reverse(idAluno).indexOf(matricula) != -1) {
//                System.out.println("Matricula: "+matricula+" casa com "+ idAluno);
//                idAluno = matricula;
//                count++;
//            }*/
//        }
//        if (count > 1) {
//            System.out.println("PROBLEMA! marcacao aluno é substring de mais de uma matricula");
//            return EC.PROCESSAMENTO_MATRICULA_FALHOU;
//        }
        // gambiarra para casar nao a matricula completa, mas substrings


        ModelAlunoProvaCorrecao mapc = this._mapMatricula2MAPC.get(idAluno);

        if (indice != _modelProvaCorrecao.getModelProva().getProvaStructure().getIndex()) {
            // data to save on buffer
            // _bufferEC.add(new EC(EC.PROCESSAMENTO_TIPO_PROVA_FALHOU, fotoFile, threshold, phase, -1, cps, null, mapc));
            // data to save on buffer

            return EC.PROCESSAMENTO_TIPO_PROVA_FALHOU;
        }

        if (mapc == null) {
            _matriculasFalhas.add(idAluno);
            System.out.println("Matricula Falhou: "+idAluno);

            // data to save on buffer
            // _bufferEC.add(new EC(EC.PROCESSAMENTO_MATRICULA_FALHOU, fotoFile, threshold, phase, tipo, cps, null, mapc));
            // data to save on buffer

            return EC.PROCESSAMENTO_MATRICULA_FALHOU;
        }
        /*
        else if (_matriculasOk.contains(idAluno)) {
            return EC.PROCESSAMENTO_DUPLICATA;
        }*/

        _matriculasOk.add(idAluno);

        _fr.inicializarQuesitos(tipo);

        { // obter intensidades da imagem para a parte variável da prova
            CellMap mapVariavel = _fr.getCellMapVariavel();
            double[] x = new double[2];
            double[] y = new double[2];
            for (Cell cell : mapVariavel.getCells()) {
                x[0] = cell.getX() - mapVariavel.getX0();
                x[1] = cell.getY() - mapVariavel.getY0();
                double intensity = MFI2Java.sampleCell(
                    x[0], x[1],
                    cell.getW0(), cell.getH0(),
                    cell.getW1(), cell.getH1(),
                    cell.getW2(), cell.getH2(),
                    cell.getWhiteSampleSet());
                cell.setImageIntensity(intensity);
                MFI2Java.tranformPoints(x, y, 1);
                cell.setImageXY(y[0], y[1]);
                // System.out.println(Strin_fr.format("Mapping of (%.3f,%.3f) -> (%.3f,%.3f)", x[0], x[1], y[0], y[1]));
            }
        } // obter intensidades da imagem para a parte variável da prova

        { // calcular os campos marcados para cada quesito
            CellMap cellMapVariavel = _fr.getCellMapVariavel();

            HashMap<String, Integer> mapCampoValor = new HashMap<String, Integer> ();

            java.util.List<Field> fields = cellMapVariavel.getAllFields();
            for (Field f : fields) {
                if (f instanceof OptionField) {
                    OptionField of = (OptionField) f;
                    of.evaluateFromImageIntensity(_params.getGapLevel());
                    mapCampoValor.put(of.getNome(), of.getValue());
                    // System.out.println("" + of.getNome() + " = " + of.getValue());
                }
                else if (f instanceof BinaryField) {
                    BinaryField bf = (BinaryField) f;
                    bf.evaluateFromImageIntensity(_params.getSeparationLevel());
                }
            }

            // data to save on buffer
            _bufferEC.add(new EC(EC.PROCESSAMENTO_SUCESSO, fotoFile, threshold, phase, tipo, cps, mapCampoValor, mapc));
            // data to save on buffer

            return EC.PROCESSAMENTO_SUCESSO;

        } // calcular os campos marcados para cada quesito

    }
}

class MFActionSelecionarTurmas
    extends AbstractAction {
    ModelAluno _modelAluno;
    public MFActionSelecionarTurmas(ModelAluno modelAluno) {
        super("Selecionar Turmas", Images.Aluno16x16);
        _modelAluno = modelAluno;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            ArrayList<ModelTurma> ats = _modelAluno.getTurmas();
            ArrayList<ModelTurma> ts = _modelAluno.getModelInstituicao().getTurmas();
            ts.removeAll(ats);
            DialgoChooseObjects d = new DialgoChooseObjects(MainFrame.MAIN_FRAME,
                "Turmas para o aluno: " + _modelAluno.getAluno().getNome(), true, ats, ts, new DefaultListCellRenderer() {
                public java.awt.Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    ModelTurma x = (ModelTurma) value;
                    this.setIcon(Images.Turma16x16);
                    this.setText(x.getTurma().getNome());
                    return this;
                }
            });

            linsoft.gui.util.Library.resizeAndCenterWindow(d,500,400);
            d.setVisible(true);

            if (d.isOk()) {
                List list = d.getSelectedObjects();
                // remover turmas
                for (ModelTurma x: ats) {
                    if (!list.contains(x))
                        x.removerAluno(_modelAluno,false);
                }

                // incluir turmas
                for (ModelTurma x: (List<ModelTurma>)list) {
                    if (!ats.contains(x))
                        x.matricularAluno(_modelAluno);
                }
            }
        }
        catch (SQLException ex1) {
            ex1.printStackTrace();
        }
    }
}

class MFActionRelatorioNotasTXT extends AbstractAction {

    ModelInstituicao _modelInstituicao;

    public MFActionRelatorioNotasTXT(ModelInstituicao modelInstituicao) {
        super("Gerando Nota em TXT", Images.Quesito);
        _modelInstituicao = modelInstituicao;
    }

    private String _dirName;
    private ArrayList<ModelProvaCorrecao> _correcoes;

    public void actionPerformed(ActionEvent e) {


        // escolher as correçoes
        ViewEspera ve = new ViewEspera(MainFrame.MAIN_FRAME);
        ArrayList<ModelProvaCorrecao> correcoes = (ArrayList<ModelProvaCorrecao>) ve.doWork(new linsoft.gui.util.IWorker() {
            public Object doWork() {
                try {
                    return collect();
                }
                catch (SQLException ex) {
                    return null;
                }
            }
        }
        , "Obtendo Correções...", 160, 120, Images.MIXnFIX_transparent);

        if (correcoes == null) {
            JOptionPane.showMessageDialog( (Component) e.getSource(), "Problema!");
            return;
        }

        DialogChoosePathAndCorrecoes d = new DialogChoosePathAndCorrecoes(MainFrame.MAIN_FRAME,
            "Correções", true, correcoes, new DefaultListCellRenderer() {
            public java.awt.Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                ModelProvaCorrecao x = (ModelProvaCorrecao) value;
                this.setIcon(Images.Correcao16x16);
                String name = x.getModelProva().getProva().getNome() + "_" + x.getProvaCorrecao().getNome();
                this.setText(name);
                return this;
            }
        });
        linsoft.gui.util.Library.resizeAndCenterWindow(d,640,440);
        d.setVisible(true);
        if (!d.isOk())
            return;

        _correcoes =
            new ArrayList<ModelProvaCorrecao>(
            (List<ModelProvaCorrecao>)
            d.getSelectedObjects());

        _dirName = d.getPath();


        // start wait window for possible long task
        // preparar documento e janela de visualização de
        // documento
        ve = new ViewEspera(MainFrame.MAIN_FRAME);
        ve.doWork(new linsoft.gui.util.IWorker() {
            public Object doWork() {
                hardwork();
                return null;
            }
        }
        , "Gerando Notas em TXT...", 160, 120, Images.MIXnFIX_transparent);

        // message
        JOptionPane.showMessageDialog( MainFrame.MAIN_FRAME, "Arquivos TXT gerados com sucesso!");

    }

    private ArrayList<ModelProvaCorrecao> collect() throws SQLException {
        ArrayList<ModelProvaCorrecao> pcs = new ArrayList<ModelProvaCorrecao>();
        for (ModelProva mp: _modelInstituicao.getProvas()) {
            pcs.addAll(mp.getProvasCorrecoes());
        }
        return pcs;
    }

    private void hardwork() {
        try {
            for (ModelProvaCorrecao modelProvaCorrecao: _correcoes) {
                String provaName = modelProvaCorrecao.getModelProva().getProva().getNome();
                String filename = _dirName + provaName + "_" + modelProvaCorrecao.getProvaCorrecao().getNome() + ".txt";
                System.out.println("Gerando relatório " + filename + "...");
                PrintWriter file = new PrintWriter(new FileOutputStream(filename));
                write(modelProvaCorrecao,file);
                file.close();
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void write(ModelProvaCorrecao modelProvaCorrecao, PrintWriter pw) throws IOException, SQLException {
        ArrayList<ModelEntradaProvaCorrecao> entradasProvaCorrecao = modelProvaCorrecao.getEntradasProvaCorrecaoOrdenadas();
        for (ModelEntradaProvaCorrecao modelEntradaProvaCorrecao: entradasProvaCorrecao) {
            Aluno aluno = modelEntradaProvaCorrecao.getAluno();

            modelEntradaProvaCorrecao.preencherProvaComGabaritoCorrente();
            modelEntradaProvaCorrecao.getProvaStructure().avaliarNota();

            double nota = modelEntradaProvaCorrecao.getProvaStructure().getNota();
            pw.write(String.format(Locale.ITALIAN,"%-10s%-10.2f\n", aluno.getMatricula(),nota));
        }
    }





    /**
     *
     */
    public class DialogChoosePathAndCorrecoes extends JDialog {
        PanelChooseObjects _panelChooseObjects;
        JTextField _tfPath;
        public DialogChoosePathAndCorrecoes(
            JFrame owner,
            String title,
            boolean modal,
            List correcoes,
            ListCellRenderer renderer) {
            super(owner,title,modal);

            JPanel p = new JPanel();
            p.setLayout(new GridBagLayout());

            _panelChooseObjects = new PanelChooseObjects(new ArrayList(), correcoes, renderer);
            p.add(_panelChooseObjects,new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(5,5,5,5),0,0));


            // label dir saída
            JPanel panelPathCorrecoes = new JPanel();
            panelPathCorrecoes.setLayout(new GridBagLayout());
            panelPathCorrecoes.add(new JLabel("Diretório de Saída:"),new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0));

            // text field dir saída
            String st = App.getProperty("directoryofnotastxt");
            if (st == null || "null".equals(st))
                st ="";
            _tfPath = new JTextField(st);
            _tfPath.setEditable(false);
            _tfPath.setPreferredSize(new Dimension(250,24));
            panelPathCorrecoes.add(_tfPath,new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0));

            // create buttons and their actions
            JButton btnSaida = new JButton("...");
            btnSaida.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    changePath();
                }
            });
            panelPathCorrecoes.add(btnSaida,new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0));

            p.add(panelPathCorrecoes,new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0));

            // create buttons and their actions
            JButton btnOk = new JButton("OK");
            btnOk.setMargin(new Insets(2,2,2,2));
            btnOk.setPreferredSize(new Dimension(60,25));
            btnOk.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ok();
                }
            });

            JButton btnCancel = new JButton("Cancel");
            btnCancel.setMargin(new Insets(2,2,2,2));
            btnCancel.setPreferredSize(new Dimension(60,25));
            btnCancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    cancel();
                }
            });

            JPanel panelButtons = new JPanel();
            panelButtons.setLayout(new GridBagLayout());
            panelButtons.add(btnOk,new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(0,0,0,10),0,0));
            panelButtons.add(btnCancel,new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(0,10,0,0),0,0));
            p.add(panelButtons,new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(5,5,20,5),0,0));

            this.setContentPane(p);
        }

        public void changePath() {
            JFileChooser fc = new JFileChooser();
            fc.setMultiSelectionEnabled(false);
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setSelectedFile(new File(App.getProperty("directoryofnotastxt")));

            int r = fc.showSaveDialog(MainFrame.MAIN_FRAME);
            if (r == JFileChooser.APPROVE_OPTION) {
                App.setProperty("directoryofnotastxt", fc.getSelectedFile().getAbsolutePath());
                String dirName = fc.getSelectedFile().getAbsolutePath()+"/";
                _tfPath.setText(dirName);
            }
        }

        private boolean _ok;
        private void ok() {
            _ok = true;
            this.setVisible(false);
        }

        private void cancel() {
            _ok = false;
            this.setVisible(false);
        }

        public boolean isOk() {
            return _ok;
        }

        public java.util.List getSelectedObjects() {
            return _panelChooseObjects.getSelectedObjects();
        }

        public String getPath() {
            return _tfPath.getText();
        }

    }

}
