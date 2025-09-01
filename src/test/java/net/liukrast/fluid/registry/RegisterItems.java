package net.liukrast.fluid.registry;

import net.liukrast.deployer.lib.logistics.packager.GenericPackageItem;
import net.liukrast.fluid.FluidConstants;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class RegisterItems {
    private RegisterItems() {}

    public static final DeferredRegister.Items ITEMS = DeferredRegister.Items.createItems(FluidConstants.MOD_ID);

    public static final List<DeferredItem<GenericPackageItem>> BOTTLES;

    static {
        BOTTLES = RegisterPackageStyles.STYLES.stream()
                .map(style -> ITEMS.register(style.getItemId().getPath(), () -> new GenericPackageItem(new Item.Properties().stacksTo(1), style)))
                .toList();
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
