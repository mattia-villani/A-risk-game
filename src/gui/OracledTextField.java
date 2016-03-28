/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import com.sun.xml.internal.messaging.saaj.soap.impl.TreeException;

import oracle.Tree;

public class OracledTextField extends JTextField {

	private final static boolean verbose = false;
	
	private boolean oracleEnabled = false;
	private boolean errorColor = false;
	private String lastValid = "";
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
		this.getDocument().addDocumentListener(new DocumentListener(){
			
			public boolean callRefresh(DocumentEvent arg0){
				String text;
				try {
					text = arg0.getDocument().getText(0, arg0.getDocument().getLength());
					if ( verbose ) System.out.println("Update text:" +text );
					if ( refresh( text ) ){ 
						lastValid = text;
						return true;
					}
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
				return false;
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				callRefresh(arg0);
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				callRefresh(arg0);
				if ( ! oracleEnabled ) return ;
				Tree result;
				try {
					result = tree.evalue( arg0.getDocument().getText(0, arg0.getDocument().getLength()) );
					if ( result == null ){
						errorColor=true;
						new Toast.ErrorToast("Error: \""+arg0.getDocument().getText(0, arg0.getDocument().getLength())+"\" is not a valid entry", Toast.LONG);
						SwingUtilities.invokeLater(new Runnable(){
							@Override
						    public void run(){
								setText( lastValid );
								refresh(lastValid);
						    }
						});
						Animator.add(new Animator.Handler(250,0,1){

							@Override
							public void run() {
								errorColor=false;
								OracledTextField.this.repaint();
							}
							
						});
					}else callRefresh(arg0);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}

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
					 && prediction != null ){
						OracledTextField.this.setText( OracledTextField.this.getText()+ prediction);
						if ( arg0.getKeyCode() != KeyEvent.VK_ENTER )
							arg0.consume();
					}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {}

			@Override
			public void keyTyped(KeyEvent arg0) {
				
				
			}
			
		});
				
	}
	
	public String getValidatedText(){
		String str="";
		str = getText();
		if ( oracleEnabled )
			try{
				tree.evalue(str).getUniquePath();
			}catch(Tree.NotUniqueException e){
				str = "";
				new Toast.ErrorToast("\""+str+"\" does not identify a unique string", Toast.LONG);
			}
		return str;
	}
	
	public String getExtendedText () {
		assert !oracleEnabled || prediction != null ;
		return ( oracleEnabled ? getText()+prediction : "" );
	}
	
	public boolean refresh( String text ){
		if ( oracleEnabled == false ) return false;
		assert tree != null : "tree should be defined at this point";
		
		if ( text.equals("") ){ 
			prediction = null;
			return true;
		}
		
		Tree result;
		result = tree.evalue(text);
		try{
			if ( result != null ){ 
				prediction = result.getUniquePath();
				return true;
			}else prediction = null;
		} catch(Tree.NotUniqueException e){ 
			prediction = null; 
			return true; 
		}
		return false;
	}
	
	public void enableOracle( Tree tree ){
		assert this.getText().equals("") : "this option should be used in a different moment";
		
		oracleEnabled = true;
		if ( tree != null ) this.tree = tree;
		this.invalidate();
		this.repaint();
	}
	
	public void disableOracle(){
		oracleEnabled = false;
	}
	
	
	@Override
	public void paintComponent( Graphics g ){		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		super.paintComponent(g2d);
		if ( ! oracleEnabled ) return;

		g2d.setFont(this.getFont());		
		Rectangle2D currentSize = g2d.getFontMetrics().getStringBounds(this.getText()+"",g2d);	

		int x = fix_x+(int)(currentSize.getX()+currentSize.getWidth())+this.getMargin().left;
		int y = fix_y+(int)currentSize.getY()+this.getMargin().top + (int)currentSize.getHeight();

		if (( prediction == null || prediction.equals("") )
				&& this.getText().equals("")
				&& tree.getInstruction() != null ){
			g2d.setColor(Color.GRAY);
			g2d.drawString(tree.getInstruction(), x+1, y + (int)currentSize.getHeight() );
		}
					
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
