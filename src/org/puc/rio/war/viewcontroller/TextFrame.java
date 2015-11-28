package org.puc.rio.war.viewcontroller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class TextFrame extends JFrame {
	JComboBox<String> numberOptions;

	public TextFrame(String message) {
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setTitle(message);
		this.setSize(new Dimension(300, 100));
		this.setLayout(new BorderLayout());
		this.setResizable(false);
		this.setLocationRelativeTo(null);

		JTextArea textArea = new JTextArea(message);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				dispose();
			}
		});

		this.add(textArea, BorderLayout.CENTER);
		this.add(closeButton, BorderLayout.PAGE_END);
	}
}
