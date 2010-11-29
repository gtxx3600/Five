import java.util.*;
import java.lang.*;
public class Five_Game {
	int players[];
	int count;
	boolean debug;
	int depth = 1;
	public int status[][];
	public int scores[][][];
	public Score scores_type[][][];
	int row;
	final int DefaultRow = 20;
	final int ChessBlack=2;
	final int ChessWhite=1;
	int first;
	AI ai;
	boolean started;
	ArrayDeque<P> stack;  //
	int curr_player;
	Five_UI ui;
	int level;
	public static final int WEST=0;
	public static final int NORTH_WEST=1;
	public static final int NORTH=2;
	public static final int NORTH_EAST=3;
	public static final int SCORE_LEVEL_1 = 8;
	public static final int WIN_FLAG = 100000;
	public static final int WARN_FLAG = 2000;

	int dict[][] = new int[5][1<<9];
	int subdict[][] = new int[5][1<<9];
	
	int mode[][] = {{1,1},   			//0
					{1,0,1},			//1
					{1,0,0,1},			//2
					{1,0,1,0,1},		//3
					
					{1,1,0,1},			//4
					{1,1,1},			//5
					{1,0,1,1},			//6
					
					{1,1,1,1},			//7
					
					{1,1,0,1,1},		//8
					{1,0,1,1,1},		//9
					{1,1,1,0,1},		//10
					
					{1,1,1,1,1}			//11
					};
	int modeScore[] = {
					100,
					75,
					50,
					200,
					750,
					1000,
					750,
					5000,
					750,
					1100,
					1100,
					100000
	};
	int modePunish[] = {
			75,
			50,
			25,
			0,
			550,
			800,
			550,
			4100,
			0,
			350,
			350,
			0
};
	public int[][] getStat()
	{
		return status;
	}
	public P getLastStep()
	{
		if(stack.size()>0)
			return stack.getFirst();
		else
			return null;
	}
	public int[][][] getScore()
	{
		return scores;
	}
	public Five_Game()
	{

		row=DefaultRow+1;
		started=false;
		curr_player =0;
		first=0;
		debug = false;
		players = new int[2];
		status = new int[row][row];
		scores = new int [2][row][row];
		scores_type = new Score [2][row][row];

		count =0;
		level = 0;
		stack = new ArrayDeque<P>();
		dict();

		ui = new Five_UI(this);
		
	}
	public void setDiff(int d)
	{
		depth = d*2;
		if(depth  == 0) depth = 1;
	}
	public void setMode(int mod)
	{
		this.debug = mod == 1?true:false;
	}
	public void Regret()
	{
		practiseRollbackMove();
		practiseRollbackMove();
		confirmRollbackMove();
	}
	public void practiseRollbackMove()
	{
		if(stack.size()<1)return;
		P tmp = stack.pop();
		status[tmp.x][tmp.y] = 0;
		refreshScore(tmp);
		count -= 1;
		curr_player = 1 - curr_player;
	}
	public void confirmRollbackMove()
	{
		ui.LTurns.setText("Player"+(1+curr_player)+"'s Turn...");
		ui.fb.updateUI();
	}
	
	
	public P selectPointToGo(int pn)
	{
		if(count < 5){
			int opp = 1 - pn;
			ArrayList<P> a[] = new ArrayList[3];
			a[0] = new ArrayList<P>();
			a[1] = new ArrayList<P>();
			a[2] = new ArrayList<P>();
			for(int i=0;i<this.row;i++)
			{
				for(int j=0;j<this.row;j++)
				{
					a[0].add(new P(i,j).setScore(scores[0][i][j]));
					a[1].add(new P(i,j).setScore(scores[1][i][j]));
					a[2].add(new P(i,j).setScore(scores[1][i][j]+scores[0][i][j]));
				}
			}
			Collections.sort(a[0]);
			Collections.sort(a[1]);
			Collections.sort(a[2]);
			if(a[pn].get(0).score >= WIN_FLAG)
			{
				return a[pn].get(0);
			}
			if(a[opp].get(0).score >= WIN_FLAG)
			{
				return a[opp].get(0);
			}
			if(a[opp].get(0).score >= WARN_FLAG)
			{
				return a[opp].get(0);
			}else if(a[pn].get(0).score >= 150)
			{
				return a[pn].get(0);
			}
			System.out.println("NOT EMERGENCY SELECT MAX_SCORE_SUM");
			return a[2].get(0);
		}
		if(pn == 0)
		{
			int tmp[] = getMax(Integer.MIN_VALUE,Integer.MAX_VALUE,this.depth);
			return new P(tmp[1],tmp[2]);
			
		}else if(pn == 1)
		{
			int tmp[] = getMin(Integer.MIN_VALUE,Integer.MAX_VALUE,this.depth);
			return new P(tmp[1],tmp[2]);
		}else
		{
			System.err.println("Unknown Player Number :" + pn);
			return new P(0,0);
		}
		
	}
	public void move(int x,int y)
	{
		if(ai != null && ai.isAlive())return;
		this.practiseMove(x, y);
		this.confirmMove(x, y);
//		try{
//			Thread.sleep(1000);
//		}catch(Exception e)
//		{
//			e.printStackTrace();
//		}
		this.AIHelp();
	}
	public void practiseMove(int x,int y)
	{
		if ((!started)||(!inBoard(x,y))||status[x][y] != 0)
			return;
		status[x][y] = curr_player + 1;
		P step = new P(x,y,curr_player+1);
		this.stack.push(step);
		this.refreshScore(step);
		count++;
		curr_player = 1 - curr_player;
	}
	public void confirmMove(int x,int y)
	{
		if(checkWin(x,y))
		{
			ui.LTurns.setText(((1-curr_player) == 0 ? "White" : "Black")+" WIN!!");
			ui.fb.updateUI();
			this.started = false;
			this.NewGame();	
			return;
		}
		ui.LTurns.setText((curr_player == 0 ? "White":"Black")+"'s Turn");

		ui.fb.updateUI();
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
		curr_player = 1;
		status = new int[21][21];
		scores = new int [2][row][row];
		scores_type = new Score[2][row][row];
		scores[0][10][10] = 100;
		scores[1][10][10] = 100;
		scores_type[0][10][10] = new Score(100);
		scores_type[1][10][10] = new Score(100);
		started = true;
		stack.clear();
		count=0;
		ui.fb.updateUI();
		if(first == 0)
		{
			this.AIHelp();
		}
	}
	public void AIHelp()
	{
		if(!started)return;
		if(ai != null && ai.isAlive())return;
		ai  = new AI(this);
		ai.start();
	}
	public void AIGo()
	{
		if(!started)return;
		if(count > 16)
		{
			count = count + 0;
		}
		P next = this.selectPointToGo(curr_player);
		System.out.println("AI select "+next.x+","+next.y +" for player"+curr_player);
		this.practiseMove(next.x, next.y);
		this.confirmMove(next.x, next.y);
	}
	public void refreshScore(P pp)
	{
		P p = new P(pp);
		for(int k=0;k<2;k++)
		{
			p.setColor(k+1);
			if(this.isAvailable(p))
			{
				scores[k][p.x][p.y] = getScore(p);
			}else{
				scores_type[k][p.x][p.y] = null; 
				scores[k][p.x][p.y] = 0;
			}
			for( int i=0;i<4;i++)
			{
				p.setDirect(i);
				for( int j=1;j<5;j++)
				{
					P tmp = p.inc(j);
					if(this.isAvailable(tmp))
					{
						scores[k][tmp.x][tmp.y]=getScore(tmp);
					}
				}
				for( int j=1;j<5;j++)
				{
					P tmp = p.inc(-j);
					if(this.isAvailable(tmp))
					{
						scores[k][tmp.x][tmp.y]=getScore(tmp);
					}
				}
			}
		}
	}
	public void NewGame()
	{
		//curr_player = first;
		ui.initStatus();
		if(ai != null && ai.isAlive())
		{
			ai.stop();
		}
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
		for(;i2<4;i2++)
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
		scores_type[p.color - 1][p.x][p.y] = new Score();
		s+=getScoreLv1(p);
		s+=getScoreLv2(p);
		return s;
	}
	public int getScoreLv2(P p)
	{
		int s=0;
		for(int i=0;i<4;i++)
		{
			this.getAvailableLength(p.setDirect(i));
			this.getLineScoreLv2(p);
		}
		s = scores_type[p.color-1][p.x][p.y].score;
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
	public void getLineScoreLv2(P p)
	{
		int len = p.l+p.r+1;
		if(len<5)return ;
		int s=0;
		int bs[] = new int[len];
		int count = 0;
		for( int i=p.l;i>0;i--)
		{
			P tmp = p.inc(i);
			if(status[tmp.x][tmp.y]==p.color)
			{
				bs[count++]=1;
			}else
			{
				bs[count++]=0;
			}
		}
		bs[count++]=1;
		for( int i=1;i<=p.r;i++)
		{
			P tmp = p.inc(-i);
			if(status[tmp.x][tmp.y]==p.color)
			{
				bs[count++]=1;
			}else
			{
				bs[count++]=0;
			}
		}
		int n= getBSValue(bs);
		scores_type[p.color-1][p.x][p.y].addType(dict[len-5][n]-1);
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
				break;
			}
			tmp = tmp.inc();
		}
		tmp = p.inc(-1);
		for(int i=1;i<=p.r;i++)
		{
			if(status[tmp.x][tmp.y] == p.color)
			{
				s+= (int)(1<<(4-i));
				break;
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
	public String getBSofValue(int BS,int length)
	{
		String t = "";
		for(int k=0;k<length+5;k++)
		{
			if((BS&(1<<k))!=0)
			{
				t+="1";
			}else
			{
				t+="0";
			}
		}
		return t;
	}
	public void printDict()
	{
		for(int i=0;i<5;i++)
		{
			int l = 5+i;
			for(int j=0;j<dict[i].length;j++)
			{
				if(dict[i][j]!=0)
				{
					String t = "";
					for(int k=0;k<i+5;k++)
					{
						if((j&(1<<k))!=0)
						{
							t+="1";
						}else
						{
							t+="0";
						}
					}
					t+=" : "+dict[i][j];
					System.out.println(t);
				}
			}
		}
	}
	public void dict()
	{
		for(int i=5;i<10;i++)
		{
			for(int j=0;j<mode.length;j++)
			{
				writeDict(j,i);
			}
		}
	}

	public void writeDict(int modeNum,int dictNum)
	{
		for(int i=0;i<=dictNum-mode[modeNum].length;i++)
		{
			int curr_array[] = new int[dictNum];
			writeDict(modeNum,curr_array,dictNum,i,0);
		}
	}
	public void writeDict(int modeNum,int curr_array[],int dictNum,int pos,int curr)
	{
		int d[] = dict[dictNum-5];
		int sd[] = subdict[dictNum - 5];
		if(curr >= dictNum)
		{
			int n = getBSValue(curr_array);
			int score = modeNum+1;
			
			if(pos==0||pos+mode[modeNum].length==dictNum)
			{
				score += 12;
			}else if(modeNum >=8 && modeNum <= 10)
			{
				score += 12;
			}
			if(sd[n] == 0 || sd[n] < getBSCount(mode[modeNum]))
			{
				sd[n] = getBSCount(mode[modeNum]);
				d[n] = score;
			}
			return;
		}
		if(curr == pos)
		{
			for(int i=0;i<mode[modeNum].length;i++)
			{
				curr_array[curr+i] = mode[modeNum][i];
			}
			curr = pos+mode[modeNum].length;
			writeDict(modeNum,curr_array,dictNum,pos,curr);
			return;
		}
		curr_array[curr] = 0;
		writeDict(modeNum,curr_array,dictNum,pos,curr+1);
		curr_array[curr] = 1;
		writeDict(modeNum,curr_array,dictNum,pos,curr+1);
	}
	public int getBSValue(int b[])
	{
		int ret=0;
		for(int i=0;i<b.length;i++)	{
			if(b[i]==1)
			{
				ret+=1<<i;
			}
		}
		return ret;
	}	
	public int getBSCount(int b[])
	{
		int ret=0;
		for(int i=0;i<b.length;i++)	{
			if(b[i]==1)
			{
				ret+=1;
			}
		}
		return ret;
	}	
	public int getBitCount(int n)
	{
		int ret=0;
		while(n != 0)
		{
			ret += n&1;
			n >>>= 1;
		}
		return ret;
	}
	/**
	 * evaluate the whole chess-board
	 * @return a score to evaluate the situation
	 */
	public int evaluation()
	{
		int coeff[] = {1,10,100,1000,10000,20000,40000,80000,200000,1000000};
		int array[][] = new int[2][10];
		int sum_player[] = new int[2];
		int d[][] = {
				{12,13,14},
				{0,1,2},
				{3,15,16,17,18},
				{4,5,6},
				{8,9,10,19,20,21,22},
		};
		
		for(int i=0;i<row;i++)
		{
			for(int j=0;j<row;j++)
			{
				for(int k=0;k<2;k++){
					if(scores_type[k][i][j] != null){
						Score s = scores_type[k][i][j];
						sum_player[k] += s.score;
						int tmp[] = array[k];
						for(int l = 0; l < d.length; l++)
						{
							tmp[l] += s.countTypeInRange(d[l]);
						}
						
						tmp[5] += s.countTypeInRange(Score.l3) >= 2 ? 1 : 0; 
						tmp[6] += s.hasTypeInRange(Score.l3) && s.hasTypeInRange(Score.s4) ? 1 : 0;
						tmp[7] += s.countTypeInRange(Score.s4) >= 2 ? 1 : 0;
						tmp[8] += s.countTypeInRange(Score.l4);
						tmp[9] += s.countTypeInRange(Score.win);
					}
				}
			}
		}
		int pn = curr_player;
		int opp = 1 - pn;
		for(int i = 0; i < coeff.length; i ++)
		{
			sum_player[pn] += array[pn][i]*coeff[i]*2;
			sum_player[opp] += array[opp][i]*coeff[i];
		}
		return sum_player[0] - sum_player[1];
	}
	/**
	 * 
	 * @param pn (PlayerNum)
	 * @return possible-move list
	 */ 
	public ArrayList<P> getPossibleMove(int pn)
	{
		int opp = 1 - pn;
		ArrayList<P> a[] = new ArrayList[3];
		a[0] = new ArrayList<P>();
		a[1] = new ArrayList<P>();
		a[2] = new ArrayList<P>();
		ArrayList<P> ret = new ArrayList<P>();
		for(int i=0;i<this.row;i++)
		{
			for(int j=0;j<this.row;j++)
			{
				if(scores_type[0][i][j]!=null || scores_type[1][i][j]!=null)
				{
					a[0].add(new P(i,j).setScoreType(scores_type[0][i][j]));
					a[1].add(new P(i,j).setScoreType(scores_type[1][i][j]));
					a[2].add(new P(i,j).setScore(scores[1][i][j]+scores[0][i][j]));
				}
			}
		}
		Collections.sort(a[0]);
		Collections.sort(a[1]);
		Collections.sort(a[2]);
		
		if(a[pn].get(0).score_type.hasTypeInRange(Score.win))
		{
			ret.add(a[pn].get(0));
			return ret;
		}
		if(a[opp].get(0).score_type.hasTypeInRange(Score.win))
		{
			ret.add(a[opp].get(0));
			return ret;
		}
		boolean win_flag = false;
		for(int i = 0; i < a[pn].size();i++)
		{
			P tmp = a[pn].get(i);
			if(tmp.score_type!=null)
			{
				Score st = tmp.score_type;
				if (st.countTypeInRange(Score.l4) >= 1)
				{
					ret.add(tmp);
					win_flag = true;
				}else if(st.countTypeInRange(Score.s4) >= 2 || (st.hasTypeInRange(Score.s4)&&st.hasTypeInRange(Score.l3)))
				{
					ret.add(tmp);
					win_flag = true;
				}
			}
		}
		if(win_flag)return ret;
		if(level == 3){
			boolean l4_flag = false;
			boolean s4_l3_flag = false;
			for(int i = 0; i < a[opp].size();i++)
			{
				P tmp = a[opp].get(i);
				if(tmp.score_type!=null)
				{
					Score st = tmp.score_type;
					if (st.countTypeInRange(Score.l4) >= 1)
					{
						ret.add(tmp);
						l4_flag = true;
					}else if(st.countTypeInRange(Score.s4) >= 2 || (st.hasTypeInRange(Score.s4)&&st.hasTypeInRange(Score.l3)))
					{
						ret.add(tmp);
						s4_l3_flag = true;
					}
				}
			}
			if(l4_flag || s4_l3_flag)
			{
				for(int i = 0; i < a[pn].size();i++)
				{
					P tmp = a[pn].get(i);
					if(tmp.score_type!=null)
					{
						Score st = tmp.score_type;
						if (st.countTypeInRange(Score.s4) >= 1)
						{
							ret.add(tmp);
						}
					}
				}
				return ret;
			}
		}
		boolean lose_flag = false;

			
		for(int i = 0; i < a[opp].size();i++)
		{
			P tmp = a[opp].get(i);
			if(tmp.score_type!=null)
			{
				Score st = tmp.score_type;
				if (st.countTypeInRange(Score.l4) >= 1)
				{
					ret.add(tmp);
					lose_flag = true;
				}else if(st.countTypeInRange(Score.s4) >= 2 || (st.hasTypeInRange(Score.s4)&&st.hasTypeInRange(Score.l3)))
				{
					ret.add(tmp);
					lose_flag = true;
				}
			}
		}
		if(lose_flag)return ret;

		for(int i = 0; i < a[pn].size();i++)
		{
			P tmp = a[pn].get(i);
			if(tmp.score_type!=null)
			{
				Score st = tmp.score_type;
				if (st.countTypeInRange(Score.l3) > 1)
				{
					ret.add(tmp);
				}
			}
		}
		
		for(int i = 0; i < a[opp].size();i++)
		{
			P tmp = a[opp].get(i);
			if(tmp.score_type!=null)
			{
				Score st = tmp.score_type;
				if (st.countTypeInRange(Score.l3) > 1)
				{
					ret.add(tmp);
					lose_flag = true;
				}
			}
		}
		if(lose_flag)return ret;

		int i =0;
		while(a[2].size()>0 && i<(30 - depth*depth/2) && (a[2].get(0).score > (1000 - depth*100)))
		{
			ret.add(a[2].get(0));
			a[2].remove(0);
			i++;
		}
		while(ret.size()<10)
		{
			if(a[2].size()<1)break;
			ret.add(a[2].get(0));
			a[2].remove(0);
		}
		if(depth == 6)
		{
			while(a[2].size()>0 && a[2].get(0).score > 500)
			{
				ret.add(a[2].get(0));
				a[2].remove(0);
			}
		}
		Collections.sort(ret);
		return ret;
	}
	public int[] getMax(int alpha,int beta,int depth)
	{

		int ret[] = {alpha,-1,-1};
		if(depth == 0)
		{
			ret[0] = this.evaluation();
			return ret;
		}
		ArrayList<P> possibleMoves = this.getPossibleMove(0);
		if(possibleMoves.size() == 0)
		{
			return ret;
		}
		for(int i=0 ;i < possibleMoves.size(); i++)
		{

			P pm = possibleMoves.get(i);
			if(ret[1] == -1)
			{
				ret[1] = pm.x;
				ret[2] = pm.y;
			}
			this.practiseMove(pm.x, pm.y);    //pretending go
			if(this.checkWin(pm.x, pm.y))
			{
				ret[0] = Integer.MAX_VALUE;
				ret[1] = pm.x;
				ret[2] = pm.y;
				this.practiseRollbackMove();
				return ret;
			}
			int[] tmp = this.getMin(alpha,beta,depth-1);
			this.practiseRollbackMove();      //rollback
			
			if(tmp[0] > ret[0]){
				ret[0] = tmp[0];
				ret[1] = pm.x;
				ret[2] = pm.y;
			}
			if(ret[0] >= beta){
				return ret;
			}
		}
		return ret;
	}
	public int[] getMin(int alpha, int beta, int depth)
	{
		int ret[] = {beta,-1,-1};
		if(depth == 0)
		{
			ret[0] = this.evaluation();
			return ret;
		}
		ArrayList<P> possibleMoves = this.getPossibleMove(1);
		if(possibleMoves.size() == 0)
		{
			return ret;
		}
		for(int i=0 ;i < possibleMoves.size(); i++)
		{

			P pm = possibleMoves.get(i);
			this.practiseMove(pm.x, pm.y);    //pretending go
			if(this.checkWin(pm.x, pm.y))
			{
				ret[0] = Integer.MIN_VALUE;
				ret[1] = pm.x;
				ret[2] = pm.y;
				this.practiseRollbackMove();
				return ret;
			}
			int[] tmp = this.getMin(alpha,beta,depth-1);
			this.practiseRollbackMove();      //rollback
			
			if(tmp[0] < ret[0]){
				ret[0] = tmp[0];
				ret[1] = pm.x;
				ret[2] = pm.y;
			}
			if(alpha >= ret[0]){
				return ret;
			}
		}
		return ret;
	}
}
