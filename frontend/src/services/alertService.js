import api from './api';

const alertService = {
    getAlerts: async (params) => {
        try {
            const response = await api.get('/alerts', { params });
            return response.data;
        } catch (error) {
            console.error("Error fetching alerts:", error);
            throw error;
        }
    },

    acknowledgeAlert: async (id) => {
        try {
            const response = await api.put(`/alerts/${id}/acknowledge`);
            return response.data;
        } catch (error) {
            console.error("Error acknowledging alert:", error);
            throw error;
        }
    }
};

export default alertService;
