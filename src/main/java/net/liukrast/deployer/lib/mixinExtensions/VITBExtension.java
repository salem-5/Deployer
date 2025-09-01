package net.liukrast.deployer.lib.mixinExtensions;

public interface VITBExtension {
    boolean deployer$stillWaiting(Object wrapper);
    void deployer$awaitNewVersion(Object wrapper);
}
