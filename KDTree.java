//Name: Moyang Chen
//Student ID: 260787220

import java.util.ArrayList;
import java.util.Iterator;
public class KDTree implements Iterable<Datum>{ 

	KDNode 		rootNode;
	int    		k; 
	int			numLeaves;
	
	// constructor

	public KDTree(ArrayList<Datum> datalist) throws Exception {

		Datum[]  dataListArray  = new Datum[ datalist.size() ]; 

		if (datalist.size() == 0) {
			throw new Exception("Trying to create a KD tree with no data");
		}
		else
			this.k = datalist.get(0).x.length;

		int ct=0;
		for (Datum d :  datalist) {
			dataListArray[ct] = datalist.get(ct);
			ct++;
		}
		
	//   Construct a KDNode that is the root node of the KDTree.

		rootNode = new KDNode(dataListArray);
		
		
	}
	
	//   KDTree methods
	
	public Datum nearestPoint(Datum queryPoint) {
		try {
			return rootNode.nearestPointInNode(queryPoint);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	//count leaves 
	
	

	public int height() {
		return this.rootNode.height();	
	}

	public int countNodes() {
		return this.rootNode.countNodes();	
	}
	
	public int size() {
		return this.numLeaves;	
	}

	//-------------------  helper methods for KDTree   ------------------------------

	public static long distSquared(Datum d1, Datum d2) {

		long result = 0;
		for (int dim = 0; dim < d1.x.length; dim++) {
			result +=  Math.pow(d1.x[dim] - d2.x[dim], 2);
		}
		// if the Datum coordinate values are large then we can easily exceed the limit of 'int'.
		return result;
	}

	public double meanDepth(){
		int[] sumdepths_numLeaves =  this.rootNode.sumDepths_numLeaves();
		return 1.0 * sumdepths_numLeaves[0] / sumdepths_numLeaves[1];
	}
	
	class KDNode { 

		boolean leaf;
		Datum leafDatum;           //  only stores Datum if this is a leaf
		
		//  the next two variables are only defined if node is not a leaf

		int splitDim;      // the dimension we will split on
		int splitValue;    // datum is in low if value in splitDim <= splitValue, and high if value in splitDim > splitValue  

		KDNode lowChild, highChild;   //  the low and high child of a particular node (null if leaf)
		  //  You may think of them as "left" and "right" instead of "low" and "high", respectively

		KDNode(Datum[] datalist) throws Exception{

			/*
			 *  This method takes in an array of Datum and returns 
			 *  the calling KDNode object as the root of a sub-tree containing  
			 *  the above fields.
			 */

			//   ADD YOUR CODE BELOW HERE			

			//check whether the node is a leaf by checking how many data points are in
			//the Datum provided
			
			if(datalist.length==0){
				throw new Exception("the length of the datalist can't be 0");
			}
			
			if(datalist.length==1) {
				
				
				this.leaf=true;
				this.leafDatum=datalist[0];
				numLeaves ++;
				
			}else {
				//we know that this is not the leaf
				
				this.leaf=false;
				
				//find out how many dimension are there and subtract them
				
				
				
				
				int num =datalist.length;
				int dim = datalist[0].x.length;
				int[] bigger =datalist[0].x.clone();
				int[] smaller =datalist[0].x.clone();
				
				for(int i=1;i<datalist.length;i++) {
					for(int j=0;j<dim;j++) {
						
						if(datalist[i].x[j]>bigger[j]) {
							bigger[j]=datalist[i].x[j];
						}
						
						if(datalist[i].x[j]<smaller[j]) {
							smaller[j]=datalist[i].x[j];
						}
						
					}
				}
				
				
				//add the bigger and smaller together to determine which one is bigger
				
				int[] sum = new int[dim];
				
				for(int i=0;i<dim;i++) {
					
					sum[i]=bigger[i]-smaller[i];
				}
				
				//determine which dimension we should iterate from by comparing the 
				//sum
				
				int biggest = sum[0];
				int whichInSum= 0;
				
				for(int i=1; i<dim;i++) {
					
					  if(sum[i]>biggest) {
					    	biggest=sum[i];
						    whichInSum = i;
					}
					
				}
				if(biggest==0) {
					this.leaf=true;
					this.leafDatum=datalist[0];
					numLeaves++;
					
				}else {
				this.splitDim=whichInSum;
				//get the average value of the dimension with the biggest range
				
				
				
				this.splitValue=Math.floorDiv((bigger[splitDim]+smaller[splitDim]),2);
				
				
				
				
				
				//create two empty Datum array to put values in
				
				ArrayList<Datum> lower = new ArrayList<Datum>();
				int lo=0;
				
				ArrayList<Datum> higher = new ArrayList<Datum>();
				int hi = 0;
				
				
				
				for(int i=0;i<num;i++) {
					int temp = datalist[i].x[splitDim];
					
					//check whether it is bigger or smaller than splitValue to 
					//determine if we should put it in higher or lower
					
					if(temp<=splitValue) {
						
						lower.add(datalist[i]);
						lo++;
						
					}else {
						
						higher.add(datalist[i]);
						hi++;
						
					}
				}
				
				//create a new array with the correct size
				
			
				lowChild= new KDNode(lower.toArray(new Datum[lo]));
				highChild= new KDNode(higher.toArray(new Datum[hi]));
				
			}
			}
			
			//   ADD YOUR CODE ABOVE HERE

		}

		public Datum nearestPointInNode(Datum queryPoint) throws Exception {
			Datum nearestPoint, nearestPoint_otherSide;
		
			//   ADD YOUR CODE BELOW HERE
			
			//1.assume the current KDNode is the root, so compare the 
			//queryPoint's splitDimension to determine whether to go into the higher child or the lower child

			
			KDNode cur = this;
			ArrayList<KDNode> parent = new ArrayList<KDNode>();
			int count=0;
			
			try{
				while(!cur.leaf) {
			
				int comp = queryPoint.x[cur.splitDim];
				
				if(comp<=cur.splitValue) {
					parent.add(cur);
					count++;
					
					cur=cur.lowChild;
					
				}else {
					parent.add(cur);
					count++;
					
					cur=cur.highChild;
					
				}
				
			
			}
			}catch (Exception e){
				System.out.println("The dimension might not be right");
				e.printStackTrace();
			}

			
			
			
			nearestPoint=cur.leafDatum;
			long pointF = KDTree.distSquared(nearestPoint, queryPoint);
			
			
			//use recursive method to see if theres any point closer to the query point 
			//than the point we already found
			
			//only go try find the recursive method if the plane value is smaller
			//compare to the plane we already found
			
			ArrayList<Datum> smaller = new ArrayList<Datum>();
			
			
			for(int i=count-1;i>=0;i--) {
				KDNode temp = parent.get(i);
				
				Datum plane = temp.createPlane(temp, queryPoint);
				
				long planeD = KDTree.distSquared(plane, queryPoint);
				
				if(planeD<pointF) {
					
					int comp = queryPoint.x[temp.splitDim];
					
					if(comp<=temp.splitValue) {
						smaller.add(temp.highChild.nearestPointInNode(queryPoint));
					}else {
						smaller.add(temp.lowChild.nearestPointInNode(queryPoint));
					}
				}
			}
			
			//compare all the small points inside the array and return the smallest point
			
			if(!smaller.isEmpty()) {
				for(int i=0; i<smaller.size();i++) {
					long SDis= KDTree.distSquared(smaller.get(i), queryPoint);
					
					if(SDis<pointF) {
						pointF=SDis;
						
						nearestPoint=smaller.get(i);
						
					}
					
				}
			}
			
			return nearestPoint;
			
			
		
			
			
			
			//   ADD YOUR CODE ABOVE HERE

		}
		
		// -----------------  KDNode helper methods (might be useful for debugging) -------------------

		public int height() {
			if (this.leaf) 	
				return 0;
			else {
				return 1 + Math.max( this.lowChild.height(), this.highChild.height());
			}
		}

		public int countNodes() {
			if (this.leaf)
				return 1;
			else
				return 1 + this.lowChild.countNodes() + this.highChild.countNodes();
		}
		
		/*  
		 * Returns a 2D array of ints.  The first element is the sum of the depths of leaves
		 * of the subtree rooted at this KDNode.   The second element is the number of leaves
		 * this subtree.    Hence,  I call the variables  sumDepth_size_*  where sumDepth refers
		 * to element 0 and size refers to element 1.
		 */
				
		
	
		public Datum createPlane(KDNode parent, Datum query) {
			int split=parent.splitDim;
			int[] ret = new int[query.x.length];
			
			for(int i=0;i<query.x.length; i++) {
				if(i==split) {
					ret[i]=parent.splitValue;
				}else {
					ret[i]=query.x[i];
				}
				
				
			}
			
			Datum retu = new Datum(ret);
			return retu;
			
		}
		
		
		public int[] sumDepths_numLeaves(){
			int[] sumDepths_numLeaves_low, sumDepths_numLeaves_high;
			int[] return_sumDepths_numLeaves = new int[2];
			
			/*     
			 *  The sum of the depths of the leaves is the sum of the depth of the leaves of the subtrees, 
			 *  plus the number of leaves (size) since each leaf defines a path and the depth of each leaf 
			 *  is one greater than the depth of each leaf in the subtree.
			 */
			
			if (this.leaf) {  // base case
				return_sumDepths_numLeaves[0] = 0;
				return_sumDepths_numLeaves[1] = 1;
			}
			else {
				sumDepths_numLeaves_low  = this.lowChild.sumDepths_numLeaves();
				sumDepths_numLeaves_high = this.highChild.sumDepths_numLeaves();
				return_sumDepths_numLeaves[0] = sumDepths_numLeaves_low[0] + sumDepths_numLeaves_high[0] + sumDepths_numLeaves_low[1] + sumDepths_numLeaves_high[1];
				return_sumDepths_numLeaves[1] = sumDepths_numLeaves_low[1] + sumDepths_numLeaves_high[1];
			}	
			return return_sumDepths_numLeaves;
		}
		
	}

	public Iterator<Datum> iterator() {
		return new KDTreeIterator(this);
	}
	
	public class KDTreeIterator implements Iterator<Datum> {

		int current;
		int sum; //represent how many KDNodes are there in the points we already go through
		ArrayList<Datum> points=new ArrayList<Datum>();
		
		
		public KDTreeIterator(KDTree tree) {
			
			//when initialize the iterator
			//trying to get the current point to the bottom left of the tree
			sum=0;
			current =0;
			
			ArrayList<KDNode> nodes = new ArrayList<KDNode>();
			
			KDNode root = tree.rootNode;
			nodes.add(root);
			
			while(!root.lowChild.leaf) {
				root=root.lowChild;
				nodes.add(root);
				
			}
			
			points.add(root.lowChild.leafDatum);
			sum++;
			
			while(sum<tree.size()) {
				
				if(root.highChild!=null) {
					if(root.highChild.leaf) {
						
						points.add(root.highChild.leafDatum);
						sum++;
						
						int temp=nodes.size();
						if(temp>=2) {
						//remove the last node inside the arraylist and jump to the second last arraylist
						root=nodes.get(temp-2);
						nodes.remove(temp-1);
						}
					}else {
						root=root.highChild;
						//remove this node
						
						int temp = nodes.size();
						nodes.remove(temp-1);
						
						nodes.add(root);
						
						while(!root.lowChild.leaf) {
							root=root.lowChild;
							
							nodes.add(root);
						}
						
						points.add(root.lowChild.leafDatum);
						sum++;
						
					}
				}
				
				}
			
			}
			
		
		
		
		//   ADD YOUR CODE BELOW HERE
		@Override
		public boolean hasNext() {
			//check whether this is a leaf
			if (current==sum) {
				return false;
			}
			return true;
			}


		
		

		@Override
		public Datum next() {
			
			if(!hasNext()) {
				return null;
			}else {
				if(current>=sum) {
					System.out.println("Something went wrong");
					return null;
				}else {
					Datum temp = points.get(current);
					current++;
					return temp;
				}
			}
				
			
			
		
			
		
		//   ADD YOUR CODE ABOVE HERE

	}
	
	}
}
	


