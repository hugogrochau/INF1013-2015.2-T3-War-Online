package org.puc.rio.war.viewcontroller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.puc.rio.war.WarGame;

@SuppressWarnings("serial")
public class ChooseNumberFrame extends JFrame implements ActionListener {
	JComboBox<String> numberOptions;

	public ChooseNumberFrame(int maxNumber, String message) {
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setTitle(message);
		this.setSize(new Dimension(300, 100));
		this.getContentPane().setLayout(new BorderLayout());
		this.setResizable(false);
		this.setLocationRelativeTo(null);

		JLabel instructionLabel = new JLabel(message);

		String[] options = new String[maxNumber];
		for (int i = 0; i < maxNumber; i++) {
			options[i] = ((Integer) (i + 1)).toString();
		}
		this.numberOptions = new JComboBox<String>(options);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				dispose();
			}
		});

		JButton confirmButton = new JButton("Confirm");
		confirmButton.addActionListener(this);

		this.add(instructionLabel, BorderLayout.PAGE_START);
		this.add(this.numberOptions, BorderLayout.CENTER);
		this.add(confirmButton, BorderLayout.LINE_END);
		this.add(cancelButton, BorderLayout.PAGE_END);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.dispose();

		String value = (String) this.numberOptions.getSelectedItem();
		WarGame.getInstance().selectNumber(Integer.parseInt(value));
	}
}
