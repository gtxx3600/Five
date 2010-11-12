
public class P {// Represent Point or Tuple
	public int x,y;
	public int l,r;
	public int color;
	int direct;
	public P(int a,int b)
	{
		x = a;
		y = b;
		l = r = 0;
		color = 0;
		direct = -1;
	}
//	public P(int a,int b,int d)
//	{
//		x=a;y=b;direct=d;
//		l=r=color=0;
//	}
	public P(int a,int b,int c,int d)
	{
		x=a;y=b;direct=d;
		l=r=color=0;
	}
	public P setColor(int c)
	{
		color = c;
		return this;
	}
	public P setAvailableLength(int left,int right)
	{
		l = left;
		r = right;
		return this;
	}
	public P setDirect(int d)
	{
		direct = d;
		l=r=0;
		return this;
	}
	public P inc(int num)
	{
		switch(direct)
		{
		case Five_Game.WEST:
			return new P(x-num,y,color,direct);
		case Five_Game.NORTH_WEST:
			return new P(x-num,y-num,color,direct);
		case Five_Game.NORTH:
			return new P(x,y-num,color,direct);
		case Five_Game.NORTH_EAST:
			return new P(x+num,y,color,direct);
		default:
			System.err.println("ERROR in P.inc():direct out of range");
			return new P(-1,-1,0,0);
		}
	}
	public P inc()
	{
		return inc(1);
	}
}
