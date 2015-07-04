package com.example.supreme.tictactoe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class TicTacToeView extends View {
    private Paint background_;
    private Paint gridBars_;
    private Paint pieceX_;
    private Paint pieceO_;
    private Paint gameOverText_;

    private int startX_;
    private int startY_;
    private int width_;
    private int height_;

    public enum Player {
        FIRST, SECOND, INVALID;
        private int playerEnum;
        public String toString() {
            if (this == FIRST) return "X";
            else if (this == SECOND) return "O";
            else return "?";
        }
    }
    Player playerTurn_ = Player.FIRST;

    private int board_[] = new int[9];

    public TicTacToeView(Context context) {
        super(context);

        background_ = new Paint(Paint.ANTI_ALIAS_FLAG);
        background_.setColor(0xffffffff);

        gridBars_ = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridBars_.setColor(0xff000000);
        gridBars_.setStrokeWidth(10.0f);

        pieceX_ = new Paint(Paint.ANTI_ALIAS_FLAG);
        pieceX_.setColor(0xffef2327);
        pieceX_.setStyle(Paint.Style.STROKE);
        pieceX_.setStrokeWidth(10.0f);

        pieceO_ = new Paint(Paint.ANTI_ALIAS_FLAG);
        pieceO_.setColor(0xff1c63ef);
        pieceO_.setStyle(Paint.Style.STROKE);
        pieceO_.setStrokeWidth(10.0f);

        gameOverText_ = new Paint(Paint.ANTI_ALIAS_FLAG);
        gameOverText_.setTextSize(150);
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(startX_, startY_, startX_ + width_, startY_ + height_, background_);

        int blockW = width_ / 3;
        int blockH = height_ / 3;
        for (int i = 0; i < 2; i++) {
            int lineY = startY_ + (i + 1) * blockH;
            canvas.drawLine(startX_, lineY, startX_ + width_, lineY, gridBars_);

            int lineX = startX_ + (i + 1) * blockW;
            canvas.drawLine(lineX, startY_, lineX, startY_ + height_, gridBars_);
        }

        for (int i = 0; i < board_.length; i++) {
            int r = i / 3;
            int c = i % 3;
            Player player = boardPieceToPlayer(board_[i]);
            if (player != Player.INVALID) {
                if (player == Player.FIRST) {
                    float sideLength = blockW * 0.6f / ((float) Math.sqrt(2));
                    float left = c * blockW + startX_ + blockW / 2 - sideLength / 2;
                    float top = r * blockH + startY_ + blockH / 2 - sideLength / 2;
                    canvas.drawLine(left, top, left + sideLength, top + sideLength, pieceX_);
                    canvas.drawLine(left + sideLength, top, left, top + sideLength, pieceX_);
                } else if (player == Player.SECOND) {
                    canvas.drawCircle(c * blockW + startX_ + blockW / 2,
                                      r * blockH + startY_ + blockH / 2,
                                      blockW * 0.3f,
                                      pieceO_);
                }
            }
        }

        if (gameOver()) {
            String gameOver = gameOverString();

            gameOverText_.setStyle(Paint.Style.STROKE);
            gameOverText_.setColor(0xff3c3d3c);
            gameOverText_.setStrokeWidth(10.0f);
            Rect textBounds = new Rect();
            gameOverText_.getTextBounds(gameOver, 0, gameOver.length(), textBounds);
            canvas.drawText(gameOver, 0, gameOver.length(), startX_ + width_ / 2 - textBounds.exactCenterX(),
                            startY_ + height_ / 2 - textBounds.exactCenterY(),
                            gameOverText_);

            gameOverText_.setStyle(Paint.Style.FILL);
            gameOverText_.setColor(0xff43e041);
            gameOverText_.getTextBounds(gameOver, 0, gameOver.length(), textBounds);
            canvas.drawText(gameOver, 0, gameOver.length(), startX_ + width_ / 2 - textBounds.exactCenterX(),
                            startY_ + height_ / 2 - textBounds.exactCenterY(),
                            gameOverText_);
        }
    }

    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        startX_ = getPaddingLeft();
        startY_ = getPaddingTop();
        width_ = w - startX_ - getPaddingRight();
        height_ = h - startY_ - getPaddingBottom();
        if (width_ < height_) {
            startY_ += (height_ - width_) / 2;
            height_ = width_;
        } else if (height_ < width_) {
            startX_ += (width_ - height_) / 2;
            width_ = height_;
        }
        invalidate();
    }

    static Player boardPieceToPlayer(int piece) {
        if (piece == 1) return Player.FIRST;
        else if (piece == 2) return Player.SECOND;
        else return Player.INVALID;
    }

    static boolean gameTied(int board[]) {
        for (int piece : board)
            if (piece == 0)
                return false;
        return true;
    }

    public boolean gameTied() {
        return gameTied(board_);
    }

    public boolean gameOver() {
        return gameTied() || winner() != Player.INVALID;
    }

    public String gameOverString() {
        if (winner() == Player.FIRST)
            return "X wins";
        if (winner() == Player.SECOND)
            return "O wins";
        if (gameTied())
            return "Tied";
        return "Game not over";
    }

    static Player winner(int board[]) {
        int winWays[][] = {
                {0, 1, 2},
                {3, 4, 5},
                {6, 7, 8},
                {0, 3, 6},
                {1, 4, 7},
                {2, 5, 8},
                {0, 4, 8},
                {2, 4, 6}
        };

        for (int winWay[] : winWays) {
            boolean foundWinner = board[winWay[0]] != 0;
            foundWinner = foundWinner && board[winWay[0]] == board[winWay[1]];
            foundWinner = foundWinner && board[winWay[1]] == board[winWay[2]];
            if (foundWinner) {
                return boardPieceToPlayer(board[winWay[0]]);
            }
        }

        return Player.INVALID;
    }

    public Player winner() {
        return winner(board_);
    }

    enum GameResult { WIN, LOSE, TIE };

    public static class MoveResult {
        public int move;
        public GameResult result;
        public MoveResult(int move, GameResult result) {
            reset(move, result);
        }
        public void reset(int move, GameResult result) {
            this.move = move;
            this.result = result;
        }
    }

    static MoveResult bestMoveRecursive(int board[], Player player) {
        Player nextPlayer;
        int playerNum;
        if (player == Player.FIRST) {
            nextPlayer = Player.SECOND;
            playerNum = 1;
        } else {
            nextPlayer = Player.FIRST;
            playerNum = 2;
        }
        MoveResult bestMoveResult = new MoveResult(-1, GameResult.LOSE);
        for (int cell = 0; cell < board.length; cell++) {
            if (board[cell] != 0) continue;

            board[cell] = playerNum;
            Player winner = winner(board);
            if (winner != Player.INVALID) {
                bestMoveResult.reset(cell, GameResult.WIN);
                board[cell] = 0;
                break;
            } else if (gameTied(board)) {
                bestMoveResult.reset(cell, GameResult.TIE);
                board[cell] = 0;
            } else {
                MoveResult moveResult = bestMoveRecursive(board, nextPlayer);
                board[cell] = 0;
                if (moveResult.result == GameResult.LOSE) {
                    bestMoveResult.reset(cell, GameResult.WIN);
                    break;
                } else if (moveResult.result == GameResult.TIE) {
                    bestMoveResult.reset(cell, GameResult.TIE);
                }
            }
        }
        return bestMoveResult;
    }

    public int bestMove() {
        final int boardCopy[] = board_.clone();
        final int move[] = new int[1];

        ThreadGroup group = new ThreadGroup("tic-tac-toe-worker-group");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                move[0] = bestMoveRecursive(boardCopy, playerTurn_).move;
            }
        };

        Thread thread = new Thread(group, runnable, "tic-tac-toe-worker", 2000000);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
        }

        return move[0];
    }

    public void startOver() {
        playerTurn_ = Player.FIRST;
        board_ = new int[9];
        invalidate();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP)
            return true;

        if (gameOver()) {
            startOver();
            return true;
        }

        if (event.getX() < startX_ || event.getX() > startX_ + width_)
            return true;
        if (event.getY() < startY_ || event.getY() > startY_ + height_)
            return true;

        int col = (int)(((event.getX() - startX_) / width_) * 3);
        int row = (int)(((event.getY() - startY_) / height_) * 3);
        int boardIndex = row * 3 + col;
        if (board_[boardIndex] == 0) {
            if (playerTurn_ == Player.FIRST) {
                board_[boardIndex] = 1;
                playerTurn_ = Player.SECOND;
            } else if (playerTurn_ == Player.SECOND) {
                board_[boardIndex] = 2;
                playerTurn_ = Player.FIRST;
            }

            if (!gameOver()) {
                int move = bestMove();
                board_[move] = playerTurn_ == Player.FIRST ? 1 : 2;
                playerTurn_ = playerTurn_ == Player.FIRST ? Player.SECOND : Player.FIRST;
            }
            invalidate();
        }

        return true;
    }
}
