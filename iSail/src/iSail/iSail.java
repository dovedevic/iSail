package iSail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/*
dsb:
19 Sponge
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
140 Flower Pot

acp:
87 Netherrack

Turning:

placed:
133 Block of Emerald
1
2
3
4
*/
public class iSail extends JavaPlugin implements Listener
{
static List<Ship> ships = new ArrayList<Ship>();
static List<String> sleepPlayers = new ArrayList<String>();
static List<PotionEffect> pe = new ArrayList<PotionEffect>();
TreeMap<String, Sail> cs = new TreeMap<String, Sail>();
	   public static final Logger _log = Logger.getLogger("Minecraft");
	   @Override
	   public void onEnable() {
	       this.saveDefaultConfig();
	       Bukkit.getPluginManager().registerEvents(this, this);
	       windDirection = 360*Math.random();
	       windPower = 18*Math.random();
	       Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
	       {
	    	   @Override
	    	   public void run()
	    	   {
    				World w = Bukkit.getWorld("World");
    				ChatColor color = ChatColor.AQUA;
    				if(w.hasStorm()) 
    				{
    					weather = 500;
    					weatherMax = 30;
    					weatherMin = 18;
    					color = ChatColor.RED;
    				}
    				else if(w.isThundering())
    				{
    					weather = 250;
    					weatherMax = 18;
    					weatherMin = 12;
    					color = ChatColor.GOLD;
    				}
    				else
    				{
    					weather = 100;
    					weatherMax = 12;
    					weatherMin = 1;
    				}
					windPower += weather*Math.random()/1000;
					windPower -= weather*Math.random()/1000;
					if(windPower <  weatherMin) windPower += Math.random();
					if(weatherMax < windPower) windPower -= Math.random();
					/*windDirection += Math.sqrt(Math.random())*30*weather;
					windDirection -= Math.sqrt(Math.random())*30*weather;
					if(windDirection < 0) windDirection += 360;
					else if(360 < windDirection) windDirection -= 360;*/
					windDirection += Math.random()*weather/1000;
					windDirection -= Math.random()*weather/1000;
					if(windDirection < 0) windDirection += 360;
					else if(360 < windDirection) windDirection -= 360;
					if(windPhase == 60)
					{
						if(ships != null) for(int i = 0; i < ships.size(); i++)
						{
							ships.get(i).sendToEcipage(color + "[iSail] Wind has been changed to" + ChatColor.WHITE + " ~" + String.valueOf((int) windPower + 1) + color + " m/s and direction is " + ChatColor.WHITE +  "~" + String.valueOf((int) windDirection) + color +  " degrees relative to " +ChatColor.WHITE + "North!", BroadcastType.WIND);
						}
						windPhase = 0;
					}
					else windPhase++;
	    	   }
	       }, 0L, 20L);
	       pe.add(new PotionEffect(PotionEffectType.SLOW_DIGGING, (int) (18*time), 12, true));
	       //pe.add(new PotionEffect(PotionEffectType.SLOW, (int) (18*time), 12, true));
	       //pe.add(new PotionEffect(PotionEffectType.CONFUSION, (int) (120*time - 1), 12, true));
	       //pe.add(new PotionEffect(PotionEffectType.WITHER, (int) (120*time - 1), 8, true));
	       //pe.add(new PotionEffect(PotionEffectType.BLINDNESS, (int) (120*time - 1), 8, true));
	      //getConfig().options().copyDefaults(true);
	       //saveConfig();
		   File shipsFile = new File("plugins/iSail/ships.yml");
		   FileConfiguration sfc = YamlConfiguration.loadConfiguration(shipsFile);
		   sleepPlayers = sfc.getStringList("SleepPlayers");
//		   for(int i = 0; i < sfc.getInt("AmountOfShips"); i++)
//		   {
//			   ConfigurationSection section = sfc.getConfigurationSection(String.valueOf(i));
//			   ConfigurationSection location = section.getConfigurationSection("Location");
//			   Location l = new Location(Bukkit.getWorld(location.getString("World")), location.getInt("X"), location.getInt("Y"), location.getInt("Z"));
//			   String name = section.getString("Name");
//			   byte data = (byte) section.getInt("Data");
//			   Ship ship = new Ship(l, name, data, false);
//			   ship.sleeps = section.getStringList("SleepPlayers");
//			   ships.add(ship);
//		       _log.info("[iSail] Ship " + name + " was enabled!");
//		   }
	       //movingable.add(Material.AIR)
	       File c = new File(getDataFolder(), "config.yml");
	       FileConfiguration config = YamlConfiguration.loadConfiguration(c);
	       if(config !=null)
	       {
		       _log.info("[iSail] loading configuration file " + c.getName());
			   world = Bukkit.getWorld(config.getString("World"));
			   waterLine = config.getInt("OceanLevel");
			   limitOfSinking = config.getInt("LimitOfSinking");
		       blocksPerPlayer = config.getInt("BlocksPerPlayer");
		       time = (long) config.getInt("Phase");
			   MaxSizeOfShip = config.getInt("MaxSizeOfShip");
			   if(1300 < MaxSizeOfShip) useLot = true;
			   else useLot = false;
			   if(Bukkit.getServer().getPluginManager().getPlugin("Cannons") != null) useCannons = true;
			   MinimalEcipage = config.getInt("MinimalEcipage");
			   try 
			   {
				   config.save(c);
			   } 
			   catch (IOException e) 
			   {
				   e.printStackTrace();
			   }
	       }
		   if(shipsFile.isFile()) shipsFile.delete();
	       _log.info("[iSail] was enabled!");
	   }
	   @Override
	   public void onDisable()
	   {
		   File shipsFile = new File("plugins/iSail/ships.yml");
		   try {
			shipsFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		   FileConfiguration sfc = YamlConfiguration.loadConfiguration(shipsFile);
		   sfc.set("SleepPlayers", sleepPlayers);
		   int ss = 0;
		   for(int i = 0; i<ships.size(); i++)
		   {
			   Ship ship = ships.get(i);
			   _log.info("[iSail] Disabling ship " + ship.name);
			   ship.disable();
			   ss++;
			   Location l = ship.l;
			   String name = ship.name;
			   ConfigurationSection section = sfc.createSection(String.valueOf(i));
			   ConfigurationSection location = section.createSection("Location");
			   location.set("World", l.getWorld().getName());
			   location.set("X", l.getX());
			   location.set("Y", l.getY());
			   location.set("Z", l.getZ());
			   section.set("Data", ship.data);
			   section.set("Name", name);
			   section.set("SleepPlayers", ship.sleeps);
		   }
		   sfc.set("AmountOfShips", ss);
		   try {
				sfc.save(shipsFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		   _log.info("[iSail] was disabled!");
	   }
	   @Override
	   public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[])
	   {
		   Player p = (Player) sender;
		   if(commandLabel.equalsIgnoreCase("iSail"))
		   {
			   if(0 < args.length) 
			   {
				   switch (args[0])
				   {
				   		case "broadcast":
				   		{
				   			String str = null;
				   			if(1 < args.length) str = args[1];
				   			changeBroadcast(p, str);
				   			break;
				   		}
				   		case "wind":
				   		{
				   			p.sendMessage(new StringBuilder().append(ChatColor.AQUA).append("[iSail] Wind now have speed ").append(windPower).append(" m/s and direction is ").append(windDirection).append(" relative to ").append(ChatColor.WHITE).append("North").append(ChatColor.AQUA).append("!").toString());
				   			break;
				   		}
				   		case "fixme":
				   		{
				   			p.teleport(p.getLocation());
				   			break;
				   		}
				   		case "where":
				   		{
				   			String str = null;
				   			if(1 < args.length)
				   			{
				   				str = args[1];
				   				Ship s = null;
				   				s = getShip(str);
				   				if(s != null)
				   				{
					   				if(p.isOp() || p.hasPermission("range")) s.sendLocation(p);
					   				else if(p.getLocation().distance(s.getLocation()) <= LocalRange) s.sendLocation(p);
				   				}
				   				else p.sendMessage("[iSail] Ship not found!");
				   			}
				   			break;
				   		}
				   		case "tp":
				   		{
				   			String str = null;
				   			if(1 < args.length)
				   			{
				   				str = args[1];
				   				Ship s = null;
				   				s = getShip(str);
				   				if(s != null)
				   				{
					   				if(p.isOp() || p.hasPermission("tp")) p.teleport(s.getLocation());
				   				}
				   				else p.sendMessage("[iSail] Ship not found!");
				   			}
				   			break;
				   		}
				   		case "help":
				   		{
				   			p.sendMessage(ChatColor.GREEN + "[iSail] List of commands of /iSail:");
				   			p.sendMessage(ChatColor.GOLD + "help - shows you list of commands");
				   			p.sendMessage(ChatColor.BOLD + "_____________________________________");
				   			p.sendMessage(ChatColor.GOLD + "broadcast (+ move/accident/timer/wind) - changing broadcastion of iSail.");
				   			p.sendMessage(ChatColor.BOLD + "_____________________________________");
				   			p.sendMessage(ChatColor.GOLD + "wind - wind information");
				   			p.sendMessage(ChatColor.BOLD + "_____________________________________");
				   			p.sendMessage(ChatColor.GOLD + "fixme - fix your position");
				   			p.sendMessage(ChatColor.GREEN + "This is all commands at this moment");
				   			break;
				   		}
				   }
			   }
			   else
			   {
				   p.sendMessage(ChatColor.RED + "[iSail] Uncnown command. Type /iSail help to help");
			   }
		   }
		   return false;
	   }
	   //Константы
	   //List<Ship> Ship;
	   //List<Player> player;
	   public static int waterLine = 62;
	   public static int limitOfSinking = 40;
	   public static int blocksPerPlayer = 500;
	   public static int MaxSizeOfShip = 1300;
	   public static int MinimalEcipage;
	   public static int LocalRange = 333;
	   public static long time = 10;
	   public static final String I= "Test";
	   public static enum SailType { SQUARE, TRIANGLE, REAR, FOREANDAFT, FRONT };
	   public static enum MahtType { FOK, GROT, BIZAN, DISABLE };
	   public static enum BroadcastType { DEFAULT, MOVE, ACCIDENT, TIMER, WIND }
	   private List<String> offDefault = new ArrayList<String>();
	   private List<String> offMove = new ArrayList<String>();
	   private List<String> offAccident = new ArrayList<String>();
	   private List<String> offTimer = new ArrayList<String>();
	   private List<String> offWind = new ArrayList<String>();
	   public void changeBroadcast(Player p, String str)
	   {
		   BroadcastType bt = toBroadcastType(str);
		   if(bt.equals(BroadcastType.DEFAULT))
		   {
			   if(offDefault.contains(p.getName()))
			   {
				   offDefault.remove(p.getName());
				   p.sendMessage(ChatColor.GREEN + "[iSail] Broadcast was enabled");
			   }
			   else
			   {
				   offDefault.add(p.getName());
				   p.sendMessage(ChatColor.RED + "[iSail] Broadcast was disabled");
			   }
		   }
		   if(bt.equals(BroadcastType.MOVE))
		   {
			   if(offMove.contains(p.getName()))
			   {
				   offMove.remove(p.getName());
				   p.sendMessage(ChatColor.GREEN + "[iSail] Move broadcast was enabled");
			   }
			   else
			   {
				   offMove.add(p.getName());
				   p.sendMessage(ChatColor.RED + "[iSail] Move broadcast was disabled");
			   }
		   }
		   if(bt.equals(BroadcastType.ACCIDENT))
		   {
			   if(offAccident.contains(p.getName()))
			   {
				   offAccident.remove(p.getName());
				   p.sendMessage(ChatColor.GREEN + "[iSail] Accident broadcast was enabled");
			   }
			   else
			   {
				   offAccident.add(p.getName());
				   p.sendMessage(ChatColor.RED + "[iSail] Accident broadcast was disabled");
			   }
		   }
		   if(bt.equals(BroadcastType.TIMER))
		   {
			   if(offTimer.contains(p.getName()))
			   {
				   offTimer.remove(p.getName());
				   p.sendMessage(ChatColor.GREEN + "[iSail] Timer broadcast was enabled");
			   }
			   else
			   {
				   offTimer.add(p.getName());
				   p.sendMessage(ChatColor.RED + "[iSail] Timer broadcast was disabled");
			   }
		   }
		   if(bt.equals(BroadcastType.WIND))
		   {
			   if(offWind.contains(p.getName()))
			   {
				   offWind.remove(p.getName());
				   p.sendMessage(ChatColor.GREEN + "[iSail] Wind broadcast was enabled");
			   }
			   else
			   {
				   offWind.add(p.getName());
				   p.sendMessage(ChatColor.RED + "[iSail] Wind broadcast was disabled");
			   }
		   }
	   }
	   public boolean canBroadcast(Player p, BroadcastType bt)
	   {
		   if(bt.equals(BroadcastType.DEFAULT)) return !offDefault.contains(p.getName());
		   if(bt.equals(BroadcastType.MOVE)) return !(offMove.contains(p.getName()) || offDefault.contains(p.getName()));
		   if(bt.equals(BroadcastType.ACCIDENT)) return !(offAccident.contains(p.getName()) || offDefault.contains(p.getName()));
		   if(bt.equals(BroadcastType.TIMER)) return !(offTimer.contains(p.getName()) || offDefault.contains(p.getName()));
		   if(bt.equals(BroadcastType.WIND)) return !(offWind.contains(p.getName()) || offDefault.contains(p.getName()));
		   return true;
	   }
	   public static World world;
	   public static final String refreshString = ChatColor.GREEN+ "hit refresh";
	   public static final String shipString = ChatColor.GOLD + "[ship]";
	   public static final String speedString = ChatColor.DARK_AQUA + "Speed: ";
	   public static final String healthString = ChatColor.GREEN + "Hp: ";
	   public static final String openS = ChatColor.GREEN+"raised";
	   public static final String closeS = ChatColor.RED+"lower";
	   public static final String sailS = ChatColor.WHITE + "[sail]";
	   public static final String t = ChatColor.WHITE + "==takelage==";
	   public static final String lt = ChatColor.WHITE + "===left===";
	   public static final String rt = ChatColor.WHITE + "===right===";
	   public static final String mahtCreate = "m";
	   public static final String fokCreate = "f";
	   public static final String grotCreate = "g";
	   public static final String bizanCreate = "b";
	   public static final String disableCreate = "d";
	   public static final String sailCreate = "s";
	   public static final String squareType = "s";
	   public static final String triangleType = "t";
	   public static final String rearType = "r";
	   public static final String foreAndAftType = "f";
	   public static final String frontType = "fr";
	   public static final String wheel = ChatColor.YELLOW + "\\   I   I   /";
	   public static final String wheelR = ChatColor.YELLOW + "  \\    I   I ";
	   public static final String wheelL = ChatColor.YELLOW + "I   I    /   ";
	   public static final String wheelD = ChatColor.YELLOW + "/  I  I  \\";
	   public static final String wheelDR = ChatColor.YELLOW + "/  I  I      ";
	   public static final String wheelDL = ChatColor.YELLOW + "      I  I  \\";
	   public static final String portString = ChatColor.GREEN + "[Port]";
	   public static final String portCreate = "port";
	   public static final String bridgeString = ChatColor.GREEN + "[Bridge]";
	   public static final String bridgeCreate = "b";
	   public static final String anchorCreate = "anchor";
	   public static final String upAnchor= ChatColor.WHITE + "| |";
	   public static final String upAnchor1 = ChatColor.WHITE + "| |";
	   public static final String upAnchor2 = ChatColor.WHITE + "\\\\__| |__//";
	   public static final String upAnchor3 = ChatColor.WHITE + "\\/";
	   public static final String downAnchor = ChatColor.YELLOW + "| |";
	   public static double windDirection = 0;
	   public static double windPower = 5;
	   public static int weather = 1;
	   public static int weatherMax = 18;
	   public static int weatherMin = 2;
	   public static int g = 10;
	   public static int cannonId = 35;
	   public static int windPhase = 35;
	   public static int climbAbleId = 85;
	   public static byte cannonData = 15;
	   public static int cannonTorch = 50;
	   public static boolean useCannons = false;//TODO
	   public static boolean useLot = true;
	   //public static Plugin iSail;
		public static TreeMap<String, Integer> pompers = new TreeMap<String, Integer>();
		@EventHandler
		public void Quench(PlayerInteractEvent e)//iQuench
		{
			Player p = e.getPlayer();
			Block b = e.getClickedBlock();
			if(e.getClickedBlock() != null)
			{
				if(p.getItemInHand().equals(new ItemStack(Material.WATER_BUCKET, 1)))
				{
						if(e.getAction().equals(Action.LEFT_CLICK_BLOCK))
						{
							p.getInventory().setItemInHand(new ItemStack(Material.BUCKET, 1));
							boolean fire = false;
							for(int x = -2; x<=2; x++)
							{
								for(int z = -2; z<=2; z++)
								{
									for(int y = -25; y<=3; y++)
									{
										Block newB = b.getRelative(x, y, z);
										if(newB.getType().equals(Material.FIRE))
										{
											newB.setType(Material.AIR);
											fire = true;
										}
									}
								}
							}
							if(fire) p.playEffect(p.getLocation(), Effect.EXTINGUISH, 10);
						}
				}
				else if(p.getItemInHand().getTypeId() == 33)
				{
					b = e.getClickedBlock().getRelative(e.getBlockFace());
					int id = b.getTypeId();
					if(8 <= id && id <= 11) drain(b, p);
				}
			}
		}
		public void drain(Block b, Player p)
		{
			if(!pompers.containsKey(p.getName())) pompers.put(p.getName(), 0);
			final int c = pompers.get(p.getName());
			if(c == 20)
			{
				List<Block> water = getWater(b);
				if(water != null) for(int i = 0; i < water.size(); i ++)
				{
					Block newB = water.get(i);
					newB.setTypeIdAndData(0, (byte) 0, true);
				}
				p.playSound(p.getLocation(), Sound.SWIM, 10, 10);
				pompers.remove(p.getName());
				pompers.put(p.getName(), 0);
			}
			else
			{
				pompers.remove(p.getName());
				pompers.put(p.getName(), c+1);
			}
		}
		public List<Block> getWater(Block b)
		{
			List<Block> water = new ArrayList<Block>();
			Block up = getUp(b);
			water.addAll(pickWater(up, 555));
			return water;
		}
		public Collection<Block> pickWater(Block b, int waterLimit)
		{
			TreeMap<String, Block> wm = new TreeMap<String, Block>();
			List<Block> water = new ArrayList<Block>();
			water.add(b);
			wm.put(getKey(b), b);
			while(0 < water.size())
			{
				b = water.get(0);
				water.remove(0);
				for(int y = -1; y <= 1; y++)
					for(int x = -1; x <= 1; x++)
						for(int z = -1; z <= 1; z++)
						{
							Block newB = b.getRelative(x, y, z);
							if(wm.size() < waterLimit) if(!wm.containsKey(getKey(newB)))
							{
								final int id = newB.getTypeId();
								if(8 <= id && id <= 11)
								{
									water.add(newB);
									wm.put(getKey(newB), newB);
								}
							}
						}
			}
			return wm.values();
		}
		public boolean isIsland(Block b)
		{
			TreeMap<String, Block> lm = new TreeMap<String, Block>();
			List<Block> land = new ArrayList<Block>();
			land.add(b);
			lm.put(getKey(b), b);
			while(0 < land.size())
			{
				b = land.get(0);
				land.remove(0);
				for(int y = -1; y <= 1; y++)
					for(int x = -1; x <= 1; x++)
						for(int z = -1; z <= 1; z++)
						{
							Block newB = b.getRelative(x, y, z);
							if(lm.size() < 500) 
							{
								if(!lm.containsKey(getKey(newB)))
								{
									final int id = newB.getTypeId();
									if(isla(id))
									{
										land.add(newB);
										lm.put(getKey(newB), newB);
									}
								}
							}
							else return true;
						}
			}
			return false;
		}
		public boolean isba(int i)
		{
			return i == 5 || i == 17 || i == 20 || i == 22 || i == 35 || i == 41 || i == 42 || i == 43 || i == 44 || i == 46 || i == 47 || i == 53 || i == 57 || i == 59 || i == 85 || i == 101 || i == 102 || i ==  112 || i == 113 || i == 114 || i == 125 || i == 126 || i == 131 || i == 132 || i == 133 || i == 134 || i == 135 || i == 136;
		}
		public boolean isha(int i)
		{
			return  i == 18 || i == 19 || i == 24 || i == 25 || i == 27 || i == 28 || i == 30 || i == 31 || i == 32 || i == 37 || i == 38 || i == 39 || i == 40 || i == 45 || i == 48 || i == 49 || i == 50 || i == 51 || i == 52 || i == 60 || i == 64 || i == 65 || i == 66 || i == 67 || i == 68 || i == 69 || i == 71 || i == 77 || i == 81 || i == 82 || i == 83 || i == 25 || i == 87 || i == 88 || i == 89 || i == 90 || i == 91 || i == 92 || i == 96 || i == 97 || i == 98 || i == 99 || i == 100 || i == 103 || i == 106 || i == 107 || i == 108 || i == 109 || i == 110 || i == 121 || i == 123 || i == 124 || i == 128 || i == 127 || i == 139 || i == 143;
		}
		public boolean isia(int i)
		{
			return i == 54 || i == 58 || i == 61 || i == 62 || i == 116 || i == 117 || i == 145 || i == 146 || i == 154 || i == 158; //TODO
		}
		public boolean isea(int i)
		{
			return i == 0 || 8 <= i && i <= 11 || i == 35 || i == 18 || i == 51 || i == 111;
		}
		public boolean isla(int i)
		{
			return 1 <= i && i <= 4 || i == 12 || i == 13;
		}
		public String getKey(Block b)
		{
			return new StringBuilder().append("x").append(b.getX()).append("y").append(b.getY()).append("z").append(b.getZ()).toString();
		}
		public Block getUp(Block b)
		{
			while(b.isLiquid()) b = b.getRelative(0, 1, 0);
			return b.getRelative(0, -1, 0);
		}
		@EventHandler
		public void onBurn(BlockBurnEvent e)
		{
			Block b = e.getBlock();
			for(int x = -1; x <= 1; x++)
				for(int y = -1; y <= 1; y++)
					for(int z = -1; z <= 1; z++)
					{
						Block newB = b.getRelative(x, y, z);
						if(newB.getTypeId() == 51) newB.setData((byte) 0, true);
					}
			double chance = 0.5;
			if(b.getTypeId() == 35) chance = 0.1;
			else if(b.getTypeId() == 5) chance = 0.04;
			else if(b.getTypeId() == 17) chance = 0.03;
			if(chance <= Math.random()) e.setCancelled(true);
			/*for(int x = -2; x <= 2; x++)
				for(int y = -1; y <= 3; y++)
					for(int z = -2; z <= 2; z++)
					{
						Block newB = b.getRelative(x, y, z);
						double ch = 0.000005;
						if(0 < y && x == 0 && z == 0) ch = 0.18/(double) y;
						if(y == -1) ch = 0.000002;
						if(newB.getTypeId() == 0 && Math.random() <= ch) newB.setTypeIdAndData(51, (byte) 0, true);
					}*/
		}
	   @EventHandler
	   public void onJoin(PlayerJoinEvent e)
	   {
		   Player p = e.getPlayer();
		   String name = p.getName();
		   if(sleepPlayers.contains(name))
		   {
			   sleepPlayers.remove(name);
			   boolean drowned = true;
			   Ship ship = null;
			   for(int i = 0; i < ships.size(); i++)
			   {
				   ship = ships.get(i);
				   if(ship.sleeps.contains(name))
				   {
					   drowned = false;
					   ship.sleeps.remove(name);
					   p.teleport(ship.l);
					   p.sendMessage("[iSail] Succefuly joined a ship " + ship.name);
					   _log.info("[iSail] Player " + p.getName() + " joined to ship " + ship.name);
					   break;
				   }
			   }
			   if(drowned)
			   {
				   p.sendMessage("[iSail] You are drowned!");
				   _log.info("[iSail] Player " + p.getName() + " drowned!");
				   p.setHealth(0);
			   }
		   }
	   }
	   @EventHandler
	   public void onLogout(PlayerQuitEvent e)
	   {
		   Player p = e.getPlayer();
		   String name = p.getName();
		   for(int i = 0; i < ships.size(); i++)
		   {
			   Ship ship = ships.get(i);
			   if(ship.entityList.contains(p))
			   {
				   sleepPlayers.add(name);
				   ship.sleeps.add(name);
				   _log.info("[iSail] Player " + p.getName() + " start sleep at ship " + ship.name);
			   }
		   }
	   }
	   @EventHandler 
	   public void onBlockFade(BlockFadeEvent e)
	   {
		   if(e.getBlock().getTypeId() == 51) e.setCancelled(true);
	   }
		@EventHandler//Клик по табличке (Открытие и закрытие паруса, создание паруса, поворот, движение якоря)
		public void onInteract(PlayerInteractEvent e)
		{
			Player p = e.getPlayer();
			if(e.getClickedBlock()!=null) 
				{
					Block b = e.getClickedBlock();
					if(p.isSneaking() && b.getTypeId()  == climbAbleId && p.getLocation().add(0, -1, 0).distance(b.getLocation()) <= 3.5)
					{
						if(b.getRelative(BlockFace.UP).getTypeId() == climbAbleId || b.getRelative(BlockFace.DOWN).getTypeId() == climbAbleId) p.teleport(new Location(b.getWorld(), b.getX() + 0.5, b.getY() + 0.5, b.getZ() + 0.5, p.getLocation().getYaw(), p.getLocation().getPitch()));
						else if(b.getRelative(BlockFace.DOWN).isEmpty() || b.getRelative(BlockFace.UP).isEmpty()) p.teleport(new Location(b.getWorld(), b.getX() + 0.5, b.getY() + 1.5, b.getZ() + 0.5, p.getLocation().getYaw(), p.getLocation().getPitch()));
					}
					else if(b.getType().equals(Material.WALL_SIGN)) //Если игрок кликнул по табличке
					{
						Sign s = (Sign) b.getState();
						String lines[] = s.getLines();
						if(lines[0].equals(bridgeString) && p.getItemInHand().getTypeId() == 5)
						{
							int doit = 0;
							for(int i = 2; i < 12; i++)
							{
								Block newB = getRelativeToMain(i, b);
								if(!newB.isEmpty())
								{
									doit = i;
									break;
								}
							}
							if(doit != 0 && doit <= p.getItemInHand().getAmount()) for(int i = 2; i < doit; i++)
							{
								Block newB = getRelativeToMain(i, b);
								if(newB.isEmpty() && p.getItemInHand().getTypeId() == 5)
								{
									if(p.getItemInHand().getAmount() == 1) p.setItemInHand(null);
									else p.setItemInHand(new ItemStack(5, p.getItemInHand().getAmount() -1));
									newB.setTypeIdAndData(5, p.getItemInHand().getData().getData(), true);
								}
							}
						}
						if(lines[0].equals(mahtCreate))
						{
							String type = lines[1];
							if(type.equals(fokCreate) || type.equals(grotCreate) || type.equals(bizanCreate) || type.equals(disableCreate))
							{
								MahtType mType = toMahtType(type);
								Block mahtBlock = getMahtBlockFromSignOnMaht(b);
								if(isMahtableBlock(mahtBlock))
								{
									if(mType != null) 
									{
										if(createMaht(mahtBlock, p, mType)) b.breakNaturally();
									}
									else p.sendMessage(ChatColor.RED + "Invalid type of mast!");
								}
								else p.sendMessage(ChatColor.RED + "Invalid form of mast!");
							}
						}
						else if(lines[0].equals("lt"))
						{
							Sail sail = getMakingSail(p);
							if(sail != null)
							{
								if(!sail.haveLeft)
								{
									sail.left = b;
									sail.haveLeft = true;
									s.setLine(0, lt);
									s.setLine(1, closeS);
									s.setLine(2, "0");
									s.setLine(3, t);
									if(sail.haveRight)
									{
										cs.remove(p.getName());
										p.sendMessage(ChatColor.GREEN + "[iSail] Succefully created new sail!");
										//sail.close();
										sail.maker = null;
									}
									s.update();
								}
								else
								{
									if(sail.haveLeft && sail.haveRight)
									{
										sail.maker = null;
										sail.disable();
										if(cs.containsKey(p.getName())) cs.remove(p.getName());
										p.sendMessage(ChatColor.RED + "[iSail] Error occupied while making sail!!!");
									}
									p.sendMessage(ChatColor.RED + "[iSail] Left takelage of this sail is already exists!");
								}
							}
							else p.sendMessage(ChatColor.RED + "[iSail] You are not maker of sail!");
						}
						else if(lines[0].equals("rt"))
						{
							Sail sail = getMakingSail(p);
							if(sail != null)
							{
								if(!sail.haveRight)
								{
									sail.right = b;
									sail.haveRight= true;
									s.setLine(0, rt);
									s.setLine(1, closeS);
									s.setLine(2, "0");
									s.setLine(3, t);
									if(sail.haveLeft)
									{
										cs.remove(p.getName());
										p.sendMessage(ChatColor.GREEN + "[iSail] Succefully created new sail!");
										//sail.close();
										sail.maker = null;
									}
									s.update();
								}
								else
								{
									if(sail.haveLeft && sail.haveRight)
									{
										sail.maker = null;
										sail.disable();
										if(cs.containsKey(p.getName())) cs.remove(p.getName());
										p.sendMessage(ChatColor.RED + "[iSail] Error occupied while making sail!!!");
									}
									p.sendMessage(ChatColor.RED + "[iSail] Right takelage of this sail is already exists!");
								}
							}
							else p.sendMessage(ChatColor.RED + "[iSail] You are not maker of sail!");
						}
						else if(lines[0].equals(sailCreate))
						{
							if(!p.getItemInHand().getType().equals(Material.SHEARS))
							{
								if(!isMakerOfSail(p))
								{
									SailType type;
									if(lines[2].equals(refreshString)) type = SailType.valueOf(lines[1]);
									else type = toSailType(lines[1]);
									if(type != null)
									{
										if(type.equals(SailType.SQUARE) || type.equals(SailType.TRIANGLE) || type.equals(SailType.REAR) || type.equals(SailType.FOREANDAFT) || type.equals(SailType.FRONT))
										{
											Ship ship = getShipFromPlayer(p);
											if(ship!=null)
											{
												if(type.equals(SailType.FRONT)) createSail(ship, null, b, p, type);
												else 
												{
													Block mahtBlock = getMahtBlockForSail(ship, type, b);
													if(mahtBlock!= null)
													{
														Maht maht = getMahtAtShip(ship, mahtBlock);
														if(maht != null)
														{
															if(isConnectableToMaht(type, maht.type)) createSail(ship, maht, b, p, type);
															else p.sendMessage(ChatColor.RED + "[iSail] You can't make this sail on this mast!");
														}
														else p.sendMessage(ChatColor.RED + "[iSail] No mast detected!");
													}
													else p.sendMessage(ChatColor.RED + "[iSail] No mast blocks detected!");
												}
											}
											else p.sendMessage(ChatColor.RED + "[iSail] No ship detected!");
										}
										else p.sendMessage(ChatColor.RED + "[iSail] Invalid type of sail!");
									}
									else p.sendMessage(ChatColor.RED + "[iSail] Invalid type of sail!");
								}
								else p.sendMessage(ChatColor.RED + "[iSail] You are already making a sail!");
							}
						}
						else if(lines[3].equals(t))
						{
							Ship ship = getShipFromPlayer(p);
							if(ship != null) ship.changeSails(b, e.getAction());
						}
						if(p.getItemInHand().getType().equals(Material.WOOL) || p.getItemInHand().getType().equals(Material.SHEARS))
						{
							if(lines[0].equals(sailS))
							{
								Ship ship = getShipFromPlayer(p);
								if(ship!=null)
								{
									Sail sail = ship.getSailFromSignBlock(b);
									if(sail != null)
									{
										if(p.getItemInHand().getType().equals(Material.WOOL))
										{
											final int sp = sail.getPower(false);
											if(!sail.open) p.sendMessage(ChatColor.RED + "[iSail] Power of this sail is 0, because sail is lowered");
											else if(!sail.haveLeft) p.sendMessage(ChatColor.RED + "[iSail] Power of this sail is 0, because left takelage of this sail is not created");
											else if(!sail.haveRight) p.sendMessage(ChatColor.RED + "[iSail] Power of this sail is 0, because right takelage of this sail is not created");
											else if(sail.left == null) p.sendMessage(ChatColor.RED + "[iSail] Power of this sail is small, because left takelage of this sail is broken");
											else if(sail.right == null) p.sendMessage(ChatColor.RED + "[iSail] Power of this sail is small, because right takelage of this sail is broken");
											else if(sail.signBlock.isEmpty()) p.sendMessage(ChatColor.RED + "[iSail] Power of this sail is small, because this sail is broken");
											else if(sail.left.getTypeId() != 68) p.sendMessage(ChatColor.RED + "[iSail] Power of this sail is small, because left takelage must be assisted by wall sign");
											else if(sail.right.getTypeId() != 68) p.sendMessage(ChatColor.RED + "[iSail] Power of this sail is small, because right takelage must be assisted by wall sign");
											else if(sp == 0) p.sendMessage(ChatColor.GOLD + "[iSail] Power of this sail is 0, because no wool detected or speed of ship equals speed of wind");
											else if(sp < 0) p.sendMessage(ChatColor.GOLD + "[iSail] Power of this sail is 0, because speed of wind < speed of ship or ship is moving againist the wind");
											else p.sendMessage(ChatColor.GOLD + "[iSail] Power of this sail is " + String.valueOf(sail.getPower(false)));
											p.sendMessage(ChatColor.GOLD + "[iSail] Level = " + String.valueOf(sail.getWd()));
											p.sendMessage(ChatColor.GOLD + "[iSail] Different = " + String.valueOf(sail.getDifferent()));
											p.sendMessage(ChatColor.GOLD + "[iSail] Angle = " + String.valueOf(ship.direction + getDegrees(ship.data) - windDirection));
										}
										else
										{
											sail.disable();
											p.sendMessage(ChatColor.RED + "[iSail] Succefully disabled sail!");
										}
									}
									else p.sendMessage(ChatColor.RED + "[iSail] No sail detected");
								}
								else p.sendMessage(ChatColor.RED + "[iSail] No ship detected");
							}
						}
						if(lines[0].equals("ship")) //Создание корабля
						{
							for(int i = 0; i < ships.size(); i++)
							{
								final Ship ship = ships.get(i);
								if(b.equals(ship.l.getBlock()))
								{
									p.sendMessage(ChatColor.GREEN + "[iSail] You was refreshed ship " + ship.name + " with size " + ChatColor.DARK_PURPLE + ship.amount + ChatColor.GREEN + " blocks and max height and max lenght of sails is " + ChatColor.WHITE + ship.maxHeightOfSail + "X" + String.valueOf(ship.maxLengthOfSail*2+1) + ChatColor.GREEN + " after reload!");
									ship.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new 
											Runnable()
											{
												@Override
												public void run()
												{
													ship.startMove();
												}
											}, 0L, 20L);
									s.setLine(0, shipString);
									s.setLine(1, ship.name);
									s.setLine(2, ChatColor.WHITE + "Refreshing");
									s.setLine(3, ChatColor.GREEN + " " + ship.hp + "/" + ship.maxHp + " ");
									ship.ek.put(p.getUniqueId(), (Entity) p);
									ship.ecipage.add(p);
									s.update();
								}
							}
						}
						if(lines[0].equals(wheel) || lines[0].equals(wheelR) || lines[0].equals(wheelL))
						{
							if(b.getRelative(0,-1,0).getType().equals(Material.WALL_SIGN))
							{
								Sign ss = (Sign) b.getRelative(0,-1,0).getState();
								if(ss.getLine(0).equals(shipString))
								{
									Ship ship = null;
									for(int i = 0; i < ships.size(); i++)
									{
										if(ss.getLine(1).equals(ships.get(i).name)) ship = ships.get(i);
									}
									if(ship != null)
									{
										if(e.getAction().equals(Action.LEFT_CLICK_BLOCK))
										{
											rotateWheelLeft(ship, s);
										}
										else 
										{
											rotateWheelRight(ship, s);
										}
									}
								}
							}
						}
						if(lines[0].equals(upAnchor) || lines[0].equals(downAnchor))//Если это якорь (изменение якоря)
						{
							if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
							{
								s.setLine(0, upAnchor);
								s.setLine(1, upAnchor1);
								s.setLine(2, upAnchor2);
								s.setLine(3, upAnchor3);
								s.update();
							}
							if(e.getAction().equals(Action.LEFT_CLICK_BLOCK))
							{
								s.setLine(0, downAnchor);
								s.setLine(1, downAnchor);
								s.setLine(2, downAnchor);
								s.setLine(3, downAnchor);
								s.update();
							}
						}
					}
				}
		}
		@EventHandler
		public void signsCreating(final SignChangeEvent e) // Изменение таблички (создание якоря, корабля, парусов)
		{
			String lines[]= e.getLines();
			if(lines[0].equals(anchorCreate)) //Создание якоря
			{
				e.setLine(0, upAnchor);
				e.setLine(1, upAnchor1);
				e.setLine(2, upAnchor2);
				e.setLine(3, upAnchor3);
				e.getPlayer().sendMessage(ChatColor.GREEN + "[iSail] You was created an anchor!");
			}
			if(lines[0].equals(portCreate) && isIsland(e.getBlock())) //Создание порта
			{
				e.setLine(0, portString);
				e.getPlayer().sendMessage(ChatColor.GREEN + "[iSail] You was created an port!");
			}
			if(lines[0].equals(bridgeCreate)) //Создание моста
			{
				e.setLine(0, bridgeString);
				e.getPlayer().sendMessage(ChatColor.GREEN + "[iSail] You was created an bridgemaker!");
			}
			if(lines[0].equals("ship")) //Создание корабля
			{
				if(lines[1] != null)
				{
					boolean copy = false;
					for(int i = 0; i<ships.size(); i++)
					{
						if(ships.get(i).name.equals(e.getLine(1)))
						{
							copy = true;
						}
					}
					if(!copy)
					{
						Block b = e.getBlock();
						final Ship ship = new Ship(b.getLocation(), e.getLine(1), b.getData(), false);
						if(!ship.isBoarded())
						{
							ship.name = e.getLine(1);
							e.setLine(0, shipString);
							e.setLine(2, ChatColor.WHITE + "Creating");
							ships.add(ship);
							ship.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new 
									Runnable()
									{
										@Override
										public void run()
										{
											ship.startMove();
										}
									}, 0L, 20L);
							e.getPlayer().sendMessage(ChatColor.GREEN + "You was created a ship " + ChatColor.GOLD + ship.name + ChatColor.GREEN + " with size " + ChatColor.DARK_PURPLE + ship.amount + ChatColor.GREEN + " blocks and max height/max lenght of sails is " + ship.maxHeightOfSail + "X" + String.valueOf(ship.maxLengthOfSail*2+1));
							_log.info("[iSail] player " + e.getPlayer().getName() + " was created a ship " + ship.name + " with size " + ship.amount + " blocks");
						}
						else
						{
							b.breakNaturally();
							e.getPlayer().sendMessage(ChatColor.RED + "It is already a ship!");
						}
					}
					else 
					{
						e.getPlayer().sendMessage(ChatColor.RED + "[iSail] Sorry, but name " + ChatColor.GOLD + e.getLine(1) + ChatColor.RED + " is already exists!");
						_log.warning("[iSail] player " + e.getPlayer().getName() + " tried plagiate name of ship " + e.getLine(1));
					}
				}
				else e.getPlayer().sendMessage(ChatColor.RED + "[iSail] Type name on second line!");
			}
		}
		public boolean isMahtableBlock(Block b)
		{
			for(int i = 0; i < 5; i++)
			{
				Block newB1 = b.getRelative(0, i, 0);
				Block newB2 = b.getRelative(0, -i, 0);
				if(!(newB1.getTypeId() == 5 || newB1.getTypeId() == 17 || newB2.getTypeId() == 5 || newB2.getTypeId() == 17)) return false;
			}
			return true;
		}
		public Block getMahtBlockForSail(Ship ship, SailType type, Block signBlock)//FIXME
		{
			byte data = ship.data;
			if(type.equals(SailType.SQUARE))
			{
				switch(data)
				{
					case 5:
					{
						if(isMahtableBlock(signBlock.getRelative(2,0,0))) return signBlock.getRelative(2,0,0);
						break;
					}
					case 4:
					{
						if(isMahtableBlock(signBlock.getRelative(-2,0,0))) return signBlock.getRelative(-2,0,0);
						break;
					}
					case 3:
					{
						if(isMahtableBlock(signBlock.getRelative(0,0,2))) return signBlock.getRelative(0,0,2);
						break;
					}
					case 2:
					{
						if(isMahtableBlock(signBlock.getRelative(0,0,-2))) return signBlock.getRelative(0,0,-2);
						break;
					}
				}
			}
			else if(type.equals(SailType.TRIANGLE))
			{
				for(int i = 0; i <= 50; i++)
				{
					Block newB = getRelativeToMain(i, signBlock).getRelative(0, 1, 0);
					if(newB.getTypeId() == 5 || newB.getTypeId() == 17) if(isMahtableBlock(newB)) return newB;
				}
			}
			else if(type.equals(SailType.REAR))
			{
				for(int i = 0; i <= 50; i++)
				{
					Block newB = getRelativeToMain(i, signBlock).getRelative(0, 1, 0);
					if(newB.getTypeId() == 5 || newB.getTypeId() == 17) if(isMahtableBlock(newB)) return newB;
				}
			}
			else if(type.equals(SailType.FOREANDAFT))
			{
				switch(data)
				{
					case 5:
					{
						if(isMahtableBlock(signBlock.getRelative(-1,0,0))) return signBlock.getRelative(-1,0,0);
						break;
					}
					case 4:
					{
						if(isMahtableBlock(signBlock.getRelative(1,0,0))) return signBlock.getRelative(1,0,0);
						break;
					}
					case 3:
					{
						if(isMahtableBlock(signBlock.getRelative(0,0,-1))) return signBlock.getRelative(0,0,-1);
						break;
					}
					case 2:
					{
						if(isMahtableBlock(signBlock.getRelative(0,0,1))) return signBlock.getRelative(0,0,1);
						break;
					}
				}
			}
			return null;
		}
		public Block getRelativeToMain(int i, Block signBlock)
		{
			final byte data = signBlock.getData();
			switch(data)
			{
				case 5:
				{
					return signBlock.getRelative(-i,0,0);
				}
				case 4:
				{
					return signBlock.getRelative(i,0,0);
				}
				case 3:
				{
					return signBlock.getRelative(0,0,-i);
				}
				case 2:
				{
					return signBlock.getRelative(0,0,i);
				}
			}
			return null;
		}
		@EventHandler
		public void test(PlayerInteractEvent e)
		{
			Player p = e.getPlayer();
			//if(p.getName().equals("Test"))
			//{
				Block b = e.getClickedBlock();
				if(b != null)
				{
					if(p.getItemInHand().getType().equals(Material.STICK)) 
					{
						Ship ship = getShipFromPlayer(p);
						if(ship != null)
						{
							Maht maht = getMahtAtShip(ship, b);
							if(maht != null)
							{
								p.sendMessage(new StringBuilder().append(ChatColor.BOLD).append("[iSail] Ship: ").append(ship.name).append(", mast: ").append(maht.type.toString().toLowerCase()).append(", square sails: ").append(maht.sails.size()).toString());
							}
							//else if(p.getName().equals("Test")) p.sendMessage(new StringBuilder("Id = ").append(b.getTypeId()).append(", data = ").append(b.getData()).toString());
						}
					}
					/*if(b.getTypeId() == 54)
					{
						Chest c = (Chest) b.getState();
						e.getPlayer().sendMessage(String.valueOf(c.getInventory().getSize()));
						e.getPlayer().sendMessage(String.valueOf(c.getInventory().getContents().length));
					}*/
				}
			//}
			/*e.getPlayer().sendMessage(String.valueOf(b.getData()));
			if(b != null)
			{
				Player p = e.getPlayer();
				for(int i = 0; i < ships.size(); i++)
				{
					Ship ship = ships.get(i);
					if(ship.entityList.contains(p))
					{
						for(int k = 0; k < ship.mahts.size(); k++)
						{
							Maht m = ship.mahts.get(k);
							if(b.getX() == m.b.getX() && b.getZ() == m.b.getZ())
							{
								p.sendMessage("Maht");
							}
						}
					}
				}
			}*/
		}
		public void rotateWheelRight(Ship ship, Sign s)
		{
			String lines[] = s.getLines();
			if(ship.wheelRotating < 18)
			{
				ship.wheelRotating++;
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
		public void rotateWheelLeft(Ship ship, Sign s)
		{
			String lines[] = s.getLines();
			if(-18 < ship.wheelRotating)
			{
				ship.wheelRotating--;
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
		public Block getMahtBlockFromSignOnMaht(Block signBlock)
		{
			byte data = signBlock.getData();
			switch(data)
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
		public boolean createMaht(Block mahtBlock, Player maker, MahtType type)
		{
			Ship ship = getShipFromPlayer(maker);
			if(ship!=null)
			{
				if(!ship.haveMahtType(type) || type.equals(MahtType.DISABLE)) 
				{
					if(!ship.haveMaht(mahtBlock)) 
					{
						Maht maht = new Maht(ship, mahtBlock, type);
						ship.addMaht(maht, maker);
						return true;
					}
					else
					{
						if(type.equals(MahtType.DISABLE)) if(ship.getMaht(mahtBlock).remove()) maker.sendMessage(ChatColor.GREEN + "[iSail] Successfully disabled mast!");
						else maker.sendMessage(ChatColor.RED + "[iSail] Firstly disable all sails on this mast!");
						else maker.sendMessage(ChatColor.RED + "[iSail] This is already a mast!");
					}
				}
				else maker.sendMessage(ChatColor.RED + "[iSail] Ship " + ship.name + " is already have type of mast!");
			}
			else maker.sendMessage(ChatColor.RED + "[iSail] No ship detected!");
			return false;
		}
		public void createSail(Ship ship, Maht maht, Block signBlock, Player maker, SailType type)
		{
			Sail sail = new Sail(ship, maht, signBlock, maker, type);
			if(sail.L * sail.H != 0)
			{
				Sign s = (Sign) signBlock.getState();
				s.setLine(0, sailS);
				s.setLine(1, type.toString().toLowerCase());
				s.setLine(2, "by");
				s.setLine(3, maker.getName());
				s.update();
				cs.put(maker.getName(), sail);
				if(type.equals(SailType.SQUARE)) maht.sails.add(sail);
				if(type.equals(SailType.FOREANDAFT)) maht.foreAndAft = sail;
				if(type.equals(SailType.REAR)) maht.rear = sail;
				if(type.equals(SailType.TRIANGLE)) maht.triangle = sail;
				if(type.equals(SailType.FRONT)) ship.front = sail;
				if(maht != null) maker.sendMessage(ChatColor.GREEN + "[iSail] You was created " + type.toString().toLowerCase() + " sail on mast " + sail.maht.type.toString().toLowerCase() + " on ship " + ship.name + "!");
				else maker.sendMessage(ChatColor.GREEN + "[iSail] You was created " + type.toString().toLowerCase() + " on ship " + ship.name + "!");
			}
			else maker.sendMessage(ChatColor.RED + "[iSail] Invalid form of sail " + type.toString().toLowerCase() + "!");
		}
		public MahtType toMahtType(String type)
		{
			if(type.equals(fokCreate)) return MahtType.FOK;
			else if(type.equals(grotCreate)) return MahtType.GROT;
			else if(type.equals(bizanCreate)) return MahtType.BIZAN;
			else if(type.equals(disableCreate)) return MahtType.DISABLE;
			else return null;
		}
		public SailType toSailType(String s)
		{
			switch(s)
			{
				case squareType: return SailType.SQUARE;
				case triangleType: return SailType.TRIANGLE;
				case foreAndAftType: return SailType.FOREANDAFT;
				case rearType: return SailType.REAR;
				case frontType: return SailType.FRONT;
				default: return null;
			}
		}
		public BroadcastType toBroadcastType(String s)
		{
			switch(s)
			{
				case "timer": return BroadcastType.TIMER;
				case "move": return BroadcastType.MOVE;
				case "accident": return BroadcastType.ACCIDENT;
				case "wind": return BroadcastType.WIND;
				default: return BroadcastType.DEFAULT;
			}
		}
		/*public void connectSailToMahtlockAtShip(Ship ship, Block mahtBlock, Sail sail, Player maker, MahtType type)//Создаёт или находит мачту
		{
			Maht m = getMahtAtShip(ship, mahtBlock, maker);
			if(m == null)
			{
				maker.sendMessage(ChatColor.GREEN + "[iSail] Connecting sail to mast");
				connectSailToMahtAtShip(ship, m, sail, maker, type);
			}
			else maker.sendMessage(ChatColor.RED+ "[iSail] Creating of new mast was failed!");
		}
		public void connectSailToMahtAtShip(Ship ship, Maht maht, Sail sail, Player maker, MahtType type)//Соединяет парус с мачтой
		{
			if(isConnectableToMaht(sail, maht))
			{
				maht.sails.add(sail);
				maker.sendMessage(ChatColor.GREEN + "[iSail] You was created sail!" + ChatColor.BOLD + " Now you mast make takelage!");
			}
			else maker.sendMessage(ChatColor.RED + "[iSail] This sail can't connect to this mast!");
		}*/
		/*public Sail createSailAtMaht(Maht maht, Block signBlock, Player maker, SailType type)
		{
			Ship ship = maht.ship;
			int size = maht.sails.size();
			if(size < 5)
			{
				if((signBlock.getX() - maht.b.getX() <=2 || -2 <= signBlock.getX() - maht.b.getX()) && signBlock.getZ() == maht.b.getZ() || (signBlock.getZ() - maht.b.getZ() <=2 || -2 <= signBlock.getZ() - maht.b.getZ()) && signBlock.getX() == maht.b.getX())
				{
					Sail sail = getSailAtMaht(maht, signBlock, maker, type);
					if(sail == null)
					{
						sail = new Sail(ship, maht, signBlock, maker, type);
						maht.sails.add(sail);
						maker.sendMessage(ChatColor.GREEN + "[iSail] Adding new sail");
						return sail;
					}
					else maker.sendMessage(ChatColor.RED+ "[iSail] Creating of new sail was failed: sail already exists!");
				}
			}
			else maker.sendMessage(ChatColor.RED+ "[iSail] Creating of new sail was failed: max amount of sails per mast is 4!");
			return null;
		}*/
		public Maht getMahtAtShip(Ship ship, Block mahtBlock)
		{
			if(ship.fok != null) if(ship.fok.b.getX() == mahtBlock.getX() && ship.fok.b.getZ() == mahtBlock.getZ()) return ship.fok;
			if(ship.grot != null) if(ship.grot.b.getX() == mahtBlock.getX() && ship.grot.b.getZ() == mahtBlock.getZ()) return ship.grot;
			if(ship.bizan != null) if(ship.bizan.b.getX() == mahtBlock.getX() && ship.bizan.b.getZ() == mahtBlock.getZ()) return ship.bizan;
			return null;
		}
		public Sail getSailAtMaht(Maht maht, Block signBlock, Player maker, String type)//TODO USED
		{
			for(int i = 0; i < maht.sails.size(); i++)
			{
				Sail s = maht.sails.get(i);
				if(signBlock.equals(s.signBlock))
				{
					maker.sendMessage(ChatColor.GREEN + "[iSail] Using exists sail");
					return s;
				}
			}
			return null;
		}
		/*public void tryCreateMaht(Block mahtBlock, Player p, MahtType type)
		{
			boolean copy = false;
			Block b = getMahtBlockFromSign(b);
			for(int i = 0; i < ships.size(); i ++)
			{
				Ship s = ships.get(i);
				if(s.mahts != null) for(int m = 0; m < s.mahts.size(); m++)
				{
					Maht maht = s.mahts.get(m);
					if(maht.b.getX() == b.getX() && maht.b.getZ() == b.getZ()) copy = true;
				}
			}
			if(!copy) 
			{
				if(isMahtableBlock(b))
				{
					for(int s = 0; s < ships.size(); s++)
					{
						Ship ship = ships.get(s);
						if(ship.ecipage.contains(p))
						{
							Maht maht = new Maht(ship, b, type);
							ship.mahts.add(maht);
							p.sendMessage(ChatColor.GREEN + "[iSail] Succefully added new mast!");
							break;
						}
					}
				}
				else p.sendMessage(ChatColor.RED + "[iSail] Invalid blocks!");
			}
			else p.sendMessage(ChatColor.RED + "[iSail] Mast already exists!");
		}*/
		public Ship getShipFromPlayer(Player p)
		{
			if(ships != null) for(int i = 0; i < ships.size(); i++)
			{
				Ship ship = ships.get(i);
				if(ship.ecipage.contains(p)) return ship;
			}
			return null;
		}
		public Ship getShip(String name)
		{
			for(int i = 0; i < ships.size(); i++)
			{
				Ship ship = ships.get(i);
				if(ship.name.equals(name)) return ship;
			}
			return null;
		}
		public Ship getShip(int seed)
		{
			for(int i = 0; i < ships.size(); i++)
			{
				Ship ship = ships.get(i);
				if(ship.seed == seed) return ship;
			}
			return null;
		}
		public int getDegrees(byte data)
		{
			switch(data)
			{
				case 2:
				{
					return 180;
				}
				case 3:
				{
					return 0;
				}
				case 4:
				{
					return 90;
				}
				case 5:
				{
					return 270;
				}
			}
			return 0;
		}
		public boolean isConnectableToMaht(SailType sType, MahtType mType)
		{
			if(mType.equals(MahtType.GROT)) return true;
			else if(sType.equals(SailType.SQUARE)) return true;
			else if(sType.equals(SailType.FOREANDAFT) && !mType.equals(MahtType.BIZAN)) return true;
			else if(sType.equals(SailType.TRIANGLE) && mType.equals(MahtType.FOK)) return true;
			else if(sType.equals(SailType.REAR) && mType.equals(MahtType.BIZAN)) return true;
			return false;
		}
		public Sail getMakingSail(Player p)
		{
			if(cs.containsKey(p.getName())) return cs.get(p.getName());
			return null;
		}
		public boolean isMakerOfSail(Player p)
		{
			return cs.containsKey(p.getName());
		}
		public double sqrt(int i)
		{
			return Math.sqrt(Math.abs(i))*i/Math.abs(i);
		}
		public double sqrt(double i)
		{
			return Math.sqrt(Math.abs(i))*i/Math.abs(i);
		}
}