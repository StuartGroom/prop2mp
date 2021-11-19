package main;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.FileInputStream;
import java.util.ArrayList;

public class Main {

    // Read the file with the SAME path of the main class.
    // Please put the all the test cases in the same path of the main class in your project too.

    // Test the expression of which the root of the AST is a binary expression.
    // private static final String FILE_PATH = "src/main/Example1.java";

    // Test the expression of which the root of the AST is a unary expression.
    private static final String FILE_PATH = "src/main/Example1.java";

    public static void main(String[] args) throws Exception {
        // Set up a minimal type solver that only looks at the classes used to run this sample.
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        // Configure JavaParser to use type resolution
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("~~~~~~~~~~~~OUR TESTS~~~~~~~~~~~~~");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        for (int i = 1; i < 4; i++){
            CompilationUnit cu = StaticJavaParser.parse(new FileInputStream("src/main/TestCase_"+i+".java"));

            // Find the first recognized expression, which is the boolean propositional logic formula.
            Expression root = cu.findAll(Expression.class).get(0);

            Network network = parseProp(root, new Network());
            network.optimiseNetwork(network.getRoot());
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("NETWORK MAP");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            //network.testNetwork(network.getRoot());
            System.out.println("^");
            System.out.println("|");
            network.printNetwork(network.getRoot(), "");
        }


        System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("~~~~~~~~~~OFFICIAL TESTS~~~~~~~~~~");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        //testing on official examples
        for (int i = 1; i < 6; i++){
            CompilationUnit cu = StaticJavaParser.parse(new FileInputStream("src/main/TestCase_official_"+i+".java"));

            // Find the first recognized expression, which is the boolean propositional logic formula.
            Expression root = cu.findAll(Expression.class).get(0);

            Network network = parseProp(root, new Network());
            network.optimiseNetwork(network.getRoot());
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("NETWORK MAP");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            //network.testNetwork(network.getRoot());
            System.out.println("^");
            System.out.println("|");
            network.printNetwork(network.getRoot(), "");
        }

    }


    public static Network parseProp(Expression expr, Network network) throws Exception {

        // System.out.println("--------------------------------");

        // Entering brackets
        if (expr instanceof EnclosedExpr) {
            expr = ((EnclosedExpr) expr).getInner();
        }

        if (expr instanceof BinaryExpr) {

        /*System.out.println(((BinaryExpr) expr).getLeft().toString());
        System.out.println(((BinaryExpr) expr).getRight().toString());
        System.out.println(((BinaryExpr) expr).getOperator().toString());*/


            ArrayList<Object> neuronInputs = new ArrayList<>();
            neuronInputs.add(((BinaryExpr) expr).getLeft().toString());
            neuronInputs.add(((BinaryExpr) expr).getRight().toString());
            String operator = (((BinaryExpr) expr).getOperator().toString());

            network.addNeuron(neuronInputs, operator);

            Expression left =  ((BinaryExpr) expr).getLeft();
            Expression right =  ((BinaryExpr) expr).getRight();

            if (((BinaryExpr) expr).getLeft() instanceof EnclosedExpr) {
                left = ((EnclosedExpr) ((BinaryExpr) expr).getLeft()).getInner();
            }
            if (((BinaryExpr) expr).getRight() instanceof EnclosedExpr) {
                right = ((EnclosedExpr) ((BinaryExpr) expr).getRight()).getInner();
            }
            if (left instanceof BinaryExpr ||left instanceof UnaryExpr) {
                parseProp(((BinaryExpr) expr).getLeft(), network);
            }
            if (right instanceof BinaryExpr ||right instanceof UnaryExpr) {
                parseProp(((BinaryExpr) expr).getRight(), network);
            }
        }

        if (expr instanceof UnaryExpr) { //

/*            System.out.println(((UnaryExpr) expr).getOperator());
            System.out.println(((UnaryExpr) expr).getExpression());*/

            ArrayList<Object> neuronInputs = new ArrayList<>();
            neuronInputs.add(((UnaryExpr) expr).getExpression().toString());
            String operator = (((UnaryExpr) expr).getOperator().toString());

            network.addNeuron(neuronInputs, operator);

            //Expression node2 = expr;
            Expression child2 = ((UnaryExpr) expr).getExpression();
            // for each node, also need to check whether it is enclosed expression with brackets
            if (child2 instanceof EnclosedExpr) {
                child2 = ((EnclosedExpr) child2).getInner();
            }

            if (child2 instanceof BinaryExpr) {
                parseProp(child2, network);
            }


        }
        return network;
    }
}

