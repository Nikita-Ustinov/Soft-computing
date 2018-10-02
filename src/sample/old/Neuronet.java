package sample.old;

import java.io.Serializable;
//import java.util.LinkedList;
//	[Serializable]
public final class Neuronet implements Serializable
{
    public List l0, l1, l2;
    public static int inputLength = 250; 		//pro fully connection neuronet
    public static int prvniVrstva =  150;
    public static int druhaVrstva = 120;
    public static int tretiVrstva = 10;
    public double speedLFCN = 0.0012;				//rychlost uceni pro "fully connection neuronet"
    public double speedL1CL = 0.0018;				//rychlost uceni pro prvni "convolution layer" 10 az 14
    public double speedL2CL = 0.0018;				//rychlost uceni pro druhy "convolution layer"  5 az 9
    public double speedL3CL = 0.0018;				//rychlost uceni pro treti "convolution layer"  0 az 4

    public int inputLengthOwn;
    public int prvniVrstvaOwn;
    public int druhaVrstvaOwn;
    public int tretiVrstvaOwn;
    public ListOfConvolutions convolutions ;
    public static int cisloFiltra = 0;

    public Neuronet()
    {
        addFilter(11,11,3);				//pridani konvoluci 11x11x3
        addFilter(11,11,3);				//pridani konvoluci 11x11x3
        addFilter(11,11,3);				//pridani konvoluci 11x11x3
        addFilter(11,11,3);				//pridani konvoluci 11x11x3
        addFilter(11,11,3);                              //pridani konvoluci 11x11x3

        addFilter(5,5);					//pridani konvoluci 5x5
        addFilter(5,5);					//pridani konvoluci 5x5
        addFilter(5,5);					//pridani konvoluci 5x5
        addFilter(5,5);					//pridani konvoluci 5x5
        addFilter(5,5);					//pridani konvoluci 5x5

        addFilter(3,3);					//pridani konvoluci 3x3
        addFilter(3,3);					//pridani konvoluci 3x3
        addFilter(3,3);					//pridani konvoluci 3x3
        addFilter(3,3);					//pridani konvoluci 3x3
        addFilter(3,3);					//pridani konvoluci 3x3

        l0 = new List(0);				//create first fully connected layer  - prvni vrstva
        l1 = new List(1);				//create second fully connected layer - druha vrstva
        l2 = new List(2);				//create output fully connected layer - vystupni vrstva

        inputLengthOwn = inputLength;
        prvniVrstvaOwn = prvniVrstva;
        druhaVrstvaOwn = druhaVrstva;
        tretiVrstvaOwn = tretiVrstva;
    }

    public Neuronet(int layerNumber) {
        List l1 = new List(layerNumber);
        List l2 = new List(l1);
    }

    void addFilter(int size1, int size2, int size3) {
        cisloFiltra=0;
        if(convolutions == null) {
            Convolution newConvolution = new Convolution(size1, size2, size3, cisloFiltra);
            convolutions = new ListOfConvolutions(newConvolution);
//                convolutions.addConvolution(new Convolution(size1, size2, size3, cisloFiltra));
        }
        else {
            cisloFiltra++;
            Convolution templ = convolutions.head;
            while(templ.next != null) {
                templ = templ.next;
                cisloFiltra++;
            }
            convolutions.addConvolution(new Convolution(size1, size2, size3, cisloFiltra));
        }

    }

    void addFilter(int size1, int size2) {
        cisloFiltra = 0;
        int countTempl = 0;
        if(convolutions==null) {
            Convolution newConvolution = new Convolution(size1, size2, cisloFiltra);
            convolutions = new ListOfConvolutions(newConvolution);
        }
        else {
            cisloFiltra++;
            Convolution templ = convolutions.head;
            while(templ.next != null) {
                templ = templ.next;
                cisloFiltra++;
            }
            Convolution newConvolution = new Convolution(size1, size2, cisloFiltra);
            convolutions.addConvolution(newConvolution);
        }

    }
}


