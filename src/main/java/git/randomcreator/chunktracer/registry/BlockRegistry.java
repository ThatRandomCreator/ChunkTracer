package git.randomcreator.chunktracer.registry;

import git.randomcreator.chunktracer.chunktracer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, chunktracer.MODID);

    //static final RegistryObject<Block> CHUNK_TRACER = BLOCKS.register("chunk_tracer",
            //()-> new Block(BlockBehaviour.Properties.of()
                    //.strength(5f, 5f)
                    //.mapColor(MapColor.COLOR_LIGHT_GREEN)
            //));
}
