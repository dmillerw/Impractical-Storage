package me.dmillerw.storage.client.model;

import com.google.common.collect.Lists;
import me.dmillerw.storage.block.BlockCrate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author dmillerw
 */
public class CrateModel implements IBakedModel {

    private static BlockRendererDispatcher rendererDispatcher() {
        return Minecraft.getMinecraft().getBlockRendererDispatcher();
    }

    private static RenderItem renderItem() {
        return Minecraft.getMinecraft().getRenderItem();
    }

    private static ModelManager modelManager() {
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quads = Lists.newArrayList();

        ModelResourceLocation path = new ModelResourceLocation("impstorage:crate", "variant=" + state.getValue(BlockCrate.VARIANT).getName());
        IBakedModel base = modelManager().getModel(path);
        quads.addAll(base.getQuads(state, side, rand));

        ItemStack item = ((IExtendedBlockState) state).getValue(BlockCrate.ITEM);
        if (item == null || item.isEmpty())
            return quads;

        IBakedModel model = renderItem().getItemModelMesher().getItemModel(item);
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
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("impstorage:blocks/crate_wood");
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
