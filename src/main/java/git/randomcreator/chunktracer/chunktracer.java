package git.randomcreator.chunktracer;

import com.google.common.base.Supplier;
import com.google.common.collect.Sets;
import git.randomcreator.chunktracer.Peripherals.chunktracer_Peripheral.chunktracerTileEntity;
import git.randomcreator.chunktracer.Peripherals.chunktracer_Peripheral.chunktracer_block;
import git.randomcreator.chunktracer.registry.BlockEntityRegistry;
import git.randomcreator.chunktracer.registry.BlockRegistry;
//import git.randomcreator.chunktracer.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(chunktracer.MODID)
public class chunktracer {
    public static final String MODID = "chunktracer";

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, chunktracer.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, chunktracer.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, chunktracer.MODID);

    public static final RegistryObject<Block> chunktracer_block = register("chunktracer_block", chunktracer_block::new);

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        RegistryObject<T> registryObject = BLOCKS.register(name, block);
        ITEMS.register(name, () -> new BlockItem(registryObject.get(), new Item.Properties().stacksTo(5)));
        return registryObject;
    }
    @SuppressWarnings("removal")
    private void buildCreativeTabs(BuildCreativeModeTabContentsEvent event){
        if(event.getTabKey()== ResourceKey.create(Registries.CREATIVE_MODE_TAB,new ResourceLocation("computercraft","tab"))){
            event.accept(chunktracer_block);
        }
    }

    public static final RegistryObject<BlockEntityType<chunktracerTileEntity>> chunktracer_TileEntity = BLOCK_ENTITIES.register("chunktracer_tileentity", () -> new BlockEntityType<>(chunktracerTileEntity::new, Sets.newHashSet(chunktracer_block.get()), null));
    public chunktracer() {
        @SuppressWarnings("removal")
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        /*ItemRegistry.ITEMS.register(bus);
        BlockRegistry.BLOCKS.register(bus);
        BlockEntityRegistry.BLOCK_ENTITIES.register(bus);
        */
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
    }

}
