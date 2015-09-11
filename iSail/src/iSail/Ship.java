package iSail;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import at.pavlov.cannons.cannon.Cannon;


public class Ship extends iSail
{
							String name;
							Location l;
							int seed;
							int turn = 0;
							Block attacked = null;
							Block attacking = null;
							Block relative = null;
							int maxLengthOfSail;
							int maxHeightOfSail;
							int task = 0;
							int S = 1;
							double realS = 0;
							int power = 0;
							int m=1;
							int A=0;
							int DY = 0;
							int water=0;
							int N=0;
							int corpus=0;
							int hp=0;
							int maxHp=0;
							int amount=0;
							int phase = 0;
							float energy = 0;
							int direction = 0;
							int dirB = 0;
							int wheelRotating = 0;
							boolean created=false;
							boolean sinking=false;
							boolean destroyed=false;
							boolean boarded = false;
							boolean ported = false;
							boolean reloaded;
							//int dx;
							//int dz;
							int anchors=0;
							byte data;
							Sail front = null;
							Maht fok = null;
							Maht grot = null;
							Maht bizan = null;
							List<String> sleeps = new ArrayList<String>();
							List<Entity> entityList = new ArrayList<Entity>();
							TreeMap<UUID, Entity> ek = new TreeMap<UUID, Entity>();
							List<Entity> le = new ArrayList<Entity>();
							List<Player> localPlayers = new ArrayList<Player>();
							List<Player> ecipage = new ArrayList<Player>();
							TreeMap<String, Block> blocks = new TreeMap<String, Block>();
							TreeMap<String, Integer> cblocks = new TreeMap<String, Integer>();
							TreeMap<String, Integer> ablocks = new TreeMap<String, Integer>();
							List<Block> pb = new ArrayList<Block>();
							List<Block> hb = new ArrayList<Block>();
							List<BlockType> types = new ArrayList<BlockType>();
							List<BlockType> htypes = new ArrayList<BlockType>();
							Vector v;
									public Ship(Location l, String name, byte data, boolean refresh) //By player
									{
										this.l = l;
										this.name = name;
										this.data = data;
										pickBlocks();
										if(!refresh)
										{
											this.hp = corpus/2;
											this.maxHp = hp;
										}
										else
										{
											ek.clear();
											ecipage.clear();
										}
										this.maxLengthOfSail = (int) (Math.sqrt(amount)/2);
										this.maxHeightOfSail = (int) (maxLengthOfSail*1.5);
										//this.waterLine = l.getWorld().getSeaLevel();
										seed = ships.size();
									}
public void startMove()
{
	phase++;
	setPower();
	setRealS();
	int dr = 0;
	int hard = (int) Math.ceil(m/1000);
	energy = (float) (realS*realS);
	if(energy < 1) energy = 1;
	if(isHeavy() && !sinking)
	{
		if(0 < wheelRotating) 
		{
			dr += (int) ((double) ((double) 3*(double) realS)+2)*wheelRotating*wheelRotating*77/sqrt(hard);
			//wheelRotating -= Math.random()*Math.sqrt(dr);
		}
		else if(wheelRotating < 0) 
		{
			dr -= (int) ((double) ((double) 3*(double) realS)+2)*Math.abs((int) wheelRotating*wheelRotating*77/sqrt(hard));
			//wheelRotating += Math.random()*Math.sqrt(Math.sqrt(dr*dr));
		}
		dirB += dr;
		direction = dirB/1000;
		if(isTurning())
		{//Поворот
			boolean haveWater = setWaterLine();
			if(haveWater)
			{
				final boolean successful = pickBlocks();
				if(successful)
				{
					boarded = isBoarded();
					if(!boarded)
					{
						if(isAlive())
						{
							if(50<=direction)
							{
								tryTurnRight();
								dirB = direction*1000;
							}
							else
							{
								tryTurnLeft();
								dirB = direction*1000;
							}
						}
					}
				}
			}
		}
		if(direction < -50) direction = -50;
		else if(50 < direction) direction = 50;
		if(l.getBlock().getRelative(0, 1, 0).getTypeId() == 68)
		{
			Sign s = (Sign) l.getBlock().getRelative(0, 1, 0).getState();
			int dirFix = direction + getDegrees(data);
			if(dirFix < 0) dirFix += 360;
			else if(360 < dirFix) dirFix -= 360;
			s.setLine(1, ChatColor.YELLOW + "Ship: " + String.valueOf(dirFix));
			s.setLine(2, ChatColor.AQUA + "Wind: " + String.valueOf(windDirection));
			if(s.getLine(0).isEmpty())
			{
				s.setLine(0, wheel);
				s.setLine(3, wheelD);
			}
			s.update();
			if(wheelIsUnholded())
			{
				if(wheelRotating < 0) rotateWheelRight(s);
				if(0 < wheelRotating) rotateWheelLeft(s);
			}
		}
	}
	if(phase < time)
	{
		if(attacked != null)
		{
			attacked.getWorld().createExplosion(attacked.getX(), attacked.getY(), attacked.getZ(), energy, false, true);
			attacked.breakNaturally();
			attacked = null;
			sendToLocalPlayers(new StringBuilder().append(ChatColor.BOLD).append(ChatColor.RED).append("[iSail] Ship ").append(name).append(" crashed on moving!!!").toString(), BroadcastType.ACCIDENT);
		}
		else if(attacking != null)
		{
			attacking.getWorld().createExplosion(attacking.getX(), attacking.getY(), attacking.getZ(), energy, false, true);
			attacking.breakNaturally();
			attacking = null;
		}
		if(time-3 <= phase)
		{
			ChatColor color = ChatColor.GREEN;
			if(phase == time-2) color = ChatColor.YELLOW;
			else if(phase == time-1) color = ChatColor.RED;
			sendToEcipage(new StringBuilder().append(ChatColor.BOLD).append(color).append("M: ").append(time-phase).append("!").toString(), BroadcastType.TIMER);
		}
		if(l.getBlock().getTypeId() == 68)
		{
			Sign s = (Sign) l.getBlock().getState();
			if(!(s.getLine(2).equals(ChatColor.WHITE + "Creating") || s.getLine(2).equals(ChatColor.WHITE + "Refreshing")))
			{
				if(0<S) s.setLine(2, speedString + String.valueOf(realS));
				else s.setLine(2, ChatColor.AQUA + "Stopped");
				//if(!iwater == 0) s.setLine(2, ChatColor.RED + "Need water");
				s.setLine(0, shipString);
				s.setLine(1, name);
				s.setLine(3, ChatColor.GREEN + String.valueOf(hp) + "/" + String.valueOf(maxHp));
				s.update();
			}
		}
	}
	else
	if(!destroyed)
	{
		if(created)
		{
			phase = 0;
			Block newB = null;
			v = null;
			water = 0;
			boolean haveWater = setWaterLine();
			boolean successful = false;
			if(haveWater)
			{
				successful = pickBlocks();
				if(successful)
				{
					boarded = isBoarded();
					//setPower();
					//setRealS();
					setS();
					v = getVector(S, true);
								if(!boarded)
								{
									if(isAlive())
									{
										hp = corpus - maxHp;
										if(maxHp < hp) hp = maxHp;
										if(isHeavy() || sinking)//Если корабль целый
										{
											if(isNeedsMoving() || DY < 0)
											{//Движение прямо
												findCollusions();
												l = l.add(v);
												removeBlocks('M');
												for(int i = 0; i<pb.size(); i++)
												{
													newB = pb.get(i).getLocation().add(v).getBlock();
													moveBlock(newB, i);
												}
												for(int i = 0; i<hb.size(); i++)
												{
													newB = hb.get(i).getLocation().add(v).getBlock();
													moveHBlock(newB, i);
												}
												moveEcipage();
												moveMahts();
												int dirFix = direction + getDegrees(data);
												if(dirFix < 0) dirFix += 360;
												else if(360 < dirFix) dirFix -= 360;
												broadcastMove((Player)entityList.get(0));//uses a member of the ship to base a center point off of when we broadcast message.
												//if(0 < S) sendToLocalPlayers(new StringBuilder().append(ChatColor.GOLD).append("[iSail]").append(ChatColor.AQUA).append(ChatColor.ITALIC).append(" ship ").append(name).append(" (x").append(l.getBlockX()).append("z").append(l.getBlockZ()).append(")").append(ChatColor.GREEN).append(" was moved with speed ").append(realS).append("(").append(S).append(" blocks) and direction ").append(dirFix).append(" degrees").toString(), BroadcastType.MOVE);
												//else if(dr != 0) sendToLocalPlayers(new StringBuilder().append(ChatColor.GOLD).append("[iSail]").append(ChatColor.AQUA).append(ChatColor.ITALIC).append(" ship ").append(name).append(" (x").append(l.getBlockX()).append("z").append(l.getBlockZ()).append(")").append(ChatColor.GREEN).append(" was changed direction to ").append(dirFix).append(" degrees").toString(), BroadcastType.MOVE);
											}
										}
										else startSinking();
									}
									else destroyShip();
								}
				}
			}
			if(!destroyed)
			{
				if(!l.getBlock().isEmpty())
				{
					if(l.getBlock().getType().equals(Material.WALL_SIGN))
					{
						Sign s = (Sign) l.getBlock().getState();
						if(0<S) s.setLine(2, speedString + String.valueOf(realS));
						else s.setLine(2, ChatColor.AQUA + "Stopped");
						if(boarded) s.setLine(2, ChatColor.RED + "Boarded");
						if(ecipage.size()*blocksPerPlayer<amount) sendToEcipage(new StringBuilder().append(ChatColor.RED).append(ChatColor.BOLD).append("[iSail] Need at least ").append(amount/blocksPerPlayer).append(" players, but you have ").append(ecipage.size()).append(" players!").toString(), BroadcastType.ACCIDENT);
						if(!successful) sendToEcipage(ChatColor.RED +"Out of size", BroadcastType.ACCIDENT);
						//if(!iwater == 0) s.setLine(2, ChatColor.RED + "Need water");
						s.setLine(0, shipString);
						s.setLine(1, name);
						s.setLine(3, ChatColor.GREEN + String.valueOf(hp) + "/" + String.valueOf(maxHp));
						s.update();
					}
				}
				if(l.getBlock().getRelative(0, 1, 0).getTypeId() == 68)
				{
					Sign s = (Sign) l.getBlock().getRelative(0, 1, 0).getState();
					int dirFix = direction + getDegrees(data);
					if(dirFix < 0) dirFix += 360;
					else if(360 < dirFix) dirFix -= 360;
					s.setLine(1, ChatColor.YELLOW + "Ship: " + String.valueOf(dirFix));
					s.setLine(2, ChatColor.AQUA + "Wind: " + String.valueOf(windDirection));
					s.update();
				}
				maxLengthOfSail = (int) (Math.sqrt(amount)/2);
				maxHeightOfSail = (int) (maxLengthOfSail*1.5);
			}
		}
		else created = true;
	}
}
		public void setPower() 
		{
			power = 0;
			if(!(ecipage.size()*blocksPerPlayer <= amount || sinking))
			{
				if(front != null) power += front.getPower(true);
				if(fok != null) power += fok.getPower();
				if(grot != null) power += grot.getPower();
				if(bizan != null) power += bizan.getPower();
			}
		}
		public boolean isBoarded()
		{
			if(ported) return true;
			if(1000 < anchors)
			{
				le.clear();
				ecipage.clear();
				return true;
			}
			for(int i = 0; i < ships.size(); i++)
			{
				Ship ship = ships.get(i);
				if(!ship.name.equals(name))
				{
					Block b = ship.l.getBlock();
					if(blocks.containsKey(new StringBuilder("x").append(b.getX()).append("y").append(b.getY()).append("z").append(b.getZ()).toString())) return true;
					for(int s = 0; s < 15; s++)
					{
						Block b1 = ship.l.getBlock().getRelative(0, s, 0);
						if(blocks.containsKey(new StringBuilder("x").append(b1.getX()).append("y").append(b1.getY()).append("z").append(b1.getZ()).toString())) return true;
						Block b2 = ship.l.getBlock().getRelative(0, s, 0);
						if(blocks.containsKey(new StringBuilder("x").append(b2.getX()).append("y").append(b2.getY()).append("z").append(b2.getZ()).toString())) return true;
					}
				}
			}
			return false;
		}
		public void broadcastMove(Player crewPlayer)
		{
			int radius = (Integer) this.getConfig().get("LocalBroadcastMessageRadiusInBlocks");
			for(Entity ent : crewPlayer.getNearbyEntities(radius, radius, radius)){
				if(ent instanceof Player){
					Player p = (Player)ent;
					p.sendMessage(new StringBuilder().append("[iSail] Ship " + name + " (X:" + l.getBlockX() + ", Z:" + l.getBlockZ() + ", distance:" + (int) p.getLocation().distance(l) + ") has moved ").append(S).append(" blocks with speed ").append(realS).append(" m/s and a direction of ").append(direction + getDegrees(data)).append(" degrees relative to North and ").append(relative).append(" degrees relative to you!").toString());
				}
			}
			
			/*if(localPlayers != null) for(int i = 0; i < localPlayers.size(); i++)
			{
				Player p = localPlayers.get(i);
				int relative = getPlayerOffset(p);
				p.sendMessage(new StringBuilder().append("[iSail] Ship " + name + " (X:" + l.getBlockX() + ", Z:" + l.getBlockZ() + ", distance:" + (int) p.getLocation().distance(l) + ") has moved ").append(S).append(" blocks with speed ").append(realS).append(" m/s and a direction of ").append(direction + getDegrees(data)).append(" degrees relative to North and ").append(relative).append(" degrees relative to you!").toString());
			}
			*/
		}
		public double getGlobalDirection(Player p)
		{
			return p.getLocation().getYaw() - 180;
		}
		public int getGlobalDirection()
		{
			return direction + getDegrees(data);
		}
		public int getShipOffset(Location l)
		{
			//Vector v = new Vector
			return direction + getDegrees(data);
		}
		public int getPlayerOffset(Player p)
		{
			return (int) (getGlobalDirection(p));
		}
		public int getDifferentOfDirections(Player p)
		{
			return (int) (getGlobalDirection(p) - getGlobalDirection());
		}
		public void sendLocation(Player p)
		{
			Location pl = p.getLocation();
			int relative = getPlayerOffset(p);
			p.sendMessage(new StringBuilder().append("[iSail] Ship " + name + " (X:" + l.getBlockX() + ", Z:" + l.getBlockZ() + ", distance:" + (int) p.getLocation().distance(l) + ") was moved for ").append(S).append(" have speed ").append(realS).append(" m/s and direction ").append(direction + getDegrees(data)).append(" degrees relative to North and ").append(relative).append(" degrees relative to you!").toString());
		}
		public void sendToLocalPlayers(Block b)
		{
			if(localPlayers != null) for(int i = 0; i < localPlayers.size(); i++)
			{
				Player p = localPlayers.get(i);
				p.sendBlockChange(b.getLocation(), b.getTypeId(), b.getData());
			}
		}
		public void sendToLocalPlayers(String s, BroadcastType bt)
		{
			if(localPlayers != null) for(int i = 0; i < localPlayers.size(); i++)
			{
				Player p = localPlayers.get(i);
				if(canBroadcast(p, bt)) p.sendMessage(s);
			}
		}
		public void sendToEcipage(String message, BroadcastType bt)
		{
			if(ecipage != null)
			{
				for(int i = 0; i < ecipage.size(); i ++)
				{
					Player p = ecipage.get(i);
					if(canBroadcast(p, bt))  p.sendMessage(message);
				}
			}
		}
		public boolean getAnchors()
		{
			for(int i = 1; i<7; i++)
			{
				Block b1 = this.l.getBlock().getRelative(i,0,0);
				Block b2 = this.l.getBlock().getRelative(-i,0,0);
				Block b3 = this.l.getBlock().getRelative(0,0,i);
				Block b4 = this.l.getBlock().getRelative(0,0,-i);
				if(b1.getType().equals(Material.WALL_SIGN))
				{
					Sign s = (Sign) b1.getState();
					if(s.getLine(0).equals(downAnchor)) return true;
				}
				if(b2.getType().equals(Material.WALL_SIGN))
				{
					Sign s = (Sign) b2.getState();
					if(s.getLine(0).equals(downAnchor)) return true;
				}
				if(b3.getType().equals(Material.WALL_SIGN))
				{
					Sign s = (Sign) b3.getState();
					if(s.getLine(0).equals(downAnchor)) return true;
				}
				if(b4.getType().equals(Material.WALL_SIGN))
				{
					Sign s = (Sign) b4.getState();
					if(s.getLine(0).equals(downAnchor)) return true;
				}
			}
			return false;
		}
		public void findCollusions()
		{
			if(0<S)
			{
				cblocks.clear();
				ablocks.clear();
				Block b;
				Block newB;
				Vector lv;
				for(int k = 0; k<pb.size(); k++)
				{
					b = pb.get(k);
					for(int i = 0; i<S+2; i++)
					{
						lv = getVector(i, true);
						newB = b.getLocation().add(lv).getBlock();
						final int id = newB.getTypeId();
						if(!cblocks.containsKey(getKey(newB)))
						{
							cblocks.put(getKey(newB), i);//TODO
						}
						if(!(isea(id)))
						{
							if(!blocks.containsKey(new StringBuilder("x").append(newB.getX()).append("y").append(newB.getY()).append("z").append(newB.getZ()).toString()))
							{
								realS = 0;
								if(i - 2 < S)
								{
									S = i-2;
									if(!(id == 1 || id == 2 || id == 3 || id == 4 || id == 12 || id == 13)) attacked = newB;
									attacking = b;
									v = getVector(S, sinking);
								}
								if(S<2)
								{
									S = 0;
									v = getVector(0, sinking);
								}
							}
						}
					}
				}
				for(int k = 0; k<pb.size(); k++)
				{
					b = pb.get(k);
					for(int i = 0; i<S; i++)
					{
						lv = getVector(i, true);
						newB = b.getLocation().add(lv).getBlock();
						if(cblocks.containsKey(getKey(newB)))
						{
							ablocks.put(getKey(newB), i);//TODO
						}
					}
				}
				for(int i = 0; i < le.size(); i++)
				{
					Entity e = le.get(i);
					if(e.getType().equals(EntityType.PLAYER))
					{
						if(!ek.containsKey(e.getUniqueId()))
						{
							Block b1 = e.getLocation().getBlock();
							Block b2 = e.getLocation().getBlock().getRelative(0, 1, 0);
							if(ablocks.containsKey(getKey(b1)) || ablocks.containsKey(getKey(b2)))
							{
								Player p = (Player) e;
								p.setHealth(0);
								p.sendMessage(ChatColor.RED + "[iSail] You was killed by ship " + name + "!");
								sendToLocalPlayers(ChatColor.RED + "[iSail] Ship " + name + " killed player " + p.getName() +  "!", BroadcastType.ACCIDENT);
								_log.info("[iSail] Ship " + name + " killed player " + p.getName() +  "!");
							}
						}
					}
				}
			}
		}
		public void moveBlock(Block newB, int i)
		{
			BlockType bt = types.get(i);
			int id = bt.id;
			int nid = newB.getTypeId();
			byte dta = bt.data;
			if(isea(nid)) newB.setTypeIdAndData(id, dta, true);
		}
		public void moveHBlock(Block newB, int i)
		{
			BlockType bt = htypes.get(i);
			int id = bt.id;
			int nid = newB.getTypeId();
			byte dta = bt.data;
			String lines[] = bt.lines;
			if(isea(nid)) if(newB.setTypeIdAndData(id, dta, false)) 
				if(newB.getTypeId() == 68)
					{
						Sign sss = (Sign) newB.getState();
						sss.setLine(0, lines[0]);
						sss.setLine(1, lines[1]);
						sss.setLine(2, lines[2]);
						sss.setLine(3, lines[3]);
						sss.update();
					}
		}
		public boolean ispa(Block b)
		{
			int id = b.getTypeId();
			if(id == 5 || id == 17 || id == 35 || id == 20 || id == 85)
			{
				for(int x = -1; x <= 1; x++)
					for(int y = -1; y <= 1; y++)
						for(int z = -1; z <= 1; z++)
						{
							Block newR = b.getRelative(x, y, z);
							id = newR.getRelative(x, y, z).getTypeId();
							if(!(id == 5 || id == 17 || id == 0 || id == 8 || id == 9 || id == 20 || id == 35 || id == 85)) return false;
						}
			}
			return true;
		}
		public boolean ista(int i)
		{
			return i == 17 || i == 27 || i == 96 || i == 66 || i == 108 || i == 109 || i == 114 || i == 128 || i == 134 || i == 135 || i == 136 || i == 28 || i == 68 || i == 53 || i == 135 || i == 136 || i == 137 || i == 50 || i == 65;
		}
		public void reMoveBlock(Block b, char dir)
		{
			final int id = b.getTypeId();
			byte data = b.getData();
			int M = 0;
			if(b.getY()<=waterLine) M=8;
			BlockType bt = new BlockType(id, data, null, dir);
			types.add(bt);
			b.setTypeIdAndData(M, (byte) 0, false);
		}
		public void reMoveHBlock(Block b, char dir)
		{
			int id = b.getTypeId();
			byte data = b.getData();
			final boolean sign = b.getType().equals(Material.WALL_SIGN);
			int M = 0;
			Sign s;
			if(b.getY()<=waterLine) M=8;
			String[] lines = null;
			if(sign)
			{
				s = (Sign) b.getState();
				lines = s.getLines().clone();
			}
			BlockType bt = new BlockType(id, data, lines, dir);
			htypes.add(bt);
			b.setTypeIdAndData(M, (byte) 0, false);
			if(sign) b.getState().update(true, true);
		}
		public void moveEcipage()
		{
			for(int i = 0; i < entityList.size(); i++)
			{
				Entity p = entityList.get(i);
				p.teleport(p.getLocation().add(v));
			}
		}
		/*public void setName(String name)
		{
			this.name = name;
		}*/
		public void setRealS()
		{
			double minus = (double) (((double) realS + wheelRotating*wheelRotating/(double) 5000 + anchors));
			double plus = (double) ((double) 3*power/(double) m);
			realS = (double) (realS - minus/(double) 8);
			realS = (double) (realS + plus/(double) 65);
			if(realS <= 0 || Double.NaN == realS) realS = 0;
			if(realS < 1/time && 0 < power) realS += power/m;
			if(2.7 < realS) realS -= minus/(double) 8;
			if(3 < realS) realS -= minus/(double) 4;
			if(3.5 < realS) realS -= minus/(double) 4;
		}
		public void setS()
		{
			S = (int) (realS*time);
			if(S < 0) S = 0;
		}
		public int getHp()
		{
			return hp;
		}
		public Location getLocation()
		{
			return this.l;
		}
		public int getPower()
		{
			return this.power;
		}
		public int getMg()
		{
			return this.m;
		}
		/*public int getDx()
		{
			return this.dx;
		}
		public int getDz()
		{
			return this.dz;
		}*/
		public double getS()
		{
			return this.S;
		}
		public Vector getVector(int LL, boolean AB)
		{
			DY = 0;
			double L1 = (Math.cos(Math.toRadians(direction)))*LL;
			double B1 = (Math.sin(Math.toRadians(direction)))*LL;
			int L = (int) L1;
			int B = (int) B1;
			if(AB)
			{
				if((A < m*g) || sinking)
				{
					DY = -1;
				}
				else if(anchors == 0) DY = 1;
			}
			if(data == 5)
			{
				return new Vector(-L,DY,-B);
			}
			else
			{
				if(data == 4)
				{
					return new Vector(L,DY,B);
				}
				else
				{
					if(data == 3)
					{
						return new Vector(B,DY,-L);
					}
					else
					{
						if(data == 2)
						{
							return new Vector(-B,DY,L);
						}
						else
						{
							return null;
						}
					}
				}
			}
		}
		public boolean pickBlocks()
		{
			corpus = 0;
			anchors = 0;
			types.clear();
			htypes.clear();
			pb.clear();
			hb.clear();
			blocks.clear();
			//np.clear();
			m = 15000;
			A = 0;
			N = 0;
			entityList.clear();
			ek.clear();
			le.clear();
			blocks.clear();
			pb.clear();
			ecipage.clear();
			amount = 0;
			relative = l.getBlock(); 
			localPlayers.clear();
			ported = false;
			for(int i = 0; i < l.getWorld().getPlayers().size(); i++)
			{
				Player p = l.getWorld().getPlayers().get(i);
				if(p.getLocation().distance(l) <= 500)
				{
					localPlayers.add(p);
				}
			}
			if(pickLot(relative))
			{
				pb.addAll(blocks.values());
				pickHanding();
				pickAir();
				for(int i = 0; i < l.getWorld().getEntities().size(); i++)
				{
					Entity e = l.getWorld().getEntities().get(i);
					if(l.distance(e.getLocation()) < LocalRange) le.add(e);
				}
				for(int pn = 0; pn<le.size(); pn++)
				{
					Entity e = le.get(pn);
					Block eb = e.getLocation().getBlock().getRelative(0, 1, 0);
					boolean picked = false;
					for(int i = 0; i <= 5; i++)
					{
						Block b = eb.getRelative(0, -i, 0);
						if(blocks.containsKey(getKey(b)))
						{
							ek.put(e.getUniqueId(), e);
							if(e.getType().equals(EntityType.PLAYER))
							{
								ecipage.add((Player) e);
								if(i <= 3)  if(b.isLiquid()) ((Player) e).addPotionEffects(pe);
							}
							else if(e.getType().equals(EntityType.MINECART_CHEST)) 
							{
								m += 55;
								Vehicle v = (Vehicle) e;
								Minecart mc = (Minecart) v;
								StorageMinecart sm = (StorageMinecart) mc;
								Inventory inv = sm.getInventory();
								for(int ii = 0; ii < inv.getSize(); ii++)
								{
									ItemStack its = inv.getItem(ii);
									if(its != null) m += getM(its.getTypeId())*its.getAmount()/64;
								}
							}
							picked = true;
							break;
						}
					}
					if(!picked && e.getType().equals(EntityType.PLAYER))
					{
						Player p = (Player) e;
						if(p.isSneaking())
						{
							for(int x = -1; x <= 1; x++)
							{
								if(picked) break;
								for(int y = -1; y <= 0; y++)
								{
									if(picked) break;
									for(int z = -1; z <= 1; z++)
									{
										Block psb = p.getLocation().getBlock().getRelative(x, y, z);
										if(!ek.containsKey(p.getUniqueId()) && blocks.containsKey(getKey(psb)))
										{
											ek.put(e.getUniqueId(), e);
											ecipage.add(p);
											picked = true;
											p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (int) (18*time), 12, true));
											break;
										}
									}
								}
							}
						}
					}
				}
				entityList.addAll(ek.values());
				return true;
			}
			else return false;
		}
		public void pickHanding()
		{
			//boolean explosion = false;
			TreeMap<String, Block> hblocks = new TreeMap<String, Block>();
			for(int i = 0; i < pb.size(); i++)
			{
				Block b = pb.get(i);
				for(int x = -1; x <= 1; x++)
					for(int y = -1; y <= 1; y ++)
						for(int z = -1; z <= 1; z++)
						{
							Block newB = b.getRelative(x, y, z);
							final String key = getKey(newB);
							if(!blocks.containsKey(key) && !hblocks.containsKey(key))
							{
								final int id = newB.getTypeId();
								if(isha(id))
								{
									hblocks.put(key, newB);
									blocks.put(key, newB);
									m += getM(newB)/3;
									if(id == 68)
									{
										String text = ((Sign) newB.getState()).getLine(0);
										if(text.equals(downAnchor))
										{
											anchors++;
											//if(!explosion) 
												if(time < S) newB.getWorld().createExplosion(newB.getLocation(), energy);
											if(anchors == 1) relative = newB;
											else relative = l.getBlock();
										}
										else if(text.endsWith(portString))
										{
											anchors = 10000000;
											realS = 0;
											S = 0;
											power = 0;
											ported = true;
										}
									}
									else if(id == 64 && newB.getData() != 8)
									{
										Block newB2 = newB.getRelative(0, 1, 0);
										final String key2 = getKey(newB2);
										if(!blocks.containsKey(key2) && !hblocks.containsKey(key2))
										{
											hblocks.put(key2, newB2);
											blocks.put(key2, newB2);
										}
									}
									if(sinking && newB.getY() <= waterLine && !isla(id)) newB.breakNaturally();
								}
								else if(!(id == 0 || id == 8 || id == 9 || sinking || (S == 0 && isla(id)))) newB.breakNaturally();
							}
						}
			}
			hb.addAll(hblocks.values());
		}
		public void pickAir()
		{
			for(int i = 0; i < pb.size(); i++)
			{
				Block b = pb.get(i);
				if(b.getY() <= waterLine + 1)
				for(int y = 0; y <= waterLine + 1 - b.getY(); y++)
				{
					Block newB = b.getRelative(0, y, 0);
					final String key = getKey(newB);
					if(!blocks.containsKey(key) && newB.getTypeId() == 0)
					{
						if(!newB.getRelative(0, -1, 0).isLiquid()) 
						{
							blocks.put(key, newB);
							pb.add(newB);
							if(newB.getY() + 1<= waterLine) A += 10000 * (waterLine + 1 - newB.getY());
						}
					}
				}
			}
		}
		public boolean pickLot(Block b)
		{
			List<Block> newpb = new ArrayList<Block>();
			for(int x = -5; x <= 5; x++)
				for(int y = -5; y <= 5; y++)
					for(int z = -5; z <= 5; z++)
					{
						Block nfb = l.getBlock().getRelative(x, -y, z);
						final int id = nfb.getTypeId();
						if(isba(id))
						{
							newpb.add(0, nfb);
							blocks.put(getKey(nfb), nfb);
							amount++;
							corpus++;
							m+= getM(nfb)/3;
						}
					}
			while(0 < newpb.size())
			{
				b = newpb.get(0);
				newpb.remove(0);
				for(int x = -1; x <=1; x++)
				{
					for(int z = -1; z <=1;z++)
					{
						for(int y = -1; y <= 1; y++)
						{
							Block newB = b.getRelative(x,y,z);
							final int i = newB.getTypeId();
							final String key = getKey(newB);
							if(!blocks.containsKey(key)) 
							{
								if(isba(i))
								{
									corpus++;
									newpb.add(newB);
									blocks.put(key, newB);
									amount++;
									m+= getM(newB)/3;
									if(sinking && i == 20) newB.breakNaturally();
								}
							}
						}
					}
				}
			}
			if(25000 <= amount) _log.info("[iSail] Be careful, because ship " + name + " is very big and may to make lags on this server!");
			if(amount <= MaxSizeOfShip) return true;
			else return false;
		}
/*		public boolean tpb(Block b)
		{
			boolean successful = true;
			relative = l.getBlock();
			amount++;
			pb.add(b);
			m+= getM(b);
			if(1300 < amount) return false;
			if(b.getType().equals(Material.WALL_SIGN))
			{
				if(((Sign) b.getState()).getLine(0).equals(downAnchor))
				{
					anchors++;
					if(anchors == 1) relative = b;
					else relative = l.getBlock();
				}
			}
			if(ha.contains(b.getType()) || b.getLocation().distance(l) <= 3)
			{
				if(ca.contains(b.getType())) corpus++;
				for(int x = -1; x <=1; x++)
				{
					for(int z = -1; z <=1;z++)
					{
						for(int y = -1; y <=1;y++)
						{
							Block newB = b.getRelative(x,y,z);
							if(ba.contains(newB.getType())) if(!pb.contains(newB)) successful = tpb(newB);
						}
					}
				}
			}
			return successful;
		}*/
/*		public boolean pickCorpus(Block b)
		{
			for(int pn = 0; pn<l.getWorld().getEntities().size(); pn++)
			{
				Entity e = l.getWorld().getEntities().get(pn);
				if(!ek.containsKey(e.getUniqueId()))
				{
					if(e.getLocation().distance(b.getLocation()) <= 4)
					{
						ek.put(e.getUniqueId(), e);
						if(e.getType().equals(EntityType.PLAYER))
						{
							ecipage.add((Player) e);
							if(sinking) ((Player) e).addPotionEffects(pe);
						}
					}
				}
			}
			Block rb = b.getRelative(0,1,0);
			for(int i = b.getY(); i < waterLine + 1; i++)
			{
				if(!blocks.containsKey(new StringBuilder("x").append(rb.getX()).append("y").append(rb.getY()).append("z").append(rb.getZ()).toString()) && rb.isEmpty())
				{
					blocks.put(new StringBuilder("x").append(rb.getX()).append("y").append(rb.getY()).append("z").append(rb.getZ()).toString(), rb);
					if(rb.getY()<=waterLine) A += 200 * (waterLine - i);
				}
				rb = b.getRelative(0,1,0);
			}
			amount++;
			m+= getM(b);
			boolean successful = true;
			blocks.put(new StringBuilder("x").append(b.getX()).append("y").append(b.getY()).append("z").append(b.getZ()).toString(), b);
			if(b.getType().equals(Material.WALL_SIGN))
			{
				if(((Sign) b.getState()).getLine(0).equals(downAnchor))
				{
					anchors++;
					if(2 < realS) b.getWorld().createExplosion(b.getX(), b.getY(), b.getZ(), (float) realS, false, true);
					if(anchors == 1) relative = b;
					else relative = l.getBlock();
				}
			}
			else
			if(ha.contains(b.getTypeId()))
			{
				if(ca.contains(b.getTypeId())) corpus++;
				for(int x = -1; x <=1; x++)
				{
					for(int z = -1; z <=1;z++)
					{
						for(int y = -1; y <=1;y++)
						{
							Block newB = b.getRelative(x,y,z);
							if(amount < 1300)
							{
								if(ba.contains(newB.getTypeId())) 
								{
									if(!blocks.containsKey(new StringBuilder("x").append(newB.getX()).append("y").append(newB.getY()).append("z").append(newB.getZ()).toString())) 
									{
										if(!pickCorpus(newB)) successful = false;
									}
								}
							}
							else
							{
								successful = false;
								return successful;
							}
						}
					}
				}
			}
			return successful;
		}
		*/
		//if(i == 0 || i == 8 || i == 9) return false;
		//else return i == 22 || i == 25 || i == 41 || i == 42 || i == 43 || i == 44 || i == 47 || i == 50 || i == 51 || i == 53 || i == 57 || i == 64 || i == 65 || i == 77 || i == 85 || i == 92 || i == 101 || i == 102 || i == 107 || i == 117 || i == 118 || i == 123 || i == 124 || i == 125 || i == 126 || i == 134 || i == 135 || i == 136 || i == 152 || i == 154 || i == 171;
		/*
		22 Lapis Lazuli Block
		25 Note block
		41 Block of Gold
		42 Block of Iron
		43 Double Slab
		44 Slab
		47 Bookshelf
		50 Torch
		51 Fire
		53 Oak Stairs
		57 Block of Diamond
		64 Wood Door
		65 Ladder
		68 Wall Sign
		77 Stone Button
		85 Fence
		89 Glowstone
		92 Cake
		101 Iron Bars
		102 Glass Pane
		107 Fence Gate
		117 Brewing Stand
		118 Cauldron
		123 Inactive Lamp
		124 Active Lamp
		125 Wood Double Slab
		126 Wood Slab
		134 Spruce Stairs
		135 Birch Stairs
		136 Jungle Stairs
		143 Wood Button
		152 Block of Redstone
		154 Hopper
		171 Carpet*/
		/*19 Sponge
		54 Chest
		29 Sticky Piston
		33 Piston
		34 Piston Extension (technical block)
		36 Piston Moving Block (technical block)
		58 Crafting Table
		61 Furnace
		62 Furnace (Burning State)
		63 Sign Post
		75 Inactive Redstone Torch
		76 Active Redstone Torch
		55 Redstone Wire
		84 Jukebox
		93 Inactive Repeater
		94 Active Repeater
		23 Dispenser
		70 Stone Pressure Plate
		72 Wood Pressure Plate
		116 Enchantment Table
		130 Ender Chest
		137 Command Block
		145 Anvil
		146 Trapped Chest
		149 Inactive Comparator
		150 Active Comparator
		151 Daylight Sensor
		158 Dropper
		140 Flower Pot*/
		public int getM(Block b)
		{
			int id = b.getTypeId();
			if(useCannons)
			{
				if(id == cannonId)
				{
					if(b.getData() == cannonData)
					{
						if(b.getRelative(0, 1, 0).getTypeId() == cannonTorch)
						{
							int length = 0;
							if(b.getRelative(1, 0, 0).getTypeId() == cannonId)
							{
								Block lb = b.getRelative(1,0,0);
								length++;
								while(lb.getTypeId() == cannonId)
								{
									length++;
									lb = lb.getRelative(1,0,0);
								}
							}
							else if(b.getRelative(-1, 0, 0).getTypeId() == cannonId)
								{
									Block lb = b.getRelative(-1,0,0);
									length++;
									while(lb.getTypeId() == cannonId)
									{
										length++;
										lb = lb.getRelative(-1,0,0);
									}
								}
								else if(b.getRelative(0, 0, 1).getTypeId() == cannonId)
									{
										Block lb = b.getRelative(0,0,1);
										length++;
										while(lb.getTypeId() == cannonId)
										{
											length++;
											lb = lb.getRelative(0,0,1);
										}
									}
								else if(b.getRelative(0, 0, -1).getTypeId() == cannonId)
									{
										Block lb = b.getRelative(0,0,-1);
										length++;
										while(lb.getTypeId() == cannonId)
										{
											length++;
											lb = lb.getRelative(0,0,-1);
										}
									}
							return length*length*1500;
						}
					}
				}
			}
			switch(id)
			{
			case 1: return 1000;
			case 2: return 777;
			case 3: return 750;
			case 4: return 1000;
			case 5: return 100;
			case 7: return 100000;
			case 8: return 1000;
			case 9: return 1000;
			case 10: return 5000;
			case 11: return 5000;
			case 12: return 1000;
			case 13: return 1000;
			case 17: return 400;
			case 35: return 50;
			case 20: return 200;
			case 22: return 1000;
			case 24: return 1000;
			case 41: return 500;
			case 42: return 1000;
			case 43: return 5000;
			case 44: return 500;
			case 45: return 500;
			case 46: return 500;
			case 47: return 150;
			case 48: return 500;
			case 49: return 15555;
			case 50: return 2;
			case 53: return 75;
			case 57: return 500;
			case 64: return 18;
			case 67: return 375;
			case 68: return 2;
			case 69: return 5;
			case 70: return 100;
			case 71: return 200;
			case 72: return 50;
			case 77: return 2;
			case 78: return 2;
			case 79: return 1000;
			case 80: return 300;
			case 85: return 50;
			case 86: return 8;
			case 87: return 1000;
			case 88: return 2500;
			case 89: return 300;
			case 91: return 10;
			case 92: return 2;
			case 96: return 5;
			case 97: return 2500;
			case 98: return 5000;
			case 99: return 50;
			case 100: return 50;
			case 101: return 200;
			case 102: return 100;
			case 103: return 10;
			case 106: return 6;
			case 107: return 50;
			case 108: return 375;
			case 109: return 750;
			case 121: return 1000;
			case 123: return 300;
			case 124: return 300;
			case 125: return 100;
			case 126: return 50;
			}
			if(id == 17) return 400;
			else if(id == 5) return 100;
			else if (id == 35) return 0;
			return 2;
		}
		public int getM(int id)
		{
			switch(id)
			{
			case 0: return 0;
			case 1: return 1000;
			case 2: return 777;
			case 3: return 750;
			case 4: return 1000;
			case 5: return 100;
			case 7: return 100000;
			case 8: return 1000;
			case 9: return 1000;
			case 10: return 5000;
			case 11: return 5000;
			case 12: return 1000;
			case 13: return 1000;
			case 17: return 400;
			case 35: return 50;
			case 20: return 200;
			case 22: return 1000;
			case 24: return 1000;
			case 41: return 500;
			case 42: return 1000;
			case 43: return 5000;
			case 44: return 500;
			case 45: return 500;
			case 46: return 500;
			case 47: return 150;
			case 48: return 500;
			case 49: return 15555;
			case 50: return 2;
			case 51: return 5000;
			case 53: return 75;
			case 57: return 500;
			case 64: return 18;
			case 67: return 375;
			case 68: return 2;
			case 69: return 5;
			case 70: return 100;
			case 71: return 200;
			case 72: return 50;
			case 77: return 2;
			case 78: return 2;
			case 79: return 1000;
			case 80: return 300;
			case 85: return 50;
			case 86: return 8;
			case 87: return 1000;
			case 88: return 2500;
			case 89: return 300;
			case 91: return 10;
			case 92: return 2;
			case 96: return 5;
			case 97: return 2500;
			case 98: return 5000;
			case 99: return 50;
			case 100: return 50;
			case 101: return 200;
			case 102: return 100;
			case 103: return 10;
			case 106: return 6;
			case 107: return 50;
			case 108: return 375;
			case 109: return 750;
			case 121: return 1000;
			case 123: return 300;
			case 124: return 300;
			case 125: return 100;
			case 126: return 50;
			}
			if(id == 17) return 400;
			else if(id == 5) return 100;
			else if (id == 35) return 0;
			return 2;
		}
/*		public void pbyp(Block b)
		{
			if(ba.contains(b.getType()))
			{
				pb.add(b);
				m++;
				if(b.getType().equals(Material.WALL_SIGN))
				{
					if(((Sign) b.getState()).getLine(1).equals(downAnchor)) anchors++;
				}
				if(ha.contains(b.getType()))
				{
					if(ca.contains(b.getType())) corpus++;
					if(!pb.contains(b.getRelative(0,1,0))) pbyp(b.getRelative(0,1,0));
					//if(!pb.contains(b.getRelative(1,0,0))) pbxp(b.getRelative(1,0,0));
					if(!pb.contains(b.getRelative(-1,0,0))) pbxm(b.getRelative(-1,0,0));
					if(!pb.contains(b.getRelative(0,0,1))) pbzp(b.getRelative(0,0,1));
					if(!pb.contains(b.getRelative(0,0,-1))) pbzm(b.getRelative(0,0,-1));
				}
			}
		}
		public void pbzp(Block b)
		{
			if(ba.contains(b.getType()))
			{
				pb.add(b);
				m++;
				if(b.getType().equals(Material.WALL_SIGN))
				{
					if(((Sign) b.getState()).getLine(1).equals(downAnchor)) anchors++;
				}
				if(ha.contains(b.getType()))
				{
					{
						if(ca.contains(b.getType())) corpus++;
						if(!pb.contains(b.getRelative(0,0,1))) pbzp(b.getRelative(0,0,1));
						//if(!pb.contains(b.getRelative(1,0,0))) pbxp(b.getRelative(1,0,0));
						if(!pb.contains(b.getRelative(-1,0,0))) pbxm(b.getRelative(-1,0,0));
						if(!pb.contains(b.getRelative(0,1,0))) pbyp(b.getRelative(0,1,0));
						if(!pb.contains(b.getRelative(0,-1,0))) pbym(b.getRelative(0,-1,0));
					}
				}
			}
		}
		public void pbym(Block b)
		{
			if(ba.contains(b.getType()))
			{
				pb.add(b);
				m++;
				if(b.getType().equals(Material.WALL_SIGN))
				{
					if(((Sign) b.getState()).getLine(1).equals(downAnchor)) anchors++;
				}
				if(ha.contains(b.getType()))
				{
					{
						if(ca.contains(b.getType())) corpus++;
						if(!pb.contains(b.getRelative(0,-1,0))) pbym(b.getRelative(0,-1,0));
						//if(!pb.contains(b.getRelative(1,0,0))) pbxp(b.getRelative(1,0,0));
						if(!pb.contains(b.getRelative(-1,0,0))) pbxm(b.getRelative(-1,0,0));
						if(!pb.contains(b.getRelative(0,0,1))) pbzp(b.getRelative(0,0,1));
						if(!pb.contains(b.getRelative(0,0,-1))) pbzm(b.getRelative(0,0,-1));
					}
				}
			}
		}
		public void pbxm(Block b)
		{
			if(ba.contains(b.getType()))
			{
				pb.add(b);
				m++;
				if(b.getType().equals(Material.WALL_SIGN))
				{
					if(((Sign) b.getState()).getLine(1).equals(downAnchor)) anchors++;
				}
				if(ha.contains(b.getType()))
				{
					{
						if(ca.contains(b.getType())) corpus++;
						if(!pb.contains(b.getRelative(-1,0,0))) pbxm(b.getRelative(-1,0,0));
						if(!pb.contains(b.getRelative(0,0,1))) pbzp(b.getRelative(0,0,1));
						if(!pb.contains(b.getRelative(0,0,-1))) pbzm(b.getRelative(0,0,-1));
						if(!pb.contains(b.getRelative(0,1,0))) pbyp(b.getRelative(0,1,0));
						if(!pb.contains(b.getRelative(0,-1,0))) pbym(b.getRelative(0,-1,0));
					}
				}
			}
		}
		public void pbzm(Block b)
		{
			if(ba.contains(b.getType()))
			{
				pb.add(b);
				m++;
				if(b.getType().equals(Material.WALL_SIGN))
				{
					if(((Sign) b.getState()).getLine(1).equals(downAnchor)) anchors++;
				}
				if(ha.contains(b.getType()))
				{
					{
						if(ca.contains(b.getType())) corpus++;
						if(!pb.contains(b.getRelative(0,0,-1))) pbzm(b.getRelative(0,0,-1));
						//if(!pb.contains(b.getRelative(1,0,0))) pbxp(b.getRelative(1,0,0));
						if(!pb.contains(b.getRelative(-1,0,0))) pbxm(b.getRelative(-1,0,0));
						if(!pb.contains(b.getRelative(0,1,0))) pbyp(b.getRelative(0,1,0));
						if(!pb.contains(b.getRelative(0,-1,0))) pbym(b.getRelative(0,-1,0));
					}
				}
			}
		}*/
		public void tryTurnLeft()
		{
			if(canTurnLeft())
			{
				direction = 40;
				Block lb = l.getBlock();
				int lx = lb.getX() - relative.getX();
				int ly = lb.getY() - relative.getY();
				int lz = lb.getZ() - relative.getZ();
				Block newlB = relative.getRelative(lz, ly, -lx);
				l = newlB.getLocation();
				removeBlocks('L');
				for(int i = 0; i < pb.size(); i++)
				{
					int x = pb.get(i).getX() - relative.getX();
					int y = pb.get(i).getY() - relative.getY();
					int z = pb.get(i).getZ() - relative.getZ();
					Block newB = relative.getRelative(z, y, -x);
					moveBlock(newB, i);
				}
				for(int i = 0; i < hb.size(); i++)
				{
					int x = hb.get(i).getX() - relative.getX();
					int y = hb.get(i).getY() - relative.getY();
					int z = hb.get(i).getZ() - relative.getZ();
					Block newB = relative.getRelative(z, y, -x);
					moveHBlock(newB, i);
				}
				if(l.getBlock().getRelative(BlockFace.UP).getTypeId() == 68) ((Sign) l.getBlock().getRelative(BlockFace.UP).getState()).update();
				for(int i = 0; i < entityList.size(); i++)
				{
					Entity e = entityList.get(i);
					double x = e.getLocation().getBlockX() - relative.getLocation().getX();
					double y = e.getLocation().getY() - relative.getLocation().getY();
					double z = e.getLocation().getBlockZ() - relative.getLocation().getZ();
					Location newLoc = relative.getLocation().add(z, y, -x).getBlock().getLocation().add(0.5, 0, 0.5);
					e.teleport(newLoc);
				}
				turnMahtsLeft();
				if(front != null) front.turnLeft();
				switch(data)
				{
					case 2:
					{
						data = 4;
						break;
					}
					case 3:
					{
						data = 5;
						break;
					}
					case 4:
					{
						data = 3;
						break;
					}
					case 5:
					{
						data = 2;
						break;
					}
				}
			}
			else
			{
				sendToLocalPlayers(new StringBuilder().append(ChatColor.BOLD).append(ChatColor.RED).append("[iSail] Ship ").append(name).append(" crashed on turning left!!!").toString(), BroadcastType.ACCIDENT);
				direction = -49;
			}
		}
		public boolean canTurnLeft()
		{
			cblocks.clear();
			ablocks.clear();
			for(int i = 0; i < pb.size(); i++)
			{
				int x = pb.get(i).getX() - relative.getX();
				int y = pb.get(i).getY() - relative.getY();
				int z = pb.get(i).getZ() - relative.getZ();
				Block newB = relative.getRelative(z, y, -x);
				final int id1 = newB.getTypeId();
				if(!isea(id1)) if(!blocks.containsKey(getKey(newB)))
				{
					realS = 0;
					final int id = pb.get(i).getTypeId();
					if(!(id == 1 || id == 2 || id == 3 || id == 4 || id == 12 || id == 13)) attacked = pb.get(i);
					newB.getWorld().createExplosion(newB.getX(), newB.getY(), newB.getZ(), energy, false, true);
					return false;
				}
				else if(!cblocks.containsKey(getKey(newB))) cblocks.put(getKey(newB), 0);
			}
			for(int i = 0; i < le.size(); i++)
			{
				Entity e = le.get(i);
				if(e.getType().equals(EntityType.PLAYER))
				{
					if(!ek.containsKey(e.getUniqueId()))
					{
						Block b1 = e.getLocation().getBlock();
						Block b2 = e.getLocation().getBlock().getRelative(0, 1, 0);
						if(ablocks.containsKey(getKey(b1)) || ablocks.containsKey(getKey(b2)))
						{
							Player p = (Player) e;
							p.setHealth(0);
							p.sendMessage(ChatColor.RED + "[iSail] You was killed by ship " + name + "!");
							sendToLocalPlayers(ChatColor.RED + "[iSail] Ship " + name + " killed player " + p.getName() +  "!", BroadcastType.ACCIDENT);
							_log.info("[iSail] Ship " + name + " killed player " + p.getName() +  "!");
						}
					}
				}
			}
			return true;
		}
		public void tryTurnRight()
		{
			if(canTurnRight())
			{
				direction = -40;
				Block lb = l.getBlock();
				int lx = lb.getX() - relative.getX();
				int ly = lb.getY() - relative.getY();
				int lz = lb.getZ() - relative.getZ();
				Block newlB = relative.getRelative(-lz, ly, lx);
				l = newlB.getLocation();
				removeBlocks('R');
				for(int i = 0; i < pb.size(); i++)
				{
					int x = pb.get(i).getX() - relative.getX();
					int y = pb.get(i).getY() - relative.getY();
					int z = pb.get(i).getZ() - relative.getZ();
					Block newB = relative.getRelative(-z, y, x);
					moveBlock(newB, i);
				}
				for(int i = 0; i < hb.size(); i++)
				{
					int x = hb.get(i).getX() - relative.getX();
					int y = hb.get(i).getY() - relative.getY();
					int z = hb.get(i).getZ() - relative.getZ();
					Block newB = relative.getRelative(-z, y, x);
					moveHBlock(newB, i);
				}
				if(l.getBlock().getRelative(BlockFace.UP).getTypeId() == 68) ((Sign) l.getBlock().getRelative(BlockFace.UP).getState()).update();
				for(int i = 0; i < entityList.size(); i++)
				{
					Entity e = entityList.get(i);
					double x = e.getLocation().getBlockX() - relative.getLocation().getX();
					double y = e.getLocation().getY() - relative.getLocation().getY();
					double z = e.getLocation().getBlockZ() - relative.getLocation().getZ();
					Location newLoc = relative.getLocation().add(-z, y, x).getBlock().getLocation().add(0.5, 0, 0.5);
					e.teleport(newLoc);
				}
				turnMahtsRight();
				if(front != null) front.turnRight();
				switch(data)
				{
					case 2:
					{
						data = 5;
						break;
					}
					case 3:
					{
						data = 4;
						break;
					}
					case 4:
					{
						data = 2;
						break;
					}
					case 5:
					{
						data = 3;
						break;
					}
				}
			}
			else
			{
				sendToLocalPlayers(new StringBuilder().append(ChatColor.BOLD).append(ChatColor.RED).append("[iSail] Ship ").append(name).append(" crashed on turning right!!!").toString(), BroadcastType.ACCIDENT);
				direction = 49;
			}
		}
		public boolean canTurnRight()
		{
			for(int i = 0; i < pb.size(); i++)
			{
				int x = pb.get(i).getX() - relative.getX();
				int y = pb.get(i).getY() - relative.getY();
				int z = pb.get(i).getZ() - relative.getZ();
				Block newB = relative.getRelative(-z, y, x);
				final int id1 = newB.getTypeId();
				final String key = getKey(newB);
				if(!isea(id1)) if(!blocks.containsKey(key)) 
				{
					realS = 0;
					final int id = pb.get(i).getTypeId();
					if(!isla(id)) attacked = pb.get(i);
					newB.getWorld().createExplosion(newB.getX(), newB.getY(), newB.getZ(), energy, false, true);
					return false;
				}
				else if(!cblocks.containsKey(key)) cblocks.put(key, 0);
			}
			for(int i = 0; i < le.size(); i++)
			{
				Entity e = le.get(i);
				if(e.getType().equals(EntityType.PLAYER))
				{
					if(!ek.containsKey(e.getUniqueId()))
					{
						Block b1 = e.getLocation().getBlock();
						Block b2 = e.getLocation().getBlock().getRelative(0, 1, 0);
						if(ablocks.containsKey(getKey(b1)) || ablocks.containsKey(getKey(b2)))
						{
							Player p = (Player) e;
							p.setHealth(0);
							p.sendMessage(ChatColor.RED + "[iSail] You was killed by ship " + name + "!");
							sendToLocalPlayers(ChatColor.RED + "[iSail] Ship " + name + " killed player " + p.getName() +  "!", BroadcastType.ACCIDENT);
							_log.info("[iSail] Ship " + name + " killed player " + p.getName() +  "!");
						}
					}
				}
			}
			return true;
		}
		public void removeBlocks(char dir)
		{
			for(int i = 0; i < hb.size(); i++)
			{
				reMoveHBlock(hb.get(i), dir);
			}
			for(int i = 0; i < pb.size(); i++)
			{
				reMoveBlock(pb.get(i), dir);
			}
		}
		/*public void closeSailsForMoving()
		{
			for(int k = 0; k < mahts.size(); k++)
			{
				Maht m = mahts.get(k);
				for(int i = 0; i < m.sails.size(); i++)
				{
					Sail sail = m.sails.get(i);
					sail.signBlock = sail.signBlock.getLocation().add(v).getBlock();
					if(sail.haveLeft) sail.left = sail.left.getLocation().add(v).getBlock();
					if(sail.haveRight) sail.left = sail.right.getLocation().add(v).getBlock();
					sail.close();
				}
			}
		}
		public void openSailsForMoving()
		{
			for(int k = 0; k < mahts.size(); k++)
			{
				Maht m = mahts.get(k);
				m.b = m.b.getLocation().add(v).getBlock();
				for(int i = 0; i < m.sails.size(); i++)
				{
					Sail sail = m.sails.get(i);
					sail.signBlock = sail.signBlock.getLocation().add(v).getBlock();
					if(sail.haveLeft) sail.left = sail.left.getLocation().add(v).getBlock();
					if(sail.haveRight) sail.left = sail.right.getLocation().add(v).getBlock();
					sail.open();
				}
			}
		}*/
		public void destroyShip()
		{
			String reason = null;
			if(2*waterLine < l.getBlockY()) reason = "Out from max heigth";
			if(corpus <= 10) reason = "Damaged";
			if(l.getBlockY() <= limitOfSinking) reason = "This ship was underwater";
			if(sinking) reason = "This ship was drowned";
			for(int n = 0; n<pb.size(); n++)
			{
				int M = 0;
				Block b = pb.get(n);
				if(b.getY()<=waterLine) M=0;
				b.breakNaturally();
				b.setTypeIdAndData(M, (byte) 0, true);
			}
			if(hb != null) for(int n = 0; n<hb.size(); n++)
			{
				int M = 0;
				Block b = hb.get(n);
				if(b.getY()<=waterLine) M=0;
				b.breakNaturally();
				b.setTypeIdAndData(M, (byte) 0, true);
			}
			/*if(fok != null) fok.destroy();
			if(grot != null) grot.destroy();
			if(bizan != null) bizan.destroy();*/
			_log.info("Ship " + name + " has been destroyed. Reason: " + reason);
			sendToLocalPlayers(ChatColor.RED + "Ship " + name + " has been destroyed. Reason: " + ChatColor.DARK_RED + reason, BroadcastType.ACCIDENT);
			pb.clear();
			hb.clear();
			ecipage.clear();
			ek.clear();
			blocks.clear();
			destroyed=true;
			disable();
		}
		public void removeShip()
		{
			destroyed = true;
			_log.info("Ship" + name + "has been removed from the game!");
			sendToEcipage(ChatColor.RED + "Ship " + name + " has been removed from the game!", BroadcastType.ACCIDENT);
		}
		public Maht getMaht(Block main)
		{
			Maht maht = null;
			if(fok != null) 
			{
				maht = fok;
				if(maht.b.getX() == main.getX() && maht.b.getZ() == main.getZ()) return maht;
			}
			if(grot != null) 
			{
				maht = grot;
				if(maht.b.getX() == main.getX() && maht.b.getZ() == main.getZ()) return maht;
			}
			if(bizan != null) 
			{
				maht = bizan;
				if(maht.b.getX() == main.getX() && maht.b.getZ() == main.getZ()) return maht;
			}
			return null;
		}
		public boolean isNeedsMoving()
		{
			return  A < m*g || 0 < S || DY != 0 || sinking;
		}
		public boolean isHeavy()
		{
			return waterLine <= l.getBlockY();
		}
		public boolean isAlive()
		{
			if(sinking) return limitOfSinking <= l.getBlockY() && !isla(l.getBlock().getRelative(0, -1, 0).getTypeId());
			return 10 < pb.size();
		}
		public boolean setWaterLine()
		{
			boolean isInWater = false;
			for(int h = 0; h < 50; h++)
			{
				Block b = l.getBlock().getRelative(0,-h,0);
				if(b.isLiquid())
				{
					isInWater = true;
					break;
				}
			}
			return isInWater;
		}
		public void addMaht(Maht maht, Player maker)
		{
			MahtType type = maht.type;
			switch(type)
			{
				case FOK:
				{
					fok = maht;
					maker.sendMessage(ChatColor.GREEN + "[iSail] You was created " + type.toString().toLowerCase() + " mast on ship " + name + "!");
					break;
				}
				case GROT:
				{
					grot = maht;;
					maker.sendMessage(ChatColor.GREEN + "[iSail] You was created " + type.toString().toLowerCase() + " mast on ship " + name + "!");
					break;
				}
				case BIZAN:
				{
					bizan = maht;;
					maker.sendMessage(ChatColor.GREEN + "[iSail] You was created " + type.toString().toLowerCase() + " mast on ship " + name + "!");
					break;
				}
			}
		}
		public boolean haveMahtType(MahtType type)
		{
			switch(type)
			{
				case FOK: return fok != null;
				case GROT: return grot != null;
				case BIZAN: return bizan !=null;
				default: return false;
			}
		}
		public boolean haveMaht(Block mahtBlock)
		{
			final int mx = mahtBlock.getX();
			final int mz = mahtBlock.getZ();
			if(fok != null && fok.b.getX() == mx && fok.b.getZ() == mz) return true;
			if(grot != null && grot.b.getX() == mx && grot.b.getZ() == mz) return true;
			if(bizan != null && bizan.b.getX() == mx && bizan.b.getZ() == mz) return true;
			return false;
		}
		public boolean isTurning()
		{
			return direction<=-50 || 50<=direction;
		}
		public void startSinking()
		{
			sendToLocalPlayers(ChatColor.RED + "[iSail] ship " + ChatColor.GOLD + name + ChatColor.RED + " irrevocably began to sink!", BroadcastType.ACCIDENT);
			sinking = true;
		}
		public void disable()
		{
			l.getBlock().setTypeIdAndData(68, data, false);
			Sign sign = (Sign) l.getBlock().getState();
			sign.setLine(0, "ship");
			sign.setLine(2, ChatColor.GREEN + "Hit refresh");
			sign.update();
			disableMahts();
			try
			{
				ships.remove(getShip(name));
			}
			catch(NullPointerException e1)
			{
				_log.info("[iSail] Error occupied while disabling ship! Check you java!");
				try
				{
					ships.remove(getShip(seed));
				}
				catch(NullPointerException e2)
				{
					_log.info("[iSail] Double erro!");
					
				}
			}
			Bukkit.getScheduler().cancelTask(task);
		}
		public void disableMahts()
		{
			if(front != null) front.disable();
			if(fok != null) fok.disable();
			if(grot != null) grot.disable();
			if(bizan != null) bizan.disable();
		}
		public void turnMahtsRight()
		{
			if(fok != null) fok.turnRight();
			if(grot != null) grot.turnRight();
			if(bizan != null) bizan.turnRight();
		}
		public void turnMahtsLeft()
		{
			if(fok != null) fok.turnLeft();
			if(grot != null) grot.turnLeft();
			if(bizan != null) bizan.turnLeft();
		}
		public void moveMahts()
		{
			if(front != null) front.move();
			if(fok != null) fok.move();
			if(grot != null) grot.move();
			if(bizan != null) bizan.move();
		}
		public Sail getSailFromSignBlock(Block signBlock)
		{
			if(fok !=null) if(fok.getSailFromSignblock(signBlock) != null) return fok.getSailFromSignblock(signBlock);
			if(grot !=null) if(grot.getSailFromSignblock(signBlock) != null) return grot.getSailFromSignblock(signBlock);
			if(bizan !=null) if(bizan.getSailFromSignblock(signBlock) != null) return bizan.getSailFromSignblock(signBlock);
			if(front != null) if(front.signBlock.equals(signBlock)) return front;
			return null;
		}
		public boolean changeSails(Block signBlock, Action a)
		{
			if(front != null) if(front.Change(signBlock, a)) return true;
			if(fok != null) if(fok.changeSails(signBlock, a)) return true;
			if(grot != null) if(grot.changeSails(signBlock, a)) return true;
			if(bizan != null) if(bizan.changeSails(signBlock, a)) return true;
			return false;
		}
		public boolean isMakerOfSail(Player p)
		{
			if(front != null) if(front.isMakerOfSail(p)) return true;
			if(fok != null) if(fok.isMakerOfSail(p)) return true;
			if(grot != null) if(grot.isMakerOfSail(p)) return true;
			if(bizan != null) if(bizan.isMakerOfSail(p)) return true;
			return false;
		}
		public void rotateWheelRight(Sign s)
		{
			String lines[] = s.getLines();
			if(wheelRotating < 18)
			{
				wheelRotating++;
				if(lines[0].equals(wheel))
				{
					s.setLine(0, wheelR);
					s.setLine(3, wheelDR);
				}
			else if(lines[0].equals(wheelL))
				{
					s.setLine(0, wheel);
					s.setLine(3, wheelD);
				}
			else if(lines[0].equals(wheelR))
				{
					s.setLine(0, wheelL);
					s.setLine(3, wheelDL);
				}
				s.update();
			}
		}
		public void rotateWheelLeft(Sign s)
		{
			String lines[] = s.getLines();
			if(-18 < wheelRotating)
			{
				wheelRotating--;
				if(lines[0].equals(wheel))
				{
					s.setLine(0, wheelL);
					s.setLine(3, wheelDL);
				}
			else if(lines[0].equals(wheelR))
				{
					s.setLine(0, wheel);
					s.setLine(3, wheelD);
				}
			else if(lines[0].equals(wheelL))
				{
					s.setLine(0, wheelR);
					s.setLine(3, wheelDR);
				}
				s.update();
			}
		}
		public boolean wheelIsUnholded()
		{
			Location wl = l.getBlock().getRelative(0, 1, 0).getLocation();
			for(int i = 0; i < ecipage.size(); i++)
			{
				Player p = ecipage.get(i);
				if(p.isSneaking() && p.getLocation().distance(wl) < 5) return false;
			}
			return true;
		}
		public void engine()
		{
			
		}
	}
