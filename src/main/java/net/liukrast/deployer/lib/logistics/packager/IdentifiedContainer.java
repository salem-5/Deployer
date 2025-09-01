package net.liukrast.deployer.lib.logistics.packager;

import com.simibubi.create.api.packager.InventoryIdentifier;
import org.jetbrains.annotations.Nullable;

public record IdentifiedContainer<H>(@Nullable InventoryIdentifier identifier, H handler) {
}
