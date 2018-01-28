package com.spbstu.labs;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Board extends JPanel { 

    private final int OFFSET = 30;
    private final int SPACE = 20;
    private final int LEFT_COLLISION = 1;
    private final int RIGHT_COLLISION = 2;
    private final int TOP_COLLISION = 3;
    private final int BOTTOM_COLLISION = 4;

    private ArrayList<Wall> walls = new ArrayList<Wall>();
    private ArrayList<Baggage> baggs = new ArrayList<Baggage>();
    private ArrayList<Area> areas = new ArrayList<Area>();
    private Player soko;
    private int w = 0; // ширина уровня
    private int h = 0; // высота уровня
    private boolean completed = false;
    
    public long oldTime = 0;
    public long currTime = 0;
    public long delta = 0;
    
    private String level = // Строка, содержащая уровень
              "    ######\n"
            + "    ##   #\n"
            + "    ##$  #\n"
            + "  ####  $##\n"
            + "  ##  $ $ #\n"
            + "#### # ## #   ######\n"
            + "##   # ## #####  ..#\n"
            + "## $  $          ..#\n"
            + "###### ### #@##  ..#\n"
            + "    ##     #########\n"
            + "    ########\n";

    public Board() {

        addKeyListener(new TAdapter());
        setFocusable(true);
        initWorld();
    }

    public int getBoardWidth() {
        return this.w;
    }

    public int getBoardHeight() {
        return this.h;
    }

    public final void initWorld() {
        
		// отступ сверху-слева
        int x = OFFSET;
        int y = OFFSET;
        
        Wall wall;
        Baggage b;
        Area a;
        
        oldTime = System.currentTimeMillis();;
     
		// пробегаемся по строке-уровню
        for (int i = 0; i < level.length(); i++) 
		{

            char item = level.charAt(i); // нынешний символ
			
			
            if (item == '\n') { 
                // если переход на следующую строку
				y += SPACE; // спускаемся на один блок вниз
				
                if (this.w < x) {
					// если ширина меньше х-координаты
                    this.w = x; // расширяем доску 
                }

                x = OFFSET;// переходим в самое левое значение
			} 
			else if (item == '#') // если символ стены
			{
				// то создаем стену
                wall = new Wall(x, y);
                walls.add(wall);
				// переходим направо на один блок
                x += SPACE;
            } 
			else if (item == '$')  // символ коробки
			{
				// создаем коробку
                b = new Baggage(x, y);
                baggs.add(b);
				// передвигаемся вправо
                x += SPACE;
            } 
			else if (item == '.')  //  символ места-приемки
			{
				// создаем новое место - приемку
                a = new Area(x, y);
                areas.add(a);
				// пердвигаемся направо
                x += SPACE;
            } 
			else if (item == '@')  // символ игрока-сокобана
			{
				// создаем игрока
                soko = new Player(x, y);
				// переходим направо
                x += SPACE;
            } else if (item == ' ') {
				// пустое место
                x += SPACE;
            }

            h = y; // обновляем высоту
        }
    }

    public void buildWorld(Graphics g) {

        g.setColor(new Color(250, 240, 170)); // цвет фона
        g.fillRect(0, 0, this.getWidth(), this.getHeight()); // закраска
		
		// создаем ArrayList для хранения всех объектов в мире
        ArrayList<Object> world = new ArrayList<Object>();
        world.addAll(walls);
        world.addAll(areas);
        world.addAll(baggs);
        world.add(soko);

        for (int i = 0; i < world.size(); i++) 
		{
			// пробегаемся по каждому элементу в мире
            Actor item = (Actor) world.get(i);

            if ((item instanceof Player) || (item instanceof Baggage)) 
			{
				// небольшой сдвиг игрока и боксов, для центрирования
                g.drawImage(item.getImage(), item.x() + 2, item.y() + 2, this);
            } 
			else 
			{
				// если просто стена или место-приемка рисуем
                g.drawImage(item.getImage(), item.x(), item.y(), this);
            }

            

        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        buildWorld(g);
        g.setColor(new Color(0, 0, 0));
        
        currTime = System.currentTimeMillis();
        delta = currTime - oldTime;
        
        Integer delta_int = (int) (delta/1000);
        
        g.drawString("Time Spent  =  " + delta_int.toString()+ " s", 
        			this.getBoardWidth()/2, 20);
        if (completed)  // если завершено, то пишем completed
		{
            g.drawString("Completed", 25, 20);   
            
        }
    }

    class TAdapter extends KeyAdapter 
	{

        @Override
        public void keyPressed(KeyEvent e) 
		{
			//  если была нажата клавиша
			
        	// получаем код клавиши
            int key = e.getKeyCode();
        	
        	if (key == KeyEvent.VK_R) 
			{
				// если нажимаем R
                restartLevel(); // рестарт
            }
        	
            if (completed)  // если игра пройдена
			{
				// по нажатию действия не происходят
                return;
            }

            if (key == KeyEvent.VK_LEFT) 
			{
				// если клавиша - влево
                if (checkWallCollision(soko, LEFT_COLLISION)) 
				{
					// но слева - стена
                    return; // возврат
                }

                if (checkBagCollision(LEFT_COLLISION)) 
				{
					// но слева - коробка
                    return; // возврат
                }
				
				// если все ок, двигаем игрока влево
                soko.move(-SPACE, 0);

            } 
			else if (key == KeyEvent.VK_RIGHT) 
			{
				// если нажали - вправо
                if (checkWallCollision(soko, RIGHT_COLLISION)) 
				{
					// если справа - стена
                    return; // возврат
                }

                if (checkBagCollision(RIGHT_COLLISION)) 
				{
					// если справа - коробка
                    return; // коробка
                }
				
				// если все ок, двигаем вправо
                soko.move(SPACE, 0);

            } 
			else if (key == KeyEvent.VK_UP) 
			{
				// если клавиша - вверх
                if (checkWallCollision(soko, TOP_COLLISION)) 
				{
					// если стена
                    return;// возврат
                }

                if (checkBagCollision(TOP_COLLISION)) 
				{
					// если бокс
                    return;// возврат
                }
				
				// если все ок - двигаем вверх
                soko.move(0, -SPACE);

            } 
			else if (key == KeyEvent.VK_DOWN) 
			{
				// если клавишу - вниз
                if (checkWallCollision(soko, BOTTOM_COLLISION)) 
				{
					// если стена
                    return; // возврат
                }

                if (checkBagCollision(BOTTOM_COLLISION)) 
				{
					// если бокс
                    return; // возврат
                }
				
				// если все ок - движение вниз
                soko.move(0, SPACE);

            } else if (key == KeyEvent.VK_H) {
            	
            	StatisticFrame frame = new StatisticFrame();
            	frame.setVisible(true);
            	
            	
            	
            }
			
			// перерисовываем левел
            repaint();
        }
    }

    private boolean checkWallCollision(Actor actor, int type) 
	{

        if (type == LEFT_COLLISION) 
		{
			// если коннектимся слева
            for (int i = 0; i < walls.size(); i++) 
			{
				// по каждой стене проверяем
                Wall wall = (Wall) walls.get(i);
				
				if (actor.isLeftCollision(wall)) 
				{
                    return true; // коллизия есть
                }
            }
            return false; // коллизии нет
        } 
		else if (type == RIGHT_COLLISION) 
		{
			// если коннектимся справа
            for (int i = 0; i < walls.size(); i++) 
			{
				// по каждой стене проверяем
                Wall wall = (Wall) walls.get(i);
                if (actor.isRightCollision(wall)) 
				{
                    return true;
                }
            }
            return false;

        } else if (type == TOP_COLLISION) 
		{
			// по каждой стене проверяем
            for (int i = 0; i < walls.size(); i++) 
			{
                Wall wall = (Wall) walls.get(i);
                if (actor.isTopCollision(wall)) 
				{
                    return true;
                }
            }
            return false;

        } else if (type == BOTTOM_COLLISION) 
		{
			// по каждой стене проверяем
            for (int i = 0; i < walls.size(); i++) 
			{
                Wall wall = (Wall) walls.get(i);
                if (actor.isBottomCollision(wall)) 
				{
                    return true;
                }
            }
            return false;
        }
        return false;// если непонятный тип коллизии
    }

    private boolean checkBagCollision(int type) 
	{

        if (type == LEFT_COLLISION) 
		{
			// пробегаем по всем боксам
            for (int i = 0; i < baggs.size(); i++) 
			{
				// берем каждый бокс 
                Baggage bag = (Baggage) baggs.get(i);
                
				if (soko.isLeftCollision(bag)) 
				{
					// если коннектимся с боксом
					// проверяем коллизии этого бокса
                    for (int j=0; j < baggs.size(); j++) 
					{
						// пробегаем по всем боксам, кроме этого
                        Baggage item = (Baggage) baggs.get(j);
						if (!bag.equals(item)) 
						{ 
                            if (bag.isLeftCollision(item)) 
							{
								// если они коннектятся
                                return true; // коллизия!
                            }
                        }
						
                        if (checkWallCollision(bag, LEFT_COLLISION)) 
						{
							// проверяем на коллизию со стенам
                            return true;
                        }
                    }
					// если все ок - двигаем
                    bag.move(-SPACE, 0);
					// проверяем на завершение
                    isCompleted();
                }
            }
            return false; // коллизии - нет

        } else if (type == RIGHT_COLLISION) {

            for (int i = 0; i < baggs.size(); i++) {

                Baggage bag = (Baggage) baggs.get(i);
                if (soko.isRightCollision(bag)) {
                    for (int j=0; j < baggs.size(); j++) {

                        Baggage item = (Baggage) baggs.get(j);
                        if (!bag.equals(item)) {
                            if (bag.isRightCollision(item)) {
                                return true;
                            }
                        }
                        if (checkWallCollision(bag,
                                RIGHT_COLLISION)) {
                            return true;
                        }
                    }
                    bag.move(SPACE, 0);
                    isCompleted();                   
                }
            }
            return false;

        } else if (type == TOP_COLLISION) {

            for (int i = 0; i < baggs.size(); i++) {

                Baggage bag = (Baggage) baggs.get(i);
                if (soko.isTopCollision(bag)) {
                    for (int j = 0; j < baggs.size(); j++) {

                        Baggage item = (Baggage) baggs.get(j);
                        if (!bag.equals(item)) {
                            if (bag.isTopCollision(item)) {
                                return true;
                            }
                        }
                        if (checkWallCollision(bag,
                                TOP_COLLISION)) {
                            return true;
                        }
                    }
                    bag.move(0, -SPACE);
                    isCompleted();
                }
            }

            return false;

        } else if (type == BOTTOM_COLLISION) {
        
            for (int i = 0; i < baggs.size(); i++) {

                Baggage bag = (Baggage) baggs.get(i);
                if (soko.isBottomCollision(bag)) {
                    for (int j = 0; j < baggs.size(); j++) {

                        Baggage item = (Baggage) baggs.get(j);
                        if (!bag.equals(item)) {
                            if (bag.isBottomCollision(item)) {
                                return true;
                            }
                        }
                        if (checkWallCollision(bag,
                                BOTTOM_COLLISION)) {
                            return true;
                        }
                    }
                    bag.move(0, SPACE);
                    isCompleted();
                }
            }
        }

        return false;
    }

    public void isCompleted() {

        int num = baggs.size();
        int count = 0;

        for (int i = 0; i < num; i++) 
		{
			// проверяем, стоит ли каждый бокс на месте
            Baggage bag = (Baggage) baggs.get(i);
			for (int j = 0; j < num; j++) 
			{
                Area area = (Area) areas.get(j);
                if (bag.x() == area.x() && bag.y() == area.y()) {
                    count++;
                }
            }
        }
        int flag = 0;
        if (count == num && flag == 0) // если все на местах
		{
        	String path = "statistics.txt";
        	
        	
        	flag = 1;
        	currTime = System.currentTimeMillis();
        	delta = currTime - oldTime;
        	Integer delta_int = (int) (delta/1000);
        	
        	String str = "Your best time " + delta_int.toString() + " seconds";
        	
        	try(FileWriter writer = new FileWriter("C:\\Users\\VLIVANOV\\eclipse-workspace\\SokobanGame\\src\\com\\spbstu\\labs\\statistics.txt", true))
            {
               // запись всей строки
                
                writer.write(str);
                writer.append('\n');
                 
                writer.flush();
            }
            catch(IOException ex){
                 
                System.out.println(ex.getMessage());
            } 
        	        			
            completed = true;
            repaint();
        }
    }

    public void restartLevel() 
	{
        areas.clear();
        baggs.clear();
        walls.clear();
        initWorld();
        if (completed) 
		{
            completed = false;
        }
    }
}