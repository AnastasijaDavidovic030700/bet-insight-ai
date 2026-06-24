import { useEffect, useState } from "react";
import {
  Bar,
  BarChart,
  CartesianGrid,
  Legend,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import "./App.css";

function App() {
  const [stats, setStats] = useState(null);
  const [anomalies, setAnomalies] = useState([]);
  const [reports, setReports] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  }, []);

  async function loadDashboardData() {
    try {
      const statsResponse = await fetch("http://localhost:8080/api/analytics/dashboard");
      const statsData = await statsResponse.json();

      const anomaliesResponse = await fetch("http://localhost:8080/api/anomalies");
      const anomaliesData = await anomaliesResponse.json();

      const reportsResponse = await fetch("http://localhost:8080/api/reports");
      const reportsData = await reportsResponse.json();

      setStats(statsData);
      setAnomalies(anomaliesData);
      setReports(reportsData);
    } catch (error) {
      console.error("Error loading dashboard data:", error);
    } finally {
      setLoading(false);
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

  function prepareDailyChartData() {
    return reports
        .map((report) => ({
          date: report.reportDate,
          payments: Number(report.totalPayments),
          payouts: Number(report.totalPayouts),
          grossProfit: Number(report.grossProfit),
        }))
        .sort((a, b) => new Date(a.date) - new Date(b.date));
  }

  function prepareBranchChartData() {
    const branchMap = {};

    reports.forEach((report) => {
      const branchName = report.branch?.name || "Unknown branch";

      if (!branchMap[branchName]) {
        branchMap[branchName] = {
          branch: branchName,
          payments: 0,
          payouts: 0,
          grossProfit: 0,
        };
      }

      branchMap[branchName].payments += Number(report.totalPayments);
      branchMap[branchName].payouts += Number(report.totalPayouts);
      branchMap[branchName].grossProfit += Number(report.grossProfit);
    });

    return Object.values(branchMap);
  }

  if (loading) {
    return <div className="loading">Loading dashboard...</div>;
  }

  const dailyChartData = prepareDailyChartData();
  const branchChartData = prepareBranchChartData();

  return (
      <div className="app">
        <aside className="sidebar">
          <h2>BetInsight AI</h2>

          <nav>
            <a className="active">Dashboard</a>
            <a>Branches</a>
            <a>Daily Reports</a>
            <a>Anomalies</a>
            <a>AI Insights</a>
          </nav>
        </aside>

        <main className="main-content">
          <header className="page-header">
            <div>
              <h1>Business Dashboard</h1>
              <p>Overview of betting shop performance and detected business risks.</p>
            </div>

            <button onClick={loadDashboardData}>Refresh</button>
          </header>

          <section className="stats-grid">
            <div className="stat-card">
              <span>Total Payments</span>
              <strong>{formatMoney(stats?.totalPayments)}</strong>
            </div>

            <div className="stat-card">
              <span>Total Payouts</span>
              <strong>{formatMoney(stats?.totalPayouts)}</strong>
            </div>

            <div className="stat-card">
              <span>Gross Profit</span>
              <strong>{formatMoney(stats?.grossProfit)}</strong>
            </div>

            <div className="stat-card">
              <span>Average Ticket</span>
              <strong>{formatMoney(stats?.averageTicketAmount)}</strong>
            </div>

            <div className="stat-card">
              <span>Total Tickets</span>
              <strong>{stats?.totalTickets}</strong>
            </div>

            <div className="stat-card">
              <span>Number of Reports</span>
              <strong>{stats?.numberOfReports}</strong>
            </div>
          </section>

          <section className="insight-grid">
            <div className="info-card">
              <h3>Best Branch</h3>
              <p>{stats?.bestBranchName}</p>
            </div>

            <div className="info-card danger">
              <h3>Highest Risk Branch</h3>
              <p>{stats?.highestRiskBranchName}</p>
            </div>
          </section>

          <section className="charts-grid">
            <div className="chart-card">
              <h2>Payments vs Payouts</h2>
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={dailyChartData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="date" />
                  <YAxis />
                  <Tooltip formatter={(value) => formatMoney(value)} />
                  <Legend />
                  <Line type="monotone" dataKey="payments" name="Payments" />
                  <Line type="monotone" dataKey="payouts" name="Payouts" />
                </LineChart>
              </ResponsiveContainer>
            </div>

            <div className="chart-card">
              <h2>Gross Profit Trend</h2>
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={dailyChartData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="date" />
                  <YAxis />
                  <Tooltip formatter={(value) => formatMoney(value)} />
                  <Legend />
                  <Line type="monotone" dataKey="grossProfit" name="Gross Profit" />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </section>

          <section className="chart-card full-width">
            <h2>Branch Performance</h2>
            <ResponsiveContainer width="100%" height={320}>
              <BarChart data={branchChartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="branch" />
                <YAxis />
                <Tooltip formatter={(value) => formatMoney(value)} />
                <Legend />
                <Bar dataKey="payments" name="Payments" />
                <Bar dataKey="payouts" name="Payouts" />
                <Bar dataKey="grossProfit" name="Gross Profit" />
              </BarChart>
            </ResponsiveContainer>
          </section>

          <section className="table-card">
            <div className="table-header">
              <h2>Detected Anomalies</h2>
              <span>{anomalies.length} total</span>
            </div>

            <table>
              <thead>
              <tr>
                <th>Type</th>
                <th>Severity</th>
                <th>Branch</th>
                <th>Report Date</th>
                <th>Description</th>
              </tr>
              </thead>

              <tbody>
              {anomalies.map((anomaly) => (
                  <tr key={anomaly.id}>
                    <td>{anomaly.type}</td>
                    <td>
                    <span className={`badge ${anomaly.severity?.toLowerCase()}`}>
                      {anomaly.severity}
                    </span>
                    </td>
                    <td>{anomaly.branch?.name}</td>
                    <td>{anomaly.dailyReport?.reportDate}</td>
                    <td>{anomaly.description}</td>
                  </tr>
              ))}
              </tbody>
            </table>
          </section>
        </main>
      </div>
  );
}

export default App;