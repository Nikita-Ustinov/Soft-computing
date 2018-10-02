package sample.old;


public class Picture
{	
    public double[][][] map3D = null;
    public double[][] map2D = null;
    public boolean isColor;


    public Picture(int size1, int size2, int size3) {		//for 3-d picture
            map3D = new double[size1][size2][size3];
            isColor = true;					
    }

    public Picture(int size1, int size2) {			//for 2-d picture
            map2D = new double[size1][size2];
            isColor = false;
    }

    public Picture(double[][] array) {
            map2D = array;
    }
}
