import React from 'react';

const Dashboard = () => {
    // Dummy Data
    const metrics = [
        { title: 'Total Alerts', value: '1,248', color: 'bg-blue-500' },
        { title: 'Active Alerts', value: '12', color: 'bg-red-500' },
        { title: 'Acknowledged', value: '1,236', color: 'bg-green-500' },
        { title: 'Risk Zones', value: '3', color: 'bg-yellow-500' },
    ];

    return (
        <div>
            <h1 className="text-2xl font-bold text-gray-800 mb-6">Admin Control Panel</h1>

            {/* Metric Cards */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                {metrics.map((metric, index) => (
                    <div key={index} className="bg-white rounded-lg shadow p-6 flex items-center">
                        <div className={`w-12 h-12 rounded-full ${metric.color} flex items-center justify-center text-white mr-4`}>
                            {/* Icon Placeholder */}
                            <span className="text-xl font-bold">{metric.title.charAt(0)}</span>
                        </div>
                        <div>
                            <p className="text-gray-500 text-sm">{metric.title}</p>
                            <p className="text-2xl font-bold text-gray-800">{metric.value}</p>
                        </div>
                    </div>
                ))}
            </div>

            {/* Recent Activity Placeholder */}
            <div className="bg-white rounded-lg shadow p-6">
                <h3 className="text-lg font-semibold text-gray-800 mb-4">Recent Activity</h3>
                <p className="text-gray-500">Chart or recent logs will go here.</p>
            </div>
        </div>
    );
};

export default Dashboard;

