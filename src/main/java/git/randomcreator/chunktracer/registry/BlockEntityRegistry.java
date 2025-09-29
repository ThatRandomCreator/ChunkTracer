package git.randomcreator.chunktracer.registry;

import com.google.common.collect.Sets;
import git.randomcreator.chunktracer.Peripherals.chunktracer_Peripheral.chunktracerTileEntity;
import git.randomcreator.chunktracer.chunktracer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import git.randomcreator.chunktracer.Peripherals.chunktracer_Peripheral.chunktracer_block;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, chunktracer.MODID);

    //public static final RegistryObject<BlockEntityType<chunktracerTileEntity>> chunktracer_TileEntity = BLOCK_ENTITIES.register("chunktracer_tileentity", () -> new BlockEntityType<>(chunktracerTileEntity::new, Sets.newHashSet(chunktracer_block.get()), null));
}
