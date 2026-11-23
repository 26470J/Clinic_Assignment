import React, { useState } from 'react'
import ClinicButton from './ClinicButton'

function ClinicLogin() {

    const [doYouHaveAnAccount, setDoYouHaveAnAccount] = useState(true);

    const [userName, setUserName] = useState("")
    const [password, setPassword] = useState("")

    function navigateBetween() {
        setDoYouHaveAnAccount(!doYouHaveAnAccount)
    }

    function loginAction() {
        alert("This is your username " + userName + " and password " + password)
    }

    return (
        <>
            {doYouHaveAnAccount ? (
                <div style={{ border: "3px solid blue", marginTop: "190px" }}>
                    <label>UserName:</label>
                    <input type="text" onChange={e => setUserName(e.target.value)} placeholder='Enter Your username' /><br />
                    <label>Password:</label>
                    <input type="password" onChange={e => setPassword(e.target.value)} /><br />
                    <ClinicButton buttonLabel="LOGIN" onClickButton={loginAction} /> <br />
                    <label>New here? </label> <ClinicButton buttonLabel="Create Account" onClickButton={navigateBetween} />
                </div>
            ) : (
                <div style={{ border: "3px solid indigo", marginTop: "190px" }}>
                    <label>Student ID:</label>
                    <input type="text" placeholder='Enter Your Student ID' /><br />
                    <label>Email:</label>
                    <input type="email" /><br />
                    <label>Password:</label>
                    <input type="password" /><br />
                    <label>Confirm Password:</label>
                    <input type="password" /><br />
                    <ClinicButton buttonLabel="LOGIN" /> <br />
                    <label>Already have an account?? </label> <ClinicButton buttonLabel="Sign In" onClickButton={navigateBetween} />
                </div>
            )
            }
        </>
    )
}

export default ClinicLogin
