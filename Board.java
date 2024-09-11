import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Board extends JPanel implements ActionListener, KeyListener {

	// the players
    private Player2 player2;
    private Player1 player1;
    // controls the delay between each tick in Ms
    private final int DELAY = 25;
    // controls the size of the board
    public static final int TILE_SIZE = 50;
    public static final int ROWS = 20;
    public static final int COLUMNS = 20;
    // controls how much stuff appear on the board
    public static final int NUM_COINS = 5;
    public static final int NUM_BOMBS = 3;
    public static final int NUM_TREASURES = 50;
    // suppress serialization warning.
    // eclipse added this. dunno what it is.
    private static final long serialVersionUID = 490905409104883233L;

    
    // keep a reference to the timer object that triggers actionPerformed() in
    // case we need access to it in another method
    private Timer timer;
    private long startTime = System.currentTimeMillis();
    // stuff that appear on the game board
    // this is for changing the length of the array. 
    // copied it from: https://stackoverflow.com/questions/2426671/variable-length-dynamic-arrays-in-java
    private static ArrayList<Coin> coins;
    private static ArrayList<Bomb> bombs;
    private static Treasure treasure;



    //make images
    private BufferedImage background;
    private BufferedImage win;
    private BufferedImage lose;


    
    public Board() {

        // set the game board size
        setPreferredSize(new Dimension(TILE_SIZE * COLUMNS, TILE_SIZE * ROWS));
        // set the game board background color
        setBackground(new Color(0, 0, 0));
        // initialize the game state
        player1 = new Player1();
        player2 = new Player2();
        coins = populateCoins();
        bombs = populateBombs();
        treasure = populateTreasure();
        // the reason why they're all the same even though treasure is a single object
        // is because copy/pasting the same method is easier than making a new one

        // this timer will call the actionPerformed() method every DELAY ms
        timer = new Timer(DELAY, this);
        timer.start();
        
        loadImage();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // this method is called by the timer every DELAY ms.
        // use this space to update the state of your game or animation
        // before the graphics are redrawn.
    	
    	// long = big int forgor to tell you
        long elapsedTime = System.currentTimeMillis() - startTime;
        // better random than math.random, it does more random per random
        //https://stackoverflow.com/questions/5887709/getting-random-numbers-in-java
        Random rand = new Random();
        int randomNumber = rand.nextInt(100);
        // prevent the player from disappearing off the board
        player1.tick(elapsedTime);
        player2.tick(elapsedTime);
        

        // give the player money for collecting coins
//        collectCoins();
        
        //ends the game if the player's life is 0
        if(player1.getHp() == 0) {
        	gameEnd("lose");
        }
        if(player2.getHp() == 0) {
        	gameEnd("lose");
        }
        

        // calling repaint() will trigger paintComponent() to run again,
        // which will refresh/redraw the graphics.
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // when calling g.drawImage() i use "this" for the ImageObserver 
        // because Component makes the ImageObserver, and JPanel 
        // extends from Component. So "this" Board, can 
        // react to imageUpdate() events triggered by g.drawImage()

        // draw our graphics.
//        drawBackground(g);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        
        drawScore(g);
        for (Coin coin : coins) {
            coin.draw(g, this);
        }
        for (Bomb bomb : bombs) {
            bomb.draw(g, this);
        }
        player1.draw(g, this);
        player2.draw(g, this);

        // this smoothes out animations. too choppy if i don't. i asked chatgpt why and it
        // gave this. heres what i used for the explanation:
        //https://stackoverflow.com/questions/20804198/what-does-toolkit-getdefaulttoolkit-sync-mean
        //"syncs the graphics state" whatever that means
        Toolkit.getDefaultToolkit().sync();
    }





    @Override
    public void keyPressed(KeyEvent e) {
        // react to key down events
        player1.keyPressed(e);
        player2.keyPressed(e);
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // react to key up events
    }

    //god... this was an absolute PAIN IN THE... butt
    //WHY IS IT SO HARD TO PUT TEXT ON A SCREEN
    private void drawScore(Graphics g) {
    	//need to cast the Graphics to Graphics2D to draw decent text
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(
            RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(
            RenderingHints.KEY_FRACTIONALMETRICS,
            RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        // Set player 1 text
        String text1 = "Player 1: $" + player1.getmoney();
        // set the text color and font
        
        g2d.setColor(new Color(0,0,0));
        g2d.setFont(new Font("Lato", Font.BOLD, 25));
        FontMetrics metrics1 = g2d.getFontMetrics(g2d.getFont());
        
        // draw the score in the bottom of the screen
        // https://stackoverflow.com/a/27740330/4655368
        // Left side padding
        int x1 = 10; 
        // determine the y coordinate for the text
        int y1 = TILE_SIZE * (ROWS - 1) + metrics1.getAscent();
        // draw the string
        g2d.drawString(text1, x1, y1);

        // Set player 2 text
        String text2 = "Player 2: $" + player2.getmoney();
        // set the text color and font
        
        g2d.setColor(new Color(0,0,0));
        g2d.setFont(new Font("Lato", Font.BOLD, 25));
        FontMetrics metrics2 = g2d.getFontMetrics(g2d.getFont());
        
        // draw the score in the bottom of the screen
        // https://stackoverflow.com/a/27740330/4655368
        // Right side padding
        // determine the x coordinate for the text
        int x2 = getWidth() - 10 - metrics2.stringWidth(text2); 
        // determine the y coordinate for the text
        int y2 = TILE_SIZE * (ROWS - 1) + metrics2.getAscent();
        // draw the string
        g2d.drawString(text2, x2, y2);
    }

    private ArrayList<Coin> populateCoins() {
        ArrayList<Coin> coinList = new ArrayList<>();
        Random rand = new Random();

        // create the given number of coins in random positions on the board.
        // note that there is not check here to prevent two coins from occupying the same
        // spot, or to prevent coins from spawning in the same spot as the player
        // I didn't care enough for that
        for (int i = 0; i < NUM_COINS; i++) {
            int coinX = rand.nextInt(COLUMNS);
            int coinY = rand.nextInt(ROWS);
            coinList.add(new Coin(coinX, coinY));
        }

        return coinList;
    }
    
    private ArrayList<Bomb> populateBombs() {
        ArrayList<Bomb> bombList = new ArrayList<>();
        Random rand = new Random();

        // create the given number of bomb in random positions on the board.
        // note that there is not check here to prevent two bombs from occupying the same
        // spot, or to prevent bombs from spawning in the same spot as the player
        // I didn't care enough for that
        for (int i = 0; i < NUM_BOMBS; i++) {
            int bombX = rand.nextInt(COLUMNS);
            int bombY = rand.nextInt(ROWS);
            bombList.add(new Bomb(bombX, bombY));
        }

        return bombList;
    }
    
    private Treasure populateTreasure() {
        Random rand = new Random();

        // create the given number of treasures in random positions on the board.
        // note that there is not check here to prevent two treasures from occupying the same
        // spot, or to prevent treasures from spawning in the same spot as the player
            int treasureX = rand.nextInt(COLUMNS);
            int treasureY = rand.nextInt(ROWS);
            treasure = new Treasure(treasureX, treasureY);        

        return treasure;
    }

    private void collectCoins() {
        // allow players to pickup coins
        ArrayList<Coin> collectedCoins = new ArrayList<>();
        for (Coin coin : coins) {
            // if the player is on the same tile as a coin, collect it
            if (player1.getPos().equals(coin.getPos())) {
                // give the player some points for picking this up
                player1.addmoney(5);
                collectedCoins.add(coin);
                
            }
            if (player2.getPos().equals(coin.getPos())) {
                // give the player some points for picking this up
                player2.addmoney(5);
                collectedCoins.add(coin);
                
            }
        }
        // remove collected coins from the board
        coins.removeAll(collectedCoins);
        
        //for every coin you get you get another spawned
        for(int i = 0; i< collectedCoins.size(); i++) {
        	spawnCoin();
        }
    }
    
    private void collectBombs() {
        // allow players to pickup bombs
        ArrayList<Bomb> collectedBombs = new ArrayList<>();
        for (Bomb bomb : bombs) {
            // if the player is on the same tile as a bomb, collect it
            if (player1.getPos().equals(bomb.getPos())) {
                // give the player some points for picking this up
                player1.takeHp(1);
                collectedBombs.add(bomb);
                
            }
            if (player2.getPos().equals(bomb.getPos())) {
                // give the player some points for picking this up
            	player2.takeHp(1);
                collectedBombs.add(bomb);
                
            }
        }
        // remove collected bombs from the board
        bombs.removeAll(collectedBombs);
        
      //for every bomb you get you get another spawned
        for(int i = 0; i< collectedBombs.size(); i++) {
        	spawnBomb();
        }

    }

    private void collectTreasure() {
        // Allow players to pick up the treasure
        if (player1.getPos().equals(treasure.getPos())) {
            // Give the player some points for picking this up
            player1.addmoney(10);
            clearTreasure();
            gameEnd("win");
        } else if (player2.getPos().equals(treasure.getPos())) {
            // Give the player some points for picking this up
            player2.addmoney(10);
            clearTreasure();
            gameEnd("win");
        }
    }
    
    //spawns a coin
    private static void spawnCoin() {
        Random rand = new Random();
        int coinX;
        int coinY;
        // while nono work so i search up and find do while. 
        //https://stackoverflow.com/questions/18038533/using-while-loop-in-java

        do {
            coinX = rand.nextInt(COLUMNS);
            coinY = rand.nextInt(ROWS);
        } while (isOccupied(new Point(coinX, coinY)));

        coins.add(new Coin(coinX, coinY));
    }

    private void spawnBomb() {
        Random rand = new Random();
        int bombX;
        int bombY;
        // while nono work so i search up and find do while. 
        //https://stackoverflow.com/questions/18038533/using-while-loop-in-java

        do {
            bombX = rand.nextInt(COLUMNS);
            bombY = rand.nextInt(ROWS);
        } while (isOccupied(new Point(bombX, bombY)));

        bombs.add(new Bomb(bombX, bombY));
    }

    private static boolean isOccupied(Point pos) {
        return treasure.getPos().equals(pos);
    }

    
    
    //these do what you think they do
    public static void clearCoins() {
    	coins.clear();
    }
    
    public static void clearBombs() {
    	bombs.clear();
    }
    public static void clearTreasure() {
    	treasure = null;
    }

    
    // loads the images
    private void loadImage() {
        try {
            // you can use just the filename if the image file is in your
            // project folder, otherwise you need to provide the file path.
            background = ImageIO.read(new File("src/Images/background.png"));
        } catch (IOException exc) {
            System.out.println("Error opening image file: " + exc.getMessage());
            exc.printStackTrace(); // Print the stack trace for more details
        }
    }
   

    // This creates a new screen... or just kinda overlays the screen to show either
    //you win or you loose
   void gameEnd(String whatIf) {
        BufferedImage endImage = null;
        
        // hehe i remembered you talking about catching dautch for using ternary operators
        // so i felt like using it here to see if you ask me
        // heres where i learned it.
        //https://stackoverflow.com/questions/25163713/ternary-operator
        String fileName = whatIf.equals("win") ? "win.png" : "lose.png";
        try {
        	// this part with the getClass() was because the images were'nt loading.
        	// i copied this from stack overflow cause they had the same problem
        	// as me: https://stackoverflow.com/questions/66576669/imageio-cannot-find-file
            endImage = ImageIO.read(new File("src/Images/" + fileName));
        } catch (IOException e) {
            System.out.println("Error loading end image: " + e.getMessage());
        }

        Graphics g = getGraphics();
        g.drawImage(endImage, 0, 0, getWidth(), getHeight(), this);
        // i tried running it normally, but it wont work cause i have like 40 browser tabs
        //open so this thing clear the memory. kinda
        g.dispose();
    }
    
    //this part tracks the location of the treasure
    public static double trackTreasure(Point playerPos) {
    	Point treasurePos = treasure.getPos();
        int deltaX = playerPos.x - treasurePos.x;
        int deltaY = playerPos.y - treasurePos.y;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }
    



	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}


    //dig. i hate myself for how much suffering this caused for me.
    public void dig(Point pos) {
    	collectCoins();
    	collectBombs();
    	collectTreasure();
    }

}
