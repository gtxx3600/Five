import java.util.*;
public class Five_Game {
	int players[];
	int count;
	public int status[][];
	int row;
	final int DefaultRow = 20;
	final int ChessBlack=2;
	final int ChessWhite=1;
	int first;
	boolean started;
	ArrayDeque<Step> stack;
	int curr_player;
	Five_UI ui;
	public static final int WEST=0;
	public static final int NORTH_WEST=1;
	public static final int NORTH=2;
	public static final int NORTH_EAST=3;
	
	public static final int SCORE_LEVEL_1 = 8;
	boolean enableMouse;
	
	public int[][] getStat()
	{
		return status;
	}
	public Five_Game()
	{
		row=DefaultRow+1;
		started=false;
		curr_player =0;
		first=0;
		players = new int[2];
		status = new int[row][row];
		enableMouse = true;
		count =0;
		stack = new ArrayDeque<Step>();
		ui = new Five_UI(this);
	}
	public void setPlayer(int pos,int fp)
	{
		if(pos>1||pos<0)return;
		players[pos]=fp;
	}
	public void Regret()
	{
		if(stack.size()<2)return;
		for(int i=0;i<2;i++)
		{	
			Step tmp = stack.pop();
			status[tmp.getX()][tmp.getY()] = 0;
		}
		count-=2;
		ui.fb.updateUI();
	}
	public void move(int x,int y)
	{
		
		if ((!inBoard(x,y))||status[x][y] != 0)
			return;
		status[x][y] = curr_player + 1;
		this.stack.push(new Step(x,y));
		ui.fb.updateUI();
		if(checkWin(x,y))
		{
			ui.LTurns.setText("Player"+(1+curr_player)+" WIN!!");
			this.NewGame();
			return;
		}
		
		count++;
		curr_player = 1 - curr_player;
		ui.LTurns.setText("Player"+(1+curr_player)+"'s Turn...");
	}
	public boolean checkWin(int x, int y )
	{
		for(int i =0;i<4;i++)
		{
			for(int j=0;j<5;j++)
			{
				if(checkWin(x,y,i,j))
				{
					return true;
				}
			}
		}
		return false;
	}
	public boolean checkWin(int x,int y ,int direct,int offset)
	{
		int color = status[x][y];
		switch(direct)
		{
		case WEST:
			if(!(inBoard(x-offset,y)&&inBoard(x-offset+4,y)))
			{
				return false;
			}
			
			for(int i=0;i<5;i++)
			{
				if(status[x-offset+i][y]!=color)
				{
					return false;
				}
			}
			break;
		case NORTH_WEST:
			if(!(inBoard(x-offset,y-offset)&&inBoard(x-offset+4,y-offset+4)))
			{
				return false;
			}
			for(int i=0;i<5;i++)
			{
				if(status[x-offset+i][y-offset+i]!=color)
				{
					return false;
				}
			}
			break;
		case NORTH:
			if(!(inBoard(x,y-offset)&&inBoard(x,y-offset+4)))
			{
				return false;
			}
			for(int i=0;i<5;i++)
			{
				if(status[x][y-offset+i]!=color)
				{
					return false;
				}
			}
			break;
		case NORTH_EAST:
			if(!(inBoard(x+offset,y-offset)&&inBoard(x+offset-4,y-offset+4)))
			{
				return false;
			}
			for(int i=0;i<5;i++)
			{
				if(status[x+offset-i][y-offset+i]!=color)
				{
					return false;
				}
			}
			break;
		default:
			System.err.println("Error !");
			return false;
			
		}
		return true;
		
		
	}
	public void StartGame()
	{
		curr_player = first;
		status = new int[21][21];
		started = true;
		count=0;
		ui.fb.updateUI();
	}
	public void NewGame()
	{
		//curr_player = first;
		ui.initStatus();
		started = false;
		
	}
	public P getAvailableLength(P p,int direct)
	{
		p.setDirect(direct);
		int i1=0,i2=0;
		P tmp;
		tmp = p.inc();
		for(;i1<4;i1++)
		{
			if(!this.inBoard(tmp)||((status[tmp.x][tmp.y]!=0)&&!(status[tmp.x][tmp.y]==p.color)))
			{
				break;
			}
			tmp = tmp.inc();
		}
		tmp = p.inc(-1);
		for(;i1<4;i1++)
		{
			if(!this.inBoard(tmp)||((status[tmp.x][tmp.y]!=0)&&!(status[tmp.x][tmp.y]==p.color)))
			{
				break;
			}
			tmp = tmp.inc(-1);
		}
		return p.setAvailableLength(i1,i2);
	}
	public P getAvailableLength(P p)
	{
		return getAvailableLength(p,p.direct);
	}
	public int getScore(P p)
	{
		int s=0;
		s+=getScoreLv1(p);
		s+=getScoreLv2(p);
		return s;
	}
	public int getScoreLv2(P p)
	{
		int s=0;
		return s;
	}
	public int getScoreLv1(P p)
	{
		int s=0;
		for(int i=0;i<4;i++)
		{
			this.getAvailableLength(p.setDirect(i));
			s+=this.getLineScoreLv1(p);
		}
		return s;
	}
	public int getLineScoreLv2(P p)
	{
		int s= 0;
		return s;
	}
	public int getLineScoreLv1(P p)
	{
		int s = 0;
		P tmp = p.inc();
		for(int i=1;i<=p.l;i++)
		{
			if(status[tmp.x][tmp.y] == p.color)
			{
				s+= (int)(1<<(4-i));
			}
			tmp = tmp.inc();
		}
		tmp = p.inc(-1);
		for(int i=1;i<=p.r;i++)
		{
			if(status[tmp.x][tmp.y] == p.color)
			{
				s+= (int)(1<<(4-i));
			}
			tmp = tmp.inc(-1);
		}
		return s;
	}
	public boolean inBoard(P p)
	{
		return (p.x>=0&&p.x<21)&&(p.y>=0&&p.y<21);
	}
	public boolean inBoard(int x,int y)
	{
		return (x>=0&&x<21)&&(y>=0&&y<21);
	}
	public boolean isAvailable(P p)
	{
		return inBoard(p)&&(status[p.x][p.y]==0);
	}
	public boolean isAvailable(int x,int y)
	{
		return inBoard(x,y)&&(status[x][y]==0);
	}
	public void dict()
	{
		int size = 1<<9;
		int dict[][] = new int[5][size];
		for(int i=0;i<5;i++)
		{
			for(int j=0;j<size;j++)
			{
				
			}
		}
		BitSet bs = new BitSet(5);
		for()
		
	}
	public void writeDict(int mode[],int score,int availableLength)
	{
		
	}
	public int getBSValue(BitSet b)
	{
		int ret=0;
		for(int i = b.nextSetBit(0);i>=0;i=b.nextSetBit(i))
		{
			ret+=1<<i;
		}
		return ret;
	}
	public BitSet getBitSet(int i)
	{
		if(i<0)return null;
		BitSet b = new BitSet();
		int count=0;
		while(i>0)
		{
			if(i%2==1)
			{
				b.set(count);
			}
			i>>>=1;
			count++;
		}
		return b;
	}
}
