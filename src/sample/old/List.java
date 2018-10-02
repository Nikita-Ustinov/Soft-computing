package sample.old;


import java.io.Serializable;


//[Serializable]
public class List implements Serializable
{
    public int length;
    public Neuron head;
    public double [] outputs;
    int vrstva;


    public List(int vrstva)
    {
        this.vrstva=vrstva;
        if (vrstva==0)
            length=Neuronet.prvniVrstva;
        if(vrstva==1)
            length=Neuronet.druhaVrstva;
        if(vrstva==2)
            length=Neuronet.tretiVrstva;
        Neuron templ;
        for (int i=0; i< length; i++){
            Neuron node=new Neuron(vrstva);
            if (i==0)
                head=node;
            else {
                templ=head;
                while(templ.next!=null){
                    templ = templ.next;
                }
                do{
                    node= new Neuron(vrstva);
                } while(templ.weights[0]==node.weights[0]);
                templ.next=node;
            }
        }
        outputs=new double[length];
    }

    public List(int input, int output) {
        Neuron templ;
        for (int i=0; i< output; i++){
            Neuron node=new Neuron(input);
            if (i==0)
                head=node;
            else {
                templ=head;
                while(templ.next!=null){
                    templ = templ.next;
                }
                do{
                    node= new Neuron(input);
                } while(templ.weights[0]==node.weights[0]);
                templ.next=node;
            }
        }
        outputs=new double[output];
    }

    public List(List original) {    //dela z puvodniho Linked List(neuronu) opačný
        int output = original.head.input.length;
        int input = original.length;
        Neuron templ;
        for (int i=0; i< output; i++){
            Neuron node=new Neuron(input);
            if (i==0)
                head=node;
            else {
                templ=head;
                while(templ.next!=null){
                    templ = templ.next;
                }
                do{
                    node= new Neuron(input);
                } while(templ.weights[0]==node.weights[0]);
                templ.next=node;
            }
        }
        outputs=new double[output];
    }

    public void writeInput(double[] input){
        Neuron templ=head;
        for (int i=0; i<Neuronet.prvniVrstva; i++){
            templ.input = new double[Neuronet.inputLength];
            for (int j=0; j<input.length; j++){
//                    System.out.print("i= "+i);
//                    System.out.println(" j= "+j);
//                    if (j==150) {
//                        j = 150;
//                    }
                templ.input[j]= input[j];
            }
            templ=templ.next;
        }
    }


    public void countOutputs() throws Exception{
        Neuron templ=head;
        int counter=0;
        while(templ!=null){
            outputs[counter]=templ.countOut();
            templ= templ.next;
            counter++;
        }
    }

    public void addNeuron(){
        Neuron templ;
        templ=head;
        while(templ.next!=null){
            templ= templ.next;
        }
        Neuron node= new Neuron(vrstva);
        templ.next=node;
    }

//    public void plusOneRundomizeWeight() {
//        Neuron templ = head;
//        for(int i=0; i<outputs.length; i++) {
//            double[] newWeights = new double[templ.weights.length+1];
//            newWeights = templ.weights;
//            newWeights[templ.weights.length] =
//        }
//    }
}

