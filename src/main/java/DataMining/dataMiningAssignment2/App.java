package DataMining.dataMiningAssignment2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.Random;

/**
 * Hello world!
 *
 */
public class App 
{
	public static Vector<Point> Points = new Vector<Point>();
	public static Vector<Cluster> Clusters = new Vector<Cluster>();
	
    public static void main( String[] args ) throws IOException
    {
    	Scanner in = new Scanner(System.in);
    	System.out.print("Enter number of Clusters: ");
    	int numOfClusters = in.nextInt();
    	readFromExcel();
    	k_MeanAlgo(numOfClusters);
    	int index = getMinimumPointsIndex(Clusters);
    	
    	// -*********************** Printing Means for All Clusters *********************-
    	System.out.println("*******************************Clusters + Means**************************** ");
    	for(int i = 0 ; i < Clusters.size() ; i++)
    	{
    		System.out.print("Cluster: " + (i+1) + "  Size: " + Clusters.get(i).Points.size() +"   Mean:  ");
    		for(int j = 0 ; j < Clusters.get(i).Questions.size() ; j++)
    		{
    			System.out.print( Clusters.get(i).Questions.get(j) + " - ");
    		}
    		System.out.println();
    	}
    	
    	// -*********************** Printing Students for All Clusters *********************-
    	System.out.println();
    	System.out.println("**************************Clusters + Students*************************");
    	for(int i = 0 ; i < Clusters.size() ; i++)
    	{
    		if (index == i) {
    			System.out.print("Outlier Cluster => ");
    		}
    		System.out.print("Cluster: " + (i+1) + "  Size: " + Clusters.get(i).Points.size() +"   Students:  ");
    		for(int j = 0 ; j < Clusters.get(i).Points.size() ; j++)
    		{
    			System.out.print( Clusters.get(i).Points.get(j).StudentID + " - ");
    		}
    		System.out.println();
    	}    	
    }
    
    public static void readFromExcel() throws IOException
    {

    	File excelFile = new File("Course Evaluation .xlsx");
        FileInputStream fis = new FileInputStream(excelFile);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIt = sheet.iterator();
        rowIt.next();
        while(rowIt.hasNext())
        {
        	Point tempPoint = new Point();
        	Row row = rowIt.next();
        	Iterator<Cell> cellIterator = row.cellIterator();
        	int count = 0;
        	while(cellIterator.hasNext())
        	{
        		
        		Cell cell = cellIterator.next();
        		String x = cell.toString();
        		
        		if(count == 0)
        		{
        			float intX = Float.parseFloat(x);
        			tempPoint.StudentID = intX;
        			count++;
        		}
        		else
        		{
            		double intX = Float.parseFloat(x);
        			tempPoint.Questions.add(intX);
        		}
        		     		
        	}
        	Points.add(tempPoint);
        }
        workbook.close();
        fis.close();

    }
    public static double EuclideanDistance (Point x, Cluster y)
    {
    	double sum = 0;
    	for(int i = 0 ; i < x.Questions.size() ; i++)
    	{
    		sum+=  ( (x.Questions.get(i) - y.Questions.get(i) ) * (x.Questions.get(i) - y.Questions.get(i)) );
    	}
    	sum = Math.sqrt(sum);
    	return sum;
    }
    public static int getMinimumDistance(Vector<Double> vec)
    {
    	double temp = Collections.min(vec);
    	int index = -1;
    	for (int i = 0 ; i < vec.size() ; i++)
    	{
    		if (vec.get(i) == temp) {
    			index = i;
    			break;
    		}
    	}
    	return index;
    }
    public static void calcNewMean(Cluster x)
    {
    	for(int i = 0 ; i < x.Questions.size() ; i++)
    	{
    		double sum = 0;
    		for(int j = 0 ; j < x.Points.size()  ; j++)
    		{
    			sum+= x.Points.get(j).Questions.get(i);
    		}
    		sum = sum/x.Points.size();
    		x.Questions.set(i,sum);
    	}
    	
    }
    public static boolean checkEquality(Cluster x , Cluster y)
    {
    	boolean check = false;
    	int  counter = 0;
    	for(int i = 0 ; i < x.Points.size() ; i++)
    	{
    		for(int j = 0 ; j < y.Points.size() ; j++)
    		{
    			if(x.Points.get(i).StudentID == y.Points.get(j).StudentID)
    			{
    				counter++;
    			}
    		}
    	}
    	if( counter == x.Points.size()  && counter == y.Points.size())
    	{
    		check = true;
    	}
    	return check;
    }
    
    public static void clearCluster(Vector<Cluster> x)
    {
    	for(int i = 0 ; i < x.size() ; i++)
    	{
    		x.get(i).Points.clear();
    	}
    }
    public static void k_MeanAlgo(int numOfClusters)
    {
    	Vector<Cluster> tempClusters = new Vector<Cluster>();
    	// Choosing random clusters
    	for(int i = 0 ; i < numOfClusters ; i++)
    	{
    		Cluster y = new Cluster();
    		Random random = new Random();
        	int randomNum = random.nextInt(150 - 0) + 0;
        	y.Questions.addAll( Points.get(randomNum).Questions);
        	tempClusters.add(y);
    	}
    	int counter = 0;
    	
    	while(true)
    	{
    		boolean check = true;
	    	for(int i = 0 ; i < Points.size() ; i++)
	    	{
	    		Vector<Double> Distance = new Vector<Double>(); // Distance of that student to all clusters
	    		for(int j = 0 ; j < tempClusters.size() ; j++)
	    		{
	    			Distance.add(EuclideanDistance(Points.get(i), tempClusters.get(j)));
	    		}
	    		int nearestCluster = getMinimumDistance(Distance);
	    		tempClusters.get(nearestCluster).Points.add(Points.get(i));
	    		
	    	}
	    	
	    	//**************************** Calculating new mean ***************************************
	    	for(int i = 0 ; i < tempClusters.size() ; i++)
	    	{
	    		calcNewMean(tempClusters.get(i));
	    	}
	    	// **************************** Comparing ***************************************
	    	if(counter == 0)
	    	{
    			for(int k = 0 ; k < tempClusters.size() ; k++)
    			{
    				Cluster x = new Cluster();
    				x.Points.addAll(tempClusters.get(k).Points);
    				x.Questions.addAll(tempClusters.get(k).Questions);
    				Clusters.add(x);
    			}
    			clearCluster(tempClusters);
	    	}
	    	else if(counter != 0 )
	    	{
		    	for(int i = 0 ; i < numOfClusters ; i++ )
		    	{
			    		check = checkEquality(Clusters.get(i), tempClusters.get(i));
			    		if (check == false)
			    		{
			    			Clusters.clear();
			    			for(int k = 0 ; k < tempClusters.size() ; k++)
			    			{
			    				Cluster x = new Cluster();
			    				x.Points.addAll(tempClusters.get(k).Points);
			    				x.Questions.addAll(tempClusters.get(k).Questions);
			    				Clusters.add(x);
			    			}
			    			clearCluster(tempClusters);
			    			break;
			    		}
			    		if(check == false)
			    		{
			    			break;
			    		}	
		    	}
		    	if(check == true)
		    	{
		    		
		    		System.out.println("K-Mean Algorithm Finished.");
		    		break;
		    	}
    	}
	    	counter++;
    	}
    }
    public static int getMinimumPointsIndex(Vector<Cluster> x)
    {
    	int index = 10000;
    	for(int i = 0 ; i < x.size() ;i++)
    	{
    		if(x.get(i).Points.size() < index && x.get(i).Points.size() != 0 )
    		{
    			index = i;
    		}
    	}
    	return index;
    }
}
