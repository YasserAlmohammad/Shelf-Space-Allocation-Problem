/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ssap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 *
 * @author Yasser
 */
public class ShelfDrawing {
    int shelf_width; //including id
    int shelf_height;
    int line_space;
    Color c=Color.LIGHT_GRAY;
    String shelf_id;
    int x;
    int y;
    int id_width=30;
    public ShelfDrawing(int shelf_width, int shelf_height, int line_space, String shelf_id,int id_width, int x, int y) {
        this.shelf_width = shelf_width;
        this.shelf_height = shelf_height;
        this.line_space = line_space;
        this.shelf_id = shelf_id;
        this.x = x;
        this.y = y;
        this.id_width=id_width;
    }
    
    public void draw(Graphics2D g,ArrayList<ProductDrawing> list){
        //draw shelf id
        g.setColor(Color.WHITE);
        g.drawRect(x, y, id_width-line_space, shelf_height-line_space);
        g.setColor(Color.BLACK);
        g.drawString("S("+shelf_id+")", x+line_space, y+shelf_height/2);
        //draw shelf
        g.setColor(c);
        g.fillRect(x+id_width, y, shelf_width-id_width, shelf_height);
        g.setColor(Color.BLACK);
        g.drawRect(x+id_width, y, shelf_width-id_width, shelf_height);
        //draw products
        for(int i=0;i<list.size();i++){
            list.get(i).draw(g);
        }
    }
}
