package org.puc.rio.war.viewcontroller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.puc.rio.war.Util;
import org.puc.rio.war.WarGame;
import org.puc.rio.war.model.Player;
import org.puc.rio.war.model.Territory;

@SuppressWarnings("serial")
public class MapPanel extends JPanel implements Observer {

	private Image backgroundImage;
	private final double MULTIPLIER_X = 1.0;
	private final double MULTIPLIER_Y = 0.8;
	public double coordinatesMultiplierX;
	public double coordinatesMultiplierY;
	private Dimension mapSize;
	private List<JLabel> armiesLabels = new LinkedList<JLabel>();
	private boolean labelsHidden = false;

	public MapPanel() {
		this.setBackgroundImage("resources/maps/war_tabuleiro_completo.png");
		Dimension gameSize = Util.getGameSize();
		this.mapSize = new Dimension((int) (gameSize.width * MULTIPLIER_X),
				(int) (gameSize.height * MULTIPLIER_Y));
		this.setPreferredSize(mapSize);
		this.setSize(mapSize);
		this.setMaximumSize(mapSize);
		this.setMinimumSize(mapSize);
		this.coordinatesMultiplierX = (mapSize.width / 1024.0);
		this.coordinatesMultiplierY = (mapSize.height / 768.0);
		this.setLayout(null);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
	}

	public Dimension getMapSize() {
		return mapSize;
	}

	public void update(boolean first) {
		this.updateArmyLabels(first);
		if (first) {
			this.addMouseListener(new MapPanelMouseListener());
		}
	}

	public void updateArmyLabels(boolean first) {
		int i = 0;
		Territory selectedTerritory = WarGame.getInstance()
				.getSelectedTerritory();
		// Territory targetedTerritory = WarGame.getInstance()
		// .getTargetedTerritory();
		for (Territory t : WarGame.getInstance().getMap().getTerritories()) {
			Player owner = WarGame.getInstance().getPlayerByName(
					t.getOwnerName());
			/* defaults */
			JLabel centerLabel;
			Color borderColor = Color.BLACK;
			int width = 130;
			int zOrder = 2;
			Color backgroundColor = owner.getColor();
			String text = String.format("(%d) %s", t.getArmyCount(),
					t.getName());

			if (first) {
				centerLabel = new JLabel("", SwingConstants.CENTER);
				centerLabel.setName(t.getName());
				centerLabel.setOpaque(true);
				centerLabel.addMouseListener(new MouseListener() {
					@Override
					public void mouseClicked(MouseEvent me) {
						JLabel label = (JLabel) me.getComponent();
						Territory t = WarGame.getInstance().getMap()
								.getTerritoryByName(label.getName());
						if (t != null) {
							WarGame.getInstance().selectTerritory(t);
						} else {
							WarGame.getInstance().focusPopupIfExists();
							System.out.println(String.format(
									"Couldn't find territory with name %s",
									label.getName()));
						}
					}

					@Override
					public void mouseEntered(MouseEvent e) {
					}

					@Override
					public void mouseExited(MouseEvent e) {
					}

					@Override
					public void mousePressed(MouseEvent e) {
					}

					@Override
					public void mouseReleased(MouseEvent e) {
					}
				});
				this.armiesLabels.add(centerLabel);
				this.add(centerLabel);
			} else {
				centerLabel = this.armiesLabels.get(i);
			}

			/* selected */
			if (selectedTerritory != null && selectedTerritory.equals(t)) {
				zOrder = 0;
				borderColor = Color.RED;
				/* targeted */
				// } else if (targetedTerritory != null &&
				// targetedTerritory.equals(t)) {
				// zOrder = 0;
				// borderColor = Color.BLUE;

				/* owned by current player */
			} else if (owner.equals(WarGame.getInstance().getCurrentPlayer())) {
				zOrder = 1;
				backgroundColor = backgroundColor.darker();
				borderColor = Color.WHITE;

				/* others */
			} else {
				zOrder = 2;
				borderColor = Color.BLACK;
			}

			switch (WarGame.getInstance().getTurnState()) {
			case ATTACKING:
				if (selectedTerritory != null) {
					/* neighbors */
					if (selectedTerritory.canAttack(t)) {
						backgroundColor = backgroundColor.brighter();
						borderColor = Color.YELLOW;
						zOrder = 1;
					}
				}
				break;
			case MOVING_ARMIES:
				if (selectedTerritory != null) {
					/* neighbors */
					if (selectedTerritory.canMoveTo(t)) {
						backgroundColor = backgroundColor.brighter();
						borderColor = Color.YELLOW;
						zOrder = 1;
					}
				}
				break;
			case PLACING_NEW_ARMIES:
				break;
			default:
				break;
			}

			if (this.labelsHidden) {
				text = String.format("%d", t.getArmyCount());
				width = 30;
			}
			if (!first) {
				this.setComponentZOrder(centerLabel, zOrder);
			}
			centerLabel
					.setBounds(
							(int) (t.getCenter().x * WarGame.getInstance()
									.getWarFrame().getMapPanel().coordinatesMultiplierX),
							(int) (t.getCenter().y * WarGame.getInstance()
									.getWarFrame().getMapPanel().coordinatesMultiplierY),
							width, 20);
			centerLabel.setForeground(Player.getForegroundColor(owner
					.getColor()));
			centerLabel.setText(text);
			centerLabel.setBorder(BorderFactory.createLineBorder(borderColor,
					3, true));
			centerLabel.setBackground(backgroundColor);
			this.repaint();
			centerLabel.repaint();
			i++;
		}
	}

	public void setBackgroundImage(String path) {
		try {
			this.backgroundImage = new ImageIcon(path).getImage();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return;
		}
	}

	public void toggleMapDisplay() {
		this.labelsHidden = !this.labelsHidden;
		if (labelsHidden) {
			this.setBackgroundImage("resources/maps/war_tabuleiro_com_nomes.png");
		} else {
			this.setBackgroundImage("resources/maps/war_tabuleiro_completo.png");

		}
		this.updateArmyLabels(false);
	}

	private class MapPanelMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent me) {
			for (Territory t : WarGame.getInstance().getMap().getTerritories()) {
				if (t.getPolygon().contains(
						me.getX()
								/ WarGame.getInstance().getWarFrame()
										.getMapPanel().coordinatesMultiplierX,
						me.getY()
								/ WarGame.getInstance().getWarFrame()
										.getMapPanel().coordinatesMultiplierY)) {
					WarGame.getInstance().selectTerritory(t);
					/* Cannot select twice */
					return;
				} else {
					WarGame.getInstance().focusPopupIfExists();
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent me) {
		}

		@Override
		public void mouseExited(MouseEvent me) {
		}

		@Override
		public void mousePressed(MouseEvent me) {
		}

		@Override
		public void mouseReleased(MouseEvent me) {
		}

	}

	@Override
	public void update(Observable obs, Object obj) {
		this.update(false);
	}
}
