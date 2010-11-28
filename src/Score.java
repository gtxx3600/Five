
public class Score implements Comparable<Score>{
	int score;
	int type[];
	final public static int l3[] = {4,5,6};
	final public static int l4[] = {7};

	final public static int l34[] = {4,5,6,7,8,9,10};
	final public static int s3[] = {16,17,18};
	final public static int s4[] = {8,9,10,19,20,21,22};
	final public static int s4l3[] = {4,5,6,19,20,21,22};
	final public static int s34[] = {16,17,18,19,20,21,22};
	final public static int w4[] = {7};
	final public static int win[] = {11,23};
	final public static int t_s_dict[] = {
		100,
		75,
		50,
		200,
		750,
		1000,
		750,
		5000,
		750,
		750,
		750,
		100000,
		25,
		25,
		25,
		200,
		200,
		200,
		200,
		900,
		750,
		750,
		750,
		100000
	};
	public Score setScore(int i)
	{
		score = i;
		return this;
	}
	public Score()
	{
		score = 0;
		type = new int[24];//12-live ;12-sleep
		/*live: 2,3,4,5
		 * sleep: 2,3,4,5
		 * seq: sleep2,live2,sleep3,live3,s4,l4,5
		 */
	}
	public Score(int i)
	{
		score = i;
		type = new int[24];//12-live ;12-sleep
		/*live: 2,3,4,5
		 * sleep: 2,3,4,5
		 * seq: sleep2,live2,sleep3,live3,s4,l4,5
		 */
	}
	public void addType(int i)
	{
		if(i<0||i>23)return;
		type[i]+=1;
		refreshScore(i);
		//score+=t_s_dict[i];
	}
	public void refreshScore(int i)
	{
		if(score >= 100000)
			return;
		int cl4,cl3,cs4,cs3;
		switch(i)
		{
		case 0:
		case 1:
		case 2:
		case 3:
			score += t_s_dict[i];
			return;
		case 4:
		case 5:
		case 6:
			cl3 = countTypeInRange(l3);
			if(hasTypeInRange(l4)||hasTypeInRange(s4))
			{
				if(score < 10000)score = 10000;
				return;
			}else if(cl3 >= 2)
			{
				if(score < 4000) score = 4000;
				return ;
			}
			score += t_s_dict[i];
			return;
		case 7:
		case 8:
		case 9:
		case 10:
			cl4 = countTypeInRange(l4);
			cs4 = countTypeInRange(s4);
			if(cl4>=2||cs4>=2)
			{
				if(score < 20000)score = 20000;
				return;
			}else if(hasTypeInRange(l3))
			{
				if(score < 10000) score = 10000;
				return ;
			}
			score += t_s_dict[i];
			return;
		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
		case 16:
		case 17:
		case 18:
			score += t_s_dict[i];
			return;
		case 19:
		case 20:
		case 21:
		case 22:
			cl4 = countTypeInRange(l4);
			cs4 = countTypeInRange(s4);
			if(cl4>=2||cs4>=2)
			{
				if(score < 20000)score = 20000;
				return;
			}else if(hasTypeInRange(l3))
			{
				if(score < 10000) score = 10000;
				return ;
			}
			score += t_s_dict[i];
			return;
		case 23:
			score += t_s_dict[i];
			return;
		default:
			System.err.println("Class Score:refreshScore():index out of range");
			return;
		}

	}
	public boolean hasTypeInRange(int start,int end)
	{
		
		if(start <0 || start >= type.length)return false;
		if(end <= start || end > type.length)return false;
		for(int i = start;i<end;i++)
		{
			if(type[i]>0)return true;
		}
		
		return false;
	}
	public int countTypeInRange(int start,int end)
	{
		int ret = 0;
		if(start <0 || start >= type.length)return 0;
		if(end <= start || end > type.length)return 0;
		for(int i = start;i<end;i++)
		{
			ret+= type[i];
		}
		return ret;
	}
	public boolean hasTypeInRange(int range[])
	{
		for(int i=0;i<range.length;i++)
		{
			if(range[i]<0||range[0]>=type.length)continue;
			if(type[range[i]]>0)return true;
		}
		return false;
	}
	public int countTypeInRange(int range[])
	{
		int ret = 0;
		for(int i=0;i<range.length;i++)
		{
			if(range[i]<0||range[0]>=type.length)continue;
			ret+=type[range[i]];
		}
		return ret;
	}
	@Override
	public int compareTo(Score arg0) {
		if(arg0.score > score)return 1;
		if(arg0.score == score)return 0;
		// TODO Auto-generated method stub
		return 0;
	}
}
