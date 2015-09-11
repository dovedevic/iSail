package iSail;

public class BlockType extends iSail
{
	int id;
	byte data;
	String lines[] = null;
	BlockType(int id, byte data, String lines[], char dir)
	{
		this.id = id;
		this.data = data;
		this.lines = lines;
		if(dir != 'M')//Поворот
		{
			if(id == 17 || id == 27 || id == 96 ||id == 28 || id == 68 || id == 50 || id == 53 || id == 64 || id == 65 || id == 66 || id == 69 || id == 77 || id == 108 || id == 107 || id == 109 || id == 114 || id == 128 || id == 134 || id == 135 || id == 136 || id == 135 || id == 136 || id == 137 || id == 143)//Поворачиваемый блок
			{
				if(id == 17 && 4 <= data ) logTurn();
				else if(id == 27 || id == 28 || id == 66)//TODO
				{
					if(dir == 'R') rRight();
					else rLeft();
				}
				else if(id == 96)
				{
					if(dir == 'R') lukRight();
					else lukLeft();
				}
				else if(id == 68)
				{
					if(dir == 'R') signRight();
					else signLeft();
				}
				else if(id == 64)
				{
					if(dir == 'R') doorRight();
					else doorLeft();
				}
				else if((id == 77 || id == 143) && id != 5)
				{
					if(dir == 'R') buttonRight();
					else buttonLeft();
				}
				else if(id == 65)
				{
					if(dir == 'R') ladderRight();
					else ladderLeft();
				}
				else if(id == 69)//Рычаг
				{
					if(dir == 'R') leverRight();
					else leverLeft();
				}
				else if(id == 53 || id == 67 || id == 108 || id == 109 || id == 114 || id == 128 || id == 134 || id == 135 || id == 136)
				{
					if(dir == 'R') stairsRight();
					else stairsLeft();
				}
				else if(id == 107)
				{
					if(dir == 'R') kRight();
					else kLeft();
				}
				else if(id == 50 && data != 5)
				{
					if(dir == 'R') torchRight();
					else torchLeft();
				}
			}
		}
	}
	public void torchRight()
	{
		if(data == 1) data = 3;
		else if(data == 2) data = 4;
		else if(data == 3) data = 2;
		else if(data == 4) data = 1;
	}
	public void torchLeft()
	{
		if(data == 1) data = 4;
		else if(data == 2) data = 3;
		else if(data == 3) data = 1;
		else if(data == 4) data = 2;
	}
	public void kRight()
	{
		if(data == 0) data = 1;
		else if(data == 1) data = 2;
		else if(data == 2) data = 3;
		else if(data == 3) data = 0;
		else if(data == 4) data = 5;
		else if(data == 5) data = 6;
		else if(data == 6) data = 7;
		else if(data == 7) data = 4;
	}
	public void kLeft()
	{
		if(data == 0) data = 3;
		else if(data == 1) data = 0;
		else if(data == 2) data = 1;
		else if(data == 3) data = 2;
		else if(data == 4) data = 7;
		else if(data == 5) data = 4;
		else if(data == 6) data = 5;
		else if(data == 7) data = 6;
	}
	public void logTurn()
	{
		if(data == 4) data = 8;
		else if(data == 5) data = 9;
		else if(data == 6) data = 10;
		else if(data == 7) data = 11;
		else if(data == 8) data = 4;
		else if(data == 9) data = 5;
		else if(data == 10) data = 6;
		else if(data == 11) data = 7;
	}
	public void stairsRight()
	{
		if(data == 0) data = 2;
		else if(data == 1) data = 3;
		else if(data == 2) data = 1;
		else if(data == 3) data = 0;
		else if(data == 4) data = 6;
		else if(data == 5) data = 7;
		else if(data == 6) data = 5;
		else if(data == 7) data = 4;
	}
	public void stairsLeft()
	{
		if(data == 0) data = 3;
		else if(data == 1) data = 2;
		else if(data == 2) data = 0;
		else if(data == 3) data = 1;
		else if(data == 4) data = 7;
		else if(data == 5) data = 6;
		else if(data == 6) data = 4;
		else if(data == 7) data = 5;
	}
	public void leverRight()
	{
		if(data == 0) data = 7;
		else if(data == 1) data = 3;
		else if(data == 2) data = 4;
		else if(data == 3) data = 2;
		else if(data == 4) data = 1;
		else if(data == 5) data = 6;
		else if(data == 6) data = 5;
		else if(data == 7) data = 0;
		else if(data == 8) data = 15;
		else if(data == 9) data = 11;
		else if(data == 10) data = 12;
		else if(data == 11) data = 10;
		else if(data == 12) data = 9;
		else if(data == 13) data = 14;
		else if(data == 14) data = 13;
		else if(data == 15) data = 8;
	}
	public void leverLeft()
	{
		if(data == 0) data = 7;
		else if(data == 1) data = 4;
		else if(data == 2) data = 3;
		else if(data == 3) data = 1;
		else if(data == 4) data = 2;
		else if(data == 5) data = 6;
		else if(data == 6) data = 5;
		else if(data == 7) data = 0;
		else if(data == 8) data = 15;
		else if(data == 9) data = 12;
		else if(data == 10) data = 11;
		else if(data == 11) data = 9;
		else if(data == 12) data = 10;
		else if(data == 13) data = 14;
		else if(data == 14) data = 13;
		else if(data == 15) data = 8;
	}
	public void ladderRight()
	{
		if(data == 2) data = 5;
		else if(data == 3) data = 4;
		else if(data == 4) data = 2;
		else if(data == 5) data = 3;
	}
	public void ladderLeft()
	{
		if(data == 2) data = 4;
		else if(data == 3) data = 5;
		else if(data == 4) data = 3;
		else if(data == 5) data = 2;
	}
	public void buttonRight()
	{
		if(data == 1) data = 3;
		else if(data == 2) data = 4;
		else if(data == 3) data = 2;
		else if(data == 4) data = 1;
	}
	public void buttonLeft()
	{
		if(data == 1) data = 4;
		else if(data == 2) data = 3;
		else if(data == 3) data = 1;
		else if(data == 4) data = 2;
	}
	public void doorRight()
	{
		if(data == 0) data = 1;
		else if(data == 1) data = 2;
		else if(data == 2) data = 3;
		else if(data == 3) data = 0;
		else if(data == 4) data = 5;
		else if(data == 5) data = 6;
		else if(data == 6) data = 7;
		else if(data == 7) data = 4;
	}
	public void doorLeft()
	{
		if(data == 0) data = 3;
		else if(data == 1) data = 0;
		else if(data == 2) data = 1;
		else if(data == 3) data = 2;
		else if(data == 4) data = 7;
		else if(data == 5) data = 4;
		else if(data == 6) data = 5;
		else if(data == 7) data = 6;
	}
	public void lukRight()
	{
		if(data == 0) data = 3;
		else if(data == 1) data = 2;
		else if(data == 2) data = 0;
		else if(data == 3) data = 1;
		else if(data == 4) data = 7;
		else if(data == 5) data = 6;
		else if(data == 6) data = 4;
		else if(data == 7) data = 5;
		else if(data == 8) data = 11;
		else if(data == 9) data = 10;
		else if(data == 10) data = 8;
		else if(data == 11) data = 9;
		else if(data == 12) data = 15;
		else if(data == 13) data = 14;
		else if(data == 14) data = 12;
		else if(data == 15) data = 13;
	}
	public void lukLeft()
	{
		if(data == 0) data = 2;
		else if(data == 1) data = 3;
		else if(data == 2) data = 1;
		else if(data == 3) data = 0;
		else if(data == 4) data = 6;
		else if(data == 5) data = 7;
		else if(data == 6) data = 5;
		else if(data == 7) data = 4;
		else if(data == 8) data = 10;
		else if(data == 9) data = 11;
		else if(data == 10) data = 9;
		else if(data == 11) data = 8;
		else if(data == 12) data = 14;
		else if(data == 13) data = 15;
		else if(data == 14) data = 13;
		else if(data == 15) data = 12;
	}
	public void rRight()
	{
		if(data == 0) data = 1;
		else if(data == 1) data = 0;
		else if(data == 2) data = 5;
		else if(data == 3) data = 4;
		else if(data == 4) data = 2;
		else if(data == 5) data = 3;
	}
	public void rLeft()
	{
		if(data == 0) data = 1;
		else if(data == 1) data = 0;
		else if(data == 2) data = 4;
		else if(data == 3) data = 5;
		else if(data == 4) data = 3;
		else if(data == 5) data = 2;
	}
	public void signRight()
	{
		if(data == 2) data = 5;
		else if(data == 3) data = 4;
		else if(data == 4) data = 2;
		else if(data == 5) data = 3;
	}
	public void signLeft()
	{
		if(data == 2) data = 4;
		else if(data == 3) data = 5;
		else if(data == 4) data = 3;
		else if(data == 5) data = 2;
	}
	public boolean isTurnable(int id)
	{
		return id == 17 || id == 27 || id == 96 || id == 66 || id == 108 || id == 109 || id == 114 || id == 128 || id == 134 || id == 135 || id == 136 || id == 28 || id == 68 || id == 53 || id == 135 || id == 136 || id == 137 || id == 50 || id == 65;
	}
}
