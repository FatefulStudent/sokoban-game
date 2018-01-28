package com.spbstu.labs;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

public class Wall extends Actor 
{

    private Image image;

    public Wall(int x, int y) 
	{
		// вызываем конструктор актера с x,y
        super(x, y);

        URL loc = this.getClass().getResource("wall.png");
        ImageIcon imageIcon = new ImageIcon(loc);
        image = imageIcon.getImage();
        this.setImage(image);
    }
}