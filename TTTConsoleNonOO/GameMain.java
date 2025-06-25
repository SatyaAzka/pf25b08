import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameMain extends JPanel {
   private static final long serialVersionUID = 1L;

   public static final String TITLE = "Tic Tac Toe";
   public static final Color COLOR_BG = Color.WHITE;
   public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
   public static final Color COLOR_CROSS = new Color(239, 105, 80);
   public static final Color COLOR_NOUGHT = new Color(64, 154, 225);
   public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

   public static final int ROWS = 3;
   public static final int COLS = 3;

   private Board board;
   private State currentState;
   private Seed currentPlayer;
   private JLabel statusBar;

   private boolean vsAI = false;
   private AIPlayer aiPlayer;
   private Image backgroundImage;
   private SoundEffect currentBackgroundMusic;

   public GameMain(boolean vsAI, AIPlayer.Difficulty difficulty) {
      this.vsAI = vsAI;

      if (!vsAI) {
         backgroundImage = Seed.Background2P.getImage();
         currentBackgroundMusic = SoundEffect.BG2P;
      } else {
         if (difficulty == AIPlayer.Difficulty.HARD) {
            backgroundImage = Seed.BackgroundHard.getImage();
            currentBackgroundMusic = SoundEffect.BGHard;
         } else {
            backgroundImage = Seed.BackgroundEasy.getImage();
            currentBackgroundMusic = SoundEffect.BGEasy;
         }
      }

      currentBackgroundMusic.loop();

      addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            int mouseX = e.getX();
            int mouseY = e.getY();
            int row = mouseY / Cell.SIZE;
            int col = mouseX / Cell.SIZE;

            if (currentState == State.PLAYING) {
               if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                       && board.cells[row][col].content == Seed.NO_SEED) {
                  currentState = board.stepGame(currentPlayer, row, col);
                  playSound();

                  if (currentState == State.PLAYING) {
                     currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;

                     if (vsAI && currentPlayer == Seed.NOUGHT) {
                        int[] aiMove = aiPlayer.move();
                        if (aiMove[0] != -1) {
                           currentState = board.stepGame(Seed.NOUGHT, aiMove[0], aiMove[1]);
                           playSound();
                           currentPlayer = Seed.CROSS;
                        }
                     }
                  }
               }
            } else {
               newGame();
            }

            repaint();
         }
      });

      statusBar = new JLabel();
      statusBar.setFont(FONT_STATUS);
      statusBar.setBackground(COLOR_BG_STATUS);
      statusBar.setOpaque(true);
      statusBar.setPreferredSize(new Dimension(300, 30));
      statusBar.setHorizontalAlignment(JLabel.LEFT);
      statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

      JButton volumeButton = new JButton("🔊 Volume");
      volumeButton.addActionListener(e -> showVolumeSettings());

      JPanel bottomPanel = new JPanel(new BorderLayout());
      bottomPanel.add(statusBar, BorderLayout.CENTER);
      bottomPanel.add(volumeButton, BorderLayout.EAST);

      setLayout(new BorderLayout());
      add(bottomPanel, BorderLayout.PAGE_END);
      setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 30));
      setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

      initGame(difficulty);
      newGame();
   }

   private void initGame(AIPlayer.Difficulty difficulty) {
      board = new Board();
      if (vsAI) {
         aiPlayer = new AIPlayer(board, difficulty);
         aiPlayer.setSeed(Seed.NOUGHT);
      }
   }

   private void newGame() {
      for (int row = 0; row < Board.ROWS; ++row) {
         for (int col = 0; col < Board.COLS; ++col) {
            board.cells[row][col].content = Seed.NO_SEED;
         }
      }
      currentPlayer = Seed.CROSS;
      currentState = State.PLAYING;
   }

   public void playSound() {
      if (currentState == State.NOUGHT_WON) {
         SoundEffect.LOSE.play();
      } else if (currentState == State.DRAW) {
         SoundEffect.DRAW.play();
      } else if (currentState == State.CROSS_WON){
         SoundEffect.WIN.play();
      } else {
         SoundEffect.PLAY.play();
      }
   }

   private void showVolumeSettings() {
      String[] volumeOptions = {"Mute", "Low", "Medium", "High"};
      int choice = JOptionPane.showOptionDialog(this,
              "Select Volume Level:",
              "Volume Settings",
              JOptionPane.DEFAULT_OPTION,
              JOptionPane.QUESTION_MESSAGE,
              null,
              volumeOptions,
              volumeOptions[2]);

      SoundSettings.Volume selectedVolume = switch (choice) {
         case 0 -> SoundSettings.Volume.MUTE;
         case 1 -> SoundSettings.Volume.LOW;
         case 2 -> SoundSettings.Volume.MEDIUM;
         case 3 -> SoundSettings.Volume.HIGH;
         default -> SoundSettings.volume;
      };

      SoundEffect.setGlobalVolume(selectedVolume);
      currentBackgroundMusic.applyVolume();
   }

   @Override
   public void paintComponent(Graphics g) {
      super.paintComponent(g);

      if (backgroundImage != null) {
         g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
      } else {
         setBackground(COLOR_BG);
      }

      board.paint(g);

      if (currentState == State.PLAYING) {
         statusBar.setForeground(Color.BLACK);
         statusBar.setText((currentPlayer == Seed.CROSS) ? "Player 1's Turn" : "Player 2's Turn");
      } else if (currentState == State.DRAW) {
         statusBar.setForeground(Color.RED);
         statusBar.setText("It's a Draw! Click to play again.");
      } else if (currentState == State.CROSS_WON) {
         statusBar.setForeground(Color.RED);
         statusBar.setText("'Player 1' Won! Click to play again.");
      } else if (currentState == State.NOUGHT_WON) {
         statusBar.setForeground(Color.RED);
         statusBar.setText("'Player 2' Won! Click to play again.");
      }
   }

   public static void main(String[] args) {
      SwingUtilities.invokeLater(() -> {
         String[] modeOptions = {"2 Player", "VS AI"};
         int modeChoice = JOptionPane.showOptionDialog(null, "Choose Game Mode",
                 "Tic Tac Toe Mode", JOptionPane.DEFAULT_OPTION,
                 JOptionPane.INFORMATION_MESSAGE, null, modeOptions, modeOptions[0]);

         boolean vsAI = (modeChoice == 1);
         AIPlayer.Difficulty difficulty = AIPlayer.Difficulty.EASY;

         if (vsAI) {
            String[] difficultyOptions = {"Easy", "Hard"};
            int diffChoice = JOptionPane.showOptionDialog(null, "Select Difficulty",
                    "Difficulty", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, difficultyOptions, difficultyOptions[0]);
            difficulty = (diffChoice == 1) ? AIPlayer.Difficulty.HARD : AIPlayer.Difficulty.EASY;
         }

         GameMain gamePanel = new GameMain(vsAI, difficulty);

         JFrame frame = new JFrame(TITLE);
         frame.setContentPane(gamePanel);
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         frame.pack();
         frame.setLocationRelativeTo(null);
         frame.setVisible(true);

         frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
               SoundEffect.BG2P.stop();
               SoundEffect.BGEasy.stop();
               SoundEffect.BGHard.stop();
            }
         });
      });
   }
}