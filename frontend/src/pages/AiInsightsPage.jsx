import { useEffect, useState } from "react";

function AiInsightsPage() {
    const [insights, setInsights] = useState([]);
    const [selectedInsight, setSelectedInsight] = useState(null);
    const [formData, setFormData] = useState({
        periodFrom: "2026-06-24",
        periodTo: "2026-06-30",
    });

    useEffect(() => {
        loadInsights();
    }, []);

    async function loadInsights() {
        try {
            const response = await fetch("http://localhost:8080/api/ai-insights");
            const data = await response.json();

            setInsights(data);

            if (data.length > 0 && !selectedInsight) {
                setSelectedInsight(data[0]);
            }
        } catch (error) {
            console.error("Error loading AI insights:", error);
        }
    }

    function handleChange(event) {
        const { name, value } = event.target;

        setFormData({
            ...formData,
            [name]: value,
        });
    }

    async function generateInsight(event) {
        event.preventDefault();

        try {
            const response = await fetch("http://localhost:8080/api/ai-insights/generate", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(formData),
            });

            const newInsight = await response.json();

            setSelectedInsight(newInsight);
            loadInsights();
        } catch (error) {
            console.error("Error generating AI insight:", error);
            alert("Error generating AI insight.");
        }
    }

    return (
        <div>
            <header className="page-header">
                <div>
                    <h1>AI Insights</h1>
                    <p>
                        Generate executive business summaries based on reports, payout behavior
                        and detected anomaly cases.
                    </p>
                </div>
            </header>

            <section className="form-card">
                <h2>Generate AI Business Insight</h2>

                <form onSubmit={generateInsight} className="insight-form">
                    <div className="form-group">
                        <label>Period from</label>
                        <input
                            type="date"
                            name="periodFrom"
                            value={formData.periodFrom}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Period to</label>
                        <input
                            type="date"
                            name="periodTo"
                            value={formData.periodTo}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-actions">
                        <button type="submit">Generate AI Insight</button>
                    </div>
                </form>
            </section>

            <section className="ai-layout">
                <div className="table-card">
                    <div className="table-header">
                        <h2>Generated Insights</h2>
                        <span>{insights.length} total</span>
                    </div>

                    <table>
                        <thead>
                        <tr>
                            <th>Period</th>
                            <th>Risk</th>
                            <th>Created</th>
                        </tr>
                        </thead>

                        <tbody>
                        {insights.map((insight) => (
                            <tr
                                key={insight.id}
                                className={selectedInsight?.id === insight.id ? "selected-row" : ""}
                                onClick={() => setSelectedInsight(insight)}
                            >
                                <td>
                                    {insight.periodFrom} → {insight.periodTo}
                                </td>
                                <td>
                    <span className={`status ${insight.riskLevel?.toLowerCase()}-risk`}>
                      {insight.riskLevel}
                    </span>
                                </td>
                                <td>{insight.createdAt?.replace("T", " ").slice(0, 16)}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>

                {selectedInsight && (
                    <div className="ai-report">
                        <section className="case-card">
                            <div className="case-header">
                                <div>
                                    <h2>Executive AI Report</h2>
                                    <p>
                                        Period: {selectedInsight.periodFrom} → {selectedInsight.periodTo}
                                    </p>
                                </div>

                                <div className="risk-score-box">
                                    <span>Risk Level</span>
                                    <strong>{selectedInsight.riskLevel}</strong>
                                </div>
                            </div>
                        </section>

                        <section className="case-card">
                            <h3>Executive Summary</h3>
                            <p className="case-text">{selectedInsight.executiveSummary}</p>
                        </section>

                        <section className="case-card">
                            <h3>Key Findings</h3>
                            <pre className="actions-text">{selectedInsight.keyFindings}</pre>
                        </section>

                        <section className="case-card">
                            <h3>Recommended Actions</h3>
                            <pre className="actions-text">{selectedInsight.recommendedActions}</pre>
                        </section>
                    </div>
                )}
            </section>
        </div>
    );
}

export default AiInsightsPage;