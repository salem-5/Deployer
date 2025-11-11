package net.liukrast.fluid;

import com.simibubi.create.AllBlockEntityTypes;
import net.liukrast.fluid.registry.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@Mod(FluidConstants.MOD_ID)
public class Fluid {
    public Fluid(IEventBus eventBus) {
        RegisterBlockEntityTypes.register(eventBus);
        RegisterBlocks.register(eventBus);
        RegisterDataComponents.register(eventBus);
        RegisterItems.register(eventBus);
        RegisterStockInventoryTypes.register(eventBus);
        eventBus.register(this);
        RegisterPackageStyles.init();
    }

    @SubscribeEvent
    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        RegisterBlockEntityTypes.registerRenderers(event);
    }

    @SubscribeEvent
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                RegisterBlockEntityTypes.FLUID_PACKAGER.get(),
                (be, context) -> be.inventory
        );
    }
}
