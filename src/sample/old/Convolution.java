package sample.old;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Random;


//	[Serializable]
public class Convolution implements Serializable
{   private static int range = 1;   // 1, jestli interval [-1, 1]
    public double[][][] weights3D, avInput3D;
    public double[][] weights2D, avInput;
    public int[][][] inputSize3D;
    public int[][] inputSize;
    public int size1, size2, cisloFiltra;
    public double grad, averageOutput;
    public int size3 = 0;
    public int outputSize = 0;
    private static LinkedList<Convolution> Filters = new LinkedList<>();
    public Convolution next;



    Convolution(int size1, int size2, int cisloFiltra) {
        this.cisloFiltra = cisloFiltra;
        if(cisloFiltra == 0) {
            weights2D = doFirstRandomWeights(new double[size1][size2]);
        }
        else {
            weights2D = doRandomWeights(new double[size1][size2]);
        }
        avInput = new double[size1][size2];
        inputSize = new int[size1][size2];
        this.size1 = size1;
        this.size2 = size2;
        Filters.add(this);
    }

    Convolution(int size1, int size2, int size3, int cisloFiltra) {
        this.cisloFiltra = cisloFiltra;
        if(cisloFiltra == 0) {
            weights3D = doFirstRandomWeights(new double[size1][size2][size3]);
        }
        else {
            weights3D = doRandomWeights(new double[size1][size2][size3]);
        }
        avInput3D = new double[size1][size2][size3];
        inputSize3D = new int[size1][size2][size3];
        this.size1 = size1;
        this.size2 = size2;
        this.size3 = size3;
        Filters.add(this);
    }

    public void addInput(double input, int y, int x) {
        inputSize[y][x]++;
        avInput[y][x] += input;
    }

    public void addInput(double input, int y, int x, int k) {
        inputSize3D[y][x][k]++;
        avInput3D[y][x][k] += input;
    }

    public void addOutput(double output) {
        outputSize++;
        averageOutput += output;
    }

    public void clearInputMass() {
        avInput = new double[size1][size2];
        inputSize = new int[size1][size2];
    }

    public void clearInputMass3D() {
        avInput3D = new double[size1][size2][size3];
        inputSize3D = new int[size1][size2][size3];
    }

    public void clearOutput() {
        averageOutput = 0;
        outputSize = 0;
    }

    double[][] doFirstRandomWeights(double[][] newFilter) {
        Random rand = new Random();
        for(int i=0; i<newFilter.length; i++) {
            for(int j=0; j<newFilter[0].length; j++) {
                do{
                    newFilter[i][j] = -range + Math.random()*range*2;
                } while (newFilter[i][j] == 0 );
            }
        }
        return newFilter;
    }


    double[][][] doFirstRandomWeights(double[][][] newFilter) {
        Random rand = new Random();
        for(int i=0; i<newFilter.length; i++) {
            for(int j=0; j<newFilter[0].length; j++) {
                for(int k=0; k<newFilter[0][0].length; k++) {
                    do{
                        newFilter[i][j][k] = -range + Math.random()*range*2;
                    } while (newFilter[i][j][k] == 0 );
                }
            }
        }
        return newFilter;
    }

    double[][] doRandomWeights(double[][] newFilter) {
        Random rand = new Random();
        Convolution templ = Filters.getFirst();
        while(templ.cisloFiltra != cisloFiltra-1) {
            templ = templ.next;
        }
        for(int i=0; i<newFilter.length; i++) {
            for(int j=0; j<newFilter[0].length; j++) {
                if((cisloFiltra != 5)&&(cisloFiltra != 10)){
                    do{
                        newFilter[i][j] = -range + Math.random()*range*2;
                    } while (newFilter[i][j] == templ.weights2D[i][j]);
                }
                else {
                    do{
                        newFilter[i][j] = -range + Math.random()*range*2;
                    } while (newFilter[i][j] == 0);
                }
            }
        }
        return newFilter;
    }

    double[][][] doRandomWeights(double[][][] newFilter) {
        Random rand = new Random();
        Convolution templ = Filters.getFirst();
        while(templ.cisloFiltra != cisloFiltra-1) {
            templ = templ.next;
        }
        for(int i=0; i<newFilter.length; i++) {
            for(int j=0; j<newFilter[0].length; j++) {
                for(int k=0; k<newFilter[0][0].length; k++) {
                    if((cisloFiltra != 5)||(cisloFiltra != 10)){
                        do{
                            newFilter[i][j][k] =  -range + Math.random()*range*2;
                        } while (newFilter[i][j][k] == templ.weights3D[i][j][k]);
                    }
                    else {
                        do{
                            newFilter[i][j][k] =  -range + Math.random()*range*2;
                        } while (newFilter[i][j][k] == 0);
                    }
                }
            }
        }
        return newFilter;
    }

    public void countAverageOutput() {
        averageOutput = averageOutput / outputSize ;
    }

    public void countAverageInput() {
        if(weights3D == null) {
            for (int i=0; i<size1; i++) {
                for (int j=0; j<size2; j++) {
                    avInput[i][j] = avInput[i][j]/inputSize[i][j];
                }
            }
        }
        else
            countAverageInput3D();
    }

    void countAverageInput3D() {
        for (int i=0; i<size1; i++) {
            for (int j=0; j<size2; j++) {
                for(int k=0; k<size3; k++) {
                    avInput3D[i][j][k] = avInput3D[i][j][k]/inputSize3D[i][j][k];
                }
            }
        }
    }
}

