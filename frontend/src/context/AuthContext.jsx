import React, { createContext, useState, useEffect, useContext } from 'react';
import { jwtDecode } from 'jwt-decode';
import api from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(sessionStorage.getItem('token'));
    const [role, setRole] = useState(null);
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (token) {
            try {
                const decoded = jwtDecode(token);
                // Check expiration
                if (decoded.exp * 1000 < Date.now()) {
                    logout();
                } else {
                    setUser({
                        email: decoded.sub,
                        city: decoded.city
                    });
                    // Extract role (Spring Security usually puts it in 'authorities' or 'roles')
                    // Adjust based on your actual JWT structure
                    const userRole = decoded.authorities?.[0]?.authority || decoded.roles?.[0] || null;
                    setRole(userRole);
                    setIsAuthenticated(true);
                    sessionStorage.setItem('token', token);
                }
            } catch (error) {
                console.error("Invalid token", error);
                logout();
            }
        }
        setLoading(false);
    }, [token]);

    const login = async (email) => {
        try {
            await api.post('/auth/request-otp', { email });
            return true;
        } catch (error) {
            console.error("Login failed", error);
            throw error;
        }
    };

    const verifyOtp = async (email, otp) => {
        try {
            const response = await api.post('/auth/verify-otp', { email, otp });
            const { token } = response.data;
            setToken(token);
            return true;
        } catch (error) {
            console.error("OTP verification failed", error);
            throw error;
        }
    };

    const logout = () => {
        setToken(null);
        setUser(null);
        setRole(null);
        setIsAuthenticated(false);
        sessionStorage.removeItem('token');
    };

    return (
        <AuthContext.Provider value={{ user, token, role, isAuthenticated, login, verifyOtp, logout, loading }}>
            {!loading && children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
