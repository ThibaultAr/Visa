import java.awt.Color;
import java.util.Random;

import javax.swing.JOptionPane;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.NewImage;
import ij.gui.Plot;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
   

public class FCM_Visa_ implements PlugIn 
{
	int indice;
	double min,max;

	class Vec 
	{
		int[] data = new int[3];	//*pointeur sur les composantes*/
	} 

	////////////////////////////////////////////////////
	Random r = new Random();    
	public int rand(int min, int max) 
    {
        return min + (int)(r.nextDouble()*(max-min));
    }

	
////////////////////////////////////////////////////////////////////////////////////////////
    public void run(String arg) 
	{

		// LES PARAMETRES

		
		ImageProcessor ip;
		ImageProcessor ipseg;
		ImageProcessor ipJ;  
		ImagePlus imp;
		ImagePlus impseg;
		ImagePlus impJ;
		IJ.showMessage("Algorithme FCM","If ready, Press OK");
		ImagePlus cw;

		imp = WindowManager.getCurrentImage();
		ip = imp.getProcessor();

		int width = ip.getWidth();
		int height = ip.getHeight();
 		
		
		Object[] possibilities = {"FCM", "HCM", "PCM", "DAVE"};
		String choice = (String) JOptionPane.showInputDialog(null, "Which Technique ?", "Technique", JOptionPane.QUESTION_MESSAGE, null, possibilities, "FCM");

		impseg=NewImage.createImage("Image segmentée par " + choice,width,height,1,24,0);
		ipseg = impseg.getProcessor();
		impseg.show();

		
		int nbclasses,nbpixels,iter;
		double stab,seuil,valeur_seuil;
		int i,j,k,l;
		
		String demande =JOptionPane.showInputDialog("Nombre de classes : ");
		nbclasses =Integer.parseInt(demande);
		nbpixels = width * height; // taille de l'image en pixels
		
		demande =JOptionPane.showInputDialog("Valeur de m : ");
		double m =Double.parseDouble(demande);

		demande =JOptionPane.showInputDialog("Nombre itération max : ");
		int itermax =Integer.parseInt(demande);

		demande =JOptionPane.showInputDialog("Valeur du seuil de stabilité : ");
		valeur_seuil =Double.parseDouble(demande);

		demande =JOptionPane.showInputDialog("Randomisation améliorée ? ");
		int valeur=Integer.parseInt(demande);


		double cprev[][] = new double[nbclasses][3]; 
		int cidx[] = new int[nbclasses];
		//double m;
		double red[] = new double[nbpixels];
		double green[] = new double[nbpixels];
		double blue[] = new double[nbpixels];
		int[] colorarray = new int[3];
		double figJ[]=new double[itermax];
		for(i=0;i<itermax;i++)
		{
		  figJ[i]=0;
		}

		// Récupération des données images 
		l = 0;
		for(i = 0; i < width; i++)
		{
			for(j = 0; j < height; j++)
			{
				ip.getPixel(i,j,colorarray);
				red[l] = (double)colorarray[0];
				green[l] =(double) colorarray[1];
				blue[l] = (double)colorarray[2];
				l++;
			}
		}
		
		switch(choice){
		case "FCM":
			System.out.println("FCM");
			FCM(ip, ipseg, impseg, red, green, blue, figJ, m, nbclasses, width, height, valeur, itermax, valeur_seuil);
			break;
		case "HCM":
			System.out.println("HCM");
			HCM(ip, ipseg, impseg, red, green, blue, figJ, nbclasses, width, height, valeur, itermax, valeur_seuil);
			break;
		case "PCM":
			System.out.println("PCM");
			PCM(ip, ipseg, impseg, red, green, blue, figJ, m, nbclasses, width, height, valeur, itermax, valeur_seuil);
			break;
		case "Dave":
			System.out.println("Dave");
			Dave(ip, ipseg, impseg, red, green, blue, figJ, m, nbclasses, width, height, valeur, itermax, valeur_seuil);
			break;
		}
	}
    
	private void Dave(ImageProcessor ip, ImageProcessor ipseg, ImagePlus impseg, double[] red, double[] green,
			double[] blue, double[] figJ, double m, int nbclasses, int width, int height, int valeur, int itermax,
			double valeur_seuil) {
		System.out.println("Not Yet Implemented");
		
	}

	private void PCM(ImageProcessor ip, ImageProcessor ipseg, ImagePlus impseg, double[] red, double[] green,
			double[] blue, double[] figJ, double m, int nbclasses, int width, int height, int randomAmeliore, int itermax,
			double valeur_seuil) {
		
		int imax, jmax, kmax;
		int nbpixels = width * height;
		double Dmat[][] = new double[nbclasses][nbpixels];
		double Dprev[][] = new double[nbclasses][nbpixels];
		double Umat[][] = new double[nbclasses][nbpixels];
		double Uprev[][] = new double[nbclasses][nbpixels];
		double c[][] = new double[nbclasses][3];
		double nu[] = new double[nbclasses];
		int[] init=new int[3];
		
		imax = nbpixels;  // nombre de pixels dans l'image
		jmax = 3;  // nombre de composantes couleur
		kmax=nbclasses;
		double data[][] = new double[nbclasses][3];
		int[] fixe=new int[3]; 
		int xmin = 0;
	   	int xmax = width;
	   	int ymin = 0;
		int ymax = height;
		int rx, ry;	
		int x,y;
		int epsilonx,epsilony;
		int i, j, k, l;
		int iter;
		double stab,seuil;
	
		// Initialisation des centroides (aléatoirement )
	
		for(i=0;i<nbclasses;i++){
			if(randomAmeliore==1) {
				epsilonx=rand((int)(width/(i+2)),(int)(width/2));
			    epsilony=rand((int)(height/(4)),(int)(height/2));
			} else {
			   epsilonx=0;
			   epsilony=0;
			}
			rx = rand(xmin+epsilonx, xmax-epsilonx);
			ry = rand(ymin+epsilony, ymax-epsilony);
			ip.getPixel(rx,ry,init);
			c[i][0] = init[0]; c[i][1] =init[1]; c[i][2] = init[2];
		}
			
		// Calcul de distance entre data et centroides
		for(l = 0; l < nbpixels; l++)
		{
			for(k = 0; k < kmax; k++)
			{
				double r2 = Math.pow(red[l] - c[k][0], 2);
				double g2 = Math.pow(green[l] - c[k][1], 2);
				double b2 = Math.pow(blue[l] - c[k][2], 2);
				Dprev[k][l] = r2 + g2 + b2;
			}
		}
		
		for(l = 0; l < nbpixels; l++) {
			for(i = 0; i < kmax; i++) {
				double res = 0.0;
				for(k = 0; k < kmax; k++){
					res += Math.pow((Dprev[i][l] / Dprev[k][l]), 2/(m-1));
				}
				Uprev[i][l] = Double.isNaN(1.0 / res) ? 0.0 : 1.0/res;
			}
		}
		
		//Init nu
		for(i = 0; i<kmax; i++){
			double sum1 = 0.0;
			double sum2 = 0.0;
			for(j = 0; j < nbpixels; j++) {
				sum1 += Math.pow(Uprev[i][j], m) * Dprev[i][j];
				sum2 += Math.pow(Uprev[i][j], m);
			}
			nu[i] = sum1 / sum2;
		}

		iter = 0;
		stab = 2;
		seuil = valeur_seuil;
		
		while ((iter < itermax) && (stab > seuil)) 
		{
	
			
		// Update  the matrix of centroids
		for(k = 0; k < nbclasses; k++){
			double sum[] = {0,0,0};
			double sum2 = 0;
			
			for(l = 0; l < nbpixels; l++) {
				sum[0] += Math.pow(Uprev[k][l], m) * red[l];
				sum[1] += Math.pow(Uprev[k][l], m) * green[l];
				sum[2] += Math.pow(Uprev[k][l], m) * blue[l];
				
				sum2 += Math.pow(Uprev[k][l], m);
			}
			
			c[k][0] = sum[0] / sum2;
			c[k][1] = sum[1] / sum2;
			c[k][2] = sum[2] / sum2;
		}
		
		// Compute Dmat, the matrix of distances (euclidian) with the centroids
		for(l = 0; l < nbpixels; l++)
		{
			for(k = 0; k < kmax; k++)
			{
				double r2 = Math.pow(red[l] - c[k][0], 2);
				double g2 = Math.pow(green[l] - c[k][1], 2);
				double b2 = Math.pow(blue[l] - c[k][2], 2);
				Dmat[k][l] = r2 + g2 + b2;
			}
		}
		
		for(l = 0; l < nbpixels; l++) {
			for(i = 0; i < kmax; i++) {
				Umat[i][l] = 1.0 / Math.pow((1.0 + Dmat[i][l] / nu[i]), 1.0 / (m-1));
			}
		}
		
		// Calculate difference between the previous partition and the new partition (performance index)
		double sum = 0.0;
		double subsum = 0.0;
		for(i = 0; i<nbclasses; i++) {
			for(j = 0; j<nbpixels; j++) {
				figJ[iter] += Math.pow(Uprev[i][j], m) * Dprev[i][j];
				subsum += Math.pow(1.0 - Uprev[i][j], m);
			}
			sum += nu[i] * subsum;
		}
		figJ[iter] += sum;
		
		//Update nu
		for(i = 0; i<kmax; i++){
			double sum1 = 0.0;
			double sum2 = 0.0;
			for(j = 0; j < nbpixels; j++) {
				sum1 += Math.pow(Uprev[i][j], m) * Dprev[i][j];
				sum2 += Math.pow(Uprev[i][j], m);
			}
			nu[i] = sum1 / sum2;
		}
		
		stab = iter == 0 ? figJ[iter] : Math.abs(figJ[iter] - figJ[iter-1]);
		Uprev = Umat;
		Dprev = Dmat;
		iter++;
		////////////////////////////////////////////////////////
	
		// Affichage de l'image segmentée 
		double[] mat_array=new double[nbclasses];
		l = 0;
		for(i=0;i<width;i++)
		{
		  for(j = 0; j<height; j++)
		{
			for(k = 0; k<nbclasses; k++)
			{ 
			 mat_array[k]=Umat[k][l];
			}
			int indice= IndiceMaxOfArray(mat_array,nbclasses) ;
			int array[] = new int[3];
			array[0] = (int)c[indice][0];
			array[1] = (int)c[indice][1];
			array[2] = (int)c[indice][2];
			ipseg.putPixel(i, j, array);
			l++;
		}
		}
		impseg.updateAndDraw();
		//////////////////////////////////
		}
		
		double[] xplot= new double[itermax];
		double[] yplot=new double[itermax];
		for(int w = 0; w < itermax; w++){
			xplot[w]=(double)w;	yplot[w]=(double) figJ[w];
		}
		Plot plot = new Plot("Performance Index (PCM)","iterations","J(P) value",xplot,yplot);
		plot.setLineWidth(2);
		plot.setColor(Color.blue);
		plot.show();
	}

	private void HCM(ImageProcessor ip, ImageProcessor ipseg, ImagePlus impseg, double[] red, double[] green,
			double[] blue, double[] figJ, int nbclasses, int width, int height, int randomAmeliore, int itermax,
			double valeur_seuil) {
		
		int imax, jmax, kmax;
		int nbpixels = width * height;
		double Dmat[][] = new double[nbclasses][nbpixels];
		double Dprev[][] = new double[nbclasses][nbpixels];
		double Umat[][] = new double[nbclasses][nbpixels];
		double Uprev[][] = new double[nbclasses][nbpixels];
		double c[][] = new double[nbclasses][3];
		int[] init=new int[3];
		
		imax = nbpixels;  // nombre de pixels dans l'image
		jmax = 3;  // nombre de composantes couleur
		kmax=nbclasses;
		double data[][] = new double[nbclasses][3];
		int[] fixe=new int[3]; 
		int xmin = 0;
	   	int xmax = width;
	   	int ymin = 0;
		int ymax = height;
		int rx, ry;	
		int x,y;
		int epsilonx,epsilony;
		int i, j, k, l;
		int iter;
		double stab,seuil;
		
		// Initialisation des centroédes (aléatoirement)
		for(i=0;i<nbclasses;i++) {
		    if(randomAmeliore==1) {  
		    	epsilonx=rand((int)(width/(i+2)),(int)(width/2));
		    	epsilony=rand((int)(height/(4)),(int)(height/2));
			} else {
				epsilonx=0;
		        epsilony=0;
			}
			rx = rand(xmin+epsilonx, xmax-epsilonx);
			ry = rand(ymin+epsilony, ymax-epsilony);
			ip.getPixel(rx,ry,init);
			c[i][0] = init[0]; c[i][1] =init[1]; c[i][2] = init[2];
		}
		
		// Calcul de distance entre data et centroides
		for(l = 0; l < nbpixels; l++)
		{
			for(k = 0; k < kmax; k++)
			{
				double r2 = Math.pow(red[l] - c[k][0], 2);
				double g2 = Math.pow(green[l] - c[k][1], 2);
				double b2 = Math.pow(blue[l] - c[k][2], 2);
				Dprev[k][l] = r2 + g2 + b2;
			}
		}
		
		// Initialisation des degrés d'appartenance
		for(l = 0; l < nbpixels; l++) {
			for(i = 0; i < kmax; i++) {
				double res = 1.0;
				for(k = 0; k < kmax; k++){
					if(k!=i && !(Dprev[i][l] < Dprev[k][l]))
						res = 0.0;					
				}
				Uprev[i][l] = res;
			}
		}
		
		iter = 0;
		stab = 2;
		seuil = valeur_seuil;
		
	
		while ((iter < itermax) && (stab > seuil)) {	
			// Update  the matrix of centroids
			for(k = 0; k < nbclasses; k++){
				double sum[] = {0,0,0};
				double sum2 = 0;
				
				for(l = 0; l < nbpixels; l++) {
					sum[0] += Uprev[k][l] * red[l];
					sum[1] += Uprev[k][l] * green[l];
					sum[2] += Uprev[k][l] * blue[l];
					
					sum2 += Uprev[k][l];
				}
				
				c[k][0] = sum[0] / sum2;
				c[k][1] = sum[1] / sum2;
				c[k][2] = sum[2] / sum2;
			}
			
			// Compute Dmat, the matrix of distances (euclidian) with the centroids
			for(l = 0; l < nbpixels; l++)
			{
				for(k = 0; k < kmax; k++)
				{
					double r2 = Math.pow(red[l] - c[k][0], 2);
					double g2 = Math.pow(green[l] - c[k][1], 2);
					double b2 = Math.pow(blue[l] - c[k][2], 2);
					Dmat[k][l] = r2 + g2 + b2;
				}
			}
			
			for(l = 0; l < nbpixels; l++) {
				for(i = 0; i < kmax; i++) {
					double res = 1.0;
					for(k = 0; k < kmax; k++){
						if(k!=i && !(Dmat[i][l] < Dmat[k][l]))
							res = 0.0;					
					}
					Umat[i][l] = res;
				}
			}
			
			// Calculate difference between the previous partition and the new partition (performance index)
			for(i = 0; i<nbclasses; i++) {
				for(j = 0; j<nbpixels; j++) {
					figJ[iter] += Uprev[i][j] * Dprev[i][j];
				}
			}
			
			stab = iter == 0 ? figJ[iter] : Math.abs(figJ[iter] - figJ[iter-1]);
			Uprev = Umat;
			Dprev = Dmat;
			iter++;
			////////////////////////////////////////////////////////
		
			// Affichage de l'image segmentée 
			double[] mat_array=new double[nbclasses];
			l = 0;
			for(i=0;i<width;i++) {
			  for(j = 0; j<height; j++) {
				for(k = 0; k<nbclasses; k++){ 
				 mat_array[k]=Umat[k][l];
				}
				int indice= IndiceMaxOfArray(mat_array,nbclasses) ;
				int array[] = new int[3];
				array[0] = (int)c[indice][0];
				array[1] = (int)c[indice][1];
				array[2] = (int)c[indice][2];
				ipseg.putPixel(i, j, array);
				l++;
			  }
			}
			impseg.updateAndDraw();
		}
		
		double[] xplot= new double[itermax];
        double[] yplot=new double[itermax];
		for(int w = 0; w < itermax; w++) {
			xplot[w]=(double)w;	yplot[w]=(double) figJ[w];
		}
		Plot plot = new Plot("Performance Index (HCM)","iterations","J(P) value",xplot,yplot);
		plot.setLineWidth(2);
		plot.setColor(Color.blue);
	    plot.show();
		
	}


	public void FCM(ImageProcessor ip, ImageProcessor ipseg, ImagePlus impseg, 
			double [] red, double[] green, double [] blue, double figJ[],
			double m, int nbclasses, int width, int height, int randomAmeliore, int itermax, double valeur_seuil) {
		
		int imax, jmax, kmax;
		int nbpixels = width * height;
		double Dmat[][] = new double[nbclasses][nbpixels];
		double Dprev[][] = new double[nbclasses][nbpixels];
		double Umat[][] = new double[nbclasses][nbpixels];
		double Uprev[][] = new double[nbclasses][nbpixels];
		double c[][] = new double[nbclasses][3];
		int[] init=new int[3];
		
		imax = nbpixels;  // nombre de pixels dans l'image
		jmax = 3;  // nombre de composantes couleur
		kmax=nbclasses;
		double data[][] = new double[nbclasses][3];
		int[] fixe=new int[3]; 
		int xmin = 0;
	   	int xmax = width;
	   	int ymin = 0;
		int ymax = height;
		int rx, ry;	
		int x,y;
		int epsilonx,epsilony;
		int i, j, k, l;
		int iter;
		double stab,seuil;
	
		// Initialisation des centroides (aléatoirement )
	
		for(i=0;i<nbclasses;i++){
			if(randomAmeliore==1) {
				epsilonx=rand((int)(width/(i+2)),(int)(width/2));
			    epsilony=rand((int)(height/(4)),(int)(height/2));
			} else {
			   epsilonx=0;
			   epsilony=0;
			}
			rx = rand(xmin+epsilonx, xmax-epsilonx);
			ry = rand(ymin+epsilony, ymax-epsilony);
			ip.getPixel(rx,ry,init);
			c[i][0] = init[0]; c[i][1] =init[1]; c[i][2] = init[2];
		}
			
		// Calcul de distance entre data et centroides
		for(l = 0; l < nbpixels; l++)
		{
			for(k = 0; k < kmax; k++)
			{
				double r2 = Math.pow(red[l] - c[k][0], 2);
				double g2 = Math.pow(green[l] - c[k][1], 2);
				double b2 = Math.pow(blue[l] - c[k][2], 2);
				Dprev[k][l] = r2 + g2 + b2;
			}
		}
	
		// Initialisation des degrés d'appartenance
		//A COMPLETER
		for(l = 0; l < nbpixels; l++) {
			for(i = 0; i < kmax; i++) {
				double res = 0.0;
				for(k = 0; k < kmax; k++){
					res += Math.pow((Dprev[i][l] / Dprev[k][l]), 2/(m-1));
				}
				Uprev[i][l] = Double.isNaN(1.0 / res) ? 0.0 : 1.0/res;
			}
		}
	
		////////////////////////////////////////////////////////////
		// FIN INITIALISATION FCM
		///////////////////////////////////////////////////////////
	
	
		/////////////////////////////////////////////////////////////
		// BOUCLE PRINCIPALE
		////////////////////////////////////////////////////////////
		iter = 0;
		stab = 2;
		seuil = valeur_seuil;
		
	
		/////////////////// A COMPLETER ///////////////////////////////
		while ((iter < itermax) && (stab > seuil)) 
		{
	
			
		// Update  the matrix of centroids
		for(k = 0; k < nbclasses; k++){
			double sum[] = {0,0,0};
			double sum2 = 0;
			
			for(l = 0; l < nbpixels; l++) {
				sum[0] += Math.pow(Uprev[k][l], m) * red[l];
				sum[1] += Math.pow(Uprev[k][l], m) * green[l];
				sum[2] += Math.pow(Uprev[k][l], m) * blue[l];
				
				sum2 += Math.pow(Uprev[k][l], m);
			}
			
			c[k][0] = sum[0] / sum2;
			c[k][1] = sum[1] / sum2;
			c[k][2] = sum[2] / sum2;
		}
		// Compute Dmat, the matrix of distances (euclidian) with the centroids
		for(l = 0; l < nbpixels; l++)
		{
			for(k = 0; k < kmax; k++)
			{
				double r2 = Math.pow(red[l] - c[k][0], 2);
				double g2 = Math.pow(green[l] - c[k][1], 2);
				double b2 = Math.pow(blue[l] - c[k][2], 2);
				Dmat[k][l] = r2 + g2 + b2;
			}
		}
		
		for(l = 0; l < nbpixels; l++) {
			for(i = 0; i < kmax; i++) {
				double res = 0.0;
				for(k = 0; k < kmax; k++){
					res += Math.pow((Dmat[i][l] / Dmat[k][l]), 2/(m-1));
				}
				Umat[i][l] = Double.isNaN(1.0 / res) ? 0.0 : 1.0/res;
			}
		}
		
		// Calculate difference between the previous partition and the new partition (performance index)
		for(i = 0; i<nbclasses; i++) {
			for(j = 0; j<nbpixels; j++) {
				figJ[iter] += Math.pow(Uprev[i][j], m) * Dprev[i][j];
			}
		}
		
		stab = iter == 0 ? figJ[iter] : Math.abs(figJ[iter] - figJ[iter-1]);
		Uprev = Umat;
		Dprev = Dmat;
		iter++;
		////////////////////////////////////////////////////////
	
		// Affichage de l'image segmentée 
		double[] mat_array=new double[nbclasses];
		l = 0;
		for(i=0;i<width;i++)
		{
		  for(j = 0; j<height; j++)
		{
			for(k = 0; k<nbclasses; k++)
			{ 
			 mat_array[k]=Umat[k][l];
			}
			int indice= IndiceMaxOfArray(mat_array,nbclasses) ;
			int array[] = new int[3];
			array[0] = (int)c[indice][0];
			array[1] = (int)c[indice][1];
			array[2] = (int)c[indice][2];
			ipseg.putPixel(i, j, array);
			l++;
		}
		}
		impseg.updateAndDraw();
		//////////////////////////////////
		}  // Fin boucle
		
		double[] xplot= new double[itermax];
		                double[] yplot=new double[itermax];
		for(int w = 0; w < itermax; w++)
		{
		xplot[w]=(double)w;	yplot[w]=(double) figJ[w];
		}
		Plot plot = new Plot("Performance Index (FCM)","iterations","J(P) value",xplot,yplot);
		plot.setLineWidth(2);
		    plot.setColor(Color.blue);
		                plot.show();
	}


	//Returns the maximum of the array
	
	public int  IndiceMaxOfArray(double[] array,int val) 
	{
	    max=0;
	    for (int i=0; i<val; i++)
	    {
	       if (array[i]>max) 
	      {max=array[i];
	       indice=i;
	    }
	    }
	return indice;
	}

}


