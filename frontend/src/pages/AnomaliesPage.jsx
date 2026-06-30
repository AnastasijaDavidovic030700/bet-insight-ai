import { useEffect, useState } from "react";

function AnomaliesPage() {
    const [anomalies, setAnomalies] = useState([]);
    const [selectedAnomaly, setSelectedAnomaly] = useState(null);
    const [reviewData, setReviewData] = useState({
        status: "NEW",
        managerNote: "",
    });

    useEffect(() => {
        loadAnomalies();
    }, []);

    async function loadAnomalies() {
        try {
            const response = await fetch("http://localhost:8080/api/anomalies");
            const data = await response.json();

            setAnomalies(data);

            if (data.length > 0 && !selectedAnomaly) {
                selectAnomaly(data[0]);
            }
        } catch (error) {
            console.error("Error loading anomalies:", error);
        }
    }

    function selectAnomaly(anomaly) {
        setSelectedAnomaly(anomaly);

        setReviewData({
            status: anomaly.status || "NEW",
            managerNote: anomaly.managerNote || "",
        });
    }

    function handleReviewChange(event) {
        const { name, value } = event.target;

        setReviewData({
            ...reviewData,
            [name]: value,
        });
    }

    async function saveReview() {
        if (!selectedAnomaly) {
            return;
        }

        try {
            const response = await fetch(
                `http://localhost:8080/api/anomalies/${selectedAnomaly.id}/review`,
                {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(reviewData),
                }
            );

            const updatedAnomaly = await response.json();

            setSelectedAnomaly(updatedAnomaly);

            setAnomalies((prev) =>
                prev.map((anomaly) =>
                    anomaly.id === updatedAnomaly.id ? updatedAnomaly : anomaly
                )
            );

            alert("Investigation case updated successfully.");
        } catch (error) {
            console.error("Error saving review:", error);
            alert("Error saving investigation review.");
        }
    }

    function formatMoney(value) {
        if (value === null || value === undefined) {
            return "0 RSD";
        }

        return (
            Number(value).toLocaleString("sr-RS", {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2,
            }) + " RSD"
        );
    }

    function getFallbackExplanation(anomaly) {
        if (!anomaly) {
            return "";
        }

        if (anomaly.explanation) {
            return anomaly.explanation;
        }

        const branchName = anomaly.branch?.name || "Unknown branch";
        const reportDate = anomaly.dailyReport?.reportDate || "unknown date";

        return `AI Investigation: The report for ${branchName} on ${reportDate} was flagged because it contains an unusual business pattern. The system recommends reviewing payments, payouts, gross profit and comparing this report with previous days.`;
    }

    function getFallbackActions(anomaly) {
        if (!anomaly) {
            return "";
        }

        if (anomaly.recommendedActions) {
            return anomaly.recommendedActions;
        }

        return "1. Review the daily report manually.\n2. Compare it with previous reports for the same branch.\n3. Check whether the pattern is repeated.\n4. Add a manager note and update the case status.";
    }

    return (
        <div>
            <header className="page-header">
                <div>
                    <h1>AI Investigation Center</h1>
                    <p>
                        Review detected anomalies, understand risk explanations and document
                        management decisions.
                    </p>
                </div>

                <button onClick={loadAnomalies}>Refresh</button>
            </header>

            <section className="investigation-layout">
                <div className="investigation-list table-card">
                    <div className="table-header">
                        <h2>Anomaly Cases</h2>
                        <span>{anomalies.length} total</span>
                    </div>

                    <table>
                        <thead>
                        <tr>
                            <th>Type</th>
                            <th>Branch</th>
                            <th>Date</th>
                            <th>Risk</th>
                            <th>Status</th>
                        </tr>
                        </thead>

                        <tbody>
                        {anomalies.map((anomaly) => (
                            <tr
                                key={anomaly.id}
                                className={
                                    selectedAnomaly?.id === anomaly.id ? "selected-row" : ""
                                }
                                onClick={() => selectAnomaly(anomaly)}
                            >
                                <td>{anomaly.type}</td>
                                <td>{anomaly.branch?.name}</td>
                                <td>{anomaly.dailyReport?.reportDate}</td>
                                <td>
                    <span className="risk-pill">
                      {anomaly.riskScore || "N/A"}
                    </span>
                                </td>
                                <td>
                    <span
                        className={`status ${(
                            anomaly.status || "NEW"
                        ).toLowerCase()}-status`}
                    >
                      {anomaly.status || "NEW"}
                    </span>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>

                {selectedAnomaly && (
                    <div className="investigation-details">
                        <section className="case-card">
                            <div className="case-header">
                                <div>
                                    <h2>{selectedAnomaly.type}</h2>
                                    <p>{selectedAnomaly.description}</p>
                                </div>

                                <div className="risk-score-box">
                                    <span>Risk Score</span>
                                    <strong>{selectedAnomaly.riskScore || "N/A"}</strong>
                                </div>
                            </div>

                            <div className="case-grid">
                                <div>
                                    <span>Branch</span>
                                    <strong>{selectedAnomaly.branch?.name}</strong>
                                </div>

                                <div>
                                    <span>Report date</span>
                                    <strong>{selectedAnomaly.dailyReport?.reportDate}</strong>
                                </div>

                                <div>
                                    <span>Severity</span>
                                    <strong>{selectedAnomaly.severity}</strong>
                                </div>

                                <div>
                                    <span>Status</span>
                                    <strong>{selectedAnomaly.status || "NEW"}</strong>
                                </div>
                            </div>
                        </section>

                        <section className="case-card">
                            <h3>Business Evidence</h3>

                            <div className="evidence-grid">
                                <div>
                                    <span>Total Payments</span>
                                    <strong>
                                        {formatMoney(selectedAnomaly.dailyReport?.totalPayments)}
                                    </strong>
                                </div>

                                <div>
                                    <span>Total Payouts</span>
                                    <strong>
                                        {formatMoney(selectedAnomaly.dailyReport?.totalPayouts)}
                                    </strong>
                                </div>

                                <div>
                                    <span>Gross Profit</span>
                                    <strong>
                                        {formatMoney(selectedAnomaly.dailyReport?.grossProfit)}
                                    </strong>
                                </div>

                                <div>
                                    <span>Average Ticket</span>
                                    <strong>
                                        {formatMoney(
                                            selectedAnomaly.dailyReport?.averageTicketAmount
                                        )}
                                    </strong>
                                </div>
                            </div>
                        </section>

                        <section className="case-card">
                            <h3>AI Explanation</h3>
                            <p className="case-text">
                                {getFallbackExplanation(selectedAnomaly)}
                            </p>
                        </section>

                        <section className="case-card">
                            <h3>Recommended Actions</h3>
                            <pre className="actions-text">
                {getFallbackActions(selectedAnomaly)}
              </pre>
                        </section>

                        <section className="case-card">
                            <h3>Manager Review</h3>

                            <div className="review-form">
                                <div className="form-group">
                                    <label>Status</label>
                                    <select
                                        name="status"
                                        value={reviewData.status}
                                        onChange={handleReviewChange}
                                    >
                                        <option value="NEW">NEW</option>
                                        <option value="REVIEWED">REVIEWED</option>
                                        <option value="RESOLVED">RESOLVED</option>
                                        <option value="ESCALATED">ESCALATED</option>
                                    </select>
                                </div>

                                <div className="form-group">
                                    <label>Manager note</label>
                                    <textarea
                                        name="managerNote"
                                        value={reviewData.managerNote}
                                        onChange={handleReviewChange}
                                        placeholder="Add explanation, decision, or next step..."
                                    />
                                </div>

                                <button onClick={saveReview}>Save Review</button>
                            </div>
                        </section>
                    </div>
                )}
            </section>
        </div>
    );
}

export default AnomaliesPage;