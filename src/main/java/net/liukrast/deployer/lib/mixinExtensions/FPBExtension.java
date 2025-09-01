package net.liukrast.deployer.lib.mixinExtensions;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnection;
import net.minecraft.core.BlockPos;

import java.util.Map;

public interface FPBExtension {
    Map<BlockPos, FactoryPanelConnection> deployer$getExtra();
}
