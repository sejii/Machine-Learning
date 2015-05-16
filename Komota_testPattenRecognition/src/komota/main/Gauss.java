package komota.main;

import java.util.ArrayList;
import java.util.Random;

public class Gauss {
	public ArrayList<double[]> gauss = new ArrayList<double[]>();
	int num = 100000;
	final double NOISE = 0.2;
	Random rand = new Random();
	//コンストラクタ
	public Gauss(){
		for (int i = 0; i < num; i++) {
			double[] temp = new double[2];
			temp[0] = 0.7 * rand.nextGaussian() + 5;
			temp[1] = 0.7 * rand.nextGaussian() + 5;
			gauss.add(temp);
			//一様ノイズ
			if(Math.random() < NOISE){
				temp = new double[2];
				temp[0] = Math.random() * 10 + 5;
				temp[1] = Math.random() * 10 + 5;
				gauss.add(temp);
			}
		}
		for (int i = 0; i < num; i++) {
			double[] temp = new double[2];
			temp[0] = 0.7 * rand.nextGaussian() + 15;
			temp[1] = 0.7 * rand.nextGaussian() + 10;
			gauss.add(temp);
			//一様ノイズ
			if(Math.random() < NOISE){
				temp = new double[2];
				temp[0] = Math.random() * 10 + 5;
				temp[1] = Math.random() * 10 + 5;
				gauss.add(temp);
			}
		}
		for (int i = 0; i < num; i++) {
			double[] temp = new double[2];
			temp[0] = 0.7 * rand.nextGaussian() + 5;
			temp[1] = 0.7 * rand.nextGaussian() + 15;
			gauss.add(temp);
			temp = new double[2];
			temp[0] = 0.7 * rand.nextGaussian() + 11;
			temp[1] = 0.7 * rand.nextGaussian() + 13;
			gauss.add(temp);
			//一様ノイズ
			if(Math.random() < NOISE){
				temp = new double[2];
				temp[0] = Math.random() * 10 + 5;
				temp[1] = Math.random() * 10 + 5;
				gauss.add(temp);
			}
		}
		/*
		for(int i = 0;i < num;i++){
			double[] temp = new double[2];
			temp[0] = i / 10;
			temp[1] = Math.sin(temp[0]) * 5;
			temp = new double[2];
			temp[0] = i / 10;
			temp[1] = Math.sin(temp[0]) * 5;
			gauss.add(temp);
			//一様ノイズ
			if(Math.random() < NOISE){
				temp = new double[2];
				temp[0] = Math.random() * 10 + 5;
				temp[1] = Math.random() * 10 + 5;
				gauss.add(temp);
			}
		}
		*/
	}

	public int gausssize(){
		return gauss.size();
	}

	public double[] gaussget(int i){
		return gauss.get(i);
	}


}
