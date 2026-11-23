import React, { useState, useEffect } from 'react'
import ClinicButton from './ClinicButton'
import axios from 'axios'

function Office() {

    const [officeName, setOfficeName] = useState("")
    const [officeNumber, setOfficeNumber] = useState("")

    const [offices, setOffices] = useState([])
    const [searchTerm, setSearchTerm] = useState("")

    async function loadOffices() {
        try {
            const response = await axios.get("http://localhost:8080/api/offices/all")
            setOffices(response.data)
        } catch (error) {
            console.error("Error fetching offices:", error)
        }
    }

    useEffect(() => {
        loadOffices()
    }, [])

    async function saveOffice() {
        try {
            const response = await axios.post("http://localhost:8080/api/offices/save", {
                name: officeName,
                officeNumber: officeNumber
            })
            if (response.status >= 200 && response.status <= 203) {
                alert("Office saved successfully")
                setOfficeName("")
                setOfficeNumber("")
                loadOffices()
            }
        } catch (error) {
            alert("The office is not saved.")
        }
    }

    async function deleteOffice(officeId) {
        if (!window.confirm(`Are you sure you want to delete this office?`)) return;

        try {
            const response = await axios.delete(`http://localhost:8080/api/offices/delete/${officeId}`)
            if (response.status >= 200 && response.status <= 203) {
                alert("Office deleted successfully")
                loadOffices()
            }
        } catch (error) {
            alert("The office is not deleted.")
        }
    }

    const filteredOffices = offices.filter(office => {
        const searchLower = searchTerm.toLowerCase();
        const nameMatch = office.name?.toLowerCase().includes(searchLower);
        const numberMatch = String(office.officeNumber)?.includes(searchLower);

        return nameMatch || numberMatch;
    });

    return (
        <div style={{ padding: "20px", maxWidth: "800px", margin: "0 auto" }}>

            <h1>SAVE OFFICE</h1>
            <div style={{ display: "flex", flexDirection: "column", gap: "10px", marginBottom: "20px" }}>
                <div>
                    <label>Office Name: </label>
                    <input type='text' value={officeName} onChange={e => setOfficeName(e.target.value)} />
                </div>
                <div>
                    <label>Office Number: </label>
                    <input type='text' value={officeNumber} onChange={e => setOfficeNumber(e.target.value)} />
                </div>
                <div>
                    <ClinicButton buttonLabel="SAVE" onClickButton={saveOffice} />
                </div>
            </div>

            <hr style={{ margin: "30px 0" }} />

            <h1>AVAILABLE OFFICES</h1>

            <div style={{ marginBottom: "15px" }}>
                <input
                    type="text"
                    placeholder="Search by office name or number..."
                    value={searchTerm}
                    onChange={e => setSearchTerm(e.target.value)}
                    style={{ width: "100%", padding: "8px" }}
                />
            </div>

            <table style={{ width: "100%", borderCollapse: "collapse", textAlign: "left" }}>
                <thead>
                    <tr style={{ backgroundColor: "#f4f4f4" }}>
                        <th style={{ border: "1px solid #ddd", padding: "8px" }}>Office Name</th>
                        <th style={{ border: "1px solid #ddd", padding: "8px" }}>Office Number</th>
                        <th style={{ border: "1px solid #ddd", padding: "8px" }}>Action</th>
                    </tr>
                </thead>
                <tbody>
                    {filteredOffices.length > 0 ? (
                        filteredOffices.map((office) => (
                            <tr key={office.id}>
                                <td style={{ border: "1px solid #ddd", padding: "8px" }}>{office.name}</td>
                                <td style={{ border: "1px solid #ddd", padding: "8px" }}>{office.officeNumber}</td>
                                <td style={{ border: "1px solid #ddd", padding: "8px" }}>
                                    <ClinicButton
                                        buttonLabel="Delete"
                                        onClickButton={() => deleteOffice(office.id)}
                                    />
                                    <ClinicButton
                                        buttonLabel="Update"
                                        onClickButton={() => {}}
                                    />
                                </td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan="3" style={{ border: "1px solid #ddd", padding: "8px", textAlign: "center" }}>
                                No offices found.
                            </td>
                        </tr>
                    )}
                </tbody>
            </table>
        </div>
    )
}

export default Office
