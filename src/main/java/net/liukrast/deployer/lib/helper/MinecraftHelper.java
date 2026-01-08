package net.liukrast.deployer.lib.helper;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

/**
 * Helper class for non-create-related stuff
 * */
public class MinecraftHelper {
    private MinecraftHelper() {}

    public static CreativeModeTab.Builder createMainTab(String modId, ItemStack icon) {
        return CreativeModeTab.builder()
                .title(Component.translatable("itemGroup." + modId))
                .icon(() -> icon);
    }

}
