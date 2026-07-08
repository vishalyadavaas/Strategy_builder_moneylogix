import asyncio
import random
from datetime import date 
import numpy as np
from pydantic import BaseModel
from typing import List


from fastapi import FastAPI, WebSocket, WebSocketDisconnect
from models.recommendation import (
    RecommendationRequest, RecommendationResponse, Strategy, RiskBand
)

app = FastAPI(title="Strategy Builder ML Service")

STRATEGY_MAP = {
    RiskBand.CONSERVATIVE: Strategy(
        name="Covered Call",
        description="Hold underlying + sell OTM call for income, capped upside.",
        maxLoss=None,
        maxProfit=None,
    ),
    RiskBand.MODERATE: Strategy(
        name="Iron Condor",
        description="Sell OTM call+put spreads; profits in a defined range.",
        maxLoss=None,
        maxProfit=None,
    ),
    RiskBand.AGGRESSIVE: Strategy(
        name="Long Straddle",
        description="Buy ATM call+put; profits from large moves either direction.",
        maxLoss=None,
        maxProfit=None,
    ),
}

@app.get("/health")
def health():
    return {"status": "ok"}

@app.post("/api/recommend", response_model=RecommendationResponse)
def recommend(req: RecommendationRequest):
    strategy = STRATEGY_MAP[req.riskBand]
    return RecommendationResponse(
        userId=req.userId,
        symbol=req.symbol,
        riskBand=req.riskBand,
        recommendedStrategy=strategy,
        confidence=0.75,
    )


def generate_option_chain(symbol: str, spot: float):
    strikes = [round(spot + i * 50, 0) for i in range(-5, 6)]
    chain = []
    for strike in strikes:
        distance = abs(strike - spot)
        base_iv = 15 + (distance / spot) * 40

        call_ltp = max(spot - strike, 0) + random.uniform(5, 40)
        put_ltp = max(strike - spot, 0) + random.uniform(5, 40)

        chain.append({
            "strike": strike,
            "call": {
                "ltp": round(call_ltp, 2),
                "iv": round(base_iv + random.uniform(-1, 1), 2),
                "oi": random.randint(1000, 50000),
                "volume": random.randint(100, 5000),
            },
            "put": {
                "ltp": round(put_ltp, 2),
                "iv": round(base_iv + random.uniform(-1, 1), 2),
                "oi": random.randint(1000, 50000),
                "volume": random.randint(100, 5000),
            },
        })
    return chain


@app.websocket("/ws/options-chain/{symbol}")
async def options_chain_ws(websocket: WebSocket, symbol: str):
    await websocket.accept()
    spot = 24800.0 if symbol.upper() == "NIFTY" else 51500.0

    try:
        while True:
            spot += random.uniform(-15, 15)

            payload = {
                "symbol": symbol.upper(),
                "spot": round(spot, 2),
                "timestamp": datetime.utcnow().isoformat() + "Z",
                "chain": generate_option_chain(symbol.upper(), spot),
            }
            await websocket.send_json(payload)
            await asyncio.sleep(1.5)
    except WebSocketDisconnect:
        print(f"Client disconnected from {symbol} feed")


# ---------------- Step 7: Payoff Diagram ----------------

class OptionLeg(BaseModel):
    option_type: str   # "call" or "put"
    position: str       # "buy" or "sell"
    strike: float
    premium: float
    quantity: int = 1

class PayoffRequest(BaseModel):
    legs: List[OptionLeg]
    spot_range_pct: float = 0.1  # +/-10% around current spot
    current_spot: float
    steps: int = 100


def leg_payoff(leg: OptionLeg, spot_prices: np.ndarray) -> np.ndarray:
    if leg.option_type == "call":
        intrinsic = np.maximum(spot_prices - leg.strike, 0)
    else:  # put
        intrinsic = np.maximum(leg.strike - spot_prices, 0)

    if leg.position == "buy":
        payoff = (intrinsic - leg.premium) * leg.quantity
    else:  # sell
        payoff = (leg.premium - intrinsic) * leg.quantity

    return payoff


@app.post("/api/payoff")
def calculate_payoff(req: PayoffRequest):
    low = req.current_spot * (1 - req.spot_range_pct)
    high = req.current_spot * (1 + req.spot_range_pct)
    spot_prices = np.linspace(low, high, req.steps)

    total_payoff = np.zeros(req.steps)
    for leg in req.legs:
        total_payoff += leg_payoff(leg, spot_prices)

    # breakeven points: where payoff crosses zero
    breakevens = []
    for i in range(len(total_payoff) - 1):
        if total_payoff[i] == 0:
            breakevens.append(round(float(spot_prices[i]), 2))
        elif total_payoff[i] * total_payoff[i + 1] < 0:  # sign change
            # linear interpolation for the zero crossing
            x0, x1 = spot_prices[i], spot_prices[i + 1]
            y0, y1 = total_payoff[i], total_payoff[i + 1]
            zero_x = x0 - y0 * (x1 - x0) / (y1 - y0)
            breakevens.append(round(float(zero_x), 2))

    max_profit = float(np.max(total_payoff))
    max_loss = float(np.min(total_payoff))

    return {
        "spot_prices": [round(float(s), 2) for s in spot_prices],
        "payoff": [round(float(p), 2) for p in total_payoff],
        "breakevens": breakevens,
        "max_profit": max_profit if max_profit < 1e6 else "unlimited",
        "max_loss": max_loss if max_loss > -1e6 else "unlimited",
    }        