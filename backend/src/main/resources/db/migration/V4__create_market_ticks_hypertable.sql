CREATE EXTENSION IF NOT EXISTS timescaledb;

CREATE TABLE market_tick (
    time TIMESTAMPTZ NOT NULL,
    symbol VARCHAR(50) NOT NULL,
    strike_price NUMERIC(12,2),
    option_type VARCHAR(4),
    ltp NUMERIC(12,2) NOT NULL,
    open_interest BIGINT,
    implied_volatility NUMERIC(6,2),
    volume BIGINT
);

SELECT create_hypertable('market_tick', 'time');

CREATE INDEX idx_market_tick_symbol_time ON market_tick(symbol, time DESC);