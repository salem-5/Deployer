package net.liukrast.deployer.lib.logistics.packager;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * A simple interface that can be applied on {@link com.simibubi.create.content.logistics.box.PackageItem}.
 * */
public interface PackageProvidesCustomContent {
    /**
     * Defines your custom {@link ItemStackHandler} for this box instead of the default {@link com.simibubi.create.AllDataComponents#PACKAGE_CONTENTS}
     * @param box The box we want to get content from
     * @return The item stack handler for this box
     * */
    ItemStackHandler getCustomContents(ItemStack box);
}
