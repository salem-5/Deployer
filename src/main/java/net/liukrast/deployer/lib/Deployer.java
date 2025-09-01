package net.liukrast.deployer.lib;

import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.box.PackageStyles;
import net.liukrast.deployer.lib.registry.*;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@Mod(DeployerConstants.MOD_ID)
public class Deployer {

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DeployerConstants.MOD_ID);

    static {
        for(PackageStyles.PackageStyle style : DeployerPackages.STYLES) {
            ITEMS.register(style.getItemId().getPath(), () -> new PackageItem(new Item.Properties().stacksTo(1), style));
        }
    }

    public Deployer(IEventBus bus, ModContainer container) {
        bus.register(this);
        NeoForge.EVENT_BUS.addListener(this::loadLevel);

        DeployerPanelConnections.register(bus);
        DeployerPackets.register();
        ITEMS.register(bus);
        DeployerPartialModels.init();

        if(ModList.get().isLoaded("psic_compat")) DeployerConstants.PSIC_INSTALLED = true;
    }

    /* MOD BUS EVENTS */
    @SubscribeEvent
    private void newRegistry(NewRegistryEvent event) {
        DeployerRegistries.init(event);
    }

    /* NEO EVENTS */
    private void loadLevel(LevelEvent.Load event) {
        DeployerExtraPanelConnections.register();
    }
}
