import styles from './Popup.module.css';
import React from "react";
function Popup({ show, onClose, children }) {
    const overlayClass = show ? `${styles['popup-overlay']} ${styles.active}` : styles['popup-overlay'];
    const windowClass = show ? `${styles['popup-window']} ${styles.active}` : styles['popup-window'];
    return (
        <div className={overlayClass} onClick={onClose}>
            <div className={windowClass} onClick={(e) => e.stopPropagation()}>
                <button className={styles['close-popup']} onClick={onClose}>
                    &times;
                </button>
                {children}
            </div>
        </div>
    );
}

export default Popup;