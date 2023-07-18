/** Created by Arman Atwal
 * Expanded upon a basic tutorial discussing the logic, to better understand GUI applications
 * I performed the exercise to understand design and game development.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Minesweeper implements ActionListener {
    JFrame frame; // Allows for the creation of our window, in which our game takes place
    JPanel textPanel; // Essentially a container for us to label our items
    int[][] solution; // Double dimensional array to store the solutions
    JPanel buttonPanel;
    JButton[][] buttons; // Creates a Double Dimension Array of JButtons
    boolean[][] flagged;
    JButton resetButton; // Allows for the user to reset the game and play again
    JButton flag;
    JLabel textfield;

    Random random; // Initializing reference variable for random class object

    int size; // The size of our grid. For example, 10 means 10 x 10
    int bombs; // The number of bombs in our game

    ArrayList<Integer> xPositions; // ArrayList containing xPosition of the bombs
    ArrayList<Integer> yPositions; // ArrayList containing yPosition of the bombs

    boolean flagging; // We will use this to see if the person is currently flagging a button
    int count = 0;
    int lastXchecked;
    int lastYchecked;
    int xZero; // Stores x position of the zero that is clicked, and around whom all the numbers
    // will have to be displayed
    int yZero;

    public Minesweeper() {

        xPositions = new ArrayList<Integer>(); // Declaring the xPosition ArrayList
        yPositions = new ArrayList<Integer>(); // Declaring the yPosition ArrayList

        size = 9;
        bombs = 10;

        lastXchecked = size + 1;
        lastYchecked = size + 1;

        flagged = new boolean[size][size]; // Allows the player to mark possible mines as we go through every button


        random = new Random(); // Declaring the Random class object

        for (int i = 0; i < bombs; i++) {
            xPositions.add(random.nextInt(size)); // Using a for loop to assign bomb with any xPosition in the grid
            yPositions.add(random.nextInt(size)); // Using a for loop to assign bomb with any yPosition in the grid
        }

        for (int i = 0; i < bombs; i++) { // If bombs are at same x and y coordinate, we must change that
            for (int j = i + 1; j < bombs; j++) {
                if(xPositions.get(i) == yPositions.get(j) && yPositions.get(i) == yPositions.get(j)) {
                    xPositions.set(j, random.nextInt(size));
                    yPositions.set(j, random.nextInt(size));

                    i = 0;
                    j = 0;
                }
            }
        }

        frame = new JFrame(); // Creation of JFrame object
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ensures the application closes when the close button
        // is pressed
        frame.setVisible(true); // Ensures our frame is visible and exists
        frame.setLayout(new BorderLayout()); // This allows an element to be in the north, south, east, west, or
        // center of our frame

        textPanel = new JPanel();
        textPanel.setVisible(true);
        textPanel.setBackground(Color.BLACK);

        buttonPanel = new JPanel();
        buttonPanel.setVisible(true);
        buttonPanel.setLayout(new GridLayout(size, size)); // Creates a grid of elements in the panel for the arguments

        textfield = new JLabel();
        textfield.setHorizontalAlignment(JLabel.CENTER);
        textfield.setFont(new Font("MV Boli", Font.BOLD, 20));
        textfield.setForeground(Color.BLUE);
        textfield.setText(bombs + " Bombs");

        resetButton = new JButton(); // Creates our object for the reset button
        resetButton.setForeground(Color.BLUE); // Makes reset button blue
        resetButton.setBackground(Color.WHITE); // Background is white
        resetButton.setText("Reset");
        resetButton.setFont(new Font("MV Boli", Font.BOLD, 20));
        resetButton.setFocusable(false);
        resetButton.addActionListener(this); // Allows for detection when clicking

        flag = new JButton(); // Creates our object for the flag button
        flag.setForeground(Color.ORANGE); // Makes flag button blue
        flag.setBackground(Color.WHITE); // Background is white
        flag.setText("|>");
        flag.setFont(new Font("MV Boli", Font.BOLD, 20));
        flag.setFocusable(false);
        flag.addActionListener(this); // Allows for detection when clicking

        solution = new int[size][size];
        buttons = new JButton[size][size];
        for(int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[0].length; j++) { // Going through each and every button
                buttons[i][j] = new JButton();
                buttons[i][j].setFocusable(false);
                buttons[i][j].setFont(new Font("MV Boli", Font.BOLD, 12));
                buttons[i][j].addActionListener(this);
                buttons[i][j].setText("");
                buttonPanel.add(buttons[i][j]);

            }
        }

        textPanel.add(textfield);
        frame.add(textPanel, BorderLayout.NORTH); // North of the frame
        frame.add(resetButton, BorderLayout.SOUTH); // South of the frame
        frame.add(buttonPanel);
        frame.add(flag, BorderLayout.WEST);

        frame.setSize(570, 570);
        frame.revalidate(); // Refreshes all components of GUi and display with assigned properties
        frame.setLocationRelativeTo(null); // Frame is in the center of the screen

        getSolution();
    }

    public void getSolution() {
        for(int y = 0; y < solution.length; y++) {
            for(int x = 0; x < solution[0].length; x++) {
                boolean changed = false;
                int bombsAround = 0;

                for(int i = 0; i < xPositions.size(); i++) {
                    if(x == xPositions.get(i) && y == yPositions.get(i)) {
                        solution[y][x] = size + 1; // Bombs with size of grid + 1
                        changed = true;
                    }
                }
                if (!changed) {
                    for(int i = 0; i < xPositions.size(); i++) {
                        if (x - 1 == xPositions.get(i) && y == yPositions.get(i)) // Now we are checking if the
                            // positions around the clicked button have a bomb or not, hence x-1.
                            bombsAround++;
                        if (x + 1 == xPositions.get(i) && y == yPositions.get(i)) // One position ahead
                            bombsAround++;
                        if (x == xPositions.get(i) && y - 1 == yPositions.get(i)) // One position above
                            bombsAround++;
                        if (x == xPositions.get(i) && y + 1 == yPositions.get(i)) // One position below
                            bombsAround++;
                        if (x + 1 == xPositions.get(i) && y + 1 == yPositions.get(i)) // Bottom Right
                            bombsAround++;
                        if (x - 1 == xPositions.get(i) && y - 1 == yPositions.get(i)) // Top Left
                            bombsAround++;
                        if (x - 1 == xPositions.get(i) && y + 1 == yPositions.get(i)) // Bottom Left
                            bombsAround++;
                        if (x + 1 == xPositions.get(i) && y - 1 == yPositions.get(i)) // Top Right
                            bombsAround++;
                    }
                    solution[y][x] = bombsAround;
                }
            }
        }
    }

    public void check(int y, int x) { // When a button is clicked, this displays what is at that position in
        // the solution array
        boolean over = false; // checks if game is over

        if(solution[y][x] == (size + 1)) {
            gameOver(false); // bomb click calls game over
            over = true;
        }

        getColor(y, x);

        if(!over) {
            if (solution[y][x] == 0) {
                xZero = x;
                yZero = y;
                count = 0;
                display();
            }
            else {
                buttons[y][x].setText(String.valueOf(solution[y][x]));
            }

            checkWinner();
        }
    }

    public void checkWinner() {
        int buttonsLeft = 0;

        for(int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[0].length; j++) {
                if(buttons[i][j].getText() == "" || buttons[i][j].getText() == "|>") //If the person, wins, buttons is
                    // zero, because none of the bombs
                    // were clicked.
                    buttonsLeft++;
            }
        }
        if(buttonsLeft == bombs) // If everything except bombs are clicked, we end the game
            gameOver(true);
    }

    public void gameOver(boolean won) {
        if(!won) {
            textfield.setForeground(Color.RED);
            textfield.setText("Game Over!");
        }
        else {
            textfield.setForeground(Color.GREEN);
            textfield.setText("You Win!");
        }

        for(int i = 0; i < buttons.length; i++) {
            for(int j = 0; j < buttons[0].length; j++) {
                buttons[i][j].setBackground(null);
                buttons[i][j].setEnabled(false); // disables all buttons

                // Revealing all the bombs
                for(int count = 0; count < xPositions.size(); count++) {
                    if(j == xPositions.get(count) && i == yPositions.get(count)) {
                        buttons[i][j].setBackground(Color.BLACK);
                        buttons[i][j].setText("*");
                    }
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==flag) {
            if(flagging) { // If the person was flagging then he is disabling flag, so it becomes white again
                flag.setBackground(Color.WHITE);
                flagging = false;
            }
            else {
                flag.setBackground(Color.RED);
                flagging = true;
            }
        }
        if(e.getSource() == resetButton) { // If reset button is clicked we will delete this frame and create a new one
            frame.dispose();
            new Minesweeper(); // By making an object, we have created a new frame after removing the original
        }
        for(int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[0].length; j++) {
                if(e.getSource() == buttons[i][j]) {
                    if(flagging && (buttons[i][j].getText() == "" || buttons[i][j].getText() == "|>")) {
                        if(flagged[i][j]) { // If flagged, it needs to become normal if clicked once more!
                            buttons[i][j].setText("");
                            buttons[i][j].setBackground(null);
                            flagged[i][j] = false;
                        }
                        else {
                            buttons[i][j].setText("|>");
                            buttons[i][j].setBackground(Color.RED);
                            buttons[i][j].setForeground(Color.ORANGE);
                            flagged[i][j] = true;
                        }
                    }
                    else {
                        if(!flagged[i][j]) { // Only shows value if the button is not flagged
                            check(i, j);
                        }
                    }
                }
            }
        }

    }

    public void display() { // This is the method that will use recursion to portray our adjacent zeroes
        /** So, whenever a button is clicked, if it's zero, we store the x and y in xZero and yZero, then we make count
        0. Thus, we enter the if statement block and this reveals everything around that specific 0. Then, count is
         greater than 1, and we enter the else block. In here, we iterate through every button, including the newly
         flipped ones. From there, any newly shown zeroes have surrounding numbers revealed as well. If a button
         has an empty space around it, that means it has revealed everything it can**/
        if(count < 1) { // This block will reveal everything around the zeroes.
            if((xZero - 1 >= 0)) {
                getColor(yZero, xZero - 1);
            }
            if((xZero + 1 < size)) {
                getColor(yZero, xZero + 1);
            }
            if((yZero - 1 >= 0)) {
                getColor(yZero - 1, xZero);
            }
            if((yZero + 1 < size)) {
                getColor(yZero + 1, xZero);
            }
            if((yZero - 1 >= 0) && (xZero - 1 >= 0)) {
                getColor(yZero - 1, xZero - 1);
            }
            if((yZero + 1 < size) && (xZero + 1 < size)) {
                getColor(yZero + 1, xZero + 1);
            }
            if((yZero - 1 >= 0) && (xZero + 1 < size)) {
                getColor(yZero - 1, xZero + 1);
            }
            if((yZero + 1 < size) && (xZero - 1 >= 0)) {
                getColor(yZero + 1, xZero - 1);
            }

            count++;
            display();
        }
        else { // This will check if anymore zeroes are left to be revealed
            // Here we iterate through all the buttons
            for(int y = 0; y < buttons.length; y++) {
                for(int x = 0; x < buttons[0].length; x++) {
                    if(buttons[y][x].getText().equals("0")) { // If the button is a zero and has an empty button around
                        if(y - 1 >= 0){
                           if(buttons[y - 1][x].getText().equals("") || buttons[y - 1][x].getText().equals("|>")) {
                               lastXchecked = x;
                               lastYchecked = y;
                           }
                        }
                        if(x + 1 < size){
                            if(buttons[y][x + 1].getText().equals("") || buttons[y][x + 1].getText().equals("|>")) {
                                lastXchecked = x;
                                lastYchecked = y;
                            }
                        }
                        if(x - 1 >= 0){
                            if(buttons[y][x - 1].getText().equals("") || buttons[y][x - 1].getText().equals("|>")) {
                                lastXchecked = x;
                                lastYchecked = y;
                            }
                        }
                        if(x - 1 >= 0 && y - 1 >= 0){
                            if(buttons[y - 1][x - 1].getText().equals("") || buttons[y - 1][x - 1].getText().equals("|>")) {
                                lastXchecked = x;
                                lastYchecked = y;
                            }
                        }
                        if(x + 1 < size && y + 1 < size){
                            /**
                             * if our button is empty meaning not revealed, or still flagged, then we change the values
                             * of lastXchecked and lastYchecked.
                             */
                            if(buttons[y + 1][x + 1].getText().equals("") || buttons[y + 1][x + 1].getText().equals("|>")) {
                                lastXchecked = x;
                                lastYchecked = y;
                            }
                        }
                        if(x - 1 >= 0 && y + 1 < size){
                            if(buttons[y + 1][x - 1].getText().equals("") || buttons[y + 1][x - 1].getText().equals("|>")) {
                                lastXchecked = x;
                                lastYchecked = y;
                            }
                        }
                        if(x + 1 < size && y - 1 >= 0){
                            if(buttons[y - 1][x + 1].getText().equals("") || buttons[y - 1][x + 1].getText().equals("|>")) {
                                lastXchecked = x;
                                lastYchecked = y;
                            }
                        }
                    }
                }
            }
            /**
             * Here, having changed the values of lastXchecked and lastYchecked, from their original, it gives use
             * the coordinate in the grid which needs its surroundings revealed. We reset count to 0
             * and make the recursive call.
             */
            if(lastXchecked < size + 1 && lastYchecked < size + 1) {
                xZero = lastXchecked;
                yZero = lastYchecked;

                count = 0;

                lastYchecked = size + 1;
                lastXchecked = size + 1;

                display();
            }
        }
    }

    public void getColor(int y, int x) {
        if(solution[y][x] == 0) {
            buttons[y][x].setEnabled(false);
        }
        if(solution[y][x] == 1) {
            buttons[y][x].setForeground(Color.BLUE);
        }
        if(solution[y][x] == 2) {
            buttons[y][x].setForeground(Color.GREEN);
        }
        if(solution[y][x] == 3) {
            buttons[y][x].setForeground(Color.RED);
        }
        if(solution[y][x] == 4) {
            buttons[y][x].setForeground(Color.MAGENTA);
        }
        if(solution[y][x] == 5) {
            buttons[y][x].setForeground(new Color(128, 0, 128));
        }
        if(solution[y][x] == 6) {
            buttons[y][x].setForeground(Color.CYAN);
        }
        if(solution[y][x] == 7) {
            buttons[y][x].setForeground(new Color(42, 13, 93));
        }
        if(solution[y][x] == 8) {
            buttons[y][x].setForeground(Color.lightGray);
        }

        buttons[y][x].setBackground(null);
        buttons[y][x].setText(String.valueOf(solution[y][x]));
    }

}
