package net.liukrast.fluid.registry;

import com.simibubi.create.content.logistics.filter.FilterItemStack;
import net.liukrast.deployer.lib.logistics.packager.StockInventoryType;
import net.liukrast.deployer.lib.logistics.packagerLink.GenericRequestPromise;
import net.liukrast.deployer.lib.registry.DeployerRegistries;
import net.liukrast.fluid.FluidConstants;
import net.liukrast.fluid.content.FluidInventorySummary;
import net.liukrast.fluid.content.FluidStockInventoryType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RegisterStockInventoryTypes {
    private RegisterStockInventoryTypes() {}

    public static final DeferredRegister<StockInventoryType<?,?,?>> STOCK_INVENTORY_TYPES = DeferredRegister.create(DeployerRegistries.STOCK_INVENTORY, FluidConstants.MOD_ID);

    public static final DeferredHolder<StockInventoryType<?,?,?>, StockInventoryType<Fluid, FluidStack, IFluidHandler>> FLUID = STOCK_INVENTORY_TYPES.register("fluid", FluidStockInventoryType::new);

    public static void register(IEventBus eventBus) {
        STOCK_INVENTORY_TYPES.register(eventBus);
    }
}
