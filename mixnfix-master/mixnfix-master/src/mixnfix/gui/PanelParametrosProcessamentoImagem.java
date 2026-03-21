package mixnfix.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import linsoft.gui.Input;

class PanelParametrosProcessamentoImagem extends JPanel {
    Input _tfThresholds;
    Input _tfMinPixelWidth;
    Input _tfMaxPixelWidth;
    Input _tfMinPixelHeight;
    Input _tfMaxPixelHeight;
    Input _tfMinNumPixels;
    Input _tfMaxNumPixels;
    Input _tfPixelDensity;
    Input _tfNumClosest;
    Input _tfMinSide;
    Input _tfAngleTolerance;
    Input _tfTargetRadius;
    Input _tfCorrectSideRatio;
    Input _tfSideRatioTolerance;
    Input _tfPhase;
    Input _tfControlPointRadius;
    Input _tfGapLevel;
    Input _tfSeparationLevel;
    Input _tfLeftMargin;
    Input _tfRightMargin;
    Input _tfTopMargin;
    Input _tfBottomMargin;

    public PanelParametrosProcessamentoImagem() {
        this.setLayout(new GridBagLayout());

        int i=0;

        this.add(new JLabel("Thresholds (até 8):"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        _tfThresholds = new Input(App.getConfiguracao(),"thresholds","50,60,70,80,90,100,110,120",Input.TF_LISTA_INTEIROS,130);
        this.add(_tfThresholds,new GridBagConstraints(1,i,2,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Min-Max Pixel Width:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        _tfMinPixelWidth = new Input(App.getConfiguracao(),"minPixelWidth","3",Input.TF_INTEIRO,50);
        this.add(_tfMinPixelWidth,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        _tfMaxPixelWidth = new Input(App.getConfiguracao(),"maxPixelWidth","15",Input.TF_INTEIRO,50);
        this.add(_tfMaxPixelWidth,new GridBagConstraints(2,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Min-Max Pixel Height:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        _tfMinPixelHeight = new Input(App.getConfiguracao(),"minPixelWidth","3",Input.TF_INTEIRO,50);
        this.add(_tfMinPixelHeight,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        _tfMaxPixelHeight = new Input(App.getConfiguracao(),"maxPixelHeight","15",Input.TF_INTEIRO,50);
        this.add(_tfMaxPixelHeight,new GridBagConstraints(2,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Min-Max Num Pixels:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        _tfMinNumPixels = new Input(App.getConfiguracao(),"minNumPixels","3",Input.TF_INTEIRO,50);
        this.add(_tfMinNumPixels,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        _tfMaxNumPixels = new Input(App.getConfiguracao(),"maxNumPixels","15",Input.TF_INTEIRO,50);
        this.add(_tfMaxNumPixels,new GridBagConstraints(2,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Min Pixel Density:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfPixelDensity = new Input(App.getConfiguracao(),"minPixelDensity","0.4",Input.TF_FLOAT,50);
        this.add(_tfPixelDensity,new GridBagConstraints(1,i,2,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Num Closest:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfNumClosest = new Input(App.getConfiguracao(),"numClosest","2",Input.TF_INTEIRO,50);
        this.add(_tfNumClosest,new GridBagConstraints(1,i,2,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Min Side:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfMinSide = new Input(App.getConfiguracao(),"minSide","150",Input.TF_INTEIRO,50);
        this.add(_tfMinSide,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Angle Tolerance:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfAngleTolerance = new Input(App.getConfiguracao(),"angletol","5",Input.TF_FLOAT,50);
        this.add(_tfAngleTolerance,new GridBagConstraints(1,i,2,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Target Radius:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfTargetRadius = new Input(App.getConfiguracao(),"targetRadius","30",Input.TF_INTEIRO,50);
        this.add(_tfTargetRadius,new GridBagConstraints(1,i,2,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Correct Side Ratio:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfCorrectSideRatio = new Input(App.getConfiguracao(),"correctSideRatio","30",Input.TF_FLOAT,50);
        this.add(_tfCorrectSideRatio,new GridBagConstraints(1,i,2,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Side Ratio Tolerance:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfSideRatioTolerance = new Input(App.getConfiguracao(),"correctSideRatio","0.9",Input.TF_FLOAT,50);
        this.add(_tfSideRatioTolerance,new GridBagConstraints(1,i,2,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Phase (0-3):"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfPhase = new Input(App.getConfiguracao(),"phase","0",Input.TF_FLOAT,50);
        this.add(_tfPhase,new GridBagConstraints(1,i,2,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Control Point Radius:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfControlPointRadius = new Input(App.getConfiguracao(),"controlPointsRadius","20",Input.TF_FLOAT,50);
        this.add(_tfControlPointRadius,new GridBagConstraints(1,i,2,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Gap Level:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfGapLevel = new Input(App.getConfiguracao(),"gaplevel","20.0",Input.TF_FLOAT,50);
        this.add(_tfGapLevel,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Separation Level:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfSeparationLevel = new Input(App.getConfiguracao(),"separationlevel","100.0",Input.TF_FLOAT,50);
        this.add(_tfSeparationLevel,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Left Margin:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfLeftMargin = new Input(App.getConfiguracao(),"searchRegionLeftMargin","0",Input.TF_FLOAT,50);
        this.add(_tfLeftMargin,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Right Margin:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfRightMargin = new Input(App.getConfiguracao(),"searchRegionRightMargin","0",Input.TF_FLOAT,50);
        this.add(_tfRightMargin,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Top Margin:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfTopMargin = new Input(App.getConfiguracao(),"searchRegionTopMargin","0",Input.TF_FLOAT,50);
        this.add(_tfTopMargin,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Bottom Margin:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfBottomMargin = new Input(App.getConfiguracao(),"searchRegionBottomMargin","0",Input.TF_FLOAT,50);
        this.add(_tfBottomMargin,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        // add listener
        Input.InputListener il = new Input.InputListener() {
            public void inputValueChanged(Input i) {
                fireChange();
            }
        };
        _tfLeftMargin.addListener(il);
        _tfRightMargin.addListener(il);
        _tfTopMargin.addListener(il);
        _tfBottomMargin.addListener(il);
    }

    public int[] getThresholds() { return _tfThresholds.getListaInteiros(); }
    public int getMinPixelWidth() {  return _tfMinPixelWidth.getInt(); }
    public int getMaxPixelWidth() {  return _tfMaxPixelWidth.getInt(); }
    public int getMinPixelHeight() {  return _tfMinPixelHeight.getInt(); }
    public int getMaxPixelHeight() {  return _tfMaxPixelHeight.getInt(); }
    public int getMinNumPixels() {  return _tfMinNumPixels.getInt(); }
    public int getMaxNumPixels() {  return _tfMaxNumPixels.getInt(); }
    public float getPixelDensity() {  return _tfPixelDensity.getFloat(); }
    public int getNumClosest() {  return _tfNumClosest.getInt(); }
    public float getMinSide() {  return _tfMinSide.getFloat(); }
    public float getAngleTolerance() {  return _tfAngleTolerance.getFloat(); }
    public float getTargetRadius() {  return _tfTargetRadius.getFloat(); }
    public float getCorrectSideRatio() {  return _tfCorrectSideRatio.getFloat(); }
    public float getSideRatioTolerance() {  return _tfSideRatioTolerance.getFloat(); }
    public int getPhase() {  return _tfPhase.getInt(); }
    public float getControlPointRadius() {  return _tfControlPointRadius.getFloat(); }
    public float getGapLevel() {  return _tfGapLevel.getFloat(); }
    public float getSeparationLevel() {  return _tfSeparationLevel.getFloat(); }
    public float getLeftMargin() {  return _tfLeftMargin.getFloat(); }
    public float getRightMargin() {  return _tfRightMargin.getFloat(); }
    public float getTopMargin() {  return _tfTopMargin.getFloat(); }
    public float getBottomMargin() {  return _tfBottomMargin.getFloat(); }



    // listeners
    public interface Listener {
        public void change();
    }
    private void fireChange() {
        for (Listener l: _listeners) {
            l.change();
        }
    }
    private ArrayList<Listener> _listeners = new ArrayList<Listener>();
    public void addListener(Listener il) {
        _listeners.add(il);
    }
    public void removeListener(Listener il) {
        _listeners.remove(il);
    }

}
