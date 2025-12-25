package net.liukrast.fluid.content;

import net.liukrast.deployer.lib.logistics.packager.AbstractInventorySummary;
import net.liukrast.fluid.registry.RegisterStockInventoryTypes;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.Supplier;

public class FluidInventorySummary extends AbstractInventorySummary<Fluid, FluidStack> {

    public static final Supplier<FluidInventorySummary> EMPTY = FluidInventorySummary::new;

    public FluidInventorySummary() {
        super(RegisterStockInventoryTypes.FLUID.get());
    }
}
