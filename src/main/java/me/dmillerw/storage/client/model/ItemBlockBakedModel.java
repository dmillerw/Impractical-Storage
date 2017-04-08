package me.dmillerw.storage.client.model;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.dmillerw.storage.block.BlockItemBlock;
import me.dmillerw.storage.block.ModBlocks;
import me.dmillerw.storage.core.BlockOverrides;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * Created by dmillerw
 */
public class ItemBlockBakedModel implements IBakedModel {

    private static final List<BakedQuad> EMPTY_LIST = Lists.newArrayList();

    private static BlockRendererDispatcher rendererDispatcher() {
        return Minecraft.getMinecraft().getBlockRendererDispatcher();
    }

    private static RenderItem renderItem() {
        return Minecraft.getMinecraft().getRenderItem();
    }

    private Set<String> renderBlacklist = Sets.newHashSet();

    private VertexFormat format;
    private TextureAtlasSprite wood;

    public ItemBlockBakedModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        this.format = format;
        this.wood = bakedTextureGetter.apply(new ResourceLocation("quadrum:blocks/crate_side"));
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quads = Lists.newArrayList();

        try {
            quads = safeGetQuads(state, side, rand);
        } catch (Exception ex) {
            IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
            renderBlacklist.add(extendedBlockState.getValue(BlockItemBlock.ITEM).getItem().getRegistryName().toString());

            IBlockState s = ModBlocks.crate.getDefaultState();
            quads = rendererDispatcher().getModelForState(s).getQuads(s, side, rand);
        }

        return quads;
    }

    private List<BakedQuad> safeGetQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;

        ItemStack itemStack = extendedBlockState.getValue(BlockItemBlock.ITEM);

        List<BakedQuad> quads = Lists.newArrayList();

        Block renderBlock;
        int renderMeta = 0;
        if (itemStack.isEmpty() || renderBlacklist.contains(itemStack.getItem().getRegistryName().toString())) {
            renderBlock = ModBlocks.crate;
            renderMeta = 0;
        } else {
            if (BlockOverrides.shouldTreatAsItem(itemStack.getItem())) {
                renderBlock = ModBlocks.crate;
                renderMeta = 0;

                IBakedModel model = renderItem().getItemModelMesher().getItemModel(itemStack);
                TextureAtlasSprite texture = model.getParticleTexture();
                if (texture == null)
                    texture = rendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel().getParticleTexture();

                BlockPartFace blockPartFace = new BlockPartFace(side, 0, texture.toString(), new BlockFaceUV(new float[]{0, 0, 16, 16}, 0));
                BlockPartRotation blockPartRotation = new BlockPartRotation(new Vector3f(0, 0, 0), EnumFacing.Axis.X, 0, false);

                final float shrink = 2.5F;

                if (MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.CUTOUT) {
                    if (side != null) {
                        if (side != EnumFacing.UP && side != EnumFacing.DOWN) {
                            final float minX = side == EnumFacing.EAST || side == EnumFacing.WEST ? -0.005F : shrink;
                            final float maxX = side == EnumFacing.EAST || side == EnumFacing.WEST ? 16.005f : 16 - shrink;
                            final float minZ = side == EnumFacing.EAST || side == EnumFacing.WEST ? shrink : -0.005F;
                            final float maxZ = side == EnumFacing.EAST || side == EnumFacing.WEST ? 16 - shrink : 16.005F;

                            BakedQuad itemQuad = new FaceBakery().makeBakedQuad(new Vector3f(minX, shrink, minZ), new Vector3f(maxX, 16 - shrink, maxZ), blockPartFace, model.getParticleTexture(), side, ModelRotation.X0_Y0, blockPartRotation, true, true);
                            quads.add(itemQuad);
                        } else if (side == EnumFacing.UP) {
                            final float minX = shrink;
                            final float maxX = 16 - shrink;
                            final float minZ = shrink;
                            final float maxZ = 16 - shrink;

                            BakedQuad itemQuad = new FaceBakery().makeBakedQuad(new Vector3f(minX, 16.005F, minZ), new Vector3f(maxX, 16.005F, maxZ), blockPartFace, model.getParticleTexture(), side, ModelRotation.X0_Y0, blockPartRotation, true, true);
                            quads.add(itemQuad);
                        }
                    }
                }
            } else {
                renderBlock = Block.getBlockFromItem(itemStack.getItem());
                if (renderBlock == null || renderBlock == Blocks.AIR) renderBlock = ModBlocks.crate;
            }
        }

        if (!renderBlock.canRenderInLayer(renderBlock.getDefaultState(), MinecraftForgeClient.getRenderLayer()))
            return quads;

        IBlockState renderState = renderBlock.getStateFromMeta(renderMeta);
        IBakedModel model = rendererDispatcher().getModelForState(renderState);

        quads.addAll(model.getQuads(renderState, side, rand));

        return quads;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return wood;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}
