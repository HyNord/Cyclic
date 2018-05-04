/*******************************************************************************
 * The MIT License (MIT)
 * 
 * Copyright (C) 2014-2018 Sam Bassett (aka Lothrazar)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.lothrazar.cyclicmagic.block.pump.energy;

import com.lothrazar.cyclicmagic.IHasRecipe;
import com.lothrazar.cyclicmagic.core.block.BlockBaseFacingOmni;
import com.lothrazar.cyclicmagic.core.registry.RecipeRegistry;
import com.lothrazar.cyclicmagic.gui.ForgeGuiHandler;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockEnergyPump extends BlockBaseFacingOmni implements ITileEntityProvider, IHasRecipe {

  public BlockEnergyPump() {
    super(Material.WOOD);
    this.setHardness(3F);
    this.setResistance(3F);
    this.setHarvestLevel("pickaxe", 1);
    this.setTranslucent();
    super.setGuiId(ForgeGuiHandler.GUI_INDEX_ENERGYPUMP);
    this.placeType = PlacementType.SIDE_BLOCK;
  }

  @Override
  public TileEntity createNewTileEntity(World worldIn, int meta) {
    return new TileEntityEnergyPump();
  }

  @Override
  public IRecipe addRecipe() {
    return RecipeRegistry.addShapedRecipe(new ItemStack(this),
        "i i",
        " r ",
        "ibi",
        'b', Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE,
        'i', "dustRedstone",
        'r', Blocks.DROPPER);
  }
}