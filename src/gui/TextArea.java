/** 
 *  Group Name: 42
 *  Team Members: Jimmy Carney (15207581), Mattia Villani (15201690), Andrew Kilbride (04429516)
 **/

package gui;

import javax.swing.*;
import java.awt.*;

public class TextArea extends JTextArea {

	private static final long serialVersionUID = 1L;

	public TextArea() {
		setEditable(false);
		setLineWrap(true);
		setWrapStyleWord(true);
		setBackground(new Color(244, 239, 202));
		setMargin(new Insets(5,5,5,5));
		return;
	}
}