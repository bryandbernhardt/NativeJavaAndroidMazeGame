package com.ucs.labiridroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class MazeView extends View {
    private Cell[][] cells;
    private static final int COLS =  8, ROWS = 12;
    private static final float WALL_THICKNESS = 4;
    private float cellSize, hMargin, vMargin;
    private Paint wallPaint;
    private Random random;

    public MazeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        wallPaint = new Paint();
        wallPaint.setColor(Color.BLACK);
        wallPaint.setStrokeWidth(WALL_THICKNESS);

        random = new Random();

        createMaze();
    }

    private Cell getNext(Cell cell) {
        ArrayList<Cell> nexts = new ArrayList<>();

        // índice para esquerda
        if(cell.col > 0 && !cells[cell.col - 1][cell.row].visited) {
            nexts.add(cells[cell.col - 1][cell.row]);
        }

        // índice para direita
        if(cell.col < COLS - 1 && !cells[cell.col + 1][cell.row].visited) {
            nexts.add(cells[cell.col + 1][cell.row]);
        }

        // índice para cima
        if(cell.row > 0 && !cells[cell.col][cell.row - 1].visited) {
            nexts.add(cells[cell.col][cell.row - 1]);
        }

        // índice para baixo
        if(cell.row < ROWS - 1 && !cells[cell.col][cell.row + 1].visited) {
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

    private void createMaze() {
        cells = new Cell[COLS][ROWS];
        Stack<Cell> stack = new Stack<>();
        Cell current, next;

        for(int x = 0; x < COLS; x++) {
            for(int y = 0; y < ROWS; y++) {
                cells[x][y] = new Cell(x, y);
            }
        }

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

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.parseColor("#04D900"));

        int width = getWidth();
        int height = getHeight();

        if(width/height < ROWS/COLS) {
            cellSize = width/(COLS + 1);
        } else if (width/height < ROWS/COLS){
            cellSize = height/(COLS + 1);
        } else {
            cellSize = height/(ROWS + 1);
        }

        hMargin = (width - COLS * cellSize) / 2;
        vMargin = (height - ROWS * cellSize) / 2;

        canvas.translate(hMargin, vMargin);

        for(int x = 0; x < COLS; x++) {
            for(int y = 0; y < ROWS; y++) {
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
