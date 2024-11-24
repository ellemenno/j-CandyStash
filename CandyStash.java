import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class CandyStash {
// TODO: add colors arrays and composite when printing

/**
Candy Stash
 - player vs 'ai'
 - 50 x 25 text-mode UI

         1         2         3         4         5
12345678901234567890123456789012345678901234567890
                              candy stash         1
p1 123456789   p2 123456789                       2
 a .........    a /..//////   X bite              3
 b .L.CC....    b /X///////   / unknown           4
 c .L.CC....    c /////////   . empty             5
 d .L.CCMM..    d /X.XXX///   L licorice rope 1x4 6
 e .L.......    e /////////   C chocolate bar 2x3 7
 f .........    f /////////   M marshmallow   2x1 8
                                                  9
p1 guess: _                                       0 1
               p2 guess: _                        1

 */
  private static final char BLANK = ' ';
  private static final char X = 'X'; // bite
  private static final char U = '/'; // unknown
  private static final char E = '.'; // empty
  private static final char L = 'L'; // licorice rope 1x4
  private static final char C = 'C'; // chocolate bar 2x3
  private static final char M = 'M'; // marshmallow   2x1
  private static final int TOTAL_BITES = 12; // 1x4 + 2x3 + 2x1
  private static final int ROWS = 6; // board rows for one player
  private static final int COLS = 9; // board cols for one player
  private static final int SPACE = 3;// width of y axis labels and padding between boards
  private static final int BOARD_SCRN_COLS = SPACE+COLS+SPACE; // screen columns needed for one players board, labels, and padding
  private static final int SCREEN_ROWS = ROWS+3; // total screen rows
  private static final int SCREEN_COLS = 2*(BOARD_SCRN_COLS)+20; // total screen columns
  private static final String[] BUMMERS = { "aww", "hey", "dang", "shoot", "bummer" };
  private static final String[] FIND_VERBS = { "ate", "got", "found", "stole", "swiped" };
  private static final String[] PICK_VERBS = { "peeks at", "guesses", "selects", "peers into", "chooses", "picks", "likes", "decides on", "goes for", "takes" };

  public static void main(String[] args) {
    int seed = (int) Math.round(Math.random() * 99999);
    Random prng = new Random(seed);

    int[] p1Bites = { 0, 0, 0 };
    int[] p2Bites = { 0, 0, 0 };
    char[][] p1 = new char[ROWS][COLS];
    char[][] p2 = new char[ROWS][COLS];
    boolean[][] p1Peeks = new boolean[ROWS][COLS];
    boolean[][] p2Peeks = new boolean[ROWS][COLS];
    StringBuffer[] screen = new StringBuffer[SCREEN_ROWS];

    setNewBoard(screen, p1, p2, p1Peeks, p2Peeks, prng);

    Scanner scanner = new Scanner(System.in);
    boolean playing = true, p1Turn = true;
    String endGameMessage = "bye.";
    String moveMessage = "";
    while (playing) {
      setBoard(screen, p1, p1Peeks, false, SPACE);
      setBoard(screen, p2, p2Peeks, true, SPACE+BOARD_SCRN_COLS);
      if (p1Turn) {
        boolean validMove = false;
        while (!validMove && playing) {
          renderScreen(screen, p1, p2);
          String p1Move = getP1Move(scanner);
          if (p1Move.equals("q") || p1Move.equals("quit")) { validMove = true; playing = false; }
          else {
            validMove = isValidMove(p1Move);
            if (validMove) { moveMessage = applyMove(p1Move, p2Peeks, p2, p2Bites, prng); }
            else { setMessage("invalid move. try again.", scanner); }
          }
        }
      }
      else {
        renderScreen(screen, p1, p2);
        applyMove(getP2Move(p1Peeks, p1, prng, scanner), p1Peeks, p1, p1Bites, prng);
      }
      p1Turn = !p1Turn;
      int p1Score = countBites(p2, p2Peeks);
      int p2Score = countBites(p1, p1Peeks);
      setScore(screen[0], p1Score, 0);
      setScore(screen[0], p2Score, 15);
      if (moveMessage.length() > 0) { setMessage(moveMessage, scanner); moveMessage = ""; }
      if (p1Score == TOTAL_BITES) { playing = false; endGameMessage = "player 1 wins. well done!"; }
      if (p2Score == TOTAL_BITES) { playing = false; endGameMessage = "player 2 wins. better luck next time!"; }
    }
    setBoard(screen, p1, p1Peeks, false, SPACE);
    setBoard(screen, p2, p2Peeks, true, SPACE+BOARD_SCRN_COLS);
    renderScreen(screen, p1, p2);
    System.out.println(endGameMessage);
  }

  private static int countBites(char[][] board, boolean[][] peeks) {
    int rows = peeks.length, cols = peeks[0].length, score = 0;
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        if (peeks[r][c] == true && board[r][c] != E) { score++; }
      }
    }
    return score;
  }

  private static boolean isValidMove(String move) {
    if (move.length() != 2) { return false; }
    // extract and validate r and c
    int r = "abcdef".indexOf(move.charAt(0));
    if (r < 0) { return false; }
    int c = Character.digit(move.charAt(1), 10) - 1;
    if (c < 0 || c >= 9) { return false; }
    return true;
  }

  private static int moveRow(String move) {
    return "abcdef".indexOf(move.charAt(0));
  }

  private static int moveCol(String move) {
    return Character.digit(move.charAt(1), 10) - 1;
  }

  private static String applyMove(String move, boolean[][] peeks, char[][] board, int[] bites, Random prng) {
    String candy = "";
    int r = moveRow(move), c = moveCol(move);
    // mark this cell as seen and record any bites
    peeks[r][c] = true;
    char bc =  board[r][c];
    int IL = 0, IC = 1, IM = 2;
    switch (bc) {
      case L: bites[IL]++; if (bites[IL] == 4) { candy = "licorice"; } break;
      case C: bites[IC]++; if (bites[IC] == 6) { candy = "chocolate bar"; } break;
      case M: bites[IM]++; if (bites[IM] == 2) { candy = "marshmallow"; } break;
    }
    return candy.length() > 0 ? String.format("%s, you %s my %s!", bummer(prng), find(prng), candy) : candy;
  }

  private static String bummer(Random r) {
    return BUMMERS[r.nextInt(BUMMERS.length)];
  }

  private static String pick(Random r) {
    return PICK_VERBS[r.nextInt(PICK_VERBS.length)];
  }

  private static String find(Random r) {
    return FIND_VERBS[r.nextInt(FIND_VERBS.length)];
  }

  private static String getP2Move(boolean[][] peeks, char[][] board, Random prng, Scanner scanner) {
    List<String> guessOk = new ArrayList<>();
    List<String> guessBetter = new ArrayList<>();
    int rows = peeks.length, cols = peeks[0].length;
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        if (peeks[r][c] == false) { guessOk.add(String.format("%c%d", Character.forDigit(r+10, 16), c+1)); }
      }
    }
    for (String g : guessOk) {
      int r = moveRow(g), c = moveCol(g);
      if (r+1 < rows && peeks[r+1][c]) { if (board[r+1][c] != E) { guessBetter.add(g); } }
      if (r-1 >= 0   && peeks[r-1][c]) { if (board[r-1][c] != E) { guessBetter.add(g); } }
      if (c+1 < cols && peeks[r][c+1]) { if (board[r][c+1] != E) { guessBetter.add(g); } }
      if (c-1 >= 0   && peeks[r][c-1]) { if (board[r][c-1] != E) { guessBetter.add(g); } }
    }
    int gg = guessBetter.size(), g = guessOk.size();
    String move = (gg > 0) ? guessBetter.get(prng.nextInt(gg)) : guessOk.get(prng.nextInt(g));
    String indent = "              ";
    System.out.format("%s p2 %s %s%n", indent, pick(prng), move);
    setMessage(indent, scanner);
    return move;
  }

  private static String getP1Move(Scanner scanner) {
    System.out.print("p1 guess: ");
    return scanner.nextLine().toLowerCase();
  }

  private static String setMessage(String msg, Scanner scanner) {
    System.out.format("%s (continue) ", msg);
    return scanner.nextLine();
  }

  private static void setNewBoard(StringBuffer[] screen, char[][] player1, char[][] player2, boolean[][] peeks1, boolean[][] peeks2, Random prng) {
    fillScreen(screen, SCREEN_COLS, BLANK);
    fillBoard(player1, E);
    fillBoard(player2, E);

    placeAllPieces(player1, prng);
    placeAllPieces(player2, prng);
    setPeeks(peeks1, false);
    setPeeks(peeks2, false);

    setScore(screen[0], 0, 0);
    setScore(screen[0], 0, 15);
    setTitle(screen[0], 2*BOARD_SCRN_COLS);
    setAxes(screen, 1, COLS, ROWS, SPACE);
    setAxes(screen, 2, COLS, ROWS, SPACE);
    setLegend(screen, 2*BOARD_SCRN_COLS);
  }

  private static void fillScreen(StringBuffer[] screen, int cols, char fc) {
    int rows = screen.length;
    for (int r = 0; r < rows; r++) {
      StringBuffer sb = new StringBuffer();
      for (int c = 0; c < cols; c++) { sb.append(fc); }
      screen[r] = sb;
    }
  }

  private static void fillBoard(char[][] board, char fc) {
    int rows = board.length, cols = board[0].length;
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) { board[r][c] = fc; }
    }
  }

  private static void flipBoardH(char[][] board) {
    int rows = board.length;
    for (int r = 0; r < rows; r++) {
      char[] row = board[r];
      int n = row.length;
      for (int i = 0; i < n/2; i++) {
        char t = row[i];
        row[i] = row[n-1-i];
        row[n-1-i] = t;
      }
    }
  }

  private static void flipBoardV(char[][] board) {
    int cols = board[0].length;
    for (int c = 0; c < cols; c++) {
      int n = board.length;
      for (int i = 0; i < n/2; i++) {
        char t = board[i][c];
        board[i][c] = board[n-1-i][c];
        board[n-1-i][c] = t;
      }
    }
  }

  private static int randInt(int lo, int hi, Random r) {
    return lo + r.nextInt(hi - lo);
  }

  private static void placePiece(char pc, int[] pDim, boolean canRotate, int[] bounds, char[][] grid, Random prng) {
    int LEFT = 0, TOP = 1, RIGHT = 2, BOTTOM = 3;
    int W = 0, H = 1;
    int[] dim = { pDim[W], pDim[H] };
    if (canRotate && prng.nextBoolean()) { dim[W] = pDim[H]; dim[H] = pDim[W]; }
    int rt = bounds[TOP], rb = bounds[BOTTOM]-dim[H];
    int cl = bounds[LEFT], cr = bounds[RIGHT]-dim[W];
    int r0 = (rb - rt) > 0 ? randInt(rt, rb, prng) : rt;
    int c0 = (cr - cl) > 0 ? randInt(cl, cr, prng) : cl;
    for (int r = 0; r < dim[H]; r++) {
      for (int c = 0; c < dim[W]; c++) { grid[r0+r][c0+c] = pc; }
    }
  }

  private static void placeAllPieces(char[][] board, Random r) {
    int rows = board.length, cols = board[0].length;
    int W = 0, H = 1;
    int[] dimL = { 1, 4 };
    int[] dimC = { 2, 3 };
    int[] dimM = { 1, 2 };
    int LEFT = 0, TOP = 1, RIGHT = 2, BOTTOM = 3;
    int[] boxL = new int[4];
    int[] boxC = new int[4];
    int[] boxM = new int[4];
    boolean hNotV = r.nextBoolean();
    //   hNotV      !hNotV
    //   _______    _______
    //  |___a___|  |  | b  |
    //  |b |  c |  | a|____|
    //  |__|____|  |__|_c__|
    //
    if (hNotV) {
      int t = dimL[0]; dimL[0] = dimL[1]; dimL[1] = t;
      boxL[LEFT] = 0; boxL[TOP] = 0; boxL[RIGHT] = cols; boxL[BOTTOM] = randInt(dimL[H], rows-dimC[H], r);
      boxC[LEFT] = 0; boxC[TOP] = boxL[BOTTOM]; boxC[RIGHT] = randInt(dimC[H], cols-dimM[H], r); boxC[BOTTOM] = rows;
      boxM[LEFT] = boxC[RIGHT]; boxM[TOP] = boxL[BOTTOM]; boxM[RIGHT] = cols; boxM[BOTTOM] = rows;
    }
    else {
      boxL[LEFT] = 0; boxL[TOP] = 0; boxL[RIGHT] = randInt(dimL[W], cols-dimC[H], r); boxL[BOTTOM] = rows;
      boxC[LEFT] = boxL[RIGHT]; boxC[TOP] = 0; boxC[RIGHT] = cols; boxC[BOTTOM] = randInt(dimC[H], rows-dimM[H], r);
      boxM[LEFT] = boxL[RIGHT]; boxM[TOP] = boxC[BOTTOM]; boxM[RIGHT] = cols; boxM[BOTTOM] = rows;
    }

    placePiece(L, dimL, false, boxL, board, r);
    placePiece(C, dimC, true, boxC, board, r);
    placePiece(M, dimM, true, boxM, board, r);

    if (r.nextBoolean()) { flipBoardV(board); }
    if (r.nextBoolean()) { flipBoardH(board); }
  }

  private static void setPeeks(boolean[][] peeks, boolean v) {
    int rows = peeks.length, cols = peeks[0].length;
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) { peeks[r][c] = v; }
    }
  }

  private static void setText(StringBuffer sb, String s, int i) {
    int n = s.length();
    if (i+n >= sb.length()) { return; }
    for (int j = 0; j < n; j++) { sb.setCharAt(i+j, s.charAt(j)); }
  }

  private static void setTitle(StringBuffer sb, int i) {
    setText(sb, "candy stash", i);
  }

  private static void setScore(StringBuffer sb, int score, int i) {
    setText(sb, String.format("%2d", score), i);
  }

  private static void setAxes(StringBuffer[] screen, int p, int cols, int rows, int space) {
    int i, c0 = (p-1) * (2*space+cols);
    StringBuffer h = new StringBuffer();
    h.append('p');
    h.append( p );
    h.append(' ');
    for (i = 0; i < cols; i++) { h.append(Character.forDigit(i+1, 10)); }
    for (i = 0; i < space; i++) { h.append(' '); }
    setText(screen[1], h.toString(), c0);
    for (i = 0; i < rows; i++) { screen[2+i].setCharAt(c0+1, Character.forDigit(i+9+1, 16)); }
  }

  private static void setLegend(StringBuffer[] screen, int i) {
    setText(screen[2], "X bite             ", i);
    setText(screen[3], "/ unknown          ", i);
    setText(screen[4], ". empty            ", i);
    setText(screen[5], "L licorice rope 1x4", i);
    setText(screen[6], "C chocolate bar 2x3", i);
    setText(screen[7], "M marshmallow   2x1", i);
  }

  private static char paintedCell(char c, boolean isOpponent, boolean isSeen) {
    //return c;
    char pc = U;
    if (isOpponent) {
      if (isSeen) { pc = (c == E) ? E : X; }
      else { pc = U; }
    }
    else {
      switch (c) {
        case E: pc = isSeen ? ':' : '.'; break;
        case L: pc = isSeen ? 'L' : 'l'; break;
        case C: pc = isSeen ? 'C' : 'c'; break;
        case M: pc = isSeen ? 'M' : 'm'; break;
      }
    }
    return pc;
  }

  private static void setBoard(StringBuffer[] screen, char[][] player, boolean[][] peeks, boolean isOpponent, int i) {
    int rows = player.length, cols = player[0].length;
    for (int r = 0; r < rows; r++) {
      StringBuffer sb = new StringBuffer();
      for (int c = 0; c < cols; c++) {
        sb.append(paintedCell(player[r][c], isOpponent, peeks[r][c]));
      }
      setText(screen[2+r], sb.toString(), i);
    }
  }

  private static void clearScreen() { System.out.print("\u001Bc"); }

  private static void renderScreen(StringBuffer[] screen, char[][] p1, char[][] p2) {
    clearScreen();
    for (StringBuffer sb : screen) { System.out.println(sb); }
  }
}