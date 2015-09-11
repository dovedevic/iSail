package iSail;

import org.bukkit.block.Block;

@SuppressWarnings("deprecation")
public class SailBlock extends iSail
{
	boolean wool;
	byte data;
	public SailBlock(Block b)
	{
		if(b.getTypeId() == 35) this.wool = true;
		this.data = b.getData();
	}
}