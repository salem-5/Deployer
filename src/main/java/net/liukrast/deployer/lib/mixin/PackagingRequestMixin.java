package net.liukrast.deployer.lib.mixin;

import com.simibubi.create.content.logistics.packager.PackagingRequest;
import net.liukrast.deployer.lib.mixinExtensions.PRExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PackagingRequest.class)
public class PackagingRequestMixin implements PRExtension {
    @Unique
    private boolean deployer$flag = false;

    @Override
    public void deployer$flag() {
        deployer$flag = true;
    }

    @Override
    public boolean deployer$isFlagged() {
        return deployer$flag;
    }
}
