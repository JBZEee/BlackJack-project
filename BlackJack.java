import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class BlackJack {
    private class Card{
        String value;
        String type;

        public Card(String value, String type){
            this.value = value;
            this.type = type;
        }
        public String toString(){
            return value + "-" + type;
        }
        public int getValue(){
            if("AJQK".contains(value)){ // for A J Q K
                if(value == "A"){
                    return 11;
                }
                return 10;
            }
            return Integer.parseInt(value);//2-10
        }
        public boolean isAce(){
            return value == "A"; //check if the value is A
        }
        public String getImgPath(){
            return "./Cards/" + toString() + ".png";
        }
    }
    
    ArrayList<Card> deck;
    Random random = new Random(); //to shuffle the deck
    
    //dealer
    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;

    //player
    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;

    //window
    int boardWidth = 600;
    int boardHeight = boardWidth;
    int cardWidth = 110; // ratio should be 1 to 1.4 for the images to be sharp
    int cardHeight = 154;
    JFrame frame = new JFrame("BlackJack");
    JPanel gamePanel = new JPanel()  {
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);

            try{
            //draw hidden card
            Image hiddenCardImg = new ImageIcon(getClass().getResource("./Cards/BACK.png")).getImage();
            if(!stay.isEnabled()){
                hiddenCardImg = new ImageIcon(getClass().getResource(hiddenCard.getImgPath())).getImage();
                };
            
            g.drawImage(hiddenCardImg, 20, 20, cardWidth, cardHeight,null);

            //draw dealer's hand
            for (int i =0; i<dealerHand.size();i++){
                Card card = dealerHand.get(i);
                Image cardImage = new ImageIcon(getClass().getResource(card.getImgPath())).getImage();
                g.drawImage(cardImage,cardWidth + 25 + (cardWidth + 5)*i, 20,cardWidth,cardHeight,null);
            }

            //draw player's hand
            for(int i=0;i<playerHand.size();i++){
                Card card = playerHand.get(i);
                Image cardImage = new ImageIcon(getClass().getResource(card.getImgPath())).getImage();
                g.drawImage(cardImage, 20 + (cardWidth + 5)*i, 320, cardWidth,cardHeight,null);
            }
            if (!stay.isEnabled()){
                dealerSum = reduceDealerAce();
                playerSum = reducePlayerAce();
                System.out.println("STAY: ");
                System.out.println(dealerSum);
                System.out.println(playerSum);

                String message = "";
                if(playerSum>21){
                    message = "YOU LOSE";
                }
                else if(dealerSum>21){
                    message = "YOU WIN";
                } // playersum and dealersum both are under 21
                else if(dealerSum == playerSum){
                    message = "TIE";
                } else if(playerSum>dealerSum){
                    message = "YOU WIN";
                } else{
                    message = "YOU LOSE";
                }
                g.setFont(new Font("Ariel", Font.PLAIN,30));
                g.setColor(Color.BLACK);
                g.drawString(message, 220, 250);
            }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    };
    JPanel buttonPanel = new JPanel();
    JButton hitButton = new JButton("Hit");
    JButton stay = new JButton("Stay");
    //constructor
    BlackJack(){
        startGame();

        frame.setVisible(true);
        frame.setSize(boardWidth,boardHeight);
        frame.setLocationRelativeTo(null);//open the window in the center of the screen instead of the top left
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(53,101,77));
        frame.add(gamePanel);

        hitButton.setFocusable(false);
        buttonPanel.add(hitButton);
        stay.setFocusable(false);
        buttonPanel.add(stay);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        hitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                Card card = deck.remove(deck.size()-1);
                playerSum += card.getValue();
                playerAceCount += card.isAce() ? 1:0;
                playerHand.add(card);
                if(reducePlayerAce()>21){
                    hitButton.setEnabled(false);
                }
                gamePanel.repaint();
            }
        });
        stay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                hitButton.setEnabled(false);
                stay.setEnabled(false);
                while(dealerSum < 17){
                    Card card = deck.remove(deck.size()-1);
                    dealerSum += card.getValue();
                    dealerAceCount +=card.isAce() ?1:0;
                    dealerHand.add(card);
                    gamePanel.repaint();
                }
            }
        });
        gamePanel.repaint();
    }
    public void startGame(){
        //deck 
        buildDeck();
        shuffleDeck();

        //dealer
        dealerHand = new ArrayList<Card>();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCard = deck.remove(deck.size() - 1);//remove a card from the deck at the last index
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card card  = deck.remove(deck.size() - 1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0 ;
        dealerHand.add(card);

        System.out.println("DEALER");
        System.out.println(hiddenCard);
        System.out.println(dealerHand);
        System.out.println(dealerSum);
        System.out.println(dealerAceCount);

        //player
        playerHand = new ArrayList<Card>();
        playerSum = 0;
        playerAceCount = 0;
        for(int i =0; i<2;i++){
            card = deck.remove(deck.size()-1);
            playerSum += card.getValue();
            playerAceCount = card.isAce() ? 1:0;
            playerHand.add(card);
        }
        System.out.println("PLAYER:");
        System.out.println(playerHand);
        System.out.println(playerSum);
        System.out.println(playerAceCount);
    }

    public void buildDeck(){
        deck = new ArrayList<Card>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7","8","9", "10", "J","Q","K"};
        String[] types = {"C","D","H","S"};
        for(int i=0; i< types.length; i++){
            for(int j=0; j < values.length;j++){
                Card card = new Card(values[j],types[i]);
                deck.add(card);
            }
        }
        System.out.println("BUILD DECK:");
        System.out.println(deck);
    }
    public void shuffleDeck(){
        for (int i =0;i<deck.size();i++){
            int j = random.nextInt(deck.size());
            Card currCard = deck.get(i);
            Card randomCard = deck.get(j);
            deck.set(i,randomCard);
            deck.set(j, currCard);

        }
        System.out.println("AFTER SHUFFLE:");
        System.out.println(deck);
    }
    public int reducePlayerAce(){
        while(playerSum>21 && playerAceCount>0){
            playerSum -= 10;
            playerAceCount -= 1;
        } 
        return playerSum;
    }

    public int reduceDealerAce(){
        while(dealerSum>21 && dealerAceCount>0){
            dealerSum -=10;
            dealerAceCount -=1;
        }
        return dealerSum;
    }
}
