import { useEffect, useState } from "react";

const Home = () => {

    const [state, setState] = useState();

    useEffect(() => {
        setState("Alphabets");
    }, [])

    const changeState = () => {
        state === 'Alphabets' ? setState('Numbers') : setState('Alphabets');
    }

    return (
        <div className="Home" style={{padding : 50}}>

            <h2>Home</h2>
            <br />
            <button onClick={changeState}>Change State</button>
            {state === 'Alphabets' &&
            <AlphabetComponent state={state} /> }
            {state === 'Numbers' &&
            <NumbersComponent state={state} /> }
        </div>
    );
}

export default Home;

const AlphabetComponent = (props) => {

    const [alphabets, setAlphabets] = useState(null);
    const [show, setShow] = useState(false);


    const print1 = () => {
        console.log("On click called")
        setAlphabets(['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h']);
        setShow(!show);
    }

    return (
        <div className="AlphabetComponent">
            <h4>{props.state}</h4>
            <button onClick={print1}>{show ? 'Hide': 'Show' }</button>
            {alphabets && show && alphabets.map(alphabet => (
                <h4>{alphabet}</h4>
            ))}

        </div>
    );
}

const NumbersComponent = (props) => {

    const [numbers, setNumbers] = useState(null);
    const [show, setShow] = useState(false);


    const print1 = () => {
        console.log("On click called")
        setNumbers(['0', '1', '2', '3', '4', '5', '6', '7', '8', '9']);
        setShow(!show);
    }

    return (
        <div className="AlphabetComponent">
            <h4>{props.state}</h4>
            <button onClick={print1}>{show ? 'Hide': 'Show' }</button>
            {numbers && show && numbers.map(number => (
                <h4>{number}</h4>
            ))}

        </div>
    );
}
