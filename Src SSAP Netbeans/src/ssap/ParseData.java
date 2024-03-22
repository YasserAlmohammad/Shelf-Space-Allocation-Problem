/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ssap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.jfree.util.Log;

/**
 *passing a file name, read the file content and store it into a String
 *The format of these data files is:
 *number of planes (p), freeze time
 *for each plane i (i=1,...,p):
 *  appearance time, earliest landing time, target landing time,
 *  latest landing time, penalty cost per unit of time for landing
 *  before target, penalty cost per unit of time for landing
 *  after target
 *  for each plane j (j=1,...p): separation time required after 
 *                               i lands before j can land
 */
public class ParseData {
        
        public static final int PRODUCT_ID_INDEX=0; //unique, index location in the data arrays.
        public static final int FACING_LENGTH_INDEX=1;
        public static final int MIN_FACINGS_INDEX=2;
        public static final int MAX_FACINGS_INDEX=3;
        public static final int PROFIT_INDEX=4;

        public static final int SHELF_PART_ID_INDEX=0; //unique
        public static final int SHELF_ID_INDEX=1;
        public static final int SHELF_PRIORITY_COEFFECIENT_INDEX=2;
        public static final int PART_PRIORITY_COEFFECIENT_INDEX=3;
        public static Data parse(String source) {
            Data data=new Data();
            Scanner sc = new Scanner(source);
            data.n_products = sc.nextInt();
            data.n_shelves=sc.nextInt();
            data.shelf_width=sc.nextInt();
            data.products=new int[data.n_products][5];
            data.shelves=new int[data.n_shelves][6];
            data.product_name=new String[data.n_products];
            data.product_family=new String[data.n_products];
            
                    
            for (int i = 0; i < data.n_products; i++) {
                data.product_name[i]=sc.next(); //string
                data.product_family[i]=sc.next(); //string
                data.products[i][0] = sc.nextInt(); // ID
                data.products[i][1] = sc.nextInt(); // facing length
                data.products[i][2] = sc.nextInt(); // minimum number of facings
                data.products[i][3] = sc.nextInt(); // maximum number of facings
                data.products[i][4] = (int)(sc.nextDouble()*SSAP.SCALE_FACTOR); // profit
            }
            
            for (int i = 0; i < data.n_shelves; i++) {
                data.shelves[i][SHELF_PART_ID_INDEX] = sc.nextInt(); //space id
                data.shelves[i][SHELF_ID_INDEX] = sc.nextInt(); // shelf id
                data.shelves[i][SHELF_PRIORITY_COEFFECIENT_INDEX] = (int)(sc.nextDouble()*SSAP.SCALE_FACTOR); // shelf coefficient
                data.shelves[i][PART_PRIORITY_COEFFECIENT_INDEX] = (int)(sc.nextDouble()*SSAP.SCALE_FACTOR); // part coefficient
            }
            
            sc.close();
            return data;
        }
        
        
    /**
     * for custom files to be loaded from the file system
     * @param filename  absolute file name
     * @return 
     */
    public static String getDataFromFile(String filename){
        StringBuilder builder=new StringBuilder();
        BufferedReader reader;
        try {
            reader=new BufferedReader(new FileReader(filename));
            String line=null;
            while((line=reader.readLine())!=null){
                builder.append(line);
            }
        } catch (Exception ex) {
            System.out.println("error reading datafile");
        }
        return builder.toString();
    }
    
    /**
     * to load a test sample from the package (airland1.txt ... 13.txt) packed with the project.
     * @param resourcename
     * @return 
     */
    public static String getDataFromResource(String resourcename){
        StringBuilder builder=new StringBuilder();
        BufferedReader reader;
        try {
            reader=new BufferedReader(new InputStreamReader(ParseData.class.getResourceAsStream(resourcename)));
            String line=null;
            while((line=reader.readLine())!=null){
                builder.append(line);
            }
        } catch (Exception ex) {
            System.out.println("error reading datafile");
        }
        return builder.toString();
    }
}
