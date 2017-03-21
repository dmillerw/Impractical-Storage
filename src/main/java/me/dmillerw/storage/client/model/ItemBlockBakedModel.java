package me.dmillerw.storage.client.model;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import me.dmillerw.storage.block.BlockItemBlock;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
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

    private final List<BakedQuad> GENERIC_CUBE_LIST = Lists.newArrayList();
    private final List<BakedQuad> ACTIVE_CUBE_LIST = Lists.newArrayList();

    private VertexFormat format;

    private TextureAtlasSprite wood;

    private void putVertex(UnpackedBakedQuad.Builder builder, Vec3d normal, Vec3d vertex, float u, float v, TextureAtlasSprite sprite) {
        for (int e = 0; e < format.getElementCount(); e++) {
            switch (format.getElement(e).getUsage()) {
                case POSITION:
                    builder.put(e, (float)vertex.xCoord, (float)vertex.yCoord, (float)vertex.zCoord, 1.0f);
                    break;
                case COLOR:
                    builder.put(e, 1.0f, 1.0f, 1.0f, 1.0f);
                    break;
                case UV:
                    if (format.getElement(e).getIndex() == 0) {
                        u = sprite.getInterpolatedU(u);
                        v = sprite.getInterpolatedV(v);
                        builder.put(e, u, v, 0f, 1f);
                        break;
                    }
                case NORMAL:
                    builder.put(e, (float) normal.xCoord, (float) normal.yCoord, (float) normal.zCoord, 0f);
                    break;
                default:
                    builder.put(e);
                    break;
            }
        }
    }

    private BakedQuad createQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, TextureAtlasSprite sprite) {
        return createQuad(v1, v2, v3, v4, sprite, 0);
    }

    private BakedQuad createQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, TextureAtlasSprite sprite, int rotateUV) {
        Vec3d normal = v1.subtract(v2).crossProduct(v3.subtract(v2));
        normal = normal.normalize().rotatePitch(180).rotateYaw(180);

        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
        builder.setTexture(sprite);

        switch (rotateUV) {
            case 3: {
                putVertex(builder, normal, v1, 16, 0, sprite);
                putVertex(builder, normal, v2, 16, 16, sprite);
                putVertex(builder, normal, v3, 0, 16, sprite);
                putVertex(builder, normal, v4, 0, 0, sprite);
                break;
            }
            case 2: {
                putVertex(builder, normal, v1, 16, 16, sprite);
                putVertex(builder, normal, v2, 0, 16, sprite);
                putVertex(builder, normal, v3, 0, 0, sprite);
                putVertex(builder, normal, v4, 16, 0, sprite);
                break;
            }
            case 1: {
                putVertex(builder, normal, v1, 0, 16, sprite);
                putVertex(builder, normal, v2, 0, 0, sprite);
                putVertex(builder, normal, v3, 16, 16, sprite);
                putVertex(builder, normal, v4, 16, 0, sprite);
                break;
            }
            default: {
                putVertex(builder, normal, v1, 0, 0, sprite);
                putVertex(builder, normal, v2, 0, 16, sprite);
                putVertex(builder, normal, v3, 16, 16, sprite);
                putVertex(builder, normal, v4, 16, 0, sprite);
                break;
            }
        }

        return builder.build();
    }

    private List<BakedQuad> getGenericCube() {
        if (GENERIC_CUBE_LIST.isEmpty()) {
            List<BakedQuad> list = Lists.newArrayList();

            list.add(createQuad(
                    new Vec3d(0, 0, 0),
                    new Vec3d(1, 0, 0),
                    new Vec3d(1, 0, 1),
                    new Vec3d(0, 0, 1),
                    wood
            ));

            list.add(createQuad(
                    new Vec3d(0, 1, 1),
                    new Vec3d(1, 1, 1),
                    new Vec3d(1, 1, 0),
                    new Vec3d(0, 1, 0),
                    wood
            ));

            list.add(createQuad(
                    new Vec3d(0, 1, 0),
                    new Vec3d(1, 1, 0),
                    new Vec3d(1, 0, 0),
                    new Vec3d(0, 0, 0),
                    wood
            ));

            list.add(createQuad(
                    new Vec3d(0, 0, 1),
                    new Vec3d(1, 0, 1),
                    new Vec3d(1, 1, 1),
                    new Vec3d(0, 1, 1),
                    wood
            ));

            list.add(createQuad(
                    new Vec3d(0, 0, 0),
                    new Vec3d(0, 0, 1),
                    new Vec3d(0, 1, 1),
                    new Vec3d(0, 1, 0),
                    wood
            ));

            list.add(createQuad(
                    new Vec3d(1, 1, 0),
                    new Vec3d(1, 1, 1),
                    new Vec3d(1, 0, 1),
                    new Vec3d(1, 0, 0),
                    wood
            ));

            GENERIC_CUBE_LIST.addAll(list);
        }
        return GENERIC_CUBE_LIST;
    }

    public ItemBlockBakedModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        this.format = format;
        this.wood = bakedTextureGetter.apply(new ResourceLocation("minecraft:blocks/planks_oak"));
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
        String block = extendedBlockState.getValue(BlockItemBlock.ITEM_BLOCK);
        if (block == null || block.isEmpty())
            return getGenericCube();

        Block block1 = Block.getBlockFromName(block);
        if (block1 == null)
            return getGenericCube();

        if (!block1.canRenderInLayer(block1.getDefaultState(), MinecraftForgeClient.getRenderLayer()))
            return EMPTY_LIST;

        IBlockState state1 = block1.getStateFromMeta(extendedBlockState.getValue(BlockItemBlock.ITEM_BLOCK_META).intValue());

        IBakedModel model = rendererDispatcher().getModelForState(state1);
        if (model == rendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel())
            return getGenericCube();

        return model.getQuads(state1, side, rand);
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