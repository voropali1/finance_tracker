import axios from 'axios';

export const fetchData = async () => {
    try {
        const response = await axios.get('/api/some-endpoint');
        return response.data;
    } catch (error) {
        console.error(error);
        throw error; // Re-throw the error so that the component can handle it
    }
};