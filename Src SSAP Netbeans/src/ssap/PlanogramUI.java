/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ssap;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Yasser
 */
public class PlanogramUI extends javax.swing.JFrame {

    /**
     * Creates new form Planogram
     */
    PlanoGraphics planogram;
    public PlanogramUI(int width,int count,int pcount) {
        initComponents();
        planogram=new PlanoGraphics(width, count,pcount,this);
        shelves=null;
     //   planoPlanel.setPreferredSize(new Dimension(PlanoGraphics.W_FACTOR*SSAP.shelf_length, PlanoGraphics.H_FACTOR*count));
        this.scrollPlano.getViewport().addChangeListener(new ListenShelvesScrolled());
        this.scrollColors.getViewport().addChangeListener(new ListenColorsScrolled());


    }
    public class ListenShelvesScrolled implements ChangeListener{
        public void stateChanged(ChangeEvent e){
          //  planoPlanel.revalidate();
            ((Graphics2D)planoPlanel.getGraphics()).drawImage(planogram.getImage(), null, 0, 0);
        }
    }
    public class ListenColorsScrolled implements ChangeListener{
        public void stateChanged(ChangeEvent e){
          //  planoPlanel.revalidate();
            ((Graphics2D)colorPanel.getGraphics()).drawImage(planogram.getColorImage(), null, 0, 0);
        }
    }
    ArrayList<ProductDrawingData> []shelves;
    public void updateData(ArrayList<ProductDrawingData> []shelves){
        this.shelves=shelves;
        planogram.drawShelves(shelves);
        ((Graphics2D)planoPlanel.getGraphics()).drawImage(planogram.getImage(), null, 0, 0);
        ((Graphics2D)colorPanel.getGraphics()).drawImage(planogram.getColorImage(), null, 0, 0);
  //      repaint();
        
    }
    public void savePlanoGrams(File planoFile){
        try{
            ImageIO.write(planogram.getImage(), "png", planoFile);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        
    }
    
    /*
    @Override
    public void update(Graphics window) {
        super.update(window);
        paint(window);
    }
    */
    
    public void paint(Graphics window) {
        super.paint(window);
        ((Graphics2D)planoPlanel.getGraphics()).drawImage(planogram.getImage(), null, 0, 0);
        ((Graphics2D)colorPanel.getGraphics()).drawImage(planogram.getColorImage(), null, 0, 0);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPlano = new javax.swing.JScrollPane();
        planoPlanel = new javax.swing.JPanel();
        scrollColors = new javax.swing.JScrollPane();
        colorPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        scrollPlano.setBackground(new java.awt.Color(255, 153, 153));
        scrollPlano.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        planoPlanel.setPreferredSize(new java.awt.Dimension(2000, 1500));

        javax.swing.GroupLayout planoPlanelLayout = new javax.swing.GroupLayout(planoPlanel);
        planoPlanel.setLayout(planoPlanelLayout);
        planoPlanelLayout.setHorizontalGroup(
            planoPlanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2000, Short.MAX_VALUE)
        );
        planoPlanelLayout.setVerticalGroup(
            planoPlanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1500, Short.MAX_VALUE)
        );

        scrollPlano.setViewportView(planoPlanel);

        scrollColors.setBackground(new java.awt.Color(204, 255, 51));

        colorPanel.setPreferredSize(new java.awt.Dimension(308, 2292));

        javax.swing.GroupLayout colorPanelLayout = new javax.swing.GroupLayout(colorPanel);
        colorPanel.setLayout(colorPanelLayout);
        colorPanelLayout.setHorizontalGroup(
            colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 308, Short.MAX_VALUE)
        );
        colorPanelLayout.setVerticalGroup(
            colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2292, Short.MAX_VALUE)
        );

        scrollColors.setViewportView(colorPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(scrollColors, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPlano, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollColors, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(scrollPlano, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel colorPanel;
    private javax.swing.JPanel planoPlanel;
    private javax.swing.JScrollPane scrollColors;
    private javax.swing.JScrollPane scrollPlano;
    // End of variables declaration//GEN-END:variables
}
