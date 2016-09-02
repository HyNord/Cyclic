package com.lothrazar.cyclicmagic.item;
import com.lothrazar.cyclicmagic.util.Const;
import com.lothrazar.cyclicmagic.util.UtilItem;
import com.lothrazar.cyclicmagic.util.UtilParticle;
import com.lothrazar.cyclicmagic.util.UtilSound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class ItemCharmFire extends BaseCharm {
  private static final int durability = 6;
  private static final int seconds = 6;
  public ItemCharmFire() {
    super(durability);
  }
  /**
   * Called each tick as long the item is on a player inventory. Uses by maps to
   * check if is on a player hand and update it's contents.
   */
  public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    if (entityIn instanceof EntityPlayer) {
      EntityPlayer living = (EntityPlayer) entityIn;
      if (living.isBurning() && !living.isPotionActive(MobEffects.FIRE_RESISTANCE)) { // do nothing if you already have
        living.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, seconds * Const.TICKS_PER_SEC, Const.Potions.I));
        UtilItem.damageItem(living, stack);
        UtilSound.playSound(living, worldIn.getSpawnPoint(), SoundEvents.BLOCK_FIRE_EXTINGUISH, living.getSoundCategory());
        UtilParticle.spawnParticle(worldIn, EnumParticleTypes.WATER_WAKE, living.getPosition());
        UtilParticle.spawnParticle(worldIn, EnumParticleTypes.WATER_WAKE, living.getPosition().up());
      }
    }
  }
}
