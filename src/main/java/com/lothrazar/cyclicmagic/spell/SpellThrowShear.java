package com.lothrazar.cyclicmagic.spell;

import com.lothrazar.cyclicmagic.projectile.EntityShearingBolt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class SpellThrowShear extends BaseSpellExp implements ISpell {

	private final int cooldown = 45;

	@Override
	public int getCastCooldown() {
		return cooldown;
	}

	@Override
	public void cast(World world, EntityPlayer player, BlockPos pos, EnumFacing side, Entity target) {

		world.spawnEntityInWorld(new EntityShearingBolt(world, player));
	}
}
