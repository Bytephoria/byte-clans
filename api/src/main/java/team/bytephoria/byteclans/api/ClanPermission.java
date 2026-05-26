package team.bytephoria.byteclans.api;

import org.jetbrains.annotations.NotNull;

public interface ClanPermission {

    @NotNull String namespace();

    @NotNull String key();

    default @NotNull String id() {
        return this.namespace() + ":" + this.key();
    }

}
