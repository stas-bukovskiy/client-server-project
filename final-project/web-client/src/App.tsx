import './App.css'
import Navbar from "./components/Navbar/Navbar.tsx";
import Login from "./components/Login/Login.tsx";
import {BrowserRouter, Route, Routes} from 'react-router-dom';
import GroupsPage from "./components/Group/GroupsPage.tsx";
import CreateGroup from "./components/Group/CreateGroup.tsx";
import ViewGroup from "./components/Group/ViewGroup.tsx";
import EditGroup from "./components/Group/EditGroup.tsx";
import CreateGood from "./components/Good/CreateGood.tsx";
import EditGood from "./components/Good/EditGood.tsx";
import SearchPage from "./components/Search/SearchPage.tsx";
import GoodsPage from "./components/Good/GoodsPage.tsx";
import UserComponent from "./components/User/UsersPage.tsx";
import CreateUser from "./components/User/CreateUser.tsx";
import EditUser from "./components/User/EditUser.tsx";

function App() {
    return (
        <BrowserRouter>
            <Navbar/>
            <Routes>
                <Route path="/" element={<GroupsPage/>}/>
                <Route path="/login" element={<Login/>}/>
                <Route path="/groups" element={<GroupsPage/>}/>
                <Route path="/groups/create" element={<CreateGroup/>}/>
                <Route path="/groups/edit/:id" element={<EditGroup/>}/>
                <Route path="/goods/edit/:id" element={<EditGood/>}/>
                <Route path="/groups/:id" element={<ViewGroup/>}/>
                <Route path="/goods/create/:groupId" element={<CreateGood/>}/>
                <Route path="/search" element={<SearchPage/>}/>
                <Route path="/goods" element={<GoodsPage/>}/>

                <Route path="/users" element={<UserComponent/>}/>
                <Route path="/users/create" element={<CreateUser/>}/>
                <Route path="/users/edit/:id" element={<EditUser/>}/>
            </Routes>
        </BrowserRouter>
    );

}

export default App
