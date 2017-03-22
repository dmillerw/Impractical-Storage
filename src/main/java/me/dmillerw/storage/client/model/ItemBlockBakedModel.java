package me.dmillerw.storage.client.model;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import me.dmillerw.storage.block.BlockItemBlock;
import me.dmillerw.storage.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by dmillerw
 */
public class ItemBlockBakedModel implements IBakedModel {

    private static final List<BakedQuad> EMPTY_LIST = Lists.newArrayList();

    private static BlockRendererDispatcher rendererDispatcher() {
        return Minecraft.getMinecraft().getBlockRendererDispatcher();
    }

    private VertexFormat format;
    private TextureAtlasSprite wood;

    public ItemBlockBakedModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        this.format = format;
        this.wood = bakedTextureGetter.apply(new ResourceLocation("quadrum:blocks/crate_side"));
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;

        boolean isBlock = extendedBlockState.getValue(BlockItemBlock.IS_BLOCK);
        String renderValue = extendedBlockState.getValue(BlockItemBlock.RENDER_VALUE);
        int renderValueMeta = extendedBlockState.getValue(BlockItemBlock.RENDER_VALUE_META).intValue();

        Block renderBlock;
        if (renderValue == null || renderValue.isEmpty()) {
            renderBlock = ModBlocks.crate;
        } else {
            final ItemStack itemStack = new ItemStack(Item.getByNameOrId(renderValue), 1, renderValueMeta);

            if (isBlock) {
                IBakedModel inventoryModel = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(itemStack);
                if (!inventoryModel.isGui3d()) {
                    renderBlock = ModBlocks.crate;
                } else {
                    renderBlock = Block.getBlockFromName(renderValue);
                }
            } else {
                renderBlock = ModBlocks.crate;
            }
        }

        if (!renderBlock.canRenderInLayer(renderBlock.getDefaultState(), MinecraftForgeClient.getRenderLayer()))
            return EMPTY_LIST;

        IBlockState renderState = renderBlock.getStateFromMeta(renderValueMeta);
        IBakedModel model = rendererDispatcher().getModelForState(renderState);

        try {
            return model.getQuads(renderState, side, rand);
        } catch (Exception ex) {
            return rendererDispatcher().getBlockModelShapes().getModelForState(ModBlocks.crate.getDefaultState()).getQuads(renderState, side, rand);
        }
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