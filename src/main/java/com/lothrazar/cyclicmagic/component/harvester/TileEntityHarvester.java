package com.lothrazar.cyclicmagic.component.harvester;
import java.util.ArrayList;
import java.util.List;
import com.lothrazar.cyclicmagic.block.base.TileEntityBaseMachineInvo;
import com.lothrazar.cyclicmagic.gui.ITilePreviewToggle;
import com.lothrazar.cyclicmagic.gui.ITileRedstoneToggle;
import com.lothrazar.cyclicmagic.gui.ITileSizeToggle;
import com.lothrazar.cyclicmagic.util.UtilHarvestCrops;
import com.lothrazar.cyclicmagic.util.UtilHarvestCrops.HarvestSetting;
import com.lothrazar.cyclicmagic.util.UtilInventoryTransfer;
import com.lothrazar.cyclicmagic.util.UtilItemStack;
import com.lothrazar.cyclicmagic.util.UtilParticle;
import com.lothrazar.cyclicmagic.util.UtilShape;
import com.lothrazar.cyclicmagic.util.UtilWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public class TileEntityHarvester extends TileEntityBaseMachineInvo implements ITileRedstoneToggle, ITileSizeToggle, ITilePreviewToggle, ITickable {
  private static final int MAX_SIZE = 7;//radius 7 translates to 15x15 area (center block + 7 each side)
  private int size = MAX_SIZE;//default to the old fixed size, backwards compat
  public final static int TIMER_FULL = 200;
  private static final int[] hopperInputFuel = { 27 };// all slots
  private static final int[] hopperOUTPUT = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,
      18, 19, 20, 21, 22, 23, 24, 25, 26 };// all slots
  public static enum Fields {
    TIMER, REDSTONE, SIZE, RENDERPARTICLES, FUEL, FUELMAX, HARVESTMODE;
  }
  private HarvestSetting conf;
  private int needsRedstone = 1;
  private int renderParticles = 0;
  private int normalModeIfZero = 0;//if this == 1, then do full field at once
  public TileEntityHarvester() {
    super(1 + 3 * 9);
    this.setFuelSlot(27);
    this.timer = TIMER_FULL;
    conf = new HarvestSetting();
    conf.doesCrops = true;
    conf.doesMushroom = true;
    conf.doesPumpkinBlocks = true;
    conf.doesMelonBlocks = true;
    conf.dropInPlace = false;
  }
  @Override
  public int[] getFieldOrdinals() {
    return super.getFieldArray(Fields.values().length);
  }
  public HarvestSetting getHarvestConf() {
    return conf;
  }
  @Override
  public void readFromNBT(NBTTagCompound tags) {
    super.readFromNBT(tags);
    this.size = tags.getInteger(NBT_SIZE);
    this.needsRedstone = tags.getInteger(NBT_REDST);
    this.renderParticles = tags.getInteger(NBT_RENDER);
    this.normalModeIfZero = tags.getInteger("HM");
  }
  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound tags) {
    tags.setInteger(NBT_REDST, this.needsRedstone);
    tags.setInteger(NBT_RENDER, renderParticles);
    tags.setInteger(NBT_SIZE, size);
    tags.setInteger("HM", normalModeIfZero);
    return super.writeToNBT(tags);
  }
  //  public boolean isFuelBurning() {
  //    return this.timer > 0 && this.timer < TIMER_FULL;
  //  }
  @Override
  public void update() {
    if (!isRunning()) { return; }
    this.spawnParticlesAbove();
    if (this.updateTimerIsZero()) {
      timer = TIMER_FULL;//harvest worked!
      this.spawnParticlesAbove();
      if (this.normalModeIfZero == 0) {
        tryHarvestSingle();
      }
      else {
        tryHarvestArea();
      }
    }
    else {
      this.updateFuelIsBurning();
    }
  }
  private void tryHarvestArea() {
    int success = UtilHarvestCrops.harvestArea(world, getTargetCenter(), this.size, conf);
  
    if (conf.drops != null) {
      setOutputItems(conf.drops);
    }
    //POTENTIAL to exploit here. if fuel is at say 2, and harvesting wants to pay cost of 18
    //well its too late so, 
    // then youre getting something for free
    //not terribly worried about it. after that it wont work again until you refuel which will be max
    this.consumeFuel(success * 10);//10 fuel per item instead of one
  }
  private void tryHarvestSingle() {
    conf.resetDrops();
    BlockPos harvest = getTargetPos();
    if (UtilHarvestCrops.harvestSingle(getWorld(), harvest, conf)) {
      this.updateFuelIsBurning();
      UtilParticle.spawnParticle(getWorld(), EnumParticleTypes.DRAGON_BREATH, harvest);
      if (conf.drops != null) {
        setOutputItems(conf.drops);
      }
    }
    else {
      timer = 1;//harvest didnt work, try again really quick
    }
  }
  private void setOutputItems(List<ItemStack> output) {
    ArrayList<ItemStack> toDrop = UtilInventoryTransfer.dumpToIInventory(output, this, 0, this.getSizeInventory());
    if (!toDrop.isEmpty()) {
      for (ItemStack s : toDrop) {
        UtilItemStack.dropItemStackInWorld(this.getWorld(), this.getPos().up(), s);
      }
    }
  }
  public BlockPos getTargetCenter() {
    //move center over that much, not including exact horizontal
    return this.getPos().offset(this.getCurrentFacing(), this.size + 1);
  }
  private BlockPos getTargetPos() {
    return UtilWorld.getRandomPos(getWorld().rand, getTargetCenter(), this.size);
  }
  @Override
  public int getField(int id) {
    switch (Fields.values()[id]) {
      case TIMER:
        return timer;
      case REDSTONE:
        return this.needsRedstone;
      case SIZE:
        return this.size;
      case RENDERPARTICLES:
        return this.renderParticles;
      case FUEL:
        return this.getFuelCurrent();
      case FUELMAX:
        return this.getFuelMax();
      case HARVESTMODE:
        return this.normalModeIfZero;
    }
    return -1;
  }
  @Override
  public void setField(int id, int value) {
    switch (Fields.values()[id]) {
      case TIMER:
        this.timer = value;
      break;
      case REDSTONE:
        this.needsRedstone = value;
      break;
      case SIZE:
        this.size = value;
      break;
      case RENDERPARTICLES:
        this.renderParticles = value % 2;
      break;
      case FUEL:
        this.setFuelCurrent(value);
      break;
      case FUELMAX:
        this.setFuelMax(value);
      break;
      case HARVESTMODE:
        this.normalModeIfZero = value % 2;
      break;
    }
  }
  @Override
  public int getFieldCount() {
    return Fields.values().length;
  }
  /**
   * facing DOWN means the hopper is facing down, so items are coming in through
   * the top side
   */
  @Override
  public int[] getSlotsForFace(EnumFacing side) {
    if (side == EnumFacing.DOWN)
      return hopperOUTPUT;
    return hopperInputFuel;
  }
  public void toggleSizeShape() {
    this.size++;
    if (this.size > MAX_SIZE) {
      this.size = 0;
    }
  }
  @Override
  public void toggleNeedsRedstone() {
    int val = this.needsRedstone + 1;
    if (val > 1) {
      val = 0;//hacky lazy way
    }
    this.setField(Fields.REDSTONE.ordinal(), val);
  }
  @Override
  public boolean onlyRunIfPowered() {
    return this.needsRedstone == 1;
  }
  @Override
  public void togglePreview() {
    this.renderParticles = (renderParticles + 1) % 2;
  }
  @Override
  public List<BlockPos> getShape() {
    return UtilShape.squareHorizontalHollow(getTargetCenter(), this.size);
  }
  @Override
  public boolean isPreviewVisible() {
    return this.getField(Fields.RENDERPARTICLES.ordinal()) == 1;
  }
}