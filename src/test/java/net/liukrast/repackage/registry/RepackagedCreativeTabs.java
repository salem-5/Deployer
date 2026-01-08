package net.liukrast.repackage.registry;

import net.liukrast.deployer.lib.helper.MinecraftHelper;
import net.liukrast.repackage.RepackagedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RepackagedCreativeTabs {
    private RepackagedCreativeTabs() {}

    private static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, RepackagedConstants.MOD_ID);

    static {
        REGISTER.register("main_tab", () -> MinecraftHelper.createMainTab(RepackagedConstants.MOD_ID, RepackagedItems.STANDARD_BOTTLES.getFirst().toStack())
                .displayItems((pars, out) -> {
                    for(var entry : RepackagedBlocks.ITEMS.getEntries()) {
                        out.accept(entry.get());
                    }
                    for(var entry : RepackagedItems.REGISTER.getEntries()) {
                        out.accept(entry.get());
                    }
                    RepackagedConstants.getElements(BuiltInRegistries.FLUID).forEach(f -> out.accept(f.getBucket()));
                })
                .build());
    }

    public static void init(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
