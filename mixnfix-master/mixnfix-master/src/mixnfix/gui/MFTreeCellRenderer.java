package mixnfix.gui;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import mixnfix.modelo.Aluno;
import mixnfix.modelo.AlunoCursoInstancia;
import mixnfix.modelo.AlunoTurma;
import mixnfix.modelo.Avaliacao;
import mixnfix.modelo.AvaliacaoAluno;
import mixnfix.modelo.Correcao;
import mixnfix.modelo.Curso;
import mixnfix.modelo.CursoInstancia;
import mixnfix.modelo.Turma;
import mixnfix.prova.Grupo;
import mixnfix.prova.ItemQuesito;
import mixnfix.prova.ProvaStructure;
import mixnfix.prova.Quesito;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MFTreeCellRenderer
    extends DefaultTreeCellRenderer {
    public Component getTreeCellRendererComponent(
        JTree tree,
        Object value,
        boolean sel,
        boolean expanded,
        boolean leaf,
        int row,
        boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        // user object
        Object obj = ( (DefaultMutableTreeNode) value).getUserObject();

        if (!(value instanceof MFTreeNode))
            return this;

        MFTreeNode node = (MFTreeNode) value;

        switch (node.getType()) {
            case MFTreeNode.TYPE_INSTITUICAO: {
                ModelInstituicao instituicao = (ModelInstituicao) obj;
                this.setText(instituicao.getInstituicao().getNome());
                this.setIcon(Images.Instituicao16x16);
            }
            break;
            case MFTreeNode.TYPE_ROOT: {
                // Instituicao instituicao = (Instituicao) obj;
                this.setText("Pastas");
                this.setIcon(Images.Folder16x16);
            }
            break;
            case MFTreeNode.TYPE_CURSOS: {
                // Instituicao instituicao = (Instituicao) obj;
                this.setText("Cursos");
                this.setIcon(Images.Folder16x16);
            }
            break;
            case MFTreeNode.TYPE_TURMAS: {
                // Instituicao instituicao = (Instituicao) obj;
                this.setText("Turmas");
                this.setIcon(Images.Folder16x16);
            }
            break;
            case MFTreeNode.TYPE_ALUNOS: {
                // Instituicao instituicao = (Instituicao) obj;
                this.setText("Alunos");
                this.setIcon(Images.Folder16x16);
            }
            break;
            case MFTreeNode.TYPE_PROVAS: {
                // Instituicao instituicao = (Instituicao) obj;
                this.setText("Provas");
                this.setIcon(Images.Folder16x16);
            }
            break;
            case MFTreeNode.TYPE_CURSO: {
                Curso curso = (Curso) obj;
                this.setText(curso.getNome());
                this.setIcon(Images.Curso16x16);
            }
            break;
            case MFTreeNode.TYPE_TURMA: {
                Turma turma = (Turma) obj;
                this.setText(turma.getNome());
                this.setIcon(Images.Turma16x16);
            }
            break;
            case MFTreeNode.TYPE_ALUNO: {
                Aluno aluno = (Aluno) obj;
                this.setText(aluno.getNome() + "(" + aluno.getMatricula() + ")");
                this.setIcon(Images.Aluno16x16);
            }
            break;
            case MFTreeNode.TYPE_ALUNO_CURSO_INSTANCIA: {
                AlunoCursoInstancia alunoCursoInstancia = (AlunoCursoInstancia) obj;
                this.setText(alunoCursoInstancia.getAluno_AlunoCursoInstancia().getNome());
                this.setIcon(Images.Aluno16x16);
            }
            break;
            case MFTreeNode.TYPE_PROVA: {
                ModelProva modelProva = (ModelProva) obj;
                this.setText(modelProva.getProva().getNome());
                if (modelProva.getStructureChanged()) {
                    this.setIcon(Images.Prova16x16_Modificada);
                }
                else {
                    this.setIcon(Images.Prova16x16);
                }
            }
            break;
            case MFTreeNode.TYPE_CORRECAO: {
                Correcao correcao = (Correcao) obj;
                this.setText(correcao.getAluno_Correcao().getNome() + " " + String.format("%3.2f", correcao.getNota()));
                this.setIcon(Images.Correcao16x16);
            }
            break;
            case MFTreeNode.TYPE_CURSO_INSTANCIA: {
                ModelCursoInstancia modelCursoInstancia = (ModelCursoInstancia) obj;
                CursoInstancia ci = modelCursoInstancia.getCursoInstancia();
                this.setText(ci.getCurso_CursoInstancia().getNome() + " " + ci.getPeriodo_CursoInstancia().getId_periodo());
            }
            break;
            case MFTreeNode.TYPE_AVALIACAO: {
                Avaliacao avaliacao = (Avaliacao) obj;
                this.setText(avaliacao.getNome());
            }
            break;
            case MFTreeNode.TYPE_AVALIACAO_ALUNO: {
                AvaliacaoAluno avaliacaoAluno = (AvaliacaoAluno) obj;
                this.setText("" + avaliacaoAluno.getAlunoCursoInstancia_AvaliacaoAluno().getAluno_AlunoCursoInstancia().getNome() + ": " + avaliacaoAluno.getNota());
            }
            break;
            case MFTreeNode.TYPE_ALUNO_TURMA: {
                AlunoTurma alunoTurma = (AlunoTurma) obj;
                this.setText("" + alunoTurma.getAluno_AlunoTurma().getNome());
            }
            break;
            case MFTreeNode.TYPE_CORRECOES_ANTIGAS: {
                this.setText("Correções Antigas");
                this.setIcon(Images.Folder16x16);
            }
            break;
            case MFTreeNode.TYPE_PROVA_CORRECAO: {
                ModelProvaCorrecao modelProvaCorrecao = (ModelProvaCorrecao) obj;
                this.setText(modelProvaCorrecao.getProvaCorrecao().getNome());
                this.setIcon(Images.Correcao16x16);
            }
            break;
            case MFTreeNode.TYPE_COLETA_QUESTIONARIO: {
                ModelColetaQuestionario modelColetaQuestionario = (ModelColetaQuestionario) obj;
                this.setText(modelColetaQuestionario.getColetaQuestionario().getNome());
                this.setIcon(Images.ColetaQuestionario);
            }
            break;
            case MFTreeNode.TYPE_PROVA_STRUCTURE: {
                ProvaStructure ps = (ProvaStructure) obj;
                // this.setText("Estrutura");
                this.setText("Cabeçalho");
                this.setIcon(Images.Prova16x16);
            }
            break;
            case MFTreeNode.TYPE_PROVA_GRUPO: {
                Grupo g = (Grupo) obj;
                this.setText(g.getTag()+(g.isTravado() ? "(Lock)" : ""));
                this.setIcon(Images.Grupo);
            }
            break;
            case MFTreeNode.TYPE_PROVA_QUESITO: {
                Quesito g = (Quesito) obj;
                this.setText(g.getTag()+(g.isTravado() ? "(Lock)" : "")+" R: "+g.getTextRespostaCorreta());
                this.setIcon(Images.Quesito);
            }
            break;
            case MFTreeNode.TYPE_PROVA_ITEM_QUESITO: {
                ItemQuesito g = (ItemQuesito) obj;
                this.setText(g.getTag());
                this.setIcon(Images.ItemQuesito);
            }
            break;
        }

        // System.out.println("Value class: "+value.getClass());
        return this;
    }
}
