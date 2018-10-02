package sample.old;


import java.util.Random;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
import java.awt.Color;
import java.awt.Graphics2D;

public class NNAutoencoders implements Serializable {

    static Boolean firstEnter = true;
    static String progressInfo;
    static String _info;
    static String _convolutionsInfo;
    static int Answer;
    static boolean[] UzByli = new boolean[]{false, false, false, false, false, false, false, false, false, false};
    static Neuronet net;
    static double Max;
    static int counterNeuronNew = 0;

    public static int counterAddNeuron = 0;
    public int iterationWithoutNewNeuron = 0;
    boolean newNeuron = false;

    public static void main(String[] args) throws Exception {
//        net = deseralizace("wiaghts");
//        Neuronet.inputLength = net.inputLengthOwn;
//        Neuronet.prvniVrstva = net.prvniVrstvaOwn;
//        Neuronet.druhaVrstva = net.druhaVrstvaOwn;
//        Neuronet.tretiVrstva = net.tretiVrstvaOwn;
//        writeProgressInfo(0,test());					//vysledek ve fajlu "short progress info"
//        getAllPicture();
        net = new Neuronet();
        try {
            String path = "foto/"+Integer.toString(0)+".jpg";
            BufferedImage inputImage = ImageIO.read(new File(path));
        } catch (Exception e) {
            prepareImages();
        }
        study();
    }

    public static void newEpoch() {
        for (int i = 0; i < 10; i++) {
            UzByli[i] = false;
        }
    }

    public NNAutoencoders(int i) {
    }

    public static Picture getPicture(boolean isColor) {
        Random rand = new Random();
        int a;
        do {
            a = rand.nextInt(10);
        } while (UzByli[a] == true);
        String fileName = "foto/" + a + ".jpeg";
        UzByli[a] = true;
        Answer = a;
        return ImgToRightPicture(fileName, isColor);
    }

    static Picture ImgToRightPicture(String file, boolean isColor)  {
//            Image img = Image.FromFile(file);
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(file));
        } catch (IOException e) {
            System.out.println("Nelze precist file");
        }
        Picture vysledek;
        if(isColor) {
            vysledek = new Picture(39,30,3);
            file += ".jpg";
//            Image img = Image.FromFile(file);
//            Bitmap bm = new Bitmap(img);
//            Picture vysledek = new Picture(39,30,3);
            int color;
            for (int i=0;i<39; i++) {
                for (int j=0; j<30; j++) {
                    color = img.getRGB(j, i);
                    for(int k=0; k<3; k++) {
                        if (k==0) {
                            vysledek.map3D[i][j][k] = (color >>> 16)& 0xFF; //red
                            vysledek.map3D[i][j][k] /= 256;
                        }
                        if (k==1) {
                            vysledek.map3D[i][j][k] = (color >>> 8)& 0xFF;    //green
                            vysledek.map3D[i][j][k] /= 256;
                        }
                        if (k==2) {
                            vysledek.map3D[i][j][k] = (color >>> 0)& 0xFF;   //blue
                            vysledek.map3D[i][j][k] /= 256;
                        }
                    }
                }
            }
//            for (int i=0;i<39; i++) {
//               for (int j=0; j<30; j++) {
//                   color = img.getRGB(j, i);
//                   for(int k=0; k<3; k++) {
//                       if (k==0)
//                           vysledek.map3D[i][j][k] /= 256;
//                       if (k==1)
//                           vysledek.map3D[i][j][k] /= 256;
//                       if (k==2)
//                           vysledek.map3D[i][j][k] /= 256;
//                   }
//               }
//           }
        }
        else {
            int color;
            vysledek = new Picture(39,30);
            for (int i = 0; i < 39; i++) {
                for (int j = 0; j < 30; j++) {
                    color = img.getRGB(j, i); //bm.GetPixel(j,i);
                    int red = (color >>> 16)& 0xFF;
                    int green = (color >>> 8)& 0xFF;
                    int blue = (color >>> 0)& 0xFF;
                    double delta = (0.2126f * red + 0.7152f * green + 0.0722f * blue)/255;
                    vysledek.map2D[i][j] = delta;
                }
            }
        }
        return vysledek;
    }

    static int calculateResult(Picture picture) throws Exception {
        Convolution templ = net.convolutions.head;
        for (int i = 0; i < net.convolutions.size; i++) {
            templ.clearInputMass();                                                     //mazani zbytecnych dat
            templ.clearOutput();
            templ = templ.next;
        }
        Picture[] firstConvolution = new Picture[net.convolutions.size / 3];		//массив массивов?? [][,]		//prvni vrstva, convolution 11x11
        for (int i = 0; i < net.convolutions.size / 3; i++) {
            firstConvolution[i] = new Picture(applyConvolution(i, picture));			//prvni konvoluce
            firstConvolution[i].map2D = function(firstConvolution[i].map2D, "Tanh");	// prvni funkce aktivace (Tanh)
            firstConvolution[i].map2D = addY(firstConvolution[i].map2D);
            firstConvolution[i].map2D = pooling(2, firstConvolution[i].map2D);		//prvni pooling
        }
//        writeConvolution(firstConvolution[1], "1 vrstva");
        Picture[] secondConvolution = new Picture[(int)Math.pow(net.convolutions.size/3, 2)];
        int cisloFiltra = net.convolutions.size / 3 ;
        int cisloPolozky = 0;
        for (int j = 0; j < net.convolutions.size / 3; j++) {
            for (int i = 0; i < net.convolutions.size / 3; i++) {
                secondConvolution[cisloPolozky] = new Picture(applyConvolution(cisloFiltra, firstConvolution[j]));
                secondConvolution[cisloPolozky].map2D = function(secondConvolution[cisloPolozky].map2D, "Tanh");
                secondConvolution[cisloPolozky].map2D = addY(secondConvolution[cisloPolozky].map2D);
                secondConvolution[cisloPolozky].map2D = pooling(2, secondConvolution[cisloPolozky].map2D);
                cisloFiltra++;
                cisloPolozky++;
            }
            cisloFiltra = net.convolutions.size / 3;
//            writeConvolution(secondConvolution[1], "2 vrstva");
        }
        double[][][] thirdConvolution = new double[(int)Math.pow(net.convolutions.size / 3, 3)][][];
        cisloFiltra = net.convolutions.size / 3 * 2;
        cisloPolozky = 0;
        for (int j = 0; j < Math.pow(net.convolutions.size / 3, 2); j++) {
            for (int i = 0; i < net.convolutions.size / 3; i++) {
                thirdConvolution[cisloPolozky] = applyConvolution(cisloFiltra, secondConvolution[j]);
                thirdConvolution[cisloPolozky] = addY(thirdConvolution[cisloPolozky]);
                thirdConvolution[cisloPolozky] = function(thirdConvolution[cisloPolozky], "Tanh");
                thirdConvolution[cisloPolozky] = pooling(3, thirdConvolution[cisloPolozky]);
                cisloFiltra++;
                cisloPolozky++;
            }
            cisloFiltra = net.convolutions.size / 3 * 2;
        }
//        writeConvolution(thirdConvolution[1], "3 vrstva");
        double[] inputFullyConnectionNet = doOneArray(thirdConvolution);
        net.l0.writeInput(inputFullyConnectionNet);				//zapisuje vstupni vektor v "fully connected" neuronovou sit
        net.l0.countOutputs();
        Neuron templ1 = net.l1.head;
        for (int i = 0; i < Neuronet.druhaVrstva; i++) {			//zapisuje do druhe vrstvy FC neuronove siti vstupni signaly
            for (int j = 0; j < Neuronet.prvniVrstva; j++) {
                templ1.input[j] = net.l0.outputs[j];
            }
            templ1 = templ1.next;
        }
        net.l1.countOutputs();
        templ1 = net.l2.head;
        for (int i = 0; i < Neuronet.tretiVrstva; i++) {			// zapisuje do treti vrstvy FC neuronove siti vstupni signaly
            for (int j = 0; j < Neuronet.druhaVrstva; j++) {
                templ1.input[j] = net.l1.outputs[j];
            }
            templ1 = templ1.next;
        }
        net.l2.countOutputs();
        int index = 0;          	//cislo neuronu ktery vyhral
        Max = net.l2.outputs[0];
        for (int i = 0; i < net.l2.outputs.length; i++) {
            if (Max < net.l2.outputs[i]) {
                Max = net.l2.outputs[i];
                index = i;
            }
        }


        templ = net.convolutions.head;
        for (int i = 0; i < net.convolutions.size; i++) {
            templ.countAverageInput();                                          //spocitani prumerneho vstupu
            templ.countAverageOutput();
//            templ.clearInputMass();                                            //mazani zbytecnych dat
//            templ.clearOutput();
            templ = templ.next;
        }
        return index;
    }

    static double[][] function(double[][] picture, String nazevFunkce) {
        double[][] result = picture;
        for (int i = 0; i < picture.length; i++) {
            for (int j = 0; j < picture[1].length; j++) {
                if (nazevFunkce == "Tanh") {
                    result[i][j] = 1.7159 * Math.tanh(0.66 * picture[i][j]);
                } else if (nazevFunkce == "ReLu") {
                    if (result[i][j] > 0) {
                        result[i][j] = result[i][j];
                    } else {
                        result[i][j] = 0;
                    }
                }

            }
        }
        return result;
    }




    static void study() throws Exception {

        double[] err = new double[Neuronet.tretiVrstva];
        int iteration = 1;
        double lokError = 0;
        int lokResult = 0;
        double errorMin = 100;
        int testValue = 0;
        int bestTestValue = 0;
        int gradNull = 0;
        writeAllConvolution(0);
        double lastResult = -1;
        int changeIteration = 0;
        boolean shakeFlag = false;
        double lastError = -1;
        int epochaWithoutNewNeuron = 0;

        while (testValue < 100) {
            lokResult = calculateResult(getPicture(true));
            Neuron templ3 = net.l2.head;
            if((Double.isNaN(templ3.weights[1])||(Double.isNaN(net.l0.head.weights[1]))||(Double.isNaN(net.l1.head.weights[1])))) { //kontrola siti
                System.out.println("NAN NAN NAN iteration "+ iteration);
            }
            for (int i = 0; i < Neuronet.tretiVrstva; i++) {
                if (Answer == i) {
                    err[i] = Max - templ3.output;				//zapisuje signal chyby vystupni vrstvy
                } else {
                    err[i] = 0 - templ3.output;
                }
                templ3 = templ3.next;
            }
            Neuron templ2;
            templ3 = net.l2.head;
            for (int i = 0; i < Neuronet.tretiVrstva; i++) {
                templ2 = net.l1.head;
                templ3.grad = 0.388 * (1.716 - templ3.output) * (1.716 + templ3.output) * err[i];//1.7159   //pocita gradient pro vystupni vyrstvu
                for (int j = 0; j < Neuronet.druhaVrstva; j++) {
                    templ3.weights[j] += net.speedLFCN * templ2.output * templ3.grad;     //pocita vahy pro vystupni vrstvu
                    templ2 = templ2.next;
                }
                templ3 = templ3.next;
            }

            double grad = 0;
            Neuron templ1;
            templ2 = net.l1.head;
            for (int i = 0; i < Neuronet.druhaVrstva; i++) {
                grad = 0;
                templ3 = net.l2.head;
                for (int u = 0; u < Neuronet.tretiVrstva; u++) {		//sumarizuje gradient predhozi vrstvy (delta pravidlo pro druhou vrstvu)
                    grad += templ3.grad * templ3.weights[i];
                    templ3 = templ3.next;
                }
                templ2.grad = grad * 0.388 * (1.716 - templ2.output) * (1.716 + templ2.output);//1.7159
                templ1 = net.l0.head;
                for (int j = 0; j < Neuronet.prvniVrstva; j++) {
                    templ2.weights[j] += net.speedLFCN * templ1.output * templ2.grad;
                    templ1 = templ1.next;
                }
                templ2 = templ2.next;
            }

            templ1 = net.l0.head;
            for (int i = 0; i < Neuronet.prvniVrstva; i++) {
                grad = 0;
                templ2 = net.l1.head;
                for (int u = 0; u < Neuronet.druhaVrstva; u++) {		//sumarizuje gradient predhozi vrstvy (delta pravidlo pro prvni vrstvu)
                    grad += templ2.grad * templ2.weights[i];
                    templ2 = templ2.next;
                }
                templ1.grad = grad * 0.388 * (1.716 - templ1.output) * (1.716 + templ1.output);//1.7159
                for (int j = 0; j < Neuronet.inputLength; j++) {
                    templ1.weights[j] += net.speedLFCN * templ1.input[j] * grad;
                }
                templ1 = templ1.next;
            }

            //pro filtry 10 az 14
            Convolution templ = net.convolutions.head;
            while (templ.cisloFiltra != net.convolutions.size / 3 * 2 ) {
                templ = templ.next;
            }
            for (int i = 0; i < net.convolutions.size / 3; i++) {
                grad = 0;
                templ1 = net.l0.head;                                           //vrstva se ktere scita gradienty
                for (int j = 0; j < net.l0.length; j++) {
                    grad += templ1.grad;					//sumarizuje gradient predhozi vrstvy
                    templ1 = templ1.next;
                }
                templ.grad = grad * 0.388 * (1.7159 - templ.averageOutput) * (1.7159 + templ.averageOutput) / net.l0.length;
                if (grad == 0) {
                    gradNull++;
                }
                double delta;
                for (int k = 0; k < templ.weights2D.length; k++) {
                    for (int q = 0; q < templ.weights2D[0].length; q++) {
                        delta = net.speedL1CL * templ.grad * templ.avInput[k][q];
                        templ.weights2D[k][q] += delta;
                    }

                }
//                writeConvolution(templ.weights, "");
//                writeAllConvolution(iteration);
                templ = templ.next;
            }

            //pro filtry 5 az 9
            templ = net.convolutions.head;
            while (templ.cisloFiltra != net.convolutions.size / 3 ) {
                templ = templ.next;
            }
//            int countTemplLast = 0;
            for (int i = 0; i < net.convolutions.size / 3; i++) {
                grad = 0;
                Convolution templLast = net.convolutions.head;          //vrstva se ktere scita gradienty
                while (templLast.cisloFiltra != net.convolutions.size / 3 * 2 ) {
                    templLast = templLast.next;
                }
                for (int j = 0; j < net.convolutions.size / 3; j++) {
                    grad += templLast.grad;				//sumarizuje gradient predhozi vrstvy
                    templLast = templLast.next;
                }
                templ.grad = grad * 0.388 * (1.7159 - templ.averageOutput) * (1.7159 + templ.averageOutput) / Neuronet.prvniVrstva;
                if (grad == 0) {
                    gradNull++;
                }
                for (int k = 0; k < templ.weights2D.length; k++) {
                    for (int q = 0; q < templ.weights2D[0].length; q++) {
                        templ.weights2D[k][q] += net.speedL2CL * templ.grad * templ.avInput[k][q];
                    }
                }
                templ = templ.next;
            }

            //pro filtry 0 az 4
            templ = net.convolutions.head;
            for (int i = 0; i < net.convolutions.size / 3; i++) {
                grad = 0;
//                countTemplLast = 0;
                Convolution templLast = net.convolutions.head;		//vrstva se ktere scita gradienty
                while (templLast.cisloFiltra != net.convolutions.size / 3 ) {
                    templLast = templLast.next;
                }
                for (int j = 0; j < net.convolutions.size / 3; j++) {
                    grad += templLast.grad;					//sumarizuje gradient predhozi vrstvy
                    templLast = templLast.next;
                }
                templ.grad = grad * 0.388 * (1.7159 - templ.averageOutput) * (1.7159 + templ.averageOutput) / Neuronet.druhaVrstva;
                if (grad == 0) {
                    gradNull++;
                }
                for (int k = 0; k < templ.weights3D.length; k++) {
                    for (int q = 0; q < templ.weights3D[0].length; q++) {
                        for(int z=0; z < templ.weights3D[0][0].length; z++) {
                            templ.weights3D[k][q][z] += net.speedL3CL * templ.grad * templ.avInput[k][q];
                        }
                    }
                }
                templ = templ.next;
            }

            if (Answer != lokResult) {
                lokError++;
            }

            if (shakeFlag) {
                net.speedL1CL /= 5;
                net.speedL2CL /= 5;
                net.speedL3CL /= 5;
                net.speedLFCN /= 5;
                shakeFlag = false;
            }
            if ((lokError / iteration * 100) < errorMin) {
                errorMin = lokError / iteration * 100;
            }

            if (iteration % 10 == 0) {
                testValue = test();
                if(lastResult != testValue) {
                    lastResult = testValue;
                    changeIteration = iteration;
                }
//                if (iteration - changeIteration > 1500) {
//                    net.speedL1CL *= 5;
//                    net.speedL2CL *= 5;
//                    net.speedL3CL *= 5;
//                    net.speedLFCN *= 5;
//                    shakeFlag = true;
//                }

                if (testValue > bestTestValue) {
                    bestTestValue = testValue;
                    System.out.println("");
                    System.out.println("Better result >>>>>>>>>>>  "+bestTestValue);
                    System.out.println("");
                }
                writeProgressInfo(iteration, testValue);

                if (epochaWithoutNewNeuron>=50) {
                    System.out.println("epochaWithoutNewNeuron "+ epochaWithoutNewNeuron);
                    System.out.println("Error difference " + (lastError-lokError/iteration*100));
                    if (Math.abs(lastError-lokError/iteration*100) < 1) {
                        epochaWithoutNewNeuron=0;
                        addNeuron();
                    }
                }
                else
                    epochaWithoutNewNeuron++;

                if (iteration % 1000000 == 0) {
                    _convolutionsInfo = null;
                }
                if (iteration % 1000000 == 0) {
                    progressInfo = null;
                }
                gradNull = 0;
                newEpoch();
            }
            lastError = lokError / iteration * 100;
            iteration++;
        }
        writeSrtuct();
        try {
            serializace("normal");
        } catch (Exception e){
            System.out.println("Serialization error in 'normal' way");
        }
    }

    static Boolean addNeuron(){
        counterNeuronNew++;
        System.out.println("---------------------------New neuron added!!---------------------------------");
//        writeWeights(2);
        if (counterAddNeuron==1){//counterAddNeuron==1
            counterAddNeuron=0;
            net.l1.addNeuron();
            net.druhaVrstva++;
            net.l1.length=net.druhaVrstva;
            net.l1.outputs=new double[net.druhaVrstva];

            Neuron templ=net.l2.head;
//            for(int i=0;i<net.tretiVrstva;i++){
//                templ.weights=new double[net.druhaVrstva];
//                templ.doRandomWeights();
//                templ.input= new double[net.druhaVrstva];
//                templ=templ.next;
//            }

            for(int i=0; i<net.tretiVrstva; i++) {
                templ.addOneWeightAndInput(Neuronet.druhaVrstva);
                templ = templ.next;
            }

//            templ=net.l1.head;
//            while(templ.next!=null)
//                templ=templ.next;
//            templ.weights=new double[net.prvniVrstva];
//            templ.doRandomWeights();
//            templ.input=new double[net.prvniVrstva];

//          initialization("pro druhou vrstvu");
            return false;
        }
        else {

            counterAddNeuron++;
            net.l0.addNeuron();
            net.prvniVrstva++;
            net.l0.length=net.prvniVrstva;
            net.l0.outputs=new double[net.prvniVrstva];

            Neuron templ=net.l1.head;
//            for(int i=0;i<net.druhaVrstva;i++){
//                templ.weights=new double[net.prvniVrstva];
//                templ.doRandomWeights();
//                templ.input= new double[net.prvniVrstva];
//                templ=templ.next;
//            }
            for(int i=0; i<net.druhaVrstva; i++) {
                templ.addOneWeightAndInput(Neuronet.prvniVrstva);
                templ = templ.next;
            }
//          initialization(1);
            return true;
        }
    }

    static double[][] pooling(int size, double[][] picture) {             	// size treba 2x2 => size=2
        double templ = (double)picture.length / size;                           //jenom pro to, aby Math.ceil spravne zaokrouhlil
        int massSize1 = (int)Math.ceil(templ);
        templ = (double)(picture[0].length) / size;                             //jenom pro to, aby Math.ceil spravne zaokrouhlil
        int massSize2 = (int)Math.ceil(templ);
        if (massSize2 == 0) {
            massSize2 = 1;
        }
        double[][] result = new double[massSize1][massSize2];
        int x0, x1, y0, y1;
        y0 = 0;
        y1 = size;
        for (int i = 0; i < result.length; i++) {
            x0 = 0;
            x1 = size;
            for (int j = 0; j < result[0].length; j++) {
                result[i][j] = max(picture, x0, x1, y0, y1);
                x0 += size;
                x1 += size;
            }
            y0 += size;
            y1 += size;
        }
        return result;
    }

    static double max(double[][] picture, int x0, int x1, int y0, int y1) {
        double result = picture[y0][x0];
        for (int i = y0; i < y1; i++) {
            for (int j = x0; j < x1; j++) {
                try{
                    if (picture[i][j] > result) {
                        result = picture[i][j];
                    }
                }
                catch(Exception e){}
            }
        }
        return result;
    }

    static double[][] applyConvolution(int cisloFiltra, Picture picture) {
        int countTempl = 0;
        Convolution templ = net.convolutions.head;
        while (cisloFiltra != 0) {
            templ = templ.next;
            cisloFiltra--;
        }
        int x;
        int y;
        if (picture.map3D == null) {
            x = picture.map2D[0].length - templ.weights2D[0].length + 1;		// rozmer vysledne matici - x a y
            y = picture.map2D.length - templ.weights2D.length + 1;
        }
        else {
            x = picture.map3D[0].length - templ.weights3D[0].length + 1;		// rozmer vysledne matici - x a y
            y = picture.map3D.length - templ.weights3D.length + 1;
        }
        double[][] result = new double[y][x];
        int x0, y0;
        for(int i=0; i<y; i++) {
            x0 = 0;
            y0 = 0;
            for(int j=0; j<x; j++) {
                result[i][j] = sum(picture ,templ, x0, y0);
                x0++;
            }
            y0++;
        }
        return result;
    }

    static double sum(Picture picture, Convolution templ, int x0, int y0) {
        double result = 0;
        int y = 0;			// kountery pro konvoluce
        int x = 0;
        if(picture.map3D == null) {
            for(int i=y0; i<y0+templ.weights2D.length; i++) {
                for(int j=x0; j<x0+templ.weights2D[0].length; j++) {
                    if(j==picture.map2D[0].length) {
                        result += picture.map2D[i][j-1]*templ.weights2D[y][x];
                        templ.addInput(picture.map2D[i][j - 1], y, x);
                    }
                    else {
                        result += picture.map2D[i][j]*templ.weights2D[y][x];
                        templ.addInput(picture.map2D[i][j], y, x);
                    }
                    x++;
                }
                x=0;
                y++;
            }
            templ.addOutput(result);
            return result;
        }
        else {
            for(int i=y0; i<y0+templ.size2; i++) {
                for(int j=x0; j<x0+templ.size1; j++) {
                    for(int k=0; k<3; k++) {
                        if(j==picture.map3D[0].length) {
                            result += picture.map3D[i][j-1][k]*templ.weights3D[y][x][k];
                            templ.addInput(picture.map3D[i][j - 1][ k], y, x, k);
                        }
                        else {
                            result += picture.map3D[i][j][k]*templ.weights3D[y][x][k];
                            templ.addInput(picture.map3D[i][j][k], y, x, k);
                        }
                    }
                    x++;
                }
                x=0;
                y++;
            }
            templ.addOutput(result);
            return result;
        }
    }

    static double[] doOneArray(double[][][] thirdConvolution) {
        int length = thirdConvolution.length * thirdConvolution[10].length;
        double[] result = new double[length];
        int counter = 0;
        for (int i = 0; i < thirdConvolution.length; i++) {
            for (int j = 0; j < thirdConvolution[i].length; j++) {
                for (int k = 0; k < thirdConvolution[i][0].length; k++) {
                    result[counter] = thirdConvolution[i][j][k];
                    counter++;
                }
            }
        }
        return result;
    }

    static void writeSrtuct() {
        String text = "NN struct:" + "\r\n";
        text+= "vstupni vrstva: "+ net.inputLength+ "\r\n";
        text+= "prvni vrstva: "+ net.prvniVrstva+ "\r\n";
        text+= "druha vrtstva: "+ net.druhaVrstva+ "\r\n";
        text+= "treti vrstva: "+ net.tretiVrstva;

        write("NN struct.txt", text);
    }

    public static void writeInfo(double[][] picture, String typeOfTransformation)  {                //zobrazuje zmeny primo v konvolucich
        _info += typeOfTransformation + " -> " + "\r\n" + "x- " + picture[0].length + "\r\n" + "y -" + picture.length + "\r\n";
        try {
            File.createTempFile("Date.txt", _info); //WriteAllText("Date.txt", _info);
        } catch (IOException e) {
        }
        writeConvolution(picture, typeOfTransformation);
    }

    static void writeProgressInfo(int iteration, double testValue) {
        progressInfo += "epoch = " + iteration / 10 + " test value = " + testValue + "\r\n";
        System.out.println("epoch = " + iteration / 10 + " test value = " + testValue);
        write("Short progress info.txt", progressInfo);
    }

    static void writeAllConvolution(int iteration) {
        Convolution templ = net.convolutions.head;
        for (int i = 0; i < net.convolutions.size; i++) {
            writeConvolution(templ, i, iteration);
            templ = templ.next;
        }
    }

    static void writeConvolution(Convolution convolution, int counvolutionNumber, int iteration ) {
        if(firstEnter) {
            firstEnter = false;
            _convolutionsInfo = null;
        }
        _convolutionsInfo += "iteration:"+iteration+" №:"+counvolutionNumber;
        _convolutionsInfo += "\r\n";
        if(convolution.weights3D == null) {
            for (int i = 0; i < convolution.weights2D.length; i++) {
                for (int j = 0; j < convolution.weights2D[0].length; j++) {
                    _convolutionsInfo += convolution.weights2D[i][j] + "  ";
                }
                _convolutionsInfo += "\r\n";
            }
            _convolutionsInfo += "\r\n";
        }

        else {
            for (int i = 0; i < convolution.weights3D.length; i++) {
                for (int j = 0; j < convolution.weights3D[0].length; j++) {
                    _convolutionsInfo += "[";
                    for (int k=0; k<convolution.weights3D[0][0].length; k++) {
                        _convolutionsInfo += convolution.weights3D[i][j][k] + ",";
                    }
                    _convolutionsInfo += "] ";
                }
                _convolutionsInfo += "\r\n";
            }
            _convolutionsInfo += "\r\n"+"\r\n";
        }
        write("Convolutions.txt", _convolutionsInfo);
    }

    static void writeConvolution(double[][] picture, String typeOfTransformation) {
        _convolutionsInfo += typeOfTransformation + "\r\n";
        for (int i = 0; i < picture.length; i++) {
            for (int j = 0; j < picture[0].length; j++) {
                _convolutionsInfo += picture[i][j] + "  ";
            }
            _convolutionsInfo += "\r\n";
        }
        _convolutionsInfo += "\r\n" + "\r\n";
        write("Convolutions.txt", _convolutionsInfo);

    }

    public static int test() throws Exception {
        int vysledek = 0;
        for (int j = 0; j < 1; j++) {
            newEpoch();
            for (int i = 0; i < 10; i++) {
                int vysOperace = calculateResult(getPicture(true));
                if (Answer == vysOperace) {
                    vysledek += 1;
                } else {
                    vysledek += 0;
                }
            }
        }
        return vysledek * 10;
    }

    public static void serializace(String wayOfSaving) throws Exception {
        String fileName = null;
        if (wayOfSaving == "normal") {
            fileName = "wiaghts";
        } else {
            fileName = "BestWeights";;
        }
        FileOutputStream fos = new FileOutputStream(fileName+".out");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(net);
        oos.close();
    }

    static Neuronet deseralizace(String way) throws Exception {
        try {
            way += ".out";
            Neuronet net = null;
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(way));
            net = (Neuronet) in.readObject();
            return net;
        } catch (Exception e) {
            return new Neuronet();
        }
    }

    static double[][] addX(double[][] picture) {					//pridani sloupce '0' k polu
        double[][] result = new double[picture.length][picture[0].length + 1];
        for (int i = 0; i < picture.length; i++) {
            for (int j = 0; j < picture[0].length + 1; j++) {
                if (j == picture[0].length) {
                    result[i][j] = -100;
                } else {
                    result[i][j] = picture[i][j];
                }
            }
        }
        return result;
    }

    static double[][] addY(double[][] picture) {					//pridani radka '0' k polu
        double[][] result = new double[picture.length + 1][picture[0].length];
        for (int i = 0; i < picture.length + 1; i++) {
            for (int j = 0; j < picture[0].length; j++) {
                if (i == picture.length) {
                    result[i][j] = -100;
                } else {
                    result[i][j] = picture[i][j];
                }
            }
        }
        return result;
    }

    static void write(String fileName, String text) {
        File file = new File(fileName);
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
        } catch (IOException e) {
//            throw new RuntimeException(e);
        }
    }

    static BufferedImage scaleImage(BufferedImage input ) throws IOException {
        BufferedImage scaled = new BufferedImage(40, 40,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = scaled.createGraphics();
        g.drawImage(input, 0, 0, 40, 40, null);
        g.dispose();

//        ImageIO.write(scaled, "JPEG", new File("111.jpg"));
        return (BufferedImage) scaled;
    }

    static void prepareImages() throws IOException {
        String path = null;
        for (int i=0; i<UzByli.length; i++) {
            try {
                path = "foto_0.8/"+Integer.toString(i)+".jpeg";
                BufferedImage inputImage = ImageIO.read(new File(path));
                inputImage = scaleImage(inputImage);
                path = "foto/"+Integer.toString(i)+".jpeg";
                ImageIO.write(inputImage,"jpeg", new File(path));
            } catch (IIOException e){
                System.out.println("File reading error! massege genereted with file:"+path);
            }
        }
    }
}
