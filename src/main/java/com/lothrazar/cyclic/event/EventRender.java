package com.lothrazar.cyclic.event;

import java.awt.Color;
import java.util.List;
import java.util.Random;
import com.lothrazar.cyclic.ModCyclic;
import com.lothrazar.cyclic.item.builder.BuildStyle;
import com.lothrazar.cyclic.item.builder.BuilderActionType;
import com.lothrazar.cyclic.item.builder.BuilderItem;
import com.lothrazar.cyclic.item.builder.PacketSwapBlock;
import com.lothrazar.cyclic.item.random.RandomizerItem;
import com.lothrazar.cyclic.render.FakeBlockRenderTypes;
import com.lothrazar.cyclic.util.UtilRender;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventRender {

  @OnlyIn(Dist.CLIENT)
  @SubscribeEvent
  public void renderOverlay(RenderWorldLastEvent evt) {
    PlayerEntity player = ModCyclic.proxy.getClientPlayer();
    if (player == null) {
      return;
    }
    ItemStack stack = BuilderItem.getIfHeld(player);
    if (stack.getItem() instanceof BuilderItem) {
      builderItemRender(evt, player, stack);
    }
    stack = RandomizerItem.getIfHeld(player);
    if (stack.getItem() instanceof RandomizerItem) {
      randomizerItemRender(evt, player, stack);
    }
  }

  private void randomizerItemRender(RenderWorldLastEvent evt, PlayerEntity player, ItemStack stack) {
    World world = player.world;
    final Minecraft mc = Minecraft.getInstance();
    IRenderTypeBuffer.Impl buffer = mc.getRenderTypeBuffers().getBufferSource();
    int range = 5;
    BlockRayTraceResult lookingAt = (BlockRayTraceResult) player.pick(range, 0F, false);
    if (world.getBlockState(lookingAt.getPos()) == Blocks.AIR.getDefaultState()) {
      return;
    }
    List<BlockPos> coords = RandomizerItem.getPlaces(lookingAt.getPos(), lookingAt.getFace());
    Vec3d view = mc.gameRenderer.getActiveRenderInfo().getProjectedView();
    MatrixStack matrix = evt.getMatrixStack();
    matrix.push();
    matrix.translate(-view.getX(), -view.getY(), -view.getZ());
    IVertexBuilder builder;
    builder = buffer.getBuffer(FakeBlockRenderTypes.TRANSPARENT_SOLID_COLOUR);
    for (BlockPos e : coords) {
      matrix.push();
      matrix.translate(e.getX(), e.getY(), e.getZ());
      matrix.translate(-0.005f, -0.005f, -0.005f);
      matrix.scale(1.01f, 1.01f, 1.01f);
      matrix.rotate(Vector3f.YP.rotationDegrees(-90.0F));
      Matrix4f positionMatrix = matrix.getLast().getMatrix();
      Color color = RandomizerItem.canMove(world.getBlockState(e), world, e) ? Color.GREEN : Color.RED;
      UtilRender.renderCube(positionMatrix, builder, e, color);
      matrix.pop();
    }
    matrix.pop();
    RenderSystem.disableDepthTest();
    buffer.finish(FakeBlockRenderTypes.TRANSPARENT_SOLID_COLOUR);
  }

  private void builderItemRender(RenderWorldLastEvent evt, PlayerEntity player, ItemStack stack) {
    World world = player.world;
    BuildStyle buildStyle = ((BuilderItem) stack.getItem()).style;
    BlockState renderBlockState = BuilderActionType.getBlockState(stack);
    if (renderBlockState == null) {
      return;
    }
    //render 
    Minecraft.getInstance().getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
    //
    double range = 6F;
    BlockRayTraceResult lookingAt = (BlockRayTraceResult) player.pick(range, 0F, false);
    if (world.isAirBlock(lookingAt.getPos())) {
      return;
    }
    BlockPos pos = lookingAt.getPos();
    if (buildStyle.isOffset() && lookingAt.getFace() != null) {
      //
      pos = pos.offset(lookingAt.getFace());
    }
    MatrixStack matrix = evt.getMatrixStack();
    Minecraft mc = Minecraft.getInstance();
    IRenderTypeBuffer.Impl buffer = mc.getRenderTypeBuffers().getBufferSource();
    IVertexBuilder builder = buffer.getBuffer(FakeBlockRenderTypes.FAKE_BLOCK);//i guess?
    BlockRendererDispatcher dispatcher = mc.getBlockRendererDispatcher();
    matrix.push();
    Vec3d playerPos = mc.gameRenderer.getActiveRenderInfo().getProjectedView();
    matrix.translate(-playerPos.getX(), -playerPos.getY(), -playerPos.getZ());
    //    ModCyclic.LOGGER.info(" lookingAt.getPos() " + lookingAt.getPos());
    List<BlockPos> coordinates = PacketSwapBlock.getSelectedBlocks(world, pos, BuilderItem.getActionType(stack), lookingAt.getFace(), buildStyle);
    ///
    //    builder.pos
    //    int[] RGB = new int[] { 75, 0, 130 };
    //    int r = RGB[0];
    //    int g = RGB[1];
    //    int b = RGB[2];
    //    BuilderItem.get
    for (BlockPos coordinate : coordinates) {
      float x = coordinate.getX();
      float y = coordinate.getY();
      float z = coordinate.getZ();
      //      ModCyclic.LOGGER.info("y value of build " + y);
      matrix.push();
      matrix.translate(x, y, z);
      //
      //shrink it up
      matrix.translate(-0.0005f, -0.0005f, -0.0005f);
      matrix.scale(1.001f, 1.001f, 1.001f);
      //
      //      UtilWorld.OutlineRenderer.renderHighLightedBlocksOutline(builder, x, y, z, r / 255.0f, g / 255.0f, b / 255.0f, 1.0f); // .02f
      IBakedModel ibakedmodel = dispatcher.getModelForState(renderBlockState);
      BlockColors blockColors = Minecraft.getInstance().getBlockColors();
      int color = blockColors.getColor(renderBlockState, player.world, coordinate, 0);
      float red = (color >> 16 & 255) / 255.0F;
      float green = (color >> 8 & 255) / 255.0F;
      float blue = (color & 255) / 255.0F;
      float alpha = 0.7F;
      try {
        if (renderBlockState.getRenderType() == BlockRenderType.MODEL) {
          for (Direction direction : Direction.values()) {
            UtilRender.renderModelBrightnessColorQuads(matrix.getLast(), builder, red, green, blue, alpha,
                ibakedmodel.getQuads(renderBlockState, direction, new Random(MathHelper.getPositionRandom(coordinate)), EmptyModelData.INSTANCE), 15728640, 655360 / 2);
          }
          UtilRender.renderModelBrightnessColorQuads(matrix.getLast(), builder, red, green, blue, alpha,
              ibakedmodel.getQuads(renderBlockState, null, new Random(MathHelper.getPositionRandom(coordinate)), EmptyModelData.INSTANCE),
              15728640, 655360);
        }
      }
      catch (Throwable t) {
        ModCyclic.LOGGER.trace("Block at {} with state {} threw exception, whilst rendering", coordinate, renderBlockState, t);
      }
      matrix.pop();
    }
    ///
    matrix.pop();
  }
}