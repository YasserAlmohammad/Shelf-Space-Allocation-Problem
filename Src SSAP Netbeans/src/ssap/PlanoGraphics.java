/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ssap;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author Yasser
 */
public class PlanoGraphics {
    int width=0; //width of drawing
    int height=0; //height of drawing
    int color_panel_height=0;
    public static final int W_FACTOR=5; //10 pixels for each 1 cm of width
    public static final int H_FACTOR=30; //height of shelf in pixels
    public static final int FACING_SHIFT=2; //distance between facing drawing and shelf line
    public static final int COLOR_RECT_HEIGHT=20;
    public static final int COLOR_RECT_WIDTH=20;
    public static final int COLOR_PANEL_WIDTH=COLOR_RECT_WIDTH*5;
    public static final int ID_WIDTH=30;
    BufferedImage image;
    BufferedImage colorImage;
    PlanoGraphics(int shelf_width /* width of a shelf*/,int shelves_count /* number of shelves*/,int prod_count,Component c){
        width=shelf_width*W_FACTOR+ID_WIDTH;
        height=shelves_count*H_FACTOR;
        color_panel_height=COLOR_RECT_HEIGHT*prod_count;
        image=(BufferedImage)c.createImage(width, height);//will be created by a component;
        colorImage=(BufferedImage)c.createImage(COLOR_PANEL_WIDTH,color_panel_height);
    }
    
    BufferedImage getImage(){
        return image;
    }
    BufferedImage getColorImage(){
        return colorImage;
    }
    //draw on a background buffer
    public void drawShelves( ArrayList<ProductDrawingData> []shelves){
        Graphics2D g=(Graphics2D)image.getGraphics();
        Graphics2D g2=(Graphics2D)colorImage.getGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, COLOR_PANEL_WIDTH, color_panel_height+FACING_SHIFT);
        int c_x=COLOR_RECT_WIDTH+FACING_SHIFT; //for color box text
        int c_y=0; //for color box
        for(int i=0;i<shelves.length;i++){
            ShelfDrawing shelf=new ShelfDrawing(width,H_FACTOR,2, ""+(i+1),ID_WIDTH, 0, i*H_FACTOR);
            ArrayList<ProductDrawing> list=new ArrayList<>();
            for(int j=0;j<shelves[i].size();j++){
                ProductDrawingData data=shelves[i].get(j);
                //give random strong colors
                Color c=new Color((int)(Math.random()*255)%200,((int)(Math.random()*255))%200,((int)(Math.random()*255))%200);
                ProductDrawing pDraw=new ProductDrawing(data.facing_width*W_FACTOR, H_FACTOR, data.count,
                         FACING_SHIFT, c, data.name, data.family, data.start_pos*W_FACTOR+ID_WIDTH, i*H_FACTOR,data.id);
                list.add(pDraw);
                //add explanation to color and name list
                g2.setColor(Color.BLACK);
                g2.drawRect(0, c_y, COLOR_PANEL_WIDTH, COLOR_RECT_HEIGHT);
                g2.setColor(c);
                g2.fillRect(0, c_y+1, COLOR_RECT_WIDTH, COLOR_RECT_HEIGHT-FACING_SHIFT);
                c_y+=COLOR_RECT_HEIGHT;
                g2.drawString(pDraw.name,c_x, c_y-COLOR_RECT_HEIGHT/3);
            }
            shelf.draw(g, list);
        }
    }
}
