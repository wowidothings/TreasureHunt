import java.awt.event.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Player1 {

    // image that represents the player's position on the board
    private BufferedImage image;
    // current position of the player on the board grid. better than using 2 ints.
    private Point pos;
    // keep track of the player's money
    private int money;
    // keep track of the radar
    private int tracker;
    // keep track of HP
    private int hp;
    

    public Player1() {
        // load the image
        loadImage();

        // initialize the state
        pos = new Point(0, 0);
        money = 0;
        hp = 3;
    }

    private void loadImage() {
        try {
            // picture. if picture not work, no picture.
            image = ImageIO.read(new File("src/Images/player1.png"));
        } catch (IOException exc) {
            System.out.println("Error opening image file: " + exc.getMessage());
        }
    }

    public void draw(Graphics g, ImageObserver observer) {
        // https://stackoverflow.com/a/30220114/4655368
        // this is where translate board grid position into a canvas pixel
        // position by multiplying by the tile size.
        g.drawImage(
            image, 
            pos.x * Board.TILE_SIZE, 
            pos.y * Board.TILE_SIZE, 
            observer
        );
    }
    
    public void keyPressed(KeyEvent e) {
        // every keyboard get has a certain code. get the value of that code from the
        // keyboard event so that we can compare it to KeyEvent constants
        int key = e.getKeyCode();
        
        // depending on which arrow key was pressed, we're going to move the player by
        // one whole tile for this input
        if (key == KeyEvent.VK_W) {
            pos.translate(0, -1);
        }
        if (key == KeyEvent.VK_D) {
            pos.translate(1, 0);
        }
        if (key == KeyEvent.VK_S) {
            pos.translate(0, 1);
        }
        if (key == KeyEvent.VK_A) {
            pos.translate(-1, 0);
        }
        if (key == KeyEvent.VK_Q) {
            trackTreasure(pos);
        }
        if (key == KeyEvent.VK_E) {
        	//this part buys the tracker
            if(money<15) {
            	System.out.println("you don't have enough money");
            } else if(money>15) {
            	money-= 15;
            	tracker++;
            }
        }
        if (key == KeyEvent.VK_SPACE) {
            dig(pos);
        }
        
    }

    private void trackTreasure(Point pos2) {
		// TODO Auto-generated method stub
		System.out.println("The treasure is" + Board.trackTreasure(pos2) + "away");
	}
    
    private void dig(Point pos2) {
		Board board = new Board();
		// TODO Auto-generated method stub
		board.dig(pos2);
		
	}

	public void tick(long time) {
    	
        // this gets called once every tick, before the repainting process happens.
        //needed in here to update the player.

        // stop the player from moving off the edge of the board sideways
        if (pos.x < 0) {
            pos.x = 0;
        } else if (pos.x >= Board.COLUMNS) {
            pos.x = Board.COLUMNS - 1;
        }
        // stop the player from moving off the edge of the board vertically
        if (pos.y < 0) {
            pos.y = 0;
        } else if (pos.y >= Board.ROWS) {
            pos.y = Board.ROWS - 1;
        }
    }

    public String getmoney() {
        return String.valueOf(money);
    }

    public void addmoney(int amount) {
        money += amount;
    }
    
    public void takeHp(int amount) {
        hp -= amount;
    }
    
    public String getTracker() {
        return String.valueOf(tracker);
    }

    public void addTracker(int amount) {
        tracker += amount;
    }

    public Point getPos() {
        return pos;
    }
    
    public int getHp() {
    	return hp;
    }

}
