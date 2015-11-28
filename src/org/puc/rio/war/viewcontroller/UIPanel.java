package org.puc.rio.war.viewcontroller;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.puc.rio.war.Util;
import org.puc.rio.war.WarGame;
import org.puc.rio.war.WarLogic;
import org.puc.rio.war.model.Card;
import org.puc.rio.war.model.Player;

@SuppressWarnings("serial")
public class UIPanel extends JPanel implements MouseListener, Observer {

	private CardLayout layout;

	private JPanel startPanel;
	private JPanel enterNamePanel;
	private JPanel gamePanel;
	private JPanel optionsPanel;
	private JPanel namesPanel;
	private JPanel gameEndedPanel;

	private List<JLabel> playerLabels = new LinkedList<JLabel>();
	private JLabel statusLabel;

	JTextField playerNameTextField;

	private JButton actionButton;
	private JButton showObjectiveButton;
	private JButton showCardsButton;
	private JButton endTurnButton;
	private JButton toggleMapDisplayButton;

	private Dimension size;

	private final double MULTIPLIER_X = 1.0;
	private final double MULTIPLIER_Y = 0.2;

	public UIPanel() {
		this.layout = new CardLayout();
		this.setLayout(layout);

		this.size = Util.getGameSize();
		this.size.height = (int) (this.size.height * MULTIPLIER_Y);
		this.size.width = (int) (this.size.width * MULTIPLIER_X);
		this.setMaximumSize(this.size);
		this.addStartUIPanel();

		this.layout.show(this, "Starting UI");
	}

	private void addStartUIPanel() {
		this.startPanel = new JPanel();
		this.startPanel.setLayout(new BoxLayout(startPanel, BoxLayout.Y_AXIS));

		this.enterNamePanel = new JPanel();
		this.enterNamePanel.setLayout(new BoxLayout(enterNamePanel, BoxLayout.X_AXIS));
		final JLabel l1 = new JLabel("Welcome to War. Enter your name:");
		l1.setMaximumSize(new Dimension(300, 50));
		l1.setAlignmentY(Component.TOP_ALIGNMENT);
		this.enterNamePanel.add(l1);

		this.playerNameTextField = new JTextField();
		this.playerNameTextField.setMaximumSize(new Dimension(400, 50));
		this.playerNameTextField.setFont(new Font("Arial", Font.PLAIN, 34));
		this.playerNameTextField.setAlignmentY(Component.TOP_ALIGNMENT);
		this.enterNamePanel.add(playerNameTextField);

		JButton submitButton = new JButton("Submit");
		submitButton.setMaximumSize(new Dimension(100, 50));
		submitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {

				if (playerNameTextField.getText().length() > 0) {
					WarGame.getInstance().addPlayer(playerNameTextField.getText(), true);
				}
				
			}
		});
		submitButton.setAlignmentY(Component.TOP_ALIGNMENT);
		this.enterNamePanel.add(submitButton);

		this.startPanel.add(enterNamePanel);
		this.add(startPanel, "Starting UI");
	}

	public void update(boolean first) {
		this.updateGameUI(first);
		if (first) {
			layout.show(this, "Game UI");
		}
	}

	public void updateGameUI(boolean first) {
		if (first) {
			this.gamePanel = new JPanel();
			this.gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.X_AXIS));
			this.add(gamePanel, "Game UI");
			this.gamePanel.addMouseListener(this);
		}
		this.updateNamesPanel(first);
		this.updateOptionsPanel(first);
	}

	public void showGameEndedPanel(Player winner) {
		this.gameEndedPanel = new JPanel();
		this.gameEndedPanel.setLayout(new BorderLayout());
		JTextArea endGameMessage = new JTextArea(
				String.format("The game has ended, %s is victorious for completing his/her objective (%s)",
						winner.getName(), winner.getObjective().getDescription()));
		this.gameEndedPanel.add(endGameMessage, BorderLayout.CENTER);
		this.add(gameEndedPanel, "Game Ended");
		layout.show(this, "Game Ended");
	}

	private void updateNamesPanel(boolean first) {
		if (first) {
			this.namesPanel = new JPanel();
			this.namesPanel.setLayout(new BoxLayout(namesPanel, BoxLayout.Y_AXIS));
			this.namesPanel.setMaximumSize(new Dimension(this.size.width / 8, this.size.height));
			JLabel playersLabel = new JLabel("Players:");
			this.namesPanel.add(playersLabel);
			this.gamePanel.add(this.namesPanel);
		}
		int i = 0;
		for (Player p : WarGame.getInstance().getPlayers()) {
			JLabel playerLabel;
			if (first) {
				playerLabel = new JLabel();
				playerLabel.setOpaque(true);
				playerLabel.setBackground(p.getColor());
				playerLabel.setForeground(Player.getForegroundColor(p.getColor()));
				playerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
				this.playerLabels.add(playerLabel);
				this.namesPanel.add(playerLabel);
			} else {
				playerLabel = this.playerLabels.get(i);
			}
			StringBuilder sb = new StringBuilder();
			for (Card c : p.getCards()) {
				sb.append(c.getType().toString());
				sb.append(" ");
			}
			playerLabel.setText(String.format("%s (%d territories)", p.getName(),
					WarGame.getInstance().getMap().getTerritoriesByOwner(p).size()));
			if (WarGame.getInstance().getCurrentPlayer().equals(p)) {
				playerLabel.setFont(playerLabel.getFont().deriveFont(playerLabel.getFont().getStyle() | Font.BOLD));
			} else {
				playerLabel.setFont(playerLabel.getFont().deriveFont(playerLabel.getFont().getStyle() & ~Font.BOLD));
			}
			i++;
		}
	}

	public void updateOptionsPanel(boolean first) {
		if (first) {
			this.optionsPanel = new JPanel();
			this.optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
			this.optionsPanel.setMaximumSize(new Dimension(this.size.width / 8 * 7, this.size.height));
			this.statusLabel = new JLabel();
			this.statusLabel.setOpaque(true);
			this.statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			this.actionButton = new JButton();
			this.actionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			this.actionButton.setMaximumSize(new Dimension(200, 25));
			this.actionButton.setEnabled(false);
			ActionListener actionButtonListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					WarGame.getInstance().actionPerformed();
				}
			};
			actionButton.addActionListener(actionButtonListener);

			this.showCardsButton = new JButton("Show Cards");
			this.showCardsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			this.showCardsButton.setMaximumSize(new Dimension(200, 25));
			this.showCardsButton.setEnabled(false);
			ActionListener showCardsListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					WarGame.getInstance().showCards(false);
				}
			};
			this.showCardsButton.addActionListener(showCardsListener);

			this.showObjectiveButton = new JButton("Show Objective");
			this.showObjectiveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			this.showObjectiveButton.setMaximumSize(new Dimension(200, 25));
			this.showObjectiveButton.setEnabled(true);
			ActionListener showObjectiveListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					WarGame.getInstance().showObjective();
				}
			};
			this.showObjectiveButton.addActionListener(showObjectiveListener);

			this.endTurnButton = new JButton("End Turn");
			this.endTurnButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			this.endTurnButton.setMaximumSize(new Dimension(200, 25));
			this.endTurnButton.setEnabled(false);
			ActionListener endTurnButtonListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					WarGame.getInstance().nextTurn();
				}
			};
			this.endTurnButton.addActionListener(endTurnButtonListener);

			this.toggleMapDisplayButton = new JButton("Toggle Map Display");
			this.toggleMapDisplayButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			this.toggleMapDisplayButton.setMaximumSize(new Dimension(200, 25));
			ActionListener toggleMapDisplayButtonListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					WarGame.getInstance().getWarFrame().getMapPanel().toggleMapDisplay();
				}
			};
			toggleMapDisplayButton.addActionListener(toggleMapDisplayButtonListener);

			this.optionsPanel.add(this.statusLabel);
			this.optionsPanel.add(this.actionButton);
			this.optionsPanel.add(this.showCardsButton);
			this.optionsPanel.add(this.showObjectiveButton);
			this.optionsPanel.add(this.endTurnButton);
			this.optionsPanel.add(this.toggleMapDisplayButton);

			this.gamePanel.add(this.optionsPanel);
		}

		Player currentPlayer = WarGame.getInstance().getCurrentPlayer();
		String actionString = "No action";
		String statusString = "No status";
		this.actionButton.setEnabled(false);
		this.endTurnButton.setEnabled(true);
		if (currentPlayer.getCards().isEmpty()) {
			this.showCardsButton.setEnabled(false);
			this.showCardsButton.setText("Show Cards");
		} else {
			this.showCardsButton.setEnabled(true);
			this.showCardsButton.setText(String.format("Show Cards (%d)", currentPlayer.getCards().size()));
		}
		switch (WarGame.getInstance().getTurnState()) {
		case ATTACKING:
			if (WarGame.getInstance().getSelectedTerritory() == null) {
				statusString = "Select a country to attack from";
			} else {
				statusString = "Select a country to attack";
			}
			actionString = "Stop attacking";
			this.actionButton.setEnabled(true);
			break;
		case MOVING_ARMIES:
			actionString = "Clear selection";
			this.actionButton.setEnabled(true);
			if (WarGame.getInstance().getSelectedTerritory() == null) {
				statusString = "Select a country to move from";
			} else if (WarGame.getInstance().getTargetedTerritory() == null) {
				statusString = "Select a country to move to";
			}
			break;
		case PLACING_NEW_ARMIES:
			statusString = String.format("Select a country place armies in (you have %d armies left to place)",
					currentPlayer.getUnplacedArmies());
			if (WarGame.getInstance().getSelectedTerritory() != null && currentPlayer.getUnplacedArmies() > 0) {
				actionString = String.format("Place armies in %s",
						WarGame.getInstance().getSelectedTerritory().getName());
				this.actionButton.setEnabled(true);
			}
			/* must always place all armies */
			this.endTurnButton.setEnabled(false);
			break;
		default:
			break;
		}
		this.statusLabel.setText(String.format("(%s's turn) %s", currentPlayer.getName(), statusString));
		this.statusLabel.setBackground(currentPlayer.getColor());
		this.statusLabel.setForeground(Player.getForegroundColor(currentPlayer.getColor()));
		this.actionButton.setText(actionString);
	}

	@Override
	public void mouseClicked(MouseEvent me) {
		WarGame.getInstance().focusPopupIfExists();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Observable obs, Object obj) {
		this.update(false);
	}
}
