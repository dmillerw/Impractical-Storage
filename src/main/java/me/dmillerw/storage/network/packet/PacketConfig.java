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

    protected int boundX;
    protected int boundY;
    protected int boundZ;

    protected int offsetX;
    protected int offsetY;
    protected int offsetZ;

    protected SortingType sortingType;

    private boolean dimensions;
    private boolean offset;
    private boolean sort;

    public PacketConfig() {
    }

    public PacketConfig(BlockPos destination) {
        this.destination = destination;
    }

    public void setBoundaryDimensions(int x, int y, int z) {
        this.dimensions = true;
        this.boundX = x;
        this.boundY = y;
        this.boundZ = z;
    }

    public void setOffsetDimension(int x, int y, int z) {
        this.offset = true;
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
    }

    public void setSortingType(SortingType sortingType) {
        this.sort = true;
        this.sortingType = sortingType;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(destination.toLong());

        buf.writeBoolean(dimensions);
        if (dimensions) {
            buf.writeInt(boundX);
            buf.writeInt(boundY);
            buf.writeInt(boundZ);
        }

        buf.writeBoolean(offset);
        if (offset) {
            buf.writeInt(offsetX);
            buf.writeInt(offsetY);
            buf.writeInt(offsetZ);
        }

        buf.writeBoolean(sort);
        if (sort)
            buf.writeInt(sortingType.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        destination = BlockPos.fromLong(buf.readLong());

        if (dimensions = buf.readBoolean()) {
            boundX = buf.readInt();
            boundY = buf.readInt();
            boundZ = buf.readInt();
        }

        if (offset = buf.readBoolean()) {
            offsetX = buf.readInt();
            offsetY = buf.readInt();
            offsetZ = buf.readInt();
        }

        if (sort = buf.readBoolean())
            sortingType = SortingType.VALUES[buf.readInt()];
    }

    public static class Handler implements IMessageHandler<PacketConfig, IMessage> {

        @Override
        public IMessage onMessage(PacketConfig message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = ctx.getServerHandler().playerEntity.getEntityWorld();
                IBlockState state = world.getBlockState(message.destination);
                TileEntity tile = world.getTileEntity(message.destination);
                if (tile != null && tile instanceof TileController) {
                    TileController controller = (TileController) tile;

                    if (message.dimensions) {
                        controller.updateRawBounds(
                                state.getValue(BlockController.FACING),
                                message.boundX,
                                message.boundY,
                                message.boundZ);
                    }

                    if (message.offset)
                        controller.updateOffset(
                                message.offsetX,
                                message.offsetY,
                                message.offsetZ);

                    if (message.sort)
                        controller.setSortingType(message.sortingType);

                    controller.markDirtyAndNotify();
                }
            });
            return null;
        }
    }
}
