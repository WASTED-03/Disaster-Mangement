import React, { createContext, useContext, useEffect, useState, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import toast, { Toaster } from 'react-hot-toast';
import { useAuth } from './AuthContext';

const WebSocketContext = createContext(null);

export const WebSocketProvider = ({ children }) => {
    const { token, user, isAuthenticated } = useAuth();
    const [lastAlert, setLastAlert] = useState(null);
    const [unreadCount, setUnreadCount] = useState(0);
    const clientRef = useRef(null);

    useEffect(() => {
        if (isAuthenticated && token && user?.city) {
            const socket = new SockJS('http://localhost:8080/ws');
            const client = new Client({
                webSocketFactory: () => socket,
                connectHeaders: {
                    Authorization: `Bearer ${token}`
                },
                debug: (str) => {
                    console.log(str);
                },
                onConnect: () => {
                    console.log('Connected to WebSocket');

                    // Subscribe to City Topic
                    client.subscribe(`/topic/admin/alerts/${user.city}`, (message) => {
                        handleAlertMessage(JSON.parse(message.body));
                    });

                    // Subscribe to Global Topic
                    client.subscribe('/topic/admin/alerts/GLOBAL', (message) => {
                        handleAlertMessage(JSON.parse(message.body));
                    });
                },
                onStompError: (frame) => {
                    console.error('Broker reported error: ' + frame.headers['message']);
                    console.error('Additional details: ' + frame.body);
                }
            });

            client.activate();
            clientRef.current = client;

            return () => {
                client.deactivate();
            };
        }
    }, [isAuthenticated, token, user]);

    const handleAlertMessage = (alert) => {
        setLastAlert(alert);
        setUnreadCount(prev => prev + 1);

        // Show Toast
        toast((t) => (
            <div onClick={() => {
                toast.dismiss(t.id);
                window.location.href = '/admin/alerts'; // Simple navigation
            }} className="cursor-pointer">
                <p className="font-bold">{alert.alertType} Alert!</p>
                <p className="text-sm">{alert.location}</p>
            </div>
        ), {
            duration: 5000,
            position: 'top-right',
            style: {
                background: alert.severity === 'CRITICAL' ? '#fee2e2' : '#ffedd5',
                color: alert.severity === 'CRITICAL' ? '#991b1b' : '#9a3412',
                border: '1px solid currentColor'
            },
            icon: alert.severity === 'CRITICAL' ? '🚨' : '⚠️',
        });
    };

    const resetUnreadCount = () => {
        setUnreadCount(0);
    };

    return (
        <WebSocketContext.Provider value={{ lastAlert, unreadCount, resetUnreadCount }}>
            {children}
            <Toaster />
        </WebSocketContext.Provider>
    );
};

export const useWebSocket = () => useContext(WebSocketContext);
