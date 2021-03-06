package com.lothrazar.cyclic.registry;

import java.util.ArrayList;
import java.util.List;
import com.lothrazar.cyclic.ModCyclic;
import com.lothrazar.cyclic.potion.TickableEffect;
import com.lothrazar.cyclic.potion.effect.StunEffect;
import com.lothrazar.cyclic.potion.effect.SwimEffect;
import com.lothrazar.cyclic.recipe.ModBrewingRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PotionRegistry {

  @SubscribeEvent
  public static void onPotEffectRegistry(RegistryEvent.Register<Effect> event) {
    IForgeRegistry<Effect> r = event.getRegistry();
    PotionEffects.stun = register(r, new StunEffect(EffectType.HARMFUL, 0xcccc00), "stun");
    PotionEffects.swimspeed = register(r, new SwimEffect(EffectType.BENEFICIAL, 0x663300), "swimspeed");
  }

  private static TickableEffect register(IForgeRegistry<Effect> r, TickableEffect pot, String name) {
    pot.setRegistryName(new ResourceLocation(ModCyclic.MODID, name));
    r.register(pot);
    PotionEffects.effects.add(pot);
    return pot;
  }

  @SubscribeEvent
  public static void onPotRegistry(RegistryEvent.Register<Potion> event) {
    IForgeRegistry<Potion> r = event.getRegistry();
    //TODO: config options for potions?
    r.register(new Potion(ModCyclic.MODID + "_haste", new EffectInstance(Effects.HASTE, 3600)).setRegistryName(ModCyclic.MODID + ":haste"));
    r.register(new Potion(ModCyclic.MODID + "_strong_haste", new EffectInstance(Effects.HASTE, 1800, 1)).setRegistryName(ModCyclic.MODID + ":strong_haste"));
    r.register(new Potion(ModCyclic.MODID + "_stun", new EffectInstance(PotionEffects.stun, 1800)).setRegistryName(ModCyclic.MODID + ":stun"));
    r.register(new Potion(ModCyclic.MODID + "_swimspeed", new EffectInstance(PotionEffects.swimspeed, 3600)).setRegistryName(ModCyclic.MODID + ":swimspeed"));
    //    Effects.LEVITATION
    //    Effects.WITHER
    //    Effects.HUNGER 
    //    Effects.BLINDNESS
    //    Effects.DOLPHINS_GRACE 
    //    Effects.GLOWING 
  }

  public static class PotionEffects {

    //for events
    public static final List<TickableEffect> effects = new ArrayList<TickableEffect>();
    @ObjectHolder(ModCyclic.MODID + ":stun")
    public static TickableEffect stun;
    @ObjectHolder(ModCyclic.MODID + ":swimspeed")
    public static TickableEffect swimspeed;
  }

  public static class PotionItem {

    @ObjectHolder(ModCyclic.MODID + ":strong_haste")
    public static Potion strong_haste;
    @ObjectHolder(ModCyclic.MODID + ":haste")
    public static Potion haste;
    @ObjectHolder(ModCyclic.MODID + ":stun")
    public static Potion stun;
    @ObjectHolder(ModCyclic.MODID + ":swimspeed")
    public static Potion swimspeed;
  }

  public static void setup(FMLCommonSetupEvent event) {
    ///haste recipes
    ItemStack AWKWARD = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.AWKWARD);
    basicBrewing(AWKWARD.copy(), PotionRegistry.PotionItem.haste, Items.EMERALD);
    splashBrewing(PotionRegistry.PotionItem.haste, Items.EMERALD);
    basicBrewing(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), PotionItem.haste),
        PotionRegistry.PotionItem.strong_haste, Items.REDSTONE);
    splashBrewing(PotionRegistry.PotionItem.strong_haste, Items.EMERALD);
    lingerBrewing(PotionRegistry.PotionItem.haste, Items.EMERALD);
    //STUN recipes
    basicBrewing(AWKWARD.copy(), PotionRegistry.PotionItem.stun, Items.CLAY);
    splashBrewing(PotionRegistry.PotionItem.stun, Items.CLAY);
    lingerBrewing(PotionRegistry.PotionItem.stun, Items.CLAY);
    //swimspeed recipes
    basicBrewing(AWKWARD.copy(), PotionRegistry.PotionItem.swimspeed, Items.DRIED_KELP_BLOCK);
    splashBrewing(PotionRegistry.PotionItem.swimspeed, Items.DRIED_KELP_BLOCK);
    lingerBrewing(PotionRegistry.PotionItem.swimspeed, Items.DRIED_KELP_BLOCK);
    //    PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), PotionItem.haste).gr
  }

  private static void basicBrewing(ItemStack AWKWARD, Potion pot, Item item) {
    //hmm wat 
    BrewingRecipeRegistry.addRecipe(new ModBrewingRecipe(AWKWARD, Ingredient.fromItems(item),
        PotionUtils.addPotionToItemStack(
            new ItemStack(Items.POTION), pot)));
  }

  private static void splashBrewing(Potion pot, Item item) {
    BrewingRecipeRegistry.addRecipe(new ModBrewingRecipe(PotionUtils.addPotionToItemStack(
        new ItemStack(Items.SPLASH_POTION), Potions.AWKWARD),
        Ingredient.fromStacks(new ItemStack(item)),
        PotionUtils.addPotionToItemStack(
            new ItemStack(Items.SPLASH_POTION), pot)));
  }

  private static void lingerBrewing(Potion pot, Item item) {
    BrewingRecipeRegistry.addRecipe(new ModBrewingRecipe(PotionUtils.addPotionToItemStack(
        new ItemStack(Items.LINGERING_POTION), Potions.AWKWARD),
        Ingredient.fromStacks(new ItemStack(item)),
        PotionUtils.addPotionToItemStack(
            new ItemStack(Items.LINGERING_POTION), pot)));
  }
}
