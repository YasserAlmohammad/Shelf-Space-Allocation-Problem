/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ssap;

/**
 *
 * @author Yasser
 */
public class ProductDrawingData {
    String name;
    String family;
    int count;
    int start_pos;
    int facing_width;
    int id; //will be used to define color ranges.
    
    public ProductDrawingData(String name, String family, int count, int start_pos, int facing_width,int id) {
        this.name = name;
        this.family = family;
        this.count = count;
        this.start_pos = start_pos;
        this.facing_width = facing_width;
        this.id=id;
    }
    
}
