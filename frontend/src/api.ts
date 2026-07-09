const API_BASE = "http://localhost:8080/api";

export interface RiskAnswers {
  loss_tolerance: number;
  drawdown_reaction: number;
  investment_horizon: number;
  income_stability: number;
  prior_experience: number;
  goal: number;
}

export interface RiskProfileResponse {
  id: string;
  userId: string;
  riskBand: "CONSERVATIVE" | "MODERATE" | "AGGRESSIVE";
  score: number;
  createdAt: string;
}

export interface Strategy {
  name: string;
  description: string;
  maxLoss: number | null;
  maxProfit: number | null;
}

export interface RecommendationResponse {
  userId: string;
  symbol: string;
  riskBand: string;
  recommendedStrategy: Strategy;
  confidence: number;
}

export async function saveRiskProfile(answers: RiskAnswers): Promise<RiskProfileResponse> {
  const res = await fetch(`${API_BASE}/risk-profile`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ answers }),
  });
  if (!res.ok) throw new Error(`Failed to save risk profile: ${res.status}`);
  return res.json();
}

export async function getRecommendation(symbol: string = "NIFTY"): Promise<RecommendationResponse> {
  const res = await fetch(`${API_BASE}/strategy/recommend?symbol=${symbol}`);
  if (!res.ok) throw new Error(`Failed to get recommendation: ${res.status}`);
  return res.json();
}