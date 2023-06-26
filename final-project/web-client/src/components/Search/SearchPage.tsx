import React, {useState} from 'react';
import GroupCardComponent from "../Group/GroupCardComponent.tsx";
import GoodCardComponent from "../Good/GoodCardComponent.tsx";
import "./SearchPage.css";

type Group = {
    id: string;
    name: string;
    description: string;
    createdAt: Date;
    updatedAt: Date;
};
type Good = {
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

const SearchPage: React.FC = () => {
    const [searchExpression, setSearchExpression] = useState('');
    const [groups, setGroups] = useState([]);
    const [goods, setGoods] = useState([]);

    const handleSearch = (e: React.FormEvent) => {
        e.preventDefault();
        if (!searchExpression) {
            return;
        }

        // Send the GET request to search for groups
        fetch(`http://localhost:8000/api/group/search/${searchExpression}`, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`,
            }
        })
            .then((response) => response.json())
            .then((data) => {
                setGroups(data);
                console.log(data);
            })
            .catch((error) => {
                console.log('Error:', error);
            });

        // Send the GET request to search for goods
        fetch(`http://localhost:8000/api/good/search/${searchExpression}`, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`,
            }
        })
            .then((response) => response.json())
            .then((data) => {
                setGoods(data);
                console.log(data);
            })
            .catch((error) => {
                console.log('Error:', error);
            });
    };

    const handleGroupDelete = (id: string) => {
        setGroups(groups.filter((group: Group) => group.id !== id));
    }

    const handleGoodDelete = (id: string) => {
        setGoods(goods.filter((good: Good) => good.id !== id));
    }

    return (
        <div className="container">
            <div className="input-group mb-2">
                <input
                    type="text"
                    value={searchExpression}
                    className="form-control label"
                    onChange={(e) => setSearchExpression(e.target.value)}
                    placeholder="Enter search expression"
                />
                <button onClick={handleSearch} className="btn btn-primary">Search</button>
            </div>
            {groups.length !== 0 && <>
                <h2>Groups</h2>
                <div className="row">
                    {groups.map((group: Group) => (
                        <div className="col-md-6">
                            <GroupCardComponent key={group.id} group={group} handleDelete={handleGroupDelete}/>
                        </div>
                    ))}
                </div>
            </>
            }
            {goods.length !== 0 && <>
                <hr/>
                <h2>Goods</h2>
                <div className="row">
                    {goods.map((good: Good) => (
                        <div className="col-md-6">
                            <GoodCardComponent key={good.id} good={good} handleDelete={handleGoodDelete}/>
                        </div>
                    ))}
                </div>
            </>
            }
        </div>
    );
};

export default SearchPage;
