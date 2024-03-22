/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ssap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Yasser
 */
public class ProductDrawing {
    int facing_width;
    int facing_height;
    int num_of_facings;
    int facing_space;
    Color c;
    String name;
    String family;
    int id;
    int x_start;
    int y_start;

    public ProductDrawing(int facing_width, int facing_height, int num_of_facings, int facing_space, Color c, String name, String family, int x_start, int y_start,int id) {
        this.facing_width = facing_width;
        this.facing_height = facing_height;
        this.num_of_facings = num_of_facings;
        this.facing_space = facing_space;
        this.c = c;
        this.name = name;
        this.family = family;
        this.x_start = x_start;
        this.y_start = y_start;
        this.id=id;
    }
    
    //draw all facings of that product
    public void draw(Graphics2D g){
      //  g.setColor(c);
      //  g.draw(new Rectangle2D.Double(x,y,facing_width,facing_height));
        g.setColor(c);
        int x=x_start;
        int y=y_start;
        for(int i=0;i<num_of_facings;i++){
            g.setColor(c);
            g.fillRect(x,y+facing_space,facing_width-facing_space,facing_height-facing_space*2);
            g.setColor(Color.WHITE);
            g.drawString(id+"",x+facing_space,y+facing_space+facing_height/2);
            x=x+facing_width; //to prevent overlapping of lines.
        }
    }
    
}
