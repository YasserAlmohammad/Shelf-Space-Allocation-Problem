/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ssap;
import gnu.trove.map.hash.TObjectIntHashMap;
import static java.lang.Runtime.getRuntime;
import org.kohsuke.args4j.Option;
import org.chocosolver.samples.AbstractProblem;
import org.chocosolver.solver.ResolutionPolicy;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.constraints.LogicalConstraintFactory;
import org.chocosolver.solver.constraints.ternary.Max;
import org.chocosolver.solver.search.limits.FailCounter;
import org.chocosolver.solver.search.loop.lns.LNSFactory;
import org.chocosolver.solver.search.loop.monitors.SMF;
import org.chocosolver.solver.search.strategy.IntStrategyFactory;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;
import org.chocosolver.util.ESat;
import java.util.*;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import static org.chocosolver.samples.AbstractProblem.Level.QUIET;
import static org.chocosolver.samples.AbstractProblem.Level.SILENT;
import org.chocosolver.solver.explanations.ExplanationFactory;
import org.chocosolver.solver.search.loop.monitors.IMonitorSolution;
import org.chocosolver.solver.trace.Chatterbox;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
/**
 *
 * @author Author
 */
public class SSAP {
public enum Level {
        SILENT(-10), QUIET(0), VERBOSE(10), SOLUTION(20), SEARCH(30);
        int level;
        Level(int level) {
            this.level = level;
        }
        public int getLevel() {
            return level;
        }
    }
    protected Level level = Level.SOLUTION;
    protected long seed = 29091981;//29091981
    ExplanationFactory explanationEngine = ExplanationFactory.CBJ;//NONE suitable for small sets
    //CBJ useful for large variable number, planes=50+
    protected boolean noGoodResults = false;
    public static Solver solver;
    private boolean userInterruption = true;
 //   String mData;
    public String method="RLNS"; //"plns"
    public String lexico="Lexico_UB";
    public Data data=null;
    public static final int PRODUCT_ID_INDEX=0; //unique, index location in the data arrays.
    public static final int FACING_LENGTH_INDEX=1;
    public static final int MIN_FACINGS_INDEX=2;
    public static final int MAX_FACINGS_INDEX=3;
    public static final int PROFIT_INDEX=4;
    
    public static final int SHELF_PART_ID_INDEX=0; //unique
    public static final int SHELF_ID_INDEX=1;
    public static final int SHELF_PRIORITY_COEFFECIENT_INDEX=2;
    public static final int PART_PRIORITY_COEFFECIENT_INDEX=3;
    
    public static IntVar[] product_facings,product_shelf,product_shelf_part,
            /*product_start_pos,*/product_occupancy,product_profit,shelfs_coeff, product_profit_with_coeff,shelf_occupancy;
    public  static BoolVar shelves[][]; //will also be used in drawing the planogram
    
    BoolVar[] bVars; //binary booleans for MIP variablesproduct_occupancy
    
    public static IntVar objective; //maximize profit, scalled by scale factor, to avoid double calculations
    //objective should be rescald by the scal factor and converted to double to get the final number in its correct precision
    
    int parts_per_shelf=1; //for now lets assume that there are no parts per shelf
    public int shelf_length; 
    public static boolean exectuting=false;   
    boolean canBeSolved=true;
    public static final int SCALE_FACTOR=100; //to make sure that our problem is MIP, we convert all double values to integers
    //by multiplying them with 1000, then at the final results we again divide by 100 to get double back
    //assuming that coefficients at maxed at 2 number precisions.
    //and profits are maxed at 2 double precisions
    //to adapt the solver and work with what functions we have here.
    //this only applies to the following: objective, product_profit,shelfs_coeff, product_profit_with_coeff,shelf_occupancy variables
    //which should normally be double
    public int min_space;
    public SSAP(Data data){
       this.data=data;
       shelf_length=data.shelf_width;
        min_space=0;
        
        for(int i=0;i<data.n_products;i++){
            min_space+=data.products[i][MIN_FACINGS_INDEX]*data.products[i][FACING_LENGTH_INDEX];
        }
        if(min_space>data.n_shelves*shelf_length*parts_per_shelf){ //cant be solved
            System.out.println("Problem Cant be solved, you need more shelves");
            canBeSolved=false;
        }
    }
    public void createSolver() {
        solver = new Solver("Shelf Space Allocation Problem");
    }
    
    //will be manually updated

    public void buildModel() {
        //first check if the problem can be solved (minimum of all facings can fit in shelves according to placement rules)
        //there should be enough shelves to hold all the products
        if(!canBeSolved)
            return;
        
        int []coeffs=new int[data.n_shelves/parts_per_shelf];
        IntVar ZERO = VariableFactory.fixed(0, solver);
        IntVar ONE = VariableFactory.fixed(1, solver);
        List<BoolVar> booleans = new ArrayList<>();
        
        for(int c=0;c<data.n_shelves/parts_per_shelf;c++){
            coeffs[c]=data.shelves[c][SHELF_PRIORITY_COEFFECIENT_INDEX];
        }
        if(data.n_products>44){
            explanationEngine = ExplanationFactory.CBJ; //for complicated problems
        }
        product_facings = new IntVar[data.n_products]; //how many facings of each product to display
        product_shelf=new IntVar[data.n_products]; //which shelf a product will be placed
   //     product_shelf_part = new IntVar[data.n_products]; //1 2 3 4(is 1 and 2) 5(is 2 an 3) (6 is 1 and 3) (7 is 1-2-3)
    //    product_start_pos = new IntVar[data.n_products]; //within shelf limits
        product_occupancy=new IntVar[data.n_products];
        product_profit=new IntVar[data.n_products];
        product_profit_with_coeff=new IntVar[data.n_products];
        
        shelfs_coeff=new IntVar[data.n_products];        
        shelves=new BoolVar[data.n_products][data.n_shelves]; //(shelf-prod array)
        shelf_occupancy=new IntVar[data.n_shelves]; //space occupied by all products on shelf j
        
        // objective; //maximize sum of (product_facings * shelf_profit*part_proft*prod_profit (within constraints)
        double objective_upperBound = 0; //objective function upper bound, summation of best case profit upper limit of products to view
        double objective_lowerBound = 0;

        
        ////////////// these two loops bellow might not be important for bounds
        int min_coeff=data.shelves[0][SHELF_PRIORITY_COEFFECIENT_INDEX];
        int max_coeff=0;
        for(int j=0;j<data.n_shelves;j++){
            if(min_coeff>data.shelves[j][SHELF_PRIORITY_COEFFECIENT_INDEX])
                min_coeff=data.shelves[j][SHELF_PRIORITY_COEFFECIENT_INDEX];
            if(max_coeff<data.shelves[j][SHELF_PRIORITY_COEFFECIENT_INDEX])
                max_coeff=data.shelves[j][SHELF_PRIORITY_COEFFECIENT_INDEX];
        }    
        
        //we assume that best possible upper bound, is when maximum number of facings are located all on best shelf
        //as for lower bound: minmum number of facings are all located on worse shelf.
        for(int p=0;p<data.n_products;p++){
            objective_upperBound+=data.products[p][MAX_FACINGS_INDEX]*data.products[p][PROFIT_INDEX]*max_coeff;
            objective_lowerBound+=data.products[p][MIN_FACINGS_INDEX]*data.products[p][PROFIT_INDEX]*min_coeff;
        }
        ////////////
        for (int i = 0; i < data.n_products; i++) {
            product_facings[i] = VariableFactory.bounded("product_facings_" + i, (int)data.products[i][MIN_FACINGS_INDEX], (int)data.products[i][MAX_FACINGS_INDEX], solver);
            product_shelf[i] = VariableFactory.bounded("product_"+i+"_shelf", 0, data.n_shelves, solver);
        //    product_shelf_part[i] = VariableFactory.bounded("product_"+i+"_shelf_part", 1, 3 /*7*/, solver);
        //    product_start_pos[i] = VariableFactory.bounded("product_"+i+"_start_pos", 0, shelf_length*parts_per_shelf, solver);
            product_occupancy[i]=VariableFactory.scale(product_facings[i], data.products[i][FACING_LENGTH_INDEX]);
            product_profit[i]=VariableFactory.scale(product_facings[i], data.products[i][PROFIT_INDEX]); //without coefficients
            //find smallest and largest coefficient
            
            shelfs_coeff[i]=VariableFactory.bounded("shelf_"+i+"coeff", min_coeff, max_coeff, solver);
            product_profit_with_coeff[i]=VariableFactory.bounded("product_"+i+"_profit_with_coeff", 1, (int)objective_upperBound, solver);
            //upper objective bound: find best shelf and best part with highest coeffecient
            
            //constraints (this constraint might be included in another constraint, so it can be removed)
            Constraint constraint_prod_facings_sums=IntConstraintFactory.arithm(product_occupancy[i], "<=", shelf_length);
            solver.post(constraint_prod_facings_sums);
            
            //shelfs_coeff[i]=coeffs[product_shelf[i]]
            Constraint shelf_prod_coeff_cons=IntConstraintFactory.element(shelfs_coeff[i],coeffs, product_shelf[i]);
            solver.post(shelf_prod_coeff_cons);
            
            //product_profit[i]* shelfs_coeff[i]= product_profit_with_coeff[i]
            Constraint prod_with_coeff_profit_cons=IntConstraintFactory.times(product_profit[i], shelfs_coeff[i], product_profit_with_coeff[i]);
            solver.post(prod_with_coeff_profit_cons);
            
            //sum of all products facings on the same shelf is less than the shelf length.
            for(int j=0;j<data.n_shelves;j++){
                //shelves[i][j]
                shelves[i][j]=VariableFactory.bool("b_Yij_" + i + "_" + j, solver);
                booleans.add(shelves[i][j]); //for random generation of values.
                Constraint c1=IntConstraintFactory.arithm(product_shelf[i], "=", j);
                Constraint c2=IntConstraintFactory.arithm(product_shelf[i], "!=", j);
                LogicalConstraintFactory.ifThenElse(shelves[i][j], c1, c2); //if shelf has product then true, otherwise false.
            }
            //product can only be located on one shelf!
            Constraint prod_on_one_shelf_cons=IntConstraintFactory.sum(shelves[i], ONE);
            solver.post(prod_on_one_shelf_cons);
        }   
        
        for(int j=0;j<data.n_shelves;j++){
            shelf_occupancy[j] = VariableFactory.bounded("shelf_"+j+"_occupancy", 0, shelf_length*parts_per_shelf, solver);
           // Constraint sum_of_facings_cons=IntConstraintFactory.sum
           // Constraint shelf_occupancy_cons=IntConstraintFactory.arithm(shelf_occupancy[j], "<=",shelf_length*parts_per_shelf); defined through bound!
            //so we just define the occupancy variable
            IntVar prod_shelf_occupany[];
            prod_shelf_occupany=new IntVar[data.n_products];
            for(int i=0;i<data.n_products;i++){
               prod_shelf_occupany[i]=VariableFactory.bounded("prod_"+i+"shelf_"+j+"_occupancy", 0, shelf_length*parts_per_shelf, solver);  
               Constraint prod_shelf_occupany_cons=IntConstraintFactory.times(product_occupancy[i], shelves[i][j],prod_shelf_occupany[i]);
               solver.post(prod_shelf_occupany_cons);//**************
            }
            Constraint shelf_cons=IntConstraintFactory.sum(prod_shelf_occupany, "=",shelf_occupancy[j]);
            solver.post(shelf_cons);
        }
        
         
        bVars = booleans.toArray(new BoolVar[booleans.size()]);

        objective = VariableFactory.bounded("objective", (int)objective_lowerBound, (int)objective_upperBound, solver);
        //objective is equal to (summation of products*profit*shelfcoefficient*partcoefficient)
        solver.post(IntConstraintFactory.sum(product_profit_with_coeff, objective));
     
        solver.setObjectives(objective);
    }

    public void configureSearch() {
       // Arrays.sort(product_facings, (o1, o2) -> maxCost.get(o1) - maxCost.get(o2)); //gives better result than the one bellow
        if(lexico.equals("Lexico_UB")){
            solver.set(
                   IntStrategyFactory.random_bound(bVars, seed),
                   IntStrategyFactory.lexico_UB(product_facings) //lexico_UB is a faster way to go in our case than lexico_LB
            );
        }
        else{ //(lexico.equals("Lexico_LB")){
            solver.set(
                   IntStrategyFactory.random_bound(bVars, seed),
                   IntStrategyFactory.lexico_LB(product_facings) //lexico_UB is a faster way to go in our case than lexico_LB
            );
        }
        StopCriteria.stopSearch=false; //add more stop criterias
        solver.addStopCriterion(new StopCriteria()); //add criteria to stop search by user.
        //solver.makeCompleteSearch(true);
    }

    public void solve() throws Exception{
        IntVar[] ivars = solver.retrieveIntVars();
     //   
        if(method.equals("PLNS")){
            LNSFactory.pglns(solver, ivars, 30, 10, 200, 0, new FailCounter(solver, 100)); //very fast
        }
        else if(method.equals("RLNS")){
            LNSFactory.rlns(solver, ivars, 100/* slower, but better accuracy, 30 acceptable */, seed, new FailCounter(solver, 100)); //level =30 solved all problems
            
        }
        else{
            throw new Exception("Metho must be specified");
        }
      
     //   
        SMF.limitTime(solver, (3*data.n_products)+"s");
        //depending on sample size, we limit the fails.   
        solver.findOptimalSolution(ResolutionPolicy.MAXIMIZE, objective);
    }

    /*
    called when solution is found and search completes.
    */
    public void printFinalResult() {
        System.out.println("Shelf Allocations");
        StringBuilder st = new StringBuilder();
        if (solver.isFeasible() != ESat.TRUE) {
            st.append("\tINFEASIBLE");
        } else {
            for (int i = 0; i < data.n_products; i++) {
                st.append("product ").append(i).append(" [on Shelf:").
                        append(product_shelf[i].getValue()).append("").
                        append("]").append(" Num Of Facings:"+product_facings[i].getValue()+"\n");
            }             
        }
        System.out.println(st.toString());
        exectuting=false;
        JOptionPane.showMessageDialog(null, "Search Completed");
    }

 public final boolean readArgs(String... args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java " + this.getClass() + " [options...]");
            parser.printUsage(System.err);
            System.err.println();
            return false;
        }
        return true;
    }

    protected void overrideExplanation() {
        if (solver.getExplainer() == null) {
            explanationEngine.plugin(solver, noGoodResults, false);
        }
    }
    
    
    private boolean userInterruption() {
        return userInterruption;
    }

    public final void solveProblem(IMonitorSolution mon,String... args) throws Exception {
        this.createSolver();
        this.buildModel();
        this.configureSearch();

        overrideExplanation();

        if (level.getLevel() > SILENT.getLevel()) {
            Chatterbox.showStatistics(solver);
            if (level.getLevel() > Level.VERBOSE.getLevel()) Chatterbox.showSolutions(solver);
            if (level.getLevel() > Level.SOLUTION.getLevel()) Chatterbox.showDecisions(solver);
        }

        Thread statOnKill = new Thread() {
            public void run() {
                if (userInterruption()) {
                    if (level.getLevel() > SILENT.getLevel()) {
                        System.out.println(solver.getMeasures().toString());
                    }
                }

            }
        };

        getRuntime().addShutdownHook(statOnKill);
        solver.plugMonitor(mon);
        this.solve();  //holds tell finish
        if (level.getLevel() > QUIET.getLevel()) {
            printFinalResult();
        }
        userInterruption = false;
        getRuntime().removeShutdownHook(statOnKill);
    }
}
