import React from 'react';
import './App.css';

function App() {
    return (
        <div className="min-h-screen bg-gray-100 flex items-center justify-center">
            <div className="bg-white shadow-lg rounded-lg p-8 max-w-md w-full">
                <div className="text-center">
                    <h1 className="text-3xl font-bold text-gray-900 mb-4">
                        Employee Review System
                    </h1>
                    <p className="text-gray-600 mb-6">
                        Ready to build with Spring Boot + React + TypeScript
                    </p>
                    <div className="space-y-4">
                        <button className="w-full bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded-md transition duration-200">
                            Login
                        </button>
                        <button className="w-full border border-gray-300 hover:bg-gray-50 text-gray-700 font-semibold py-2 px-4 rounded-md transition duration-200">
                            Register
                        </button>
                    </div>
                    <div className="mt-6 p-4 bg-green-50 border border-green-200 rounded-md">
                        <p className="text-green-800 text-sm">
                            ✅ Spring Boot Backend: Running on localhost:8080<br/>
                            ✅ React Frontend: Running on localhost:3000<br/>
                            ✅ Tailwind CSS: Configured and working!
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default App;
