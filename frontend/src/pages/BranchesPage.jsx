import { useEffect, useState } from "react";

function BranchesPage() {
    const [branches, setBranches] = useState([]);
    const [formData, setFormData] = useState({
        name: "",
        city: "",
        address: "",
        active: true,
    });
    const [editingId, setEditingId] = useState(null);

    useEffect(() => {
        loadBranches();
    }, []);

    async function loadBranches() {
        try {
            const response = await fetch("http://localhost:8080/api/branches");
            const data = await response.json();
            setBranches(data);
        } catch (error) {
            console.error("Error loading branches:", error);
        }
    }

    function handleChange(event) {
        const { name, value, type, checked } = event.target;

        setFormData({
            ...formData,
            [name]: type === "checkbox" ? checked : value,
        });
    }

    async function handleSubmit(event) {
        event.preventDefault();

        const url = editingId
            ? `http://localhost:8080/api/branches/${editingId}`
            : "http://localhost:8080/api/branches";

        const method = editingId ? "PUT" : "POST";

        try {
            await fetch(url, {
                method: method,
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(formData),
            });

            resetForm();
            loadBranches();
        } catch (error) {
            console.error("Error saving branch:", error);
        }
    }

    function handleEdit(branch) {
        setEditingId(branch.id);
        setFormData({
            name: branch.name,
            city: branch.city,
            address: branch.address,
            active: branch.active,
        });
    }

    async function handleDelete(id) {
        const confirmed = window.confirm("Are you sure you want to delete this branch?");

        if (!confirmed) {
            return;
        }

        try {
            await fetch(`http://localhost:8080/api/branches/${id}`, {
                method: "DELETE",
            });

            loadBranches();
        } catch (error) {
            console.error("Error deleting branch:", error);
        }
    }

    function resetForm() {
        setEditingId(null);
        setFormData({
            name: "",
            city: "",
            address: "",
            active: true,
        });
    }

    return (
        <div>
            <header className="page-header">
                <div>
                    <h1>Branches</h1>
                    <p>Manage betting shop branches and locations.</p>
                </div>
            </header>

            <section className="form-card">
                <h2>{editingId ? "Edit Branch" : "Add New Branch"}</h2>

                <form onSubmit={handleSubmit} className="branch-form">
                    <div className="form-group">
                        <label>Branch name</label>
                        <input
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            placeholder="BetInsight Smederevo"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>City</label>
                        <input
                            type="text"
                            name="city"
                            value={formData.city}
                            onChange={handleChange}
                            placeholder="Smederevo"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Address</label>
                        <input
                            type="text"
                            name="address"
                            value={formData.address}
                            onChange={handleChange}
                            placeholder="Karađorđeva 20"
                            required
                        />
                    </div>

                    <label className="checkbox-row">
                        <input
                            type="checkbox"
                            name="active"
                            checked={formData.active}
                            onChange={handleChange}
                        />
                        Active branch
                    </label>

                    <div className="form-actions">
                        <button type="submit">
                            {editingId ? "Update Branch" : "Create Branch"}
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
                    <h2>All Branches</h2>
                    <span>{branches.length} total</span>
                </div>

                <table>
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>City</th>
                        <th>Address</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                    </thead>

                    <tbody>
                    {branches.map((branch) => (
                        <tr key={branch.id}>
                            <td>{branch.name}</td>
                            <td>{branch.city}</td>
                            <td>{branch.address}</td>
                            <td>
                  <span className={branch.active ? "status active-status" : "status inactive-status"}>
                    {branch.active ? "Active" : "Inactive"}
                  </span>
                            </td>
                            <td>
                                <div className="table-actions">
                                    <button onClick={() => handleEdit(branch)}>Edit</button>
                                    <button className="delete-button" onClick={() => handleDelete(branch.id)}>
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

export default BranchesPage;