package com.lothrazar.cyclic.item.scythe;

import java.util.List;
import com.lothrazar.cyclic.base.ItemBase;
import com.lothrazar.cyclic.block.harvester.TileHarvester;
import com.lothrazar.cyclic.util.UtilShape;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class ScytheHarvest extends ItemBase {

  public ScytheHarvest(Properties properties) {
    super(properties);
  }

  private static final int RADIUS = 6;//13x13
  private static final int RADIUS_SNEAKING = 2;//2x2

  public List<BlockPos> getShape(BlockPos pos, int radius) {
    return UtilShape.squareHorizontalFull(pos, radius);
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    BlockPos pos = context.getPos();
    Direction side = context.getFace();
    if (side != null) {
      pos = pos.offset(side);
    }
    int radius = (context.getPlayer().isCrouching()) ? RADIUS_SNEAKING : RADIUS;
    //
    int counts = 0;
    for (BlockPos p : getShape(pos, radius)) {
      // 
      if (TileHarvester.tryHarvestSingle(context.getWorld(), p)) {
        counts++;
        //        ModCyclic.LOGGER.info("SUCCESS " + p);
      }
    }
    //
    //
    context.getPlayer().swingArm(context.getHand());
    context.getItem().damageItem(counts, context.getPlayer(), (e) -> {});
    return super.onItemUse(context);
  }
}
