package iSail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

public class Maht extends iSail
{
	Block b;
	MahtType type;
	Ship ship;
	Sail triangle = null;
	Sail rear = null;
	Sail foreAndAft = null;
	List<Sail> sails = new ArrayList<Sail>();
	HashMap<String, Block> blocks = new HashMap<String, Block>();
	List<Block> pb = new ArrayList<Block>();
	List<Block> npb = new ArrayList<Block>();
	public Maht(Ship ship, Block b, MahtType type)
	{
		this.b = b.getWorld().getBlockAt(b.getX(), ship.l.getBlockY()+1, b.getZ());
		this.ship = ship;
		this.type = type;
	}
	public boolean isConnected()//TODO
	{
		/*int limit = 0;
		for(int x = -1; x <2; x++)
		{
			for(int y = 0; y < 100; y++)
			{
				for(int z = -1; z < 2; z++)
				{
					Block newB = b.getRelative(x,y,z);
					if(newB.getTypeId() == 0) if(limit < newB.getY()) limit = newB.getY();
				}
			}
		}*/
		return true; //TODO
	}
	public void destroy()
	{
		Block ab = b.getWorld().getBlockAt(b.getX(), ship.l.getBlockY(), b.getY());
		List<Block> blocks = new ArrayList<Block>();
		blocks.add(ab);
		ab.breakNaturally();
		while(0 < blocks.size()) 
        {
			for(int x = -1; x <=1; x++)
			{
				for(int z = -1; z <=1;z++)
				{
					for(int y = -1; y <=1;y++)
					{
						Block newB = b.getRelative(x,y,z);
						if(newB.getY() <= waterLine)
						{
							int id = newB.getTypeId();
							if(isba(id) || isha(id)) 
							{
								newB.breakNaturally();
								blocks.add(newB);
							}
						}
					}
				}
			}
            ab = ab.getRelative(0, 1, 0);
        }
		for(int i = 0; i < sails.size(); i++)
		{
			disable();
		}
	}
	public void destroyTop(Block b)
	{
		Block ab = b.getWorld().getBlockAt(b.getX(), ship.l.getBlockY(), b.getY());
		List<Block> blocks = new ArrayList<Block>();
		blocks.add(ab);
		ab.breakNaturally();
		while(0 < blocks.size()) 
        {
			for(int x = -1; x <=1; x++)
			{
				for(int z = -1; z <=1;z++)
				{
					Block newB = b.getRelative(x,1,z);
					int id = newB.getTypeId();
					if(isba(id) || isha(id)) 
					{
						newB.breakNaturally();
						blocks.add(newB);
					}
				}
			}
            ab = ab.getRelative(0, 1, 0);
        }
		for(int i = 0; i < sails.size(); i++)
		{
			disable();
		}
	}
	public int getPower()
	{
		int power = 0;
		if(triangle != null) power += triangle.getPower(true);
		else if(rear !=null) power += rear.getPower(true);
		if(foreAndAft != null) foreAndAft.getPower(true);
		if(sails != null) for(int i = 0; i < sails.size(); i++)
		{
			Sail sail = sails.get(i);
			power += sail.getPower(true);
		}
		return power;
	}
	public void disable()
	{
		if(triangle != null) triangle.disable();
		if(rear != null) rear.disable();
		if(foreAndAft != null) foreAndAft.disable();
		while(0 < sails.size())
		{
			Sail sail = sails.get(0);
			sail.disable();
		}
	}
	public boolean remove()
	{
		if(triangle == null && rear == null && foreAndAft == null && sails.isEmpty())
		{
			if(type.equals(MahtType.BIZAN)) ship.bizan = null;
			else if(type.equals(MahtType.GROT)) ship.grot = null;
			else if(type.equals(MahtType.FOK)) ship.fok = null;
			return true;
		}
		return false;
	}
	public void turnRight()
	{
		if(triangle != null) triangle.turnRight();
		if(rear != null) rear.turnRight();
		if(foreAndAft != null) foreAndAft.turnRight();
		for(int i = 0; i < sails.size(); i++)
		{
			Sail sail = sails.get(i);
			sail.turnRight();
		}
		final Block relative = ship.relative;
		int x = b.getX() - relative.getX();
		int y = b.getY() - relative.getY();
		int z = b.getZ() - relative.getZ();
		b = relative.getRelative(-z, y, x);
		//I.sendBlockChange(b.getLocation(), 35, (byte) 15);
	}
	public void turnLeft()
	{
		if(triangle != null) triangle.turnLeft();
		if(rear != null) rear.turnLeft();
		if(foreAndAft != null) foreAndAft.turnLeft();
		for(int i = 0; i < sails.size(); i++)
		{
			Sail sail = sails.get(i);
			sail.turnLeft();
		}
		final Block relative = ship.relative;
		int x = b.getX() - relative.getX();
		int y = b.getY() - relative.getY();
		int z = b.getZ() - relative.getZ();
		b = relative.getRelative(z, y, -x);
		//I.sendBlockChange(b.getLocation(), 35, (byte) 15);
	}
	public Sail getSailFromSignblock(Block signBlock)
	{
		if(triangle != null) if(triangle.signBlock.equals(signBlock)) return triangle;
		if(rear != null) if(rear.signBlock.equals(signBlock)) return rear;
		if(foreAndAft != null) if(foreAndAft.signBlock.equals(signBlock)) return foreAndAft;
		if(sails != null) for(int i = 0; i < sails.size(); i++)
		{
			Sail s = sails.get(i);
			if(s.signBlock.equals(signBlock)) return s;
		}
		return null;
	}
	public boolean changeSails(Block signBlock, Action action)
	{
		if(triangle != null) if(triangle.Change(signBlock, action)) return true;
		if(rear !=null) if(rear.Change(signBlock, action)) return true;
		if(foreAndAft != null) if(foreAndAft.Change(signBlock, action)) return true;
		if(sails != null) for(int i = 0; i < sails.size(); i++)
		{
			Sail sail = sails.get(i);
			if(sail.Change(signBlock, action)) return true;
		}
		return false;
	}
	public void move()
	{
		//I.sendMessage("Moving maht " + type.toString().toLowerCase());
		Vector v = ship.v;
		b = b.getLocation().add(v).getBlock();
		if(triangle != null) triangle.move();
		if(rear != null) rear.move();
		if(foreAndAft != null) foreAndAft.move();
		if(sails != null) for(int i = 0; i < sails.size(); i++)
		{
			Sail sail = sails.get(i);
			sail.move();
		}
	}
	public boolean isMakerOfSail(Player p)
	{
		if(triangle != null) if(triangle.isMakerOfSail(p)) return true;
		if(rear != null) if(rear.isMakerOfSail(p)) return true;
		if(foreAndAft != null) if(foreAndAft.isMakerOfSail(p)) return true;
		if(sails != null) for(int i = 0; i < sails.size(); i++)
		{
			Sail sail = sails.get(i);
			if(sail.isMakerOfSail(p)) return true;
		}
		return false;
	}
	public Maht getPrevious()
	{
		if(type.equals(MahtType.BIZAN)) return null;
		else if(type.equals(MahtType.FOK))
		{
			if(ship.grot != null) return ship.grot;
			else if(ship.bizan != null) return ship.bizan;
		}
		else if(type.equals(MahtType.GROT))
		{
			if(ship.bizan != null) return ship.bizan;
		}
		return null;
	}
}
