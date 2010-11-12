
public class Step {
	int coordinate[];
	//int deltaStatus[][];
	public Step(int x,int y)
	{
		this.coordinate=new int[2];
		this.coordinate[0]=x;
		this.coordinate[1]=y;
		//this.deltaStatus = new int[21][21];
	}
	public int getX()
	{
		return coordinate[0];
	}
	public int getY()
	{
		return coordinate[1];
	}
	
}
