package git.randomcreator.chunktracer.Peripherals.chunktracer_Peripheral;

import git.randomcreator.chunktracer.chunktracer;
import git.randomcreator.chunktracer.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * This is our block. To tell minecraft that this block has a block entity, we need to implement {@link EntityBlock}
 */
public class chunktracer_block extends Block implements EntityBlock {

    public chunktracer_block() {
        super(Properties.of().strength(5, 5));
    }

    /**
     * This is the method from {@link EntityBlock} to create a new block entity for our block
     *
     * @return A new block entity from our registry object
     */
    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return chunktracer.chunktracer_TileEntity.get().create(pos, state);
    }
}
