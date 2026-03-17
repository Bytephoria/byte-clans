package team.bytephoria.byteclans.providers.storage.sql.config;

public final class JdbcPoolConfig {

    private final long connectionTimeoutMs;
    private final long maxLifeTimeMs;
    private final int maxPoolSize;
    private final int minIdle;

    public JdbcPoolConfig(
            final long connectionTimeoutMs,
            final long maxLifeTimeMs,
            final int maxPoolSize,
            final int minIdle
    ) {
        this.connectionTimeoutMs = connectionTimeoutMs;
        this.maxLifeTimeMs = maxLifeTimeMs;
        this.maxPoolSize = maxPoolSize;
        this.minIdle = minIdle;
    }

    public long connectionTimeoutMs() {
        return this.connectionTimeoutMs;
    }

    public long maxLifeTimeMs() {
        return this.maxLifeTimeMs;
    }

    public int maxPoolSize() {
        return this.maxPoolSize;
    }

    public int minIdle() {
        return this.minIdle;
    }
}
