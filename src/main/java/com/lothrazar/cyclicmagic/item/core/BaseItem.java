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
package com.lothrazar.cyclicmagic.item.core;

import java.util.List;
import com.lothrazar.cyclicmagic.util.UtilChat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BaseItem extends Item {

  protected String getTooltip() {
    return this.getUnlocalizedName() + ".tooltip";
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void addInformation(ItemStack stack, World player, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
    if (getTooltip() != null) {
      tooltip.add(UtilChat.lang(getTooltip()));
    }
    super.addInformation(stack, player, tooltip, advanced);
  }

  protected NonNullList<ItemStack> findAmmoList(EntityPlayer player, Item item) {
    NonNullList<ItemStack> found = NonNullList.create();
    for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
      ItemStack itemstack = player.inventory.getStackInSlot(i);
      if (itemstack.getItem() == item) {
        found.add(itemstack);
      }
    }
    return found;
  }

  protected ItemStack findAmmo(EntityPlayer player, Item item) {
    for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
      ItemStack itemstack = player.inventory.getStackInSlot(i);
      if (itemstack.getItem() == item) {
        return itemstack;
      }
    }
    return ItemStack.EMPTY;
  }
}
