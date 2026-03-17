package team.bytephoria.byteclans.infrastructure.bootstrap;

public interface PluginLifecycle {

    void load();

    void enable();

    void disable();

}
