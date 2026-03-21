package mixnfix.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import mixnfix.Controller;
import mixnfix.Model;
import mixnfix.modelo.Aluno;
import mixnfix.modelo.AlunoTurma;
import mixnfix.modelo.Curso;
import mixnfix.modelo.CursoInstancia;
import mixnfix.modelo.Instituicao;
import mixnfix.modelo.Periodo;
import mixnfix.modelo.Prova;
import mixnfix.modelo.Turma;
import mixnfix.prova.ProvaStructure;

/**
 * ModelInstituicao
 */
public class ModelInstituicao extends Model {

    private Instituicao _instituicao;

    public ModelInstituicao(ModelMF modelMF, Instituicao instituicao) {
        this.setParent(modelMF);
        _instituicao = instituicao;
    }

    public Instituicao getInstituicao() {
        return _instituicao;
    }

    // as provas desta instituicao
    private ArrayList<ModelProva> _provas;

    /**
     * Get a clone list of the provas correcoes.
     */
    public ArrayList<ModelProva> getProvas() throws SQLException {
        this.syncProvas(false);
        return (ArrayList<ModelProva>) _provas.clone();
    }

    public ModelMF getModelMF() {
        return (ModelMF) this.getParent();
    }

    public ModelAluno getAlunoById(int id) throws SQLException {
        this.syncAlunos(false);
        for (ModelAluno ma: _alunos) {
            if (ma.getAluno().getId() == id)
                return ma;
        }
        return null;
    }

    public ModelAluno getAlunoByMatricula(String matricula) throws SQLException {
        this.syncAlunos(false);
        for (ModelAluno ma: _alunos) {
            if (ma.getAluno().getMatricula().equals(matricula))
                return ma;
        }
        return null;
    }


    /**
     * Synchronize ProvasCorrecoes with database.
     * @throws SQLException
     */
    private void syncProvas(boolean force) throws SQLException {
        if (force || _provas == null) {
            Vector v = App.getRepositorio().consultarProvaPorInstituicao(_instituicao);
            _provas = new ArrayList<ModelProva>(v.size());
            ArrayList<Model> loadList = new ArrayList<Model>(v.size());
            for (Object o: v) {
                ModelProva mp = new ModelProva(this,(Prova) o);
                _provas.add(mp);
                loadList.add(mp);
            }
            this.fireNodesLoaded(loadList);
        }
    }

    /**
     * Add a prova correcao.
     */
    public List<Model> addProvas(File[] files) throws IOException, SQLException {
        this.syncProvas(false);

        ArrayList<Model> provasAdded = new ArrayList<Model>();
        int n = _provas.size();
        for (File f: files) {
            // open prova to see if it is a valid archive
            try {
                ProvaStructure ps = ModelProva.getProvaStructure(f);
                String nome = "p" + System.currentTimeMillis() + ".prova";
                mixnfix.Library.copyFile(f, new File(App.getConfiguracao().getProperty(ConfiguracaoMIXnFIX.datadir)+"/"+nome));
                Prova p = App.getRepositorio().inserirProva(f.getName(), nome, ps.getIndex(), _instituicao);
                ModelProva mp = new ModelProva(this, p);
                provasAdded.add(mp);
                _provas.add(mp);
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        fireNodesAdded(provasAdded,n);
        return provasAdded;
    }

    /**
     * Add a prova correcao.
     */
    public ModelProva addNewProva(String name) throws IOException, SQLException, Exception {
        this.syncProvas(false);

        String dirName = "d"+System.currentTimeMillis();
        File tmp = new File(Controller.TMP_DIR+dirName+"/prova.xml");
        File tmpPontoProva = new File(Controller.TMP_DIR+"/"+name);
        tmp.getParentFile().mkdirs();

        PrintWriter pw = new PrintWriter(new FileOutputStream(tmp));
        pw.append("<prova><cabecalho></cabecalho></prova>");
        pw.flush();
        pw.close();

        Controller.zipDirectory(tmp.getParentFile().getAbsolutePath(),tmpPontoProva.getAbsolutePath());

        ModelProva result = (ModelProva) (addProvas(new File[] {tmpPontoProva})).get(0);
        tmp.delete();
        tmp.getParentFile().delete();
        tmpPontoProva.delete();

        return result;
    }


    public int getNumeroProvas() {
        try {
            return this.getProvas().size();
        }
        catch (SQLException ex) {
            return 0;
        }
    }

    public int getNumeroAlunos() {
        try {
            return this.getAlunos().size();
        }
        catch (SQLException ex) {
            return 0;
        }
    }

    public int getNumeroTurmas() {
        try {
            return this.getTurmas().size();
        }
        catch (SQLException ex) {
            return 0;
        }
    }

    // cascade dá a semântica do remover
    // se cascade = true  -> remove recursivamente tudo
    // se cascade = false -> respeita a integridade referencial
    public void remover(boolean cascade) throws IOException, SQLException {
        if (!cascade) {
            if (this.getNumeroProvas() != 0)
                throw new RuntimeException("Não é possível remover instituição " +
                                     this.getInstituicao().getNome() +
                                     ": existe(m) prova(s) associada(s) a mesma");
            if (this.getNumeroAlunos() != 0)
                throw new RuntimeException("Não é possível remover instituição " +
                                     this.getInstituicao().getNome() +
                                     ": existe(m) aluno(s) associado(s) a mesma");
            if (this.getNumeroTurmas() != 0)
                throw new RuntimeException("Não é possível remover instituição " +
                                     this.getInstituicao().getNome() +
                                     ": existe(m) turma(s) associada(s) a mesma");
        }
        ModelMF mmf = (ModelMF) this.getParent();
        mmf.removerInstituicao(this,cascade);
    }


    /**
     * Remover prova
     */
    public void removerProva(ModelProva mp, boolean cascade) throws IOException, SQLException {
        this.syncProvas(false);

        // nao remove se nao for uma prova da instituicao
        if (!_provas.contains(mp))
            throw new RuntimeException("Oooppps");

        //////////////////////////////// Recursao
        // remover ProvasCorrecoes
        for (ModelProvaCorrecao mpc: mp.getProvasCorrecoes())
            mpc.remover(cascade);

        // remover ColetasQuestionarios
        for (ModelColetaQuestionario mcq: mp.getColetasQuestionario())
            mcq.remover(cascade);
        //////////////////////////////// Recursao

        // remover arquivo
        (new File(App.getConfiguracao().getProperty(ConfiguracaoMIXnFIX.datadir)+"/"+mp.getProva().getFonte())).delete();

        // remover do BD
        App.getRepositorio().removerProva(mp.getProva().getId());

        // remover do modelo
        _provas.remove(mp);

        // sinalizar
        this.fireNodeRemoved(mp);
    }


    // ------------------------------------------------------------------------
    // Alunos

    // as Alunos desta instituicao
    private ArrayList<ModelAluno> _alunos;

    /**
     * Get a clone list of the Alunos correcoes.
     */
    public ArrayList<ModelAluno> getAlunos() throws SQLException {
        this.syncAlunos(false);
        return (ArrayList<ModelAluno>) _alunos.clone();
    }

    /**
     * Synchronize AlunosCorrecoes with database.
     * @throws SQLException
     */
    private void syncAlunos(boolean force) throws SQLException {
        if (force || _alunos == null) {
            Vector v = App.getRepositorio().consultarAlunoPorInstituicao(_instituicao);
            _alunos = new ArrayList<ModelAluno>(v.size());
            ArrayList<Model> loadList = new ArrayList<Model>(v.size());
            for (Object o: v) {
                ModelAluno mp = new ModelAluno(this,(Aluno) o);
                _alunos.add(mp);
                loadList.add(mp);
            }
            this.fireNodesLoaded(loadList);
        }
    }

    /**
     * Add a Aluno correcao.
     */
    public ModelAluno addNewAluno(String matricula, String name) throws SQLException {
        this.syncAlunos(false);
        Aluno a = App.getRepositorio().inserirAluno(name,matricula,this.getInstituicao());
        ModelAluno ma = new ModelAluno(this,a);
        _alunos.add(ma);
        this.fireNodeAdded(ma,_alunos.size()-1);
        return ma;
    }

    /**
     * Add alunos
     */
    public void addAlunos(ArrayList<Aluno> alunos) throws SQLException {
        this.syncAlunos(false);
        for (Aluno aluno: alunos) {
            ModelAluno ma = new ModelAluno(this,aluno);
            _alunos.add(ma);
            this.fireNodeAdded(ma,_alunos.size()-1);
        }
    }

    public void matricularAlunosTurmas(ArrayList<AlunoTurma> alunosTurmas)  throws SQLException {
        for (AlunoTurma alunoTurma : alunosTurmas) {
            ModelTurma modelTurma = this.getTurmaByName(alunoTurma.getTurma_AlunoTurma().getNome());
            if (modelTurma == null)
                continue;
            ModelAluno modelAluno = this.getAlunoByMatricula(alunoTurma.getAluno_AlunoTurma().getMatricula());
            modelTurma.matricularAluno(modelAluno);
        }
    }


    /**
     * Remover aluno
     */
    public void removerAluno(ModelAluno ma, boolean cascade) throws IOException, SQLException {
        this.syncAlunos(false);

        // nao remove se nao for uma prova da instituicao
        if (!_alunos.contains(ma))
            throw new RuntimeException("Oooppps");

        // remover aluno das turmas
        for (ModelTurma mt: ma.getTurmas())
            mt.removerAluno(ma,cascade);

        // remover do BD
        App.getRepositorio().removerAluno(ma.getAluno());

        // remover do modelo
        _alunos.remove(ma);

        // sinalizar
        this.fireNodeRemoved(ma);
    }

    // Alunos
    // ------------------------------------------------------------------------



    // ------------------------------------------------------------------------
    // Turmas

    // as Turmas desta instituicao
    private ArrayList<ModelTurma> _turmas;

    /**
     * Get a clone list of the Turmas correcoes.
     */
    public ArrayList<ModelTurma> getTurmas() throws SQLException {
        this.syncTurmas(false);
        return (ArrayList<ModelTurma>) _turmas.clone();
    }

    /**
     * Synchronize TurmasCorrecoes with database.
     * @throws SQLException
     */
    private void syncTurmas(boolean force) throws SQLException {
        if (force || _turmas == null) {
            Vector v = App.getRepositorio().consultarTurmaPorInstituicao(_instituicao);
            _turmas = new ArrayList<ModelTurma>(v.size());
            ArrayList<Model> loadList = new ArrayList<Model>(v.size());
            for (Object o: v) {
                ModelTurma mp = new ModelTurma(this,(Turma) o);
                _turmas.add(mp);
                loadList.add(mp);
            }
            this.fireNodesLoaded(loadList);
        }
    }

    /**
     * Add a Turma correcao.
     */
    public ModelTurma addNewTurma(String name) throws SQLException  {
        this.syncTurmas(false);
        Turma a = App.getRepositorio().inserirTurma(name,this.getInstituicao());
        ModelTurma ma = new ModelTurma(this,a);
        _turmas.add(ma);
        this.fireNodeAdded(ma,_turmas.size()-1);
        return ma;
    }

    /**
     *
     * @param turmaNome String
     * @return ModelTurma
     */
    public ModelTurma getTurmaByName(String turmaNome) throws SQLException {
        this.syncTurmas(false);
        for (ModelTurma t: _turmas) {
            if (t.getTurma().getNome().equals(turmaNome)) {
                return t;
            }
        }
        return null;
    }

    public ModelProva getProvaByName(String provaNome) throws SQLException {
        this.syncProvas(false);
        for (ModelProva mp: _provas) {
            if (mp.getProva().getNome().equals(provaNome)) {
                return mp;
            }
        }
        return null;
    }

    public ModelProva getProvaByIndice(int indice) throws SQLException {
        this.syncProvas(false);
        for (ModelProva mp: _provas) {
            if (mp.getProva().getIndice() == indice) {
                return mp;
            }
        }
        return null;
    }

    /**
     * Remover a Turma correcao.
     */
    public void removerTurma(ModelTurma ma, boolean cascade) throws IOException, SQLException {
        this.syncTurmas(false);

        // nao remove se nao for uma Turma da instituicao
        if (!_turmas.contains(ma))
            throw new RuntimeException("Oooppps");

        // remover do BD
        App.getRepositorio().removerTurma(ma.getTurma());

        // remove do modelo
        _turmas.remove(ma);

        // sinaliza
        this.fireNodeRemoved(ma);
    }

    // Turmas
    // ------------------------------------------------------------------------


    public ArrayList<ModelTurma> getTurmas(ModelAluno a) throws SQLException {
        ArrayList<ModelTurma> turmas = new ArrayList<ModelTurma>();
        for (ModelTurma t: _turmas) {
            if (t.contains(a))
                turmas.add(t);
        }
        return turmas;
    }

    // as CursoInstancias desta instituicao
    private ArrayList<ModelCursoInstancia> _cursos;

    /**
     * Get a clone list of the CursoInstancias correcoes.
     */
    public List<ModelCursoInstancia> getCursoInstancias() throws SQLException {
        this.syncCursoInstancias(false);
        return (ArrayList<ModelCursoInstancia>) _cursos.clone();
    }

    /**
     * Synchronize CursoInstanciasCorrecoes with database.
     * @throws SQLException
     */
    private void syncCursoInstancias(boolean force) throws SQLException {
        if (force || _cursos == null) {
            Vector v = App.getRepositorio().consultarCursoInstanciaPorInstituicao(_instituicao);
            _cursos = new ArrayList<ModelCursoInstancia>(v.size());
            ArrayList<Model> loadList = new ArrayList<Model>(v.size());
            for (Object o: v) {
                ModelCursoInstancia mp = new ModelCursoInstancia(this,(CursoInstancia) o);
                _cursos.add(mp);
                loadList.add(mp);
            }
            this.fireNodesLoaded(loadList);
        }
    }

    /**
     * Add a CursoInstancia correcao.
     */
    public Model addCursoInstancias(Curso curso, Periodo periodo) throws SQLException {
        this.syncCursoInstancias(false);

        //
        CursoInstancia ci = App.getRepositorio().inserirCursoInstancia(_instituicao,curso,periodo);
        ModelCursoInstancia mci = new ModelCursoInstancia(this,ci);
        _cursos.add(mci);

        fireNodeAdded(mci,_cursos.size()-1);
        return mci;
    }

    /**
     * Remover CursoInstancia da instituicao.
     */
    public void removerCursoInstancia(ModelCursoInstancia mp) throws IOException, SQLException {
        this.syncCursoInstancias(false);

        // nao remove se nao for uma CursoInstancia da instituicao
        if (!_cursos.contains(mp))
            throw new RuntimeException("Oooppps");

        App.getRepositorio().removerCursoInstancia(mp.getCursoInstancia());

        _cursos.remove(mp);

        this.fireNodeRemoved(mp);
    }

    public static String getRelatoriosFalta(ModelInstituicao modelInstituicao) throws SQLException {
        StringBuffer report = new StringBuffer();

        report.append("Alunos Faltando\n\n");
        List<ModelProva> modelsProvas = modelInstituicao.getProvas();
        Collections.sort(modelsProvas, new Comparator() {
            public int compare(Object o1, Object o2) {
                ModelProva mp1 = (ModelProva) o1;
                ModelProva mp2 = (ModelProva) o2;
                return (mp1.getProva().getNome().compareTo(mp2.getProva().getNome()));
            }
        });

        for (ModelProva modelProva: modelsProvas) {
            report.append("Prova: "+modelProva.getProva().getNome() +"\n\n");

            List<ModelProvaCorrecao> modelsProvasCorrecoes = modelProva.getProvasCorrecoes();
            for (ModelProvaCorrecao modelProvaCorrecao : modelsProvasCorrecoes) {
                report.append("Correção: "+modelProvaCorrecao.getProvaCorrecao().getNome() +"\n\n");

                ArrayList<Aluno> alunosSemCorrecao = modelProvaCorrecao.getAlunosSemEntradas();
                for (Aluno aluno: alunosSemCorrecao)
                    report.append(String.format("%-10s%s\n",aluno.getMatricula(),aluno.getNome()));
                report.append("\n\n");
            }
            report.append("\n\n");


        }



        String result = report.toString();
        return result;
    }

    public void adjustImageFileNames() throws Exception {
        List<ModelProva> provas = this.getProvas();
        for (ModelProva p: provas) {
            System.out.println("Corrigindo "+p.getProva().getNome());
            p.getProvaStructure();
            p.adjustImageFileNames();
            p.gravar();
        }
    }

}
