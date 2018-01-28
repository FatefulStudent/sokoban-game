package com.spbstu.labs;

import javax.swing.JFrame;


@SuppressWarnings("serial")
public final class Sokoban extends JFrame {

    private final int OFFSET = 30;

    public Sokoban() 
	{
    	// добавляем доску
        Board board = new Board();
        add(board);
		
		// операция закрытия
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // указываем размер
		setSize(board.getBoardWidth() + OFFSET, 
					board.getBoardHeight() + 2*OFFSET);
		// центрируем
        setLocationRelativeTo(null);
        setTitle("Sokoban");
    }


    public static void main(String[] args) {
        Sokoban sokoban = new Sokoban();
        sokoban.setVisible(true);
    }
}