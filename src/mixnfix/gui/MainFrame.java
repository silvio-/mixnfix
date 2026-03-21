package mixnfix.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultListCellRenderer;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import mixnfix.Model;
import mixnfix.modelo.Aluno;
import mixnfix.modelo.Instituicao;
import mixnfix.modelo.Turma;

public class MainFrame extends JFrame {
    PanelCadastro _panelCadastro;

    private static ModelMF _modelMF = new ModelMF();

    public static ModelMF getModelMF() {
        return _modelMF;
    }

    public MainFrame() {
        //
    	this.setTitle(this.getCustomTitle());
        this.setIconImage(Images.SmallIcon);
        
        try {
            _panelCadastro = new PanelCadastro();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        this.setJMenuBar(mountMenuBar());

        // _desktopPane.add(_panelCadastro);
        // this.setContentPane(_desktopPane);
        this.setContentPane(_panelCadastro);

    
        // schedule an event to monitor the used memory 
		Timer t = new Timer(true);
		t.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				// TODO Auto-generated method stub
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						setTitle(getCustomTitle());
					}
				});
			}
		}, 0, 2000);      
		
		//
		plugActions();
    }
    
    
	private void plugActions() {
		// map the keys of the keyboard
		ActionMap amap = _panelCadastro.getActionMap();
		amap.put("gc", ACTION_GARBAGE_COLLECTION);

		InputMap imap = _panelCadastro.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		imap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G,java.awt.event.KeyEvent.CTRL_DOWN_MASK, false), "gc");
	}

	private static Action ACTION_GARBAGE_COLLECTION = new AbstractAction("gc") {
		public void actionPerformed(ActionEvent actionEvent) {
			System.out.println("gc");
			Runtime.getRuntime().gc();
		}
	};


    private JMenuBar mountMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuCadastro = new JMenu("Cadastro");
        JMenu menuOperacoes = new JMenu("Operações");

        ActionListener a = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    // viewData( ( (JMenuItem) e.getSource()).getText());
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        JMenuItem menuInstituicoes = new JMenuItem("Instituições",Images.Instituicao16x16);
        menuInstituicoes.addActionListener(a);
        JMenuItem menuProvas = new JMenuItem("Provas",Images.Prova16x16);
        menuProvas.addActionListener(a);

        JMenuItem menuNovaInstituicao = new JMenuItem(new MFActionAdicionarInstituicao(MainFrame.getModelMF()));

        JMenuItem menuSettings = new JMenuItem("Settings");
        menuSettings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    settings();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        JMenuItem menuImagePattern = new JMenuItem("Image Pattern");
        menuImagePattern.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    imagePattern();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        menuBar.add(menuCadastro);
        menuBar.add(menuOperacoes);
        menuOperacoes.add(menuNovaInstituicao);
        menuOperacoes.add(menuImagePattern);
        menuOperacoes.add(menuSettings);
        return menuBar;
    }

    /**
     *  Settings
     */
    public void settings() throws Exception {
//        JDialog d = new JDialog(this,"Settings",true);
//        d.setContentPane(new PanelEditarSettings());
//        linsoft.gui.util.Library.resizeAndCenterWindow(d,420,470);
//        d.setVisible(true);

        JDialog d = new JDialog(this,"Config",true);
        d.setContentPane(new PanelConfig());
        d.pack();
        linsoft.gui.util.Library.resizeAndCenterWindow(d,d.getWidth(),d.getHeight());
        d.setVisible(true);
    
    }

    public static void start() {
//        try {
//            UIManager.setLookAndFeel(
//                "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }

        // nova segurança
        // {.......
//        Calendar c = GregorianCalendar.getInstance();
//        c.set(2007,11,31,0,0,0);
//        if (System.currentTimeMillis() > c.getTimeInMillis()) {
//            JOptionPane.showMessageDialog(new JFrame(),"Sistema expirou! Entre em contato com o suporte.");
//            System.exit(0);
//        }
        // .......}

        Date d = new Date();

        // System.out.println("Starting "+App.getProperty(ConfiguracaoMIXnFIX.dbname)+"  getName());
        MainFrame f = new MainFrame();
        MAIN_FRAME = f;
        f.setBounds(0, 0, 800, 600);
        linsoft.gui.util.Library.resizeAndCenterWindow(f, 800, 600);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Locale.setDefault(new Locale("pt", "BR"));
        Locale.setDefault(Locale.US);
    }

    public String getCustomTitle() {		
    	long memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    	return String.format("MIXnFIX, 02/01/2008, dbname: %s, datadir: %s, mem.used: %dkb",
    			App.getProperty(ConfiguracaoMIXnFIX.dbname),		
    			App.getProperty(ConfiguracaoMIXnFIX.datadir),
    			memory / 1024
    	);
    }
    
    
    public static MainFrame MAIN_FRAME;

    /**
     * Nova Instituição
     */
    public void criarInstituicao() throws Exception {
        Object result = JOptionPane.showInputDialog(this,"Nova Instituiçao");
        if (result != null) {
            System.out.println("Criar Instituição: "+result);
            App.getRepositorio().inserirInstituicao(""+result);
        }
    }

    /**
     * Importacao de provas.
     */
    public void importarAlunos() throws Exception {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(App.getProperty("alunos2dir")));

        //
        JComboBox jc = new JComboBox();
        {
            for (Instituicao i : (List<Instituicao>) App.getRepositorio().consultarInstituicao())
                jc.addItem(i);

            if (jc.getItemCount() == 0)
                throw new RuntimeException("Nao há instituicao disponivel!");

            // JComboBox -
            jc.setRenderer(new DefaultListCellRenderer() {
                public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    this.setText(((Instituicao) value).getNome());
                    return this;
                }
            });
            JPanel p = new JPanel();
            p.add(new JLabel("Instituição"));
            p.add(jc);
            fc.setAccessory(p);
            //fc.add(jc,JButton.SOUTH);
        }

        int r = fc.showOpenDialog(this);
        if (r == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            App.setProperty("alunos2dir",f.getAbsolutePath());

            // abrir a prova
            System.out.println("Importando alunos "+f.getAbsolutePath()+"...");

            long t0 = System.currentTimeMillis();

            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            int lineNumber = 0;
            while ((line=br.readLine()) != null) {
                lineNumber++;
                try {
                    StringTokenizer t = new StringTokenizer(line, "\t");
                    String matricula = t.nextToken();
                    String nome = t.nextToken();

                    System.out.println("Criando aluno "+nome+" "+matricula);
                    Instituicao i = (Instituicao) jc.getSelectedItem();
                    Aluno a = App.getRepositorio().inserirAluno(nome,matricula,i);

                    while (t.hasMoreTokens()) {
                        String turmaName = t.nextToken();
                        Vector v = App.getRepositorio().consultarTurmaPorInstituicaoNome(turmaName,i);
                        Turma turma = null;
                        if (v.isEmpty()) {
                            turma = App.getRepositorio().inserirTurma(turmaName,i);
                        }
                        else {
                            turma = (Turma) v.get(0);
                        }
                        App.getRepositorio().inserirAlunoTurma(a,turma);
                    }
                }
                catch (Exception ex) {
                    System.out.println("<Alerta> Linha "+lineNumber+" nao pode ser importada");
                }
            }


            long deltaT = System.currentTimeMillis() - t0;
            System.out.println("Tempo para adicionar a galera (mseg): "+deltaT);
        }
    }

    private void imagePattern() throws Exception {
        // PREFERENCE: get preference
        JFileChooser jfc = new JFileChooser();
        jfc.setSelectedFile(new File(App.getProperty("imagePattern")));
        jfc.setMultiSelectionEnabled(false);
        jfc.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                StringTokenizer st = new StringTokenizer(f.getName(),".");
                String last = null;
                while (st.hasMoreTokens()) {
                    last = st.nextToken();
                }
                if (last != null) {
                    last = last.toLowerCase();
                    if ("jpg".equals(last) || "jpeg".equals(last))
                        return true;
                }
                return false;
            }
            public String getDescription() {
                return "Fotos (.jpg ou .jpeg)";
            }
        });

        int result = jfc.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION)
            return;

        File file = jfc.getSelectedFile();
        PanelImagePattern panelImagePattern = new PanelImagePattern(file.getAbsolutePath());

        // PREFERENCE: get preference
        App.setProperty("imagePattern",file.getAbsolutePath());

        JDialog f = new JDialog(this,"Image Pattern",true);
        f.setContentPane(panelImagePattern);
        linsoft.gui.util.Library.resizeAndCenterWindow(f,700,510);
        f.setVisible(true);
    }

    public void select(Model m) {
        this._panelCadastro.select(m);
    }
}
