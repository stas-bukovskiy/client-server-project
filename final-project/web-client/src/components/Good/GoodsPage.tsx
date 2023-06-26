import React, {useEffect, useState} from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import GoodCardComponent from "./GoodCardComponent.tsx";
import Spinner from "../Spinner/Spinner.tsx";

interface Good {
    id: string;
    name: string;
    description: string;
    producer: string;
    quantity: number;
    price: number;
    createdAt: string;
    updatedAt: string;
    group: {
        id: string;
        name: string;
    };
}

const GroupComponent: React.FC = () => {
    const [loading, setLoading] = useState(false);
    const [goods, setGoods] = useState([]);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchGoods = async () => {
            setLoading(true);
            setError('');

            try {
                const token = localStorage.getItem('token');
                const response = await fetch('http://localhost:8000/api/good', {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                if (response.ok) {
                    const data = await response.json();
                    setGoods(data);
                } else {
                    setError('Failed to fetch groups.');
                }
            } catch (error) {
                setError('An error occurred while fetching groups.');
            }

            setLoading(false);
        };

        fetchGoods();
    }, []);

    if (loading) {
        return (
            <Spinner/>
        );
    }

    if (error) {
        return <div>Error: {error}</div>;
    }

    const handleDelete = async (id: string) => {
        try {
            const token = localStorage.getItem('token');
            const response = await fetch(`http://localhost:8000/api/group/${id}`, {
                method: 'DELETE',
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (response.ok) {
                // Remove the deleted group from the list
                setGoods(goods.filter((good: Good) => good.id !== id));
            } else {
                response.json()
                    .then(data => {
                        setError(data.message)
                    });
            }
        } catch (error) {
            setError('An error occurred while deleting the group.');
        }
    };

    return (
        <div className="container">
            <h2>Goods</h2>
            {
                error && (
                    <div className="alert alert-danger" role="alert">
                        {error}
                    </div>
                )
            }
            <hr/>
            <div className="row">
                {goods.map((good: Good) => (
                    <div key={good.id} className="col-md-6">
                        <GoodCardComponent key={good.id} good={good} handleDelete={handleDelete}/>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default GroupComponent;

