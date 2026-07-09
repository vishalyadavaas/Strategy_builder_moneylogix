import { useState } from "react";
import { saveRiskProfile, getRecommendation, type RiskAnswers, type RecommendationResponse } from "./api";

const QUESTIONS: { key: keyof RiskAnswers; label: string }[] = [
  { key: "loss_tolerance", label: "Loss tolerance before panicking" },
  { key: "drawdown_reaction", label: "Reaction to 20% drawdown" },
  { key: "investment_horizon", label: "Investment time horizon" },
  { key: "income_stability", label: "Income stability" },
  { key: "prior_experience", label: "Prior trading experience" },
  { key: "goal", label: "Primary investment goal" },
];

const LABELS = ["", "Low", "Moderate", "High", "Very High"];

const BAND_COLOR: Record<string, string> = {
  CONSERVATIVE: "text-blue-400 bg-blue-950 border-blue-800",
  MODERATE: "text-amber-400 bg-amber-950 border-amber-800",
  AGGRESSIVE: "text-rose-400 bg-rose-950 border-rose-800",
};

function App() {
  const [answers, setAnswers] = useState<RiskAnswers>({
    loss_tolerance: 2,
    drawdown_reaction: 2,
    investment_horizon: 2,
    income_stability: 2,
    prior_experience: 2,
    goal: 2,
  });
  const [recommendation, setRecommendation] = useState<RecommendationResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleChange = (key: keyof RiskAnswers, value: number) => {
    setAnswers((prev) => ({ ...prev, [key]: value }));
  };

  const handleSubmit = async () => {
    setLoading(true);
    setError(null);
    setRecommendation(null);
    try {
      await saveRiskProfile(answers);
      const rec = await getRecommendation("NIFTY");
      setRecommendation(rec);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Something went wrong");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#0e1116] text-gray-200 flex font-sans">
      {/* Sidebar */}
      <aside className="w-56 bg-[#131722] border-r border-gray-800 flex-shrink-0 hidden md:flex flex-col">
        <div className="px-5 py-5 border-b border-gray-800">
          <div className="text-emerald-400 font-bold text-lg tracking-tight">MoneyLogix</div>
          <div className="text-gray-500 text-xs mt-0.5">Strategy Builder</div>
        </div>
        <nav className="flex-1 px-3 py-4 space-y-1">
          <div className="px-3 py-2 rounded-md bg-emerald-500/10 text-emerald-400 text-sm font-medium">
            Risk Profile
          </div>
          <div className="px-3 py-2 rounded-md text-gray-500 text-sm">Positions</div>
          <div className="px-3 py-2 rounded-md text-gray-500 text-sm">Watchlist</div>
          <div className="px-3 py-2 rounded-md text-gray-500 text-sm">Orders</div>
        </nav>
        <div className="px-5 py-4 border-t border-gray-800 text-xs text-gray-600">
          NIFTY · Live ticks every 3s
        </div>
      </aside>

      {/* Main content */}
      <main className="flex-1 px-6 py-8 md:px-10 max-w-3xl">
        <h1 className="text-2xl font-semibold text-gray-100">Risk Assessment</h1>
        <p className="text-gray-500 text-sm mt-1 mb-8">
          Answer honestly — this drives your options strategy recommendation for NIFTY.
        </p>

        <div className="bg-[#151a24] border border-gray-800 rounded-lg divide-y divide-gray-800">
          {QUESTIONS.map((q) => (
            <div key={q.key} className="px-5 py-4">
              <div className="flex items-center justify-between mb-3">
                <label className="text-sm text-gray-300">{q.label}</label>
                <span className="text-xs font-mono px-2 py-0.5 rounded bg-gray-800 text-gray-400">
                  {LABELS[answers[q.key]]}
                </span>
              </div>
              <input
                type="range"
                min={1}
                max={4}
                value={answers[q.key]}
                onChange={(e) => handleChange(q.key, Number(e.target.value))}
                className="w-full h-1.5 rounded-full appearance-none bg-gray-700 accent-emerald-500 cursor-pointer"
              />
            </div>
          ))}
        </div>

        <button
          onClick={handleSubmit}
          disabled={loading}
          className="mt-6 w-full md:w-auto px-8 bg-emerald-600 hover:bg-emerald-500 disabled:opacity-50 disabled:cursor-not-allowed text-white rounded-md py-2.5 text-sm font-semibold transition-colors"
        >
          {loading ? "Analyzing…" : "Get My Strategy"}
        </button>

        {error && (
          <div className="mt-6 bg-rose-950 border border-rose-800 rounded-lg px-4 py-3 text-rose-300 text-sm">
            {error}
          </div>
        )}

        {recommendation && (
          <div className="mt-6 bg-[#151a24] border border-gray-800 rounded-lg p-6">
            <div className="flex items-center justify-between mb-4">
              <span
                className={`text-xs font-mono px-2.5 py-1 rounded border ${
                  BAND_COLOR[recommendation.riskBand] ?? "text-gray-400 bg-gray-800 border-gray-700"
                }`}
              >
                {recommendation.riskBand}
              </span>
              <span className="text-xs text-gray-500 font-mono">{recommendation.symbol}</span>
            </div>

            <h2 className="text-xl font-semibold text-gray-100 mb-2">
              {recommendation.recommendedStrategy.name}
            </h2>
            <p className="text-gray-400 text-sm leading-relaxed mb-5">
              {recommendation.recommendedStrategy.description}
            </p>

            <div className="grid grid-cols-3 gap-4 pt-4 border-t border-gray-800">
              <div>
                <div className="text-xs text-gray-500 mb-1">Confidence</div>
                <div className="text-emerald-400 font-mono text-sm font-semibold">
                  {(recommendation.confidence * 100).toFixed(0)}%
                </div>
              </div>
              <div>
                <div className="text-xs text-gray-500 mb-1">Max Profit</div>
                <div className="font-mono text-sm text-gray-300">
                  {recommendation.recommendedStrategy.maxProfit ?? "—"}
                </div>
              </div>
              <div>
                <div className="text-xs text-gray-500 mb-1">Max Loss</div>
                <div className="font-mono text-sm text-gray-300">
                  {recommendation.recommendedStrategy.maxLoss ?? "—"}
                </div>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

export default App;