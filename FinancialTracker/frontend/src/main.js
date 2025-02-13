import React from 'react';
import Header from './components/Header';
import BookContainer from './components/BookContainer';
import Popup from './components/Popup';
import './App.css';

function App() {
    return (
        <div className="App">
            <Header />
            <BookContainer />
            <Popup />
        </div>
    );
}

export default App;