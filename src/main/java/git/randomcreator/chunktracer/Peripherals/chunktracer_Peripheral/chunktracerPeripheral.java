package git.randomcreator.chunktracer.Peripherals.chunktracer_Peripheral;

import com.ibm.icu.impl.UResource;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

/**
 * Our peripheral class, this is the class where we will register functions for our block.
 */
public class chunktracerPeripheral implements IPeripheral {
    @SuppressWarnings("removal")
    ResourceLocation ticketId = new ResourceLocation("chunktracer", "block_chunkloader");
    /**
     * A list of all our connected computers. We need this for event usages.
     */
    private final List<IComputerAccess> connectedComputers = new ArrayList<>();

    /**
     * This is our tile entity, we set the tile entity when we create a new peripheral. We use this tile entity to access the block or the world
     */
    private final chunktracerTileEntity tileEntity;


    /**
     * @param tileEntity the tile entity of this peripheral
     */
    public chunktracerPeripheral(chunktracerTileEntity tileEntity) {
        this.tileEntity = tileEntity;
    }

    /**
     * We use getType to set the name for our peripheral. A modem would wrap our block as "test_n"
     *
     * @return the name of our peripheral
     */
    @Nonnull
    @Override
    public String getType() {
        return "test";
    }

    /**
     * CC use this method to check, if the peripheral in front of the modem is our peripheral
     */
    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return this == iPeripheral;
    }

    /**
     * Will be called when a computer disconnects from our block
     */
    @Override
    public void detach(@Nonnull IComputerAccess computer) {
        connectedComputers.remove(computer);
    }

    /**
     * Will be called when a computer connects to our block
     */
    @Override
    public void attach(@Nonnull IComputerAccess computer) {
        connectedComputers.add(computer);
    }

    public chunktracerTileEntity getTileEntity() {
        return tileEntity;
    }

    public final String GetBlockForPeripherals(Level world, BlockPos blockpos) {
        BlockState BlockStateAtPos = world.getBlockState(blockpos);
        Block BlockAtPos = BlockStateAtPos.getBlock();
        String BlockName = BuiltInRegistries.BLOCK.getKey(BlockAtPos).toString();
        return BlockName;
    }


    /**
     * Because we want to access the world, we need to run this function on the main thread.
     */
    @LuaFunction(mainThread = true)
    public final boolean isRaining() {
        return getTileEntity().getLevel().getRainLevel(0) > 0;
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getBlockAtPos(int x, int y, int z) throws LuaException {
        Level world = getTileEntity().getLevel();
        BlockPos BlockCheck = new BlockPos(x, y, z);
        LevelChunk Chunk = world.getChunkAt(BlockCheck);
        if (world == null) throw new LuaException("Nuh uh Try again later (This Means I lowk trash at coding)");
        String BlockName = GetBlockForPeripherals(world, BlockCheck);
        Map<String, Object> BlockInfo = new HashMap<>();
        BlockInfo.put("name", BlockName);
        BlockInfo.put("tags", world.getBlockState(BlockCheck).getTags());
        return BlockInfo;
    }

    @LuaFunction(mainThread = true)
    public final List<Map<String, Object>> getBlocksInRect(int start_x, int start_y, int start_z, int end_x, int end_y, int end_z) throws LuaException {
        Level world = getTileEntity().getLevel();
        int x_distance = Math.abs(start_x - end_x);
        int y_distance = Math.abs(start_y - end_y);
        int z_distance = Math.abs(start_z - end_z);
        Set<ChunkPos> loadedChunks = new HashSet<>();
        List<Map<String, Object>> result = new ArrayList<>();
        if (x_distance * y_distance * z_distance > 35000) throw new LuaException("The total area can not be more than 35000 blocks");
        for (int x = 0; x <= x_distance; x++) {
            for (int y = 0; y <= y_distance; y++) {
                for (int z = 0; z <= z_distance; z++) {

                    BlockPos pos = new BlockPos(start_x + x, start_y + y, start_z + z);
                    int chunkX = Math.floorDiv(start_x + x, 16);
                    int chunkZ = Math.floorDiv(start_z + z, 16);
                    ChunkPos chunkChecked = new ChunkPos(chunkX, chunkZ);
                    if (!loadedChunks.contains(chunkChecked)) {
                        world.getChunkAt(pos);
                        loadedChunks.add(chunkChecked);
                    };
                    String blockName = GetBlockForPeripherals(world, pos);
                    if (!blockName.equals("minecraft:air") && !blockName.equals("minecraft:void_air") && !blockName.equals("minecraft:cave_air")) {
                        // pos table
                        Map<String, Integer> coords = new HashMap<>();
                        coords.put("x", pos.getX());
                        coords.put("y", pos.getY());
                        coords.put("z", pos.getZ());

                        // block entry
                        Map<String, Object> blockEntry = new HashMap<>();
                        blockEntry.put("name", blockName);
                        blockEntry.put("pos", coords);
                        blockEntry.put("tags", world.getBlockState(pos).getTags());

                        result.add(blockEntry);
                        break;
                    }
                }
            }
        }

        return result;
    }

    @LuaFunction(mainThread = true)
    public final List<Map<String, Object>> getBlocksOnSurfaceInChunk(int x, int z) throws LuaException {
        Level world = getTileEntity().getLevel();
        if (world == null) throw new LuaException("Nuh uh Try again later (This Means I lowk trash at coding)");
        List<Map<String, Object>> result = new ArrayList<>();
        BlockPos BlockGiven = new BlockPos(x, 90, z);
        LevelChunk ChunkFound = world.getChunkAt(BlockGiven);
        int chunkx = ChunkFound.getPos().getMinBlockX();
        int chunkz = ChunkFound.getPos().getMinBlockZ();
        for (int xA = 0; xA < 16; xA++) {
            for (int zA = 0; zA < 16; zA++) {
                for (int yA = world.getMaxBuildHeight() - 1; yA >= world.getMinBuildHeight() - 1; yA--) {

                    BlockPos pos = new BlockPos(xA + chunkx, yA, zA + chunkz);
                    String blockName = GetBlockForPeripherals(world, pos);

                    if (!blockName.equals("minecraft:air") && !blockName.equals("minecraft:void_air") && !blockName.equals("minecraft:cave_air")) {
                            // pos table
                            Map<String, Integer> coords = new HashMap<>();
                            coords.put("x", pos.getX());
                            coords.put("y", pos.getY());
                            coords.put("z", pos.getZ());

                            // block entry
                            Map<String, Object> blockEntry = new HashMap<>();
                            blockEntry.put("name", blockName);
                            blockEntry.put("pos", coords);
                            blockEntry.put("tags", world.getBlockState(pos).getTags());

                            result.add(blockEntry);
                            break;
                    }
                }
            }
        }
        return result;
    }

    @LuaFunction(mainThread = true)
    public List<Map<String, Object>> getCubeFromRadius(int x, int y, int z, int radius) throws LuaException {
        Level world = getTileEntity().getLevel();
        List<Map<String, Object>> result = new ArrayList<>();
        if (radius < 0 || radius > 30) throw new LuaException("Radius has a max of 30 and a min of 1.");
        BlockPos center = new BlockPos(x, y, z);
        Set<ChunkPos> loadedChunks = new HashSet<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    int chunkX = Math.floorDiv(dx, 16);
                    int chunkZ = Math.floorDiv(dz, 16);
                    ChunkPos chunkChecked = new ChunkPos(chunkX, chunkZ);
                    if (!loadedChunks.contains(chunkChecked)) {
                        world.getChunkAt(pos);
                        loadedChunks.add(chunkChecked);
                    };
                    String blockName = GetBlockForPeripherals(world, pos);
                    if (!blockName.equals("minecraft:air") && !blockName.equals("minecraft:void_air") && !blockName.equals("minecraft:cave_air")) {
                        // pos table
                        Map<String, Integer> coords = new HashMap<>();
                        coords.put("x", pos.getX());
                        coords.put("y", pos.getY());
                        coords.put("z", pos.getZ());

                        // block entry
                        Map<String, Object> blockEntry = new HashMap<>();
                        blockEntry.put("name", blockName);
                        blockEntry.put("pos", coords);
                        blockEntry.put("tags", world.getBlockState(pos).getTags());

                        result.add(blockEntry);
                        break;
                    }
                }
            }
        }
        return result;
    }

    @LuaFunction(mainThread = true)
    public final List<Map<String, Object>> getSphereFromRadius(int centerX, int centerY, int centerZ, int radius) throws LuaException {
        Level world = getTileEntity().getLevel();
        if (world == null) throw new LuaException("World not loaded");

        List<Map<String, Object>> blocks = new ArrayList<>();
        int radiusSq = radius * radius;

        Set<ChunkPos> loadedChunks = new HashSet<>();

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    int dx = x - centerX;
                    int dy = y - centerY;
                    int dz = z - centerZ;


                    if (dx * dx + dy * dy + dz * dz <= radiusSq) {
                        BlockPos pos = new BlockPos(x, y, z);
                        int chunkX = Math.floorDiv(dx, 16);
                        int chunkZ = Math.floorDiv(dz + z, 16);
                        ChunkPos chunkChecked = new ChunkPos(chunkX, chunkZ);
                        if (!loadedChunks.contains(chunkChecked)) {
                            world.getChunkAt(pos);
                            loadedChunks.add(chunkChecked);
                        };
                        BlockState state = world.getBlockState(pos);

                        if (!state.isAir()) {
                            Map<String, Object> blockData = new HashMap<>();
                            blockData.put("x", x);
                            blockData.put("y", y);
                            blockData.put("z", z);
                            blockData.put("name", state.getBlock().getName().getString());
                            blockData.put("tags", state.getTags());
                            blocks.add(blockData);
                        }
                    }
                }
            }
        }

        return blocks;
    }

}
