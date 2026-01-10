package net.liukrast.deployer.lib.logistics.packager;

import net.minecraft.world.item.ItemStack;

/**
 * A simple interface that can be applied on {@link com.simibubi.create.content.logistics.box.PackageItem}.
 * */
public interface PackageCanBeHandled {
    /**
     * Invoked by packagers trying to unwrap this box
     * @param box The box to be unwrapped
     * @return true if you want packagers and saws to unwrap this box, false otherwise
     * */
    boolean canBeHandled(ItemStack box);
}
