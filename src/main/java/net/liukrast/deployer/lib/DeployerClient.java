package net.liukrast.deployer.lib;

import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = DeployerConstants.MOD_ID, dist = Dist.CLIENT)
public class DeployerClient {
    public DeployerClient(IEventBus eventBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, DeployerConfig.Client.SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) -> new BaseConfigScreen(parent, modContainer.getModId()));
    }
}
