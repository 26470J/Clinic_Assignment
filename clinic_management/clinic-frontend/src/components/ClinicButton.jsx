import React from 'react'

function ClinicButton({ buttonLabel, onClickButton }) {
    const [count, setCount] = React.useState(0);

    function incrementing() {
        setCount(count + 1)
    }
    return (
        < div >
            <button onClick={onClickButton} >{buttonLabel}</button>
        </div >
    )
}

export default ClinicButton
