package me.dmillerw.storage.network.packet;

import io.netty.buffer.ByteBuf;
import me.dmillerw.storage.block.BlockController;
import me.dmillerw.storage.block.tile.TileController;
import me.dmillerw.storage.lib.data.SortingType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author dmillerw
 */
public class PacketConfig implements IMessage {

    protected BlockPos destination;

    protected int x;
    protected int y;
    protected int z;

    protected SortingType sortingType;

    protected boolean showBounds;

    private boolean dimensions;
    private boolean sort;
    private boolean bounds;

    public PacketConfig() {
    }

    public PacketConfig(BlockPos destination) {
        this.destination = destination;
    }

    public void setBoundaryDimensions(int x, int y, int z) {
        this.dimensions = true;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setSortingType(SortingType sortingType) {
        this.sort = true;
        this.sortingType = sortingType;
    }

    public void setShowBounds(boolean showBounds) {
        this.bounds = true;
        this.showBounds = showBounds;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(destination.toLong());

        buf.writeBoolean(dimensions);
        if (dimensions) {
            buf.writeInt(x);
            buf.writeInt(y);
            buf.writeInt(z);
        }

        buf.writeBoolean(bounds);
        if (bounds)
            buf.writeBoolean(showBounds);

        buf.writeBoolean(sort);
        if (sort)
            buf.writeInt(sortingType.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        destination = BlockPos.fromLong(buf.readLong());

        if (dimensions = buf.readBoolean()) {
            x = buf.readInt();
            y = buf.readInt();
            z = buf.readInt();
        }

        if (bounds = buf.readBoolean())
            showBounds = buf.readBoolean();

        if (sort = buf.readBoolean())
            sortingType = SortingType.VALUES[buf.readInt()];
    }

    public static class Handler implements IMessageHandler<PacketConfig, IMessage> {

        @Override
        public IMessage onMessage(PacketConfig message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = ctx.getServerHandler().playerEntity.world;
                IBlockState state = world.getBlockState(message.destination);
                TileEntity tile = world.getTileEntity(message.destination);
                if (tile != null && tile instanceof TileController) {
                    TileController controller = (TileController) tile;

                    if (message.dimensions) {
                        controller.updateRawBounds(
                                state.getValue(BlockController.FACING),
                                message.x,
                                message.y,
                                message.z);
                    }

                    if (message.bounds)
                        controller.setShowBounds(message.showBounds);

                    if (message.sort)
                        controller.setSortingType(message.sortingType);

                    controller.markDirtyAndNotify();
                }
            });
            return null;
        }
    }
}
