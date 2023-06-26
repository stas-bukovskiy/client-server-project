import React, {useEffect, useState} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import GoodCardComponent from '../Good/GoodCardComponent';
import AddButton from "../Buttons/AddButton.tsx";
import Spinner from "../Spinner/Spinner.tsx";

interface Group {
    id: string;
    name: string;
    description: string;
    createdAt: Date;
    updatedAt: Date;
}

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

const ViewGroup: React.FC = () => {
    const {id} = useParams<{ id: string }>();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);
    const [group, setGroup] = useState<Group | null>(null);
    const [goods, setGoods] = useState<Good[]>([]);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchGroup = async () => {
            setLoading(true);
            setError('');

            try {
                const token = localStorage.getItem('token');
                const response = await fetch(`http://localhost:8000/api/group/${id}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                const data = await response.json();
                if (response.ok) {
                    setGroup(data);
                } else {
                    setError(data.message);
                }
            } catch (error) {
                setError('An error occurred while fetching group.');
            }

            setLoading(false);
        };

        const fetchGoods = async () => {
            setLoading(true);
            setError('');

            try {
                const token = localStorage.getItem('token');
                const response = await fetch(
                    `http://localhost:8000/api/good/by/group/${id}`,
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    }
                );

                const data = await response.json();
                if (response.ok) {
                    setGoods(data);
                } else {
                    setError(data.message);
                }
            } catch (error) {
                setError('An error occurred while fetching goods.');
            }

            setLoading(false);
        };

        fetchGroup();
        fetchGoods();
    }, [id]);

    if (loading) {
        return (
            <Spinner/>
        );
    }

    if (error) {
        return <div>Error: {error}</div>;
    }

    const handleDelete = (id: string) => {
        setGoods(goods.filter((good: Good) => good.id !== id));
    }

    return (
        <div className="container">
            <h2>Group Details</h2>
            {
                error && (
                    <div className="alert alert-danger" role="alert">
                        {error}
                    </div>
                )
            }
            {group && (
                <div className="group-details">
                    <h3>{group.name}</h3>
                    <p className="text-muted">{group.description}</p>
                    <hr/>
                    <div className="d-flex justify-content-evenly">
                        <p className="card-text">
                            Created at: <strong>{group.createdAt.toString()}</strong>
                        </p>
                        <p className="card-text">
                            Updated at: <strong>{group.updatedAt.toString()}</strong>
                        </p>
                    </div>
                </div>
            )}

            <h2>Goods</h2>
            <div className="row">
                {goods.map((good) => (
                    <div className="col-md-6">
                        <GoodCardComponent key={good.id} good={good} handleDelete={() => handleDelete(good.id)}/>
                    </div>
                ))}
            </div>
            <AddButton onClick={() => navigate(`/goods/create/${id}`)}/>
        </div>
    );
};

export default ViewGroup;
