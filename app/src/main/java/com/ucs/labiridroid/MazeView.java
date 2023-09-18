package com.ucs.labiridroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.metrics.PlaybackErrorEvent;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class MazeView extends View {
    private Cell[][] cells;
    private Cell player, exit;
    private int Cols =  2, Rows = 4;
    private static final float WALL_THICKNESS = 4;
    private float cellSize, hMargin, vMargin;
    private Paint wallPaint, playerPaint, exitPaint;
    private Random random;
    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
    private int points = 0;

    public MazeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        wallPaint = new Paint();
        wallPaint.setColor(Color.BLACK);
        wallPaint.setStrokeWidth(WALL_THICKNESS);

        playerPaint = new Paint();
        playerPaint.setColor(Color.WHITE);

        exitPaint = new Paint();
        exitPaint.setColor(Color.BLACK);

        random = new Random();

        createMaze();
    }

    private void createMaze() {
        cells = new Cell[Cols][Rows];
        Stack<Cell> stack = new Stack<>();
        Cell current, next;

        // cria as cédulas
        for(int x = 0; x < Cols; x++) {
            for(int y = 0; y < Rows; y++) {
                cells[x][y] = new Cell(x, y);
            }
        }

        // define posição do jogador e final
        player = cells[0][0];
        exit = cells[Cols - 1][Rows - 1];

        // gera labirinto aleatório
        current = cells[0][0];
        current.visited = true;

        do {
            next = getNext(current);
            if (next != null) {
                removeWall(current, next);
                stack.push(current);
                current = next;
                current.visited = true;
            } else {
                current = stack.pop();
            }
        } while(!stack.empty());
    }

    private Cell getNext(Cell cell) {
        ArrayList<Cell> nexts = new ArrayList<>();

        // índice para esquerda
        if(cell.col > 0 && !cells[cell.col - 1][cell.row].visited) {
            nexts.add(cells[cell.col - 1][cell.row]);
        }

        // índice para direita
        if(cell.col < Cols - 1 && !cells[cell.col + 1][cell.row].visited) {
            nexts.add(cells[cell.col + 1][cell.row]);
        }

        // índice para cima
        if(cell.row > 0 && !cells[cell.col][cell.row - 1].visited) {
            nexts.add(cells[cell.col][cell.row - 1]);
        }

        // índice para baixo
        if(cell.row < Rows - 1 && !cells[cell.col][cell.row + 1].visited) {
            nexts.add(cells[cell.col][cell.row + 1]);
        }

        if(nexts.size() > 0) {
            int index = random.nextInt(nexts.size());
            return nexts.get(index);
        }
        return null;
    }

    private void removeWall(Cell current, Cell next) {

        // índice para esquerda
        if(current.col == next.col + 1 && current.row == next.row) {
            current.leftWall = false;
            next.rightWall = false;
        }

        // índice para direita
        if(current.col == next.col - 1 && current.row == next.row) {
            current.rightWall = false;
            next.leftWall = false;
        }

        // índice para cima
        if(current.col == next.col && current.row == next.row + 1) {
            current.topWall = false;
            next.bottomWall = false;
        }

        // índice para baixo
        if(current.col == next.col && current.row == next.row - 1) {
            current.bottomWall = false;
            next.topWall = false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.parseColor("#04D900"));

        // adapta ao tamanho da tela
        int width = getWidth();
        int height = getHeight();

        if(width/height < Rows/Cols) {
            cellSize = width/(Cols + 1);
        } else if (width/height < Rows/Cols){
            cellSize = height/(Cols + 1);
        } else {
            cellSize = height/(Rows + 1);
        }

        hMargin = (width - Cols * cellSize) / 2;
        vMargin = (height - Rows * cellSize) / 2;

        canvas.translate(hMargin, vMargin);

        // desenha matriz
        for(int x = 0; x < Cols; x++) {
            for(int y = 0; y < Rows; y++) {
                if(cells[x][y].leftWall) {
                    canvas.drawLine(
                            x * cellSize,
                            y * cellSize,
                            x * cellSize,
                            (y + 1) * cellSize,
                            wallPaint
                    );
                }

                if(cells[x][y].rightWall) {
                    canvas.drawLine(
                            (x + 1) * cellSize,
                            y * cellSize,
                            (x + 1) * cellSize,
                            (y + 1) * cellSize,
                            wallPaint
                    );
                }

                if(cells[x][y].topWall) {
                    canvas.drawLine(
                            x * cellSize,
                            y * cellSize,
                            (x + 1) * cellSize,
                            y * cellSize,
                            wallPaint
                    );
                }

                if(cells[x][y].bottomWall) {
                    canvas.drawLine(
                            x * cellSize,
                            (y + 1) * cellSize,
                            (x + 1) * cellSize,
                            (y + 1) * cellSize,
                            wallPaint
                    );
                }
            }
        }

        // desenha jogador e final
        float margin = cellSize / 10;

        canvas.drawRect(
                player.col * cellSize + margin,
                player.row * cellSize + margin,
                (player.col + 1) * cellSize - margin,
                (player.row + 1) * cellSize - margin,
                playerPaint
        );

        canvas.drawRect(
                exit.col * cellSize + margin,
                exit.row * cellSize + margin,
                (exit.col + 1) * cellSize - margin,
                (exit.row + 1) * cellSize - margin,
                exitPaint
        );
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            return true;
        }

        // se o evento de touch for igual ACTION_MOVE
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            // captura a posição do evento
            float x = event.getX();
            float y = event.getY();

            // captura a posição do jogador
            float playerCenterX = hMargin + (player.col + 0.5f) * cellSize;
            float playerCenterY = vMargin + (player.row + 0.5f) * cellSize;

            // calcula a diferença entre o evento e o jogador
            float diferenceX = x - playerCenterX;
            float diferenceY = y - playerCenterY;

            float absoluteDiferenceX = Math.abs(diferenceX);
            float absoluteDiferenceY = Math.abs(diferenceY);

            // verifica qual direção movimentar
            if (absoluteDiferenceX > cellSize || absoluteDiferenceY > cellSize) {
                if (absoluteDiferenceX > absoluteDiferenceY) {
                    // move na direção X
                    if (diferenceX > 0) {
                        // move para direita
                        movePlayer(Direction.RIGHT);
                    } else {
                        // move para esquerda
                        movePlayer(Direction.LEFT);
                    }
                } else {
                    // move na direção Y
                    if (diferenceY > 0) {
                        // move para baixo
                        movePlayer(Direction.DOWN);
                    } else {
                        // move para cima
                        movePlayer(Direction.UP);
                    }
                }
            }
            return true;
        }

        return super.onTouchEvent(event);
    }

    private void movePlayer(Direction direction) {
        switch (direction) {
            case UP:
                if (!player.topWall) {
                    player = cells[player.col][player.row - 1];
                }
                break;
            case DOWN:
                if (!player.bottomWall) {
                    player = cells[player.col][player.row + 1];
                }
                break;
            case LEFT:
                if (!player.leftWall) {
                    player = cells[player.col - 1][player.row];
                }
                break;
            case RIGHT:
                if (!player.rightWall) {
                    player = cells[player.col + 1][player.row];
                }
        }

        // verifica se está no final
        checkExit();

        // chama o método on draw
        invalidate();
    }

    private void checkExit() {
        if (player == exit) {
            points += 1;
            Cols += 1;
            Rows += 1;
            createMaze();
        }
    }

    private class Cell {
        boolean
                topWall = true,
                leftWall = true,
                bottomWall = true,
                rightWall = true,
                visited = false;
        int col, row;

        public Cell(int col, int row) {
            this.col = col;
            this.row = row;
        }
    }
}
