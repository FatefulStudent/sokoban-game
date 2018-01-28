package com.spbstu.labs;

import java.awt.Color;
import java.awt.Graphics;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class StatisticFrame extends JFrame {
	
	public StatisticFrame() {
		
		// добавляем доску
        StatBoard statboard = new StatBoard();
        add(statboard);
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // указываем размер
		setSize(1250,250);
		// центрируем
        setLocationRelativeTo(null);
        setTitle("Statistics");
	}
	
	@SuppressWarnings("null")
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		setFocusable(true);
		g.setColor(new Color(0, 255, 0));
		
		String str = "";
		try(FileReader reader = new FileReader("C:\\Users\\VLIVANOV\\eclipse-workspace\\SokobanGame\\src\\com\\spbstu\\labs\\statistics.txt"))
		{
			// читаем посимвольно
			int c;
			while((c=reader.read())!=-1){
				char Char = (char)c;
				//System.out.print(Char);
				String Char_str = Character.toString(Char);
				str += Char_str;
			} 
		}
		catch(IOException ex){
			System.out.println(ex.getMessage());
		}
		
		//str.toString();
		g.drawString(str, 10, 25);
		
	}
}
