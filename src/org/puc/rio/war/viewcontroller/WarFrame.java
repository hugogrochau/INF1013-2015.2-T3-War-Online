package org.puc.rio.war.viewcontroller;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import org.puc.rio.war.Util;
import org.puc.rio.war.model.Player;
import org.puc.rio.war.model.Territory;

@SuppressWarnings("serial")
public class WarFrame extends JFrame {

	private UIPanel uiPanel;
	private MapPanel mapPanel;
	private AttackFrame attackFrame;
	private TextFrame textFrame;
	private ChooseNumberFrame chooseNumberFrame;
	private CardSelectionFrame cardSelectionFrame;

	public WarFrame() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("War - by Hugo Grochau and Lucas Menezes");
		this.setSize(Util.getGameSize());
		this.getContentPane().setLayout(
				new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setResizable(false);
		this.addPanels();
		this.setVisible(true);
	}

	private void addPanels() {
		this.mapPanel = new MapPanel();
		this.uiPanel = new UIPanel();
		this.getContentPane().add(mapPanel);
		this.getContentPane().add(uiPanel);
	}

	public MapPanel getMapPanel() {
		return this.mapPanel;
	}

	public UIPanel getUIPanel() {
		return this.uiPanel;
	}

	public void startGame() {
		this.update(true);
	}

	public void update(boolean first) {
		this.uiPanel.update(first);
		this.mapPanel.update(first);
	}

	public void focusPopup() {
		if (this.hasAttackFrameActive()) {
			this.attackFrame.toFront();
		} else if (this.hasChooseNumberFrameActive()) {
			this.chooseNumberFrame.toFront();
		} else if (this.hasTextFrameActive()) {
			this.textFrame.toFront();
		} else if (this.hasCardSelectionFrameActive()) {
			this.cardSelectionFrame.toFront();
		}
	}

	public boolean hasAttackFrameActive() {
		return this.attackFrame != null && this.attackFrame.isVisible();
	}

	public boolean hasChooseNumberFrameActive() {
		return this.chooseNumberFrame != null
				&& this.chooseNumberFrame.isVisible();
	}

	public boolean hasTextFrameActive() {
		return this.textFrame != null && this.textFrame.isVisible();
	}

	public boolean hasCardSelectionFrameActive() {
		return this.cardSelectionFrame != null
				&& this.cardSelectionFrame.isVisible();
	}

	public boolean hasPopupActive() {
		return this.hasAttackFrameActive() || this.hasChooseNumberFrameActive()
				|| this.hasTextFrameActive()
				|| this.hasCardSelectionFrameActive();
	}

	public void spawnAttackFrame(Territory from, Territory to, int number) {
		/* only one at once */
		if (!this.hasPopupActive()) {
			this.attackFrame = new AttackFrame(from, to, number);
			this.attackFrame.setVisible(true);
		}
	}

	public void spawnChooseNumberFrame(int number, String message) {
		/* only one at once */
		if (!this.hasPopupActive()) {
			this.chooseNumberFrame = new ChooseNumberFrame(number, message);
			this.chooseNumberFrame.setVisible(true);
		}
	}

	public void spawnTextFrame(String message) {
		/* only one at once */
		if (!this.hasPopupActive()) {
			this.textFrame = new TextFrame(message);
			this.textFrame.setVisible(true);
		}
	}

	public void spawnCardSelectionFrame(Player currentPlayer,
			int maxNumberOfCards, boolean forcedToExchange) {
		/* only one at once */
		if (!this.hasPopupActive()) {
			this.cardSelectionFrame = new CardSelectionFrame(currentPlayer,
					maxNumberOfCards, forcedToExchange);
			this.cardSelectionFrame.setVisible(true);
		}
	}

}
