package iSail;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;


public class Sail extends iSail
{
	Ship ship;
	Maht maht = null;
	Player maker;
	SailType type;
	Block signBlock;//Moves
	Block left;//Moves
	double li = 0;
	boolean haveLeft = false;
	Block right;//Moves
	double ri = 0;
	boolean haveRight = false;
	int L;
	int H;
	int hard;
	int openWool = 0;
	List<SailBlock> wool = new ArrayList<SailBlock>();
	boolean open;
	boolean isHeavy;
	public Sail(Ship ship, Maht maht, Block signBlock, Player maker, SailType type)
	{
		this.type = type;
		this.signBlock = signBlock;
		this.maht = maht;
		this.ship = ship;
		this.maker = maker;
		this.L = findLength();
		this.H = findHeight();
		this.hard = L*H;
		this.open = true;
		close(true);
		maker.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(ChatColor.BOLD).append("[iSail] This sail have L: ").append(L).append(" m and H: ").append(H).append(" m!").toString());
	}
	public void open(boolean force)
	{
		if(!open)
		{
			if(force || left != null && right != null && left.getType().equals(Material.WALL_SIGN) && right.getType().equals(Material.WALL_SIGN))
			{
				Block b;
				byte data = ship.data;
				open = true;
				int A = 0;
				Block first;
				if(type.equals(SailType.SQUARE) || type.equals(SailType.FRONT))
				{
					if(data == 3)
					{
						first = getMain().getRelative((L-1)/2, -H, 0);
						for(int l = 0; l <= L; l++)
						{
							for(int h = H; 0 <= h; h--)
							{
								for(int d = 0; d <= 3; d++)
								{
									b = first.getRelative(-l, h, -d);
									//I.sendBlockChange(b.getLocation(), 9, (byte) 0);
									if(b.isEmpty() && wool.get(A).wool) b.setTypeIdAndData(35, wool.get(A).data, true);
									A++;
								}
							}
						}
					}
					if(data == 2)
					{
						first = getMain().getRelative(-(L-1)/2, -H, 0);
						for(int l = 0; l <= L; l++)
						{
							for(int h = H; 0 <= h; h--)
							{
								for(int d = 0; d <= 3; d++)
								{
									b = first.getRelative(l, h, d);
									//I.sendBlockChange(b.getLocation(), 9, (byte) 0);
									if(b.isEmpty() && wool.get(A).wool) b.setTypeIdAndData(35, wool.get(A).data, true);
									A++;
								}
							}
						}
					}
					if(data == 5)
					{
						first = getMain().getRelative(0, -H, -(L-1)/2);
						for(int l = 0; l <= L; l++)
						{
							for(int h = H; 0 <= h; h--)
							{
								for(int d = 0; d <= 3; d++)
								{
									b = first.getRelative(-d, h, l);
									//I.sendBlockChange(b.getLocation(), 9, (byte) 0);
									if(b.isEmpty() && wool.get(A).wool) b.setTypeIdAndData(35, wool.get(A).data, true);
									A++;
								}
							}
						}
					}
					if(data == 4)
					{
						first = getMain().getRelative(0, -H, (L-1)/2);
						for(int l = 0; l <= L; l++)
						{
							for(int h = H; 0 <= h; h--)
							{
								for(int d = 0; d <= 3; d++)
								{
									b = first.getRelative(d, h, -l);
									//I.sendBlockChange(b.getLocation(), 9, (byte) 0);
									if(b.isEmpty() && wool.get(A).wool) b.setTypeIdAndData(35, wool.get(A).data, true);
									A++;
								}
							}
						}
					}
				}
				else if(type.equals(SailType.TRIANGLE))//FIXME
				{
					first = getFirst();
					final int lfl = getLimitForInt();
					for(int l = 0; l <= L - lfl; l++)
						for(int h = 0; h <= H; h++)
						{
							b = getSailBlock(l, h, 0, first);
							if(b.isEmpty() && wool.get(A).wool) b.setTypeIdAndData(35, wool.get(A).data, true);
							A++;
						}
				}
				else if(type.equals(SailType.FOREANDAFT))
				{
					first = getFirst();
					final int lfl = getLimitForInt();
					for(int l = 0; l <= L - lfl; l++)
						for(int h = 0; h <= H; h++)
						{
							b = getSailBlock(l, h, 0, first);
							if(b.isEmpty() && wool.get(A).wool) b.setTypeIdAndData(35, wool.get(A).data, true);
							A++;
						}
				}
				else if(type.equals(SailType.REAR))
				{
					first = getFirst();
					for(int l = 0; l <= L; l++)
						for(int h = 0; h <= H; h++)
						{
							b = getSailBlock(l, h, 0, first);
							if(b.isEmpty() && wool.get(A).wool) b.setTypeIdAndData(35, wool.get(A).data, true);
							A++;
						}
				}
			}
		}
	}
	public int getPower(boolean move)
	{
		int A = 0;
		final double wd = (double) getWd();
		repair();
		if(haveLeft && haveRight)
		{
			if(signBlock.getTypeId() == 68 && left.getTypeId() == 68 && right.getTypeId() == 68)
			{
				if(open) 
				{
					byte data = ship.data;
					Block first;
					Block b;
					if(type.equals(SailType.SQUARE) || type.equals(SailType.FRONT))
					{
						if(data == 3)
						{
							first = getMain().getRelative((L-1)/2, -H, 0);
							for(int l = 0; l <= L; l++)
							{
								for(int h = H; 0 <= h; h--)
								{
									for(int d = 0; d <= 3; d++)
									{
										b = first.getRelative(-l, h, -d);
										if(b.getTypeId() == 35)
										{
											A+=2;
											if(b.getRelative(0, 0, -1).getTypeId() == 35) A-=3;
											else if(move) if(b.getRelative(0, 0, 1).getTypeId() == 0) if(Math.random()<0.0000000008*windPower*windPower*windPower*windPower) b.setTypeIdAndData(0, (byte) 0, true);
										}
									}
								}
							}
						}
						if(data == 2)
						{
							first = getMain().getRelative(-(L-1)/2, -H, 0);
							for(int l = 0; l <= L; l++)
							{
								for(int h = H; 0 <= h; h--)
								{
									for(int d = 0; d <= 3; d++)
									{
										b = first.getRelative(l, h, d);
										if(b.getTypeId() == 35)
										{
											A+=2;
											if(b.getRelative(0, 0, 1).getTypeId() == 35) A-=3;
											else if(move) if(b.getRelative(0, 0, -1).getTypeId() == 0) if(Math.random()<0.0000000008*windPower*windPower*windPower*windPower) b.setTypeIdAndData(0, (byte) 0, true);
										}
									}
								}
							}
						}
						if(data == 5)
						{
							first = getMain().getRelative(0, -H, -(L-1)/2);
							for(int l = 0; l <= L; l++)
							{
								for(int h = H; 0 <= h; h--)
								{
									for(int d = 0; d <= 3; d++)
									{
										b = first.getRelative(-d, h, l);
										if(b.getTypeId() == 35)
										{
											A+=2;
											if(b.getRelative(-1, 0, 0).getTypeId() == 35) A-=3;
											else if(move) if(b.getRelative(1, 0, 0).getTypeId() == 0) if(Math.random()<0.0000000008*windPower*windPower*windPower*windPower) b.setTypeIdAndData(0, (byte) 0, true);
										}
									}
								}
							}
						}
						if(data == 4)
						{
							first = getMain().getRelative(0, -H, (L-1)/2);
							for(int l = 0; l <= L; l++)
							{
								for(int h = H; 0 <= h; h--)
								{
									for(int d = 0; d <= 3; d++)
									{
										b = first.getRelative(d, h, -l);
										if(b.getTypeId() == 35)
										{
											A+=2;
											if(b.getRelative(1, 0, 0).getTypeId() == 35) A-=3;
											else if(move) if(b.getRelative(-1, 0, 0).getTypeId() == 0) if(Math.random()<0.0000000008*windPower*windPower*windPower*windPower) b.setTypeIdAndData(0, (byte) 0, true);
										}
									}
								}
							}
						}
					}
					else if(type.equals(SailType.TRIANGLE))//FIXME
					{
						first = getFirst();
						final int lfl = getLimitForInt();
						for(int l = 0; l <= L - lfl; l++)
							for(int h = 0; h <= H; h++)
							{
								b = getSailBlock(l, h, 0, first);
								if(b.getTypeId() == 35)
								{
									A += 2;
									if(move) if(Math.random()<0.0000000005*windPower*windPower*windPower*windPower) b.setTypeIdAndData(0, (byte) 0, true);
								}
							}
					}
					else if(type.equals(SailType.FOREANDAFT))
					{
						first = getFirst();
						final int lfl = getLimitForInt();
						for(int l = 0; l <= L - lfl; l++)
							for(int h = 0; h <= H; h++)
							{
								b = getSailBlock(l, h, 0, first);
								if(b.getTypeId() == 35)
								{
									A += 2;
									if(move) if(Math.random()<0.0000000008*windPower*windPower*windPower*windPower) b.setTypeIdAndData(0, (byte) 0, true);
								}
							}
					}
					else if(type.equals(SailType.REAR))
					{
						first = getFirst();
						for(int l = 0; l <= L; l++)
							for(int h = 0; h <= H; h++)
							{
								b = getSailBlock(l, h, 0, first);;
								if(b.getTypeId() == 35)
								{
									A += 2;
									if(move) if(Math.random()<0.000000001*windPower*windPower*windPower*windPower) b.setTypeIdAndData(0, (byte) 0, true);
								}
							}
					}
				}
			}
			else destroy();
		}
		int power = (int) (A*wd*1);
		if(haveLeft) if(left.getTypeId() != 68) power = (int) (0.8*power);
		if(haveRight) if(right.getTypeId() != 68) power = (int) (0.8*power);
		if(signBlock.getTypeId() != 68) power = (int) (0.5*power);
		if(0 < power) return (int) (A*wd*(windPower-ship.realS));
		else return (int) sqrt(power);
	}
	public double getDifferent()
	{
		double globalDirection = ship.direction + getDegrees(ship.data) - windDirection;
		return (double) Math.abs(sqrt(windPower-ship.realS)*Math.cos(Math.toRadians(globalDirection)));
	}
	public double getWd()
	{
		if(type.equals(SailType.SQUARE)) return 100*sqrt(sqrt(getCos() + 0.970));
		else if(type.equals(SailType.FRONT)) return 110*sqrt(sqrt(getCos() + 0.975));
		else if(type.equals(SailType.TRIANGLE)) return 70*sqrt(sqrt(sqrt(getCos() + 0.9995)));
		else if(type.equals(SailType.FOREANDAFT)) return 80*sqrt(sqrt(sqrt(getCos() + 0.9996)));
		else if(type.equals(SailType.REAR)) return 90*sqrt(sqrt(sqrt(getCos() + 0.9995)));
		else return 0;
	}
	public double getCos()
	{
		double globalDirection = ship.direction + getDegrees(ship.data) - windDirection;
		return (double) (Math.cos(Math.toRadians(globalDirection)));
	}
	public void close(boolean force)
	{
		if(open) 
		{
			if(force || left != null && right != null && signBlock.getType().equals(Material.WALL_SIGN))
			{
				byte data = ship.data;
				wool.clear();
				open = false;
				Block first = null;
				Block b;
				if(type.equals(SailType.SQUARE) || type.equals(SailType.FRONT))
				{
					if(data == 3)
					{
						first = getMain().getRelative((L-1)/2, -H, 0);
						for(int l = 0; l <= L; l++)
						{
							for(int h = H; 0 <= h; h--)
							{
								for(int d = 0; d <= 3; d++)
								{
									b = first.getRelative(-l, h, -d);
									//I.sendBlockChange(b.getLocation(), 9, (byte) 0);
									wool.add(new SailBlock(b));
									if(b.getTypeId() == 35) b.setTypeIdAndData(0, (byte) 0, true);
								}
							}
						}
					}
					if(data == 2)
					{
						first = getMain().getRelative(-(L-1)/2, -H, 0);
						for(int l = 0; l <= L; l++)
						{
							for(int h = H; 0 <= h; h--)
							{
								for(int d = 0; d <= 3; d++)
								{
									b = first.getRelative(l, h, d);
									//I.sendBlockChange(b.getLocation(), 9, (byte) 0);
									wool.add(new SailBlock(b));
									if(b.getTypeId() == 35) b.setTypeIdAndData(0, (byte) 0, true);
								}
							}
						}
					}
					if(data == 5)
					{
						first = getMain().getRelative(0, -H, -(L-1)/2);
						for(int l = 0; l <= L; l++)
						{
							for(int h = H; 0 <= h; h--)
							{
								for(int d = 0; d <= 3; d++)
								{
									b = first.getRelative(-d, h, l);
									//I.sendBlockChange(b.getLocation(), 9, (byte) 0);
									wool.add(new SailBlock(b));
									if(b.getTypeId() == 35) b.setTypeIdAndData(0, (byte) 0, true);
								}
							}
						}
					}
					if(data == 4)
					{
						first = getMain().getRelative(0, -H, (L-1)/2);
						for(int l = 0; l <= L; l++)
						{
							for(int h = H; 0 <= h; h--)
							{
								for(int d = 0; d <= 3; d++)
								{
									b = first.getRelative(d, h, -l);
									//I.sendBlockChange(b.getLocation(), 9, (byte) 0);
									wool.add(new SailBlock(b));
									if(b.getTypeId() == 35) b.setTypeIdAndData(0, (byte) 0, true);
								}
							}
						}
					}
				}
				else if(type.equals(SailType.TRIANGLE))//FIXME
				{
					first = getFirst();
					final int lfl = getLimitForInt();
					for(int l = 0; l <= L - lfl; l++)
						for(int h = 0; h <= H; h++)
						{
							b = getSailBlock(l, h, 0, first);
							wool.add(new SailBlock(b));
							if(b.getTypeId() == 35) b.setTypeIdAndData(0, (byte) 0, true);
						}
				}
				else if(type.equals(SailType.FOREANDAFT))
				{
					first = getFirst();
					final int lfl = getLimitForInt();
					for(int l = 0; l <= L - lfl; l++)
						for(int h = 0; h <= H; h++)
						{
							b = getSailBlock(l, h, 0, first);
							wool.add(new SailBlock(b));
							if(b.getTypeId() == 35) b.setTypeIdAndData(0, (byte) 0, true);
						}
				}
				else if(type.equals(SailType.REAR))
				{
					first = getFirst();
					for(int l = 0; l <= L; l++)
						for(int h = 0; h <= H; h++)
						{
							b = getSailBlock(l, h, 0, first);
							wool.add(new SailBlock(b));
							if(b.getTypeId() == 35) b.setTypeIdAndData(0, (byte) 0, true);
						}
				}
			}
		}
	}
	public int findLength()
	{
		int mid = getMain().getTypeId();
		byte data = ship.data;
		if(type.equals(SailType.SQUARE) || type.equals(SailType.FRONT))
		{
			if(mid == 5 || mid == 17)
			{
				if(data == 5 || data == 4) //Z
				{
					for(int l = 1; l<=ship.maxLengthOfSail+1; l++)
					{
						int id = getMain().getRelative(0,0,l).getTypeId();
						if(mid != id)
						{
							return l*2+4;
						}
					}
				}
				else
				{
					if(data == 3 || data == 2) //Z
					{
						for(int l = 1; l<=ship.maxLengthOfSail+1;l++)
						{
							int id = getMain().getRelative(l,0,0).getTypeId();
							if(mid != id)
							{
								return l*2+4;
							}
						}
					}
				}
			}
		}
		else if(type.equals(SailType.TRIANGLE) || type.equals(SailType.REAR)) return Math.abs(maht.b.getX() - getMain().getX() + maht.b.getZ() - getMain().getZ()) - 1;//Аккуратнее
		else if(type.equals(SailType.FOREANDAFT))
		{
			Block newB = null;
			Maht mahtf = maht.getPrevious();
			if(mahtf != null)
			{
				newB = world.getBlockAt(mahtf.b.getX(), signBlock.getY(), mahtf.b.getZ());
				return Math.abs(newB.getX() - getMain().getX() + newB.getZ() - getMain().getZ());
			}
		}
		return 0;
	}
	public int findHeight()
	{
		if(type.equals(SailType.SQUARE)) 
		{
			int id = getMain().getTypeId();
			byte data = getMain().getData();
			for(int h = 1; h<=ship.maxHeightOfSail+1; h++) if(getMain().getRelative(0, -h, 0).getTypeId() == id && getMain().getRelative(0, -h, 0).getData() == data || getMain().getRelative(0, -h, 0).getTypeId() != 0 && getMain().getRelative(0, -h, 0).getTypeId() != 35) return h-1;
		}
		else if(type.equals(SailType.TRIANGLE) || type.equals(SailType.FOREANDAFT) || type.equals(SailType.REAR))
		{
			Block b = maht.b.getWorld().getBlockAt(maht.b.getX(), signBlock.getY(), maht.b.getZ());
			//Bukkit.getPlayer("Test").sendBlockChange(b.getLocation(), 20, (byte) 0);
			for(int i = 0; i < 50; i ++)
			{
				Block newB = b.getRelative(0, i, 0);
				final int mid = maht.b.getTypeId();
				final byte md = maht.b.getData();
				final int id = newB.getTypeId();
				final byte d = newB.getData();
				if(id != mid || d != md) return i-1;
			}
		}
		if(type.equals(SailType.FRONT)) return 5;
		return 0;
	}
	public Block getSailBlock(int l, int h, int d, Block first)
	{
		byte data = ship.data;
		if(data == 2) return first.getRelative(0, h, -l);
		if(data == 3) return first.getRelative(0, h, l);
		if(data == 4) return first.getRelative(-l, h, 0);
		if(data == 5) return first.getRelative(l, h, 0);
		return null;
	}
	public int getLimitForInt()
	{
		if(type.equals(SailType.REAR) || type.equals(SailType.SQUARE) || type.equals(SailType.FOREANDAFT)) return 0;//На всякий случай
		if(0 < maht.sails.size()) return 4;
		return 0;
	}
	public Block getFirst()
	{
		byte data = ship.data;
		if(type.equals(SailType.TRIANGLE)) return getMain().getRelative(0, 1, 0);
		else if(type.equals(SailType.REAR)) 
		{
			if(data == 2) return getMain().getRelative(0, 1, L);
			else if(data == 3) return getMain().getRelative(0, 1, -L);
			else if(data == 4) return getMain().getRelative(L, 1, 0); 
			else if(data == 5) return getMain().getRelative(-L, 1, 0);
		}
		else if(type.equals(SailType.FOREANDAFT)) return signBlock;
		return null;
	}
	public Block getMain()
	{
		byte data = ship.data;
		if(type.equals(SailType.SQUARE) || type.equals(SailType.FOREANDAFT) || type.equals(SailType.TRIANGLE) || type.equals(SailType.FRONT))
		switch(data)
		{
			case 4:
			{
				return signBlock.getRelative(-1,0,0);
			}
			case 5:
			{
				return signBlock.getRelative(1,0,0);
			}
			case 2:
			{
				return signBlock.getRelative(0,0,-1);
			}
			case 3:
			{
				return signBlock.getRelative(0,0,1);
			}
		}
		else switch(data)
			{
				case 4:
				{
					return signBlock.getRelative(1,0,0);
				}
				case 5:
				{
					return signBlock.getRelative(-1,0,0);
				}
				case 2:
				{
					return signBlock.getRelative(0,0,1);
				}
				case 3:
				{
					return signBlock.getRelative(0,0,-1);
				}
			}
		return null;
	}
	public Block getRelativeToMain(int i)
	{
		byte data = ship.data;
		if(type.equals(SailType.SQUARE) || type.equals(SailType.FOREANDAFT) || type.equals(SailType.TRIANGLE) || type.equals(SailType.FRONT))
		switch(data)
		{
			case 4:
			{
				return signBlock.getRelative(-i,0,0);
			}
			case 5:
			{
				return signBlock.getRelative(i,0,0);
			}
			case 2:
			{
				return signBlock.getRelative(0,0,-i);
			}
			case 3:
			{
				return signBlock.getRelative(0,0,i);
			}
		}
		else switch(data)
			{
				case 4:
				{
					return signBlock.getRelative(i,0,0);
				}
				case 5:
				{
					return signBlock.getRelative(-i,0,0);
				}
				case 2:
				{
					return signBlock.getRelative(0,0,i);
				}
				case 3:
				{
					return signBlock.getRelative(0,0,-i);
				}
			}
		return null;
	}
	public void repair()
	{
		try
		{
			if(signBlock.getTypeId() == 68)
			{
				Sign sb = (Sign) signBlock.getState();
				sb.setLine(0, sailS);
				sb.setLine(1, type.toString().toLowerCase());
				sb.setLine(2, ship.name);
				sb.setLine(3, ChatColor.WHITE + "Size: " + String.valueOf(L*H));
				sb.update();
			}
			if(haveLeft) if(left.getTypeId() == 68)
			{
				Sign sb = (Sign) left.getState();
				sb.setLine(0, lt);
				if(open)
				{
					sb.setLine(1, openS);
				}
				else
				{
					sb.setLine(1, closeS);
				}
				sb.setLine(3, t);
				sb.update();
			}
			if(haveRight) if(right.getTypeId() == 68)
			{
				Sign sb = (Sign) right.getState();
				sb.setLine(0, rt);
				if(open)
				{
					sb.setLine(1, openS);
				}
				else
				{
					sb.setLine(1, closeS);
				}
				sb.setLine(3, t);
				sb.update();
			}
		}
		catch(NullPointerException e)
		{
			if(signBlock != null) signBlock.breakNaturally();
			if(haveLeft) if(left != null) left.breakNaturally();
			if(haveRight) if(right != null) right.breakNaturally();
		}
	}
	public void Break()
	{
		open(false);
		if(maker != null) maker.sendMessage(ChatColor.RED + "[iSail] Creating of sail was failed!");
		maker = null;
		signBlock.breakNaturally();
		if(haveLeft) left.breakNaturally();
		if(haveRight) right.breakNaturally();
	}
	public void disable()
	{
		open(false);
		this.maker = null;
		Sign s;
		try
		{
		if(signBlock.getTypeId() == 68)
		{
			s = (Sign) signBlock.getState();
			s.setLine(0, "s");
			s.setLine(1, type.toString());
			s.setLine(2, refreshString);
			s.setLine(3, null);
			s.update();
		}
		if(haveLeft)
		{
			if(left.getTypeId() == 68)
			{
				if(left != null)
				{
					s = (Sign) left.getState();
					s.setLine(0, "lt");
					s.setLine(1, refreshString);
					s.setLine(2, null);
					s.setLine(3, null);
					s.update();
				}
			}
		}
		if(haveRight)
		{
			if(right.getTypeId() == 68)
			{
				if(right != null)
				{
					s = (Sign) right.getState();
					s.setLine(0, "rt");
					s.setLine(1, refreshString);
					s.setLine(2, null);
					s.setLine(3, null);
					s.update();
				}
			}
		}
		}
		catch (NullPointerException e)
		{
			_log.info("[iSail] Error occupied while disbaling sail " + type.toString().toLowerCase() +" on mast " + maht.toString().toLowerCase() + " on ship " + ship.name + "!");
		}
		if(type.equals(SailType.FRONT)) ship.front = null;
		else if(type.equals(SailType.SQUARE))
		{
			for(int i = 0; i < maht.sails.size(); i++)
			{
				Sail sail = maht.sails.get(i);
				if(sail.signBlock.equals(signBlock)) maht.sails.remove(i);
			}
		}
		else if(type.equals(SailType.TRIANGLE)) maht.triangle = null;
		else if(type.equals(SailType.FOREANDAFT)) maht.foreAndAft = null;
		else if(type.equals(SailType.REAR)) maht.rear = null;
	}
	public void turnRight()
	{
		final Block relative = ship.relative;
		int x = signBlock.getX() - relative.getX();
		int y = signBlock.getY() - relative.getY();
		int z = signBlock.getZ() - relative.getZ();
		signBlock = relative.getRelative(-z, y, x);
		//I.sendBlockChange(signBlock.getLocation(), 35, (byte) 1);
		if(haveLeft)
		{
			x = left.getX() - relative.getX();
			y = left.getY() - relative.getY();
			z = left.getZ() - relative.getZ();
			left = relative.getRelative(-z, y, x);
			//I.sendBlockChange(left.getLocation(), 35, (byte) 2);
		}
		if(haveRight)
		{
			x = right.getX() - relative.getX();
			y = right.getY() - relative.getY();
			z = right.getZ() - relative.getZ();
			right = relative.getRelative(-z, y, x);
			//I.sendBlockChange(right.getLocation(), 35, (byte) 3);
		}
	}
	public void turnLeft()
	{
		final Block relative = ship.relative;
		int x = signBlock.getX() - relative.getX();
		int y = signBlock.getY() - relative.getY();
		int z = signBlock.getZ() - relative.getZ();
		signBlock = relative.getRelative(z, y, -x);
		//I.sendBlockChange(signBlock.getLocation(), 35, (byte) 1);
		if(haveLeft)
		{
			x = left.getX() - relative.getX();
			y = left.getY() - relative.getY();
			z = left.getZ() - relative.getZ();
			left = relative.getRelative(z, y, -x);
			//I.sendBlockChange(left.getLocation(), 35, (byte) 2);
		}
		if(haveRight)
		{
			x = right.getX() - relative.getX();
			y = right.getY() - relative.getY();
			z = right.getZ() - relative.getZ();
			right = relative.getRelative(z, y, -x);
			//I.sendBlockChange(right.getLocation(), 35, (byte) 3);
		}
	}
	public boolean Change(Block b, Action a)
	{
		boolean changed = false;
		if(haveLeft && haveRight)
		{
			if(left != null && right != null && signBlock != null)
			{
				if(left.getType().equals(Material.WALL_SIGN) && right.getType().equals(Material.WALL_SIGN) && signBlock.getType().equals(Material.WALL_SIGN))
				{
					Sign ls = (Sign) left.getState();
					Sign rs = (Sign) right.getState();
					if(left.equals(b))
					{
						changed = true;
						if(a.equals(Action.LEFT_CLICK_BLOCK))
						{
							li -= (double) ((double) 2000/(double) hard);
							if(li < 0 ) li = (double) 0;
						}
						else 
						{
							li += (double) ((double) 1000/(double) hard);
							if(100 < li) li = (double) 100;
						}
						ls.setLine(2, String.valueOf(li));
						if(li == 0 && ri ==0 )
						{
							close(false);
						}
					else if(li == 100 && ri == 100)
						{
							open(false);
						}
					}
					if(right.equals(b))
					{
						changed = true;
						if(a.equals(Action.LEFT_CLICK_BLOCK))
						{
							ri -= (double) ((double) 2000/(double) hard);
							if(ri < 0 ) ri = (double) 0;
						}
						else 
						{
							ri += (double) ((double) 1000/(double) hard);
							if(100 < ri) ri = (double) 100;
						}
						rs.setLine(2, String.valueOf(ri));
						if(li == 0 && ri == 0 )
						{
							close(false);
						}
					else if(li == 100 && ri == 100)
						{
							open(false);
						}
					}
					if(open)
					{
						rs.setLine(1, openS);
						ls.setLine(1, openS);
					}
					if(!open)
					{
						rs.setLine(1, closeS);
						ls.setLine(1, closeS);
					}
					rs.update(true);
					ls.update(true);
				}
			}
		}
		return changed;
	}
	public boolean isMakerOfSail(Player p)
	{
		return maker != null && maker.getName().equals(p.getName());
	}
	public void move()
	{
		Vector v = ship.v;
		signBlock = signBlock.getLocation().add(v).getBlock();
		if(haveLeft) left = left.getLocation().add(v).getBlock();
		if(haveRight) right = right.getLocation().add(v).getBlock();
		if(signBlock.getTypeId() != 68) destroy();
	}
	public void destroy()
	{
		open(true);
		byte data = ship.data;
		wool.clear();
		open = false;
		Block first = null;
		Block b;
		if(type.equals(SailType.SQUARE) || type.equals(SailType.FRONT))
		{
			if(data == 3)
			{
				first = getMain().getRelative((L-1)/2, -H, 0);
				for(int l = 0; l <= L; l++)
				{
					for(int h = H; 0 <= h; h--)
					{
						for(int d = 0; d <= 3; d++)
						{
							b = first.getRelative(-l, h, -d);
							//I.sendBlockChange(b.getLocation(), 9, (byte) 0);
							wool.add(new SailBlock(b));
							if(b.getTypeId() == 35) b.breakNaturally();
						}
					}
				}
			}
			if(data == 2)
			{
				first = getMain().getRelative(-(L-1)/2, -H, 0);
				for(int l = 0; l <= L; l++)
				{
					for(int h = H; 0 <= h; h--)
					{
						for(int d = 0; d <= 3; d++)
						{
							b = first.getRelative(l, h, d);
							//I.sendBlockChange(b.getLocation(), 9, (byte) 0);
							wool.add(new SailBlock(b));
							if(b.getTypeId() == 35) b.breakNaturally();
						}
					}
				}
			}
			if(data == 5)
			{
				first = getMain().getRelative(0, -H, -(L-1)/2);
				for(int l = 0; l <= L; l++)
				{
					for(int h = H; 0 <= h; h--)
					{
						for(int d = 0; d <= 3; d++)
						{
							b = first.getRelative(-d, h, l);
							//I.sendBlockChange(b.getLocation(), 9, (byte) 0);
							wool.add(new SailBlock(b));
							if(b.getTypeId() == 35) b.breakNaturally();
						}
					}
				}
			}
			if(data == 4)
			{
				first = getMain().getRelative(0, -H, (L-1)/2);
				for(int l = 0; l <= L; l++)
				{
					for(int h = H; 0 <= h; h--)
					{
						for(int d = 0; d <= 3; d++)//TODO
						{
							b = first.getRelative(d, h, -l);
							//I.sendBlockChange(b.getLocation(), 9, (byte) 0);
							wool.add(new SailBlock(b));
							if(b.getTypeId() == 35) b.breakNaturally();
						}
					}
				}
			}
		}
		else if(type.equals(SailType.TRIANGLE))//FIXME
		{
			first = getFirst();
			final int lfl = getLimitForInt();
			for(int l = 0; l <= L - lfl; l++)
				for(int h = 0; h <= H; h++)
				{
					b = getSailBlock(l, h, 0, first);
					wool.add(new SailBlock(b));
					if(b.getTypeId() == 35) b.breakNaturally();
				}
		}
		else if(type.equals(SailType.FOREANDAFT))
		{
			first = getFirst();
			final int lfl = getLimitForInt();
			for(int l = 0; l <= L - lfl; l++)
				for(int h = 0; h <= H; h++)
				{
					b = getSailBlock(l, h, 0, first);
					wool.add(new SailBlock(b));
					if(b.getTypeId() == 35) b.breakNaturally();
				}
		}
		else if(type.equals(SailType.REAR))
		{
			first = getFirst();
			for(int l = 0; l <= L; l++)
				for(int h = 0; h <= H; h++)
				{
					b = getSailBlock(l, h, 0, first);
					wool.add(new SailBlock(b));
					if(b.getTypeId() == 35) b.breakNaturally();
				}
		}
		disable();
	}
}