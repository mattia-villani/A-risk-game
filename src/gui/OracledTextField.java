package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import oracle.Tree;

public class OracledTextField extends JTextField {

	private final static boolean verbose = true;
	
	private boolean oracleEnabled = true;
	private boolean errorColor = false;
	private String prediction;
	private Tree tree;
	private int fix_x = 4;
	private int fix_y = -1;
	private double fix_height = 1.3f;
		
	@Override
	public boolean isManagingFocus(){
		return true; // so that the tab key is detected
	}
	
	public OracledTextField(){
		super();
		this.enableOracle( new Tree( new String[]{
				"prova",
				"provaNumeroDue",
				"cosa sta succedendo"
		} ) );
		
		this.getDocument().addDocumentListener(new DocumentListener(){
			
			public void callRefresh(DocumentEvent arg0){
				String text;
				try {
					text = arg0.getDocument().getText(0, arg0.getDocument().getLength());
					if ( verbose ) System.out.println("Update text:" +text );
					refresh( text );
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				callRefresh(arg0);
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				callRefresh(arg0);
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				callRefresh(arg0);
			}
			
		});
		
		this.addKeyListener( new KeyListener(){

			@Override
			public void keyPressed(KeyEvent arg0) {
				if ( oracleEnabled == false ) return;
				if ( ( arg0.getKeyCode() == KeyEvent.VK_TAB 
						|| arg0.getKeyCode() == KeyEvent.VK_ENTER )
					 && prediction.equals("") == false ){
						System.out.println("Tab pressed");
						OracledTextField.this.setText( OracledTextField.this.getText()+ prediction);
						arg0.consume();
					}
				else if ( arg0.getKeyCode() != KeyEvent.VK_BACK_SPACE){
					Tree result = tree.evalue( getText()+arg0.getKeyChar() );
					if ( result == null ){
						arg0.consume();
						setText( getText() );
						errorColor=true;
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {}

			@Override
			public void keyTyped(KeyEvent arg0) {}
			
		});
				
	}
	
	public void refresh( String text ){
		if ( oracleEnabled == false ) return;
		assert tree != null : "tree should be defined at this point";
		
		Tree result;
		result = tree.evalue(text);
		try{
			if ( result != null ){ 
				prediction = result.getUniquePath();
				errorColor = false;
			}else prediction = null;
		} catch(Tree.NotUniqueException e){}

	}
	
	public void enableOracle( Tree tree ){
		assert this.getText().equals("") : "this option should be used in a different moment";
		
		oracleEnabled = true;
		errorColor = false;
		if ( tree != null ) this.tree = tree;
	}
	public void disableOracle(){
		oracleEnabled = false;
	}
	
	
	@Override
	public void paintComponent( Graphics g ){		
		super.paintComponent(g);
		if ( ! oracleEnabled ) return;

		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setFont(this.getFont());		
		Rectangle2D currentSize = g2d.getFontMetrics().getStringBounds(this.getText()+"",g2d);	
		
		if ( errorColor ){
			int X = (int) currentSize.getX() + this.getMargin().left;
			int Y = (int) currentSize.getY() + (int) currentSize.getHeight() + this.getMargin().top;
			int W = (int) currentSize.getWidth()+2;
			int H = (int) currentSize.getHeight()+2;
			g2d.setColor(Color.red);
			g2d.fillRect(X,Y,W,H);
			g2d.setColor(Color.BLUE);
			g2d.drawString(this.getText(), X, Y+(int)currentSize.getHeight() );
		}

		if ( prediction==null || prediction.equals("") ) return;
		
		g2d.setFont(this.getFont().deriveFont(Font.BOLD));
		Rectangle2D toAddSize = g2d.getFontMetrics().getStringBounds(prediction+"",g2d);
		
		int x = fix_x+(int)(currentSize.getX()+currentSize.getWidth())+this.getMargin().left;
		int y = fix_y+(int)currentSize.getY()+this.getMargin().top + (int)currentSize.getHeight();
		
		// back light
		g.setColor(Color.BLUE);
		g2d.fillRect(
				x, 
				y, 
				(int)toAddSize.getWidth(), 
				(int)(toAddSize.getHeight()*fix_height));
		// sudgestion
		g.setColor(Color.LIGHT_GRAY);
		g2d.drawString(prediction , x, y+(int)toAddSize.getHeight());

	}
	
}
