import { useEffect, useState } from "react";

function DailyReportsPage() {
    const [reports, setReports] = useState([]);
    const [branches, setBranches] = useState([]);
    const [editingId, setEditingId] = useState(null);

    const [formData, setFormData] = useState({
        branchId: "",
        reportDate: "",
        numberOfTickets: "",
        totalPayments: "",
        totalPayouts: "",
        note: "",
    });

    useEffect(() => {
        loadReports();
        loadBranches();
    }, []);

    async function loadReports() {
        try {
            const response = await fetch("http://localhost:8080/api/reports");
            const data = await response.json();
            setReports(data);
        } catch (error) {
            console.error("Error loading reports:", error);
        }
    }

    async function loadBranches() {
        try {
            const response = await fetch("http://localhost:8080/api/branches");
            const data = await response.json();
            setBranches(data);

            if (data.length > 0 && !formData.branchId) {
                setFormData((prev) => ({
                    ...prev,
                    branchId: data[0].id,
                }));
            }
        } catch (error) {
            console.error("Error loading branches:", error);
        }
    }

    function handleChange(event) {
        const { name, value } = event.target;

        setFormData({
            ...formData,
            [name]: value,
        });
    }

    async function handleSubmit(event) {
        event.preventDefault();

        const payload = {
            branchId: Number(formData.branchId),
            reportDate: formData.reportDate,
            numberOfTickets: Number(formData.numberOfTickets),
            totalPayments: Number(formData.totalPayments),
            totalPayouts: Number(formData.totalPayouts),
            note: formData.note,
        };

        const url = editingId
            ? `http://localhost:8080/api/reports/${editingId}`
            : "http://localhost:8080/api/reports";

        const method = editingId ? "PUT" : "POST";

        try {
            await fetch(url, {
                method: method,
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(payload),
            });

            resetForm();
            loadReports();
        } catch (error) {
            console.error("Error saving report:", error);
        }
    }

    function handleEdit(report) {
        setEditingId(report.id);

        setFormData({
            branchId: report.branch?.id || "",
            reportDate: report.reportDate || "",
            numberOfTickets: report.numberOfTickets || "",
            totalPayments: report.totalPayments || "",
            totalPayouts: report.totalPayouts || "",
            note: report.note || "",
        });
    }

    async function handleDelete(id) {
        const confirmed = window.confirm("Are you sure you want to delete this report?");

        if (!confirmed) {
            return;
        }

        try {
            await fetch(`http://localhost:8080/api/reports/${id}`, {
                method: "DELETE",
            });

            loadReports();
        } catch (error) {
            console.error("Error deleting report:", error);
            alert("This report may already be connected to an anomaly, so it cannot be deleted yet.");
        }
    }

    function resetForm() {
        setEditingId(null);

        setFormData({
            branchId: branches.length > 0 ? branches[0].id : "",
            reportDate: "",
            numberOfTickets: "",
            totalPayments: "",
            totalPayouts: "",
            note: "",
        });
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

    return (
        <div>
            <header className="page-header">
                <div>
                    <h1>Daily Reports</h1>
                    <p>Enter and manage daily business reports for each branch.</p>
                </div>
            </header>

            <section className="form-card">
                <h2>{editingId ? "Edit Daily Report" : "Add Daily Report"}</h2>

                <form onSubmit={handleSubmit} className="report-form">
                    <div className="form-group">
                        <label>Branch</label>
                        <select
                            name="branchId"
                            value={formData.branchId}
                            onChange={handleChange}
                            required
                        >
                            {branches.map((branch) => (
                                <option key={branch.id} value={branch.id}>
                                    {branch.name}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="form-group">
                        <label>Report date</label>
                        <input
                            type="date"
                            name="reportDate"
                            value={formData.reportDate}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Number of tickets</label>
                        <input
                            type="number"
                            name="numberOfTickets"
                            value={formData.numberOfTickets}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Total payments</label>
                        <input
                            type="number"
                            name="totalPayments"
                            value={formData.totalPayments}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Total payouts</label>
                        <input
                            type="number"
                            name="totalPayouts"
                            value={formData.totalPayouts}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Note</label>
                        <input
                            type="text"
                            name="note"
                            value={formData.note}
                            onChange={handleChange}
                            placeholder="Regular business day"
                        />
                    </div>

                    <div className="form-actions">
                        <button type="submit">
                            {editingId ? "Update Report" : "Create Report"}
                        </button>

                        {editingId && (
                            <button type="button" className="secondary-button" onClick={resetForm}>
                                Cancel
                            </button>
                        )}
                    </div>
                </form>
            </section>

            <section className="table-card">
                <div className="table-header">
                    <h2>All Daily Reports</h2>
                    <span>{reports.length} total</span>
                </div>

                <table>
                    <thead>
                    <tr>
                        <th>Date</th>
                        <th>Branch</th>
                        <th>Tickets</th>
                        <th>Payments</th>
                        <th>Payouts</th>
                        <th>Gross Profit</th>
                        <th>Average Ticket</th>
                        <th>Actions</th>
                    </tr>
                    </thead>

                    <tbody>
                    {reports.map((report) => (
                        <tr key={report.id}>
                            <td>{report.reportDate}</td>
                            <td>{report.branch?.name}</td>
                            <td>{report.numberOfTickets}</td>
                            <td>{formatMoney(report.totalPayments)}</td>
                            <td>{formatMoney(report.totalPayouts)}</td>
                            <td>{formatMoney(report.grossProfit)}</td>
                            <td>{formatMoney(report.averageTicketAmount)}</td>
                            <td>
                                <div className="table-actions">
                                    <button onClick={() => handleEdit(report)}>Edit</button>
                                    <button className="delete-button" onClick={() => handleDelete(report.id)}>
                                        Delete
                                    </button>
                                </div>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </section>
        </div>
    );
}

export default DailyReportsPage;