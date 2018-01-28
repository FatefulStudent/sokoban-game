package com.spbstu.labs;

import java.awt.Color;
import java.awt.Graphics;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JPanel;

public class StatBoard extends JPanel{

	public StatBoard() {
		setFocusable(true);
	}
	
	
	
	@Override
    public void paint(Graphics g) {
        
        super.paint(g);
		
		g.setColor(new Color(0, 0, 0));
		
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
