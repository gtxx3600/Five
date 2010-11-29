import javax.swing.UIManager;


public class FiveChess {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		Five_Game fg = new Five_Game();
		
		
	}

}
