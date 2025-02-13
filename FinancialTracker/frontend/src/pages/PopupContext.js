import React, { createContext, useContext, useState } from 'react';

const PopupContext = createContext();

export const PopupProvider = ({ children }) => {
    const [isPopupVisible, setIsPopupVisible] = useState(false);

    const showPopup = () => setIsPopupVisible(true);
    const hidePopup = () => setIsPopupVisible(false);

    return (
        <PopupContext.Provider value={{ isPopupVisible, showPopup, hidePopup }}>
            {children}
        </PopupContext.Provider>
    );
};

export const usePopup = () => useContext(PopupContext);