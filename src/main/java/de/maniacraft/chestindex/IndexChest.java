package de.maniacraft.chestindex;

public class IndexChest {
	public int x, y, z, id, amount;
	public String world, item;

	//
	public IndexChest(String world, int x, int y, int z, int amount, String item) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.amount = amount;
		this.item = item;
	}
}
