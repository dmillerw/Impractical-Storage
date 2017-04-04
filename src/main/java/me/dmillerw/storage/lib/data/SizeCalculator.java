package me.dmillerw.storage.lib.data;

import me.dmillerw.storage.block.ModBlocks;
import me.dmillerw.storage.block.tile.TileController;
import net.minecraft.util.math.BlockPos;

/**
 * @author dmillerw
 */
public abstract class SizeCalculator {

    private static final boolean isBlockOccluded(TileController tile, int x, int y, int z) {
        return tile.worldOcclusionMap[y][x][z] || (!tile.getWorld().isAirBlock(tile.origin.add(x, y, z)) && tile.getWorld().getBlockState(new BlockPos(x, y, z)) != ModBlocks.item_block);
    }

    public static final SizeCalculator DEFAULT = new SizeCalculator() {

        @Override
        public void calculate(TileController tile) {
            int occludedSpots = 0;
            for (int y = 0; y < tile.height; y++) {
                for (int z = 0; z < tile.zLength; z++) {
                    for (int x = 0; x < tile.xLength; x++) {
                        if (SizeCalculator.isBlockOccluded(tile, x, y, z)) {
                            tile.worldOcclusionMap[y][x][z] = true;
                            occludedSpots++;
                        }
                    }
                }
            }

            tile.totalSize = (tile.height * tile.xLength * tile.zLength) - occludedSpots;
        }
    };

    public static final SizeCalculator PYRAMID = new SizeCalculator() {

        @Override
        public void calculate(TileController tile) {
            int size = 0;
            int occludedSpots = 0;
            for (int y = 0; y < tile.height; y++) {
                for (int x = y; x < tile.xLength - y; x++) {
                    for (int z = y; z < tile.zLength - y; z++) {
                        if (SizeCalculator.isBlockOccluded(tile, x, y, z)) {
                            tile.worldOcclusionMap[y][x][z] = true;
                            occludedSpots++;
                        } else {
                            size++;
                        }
                    }
                }
            }

            tile.totalSize = size;
        }
    };

    public void calculate(TileController tile) {

    }
}
