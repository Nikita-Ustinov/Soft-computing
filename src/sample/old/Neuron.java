package sample.old;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Random;
import static sample.old.NNAutoencoders.net;




public class Neuron implements Serializable
{
    public double [] weights, input;
    public double output, grad, sum ;
    public Neuron next;
    private static int range = 1;   // 1, jestli interval [-1, 1]
    int vrstva;

    public Neuron(int vrstva)
    {	
        if (vrstva==0){
                weights = new double[Neuronet.inputLength];
                input = new double[Neuronet.inputLength];
        } else 
        if (vrstva==1){
                weights = new double[Neuronet.prvniVrstva];
                input=new double[Neuronet.prvniVrstva];
        } else 
        if (vrstva==2){
                weights = new double[Neuronet.druhaVrstva];
                input=new double[Neuronet.druhaVrstva];
        } else {
            weights = new double[vrstva];
            input=new double[vrstva];
        } 
        this.vrstva = vrstva;
        doRandomWeights();
    }		


    public void doRandomWeights()							
    {  
            Random rand = new Random();
            for (int i=0;i<weights.length; i++){
                    do{
//                    weights[i] = -0.6+rand.nextDouble(); //-6 -> 5
                       weights[i] = -range + Math.random()*range*2;
                    } while (weights[i]==0);
            }
    }
    
    
    boolean dead() {
        boolean result = true;
        for (int i=0; i<weights.length; i++) {
            if(weights[i]!=0) 
                result = false;
        }
        return result;
    }
    
    
    public double countOut() throws Exception
    { 
        if(dead()) 
        throw new Exception("Neuron is dead, vrstva "+vrstva);
        
        sum=0;
        output=0;
        for (int i=0; i<input.length; i++)
        {   
            if(Double.isNaN(sum)) {
                int ads = 2;
            }
            sum+=weights[i]*input[i];
        }
        output=1.7159*Math.tanh(0.66*sum); 
        return output;
    }

    public void addOneWeightAndInput(int neuronNumber) {
        double[] templ = weights;
        weights = new double[neuronNumber];
        for (int i = 0; i<templ.length; i++) {
                weights[i] = templ[i];
        }
        Random rand = new Random();
        weights[weights.length-1] = -range + Math.random()*range*2;
        input = new double[neuronNumber];
    }
    
    void serialization(int counter) throws FileNotFoundException, IOException {
        String weight = String.valueOf(weights[0]);
        weight +="\r\n";
        for(int i=1; i<weights.length; i++) {
            weight += String.valueOf(weights[i]);
            weight +="\r\n";
        }
        write("serialization files/"+vrstva+"/"+counter, weight);
    }
    
    void serializationStandart(int counter) throws FileNotFoundException, IOException {
        String fileName = "serialization files/"+vrstva+"/"+counter;
        FileOutputStream fos = new FileOutputStream(fileName+".out");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(net);
        oos.close();
    }
    
    public Neuron deserialization(int counter) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader("serialization files/"+vrstva+"/"+counter+".txt"));
        String line;
        try {
            line = br.readLine();
//            weights[0] = Double.parseDouble(line);
            int i=0;
            while (line != null) {
                weights[i] = Double.parseDouble(line);
                i++;
                line = br.readLine();
            }
        } finally {
            br.close();
        }
        return new Neuron(vrstva);
    }
    
    static void write(String fileName, String text) {
        File file = new File(fileName+".txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());

            try {
                out.print(text);
            } finally {
                out.close();
            }
        } catch (IOException e) {}
    }

}


