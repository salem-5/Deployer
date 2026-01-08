package net.liukrast.deployer.lib.registry;

import com.simibubi.create.content.logistics.box.PackageStyles;

import java.util.List;

public class DeployerPackages {
    private DeployerPackages() {}

    public static final List<PackageStyles.PackageStyle> STYLES = List.of(
            rare("liukrast"),
            rare("swzo")
    );

    private static PackageStyles.PackageStyle rare(String name) {
        return new PackageStyles.PackageStyle("rare_" + name, 12, 10, 21f, true);
    }
}
