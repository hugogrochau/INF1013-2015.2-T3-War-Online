package org.puc.rio.war.viewcontroller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.puc.rio.war.WarGame;
import org.puc.rio.war.model.Card;
import org.puc.rio.war.model.Player;
import org.puc.rio.war.model.TerritoryCard;

@SuppressWarnings("serial")
public class CardSelectionFrame extends JFrame implements MouseListener {
	private HashMap<JLabel, Card> cards = new HashMap<JLabel, Card>();
	private List<Card> selectedCards = new LinkedList<Card>();

	private int maxNumberOfCards;

	private JPanel cardDisplayPanel;

	private JButton exchangeCardsButton;

	private Player player;

	public CardSelectionFrame(Player p, int maxNumberOfCards,
			boolean forcedToExchange) {
		this.player = p;
		this.maxNumberOfCards = maxNumberOfCards;
		if (forcedToExchange) {
			this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		} else {
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
		this.setTitle(String.format("%s's cards%s", p.getName(),
				forcedToExchange ? " (Must exchange)" : ""));
		this.setSize(new Dimension(1100 + 2 * 2 * 5, 600));
		this.getContentPane().setLayout(new BorderLayout());
		this.setResizable(false);
		this.setLocationRelativeTo(null);

		/* Card display panel */
		this.cardDisplayPanel = new JPanel();
		this.cardDisplayPanel.setLayout(new BoxLayout(cardDisplayPanel,
				BoxLayout.X_AXIS));

		for (Card c : p.getCards()) {
			JLabel cardLabel = new JLabel();
			cardLabel.setSize(220, 363);
			cardLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			cardLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
			cardLabel.addMouseListener(this);
			String imagePath;
			if (c instanceof TerritoryCard) {
				TerritoryCard tc = (TerritoryCard) c;
				imagePath = String.format(
						"resources/cards/war_carta_%s.png",
						tc.getTerritory().getName().toLowerCase()
								.replaceAll("\\s+", ""));
				imagePath = Normalizer
						.normalize(imagePath, Normalizer.Form.NFD);
				imagePath = imagePath.replaceAll("[^\\x00-\\x7F]", "");
			} else {
				imagePath = "resources/cards/war_carta_coringa.png";
			}
			ImageIcon cardImage = new ImageIcon(imagePath);
			Image resizedImage = cardImage.getImage().getScaledInstance(
					cardLabel.getWidth(), cardLabel.getHeight(),
					Image.SCALE_SMOOTH);
			cardLabel.setIcon(new ImageIcon(resizedImage));

			this.cards.put(cardLabel, c);
			this.cardDisplayPanel.add(cardLabel);
		}
		String exchangeCardsButtonText = String.format(
				"Exchange cards for %d armies", WarGame.getInstance()
						.getCardExchangeArmyCount());
		if (WarGame.getInstance().isAttacking()) {
			exchangeCardsButtonText = String.format("Take these cards from %s",
					p.getName());
		}
		this.exchangeCardsButton = new JButton(exchangeCardsButtonText);
		ActionListener exchangeCardsListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				WarGame.getInstance().exchangeCards(selectedCards);
				dispose();
			}

		};
		this.exchangeCardsButton.addActionListener(exchangeCardsListener);
		this.exchangeCardsButton.setEnabled(false);

		this.getContentPane().add(this.cardDisplayPanel, BorderLayout.CENTER);
		this.getContentPane().add(this.exchangeCardsButton,
				BorderLayout.PAGE_END);
	}

	@Override
	public void mouseClicked(MouseEvent me) {
		JLabel clickedCard = (JLabel) me.getSource();
		int index = this.selectedCards.indexOf(this.cards.get(clickedCard));
		/* already selected */
		if (index >= 0) {
			this.selectedCards.remove(index);
			clickedCard.setBorder(BorderFactory
					.createLineBorder(Color.BLACK, 2));
		} else if (this.selectedCards.size() < this.maxNumberOfCards) {
			this.selectedCards.add(this.cards.get(clickedCard));
			clickedCard
					.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
		}
		if ((this.player.hasValidCardExchange(this.selectedCards) && WarGame
				.getInstance().isPlacing())
				|| (WarGame.getInstance().isAttacking() && this.selectedCards
						.size() < this.maxNumberOfCards)
				&& !player.getName().equals(
						WarGame.getInstance().getCurrentPlayer().getName())) {
			this.exchangeCardsButton.setEnabled(true);
		} else {
			this.exchangeCardsButton.setEnabled(false);
		}
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
}
