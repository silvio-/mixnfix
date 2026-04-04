/*
 * ConfiguracaoMIXnFIX.java
 *
 * Created on September 18, 2007, 10:33 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package mixnfix.gui;

import java.util.HashMap;

/**
 *
 * @author lauro
 */
public class ConfiguracaoMIXnFIX extends Configuracao {
    private static final HashMap<String,String> _mapDefaults = new HashMap<String,String>();
    
    public static final String openPDF = "openPDF";
    static { _mapDefaults.put(openPDF,"evince $pdffile");}
    
    public static final String openXLS = "openXLS";
    static { _mapDefaults.put(openXLS,"soffice $xlsfile");}

    public static final String openHTML = "openHTML";
    static { _mapDefaults.put(openHTML,"firefox $htmlfile");}
        
    public static final String convertEPS2PDF = "convertEPS2PDF";
    static { _mapDefaults.put(convertEPS2PDF,"epstopdf $pdffile");}
    
    public static final String compileTEX2PDF = "compileTEX2PDF";
    // TeX Live pdflatex. TEXINPUTS is set by the caller so $sourcedir is searched for inputs.
    static { _mapDefaults.put(compileTEX2PDF,"pdflatex -halt-on-error -interaction=nonstopmode -output-directory=$outputdir $texfile");}

    public static final String triangulate = "triangulate";
    // Shewchuk's Triangle: install via `apt install triangle-bin` or build from
    // https://www.cs.cmu.edu/~quake/triangle.html (gcc -O2 -DLINUX -o triangle triangle.c -lm)
    static { _mapDefaults.put(triangulate,"triangle $file");}
    
    public static final String estatisticaPorQuestaoDirReport = "estatisticaPorQuestaoDirReport";
    static { _mapDefaults.put(estatisticaPorQuestaoDirReport,"."); }    
    
    public static final String instituicaoreport = "instituicaoreport";
    static { _mapDefaults.put(instituicaoreport,"."); }    
    
    public static final String reportstatisticsall = "reportstatisticsall";
    static { _mapDefaults.put(reportstatisticsall,"."); }    
    
    public static final String reportstatistics = "reportstatistics";
    static { _mapDefaults.put(reportstatistics,"."); }    

    public static final String alunosdir = "alunosdir";
    static { _mapDefaults.put(alunosdir,"alunos.txt"); }  
    
    public static final String arquivoExportacaoAluno = "arquivoExportacaoAluno";
    static { _mapDefaults.put(arquivoExportacaoAluno,"alunos.txt"); }  

    public static final String datadir = "datadir";
    static { _mapDefaults.put(datadir,"data"); }

    public static final String bancoquesitosdir = "bancoquesitosdir";
    static { _mapDefaults.put(bancoquesitosdir,"banco"); }

    public static final String dbname = "dbname";
    static { _mapDefaults.put(dbname,"db"); }    
    
    /** Creates a new instance of ConfiguracaoMIXnFIX */
    public ConfiguracaoMIXnFIX(String rootPath) {
        super(rootPath);
    }

    public String getDefaultValue(String propertyName) {
    	String result = _mapDefaults.get(propertyName);
        if (result == null)
        	return "";
        else return result;
    }
    
    public String getCommandCompileTEX2PDF(String sourcedir, String outputdir, String texfile) {
        String template = this.getProperty(this.compileTEX2PDF);
        sourcedir = sourcedir.replace("\\","/").replace(" ","\\ "); 
        outputdir = outputdir.replace("\\","/").replace(" ","\\ "); 
        texfile = texfile.replace("\\","/").replace(" ","\\ ");         
        template = template.replace("$sourcedir",sourcedir);
        template = template.replace("$outputdir",outputdir);
        template = template.replace("$texfile",texfile);
        return template.replace("\n", " ");
    }
    
    public String getCommandOpenPDF(String pdffile) {
        String template = this.getProperty(this.openPDF);
        pdffile = pdffile.replace("\\","/").replace(" ","\\ "); 
        template = template.replace("$pdffile",pdffile);
        return template.replace("\n", " ");
    }
    
    public String getCommandOpenXLS(String xlsfile) {
        String template = this.getProperty(this.openXLS);
        xlsfile = xlsfile.replace("\\","/").replace(" ","\\ "); 
        template = template.replace("$xlsfile",xlsfile);
        return template.replace("\n", " ");
    }
    
    public String getCommandOpenHTML(String htmlfile) {
        String template = this.getProperty(this.openHTML);
        htmlfile = htmlfile.replace("\\","/").replace(" ","\\ "); 
        template = template.replace("$htmlfile",htmlfile);
        return template.replace("\n", " ");
    }
    
    public String getCommandTriangulate(String file) {
        String template = this.getProperty(this.triangulate);
        file = file.replace("\\","/").replace(" ","\\ "); 
        template = template.replace("$file",file);
        return template.replace("\n", " ");
    }

}
